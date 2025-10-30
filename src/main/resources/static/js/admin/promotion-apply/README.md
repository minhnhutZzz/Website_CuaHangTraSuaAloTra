# Module Áp dụng Khuyến mãi (Promotion Apply)

## Tổng quan
Module áp dụng khuyến mãi cho phép admin chọn khuyến mãi và áp dụng cho các sản phẩm cụ thể trong hệ thống.

## Cấu trúc Files

### Backend (Java)
- `PromotionProduct.java` - Domain entity cho mối quan hệ khuyến mãi-sản phẩm
- `PromotionProductRepository.java` - MongoDB repository
- `PromotionProductService.java` - Business logic
- `PromotionProductController.java` - REST API controller
- `PromotionApplyPageController.java` - Web page controller

### Frontend (HTML/CSS/JS)
- `promotion-apply.html` - Trang áp dụng khuyến mãi
- `promotion-apply.js` - JavaScript xử lý form
- `promotions.css` - Styles (sử dụng chung)

## Tính năng chính

### 1. Chọn Khuyến mãi
- ✅ Hiển thị danh sách khuyến mãi đang hoạt động
- ✅ Tìm kiếm khuyến mãi theo tên
- ✅ Hiển thị thông tin chi tiết khuyến mãi
- ✅ Chọn khuyến mãi để áp dụng

### 2. Chọn Sản phẩm
- ✅ Hiển thị danh sách sản phẩm có thể áp dụng
- ✅ Tìm kiếm sản phẩm theo tên/mô tả
- ✅ Chọn nhiều sản phẩm cùng lúc
- ✅ Chọn tất cả/bỏ chọn tất cả

### 3. Áp dụng Khuyến mãi
- ✅ Áp dụng khuyến mãi cho sản phẩm đã chọn
- ✅ Hiển thị sản phẩm đã áp dụng khuyến mãi
- ✅ Hủy áp dụng khuyến mãi cho sản phẩm
- ✅ Validation đầy đủ

### 4. Giao diện
- ✅ Responsive design
- ✅ Real-time search
- ✅ Loading states
- ✅ Success/error notifications
- ✅ Interactive UI

## API Endpoints

### REST API
```
POST   /api/promotion-products/apply              - Áp dụng khuyến mãi cho sản phẩm
POST   /api/promotion-products/apply-multiple     - Áp dụng khuyến mãi cho nhiều sản phẩm
DELETE /api/promotion-products/remove             - Hủy áp dụng khuyến mãi cho sản phẩm
DELETE /api/promotion-products/remove-multiple    - Hủy áp dụng khuyến mãi cho nhiều sản phẩm
GET    /api/promotion-products/by-promotion/{id}  - Lấy sản phẩm theo khuyến mãi
GET    /api/promotion-products/by-product/{id}    - Lấy khuyến mãi theo sản phẩm
GET    /api/promotion-products/active/by-promotion/{id} - Lấy sản phẩm active theo khuyến mãi
GET    /api/promotion-products/active/by-product/{id}   - Lấy khuyến mãi active theo sản phẩm
GET    /api/promotion-products/check/{id}         - Kiểm tra sản phẩm có khuyến mãi không
GET    /api/promotion-products/{id}               - Lấy chi tiết PromotionProduct
PUT    /api/promotion-products/{id}               - Cập nhật PromotionProduct
DELETE /api/promotion-products/{id}               - Xóa PromotionProduct
GET    /api/promotion-products                    - Lấy tất cả PromotionProduct
```

### Web Pages
```
GET    /admin/promotion-apply                     - Trang áp dụng khuyến mãi
GET    /admin/promotion-apply/select-products/{id} - Trang chọn sản phẩm
POST   /admin/promotion-apply/apply               - Xử lý áp dụng khuyến mãi
POST   /admin/promotion-apply/remove              - Xử lý hủy áp dụng khuyến mãi
GET    /admin/promotion-apply/api/products/{id}   - API lấy sản phẩm theo khuyến mãi
GET    /admin/promotion-apply/api/available-products/{id} - API lấy sản phẩm chưa áp dụng
```

## Cấu trúc Database

### Collection: promotion_products
```json
{
  "id": "promotion_product_id",
  "promotionId": "promotion_id",
  "productId": "product_id",
  "discountAmount": 100000,
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00.000+00:00",
  "updatedAt": "2024-01-01T00:00:00.000+00:00",
  "_class": "com.example.spring_boot.domains.PromotionProduct"
}
```

## Sử dụng

### 1. Truy cập trang áp dụng khuyến mãi
```
http://localhost:8080/admin/promotion-apply
```

### 2. Chọn khuyến mãi
1. Xem danh sách khuyến mãi đang hoạt động
2. Click vào khuyến mãi muốn áp dụng
3. Trang sẽ reload với khuyến mãi đã chọn

### 3. Chọn sản phẩm
1. Xem danh sách sản phẩm có thể áp dụng
2. Sử dụng tìm kiếm để lọc sản phẩm
3. Chọn sản phẩm muốn áp dụng khuyến mãi
4. Có thể chọn tất cả hoặc bỏ chọn tất cả

### 4. Áp dụng khuyến mãi
1. Click nút "Áp dụng khuyến mãi"
2. Xác nhận áp dụng
3. Nhận thông báo thành công

### 5. Quản lý sản phẩm đã áp dụng
1. Xem danh sách sản phẩm đã áp dụng
2. Click nút X để hủy áp dụng
3. Xác nhận hủy áp dụng

## JavaScript Classes

### PromotionApplyHandler
Xử lý chính cho trang áp dụng khuyến mãi:
- Chọn khuyến mãi và sản phẩm
- Tìm kiếm và lọc
- Áp dụng và hủy áp dụng khuyến mãi
- Hiển thị thông báo

### PromotionApplyUtils
Các utility functions:
- Format currency
- Format date/datetime
- Kiểm tra trạng thái khuyến mãi
- Tính thời gian còn lại

## CSS Classes

### Layout
- `.promotion-card` - Card khuyến mãi
- `.product-card` - Card sản phẩm
- `.selected-products` - Danh sách sản phẩm đã chọn

### States
- `.selected` - Trạng thái đã chọn
- `.product-checkbox` - Checkbox sản phẩm
- `.search-results` - Kết quả tìm kiếm

### Components
- `.notification-toast` - Thông báo toast
- `.search-box` - Hộp tìm kiếm
- `.promotion-list` - Danh sách khuyến mãi

## Validation

### Backend Validation
- Khuyến mãi phải tồn tại và active
- Khuyến mãi phải trong thời gian áp dụng
- Sản phẩm phải tồn tại và chưa bị xóa
- Không được áp dụng trùng khuyến mãi cho sản phẩm

### Frontend Validation
- Phải chọn ít nhất một sản phẩm
- Hiển thị loading state khi xử lý
- Thông báo lỗi rõ ràng

## Lưu ý

1. **Performance**: Sử dụng pagination cho danh sách lớn
2. **Security**: Cần authentication để truy cập admin
3. **Validation**: Kiểm tra đầy đủ ở cả frontend và backend
4. **Error Handling**: Xử lý lỗi thống nhất
5. **Responsive**: Hoạt động tốt trên mobile

## Troubleshooting

### Lỗi thường gặp
1. **"Khuyến mãi không còn hoạt động"** - Kiểm tra trạng thái khuyến mãi
2. **"Khuyến mãi không còn trong thời gian áp dụng"** - Kiểm tra ngày bắt đầu/kết thúc
3. **"Sản phẩm đã bị xóa"** - Kiểm tra trạng thái sản phẩm
4. **"Khuyến mãi đã được áp dụng cho sản phẩm này"** - Kiểm tra trùng lặp

### Debug
- Kiểm tra console browser cho JavaScript errors
- Kiểm tra server logs cho backend errors
- Sử dụng Swagger UI để test API: `http://localhost:8080/swagger-ui/index.html`

## Kết luận

Module áp dụng khuyến mãi cung cấp giao diện trực quan và dễ sử dụng để:
- ✅ Chọn khuyến mãi từ danh sách
- ✅ Chọn sản phẩm để áp dụng
- ✅ Áp dụng khuyến mãi cho nhiều sản phẩm
- ✅ Quản lý sản phẩm đã áp dụng
- ✅ Hủy áp dụng khi cần thiết

Chúc bạn sử dụng thành công! 🎉
