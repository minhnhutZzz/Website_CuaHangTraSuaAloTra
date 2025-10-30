# 🚀 Hướng dẫn sử dụng nhanh - Module Khuyến mãi

## 📍 Truy cập chức năng

### 1. Từ Sidebar Admin
- **Quản lý khuyến mãi**: `http://localhost:8080/admin/promotions`
- **Thêm khuyến mãi**: `http://localhost:8080/admin/promotions/create`

### 2. Từ Menu Sidebar
- Click vào **"Quản lý khuyến mãi"** để xem danh sách
- Click vào **"Thêm khuyến mãi"** để tạo mới

## 🎯 Các bước thêm khuyến mãi

### Bước 1: Truy cập trang thêm khuyến mãi
```
http://localhost:8080/admin/promotions/create
```

### Bước 2: Điền thông tin bắt buộc
- ✅ **Tên khuyến mãi**: Tên rõ ràng, dễ hiểu
- ✅ **Phần trăm giảm giá**: Từ 1-100%
- ✅ **Ngày bắt đầu**: Chọn ngày bắt đầu
- ✅ **Ngày kết thúc**: Phải sau ngày bắt đầu

### Bước 3: Điền thông tin tùy chọn
- 📝 **Mô tả**: Mô tả chi tiết khuyến mãi
- 🔘 **Trạng thái**: Bật/tắt khuyến mãi

### Bước 4: Xem trước và tạo
- 👀 **Preview**: Xem trước khuyến mãi real-time
- 💾 **Tạo khuyến mãi**: Click nút tạo
- ✅ **Thông báo**: Nhận thông báo thành công

## 🔧 API Endpoints

### REST API
```bash
# Tạo khuyến mãi mới
POST /api/promotions
Content-Type: application/json

{
  "name": "Khuyến mãi test",
  "description": "Mô tả khuyến mãi",
  "discountPercent": 20,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "isActive": true
}
```

```bash
# Lấy danh sách khuyến mãi
GET /api/promotions

# Tìm kiếm khuyến mãi
GET /api/promotions/search?name=test

# Lấy khuyến mãi đang hoạt động
GET /api/promotions/active
```

### Web Pages
```bash
# Trang danh sách
GET /admin/promotions

# Trang tạo mới
GET /admin/promotions/create

# Trang chỉnh sửa
GET /admin/promotions/edit/{id}
```

## 🧪 Test chức năng

### 1. Sử dụng Demo Script
Mở browser console và chạy:
```javascript
// Tạo khuyến mãi demo
createDemoPromotions()

// Chạy demo đầy đủ
runFullDemo()

// Test giao diện
testUI()

// Dọn dẹp demo
cleanupDemoPromotions()
```

### 2. Sử dụng API Tester
```javascript
// Test tạo khuyến mãi
promotionTester.testCreatePromotion()

// Test lấy danh sách
promotionTester.testGetAllPromotions()

// Chạy tất cả tests
promotionTester.runAllTests()
```

## 🎨 Giao diện

### Trang danh sách (`/admin/promotions`)
- 📊 **Thống kê**: Tổng khuyến mãi, đang hiển thị, trang hiện tại
- 🔍 **Tìm kiếm**: Tìm theo tên khuyến mãi
- 📄 **Phân trang**: Sắp xếp và phân trang
- ⚡ **Thao tác**: Chỉnh sửa, xóa khuyến mãi

### Trang tạo mới (`/admin/promotions/create`)
- 📝 **Form**: Điền thông tin khuyến mãi
- 👀 **Preview**: Xem trước real-time
- ✅ **Validation**: Kiểm tra dữ liệu đầu vào
- 💾 **Submit**: Tạo khuyến mãi mới

## 🚨 Lưu ý quan trọng

### Validation
- Tên khuyến mãi không được trùng
- Phần trăm giảm giá từ 1-100%
- Ngày kết thúc phải sau ngày bắt đầu
- Ngày kết thúc phải sau thời gian hiện tại

### Database
- Collection: `promotions`
- Soft delete: `isActive = false`
- Timezone: Sử dụng LocalDateTime

### Security
- Cần authentication để truy cập admin
- API cần validation đầy đủ
- XSS protection cho input

## 🐛 Troubleshooting

### Lỗi thường gặp
1. **"Tên khuyến mãi đã tồn tại"**
   - Kiểm tra tên có trùng không
   - Sử dụng tên khác

2. **"Ngày kết thúc phải sau ngày bắt đầu"**
   - Kiểm tra thứ tự ngày
   - Đảm bảo ngày kết thúc sau ngày bắt đầu

3. **"Phần trăm giảm giá phải từ 1-100"**
   - Kiểm tra giá trị nhập vào
   - Sử dụng số từ 1-100

### Debug
- Kiểm tra browser console
- Kiểm tra server logs
- Sử dụng Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 📚 Tài liệu tham khảo

- **README.md**: Tài liệu chi tiết
- **api-test.js**: Script test API
- **demo.js**: Script demo chức năng
- **Swagger UI**: Tài liệu API tự động

## 🎉 Kết luận

Module khuyến mãi đã sẵn sàng sử dụng! Bạn có thể:
- ✅ Tạo khuyến mãi mới
- ✅ Quản lý danh sách khuyến mãi
- ✅ Chỉnh sửa và xóa khuyến mãi
- ✅ Sử dụng API REST
- ✅ Test đầy đủ chức năng

Chúc bạn sử dụng thành công! 🚀
