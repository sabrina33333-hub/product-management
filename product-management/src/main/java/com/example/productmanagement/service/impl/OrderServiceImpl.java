package com.example.productmanagement.service.impl;

import com.example.productmanagement.dto.request.CreateOrderRequest;
import com.example.productmanagement.dto.request.OrderItemRequest;

import com.example.productmanagement.entity.*;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.OrderStatus;
import com.example.productmanagement.repository.CustomerRepository;
import com.example.productmanagement.repository.OrderItemRepository;
import com.example.productmanagement.repository.OrderRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.OrderItemService;
import com.example.productmanagement.service.OrderService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
// 【修改】移除 @RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CustomerRepository customerRepository; 
    private final OrderItemRepository orderItemRepository; 
    private final ProductRepository productRepository; 

    // 【【【 核心修改 】】】
    // 手動添加建構子並使用 @Autowired 進行依賴注入
    //@Autowired
    public OrderServiceImpl(OrderRepository orderRepository,  
                            CustomerRepository customerRepository,OrderItemRepository orderItemRepository,ProductRepository productRepository,
                            OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderItemService = orderItemService;

    }

    @Override
    @Transactional
    public List<Order> createOrder(CreateOrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("訂單至少需要包含一個商品");
        }
          // 1. 處理顧客：檢查信箱是否存在，不存在則建立新顧客
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                Customer newCustomer = new Customer();
                newCustomer.setCustomerName(request.getCustomerName());
                newCustomer.setEmail(request.getCustomerEmail());
                newCustomer.setAddress(request.getShippingAddress());
                return customerRepository.save(newCustomer);
            });

        // 2. 建立 Order 物件本身，但先不存總金額
        // Order order = new Order();
        // LocalDate localDate = request.getOrderDate();
        // Instant orderInstant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        // order.setOrderDate(orderInstant);
        // order.setCustomer(customer);
        // order.setStatus(request.getStatus());
        // order.setShippingAddress(request.getShippingAddress());
        
        
        
        //3. 處理訂單項目 (Order Items) 並計算總金額
        Map<Vendor, List<OrderItemRequest>> itemsGroupedByVendor = request.getItems().stream()
            .collect(Collectors.groupingBy(itemReq -> {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("找不到 ID 為 " + itemReq.getProductId() + " 的商品"));
                if (product.getVendor() == null) {
                    throw new IllegalStateException("ID 為 " + product.getId() + " 的商品沒有關聯的供應商");
                }
                return product.getVendor();
            }));

        // 4. 【【【 宣告 createdOrders 變數 】】】
        List<Order> createdOrders = new ArrayList<>();

        // 5. 【【【 外層迴圈：遍歷供應商群組 】】】
        for (Map.Entry<Vendor, List<OrderItemRequest>> entry : itemsGroupedByVendor.entrySet()) {
            // 從 Map 中取得這個供應商的所有商品
            List<OrderItemRequest> vendorItems = entry.getValue();

            // // 5.1 建立這張訂單的 Order 實體
            Order order = new Order();
            order.setCustomer(customer);
            order.setVendor(entry.getKey());
            LocalDate localDate = request.getOrderDate();
            Instant orderInstant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
            order.setOrderDate(orderInstant);
            
            order.setShippingAddress(request.getShippingAddress());
            order.setStatus(request.getStatus());
            

            // 5.2 為這張訂單建立 OrderItem 並計算該訂單的總金額
            List<OrderItem> orderItemsForThisOrder = new ArrayList<>();
            BigDecimal totalAmountForThisOrder = BigDecimal.ZERO;

            // 【【【 內層迴圈：使用 vendorItems 】】】
            for (OrderItemRequest itemRequest : vendorItems) {
                // 因為分組時已驗證過，這裡直接 get() 是相對安全的
                Product product = productRepository.findById(itemRequest.getProductId()).get();

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPurchasePrice(product.getPrice()); // 【修正】使用 setPrice
                orderItem.setProductName(product.getName());
                orderItemsForThisOrder.add(orderItem);

                totalAmountForThisOrder = totalAmountForThisOrder.add(
                    product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()))
                );
            }

            // 5.3 將總金額和訂單項目設定回 Order
            order.setTotalAmount(totalAmountForThisOrder);
            order.setOrderItems(orderItemsForThisOrder);

            // 5.4 儲存這張訂單並加入到結果列表中
            createdOrders.add(orderRepository.save(order));
        }

        // 6. 返回所有被建立的訂單列表
        return createdOrders;
    }
    @Override
    public Optional<Order> getOrderByIdWithDetails(Integer orderId) {
        return orderRepository.findByIdWithDetails(orderId);
    }

    @Override
    public List<Order> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單 ID: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByVendorIdAndStatus(Integer vendorId, OrderStatus status) {
        return orderRepository.findOrdersByVendorIdAndStatus(vendorId, status);
    }
}