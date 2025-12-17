package com.example.productmanagement.controller.view;

import com.example.productmanagement.dto.request.CreateOrderRequest;
import com.example.productmanagement.entity.Order;
import com.example.productmanagement.model.OrderStatus;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.repository.OrderRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderViewController {

    private final ProductRepository productRepository;

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderViewController(OrderService orderService,OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @GetMapping("/new-order") //新增訂單
        public String showCreateOrderForm(Model model) {
            model.addAttribute("orderRequest", new CreateOrderRequest());//空表單
            model.addAttribute("allProducts", productRepository.findAll());//撈所有產品下拉選單
            model.addAttribute("allStatuses", OrderStatus.values());//提供訂單狀態選擇

            model.addAttribute("view", "orders/new-order");
            model.addAttribute("pageTitle", "新增訂單");
            model.addAttribute("activePage", "orders");
            return "main";

        }
    /**
     * 顯示訂單列表 (老闆視角)
     */
    @GetMapping
    public String showOrderList(@RequestParam(name = "status",required = false) String status, Model model) {
        log.info("請求進入：顯示所有訂單列表，狀態篩選：{}", status);

        List<Order> orders;
        

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatusEum = OrderStatus.valueOf(status.toUpperCase());
                orders =  orderRepository.findByStatusWithCustomer(orderStatusEum); 
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findAllWithCustomer(); 
            }
        } else {
            orders = orderRepository.findAllWithCustomer();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        
        model.addAttribute("view", "orders/index");
        model.addAttribute("pageTitle", "訂單管理");
        model.addAttribute("activePage", "orders");  
        return "main";
    }

    /**
     * 顯示單一訂單詳情 (老闆視角)
     */
    @GetMapping("/{id}")
    public String showOrderDetail(@PathVariable("id") Integer orderId, Model model) {
        log.info("請求進入：顯示訂單詳情，ID: {}", orderId);

        Order order = orderService.getOrderByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單，ID: " + orderId));

        model.addAttribute("order", order);

        model.addAttribute("view", "orders/show");
        model.addAttribute("pageTitle", "訂單詳情 - #" + orderId);
        model.addAttribute("activePage", "orders");
        return "main";
    }


    
    

     @PostMapping//建立一筆新訂單
    public String createOrder(@ModelAttribute("orderRequest")CreateOrderRequest request){
        log.info("處理新增訂單表單提交...");
        orderService.createOrder(request);
        return"redirect:/orders";
    }


    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Integer orderId, @RequestParam("status") OrderStatus status) {
    orderService.updateOrderStatus(orderId, status); // 假設您在 Service 中建立了這個方法
    return "redirect:/orders";
}

}