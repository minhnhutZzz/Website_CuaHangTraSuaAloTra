# Module Quản lý Khuyến mãi (Promotion Management)

## Tổng quan
Module quản lý khuyến mãi cho phép admin tạo, chỉnh sửa, xóa và quản lý các chương trình khuyến mãi trong hệ thống.

## Cấu trúc Files

### Backend (Java)
- `Promotion.java` - Domain entity
- `PromotionRepository.java` - MongoDB repository
- `PromotionService.java` - Business logic
- `PromotionController.java` - REST API controller
- `PromotionPageController.java` - Web page controller

### Frontend (HTML/CSS/JS)
- `promotions.html` - Trang danh sách khuyến mãi
- `promotion-form.html` - Trang tạo/chỉnh sửa khuyến mãi
- `promotions.css` - Styles cho module
- `promotion-form.js` - JavaScript xử lý form

## Tính năng chính

### 1. Quản lý Khuyến mãi
- ✅ Tạo khuyến mãi mới
- ✅ Chỉnh sửa khuyến mãi
- ✅ Xóa khuyến mãi (soft delete)
- ✅ Xem danh sách khuyến mãi
- ✅ Tìm kiếm theo tên
- ✅ Phân trang và sắp xếp

### 2. Validation
- ✅ Tên khuyến mãi bắt buộc
- ✅ Phần trăm giảm giá từ 1-100%
- ✅ Ngày kết thúc phải sau ngày bắt đầu
- ✅ Kiểm tra trùng tên khuyến mãi

### 3. Giao diện
- ✅ Responsive design
- ✅ Preview khuyến mãi real-time
- ✅ Thông báo thành công/lỗi
- ✅ Modal xác nhận xóa
- ✅ Loading states

## API Endpoints

### REST API
```
POST   /api/promotions              - Tạo khuyến mãi mới
GET    /api/promotions/{id}         - Lấy chi tiết khuyến mãi
PUT    /api/promotions/{id}         - Cập nhật khuyến mãi
DELETE /api/promotions/{id}         - Xóa khuyến mãi
GET    /api/promotions              - Lấy danh sách khuyến mãi
GET    /api/promotions/paged        - Lấy danh sách có phân trang
GET    /api/promotions/search       - Tìm kiếm theo tên
GET    /api/promotions/active       - Lấy khuyến mãi đang hoạt động
GET    /api/promotions/current-active - Lấy khuyến mãi hiện tại
GET    /api/promotions/count        - Đếm số lượng khuyến mãi
```

### Web Pages
```
GET    /admin/promotions            - Trang danh sách
GET    /admin/promotions/create     - Trang tạo mới
GET    /admin/promotions/edit/{id}  - Trang chỉnh sửa
POST   /admin/promotions/create     - Xử lý tạo mới
POST   /admin/promotions/edit/{id}  - Xử lý cập nhật
POST   /admin/promotions/delete/{id} - Xử lý xóa
```

## Cấu trúc Database

### Collection: promotions
```json
{
  "id": "69003c7e4fff17d513881802",
  "name": "Ưu đãi chào tuần mới: Hồng trà truyền thống",
  "description": "Mô tả khuyến mãi",
  "discountPercent": 15,
  "startDate": "2025-09-30T17:00:00.000+00:00",
  "endDate": "2025-10-30T17:00:00.000+00:00",
  "isActive": true,
  "_class": "com.example.spring_boot.domains.Promotion.Promotion"
}
```

## Sử dụng

### 1. Truy cập trang quản lý
```
http://localhost:8080/admin/promotions
```

### 2. Tạo khuyến mãi mới
1. Click nút "Thêm Khuyến mãi"
2. Điền thông tin bắt buộc:
   - Tên khuyến mãi
   - Phần trăm giảm giá (1-100%)
   - Ngày bắt đầu
   - Ngày kết thúc
3. Điền thông tin tùy chọn:
   - Mô tả khuyến mãi
   - Trạng thái hoạt động
4. Click "Tạo khuyến mãi"

### 3. Chỉnh sửa khuyến mãi
1. Click nút "Chỉnh sửa" (biểu tượng bút chì)
2. Thay đổi thông tin cần thiết
3. Click "Cập nhật"

### 4. Xóa khuyến mãi
1. Click nút "Xóa" (biểu tượng thùng rác)
2. Xác nhận trong modal
3. Khuyến mãi sẽ được đánh dấu là không hoạt động

## JavaScript Classes

### PromotionFormHandler
Xử lý form tạo/chỉnh sửa khuyến mãi:
- Validation form
- Preview real-time
- Xử lý submit

### PromotionListHandler
Xử lý trang danh sách:
- Tìm kiếm
- Xác nhận xóa
- Auto-hide alerts

### PromotionUtils
Các utility functions:
- Format currency
- Format date/datetime
- Kiểm tra trạng thái khuyến mãi
- Tính thời gian còn lại

## CSS Classes

### Layout
- `.form-section` - Phần form
- `.preview-card` - Card xem trước
- `.stats-card` - Card thống kê

### Components
- `.discount-badge` - Badge phần trăm giảm giá
- `.status-badge` - Badge trạng thái
- `.date-time-input` - Input ngày giờ

### States
- `.has-content` - Card có nội dung
- `.loading` - Trạng thái loading
- `.required-field` - Trường bắt buộc

## Lưu ý

1. **Validation**: Tất cả validation được thực hiện ở cả frontend và backend
2. **Soft Delete**: Khuyến mãi không bị xóa hoàn toàn, chỉ đánh dấu `isActive = false`
3. **Time Zone**: Sử dụng LocalDateTime, cần chú ý timezone khi deploy
4. **Performance**: Sử dụng pagination cho danh sách lớn
5. **Security**: Cần thêm authentication/authorization cho production

## Troubleshooting

### Lỗi thường gặp
1. **"Tên khuyến mãi đã tồn tại"** - Kiểm tra tên có trùng không
2. **"Ngày kết thúc phải sau ngày bắt đầu"** - Kiểm tra thứ tự ngày
3. **"Phần trăm giảm giá phải từ 1-100"** - Kiểm tra giá trị nhập vào

### Debug
- Kiểm tra console browser cho JavaScript errors
- Kiểm tra server logs cho backend errors
- Sử dụng Swagger UI để test API: `http://localhost:8080/swagger-ui/index.html`
