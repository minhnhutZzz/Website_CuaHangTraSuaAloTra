# Stage 1: Build với Maven + JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy toàn bộ code
COPY . .

# Build dự án, skip tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Chạy với JRE 17 (nhẹ hơn)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR từ stage build (thay your-app-name nếu biết chính xác)
COPY --from=build /app/target/*.jar app.jar

# Mở port (Render tự map)
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "app.jar"]