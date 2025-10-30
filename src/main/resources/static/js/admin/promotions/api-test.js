/**
 * API Test Script cho Module Promotion
 * 
 * Sử dụng để test các API endpoints của module khuyến mãi
 * 
 * @author Spring Boot Team
 * @version 1.0
 */

class PromotionAPITester {
    constructor(baseURL = 'http://localhost:8080') {
        this.baseURL = baseURL;
        this.apiBase = `${baseURL}/api/promotions`;
    }

    /**
     * Test tạo khuyến mãi mới
     */
    async testCreatePromotion() {
        const promotionData = {
            name: "Test Khuyến mãi API",
            description: "Khuyến mãi test từ API",
            discountPercent: 20,
            startDate: "2024-01-01T00:00:00",
            endDate: "2024-12-31T23:59:59",
            isActive: true
        };

        try {
            const response = await fetch(this.apiBase, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(promotionData)
            });

            const result = await response.json();
            console.log('✅ Tạo khuyến mãi thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi tạo khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Test lấy danh sách khuyến mãi
     */
    async testGetAllPromotions() {
        try {
            const response = await fetch(this.apiBase);
            const result = await response.json();
            console.log('✅ Lấy danh sách khuyến mãi thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi lấy danh sách khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Test lấy khuyến mãi theo ID
     */
    async testGetPromotionById(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            const result = await response.json();
            console.log('✅ Lấy khuyến mãi theo ID thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi lấy khuyến mãi theo ID:', error);
            throw error;
        }
    }

    /**
     * Test cập nhật khuyến mãi
     */
    async testUpdatePromotion(id, updateData) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updateData)
            });

            const result = await response.json();
            console.log('✅ Cập nhật khuyến mãi thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi cập nhật khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Test tìm kiếm khuyến mãi
     */
    async testSearchPromotions(searchTerm) {
        try {
            const response = await fetch(`${this.apiBase}/search?name=${encodeURIComponent(searchTerm)}`);
            const result = await response.json();
            console.log('✅ Tìm kiếm khuyến mãi thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi tìm kiếm khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Test lấy khuyến mãi đang hoạt động
     */
    async testGetActivePromotions() {
        try {
            const response = await fetch(`${this.apiBase}/active`);
            const result = await response.json();
            console.log('✅ Lấy khuyến mãi đang hoạt động thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi lấy khuyến mãi đang hoạt động:', error);
            throw error;
        }
    }

    /**
     * Test phân trang
     */
    async testGetPromotionsPaged(page = 0, size = 10) {
        try {
            const response = await fetch(`${this.apiBase}/paged?page=${page}&size=${size}`);
            const result = await response.json();
            console.log('✅ Lấy khuyến mãi có phân trang thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi lấy khuyến mãi có phân trang:', error);
            throw error;
        }
    }

    /**
     * Test đếm khuyến mãi
     */
    async testCountPromotions(isActive = null) {
        try {
            const url = isActive !== null ? 
                `${this.apiBase}/count?isActive=${isActive}` : 
                `${this.apiBase}/count`;
            const response = await fetch(url);
            const result = await response.json();
            console.log('✅ Đếm khuyến mãi thành công:', result);
            return result.data;
        } catch (error) {
            console.error('❌ Lỗi đếm khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Test xóa khuyến mãi
     */
    async testDeletePromotion(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`, {
                method: 'DELETE'
            });

            const result = await response.json();
            console.log('✅ Xóa khuyến mãi thành công:', result);
            return result;
        } catch (error) {
            console.error('❌ Lỗi xóa khuyến mãi:', error);
            throw error;
        }
    }

    /**
     * Chạy tất cả tests
     */
    async runAllTests() {
        console.log('🚀 Bắt đầu test API Promotion...\n');

        try {
            // Test 1: Tạo khuyến mãi
            console.log('📝 Test 1: Tạo khuyến mãi mới');
            const createdPromotion = await this.testCreatePromotion();
            const promotionId = createdPromotion.id;
            console.log('');

            // Test 2: Lấy danh sách
            console.log('📋 Test 2: Lấy danh sách khuyến mãi');
            await this.testGetAllPromotions();
            console.log('');

            // Test 3: Lấy theo ID
            console.log('🔍 Test 3: Lấy khuyến mãi theo ID');
            await this.testGetPromotionById(promotionId);
            console.log('');

            // Test 4: Tìm kiếm
            console.log('🔎 Test 4: Tìm kiếm khuyến mãi');
            await this.testSearchPromotions('Test');
            console.log('');

            // Test 5: Lấy active
            console.log('✅ Test 5: Lấy khuyến mãi đang hoạt động');
            await this.testGetActivePromotions();
            console.log('');

            // Test 6: Phân trang
            console.log('📄 Test 6: Lấy khuyến mãi có phân trang');
            await this.testGetPromotionsPaged(0, 5);
            console.log('');

            // Test 7: Đếm
            console.log('🔢 Test 7: Đếm khuyến mãi');
            await this.testCountPromotions();
            await this.testCountPromotions(true);
            console.log('');

            // Test 8: Cập nhật
            console.log('✏️ Test 8: Cập nhật khuyến mãi');
            const updateData = {
                name: "Test Khuyến mãi API - Updated",
                description: "Khuyến mãi test đã được cập nhật",
                discountPercent: 25,
                startDate: "2024-01-01T00:00:00",
                endDate: "2024-12-31T23:59:59",
                isActive: true
            };
            await this.testUpdatePromotion(promotionId, updateData);
            console.log('');

            // Test 9: Xóa
            console.log('🗑️ Test 9: Xóa khuyến mãi');
            await this.testDeletePromotion(promotionId);
            console.log('');

            console.log('🎉 Tất cả tests đã hoàn thành thành công!');

        } catch (error) {
            console.error('💥 Test thất bại:', error);
        }
    }

    /**
     * Test một endpoint cụ thể
     */
    async testEndpoint(endpoint, method = 'GET', data = null) {
        const url = `${this.apiBase}${endpoint}`;
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            const result = await response.json();
            console.log(`✅ ${method} ${endpoint} thành công:`, result);
            return result;
        } catch (error) {
            console.error(`❌ Lỗi ${method} ${endpoint}:`, error);
            throw error;
        }
    }
}

// Sử dụng
const tester = new PromotionAPITester();

// Chạy tất cả tests
// tester.runAllTests();

// Test một endpoint cụ thể
// tester.testEndpoint('/search?name=test', 'GET');

// Export cho sử dụng trong browser console
window.PromotionAPITester = PromotionAPITester;
window.promotionTester = tester;

console.log('📚 Promotion API Tester đã sẵn sàng!');
console.log('Sử dụng:');
console.log('- promotionTester.runAllTests() - Chạy tất cả tests');
console.log('- promotionTester.testCreatePromotion() - Test tạo khuyến mãi');
console.log('- promotionTester.testGetAllPromotions() - Test lấy danh sách');
console.log('- promotionTester.testEndpoint("/search?name=test") - Test endpoint cụ thể');
