package com.example.productmanagement.service.impl;

import com.example.productmanagement.dto.request.OrderItemRequest;
import com.example.productmanagement.entity.Order;
import com.example.productmanagement.entity.OrderItem;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.exception.InsufficientStockException;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.repository.OrderItemRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.OrderItemService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    
    //@Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public OrderItem createOrderItem(Order order, OrderItemRequest itemDto) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品 ID: " + itemDto.getProductId()));

        if (product.getStockQuantity() < itemDto.getQuantity()) {
            throw new InsufficientStockException("庫存不足: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
        productRepository.save(product);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setPurchasePrice(product.getPrice());

        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }
}