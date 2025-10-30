    // Products page JavaScript - Main entry point
let productManager;

// Load products khi trang được tải
document.addEventListener('DOMContentLoaded', function() {
    console.log('Products page DOM loaded, initializing...');
    
    // Kiểm tra xem các class cần thiết đã được load chưa
    if (typeof ProductManager === 'undefined') {
        console.error('ProductManager class not found!');
        return;
    }
    
    if (typeof ProductApi === 'undefined') {
        console.error('ProductApi class not found!');
        return;
    }
    
    if (typeof ProductUI === 'undefined') {
        console.error('ProductUI class not found!');
        return;
    }
    
    if (typeof ProductUtils === 'undefined') {
        console.error('ProductUtils class not found!');
        return;
    }
    
    try {
        // Khởi tạo Product Manager
        productManager = new ProductManager();
        productManager.init();
        
        // Expose productManager ra global để các modules khác có thể sử dụng
        window.productManager = productManager;
        
        // Xử lý tìm kiếm từ URL parameter
        handleSearchFromUrl();
        
        console.log('ProductManager initialized:', productManager);
        console.log('Global addToCart function:', typeof window.addToCart);
    } catch (error) {
        console.error('Error initializing ProductManager:', error);
    }
});

/**
 * Xử lý tìm kiếm từ URL parameter
 */
function handleSearchFromUrl() {
    // Lấy search term từ URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const searchTerm = urlParams.get('search');
    
    if (searchTerm && searchTerm.trim()) {
        console.log('Search term from URL:', searchTerm);
        
        // Thực hiện tìm kiếm ngay lập tức nếu ProductManager đã sẵn sàng
        if (productManager && typeof productManager.searchProducts === 'function') {
            console.log('ProductManager ready, searching immediately');
            productManager.searchProducts(searchTerm.trim());
        } else {
            // Nếu chưa sẵn sàng, đợi tối đa 2 giây
            let attempts = 0;
            const maxAttempts = 20; // 20 lần x 100ms = 2 giây
            
            const checkAndSearch = () => {
                attempts++;
                if (productManager && typeof productManager.searchProducts === 'function') {
                    console.log(`ProductManager ready after ${attempts * 100}ms, searching...`);
                    productManager.searchProducts(searchTerm.trim());
                } else if (attempts < maxAttempts) {
                    setTimeout(checkAndSearch, 100);
                } else {
                    console.error('ProductManager not available after 2 seconds');
                }
            };
            
            setTimeout(checkAndSearch, 100);
        }
    }
}

// Legacy functions - kept for backward compatibility
// These will be handled by ProductManager now

// Global functions for HTML onclick events
// These are set up by ProductManager.setupGlobalFunctions()

// Note: All functionality has been moved to modules:
// - productApi.js: API calls
// - productUI.js: UI rendering and pagination
// - productGallery.js: Image gallery functionality
// - productUtils.js: Utility functions
// - productManager.js: Main controller