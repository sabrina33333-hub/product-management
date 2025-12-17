package com.example.productmanagement.service;

import com.example.productmanagement.dto.request.VendorRequest;
import com.example.productmanagement.entity.Vendor;

import java.util.List;

/**
 * 廠商服務介面，定義廠商資料相關的業務操作。
 */
public interface VendorService {

    /**
     * 新增一個廠商。
     *
     * @param vendorRequestDto 包含廠商資訊的 DTO。
     * @return 新增後的廠商實體。
     */
    Vendor createVendor(VendorRequest vendorRequestDto);

    /**
     * 根據 ID 查詢單一廠商。
     *
     * @param vendorId 廠商 ID。
     * @return 找到的廠商實體。
     */
    Vendor getVendorById(Integer vendorId);

    /**
     * 查詢所有廠商。
     *
     * @return 所有廠商的列表。
     */
    List<Vendor> getAllVendors();

    /**
     * 更新一個現有廠商的資料。
     *
     * @param vendorId         要更新的廠商 ID。
     * @param vendorRequestDto 包含更新資訊的 DTO。
     * @return 更新後的廠商實體。
     */
    Vendor updateVendor(Integer vendorId, VendorRequest vendorRequestDto);

    /**
     * 刪除一個廠商。
     *
     * @param vendorId 要刪除的廠商 ID。
     */
    void deleteVendor(Integer vendorId);
}
