# 🐛 Debug Guide - Trang Thêm Khuyến mãi

## Vấn đề hiện tại
Trang "Thêm khuyến mãi" chỉ hiển thị tiêu đề và nút "Quay lại" nhưng thiếu form nhập liệu.

## Các bước debug

### 1. Kiểm tra các URL test
Thử truy cập các URL sau để xác định vấn đề:

```
# Form gốc (có thể bị lỗi)
http://localhost:8080/admin/promotions/create

# Form test với Thymeleaf
http://localhost:8080/admin/promotions/test

# Form đơn giản (không layout)
http://localhost:8080/admin/promotions/simple
```

### 2. Kiểm tra Console Browser
Mở Developer Tools (F12) và kiểm tra:
- **Console tab**: Có lỗi JavaScript nào không?
- **Network tab**: Có file CSS/JS nào load thất bại không?
- **Elements tab**: HTML có được render đúng không?

### 3. Kiểm tra Server Logs
Xem console của Spring Boot để kiểm tra:
- Controller có được gọi không?
- Có exception nào không?
- Model có được truyền đúng không?

### 4. Các nguyên nhân có thể

#### A. Layout Issues
- File `layouts/sidebar.html` hoặc `layouts/header.html` bị lỗi
- CSS conflict làm ẩn form
- JavaScript error ngăn render

#### B. Thymeleaf Issues
- Template không được compile đúng
- Thymeleaf expression bị lỗi
- Model attributes không được truyền

#### C. CSS Issues
- File `promotions.css` không load được
- CSS selector conflict
- Display properties bị override

#### D. JavaScript Issues
- JavaScript error ngăn form render
- Event listener conflict
- DOM manipulation lỗi

## Các file cần kiểm tra

### 1. Controller
```java
// File: PromotionPageController.java
@GetMapping("/create")
public String createPromotionPage(Model model) {
    model.addAttribute("promotion", new Promotion());
    model.addAttribute("isEdit", false);
    return "admin/promotions/promotion-form";
}
```

### 2. Template
```html
<!-- File: promotion-form.html -->
<form th:action="@{/admin/promotions/create}" th:object="${promotion}" method="post">
    <!-- Form fields -->
</form>
```

### 3. CSS
```css
/* File: promotions.css */
.form-section {
    background: #f8f9fa;
    border-radius: 15px;
    padding: 25px;
}
```

## Các bước sửa lỗi

### Bước 1: Test form đơn giản
Truy cập `http://localhost:8080/admin/promotions/simple`
- Nếu form hiển thị: Vấn đề ở layout hoặc CSS
- Nếu không hiển thị: Vấn đề ở controller hoặc routing

### Bước 2: Test form với Thymeleaf
Truy cập `http://localhost:8080/admin/promotions/test`
- Kiểm tra debug info có hiển thị không
- Kiểm tra form fields có render không

### Bước 3: Sửa form gốc
Nếu form đơn giản hoạt động:
1. Comment out layout includes
2. Thêm CSS inline
3. Kiểm tra từng phần form

### Bước 4: Khôi phục layout
Khi form hoạt động:
1. Uncomment layout includes
2. Kiểm tra CSS conflicts
3. Test responsive design

## Quick Fixes

### Fix 1: Tạm thời bỏ layout
```html
<!-- Comment out layout includes -->
<!-- <div th:replace="~{layouts/sidebar :: sidebar}"></div> -->
<!-- <div th:replace="~{layouts/header :: header}"></div> -->
```

### Fix 2: Thêm CSS inline
```html
<style>
    .form-section { 
        display: block !important; 
        background: white !important;
        padding: 20px !important;
    }
</style>
```

### Fix 3: Kiểm tra JavaScript
```javascript
// Thêm vào cuối trang
console.log('Form elements:', document.querySelectorAll('form'));
console.log('Form sections:', document.querySelectorAll('.form-section'));
```

## Kết quả mong đợi

Sau khi sửa, trang sẽ hiển thị:
- ✅ Header với tiêu đề "Thêm Khuyến mãi"
- ✅ Form với các trường: tên, mô tả, phần trăm, ngày tháng
- ✅ Preview card bên phải
- ✅ Nút "Tạo khuyến mãi" và "Quay lại"
- ✅ Validation và JavaScript hoạt động

## Liên hệ hỗ trợ

Nếu vẫn gặp vấn đề, hãy:
1. Chụp screenshot trang web
2. Copy console errors
3. Gửi server logs
4. Mô tả các bước đã thử
