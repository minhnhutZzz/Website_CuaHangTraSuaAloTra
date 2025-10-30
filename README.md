# AloTrà Milk Tea Shop – Spring Boot (Java 17, MongoDB)

> A full-featured milk-tea shop and news portal built on Spring Boot 3 and MongoDB with Thymeleaf views for Admin/Client, REST APIs with OpenAPI/Swagger, soft-delete, manual pagination, media uploads (Cloudinary), and deployment-ready Docker support.

## Mục lục nhanh
- Giới thiệu & Tính năng
- Kiến trúc & Công nghệ
- Cấu trúc thư mục
- Thiết lập môi trường & biến cấu hình
- Chạy dự án (Windows/Linux/macOS, Docker)
- Dữ liệu mẫu, tài khoản mẫu (tuỳ chọn)
- API Docs (Swagger) & tài liệu endpoint
- Hướng dẫn Admin/Client/Shipper nhanh
- Quy ước mã nguồn, kiểm tra chất lượng
- Build, đóng gói, triển khai
- Vận hành & Giám sát (Actuator)
- Troubleshooting & FAQ
- Đóng góp & Giấy phép

---

## Giới thiệu
Dự án mô phỏng hệ thống cửa hàng trà sữa AloTrà:
- Quản trị danh mục, sản phẩm, hình ảnh, thuộc tính, khuyến mãi, đơn hàng, người dùng.
- Module Tin tức (News) với bài viết, danh mục, bình luận, thích/không thích.
- Giao diện Thymeleaf cho Admin/Client; trang Shipper quản lý giao hàng.
- REST API phục vụ tích hợp front-end/mobile, có tài liệu tự động với OpenAPI/Swagger.
- Ảnh tĩnh và assets trong `src/main/resources/static`.

## Tính năng nổi bật
- Quản lý sản phẩm: CRUD, soft-delete, thuộc tính/ảnh/like.
- Quản lý danh mục: CRUD, kiểm tra trùng tên (không phân biệt hoa thường).
- Tìm kiếm, phân trang thủ công cho danh sách lớn.
- Tin tức: bài viết, danh mục, like, comment; xuất API công khai.
- Upload ảnh Cloudinary (tùy chọn), tài sản tĩnh phục vụ trực tiếp.
- Giao diện mẫu đẹp, tối ưu cho cửa hàng trà sữa (đã tinh chỉnh). 
- Trang Shipper quản lý đơn giao hàng.
- Tài liệu API đầy đủ với Swagger UI.

## Kiến trúc & Công nghệ
- Java 17, Spring Boot 3
- Spring Web, Spring Data MongoDB, Validation (Jakarta)
- Lombok, DevTools, Actuator
- springdoc-openapi (Swagger UI)
- Cloudinary SDK (tải ảnh) – tùy chọn
- Maven (mvnw/mvnw.cmd đi kèm)

### Sơ đồ lớp nghiệp vụ (khái quát)
- Product, Category, ProductImage, ProductAttribute, ProductLike
- NewsPost, NewsCategory, NewsLike, NewsComment
- Soft-delete qua trường `deletedAt` (không hiển thị trong danh sách active)

## Cấu trúc thư mục chính
- `src/main/java/com/example/spring_boot/controllers/**`: REST Controllers
- `src/main/java/com/example/spring_boot/services/**`: Nghiệp vụ
  - `products/*`: Product/Category/Image/Attribute/Like
  - `news/*`: Post/Category/Like/Comment
- `src/main/java/com/example/spring_boot/repository/**`: Mongo repositories
- `src/main/resources/views/**`: Thymeleaf (admin, clients, auth, shipper, error, test)
- `src/main/resources/static/**`: JS/CSS/images (bao gồm CKEditor)
- `src/main/resources/application.properties`: cấu hình
- `checkstyle.xml`: quy ước định dạng (LineLength=120)
- `Dockerfile`: build image chạy app

## Thiết lập môi trường
Yêu cầu:
- JDK 17+
- MongoDB (local hoặc cloud – MongoDB Atlas)
- Maven (không bắt buộc vì đã có wrapper `mvnw/mvnw.cmd`)
- Cloudinary (nếu cần upload ảnh)

### Biến môi trường/thuộc tính cấu hình
Tối thiểu cần cấu hình MongoDB và cổng dịch vụ.

Ví dụ `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/alotra
server.port=8080

# springdoc (tuỳ chọn)
# springdoc.api-docs.path=/v3/api-docs
# springdoc.swagger-ui.path=/swagger-ui.html
```

Nếu sử dụng Cloudinary, set biến môi trường trước khi chạy:
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

Windows PowerShell:
```powershell
$env:CLOUDINARY_CLOUD_NAME="your_cloud"
$env:CLOUDINARY_API_KEY="123"
$env:CLOUDINARY_API_SECRET="secret"
```

Linux/macOS:
```bash
export CLOUDINARY_CLOUD_NAME=your_cloud
export CLOUDINARY_API_KEY=123
export CLOUDINARY_API_SECRET=secret
```

## Chạy dự án
### Chạy trực tiếp (Windows)
```powershell
./mvnw.cmd spring-boot:run
```

### Chạy trực tiếp (Linux/macOS)
```bash
./mvnw spring-boot:run
```

### Đóng gói JAR và chạy
```bash
./mvnw clean package -DskipTests
java -jar target/spring-boot-0.0.1-SNAPSHOT.jar
```

Mặc định truy cập `http://localhost:8080`.

### Chạy bằng Docker
Build image:
```bash
docker build -t alotra-shop:latest .
```

Chạy container (cần MongoDB đang chạy và biến môi trường nếu dùng Cloudinary):
```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017/alotra" \
  -e CLOUDINARY_CLOUD_NAME="$CLOUDINARY_CLOUD_NAME" \
  -e CLOUDINARY_API_KEY="$CLOUDINARY_API_KEY" \
  -e CLOUDINARY_API_SECRET="$CLOUDINARY_API_SECRET" \
  alotra-shop:latest
```

## Dữ liệu mẫu (gợi ý)
- Import JSON mẫu vào MongoDB bằng `mongoimport` hoặc Compass.
- Có thể tạo nhanh danh mục/sản phẩm trong giao diện Admin (`/admin/...`).

## Tài liệu API
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Tổng hợp endpoint người viết: xem `api-endpoints-summary.md` trong repo.

Ví dụ nhanh (curl):
```bash
# News – tìm bài đã publish có từ khoá
curl "http://localhost:8080/api/news/posts?title=matcha&published=true"

# Tạo bài viết
curl -X POST "http://localhost:8080/api/news/posts" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Hello Milk Tea",
    "slug":"hello-milk-tea",
    "content":"Xin chào AloTrà",
    "thumbnailUrl":"https://...",
    "authorId":"64f0...",
    "categoryId":"64f1...",
    "published":true
  }'
```

## Hướng dẫn nhanh theo vai trò
### Admin
- Đăng nhập (nếu bật auth), truy cập trang quản trị trong `views/admin/**`.
- Quản lý danh mục, sản phẩm, khuyến mãi, đơn hàng, người dùng.

### Khách hàng (Client)
- Trang sản phẩm, chi tiết, giỏ hàng (các trang mẫu trong `views/clients/**`).

### Shipper
- Trang `views/shipper-dashboard.html` để xem/nhận đơn và xác nhận giao.
- Tiêu đề danh sách đã chỉnh: “Danh sách đơn hàng”.

## Quy ước mã nguồn & chất lượng
- Checkstyle: dòng tối đa 120 ký tự (`checkstyle.xml`).
- Đặt tên rõ nghĩa; tránh viết tắt.
- Exception cho REST: sử dụng `ResponseStatusException` với HTTP status phù hợp.
- Logging: ưu tiên Lombok `@Slf4j` hoặc `java.util.logging.Logger` theo module hiện có.

## Build & Triển khai
- Build: `./mvnw clean package -DskipTests`
- Artifact: `target/spring-boot-0.0.1-SNAPSHOT.jar`
- Chạy: `java -jar target/spring-boot-0.0.1-SNAPSHOT.jar`
- Docker: sử dụng `Dockerfile` trong root project (xem phần Docker ở trên).

## Vận hành & Giám sát
- Actuator Health: `http://localhost:8080/actuator/health`
- Bật thêm info/metrics theo nhu cầu trong `application.properties`.

## Troubleshooting
- Không kết nối được MongoDB:
  - Kiểm tra `spring.data.mongodb.uri` chính xác và DB đang chạy.
  - Nếu chạy bằng Docker trên Windows/Mac, dùng `host.docker.internal` để container truy cập host.
- Swagger không hiển thị:
  - Kiểm tra `springdoc.swagger-ui.path` nếu đã tuỳ biến.
  - Kiểm tra reverse proxy/nginx (nếu có) có chặn đường dẫn không.
- Upload ảnh lỗi:
  - Kiểm tra 3 biến Cloudinary đã được set đúng.
  - Kiểm tra quyền mạng outbound của môi trường chạy.

## FAQ
- Có cần cài Maven không? – Không bắt buộc, đã có `mvnw/mvnw.cmd`.
- Có thể dùng cơ sở dữ liệu khác? – Dự án hiện tối ưu cho MongoDB. Muốn đổi sang SQL cần viết lại Repository/Entity.
- Có authentication không? – Repo có trang `views/auth/login.html` và có thể tích hợp Spring Security theo nhu cầu (chưa bật mặc định).

## Đóng góp
- Mở issue mô tả rõ ràng; đề xuất giải pháp ngắn gọn.
- Tạo Pull Request kèm mô tả: phạm vi thay đổi, ảnh/chụp màn hình (nếu UI), bước kiểm thử.

## Giấy phép
Mã nguồn phát hành cho mục đích học tập và thương mại nhỏ lẻ; vui lòng giữ thông tin bản quyền trong file và tôn trọng giấy phép của các thư viện bên thứ ba (Spring, Lombok, Cloudinary SDK, v.v.).

---

Chúc bạn pha “AloTrà” thật ngon và triển khai hệ thống thành công!
