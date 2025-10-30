package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.products.Category;
import com.example.spring_boot.domains.products.Product;
import com.example.spring_boot.domains.products.ProductImage;
import com.example.spring_boot.services.products.CategoryService;
import com.example.spring_boot.services.products.ProductImageService;
import com.example.spring_boot.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller để khởi tạo dữ liệu mẫu
 */
@Slf4j
@Controller
@RequestMapping("/data-init")
@RequiredArgsConstructor
public class DataInitController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    
    @GetMapping("/create-sample-data")
    public String createSampleData(Model model) {
        log.info("Creating sample data...");
        
        try {
            // Tạo categories mẫu
            Category category1 = categoryService.createCategory("Thời trang nam", "Quần áo, giày dép cho nam giới");
            Category category2 = categoryService.createCategory("Thời trang nữ", "Quần áo, giày dép cho nữ giới");
            Category category3 = categoryService.createCategory("Phụ kiện", "Túi xách, ví, đồng hồ, trang sức");
            
            // Tạo products mẫu
            Product product1 = Product.builder()
                    .name("Áo thun nam basic")
                    .description("Áo thun cotton 100%, thoáng mát, dễ giặt")
                    .price(new BigDecimal("150000"))
                    .stock(50)
                    .categoryId(new ObjectId(category1.getId()))
                    .build();
            product1 = productService.create(product1);
            
            Product product2 = Product.builder()
                    .name("Quần jeans nữ slim")
                    .description("Quần jeans co giãn, form dáng đẹp")
                    .price(new BigDecimal("350000"))
                    .stock(30)
                    .categoryId(new ObjectId(category2.getId()))
                    .build();
            product2 = productService.create(product2);
            
            Product product3 = Product.builder()
                    .name("Túi xách nữ da thật")
                    .description("Túi xách da bò thật, thiết kế sang trọng")
                    .price(new BigDecimal("800000"))
                    .stock(15)
                    .categoryId(new ObjectId(category3.getId()))
                    .build();
            product3 = productService.create(product3);
            
            Product product4 = Product.builder()
                    .name("Áo sơ mi nam công sở")
                    .description("Áo sơ mi cotton, form chuẩn, phù hợp công sở")
                    .price(new BigDecimal("250000"))
                    .stock(40)
                    .categoryId(new ObjectId(category1.getId()))
                    .build();
            product4 = productService.create(product4);
            
            Product product5 = Product.builder()
                    .name("Váy đầm nữ dự tiệc")
                    .description("Váy đầm sang trọng, phù hợp dự tiệc, sự kiện")
                    .price(new BigDecimal("600000"))
                    .stock(20)
                    .categoryId(new ObjectId(category2.getId()))
                    .build();
            product5 = productService.create(product5);
            
            Product product6 = Product.builder()
                    .name("Giày sneaker unisex")
                    .description("Giày thể thao unisex, êm chân, dễ phối đồ")
                    .price(new BigDecimal("450000"))
                    .stock(35)
                    .categoryId(new ObjectId(category3.getId()))
                    .build();
            product6 = productService.create(product6);
            
            // Tạo hình ảnh mẫu cho sản phẩm
            ProductImage image1 = ProductImage.builder()
                    .productId(new ObjectId(product1.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image1);
            
            ProductImage image2 = ProductImage.builder()
                    .productId(new ObjectId(product2.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image2);
            
            ProductImage image3 = ProductImage.builder()
                    .productId(new ObjectId(product3.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image3);
            
            ProductImage image4 = ProductImage.builder()
                    .productId(new ObjectId(product4.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image4);
            
            ProductImage image5 = ProductImage.builder()
                    .productId(new ObjectId(product5.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image5);
            
            ProductImage image6 = ProductImage.builder()
                    .productId(new ObjectId(product6.getId()))
                    .imageUrl("https://spencil.vn/wp-content/uploads/2024/11/chup-anh-san-pham-SPencil-Agency-1.jpg")
                    .isPrimary(true)
                    .build();
            productImageService.create(image6);
            
            // Lấy danh sách sản phẩm để hiển thị
            var products = productService.getAllActive(0, 10);
            
            model.addAttribute("message", "Đã tạo thành công " + products.items.size() + " sản phẩm mẫu!");
            model.addAttribute("products", products.items);
            model.addAttribute("categories", List.of(category1, category2, category3));
            
            log.info("Sample data created successfully: {} products", products.items.size());
            
        } catch (Exception e) {
            log.error("Error creating sample data", e);
            model.addAttribute("error", "Lỗi khi tạo dữ liệu mẫu: " + e.getMessage());
        }
        
        return "test/simple-products";
    }
}
