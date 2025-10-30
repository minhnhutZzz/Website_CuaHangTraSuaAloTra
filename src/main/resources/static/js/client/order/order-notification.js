/**
 * Order Notification Manager
 * Quản lý thông báo đơn hàng cho khách hàng
 */
class OrderNotificationManager {
    constructor() {
        this.sessionId = this.getSessionId();
        this.notificationCount = 0;
        this.isModalOpen = false;
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.loadOrderCounts();
        
        // Auto refresh every 30 seconds
        setInterval(() => {
            this.loadOrderCounts();
        }, 30000);
    }
    
    getSessionId() {
        let sessionId = localStorage.getItem('sessionId');
        if (!sessionId) {
            sessionId = 'session_' + Date.now();
            localStorage.setItem('sessionId', sessionId);
        }
        return sessionId;
    }
    
    getUserId() {
        let user = localStorage.getItem('currentUser') || sessionStorage.getItem('currentUser');
        if (user) {
            try {
                let u = JSON.parse(user);
                return u.id || u.userId || null;
            } catch (e) {
                return null;
            }
        }
        return null;
    }
    
    setupEventListeners() {
        // Desktop notification button
        const notificationBtn = document.getElementById('orderNotificationBtn');
        if (notificationBtn) {
            notificationBtn.addEventListener('click', () => this.openOrderModal());
        }
        
        // Mobile notification button
        const notificationBtnMobile = document.getElementById('orderNotificationBtnMobile');
        if (notificationBtnMobile) {
            notificationBtnMobile.addEventListener('click', () => this.openOrderModal());
        }
        
        // Close modal button
        const closeBtn = document.getElementById('closeOrderNotificationModal');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeOrderModal());
        }
        
        // Close modal on backdrop click
        const modal = document.getElementById('orderNotificationModal');
        if (modal) {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeOrderModal();
                }
            });
        }
    }
    
    async loadOrderCounts() {
        const userId = this.getUserId();
        const sessionId = this.getSessionId();
        try {
            const url = `/api/customer/orders/count?userId=${encodeURIComponent(userId || '')}&sessionId=${encodeURIComponent(sessionId)}`;
            const response = await fetch(url);
            const data = await response.json();
            if (data.success) {
                const counts = data.data || { pending: 0, approved: 0, shipping: 0 };
                this.updateNotificationCount(counts);
            } else {
                this.updateNotificationCount({ pending: 0, approved: 0, shipping: 0 });
            }
        } catch (error) {
            console.error('Error loading order counts:', error);
            this.updateNotificationCount({ pending: 0, approved: 0, shipping: 0 });
        }
    }
    
    updateNotificationCount(counts) {
        // Tính số đơn hàng cần thông báo (pending, approved, shipping)
        const notificationCount = counts.pending + counts.approved + counts.shipping;
        
        this.notificationCount = notificationCount;
        
        // Cập nhật UI
        const countElement = document.getElementById('orderNotificationCount');
        const countElementMobile = document.getElementById('orderNotificationCountMobile');
        
        if (countElement) {
            if (notificationCount > 0) {
                countElement.textContent = notificationCount;
                countElement.classList.remove('hidden');
            } else {
                countElement.classList.add('hidden');
            }
        }
        
        if (countElementMobile) {
            if (notificationCount > 0) {
                countElementMobile.textContent = notificationCount;
                countElementMobile.classList.remove('hidden');
            } else {
                countElementMobile.classList.add('hidden');
            }
        }
    }
    
    async openOrderModal() {
        const modal = document.getElementById('orderNotificationModal');
        const content = document.getElementById('orderNotificationModalContent');
        
        if (modal && content) {
            this.isModalOpen = true;
            
            // Show modal
            modal.classList.remove('hidden', 'modal-hidden');
            modal.classList.add('flex');
            
            // Animate content
            setTimeout(() => {
                content.classList.remove('scale-95', 'opacity-0', 'modal-content-hidden');
                content.classList.add('scale-100', 'opacity-100', 'modal-content-visible');
            }, 10);
            
            // Load orders
            await this.loadRecentOrders();
        }
    }
    
    closeOrderModal() {
        const modal = document.getElementById('orderNotificationModal');
        const content = document.getElementById('orderNotificationModalContent');
        
        if (modal && content) {
            this.isModalOpen = false;
            
            // Animate content
            content.classList.remove('scale-100', 'opacity-100', 'modal-content-visible');
            content.classList.add('scale-95', 'opacity-0', 'modal-content-hidden');
            
            // Hide modal
            setTimeout(() => {
                modal.classList.add('hidden', 'modal-hidden');
                modal.classList.remove('flex');
            }, 150);
        }
    }
    
    async loadRecentOrders() {
        const orderList = document.getElementById('orderNotificationList');
        const userId = this.getUserId();
        if (!orderList) return;
        // Nếu chưa đăng nhập thì hiện nội dung yêu cầu đăng nhập
        if (!userId) {
            orderList.innerHTML = `<div class="text-center py-8 text-gray-500">Bạn cần đăng nhập để xem đơn hàng.</div>`;
            return;
        }
        try {
            orderList.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <i class="fas fa-spinner fa-spin text-2xl mb-2"></i>
                    <p>Đang tải đơn hàng...</p>
                </div>
            `;
            // Gửi kèm cả sessionId để server có thể fallback nếu đơn chưa gán userId
            const sessionId = this.sessionId;
            let url = `/api/customer/orders/recent?limit=10&userId=${userId}&sessionId=${encodeURIComponent(sessionId)}`;
            const response = await fetch(url);
            const data = await response.json();
            if (data.success) {
                const orders = data.data;
                this.renderOrders(orders, orderList);
            } else {
                this.renderError(orderList, data.message || 'Lỗi tải đơn hàng');
            }
        } catch (error) {
            console.error('Error loading recent orders:', error);
            this.renderError(orderList, 'Lỗi kết nối');
        }
    }
    
    renderOrders(orders, container) {
        if (orders.length === 0) {
            container.innerHTML = `
                <div class="text-center py-8 text-gray-500">
                    <i class="fas fa-clipboard-list text-4xl mb-4"></i>
                    <p class="text-lg font-medium">Chưa có đơn hàng nào</p>
                    <p class="text-sm">Hãy đặt hàng để xem thông báo ở đây!</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = orders.map(order => this.renderOrderItem(order)).join('');
    }
    
    renderOrderItem(order) {
        const statusInfo = this.getStatusInfo(order.status);
        const formattedDate = new Date(order.createdAt).toLocaleDateString('vi-VN');
        const formattedTime = new Date(order.createdAt).toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
        });
        
        return `
            <div class="border border-gray-200 rounded-lg p-4 mb-3 hover:bg-gray-50 transition-colors">
                <div class="flex items-start justify-between">
                    <div class="flex-1">
                        <div class="flex items-center mb-2">
                            <span class="font-semibold text-gray-900">#${order.orderNumber}</span>
                            <span class="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusInfo.class}">
                                ${statusInfo.text}
                            </span>
                        </div>
                        
                        <div class="text-sm text-gray-600 mb-1">
                            <i class="fas fa-user mr-1"></i>
                            ${order.username}
                        </div>
                        
                        <div class="text-sm text-gray-600 mb-1">
                            <i class="fas fa-phone mr-1"></i>
                            ${order.phone}
                        </div>
                        
                        <div class="text-sm text-gray-600 mb-2">
                            <i class="fas fa-money-bill-wave mr-1"></i>
                            ${this.formatPrice(order.totalAmount)}₫
                        </div>
                        
                        <div class="text-xs text-gray-500">
                            <i class="fas fa-clock mr-1"></i>
                            ${formattedDate} lúc ${formattedTime}
                        </div>
                    </div>
                    
                    <div class="ml-4 text-right">
                        <div class="text-lg font-semibold text-gray-900">
                            ${this.formatPrice(order.totalAmount)}₫
                        </div>
                        <div class="text-xs text-gray-500 mt-1">
                            ${order.paymentMethod === 'COD' ? 'COD' : 'VNPay'}
                        </div>
                    </div>
                </div>
                
                ${this.getStatusMessage(order.status)}
            </div>
        `;
    }
    
    getStatusInfo(status) {
        switch (status) {
            case 'PENDING':
                return { text: 'Chưa duyệt', class: 'bg-yellow-100 text-yellow-800' };
            case 'APPROVED':
                return { text: 'Đã duyệt', class: 'bg-blue-100 text-blue-800' };
            case 'SHIPPING':
                return { text: 'Đang giao hàng', class: 'bg-orange-100 text-orange-800' };
            case 'DELIVERED':
                return { text: 'Đã giao hàng', class: 'bg-green-100 text-green-800' };
            case 'CANCELLED':
                return { text: 'Đã hủy', class: 'bg-red-100 text-red-800' };
            default:
                return { text: status, class: 'bg-gray-100 text-gray-800' };
        }
    }
    
    getStatusMessage(status) {
        switch (status) {
            case 'PENDING':
                return `
                    <div class="mt-2 p-2 bg-yellow-50 rounded text-sm text-yellow-700">
                        <i class="fas fa-info-circle mr-1"></i>
                        Đơn hàng đang chờ admin duyệt. Chúng tôi sẽ xử lý trong thời gian sớm nhất.
                    </div>
                `;
            case 'APPROVED':
                return `
                    <div class="mt-2 p-2 bg-blue-50 rounded text-sm text-blue-700">
                        <i class="fas fa-check-circle mr-1"></i>
                        Đơn hàng đã được duyệt. Shipper sẽ liên hệ với bạn sớm.
                    </div>
                `;
            case 'SHIPPING':
                return `
                    <div class="mt-2 p-2 bg-orange-50 rounded text-sm text-orange-700">
                        <i class="fas fa-truck mr-1"></i>
                        Shipper đang giao hàng. Vui lòng giữ điện thoại để nhận cuộc gọi.
                    </div>
                `;
            case 'DELIVERED':
                return `
                    <div class="mt-2 p-2 bg-green-50 rounded text-sm text-green-700">
                        <i class="fas fa-check-circle mr-1"></i>
                        Đơn hàng đã được giao thành công! Cảm ơn bạn đã mua hàng.
                    </div>
                `;
            case 'CANCELLED':
                return `
                    <div class="mt-2 p-2 bg-red-50 rounded text-sm text-red-700">
                        <i class="fas fa-times-circle mr-1"></i>
                        Đơn hàng đã bị hủy. Vui lòng liên hệ để biết thêm chi tiết.
                    </div>
                `;
            default:
                return '';
        }
    }
    
    renderError(container, message) {
        container.innerHTML = `
            <div class="text-center py-8 text-red-500">
                <i class="fas fa-exclamation-triangle text-4xl mb-4"></i>
                <p class="text-lg font-medium">Lỗi tải đơn hàng</p>
                <p class="text-sm">${message}</p>
                <button onclick="orderNotificationManager.loadRecentOrders()" 
                        class="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors">
                    <i class="fas fa-redo mr-1"></i>
                    Thử lại
                </button>
            </div>
        `;
    }
    
    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price);
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.orderNotificationManager = new OrderNotificationManager();
});

// Global functions for modal
window.closeOrderNotificationModal = function() {
    if (window.orderNotificationManager) {
        window.orderNotificationManager.closeOrderModal();
    }
};
