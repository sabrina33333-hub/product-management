package com.example.productmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 導入您的 Service, Entity, DTO
import com.example.productmanagement.service.VendorService;
import com.example.productmanagement.dto.request.VendorRequest;
import com.example.productmanagement.entity.Vendor;

@RestController
@RequestMapping("/api/v1/vendors") // 所有廠商相關的 API 都以此路徑為基礎
public class VendorController {

    private final VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * API 端點：新增一個廠商
     * HTTP 方法：POST
     * URL：/api/v1/vendors
     */
    @PostMapping
    public ResponseEntity<Vendor> createVendor(@RequestBody VendorRequest vendorRequestDto) {
        Vendor newVendor = vendorService.createVendor(vendorRequestDto);
        return new ResponseEntity<>(newVendor, HttpStatus.CREATED); // 回傳 201 Created
    }

    /**
     * API 端點：查詢所有廠商
     * HTTP 方法：GET
     * URL：/api/v1/vendors
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors); // 回傳 200 OK
    }

    /**
     * API 端點：根據 ID 查詢單一廠商
     * HTTP 方法：GET
     * URL：/api/v1/vendors/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable("id") Integer vendorId) {
        Vendor vendor = vendorService.getVendorById(vendorId);
        return ResponseEntity.ok(vendor);
    }

    /**
     * API 端點：更新一個廠商的資料
     * HTTP 方法：PUT
     * URL：/api/v1/vendors/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable("id") Integer vendorId, @RequestBody VendorRequest vendorRequestDto) {
        Vendor updatedVendor = vendorService.updateVendor(vendorId, vendorRequestDto);
        return ResponseEntity.ok(updatedVendor);
    }

    /**
     * API 端點：刪除一個廠商
     * HTTP 方法：DELETE
     * URL：/api/v1/vendors/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable("id") Integer vendorId) {
        vendorService.deleteVendor(vendorId);
        return ResponseEntity.noContent().build(); // 回傳 204 No Content
    }
}

