package com.example.productmanagement.service.impl;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 導入您的 Repositories, Models, DTOs 和 Exceptions
import com.example.productmanagement.repository.VendorRepository;
import com.example.productmanagement.service.VendorService;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.dto.request.VendorRequest;
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.exception.ResourceNotFoundException;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository; // 用於檢查關聯商品

    //@Autowired
    public VendorServiceImpl(VendorRepository vendorRepository, ProductRepository productRepository) {
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Vendor createVendor(VendorRequest vendorRequestDto) {
        Vendor vendor = new Vendor();
        vendor.setStoreName(vendorRequestDto.name());
        vendor.setContactPerson(vendorRequestDto.contacName());
        vendor.setContactPerson(vendorRequestDto.contacEmail());
        vendor.setContactPhone(vendorRequestDto.contacPhone());

        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor getVendorById(Integer vendorId) {
        if(vendorId == null){
            throw new IllegalArgumentException("廠商ID不可空白");
        }
        return vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到此廠商ID: " + vendorId));
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Transactional
    @Override
    public Vendor updateVendor(Integer vendorId, VendorRequest vendorRequestDto) {
        Vendor existingVendor = getVendorById(vendorId);

        existingVendor.setStoreName(vendorRequestDto.name());
        existingVendor.setContactPerson(vendorRequestDto.contacName());
        existingVendor.setContactPerson(vendorRequestDto.contacEmail());
        existingVendor.setContactPhone(vendorRequestDto.contacPhone());

        return vendorRepository.save(existingVendor);
    }

    @Transactional
    @Override
    public void deleteVendor(Integer vendorId) {
        if(vendorId == null){
            throw new IllegalArgumentException("廠商ID不可空白");
        }
        // 1. 檢查廠商是否存在
        if (!vendorRepository.existsById(vendorId)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + vendorId);
        }

        // 2. 檢查是否有任何商品關聯到此廠商
        // 假設 ProductRepository 中有 existsByVendorId 方法
        if (productRepository.existsById(vendorId)) {
            throw new IllegalStateException("Cannot delete vendor with id " + vendorId + " because it has associated products.");
        }

        // 3. 如果沒有關聯商品，則執行刪除
        vendorRepository.deleteById(vendorId);
    }
}

