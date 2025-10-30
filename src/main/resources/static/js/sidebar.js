(function () {
    // Check if sidebar is already initialized
    if (window.sidebarInitialized) {
        console.log('Sidebar already initialized, skipping...');
        return;
    }

    document.addEventListener('DOMContentLoaded', function () {
        const nav = document.getElementById('adminSidebarNav');
        if (!nav) {
            console.log('Sidebar nav element not found');
            return;
        }

        // Check if menu is already rendered
        if (nav.children.length > 0) {
            console.log('Sidebar already rendered, skipping...');
            window.sidebarInitialized = true;
            return;
        }

        // Menu data - All items as direct links, no groups
        const menuData = [
            { type: 'link', href: '/auth/dashboard', icon: 'fas fa-tachometer-alt text-blue-600', text: 'Dashboard' },

            // Quản lý bán hàng
            { type: 'link', href: '/admin/categories', icon: 'fas fa-tags text-green-600', text: 'Danh mục sản phẩm' },
            { type: 'link', href: '/admin/products', icon: 'fas fa-box text-green-600', text: 'Danh sách sản phẩm' },
            { type: 'link', href: '/admin/products/create', icon: 'fas fa-plus text-green-600', text: 'Thêm sản phẩm' },
            { type: 'link', href: '/admin/promotions/create', icon: 'fas fa-plus-circle text-orange-600', text: 'Thêm khuyến mãi', extra: 'hover:bg-orange-50' },
            { type: 'link', href: '/admin/promotion-apply', icon: 'fas fa-tags text-orange-600', text: 'Áp dụng khuyến mãi', extra: 'hover:bg-orange-50' },

            // Quản lý đơn hàng
            { type: 'link', href: '/admin/orders', icon: 'fas fa-shopping-cart text-indigo-600', text: 'Quản lý đơn hàng' },

            // Quản lý tài khoản
            { type: 'link', href: '/admin/users', icon: 'fas fa-users text-purple-500', text: 'Danh sách người dùng' },


            // Đăng xuất
            { type: 'link', href: '/auth/logout', icon: 'fas fa-sign-out-alt text-red-500', text: 'Đăng xuất', extra: 'hover:bg-red-50 text-red-500' }
        ];

        // Render functions
        function createLink(item) {
            const a = document.createElement('a');
            a.href = item.href || '#';
            a.className = `flex items-center space-x-3 p-3 rounded-lg  ${item.extra || ''}`.trim();
            a.innerHTML = `<i class="${item.icon} menu-icon"></i><span class="menu-text">${item.text}</span>`;
            return a;
        }


        // Render menu only once
        console.log('Rendering sidebar menu...');

        // Set flag before rendering to prevent double initialization
        window.sidebarInitialized = true;

        // Render all items as direct links
        menuData.forEach(item => {
            nav.appendChild(createLink(item));
        });

        // Active link highlighting
        const path = window.location.pathname;
        const links = Array.from(nav.querySelectorAll('a[href]'));
        links.forEach(a => a.classList.remove('bg-green-500', 'text-white'));

        function scoreMatch(href) {
            if (!href || href === '#') return -1;
            if (href === path) return 100000 + href.length;
            const prefix = href.endsWith('/') ? href : href + '/';
            return path.startsWith(prefix) ? href.length : -1;
        }

        let best = null, bestScore = -1;
        links.forEach(a => {
            const s = scoreMatch(a.getAttribute('href'));
            if (s > bestScore) { bestScore = s; best = a; }
        });

        if (best) {
            best.classList.add('bg-green-500', 'text-white');
        }

        console.log('Sidebar initialized successfully');
    });
})();