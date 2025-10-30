// Product API Module
class ProductApi {
    constructor() {
        this.baseUrl = '/api/products';
        this.pageSize = 12;
    }

    /**
     * Lấy danh sách sản phẩm theo trang
     * @param {number} page - Số trang
     * @param {number} size - Kích thước trang
     * @returns {Promise<Object>} - Dữ liệu sản phẩm
     */
    async getProducts(page = 1, size = this.pageSize) {
        try {
            // Convert 1-based page to 0-based for Spring Boot
            const springPage = Math.max(0, page - 1);
            console.log(`Fetching products: ${this.baseUrl}/paged?page=${springPage}&size=${size}`); // Debug log
            const response = await fetch(`${this.baseUrl}/paged?page=${springPage}&size=${size}`);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            console.log('Raw API response:', data); // Debug log
            
            if (!data.success) {
                throw new Error(data.message || 'Failed to fetch products');
            }
            
            return data.data;
        } catch (error) {
            console.error('Error fetching products:', error);
            throw error;
        }
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     * @param {string} name - Tên sản phẩm
     * @returns {Promise<Array>} - Danh sách sản phẩm
     */
    async searchProducts(name) {
        try {
            const response = await fetch(`${this.baseUrl}/search?name=${encodeURIComponent(name)}`);
            const data = await response.json();
            
            if (!data.success) {
                throw new Error(data.message || 'Failed to search products');
            }
            
            return data.data;
        } catch (error) {
            console.error('Error searching products:', error);
            throw error;
        }
    }

    /**
     * Lấy sản phẩm theo danh mục
     * @param {string} categoryId - ID danh mục
     * @returns {Promise<Array>} - Danh sách sản phẩm
     */
    async getProductsByCategory(categoryId) {
        try {
            const response = await fetch(`${this.baseUrl}/category/${categoryId}`);
            const data = await response.json();
            
            if (!data.success) {
                throw new Error(data.message || 'Failed to fetch products by category');
            }
            
            return data.data;
        } catch (error) {
            console.error('Error fetching products by category:', error);
            throw error;
        }
    }

    /**
     * Lấy chi tiết sản phẩm
     * @param {string} productId - ID sản phẩm
     * @returns {Promise<Object>} - Chi tiết sản phẩm
     */
    async getProductDetail(productId) {
        try {
            const response = await fetch(`${this.baseUrl}/detail/${productId}`);
            const data = await response.json();
            
            if (!data.success) {
                throw new Error(data.message || 'Product not found');
            }
            
            return data.data;
        } catch (error) {
            console.error('Error fetching product detail:', error);
            throw error;
        }
    }
}

// Export cho ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ProductApi;
} else {
    window.ProductApi = ProductApi;
}
