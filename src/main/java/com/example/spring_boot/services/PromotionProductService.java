package com.example.spring_boot.services;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.domains.PromotionProduct;
import com.example.spring_boot.domains.products.Product;
import com.example.spring_boot.repository.PromotionProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * Service cho PromotionProduct (Áp dụng khuyến mãi cho sản phẩm)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionProductService {
    
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionService promotionService;
    private final com.example.spring_boot.services.products.ProductService productService;
    
    /**
     * Áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @param discountAmount Số tiền giảm cụ thể (có thể null)
     * @return PromotionProduct đã được tạo
     * @throws ResponseStatusException Nếu có lỗi validation
     */
    public PromotionProduct applyPromotionToProduct(String promotionId, String productId, Double discountAmount) {
        log.info("Applying promotion {} to product {}", promotionId, productId);
        
        // Validation
        validatePromotionAndProduct(promotionId, productId);
        
        // Kiểm tra xem đã áp dụng chưa
        if (promotionProductRepository.existsByPromotionIdAndProductId(promotionId, productId)) {
            throw new ResponseStatusException(CONFLICT, "Khuyến mãi đã được áp dụng cho sản phẩm này");
        }
        
        // Lấy thông tin promotion và product
        Promotion promotion = promotionService.getPromotionById(promotionId);
        Product product = productService.getById(productId);
        
        // Tính giá sau giảm
        Double finalDiscountAmount = calculateDiscountAmount(product.getPrice(), promotion.getDiscountPercent(), discountAmount);
        
        // Tạo PromotionProduct
        PromotionProduct promotionProduct = new PromotionProduct(
                promotionId, 
                productId, 
                finalDiscountAmount, 
                true
        );
        
        PromotionProduct savedPromotionProduct = promotionProductRepository.save(promotionProduct);
        
        // Cập nhật giá sản phẩm
        updateProductPrice(product, promotion.getDiscountPercent());
        
        log.info("Successfully applied promotion {} to product {} with new price: {}", 
                promotionId, productId, product.getPrice());
        
        return savedPromotionProduct;
    }
    
    /**
     * Áp dụng khuyến mãi cho nhiều sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @param discountAmount Số tiền giảm cụ thể (có thể null)
     * @return Danh sách PromotionProduct đã được tạo
     */
    public List<PromotionProduct> applyPromotionToProducts(String promotionId, List<String> productIds, Double discountAmount) {
        log.info("Applying promotion {} to {} products", promotionId, productIds.size());
        
        // Validation
        validatePromotionAndProducts(promotionId, productIds);
        
        // Lấy thông tin promotion
        Promotion promotion = promotionService.getPromotionById(promotionId);
        
        List<PromotionProduct> promotionProducts = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();
        
        for (String productId : productIds) {
            if (!promotionProductRepository.existsByPromotionIdAndProductId(promotionId, productId)) {
                // Lấy thông tin sản phẩm
                Product product = productService.getById(productId);
                
                // Tính giá sau giảm
                Double finalDiscountAmount = calculateDiscountAmount(product.getPrice(), promotion.getDiscountPercent(), discountAmount);
                
                // Tạo PromotionProduct
                PromotionProduct promotionProduct = new PromotionProduct(
                        promotionId, 
                        productId, 
                        finalDiscountAmount, 
                        true
                );
                promotionProducts.add(promotionProduct);
                
                // Cập nhật giá sản phẩm
                updateProductPrice(product, promotion.getDiscountPercent());
                productsToUpdate.add(product);
            }
        }
        
        List<PromotionProduct> savedPromotionProducts = promotionProductRepository.saveAll(promotionProducts);
        
        // Cập nhật giá sản phẩm trong database
        for (Product product : productsToUpdate) {
            productService.update(product.getId(), product);
        }
        
        log.info("Successfully applied promotion {} to {} products with updated prices", 
                promotionId, savedPromotionProducts.size());
        
        return savedPromotionProducts;
    }
    
    /**
     * Hủy áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @throws ResponseStatusException Nếu không tìm thấy
     */
    public void removePromotionFromProduct(String promotionId, String productId) {
        log.info("Removing promotion {} from product {}", promotionId, productId);
        
        PromotionProduct promotionProduct = promotionProductRepository
                .findByPromotionIdAndProductId(promotionId, productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy khuyến mãi áp dụng cho sản phẩm này"));
        
        // Khôi phục giá sản phẩm
        Product product = productService.getById(productId);
        restoreProductPrice(product, promotionProduct.getDiscountAmount());
        productService.update(productId, product);
        
        promotionProduct.setIsActive(false);
        promotionProduct.setUpdatedAt(LocalDateTime.now());
        
        promotionProductRepository.save(promotionProduct);
        log.info("Successfully removed promotion {} from product {} and restored original price", promotionId, productId);
    }
    
    /**
     * Hủy áp dụng khuyến mãi cho nhiều sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     */
    public void removePromotionFromProducts(String promotionId, List<String> productIds) {
        log.info("Removing promotion {} from {} products", promotionId, productIds.size());
        
        List<PromotionProduct> promotionProducts = promotionProductRepository
                .findByPromotionId(promotionId)
                .stream()
                .filter(pp -> productIds.contains(pp.getProductId()))
                .toList();
        
        // Khôi phục giá sản phẩm
        for (PromotionProduct pp : promotionProducts) {
            Product product = productService.getById(pp.getProductId());
            restoreProductPrice(product, pp.getDiscountAmount());
            productService.update(pp.getProductId(), product);
        }
        
        promotionProducts.forEach(pp -> {
            pp.setIsActive(false);
            pp.setUpdatedAt(LocalDateTime.now());
        });
        
        promotionProductRepository.saveAll(promotionProducts);
        log.info("Successfully removed promotion {} from {} products and restored original prices", 
                promotionId, promotionProducts.size());
    }
    
    /**
     * Lấy danh sách sản phẩm áp dụng khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @return Danh sách PromotionProduct
     */
    public List<PromotionProduct> getProductsByPromotion(String promotionId) {
        log.info("Getting products for promotion {}", promotionId);
        return promotionProductRepository.findByPromotionId(promotionId);
    }
    
    /**
     * Lấy danh sách khuyến mãi áp dụng cho sản phẩm
     * 
     * @param productId ID của sản phẩm
     * @return Danh sách PromotionProduct
     */
    public List<PromotionProduct> getPromotionsByProduct(String productId) {
        log.info("Getting promotions for product {}", productId);
        return promotionProductRepository.findByProductId(productId);
    }
    
    /**
     * Lấy danh sách sản phẩm đang áp dụng khuyến mãi active
     * 
     * @param promotionId ID của khuyến mãi
     * @return Danh sách PromotionProduct
     */
    public List<PromotionProduct> getActiveProductsByPromotion(String promotionId) {
        log.info("Getting active products for promotion {}", promotionId);
        return promotionProductRepository.findByPromotionIdAndIsActive(promotionId, true);
    }
    
    /**
     * Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm active
     * 
     * @param productId ID của sản phẩm
     * @return Danh sách PromotionProduct
     */
    public List<PromotionProduct> getActivePromotionsByProduct(String productId) {
        log.info("Getting active promotions for product {}", productId);
        return promotionProductRepository.findByProductIdAndIsActive(productId, true);
    }
    
    /**
     * Kiểm tra xem sản phẩm có đang áp dụng khuyến mãi không
     * 
     * @param productId ID của sản phẩm
     * @return true nếu có khuyến mãi active
     */
    public boolean hasActivePromotion(String productId) {
        log.info("Checking if product {} has active promotion", productId);
        return !promotionProductRepository.findByProductIdAndIsActive(productId, true).isEmpty();
    }
    
    /**
     * Lấy thông tin chi tiết PromotionProduct
     * 
     * @param id ID của PromotionProduct
     * @return PromotionProduct
     * @throws ResponseStatusException Nếu không tìm thấy
     */
    public PromotionProduct getPromotionProductById(String id) {
        log.info("Getting promotion product by ID: {}", id);
        
        return promotionProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy thông tin áp dụng khuyến mãi"));
    }
    
    /**
     * Cập nhật PromotionProduct
     * 
     * @param id ID của PromotionProduct
     * @param discountAmount Số tiền giảm mới
     * @param isActive Trạng thái active mới
     * @return PromotionProduct đã được cập nhật
     */
    public PromotionProduct updatePromotionProduct(String id, Double discountAmount, Boolean isActive) {
        log.info("Updating promotion product with ID: {}", id);
        
        PromotionProduct promotionProduct = getPromotionProductById(id);
        
        if (discountAmount != null) {
            promotionProduct.setDiscountAmount(discountAmount);
        }
        
        if (isActive != null) {
            promotionProduct.setIsActive(isActive);
        }
        
        promotionProduct.setUpdatedAt(LocalDateTime.now());
        
        PromotionProduct updatedPromotionProduct = promotionProductRepository.save(promotionProduct);
        log.info("Successfully updated promotion product with ID: {}", id);
        
        return updatedPromotionProduct;
    }
    
    /**
     * Xóa PromotionProduct
     * 
     * @param id ID của PromotionProduct
     */
    public void deletePromotionProduct(String id) {
        log.info("Deleting promotion product with ID: {}", id);
        
        PromotionProduct promotionProduct = getPromotionProductById(id);
        promotionProductRepository.delete(promotionProduct);
        
        log.info("Successfully deleted promotion product with ID: {}", id);
    }
    
    /**
     * Lấy tất cả PromotionProduct
     * 
     * @return Danh sách tất cả PromotionProduct
     */
    public List<PromotionProduct> getAllPromotionProducts() {
        log.info("Getting all promotion products");
        return promotionProductRepository.findAll();
    }
    
    /**
     * Validation cho promotion và product
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @throws ResponseStatusException Nếu có lỗi validation
     */
    private void validatePromotionAndProduct(String promotionId, String productId) {
        // Kiểm tra promotion tồn tại và active
        Promotion promotion = promotionService.getPromotionById(promotionId);
        if (!promotion.getIsActive()) {
            throw new ResponseStatusException(BAD_REQUEST, "Khuyến mãi không còn hoạt động");
        }
        
        // Kiểm tra promotion còn trong thời gian áp dụng
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new ResponseStatusException(BAD_REQUEST, "Khuyến mãi không còn trong thời gian áp dụng");
        }
        
        // Kiểm tra product tồn tại
        Product product = productService.getById(productId);
        if (product.getDeletedAt() != null) {
            throw new ResponseStatusException(BAD_REQUEST, "Sản phẩm đã bị xóa");
        }
    }
    
    /**
     * Validation cho promotion và nhiều products
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @throws ResponseStatusException Nếu có lỗi validation
     */
    private void validatePromotionAndProducts(String promotionId, List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Danh sách sản phẩm không được để trống");
        }
        
        // Validation cho promotion
        Promotion promotion = promotionService.getPromotionById(promotionId);
        if (!promotion.getIsActive()) {
            throw new ResponseStatusException(BAD_REQUEST, "Khuyến mãi không còn hoạt động");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            throw new ResponseStatusException(BAD_REQUEST, "Khuyến mãi không còn trong thời gian áp dụng");
        }
        
        // Validation cho từng product
        for (String productId : productIds) {
            Product product = productService.getById(productId);
            if (product.getDeletedAt() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Sản phẩm " + product.getName() + " đã bị xóa");
            }
        }
    }
    
    /**
     * Tính số tiền giảm giá
     * 
     * @param originalPrice Giá gốc
     * @param discountPercent Phần trăm giảm giá
     * @param fixedDiscountAmount Số tiền giảm cố định (có thể null)
     * @return Số tiền giảm giá
     */
    private Double calculateDiscountAmount(BigDecimal originalPrice, Integer discountPercent, Double fixedDiscountAmount) {
        if (fixedDiscountAmount != null) {
            return fixedDiscountAmount;
        }
        
        if (discountPercent != null && discountPercent > 0) {
            return originalPrice.doubleValue() * (discountPercent / 100.0);
        }
        
        return 0.0;
    }
    
    /**
     * Cập nhật giá sản phẩm sau khi áp dụng khuyến mãi
     * 
     * @param product Sản phẩm cần cập nhật
     * @param discountPercent Phần trăm giảm giá
     */
    private void updateProductPrice(Product product, Integer discountPercent) {
        if (discountPercent != null && discountPercent > 0) {
            BigDecimal originalPrice = product.getPrice();
            BigDecimal discountAmount = originalPrice.multiply(BigDecimal.valueOf(discountPercent / 100.0));
            BigDecimal newPrice = originalPrice.subtract(discountAmount);
            
            product.setPrice(newPrice);
            
            log.info("Updated product {} price from {} to {} (discount: {}%)", 
                    product.getName(), originalPrice, newPrice, discountPercent);
        }
    }
    
    /**
     * Khôi phục giá sản phẩm khi hủy áp dụng khuyến mãi
     * 
     * @param product Sản phẩm cần khôi phục
     * @param discountAmount Số tiền giảm giá đã áp dụng
     */
    private void restoreProductPrice(Product product, Double discountAmount) {
        if (discountAmount != null && discountAmount > 0) {
            BigDecimal currentPrice = product.getPrice();
            BigDecimal originalPrice = currentPrice.add(BigDecimal.valueOf(discountAmount));
            
            product.setPrice(originalPrice);
            
            log.info("Restored product {} price from {} to {}", 
                    product.getName(), currentPrice, originalPrice);
        }
    }
}
