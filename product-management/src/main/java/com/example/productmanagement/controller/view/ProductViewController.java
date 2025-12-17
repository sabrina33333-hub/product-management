package com.example.productmanagement.controller.view;

import com.example.productmanagement.dto.request.ProductRequest;
import com.example.productmanagement.entity.Category;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.repository.CategoryRepository;
import com.example.productmanagement.repository.VendorRepository;
import com.example.productmanagement.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products") // 專門負責所有 /products 開頭的路徑
@RequiredArgsConstructor
public class ProductViewController {

    private static final Logger logger = LoggerFactory.getLogger(ProductViewController.class);
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository; 
    // 1. 【改造】顯示所有商品列表
    @GetMapping
    public String getAllProducts(Model model) {
        logger.info("請求進入：顯示所有商品列表");
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        
        // 【關鍵改變#1】告訴 main.html 要載入哪個片段
        model.addAttribute("view", "products/product-list"); 
        model.addAttribute("pageTitle", "商品列表");

        // 【關鍵改變#2】返回主佈局，而不是具體頁面
        return "main"; 
    }

    // 2. 【改造】顯示新增/編輯商品表單
    @GetMapping("/form") // 使用 /form 作為路徑更清晰
    public String showProductForm(@RequestParam(required = false) Integer id, Model model) {
        Product product = new Product();
        String pageTitle = "新增商品";

        if (id != null) {
            logger.info("請求進入：顯示編輯商品表單，ID: {}", id);
            product = productService.findByIdForForm(id) .orElseThrow(() -> new IllegalArgumentException("無效的商品 ID: " + id)); // 根據 ID 查找商品
            pageTitle = "編輯商品";

             
        } else {
            logger.info("請求進入：顯示新增商品表單");
        }
        // 【【【 核心修改：從資料庫取得所有分類和供應商 】】】
        List<Category> allCategories = categoryRepository.findAll();
        List<Vendor> allVendors = vendorRepository.findAll();
        
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("allCategories", allCategories); // c. 【【【 用於填充下拉選單的分類列表 】】】
        model.addAttribute("allVendors", allVendors);
        model.addAttribute("view", "products/product-form");
        return "main";
    }

    // 3. 【改造】處理新增/更新商品的請求
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                          // 【修正 1】: 接收 Long 型別的 ID
                          @RequestParam("category") Integer categoryId, 
                          @RequestParam("vendor") Integer vendorId,
                          RedirectAttributes redirectAttributes) {

    try {
        // 【修正 2】: 根據您的 Service 設計，手動建立並填充 ProductRequest DTO
        ProductRequest productRequest = new ProductRequest(
            product.getId(), // 【修正 3】: 使用 getId() 而不是 getProductId()
            product.getName(),
            categoryId,
            product.getDescription(),
            product.getPrice(),
            product.getCost(),
            product.getStockQuantity(),
            product.getImageUrl(),
            vendorId
        );

        // 【修正 4】: 使用 getId() 進行判斷
        if (product.getId() == null) {
            logger.info("開始處理新增商品請求，商品名稱：{}", productRequest.name());
            // 將組裝好的 DTO 物件傳遞給 Service
            productService.createProduct(productRequest);
            redirectAttributes.addFlashAttribute("successMessage", "商品新增成功！");
        } else {
            logger.info("開始處理更新商品請求，商品 ID: {}", product.getId());
            // 同樣，將 DTO 物件傳遞給 Service
            productService.updateProduct(product.getId(), productRequest);
            redirectAttributes.addFlashAttribute("successMessage", "商品更新成功！");
        }

    } catch (Exception e) {
        logger.error("儲存商品時發生錯誤: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("errorMessage", "儲存失敗：" + e.getMessage());
        
        // 使用 getId()
        return "redirect:/products/form" + (product.getId() != null ? "?id=" + product.getId() : "");
    }

    return "redirect:/products";
}


    // 4. 【保留】處理刪除商品的請求
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        logger.info("開始處理刪除商品請求，ID：{}", id);
        productService.deleteById(id);
        return "redirect:/products";
    }

}