/**
 * JavaScript cho trang áp dụng khuyến mãi
 * 
 * @author Spring Boot Team
 * @version 1.0
 */

class PromotionApplyHandler {
    constructor() {
        this.selectedPromotionId = null;
        this.selectedProductIds = new Set();
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupSearch();
        this.setupAutoHideAlerts();
    }

    /**
     * Thiết lập event listeners
     */
    setupEventListeners() {
        // Product checkbox change
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('product-checkbox')) {
                this.handleProductSelection(e.target);
            }
        });

        // Apply promotion form
        const applyForm = document.getElementById('applyPromotionForm');
        if (applyForm) {
            applyForm.addEventListener('submit', (e) => this.handleApplyPromotion(e));
        }
    }

    /**
     * Thiết lập tìm kiếm
     */
    setupSearch() {
        const productSearch = document.getElementById('productSearch');
        if (productSearch) {
            productSearch.addEventListener('input', (e) => this.handleProductSearch(e));
        }

        const promotionSearch = document.getElementById('promotionSearch');
        if (promotionSearch) {
            promotionSearch.addEventListener('input', (e) => this.handlePromotionSearch(e));
        }
    }

    /**
     * Thiết lập auto-hide alerts
     */
    setupAutoHideAlerts() {
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    }

    /**
     * Xử lý chọn sản phẩm
     */
    handleProductSelection(checkbox) {
        if (checkbox.checked) {
            this.selectedProductIds.add(checkbox.value);
        } else {
            this.selectedProductIds.delete(checkbox.value);
        }
        this.updateSelectedCount();
    }

    /**
     * Cập nhật số lượng sản phẩm đã chọn
     */
    updateSelectedCount() {
        const selectedCount = document.querySelectorAll('.product-checkbox:checked').length;
        const applyBtn = document.querySelector('#applyPromotionForm button[type="submit"]');
        
        if (applyBtn) {
            applyBtn.innerHTML = `<i class="fas fa-tags me-2"></i>Áp dụng khuyến mãi (${selectedCount})`;
            applyBtn.disabled = selectedCount === 0;
        }
    }

    /**
     * Xử lý tìm kiếm sản phẩm
     */
    handleProductSearch(e) {
        const searchTerm = e.target.value.toLowerCase();
        const productItems = document.querySelectorAll('.product-item');
        
        productItems.forEach(item => {
            const productName = item.querySelector('.card-title')?.textContent.toLowerCase() || '';
            const productDesc = item.querySelector('.card-text')?.textContent.toLowerCase() || '';
            
            if (productName.includes(searchTerm) || productDesc.includes(searchTerm)) {
                item.style.display = 'block';
            } else {
                item.style.display = 'none';
            }
        });
    }

    /**
     * Xử lý tìm kiếm khuyến mãi
     */
    handlePromotionSearch(e) {
        const searchTerm = e.target.value.toLowerCase();
        const promotionItems = document.querySelectorAll('.promotion-item');
        
        promotionItems.forEach(item => {
            const promotionName = item.querySelector('.card-title')?.textContent.toLowerCase() || '';
            const promotionDesc = item.querySelector('.card-text')?.textContent.toLowerCase() || '';
            
            if (promotionName.includes(searchTerm) || promotionDesc.includes(searchTerm)) {
                item.style.display = 'block';
            } else {
                item.style.display = 'none';
            }
        });
    }

    /**
     * Xử lý áp dụng khuyến mãi
     */
    handleApplyPromotion(e) {
        e.preventDefault();
        
        const checkedBoxes = document.querySelectorAll('.product-checkbox:checked');
        const productIds = Array.from(checkedBoxes).map(cb => cb.value);
        
        if (productIds.length === 0) {
            this.showErrorNotification('Vui lòng chọn ít nhất một sản phẩm!');
            return;
        }
        
        // Show loading state
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang áp dụng...';
        submitBtn.disabled = true;
        
        // Set product IDs
        document.getElementById('selectedProductIds').value = productIds.join(',');
        
        // Submit form
        e.target.submit();
    }

    /**
     * Chọn tất cả sản phẩm
     */
    selectAllProducts() {
        document.querySelectorAll('.product-checkbox').forEach(checkbox => {
            checkbox.checked = true;
            this.selectedProductIds.add(checkbox.value);
        });
        this.updateSelectedCount();
    }

    /**
     * Bỏ chọn tất cả sản phẩm
     */
    clearSelection() {
        document.querySelectorAll('.product-checkbox').forEach(checkbox => {
            checkbox.checked = false;
        });
        this.selectedProductIds.clear();
        this.updateSelectedCount();
    }

    /**
     * Chọn khuyến mãi
     */
    selectPromotion(promotionId) {
        this.selectedPromotionId = promotionId;
        
        // Update UI
        document.querySelectorAll('.promotion-item').forEach(item => {
            item.classList.remove('selected');
        });
        document.querySelector(`[data-promotion-id="${promotionId}"]`)?.classList.add('selected');
        
        // Reload page with selected promotion
        window.location.href = `/admin/promotion-apply?promotionId=${promotionId}`;
    }

    /**
     * Hủy áp dụng khuyến mãi cho sản phẩm
     */
    removeProduct(productId) {
        if (confirm('Bạn có chắc chắn muốn hủy áp dụng khuyến mãi cho sản phẩm này?')) {
            this.removePromotionFromProduct(productId);
        }
    }

    /**
     * Gửi request hủy áp dụng khuyến mãi
     */
    removePromotionFromProduct(productId) {
        const form = document.createElement('form');
        form.method = 'post';
        form.action = '/admin/promotion-apply/remove';
        
        const promotionIdInput = document.createElement('input');
        promotionIdInput.type = 'hidden';
        promotionIdInput.name = 'promotionId';
        promotionIdInput.value = this.selectedPromotionId;
        
        const productIdsInput = document.createElement('input');
        productIdsInput.type = 'hidden';
        productIdsInput.name = 'productIds';
        productIdsInput.value = productId;
        
        form.appendChild(promotionIdInput);
        form.appendChild(productIdsInput);
        document.body.appendChild(form);
        form.submit();
    }

    /**
     * Hiển thị thông báo thành công
     */
    showSuccessNotification(message) {
        this.showNotification(message, 'success');
    }

    /**
     * Hiển thị thông báo lỗi
     */
    showErrorNotification(message) {
        this.showNotification(message, 'danger');
    }

    /**
     * Hiển thị thông báo
     */
    showNotification(message, type) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed notification-toast`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
            <strong>${type === 'success' ? 'Thành công!' : 'Lỗi!'}</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alertDiv);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.style.animation = 'slideOutToRight 0.3s ease-in';
                setTimeout(() => {
                    if (alertDiv.parentNode) {
                        alertDiv.remove();
                    }
                }, 300);
            }
        }, 5000);
    }
}

// Utility functions
class PromotionApplyUtils {
    /**
     * Format currency
     */
    static formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    /**
     * Format date
     */
    static formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    }

    /**
     * Format datetime
     */
    static formatDateTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('vi-VN');
    }

    /**
     * Check if promotion is active
     */
    static isPromotionActive(startDate, endDate, isActive) {
        if (!isActive) return false;
        
        const now = new Date();
        const start = new Date(startDate);
        const end = new Date(endDate);
        
        return now >= start && now <= end;
    }

    /**
     * Get time remaining
     */
    static getTimeRemaining(endDate) {
        const now = new Date();
        const end = new Date(endDate);
        const diff = end - now;
        
        if (diff <= 0) return 'Đã kết thúc';
        
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        
        if (days > 0) return `${days} ngày ${hours} giờ`;
        if (hours > 0) return `${hours} giờ ${minutes} phút`;
        return `${minutes} phút`;
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.promotionApplyHandler = new PromotionApplyHandler();
});

// Global functions for HTML onclick events
function selectPromotion(promotionId) {
    if (window.promotionApplyHandler) {
        window.promotionApplyHandler.selectPromotion(promotionId);
    }
}

function selectAllProducts() {
    if (window.promotionApplyHandler) {
        window.promotionApplyHandler.selectAllProducts();
    }
}

function clearSelection() {
    if (window.promotionApplyHandler) {
        window.promotionApplyHandler.clearSelection();
    }
}

function applyPromotion() {
    if (window.promotionApplyHandler) {
        const form = document.getElementById('applyPromotionForm');
        if (form) {
            form.dispatchEvent(new Event('submit'));
        }
    }
}

function removeProduct(productId) {
    if (window.promotionApplyHandler) {
        window.promotionApplyHandler.removeProduct(productId);
    }
}

// Export for use in other scripts
window.PromotionApplyHandler = PromotionApplyHandler;
window.PromotionApplyUtils = PromotionApplyUtils;
