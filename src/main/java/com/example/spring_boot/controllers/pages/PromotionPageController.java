package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.services.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller cho trang quản lý khuyến mãi
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class PromotionPageController {
    
    private final PromotionService promotionService;
    
    /**
     * Trang danh sách khuyến mãi
     * 
     * @param model Model để truyền dữ liệu
     * @param page Số trang
     * @param size Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp
     * @param search Từ khóa tìm kiếm
     * @return Tên view
     */
    @GetMapping
    public String promotionsPage(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "name") String sortBy,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                @RequestParam(required = false) String search) {
        
        log.info("Loading promotions page - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                page, size, sortBy, sortDir, search);
        
        try {
            Page<Promotion> promotionPage;
            
            if (search != null && !search.trim().isEmpty()) {
                // Tìm kiếm theo tên
                promotionPage = promotionService.getPromotionsPaged(page, size, sortBy, sortDir);
                // Lọc kết quả theo search term
                promotionPage.getContent().removeIf(p -> 
                    !p.getName().toLowerCase().contains(search.toLowerCase()));
            } else {
                promotionPage = promotionService.getPromotionsPaged(page, size, sortBy, sortDir);
            }
            
            model.addAttribute("promotions", promotionPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", promotionPage.getTotalPages());
            model.addAttribute("totalElements", promotionPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("search", search);
            
            // Thông tin phân trang
            model.addAttribute("hasNext", promotionPage.hasNext());
            model.addAttribute("hasPrevious", promotionPage.hasPrevious());
            model.addAttribute("isFirst", promotionPage.isFirst());
            model.addAttribute("isLast", promotionPage.isLast());
            
        } catch (Exception e) {
            log.error("Error loading promotions page", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách khuyến mãi");
        }
        
        return "admin/promotions/promotions";
    }
    
    /**
     * Trang tạo khuyến mãi mới
     * 
     * @param model Model để truyền dữ liệu
     * @return Tên view
     */
    @GetMapping("/create")
    public String createPromotionPage(Model model) {
        log.info("Loading create promotion page");
        
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("isEdit", false);
        
        return "admin/promotions/simple-create";
    }
    
    /**
     * Trang test form đơn giản
     * 
     * @param model Model để truyền dữ liệu
     * @return Tên view
     */
    @GetMapping("/test")
    public String testFormPage(Model model) {
        log.info("Loading test form page");
        
        model.addAttribute("promotion", new Promotion());
        
        return "admin/promotions/test-form";
    }
    
    /**
     * Trang form đơn giản (không dùng layout)
     * 
     * @return Tên view
     */
    @GetMapping("/simple")
    public String simpleFormPage() {
        log.info("Loading simple form page");
        return "admin/promotions/simple-form";
    }
    
    /**
     * Trang chỉnh sửa khuyến mãi
     * 
     * @param id ID của khuyến mãi
     * @param model Model để truyền dữ liệu
     * @return Tên view
     */
    @GetMapping("/edit/{id}")
    public String editPromotionPage(@PathVariable String id, Model model) {
        log.info("Loading edit promotion page for ID: {}", id);
        
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            model.addAttribute("promotion", promotion);
            model.addAttribute("isEdit", true);
            
            return "admin/promotions/promotion-form";
            
        } catch (Exception e) {
            log.error("Error loading edit promotion page for ID: {}", id, e);
            return "redirect:/admin/promotions?error=Không tìm thấy khuyến mãi";
        }
    }
    
    /**
     * Xử lý tạo khuyến mãi mới
     * 
     * @param promotion Thông tin khuyến mãi
     * @param model Model để truyền dữ liệu
     * @return Redirect đến trang danh sách
     */
    @PostMapping("/create")
    public String createPromotion(HttpServletRequest request, @ModelAttribute Promotion promotion, Model model) {
        // Log giá trị isActive từ form
        String[] actives = request.getParameterValues("isActive");
        System.out.println("==> PARAM isActive: " + java.util.Arrays.toString(actives));
        if (actives != null && actives.length > 0) {
            String lastValue = actives[actives.length - 1];
            System.out.println("==> LAST VALUE isActive: " + lastValue);
            promotion.setIsActive("true".equalsIgnoreCase(lastValue) || "on".equalsIgnoreCase(lastValue));
            System.out.println("==> SET promotion.isActive: " + promotion.getIsActive());
        } else {
            promotion.setIsActive(false);
            System.out.println("==> SET promotion.isActive: false (no value)");
        }
        try {
            Promotion createdPromotion = promotionService.createPromotion(promotion);
            log.info("Successfully created promotion with ID: {}", createdPromotion.getId());
            return "redirect:/admin/promotions?success=Thêm khuyến mãi thành công!";
        } catch (Exception e) {
            log.error("Error creating promotion", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tạo khuyến mãi: " + e.getMessage());
            model.addAttribute("promotion", promotion);
            model.addAttribute("isEdit", false);
            return "admin/promotions/promotion-form";
        }
    }
    
    /**
     * Xử lý cập nhật khuyến mãi
     * 
     * @param id ID của khuyến mãi
     * @param promotion Thông tin khuyến mãi mới
     * @param model Model để truyền dữ liệu
     * @return Redirect đến trang danh sách
     */
    @PostMapping("/edit/{id}")
    public String updatePromotion(@PathVariable String id, 
                                 @ModelAttribute Promotion promotion, 
                                 Model model) {
        log.info("Updating promotion with ID: {}", id);
        
        try {
            Promotion updatedPromotion = promotionService.updatePromotion(id, promotion);
            log.info("Successfully updated promotion with ID: {}", updatedPromotion.getId());
            
            return "redirect:/admin/promotions?success=Cập nhật khuyến mãi thành công!";
            
        } catch (Exception e) {
            log.error("Error updating promotion with ID: {}", id, e);
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật khuyến mãi: " + e.getMessage());
            model.addAttribute("promotion", promotion);
            model.addAttribute("isEdit", true);
            
            return "admin/promotions/promotion-form";
        }
    }
    
    /**
     * Xóa khuyến mãi
     * 
     * @param id ID của khuyến mãi
     * @return Redirect đến trang danh sách
     */
    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable String id) {
        log.info("Deleting promotion with ID: {}", id);
        
        try {
            promotionService.deletePromotion(id);
            log.info("Successfully deleted promotion with ID: {}", id);
            
            return "redirect:/admin/promotions?success=Xóa khuyến mãi thành công!";
            
        } catch (Exception e) {
            log.error("Error deleting promotion with ID: {}", id, e);
            return "redirect:/admin/promotions?error=Có lỗi xảy ra khi xóa khuyến mãi: " + e.getMessage();
        }
    }
}
