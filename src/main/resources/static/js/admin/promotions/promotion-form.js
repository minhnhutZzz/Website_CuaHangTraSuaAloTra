/**
 * JavaScript cho trang quản lý khuyến mãi
 * 
 * @author Spring Boot Team
 * @version 1.0
 */

// Form validation và xử lý
class PromotionFormHandler {
    constructor() {
        this.form = document.getElementById('promotionForm');
        this.previewCard = document.getElementById('previewCard');
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupDateValidation();
        this.setupFormValidation();
    }

    /**
     * Thiết lập event listeners
     */
    setupEventListeners() {
        // Auto preview on input change
        const inputs = ['name', 'description', 'discountPercent', 'startDate', 'endDate', 'isActive'];
        inputs.forEach(inputId => {
            const input = document.getElementById(inputId);
            if (input) {
                input.addEventListener('input', () => this.updatePreview());
                input.addEventListener('change', () => this.updatePreview());
            }
        });

        // Form submission
        if (this.form) {
            this.form.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }
    }

    /**
     * Thiết lập validation cho ngày tháng
     */
    setupDateValidation() {
        const now = new Date();
        const today = now.toISOString().slice(0, 16);
        
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        
        if (startDateInput) {
            startDateInput.min = today;
        }
        
        if (endDateInput) {
            endDateInput.min = today;
        }
        
        // Update end date minimum when start date changes
        if (startDateInput && endDateInput) {
            startDateInput.addEventListener('change', () => {
                endDateInput.min = startDateInput.value;
                this.validateDateRange();
            });
        }
    }

    /**
     * Thiết lập form validation
     */
    setupFormValidation() {
        // Custom validation for discount percent
        const discountInput = document.getElementById('discountPercent');
        if (discountInput) {
            discountInput.addEventListener('input', () => {
                const value = parseInt(discountInput.value);
                if (value < 1 || value > 100) {
                    discountInput.setCustomValidity('Phần trăm giảm giá phải từ 1-100');
                } else {
                    discountInput.setCustomValidity('');
                }
            });
        }

        // Custom validation for date range
        const endDateInput = document.getElementById('endDate');
        if (endDateInput) {
            endDateInput.addEventListener('change', () => {
                this.validateDateRange();
            });
        }
    }

    /**
     * Validate date range
     */
    validateDateRange() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const endDateInput = document.getElementById('endDate');
        
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            
            if (end <= start) {
                endDateInput.setCustomValidity('Ngày kết thúc phải sau ngày bắt đầu');
            } else {
                endDateInput.setCustomValidity('');
            }
        }
    }

    /**
     * Cập nhật preview
     */
    updatePreview() {
        const name = document.getElementById('name').value;
        const description = document.getElementById('description').value;
        const discountPercent = document.getElementById('discountPercent').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const isActive = document.getElementById('isActive').checked;
        
        if (name && discountPercent) {
            this.previewCard.innerHTML = `
                <div class="text-center">
                    <div class="mb-3">
                        <span class="badge bg-danger fs-6">-${discountPercent}%</span>
                    </div>
                    <h5 class="mb-2">${name}</h5>
                    ${description ? `<p class="text-muted small mb-2">${description}</p>` : ''}
                    <div class="small text-muted">
                        <div><i class="fas fa-calendar-alt me-1"></i> Từ: ${this.formatDateTime(startDate)}</div>
                        <div><i class="fas fa-calendar-alt me-1"></i> Đến: ${this.formatDateTime(endDate)}</div>
                    </div>
                    <div class="mt-2">
                        <span class="badge ${isActive ? 'bg-success' : 'bg-secondary'}">
                            ${isActive ? 'Đang hoạt động' : 'Tạm dừng'}
                        </span>
                    </div>
                </div>
            `;
            this.previewCard.classList.add('has-content');
        } else {
            this.previewCard.innerHTML = `
                <i class="fas fa-percentage fa-3x text-muted mb-3"></i>
                <h6 class="text-muted">Nhập tên và phần trăm giảm giá để xem trước</h6>
            `;
            this.previewCard.classList.remove('has-content');
        }
    }

    /**
     * Format datetime for display
     */
    formatDateTime(dateTimeString) {
        if (!dateTimeString) return '';
        const date = new Date(dateTimeString);
        return date.toLocaleString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * Xử lý form submission
     */
    handleFormSubmit(event) {
        if (!this.form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
            this.form.classList.add('was-validated');
            return;
        }

        // Show loading state
        const submitBtn = this.form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang xử lý...';
            submitBtn.disabled = true;
        }
    }
}

// Promotion list handler
class PromotionListHandler {
    constructor() {
        this.init();
    }

    init() {
        this.setupDeleteConfirmation();
        this.setupSearch();
        this.setupAutoHideAlerts();
    }

    /**
     * Thiết lập xác nhận xóa
     */
    setupDeleteConfirmation() {
        // Delete confirmation modal is handled in HTML
    }

    /**
     * Thiết lập tìm kiếm
     */
    setupSearch() {
        const searchInput = document.querySelector('input[name="search"]');
        if (searchInput) {
            // Auto search after 500ms delay
            let searchTimeout;
            searchInput.addEventListener('input', () => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    searchInput.form.submit();
                }, 500);
            });
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
}

// Utility functions
class PromotionUtils {
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
    // Initialize form handler if on form page
    if (document.getElementById('promotionForm')) {
        new PromotionFormHandler();
    }
    
    // Initialize list handler if on list page
    if (document.querySelector('.promotion-card')) {
        new PromotionListHandler();
    }
});

// Global functions for HTML onclick events
function deletePromotion(id, name) {
    document.getElementById('promotionName').textContent = name;
    document.getElementById('deleteForm').action = '/admin/promotions/delete/' + id;
    
    const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
    modal.show();
}

function previewPromotion() {
    if (window.promotionFormHandler) {
        window.promotionFormHandler.updatePreview();
    }
}

// Export for use in other scripts
window.PromotionUtils = PromotionUtils;
window.PromotionFormHandler = PromotionFormHandler;
window.PromotionListHandler = PromotionListHandler;
