// Header Menu Data and Rendering
document.addEventListener('DOMContentLoaded', function () {
    // Menu items data
    const menuItems = [
        { name: 'Khuyến mãi hấp dẫn', href: '/products/khuyen-mai', hasArrow: true },
        { name: 'Trà sữa truyền thống', href: '/products/tra-sua-truyen-thong', hasArrow: false },
        { name: 'Trà sữa trái cây', href: '/products/tra-sua-trai-cay', hasArrow: true },
        { name: 'Trà sữa kem cheese', href: '/products/tra-sua-kem-cheese', hasArrow: false },
        { name: 'Trà sữa matcha', href: '/products/tra-sua-matcha', hasArrow: false },
        { name: 'Trà sữa chocolate', href: '/products/tra-sua-chocolate', hasArrow: false },
        { name: 'Topping & Phụ kiện', href: '/products/topping-phu-kien', hasArrow: false },
        { name: 'Combo tiết kiệm', href: '/products/combo-tiet-kiem', hasArrow: false }
    ];

    // Navigation links data
    const navLinks = [
        { name: 'Trang chủ', href: '/' },
        { name: 'Menu', href: '/products' },
        { name: 'Giới thiệu', href: '/about' },
        { name: 'Liên hệ', href: '/contact' }
    ];

    // Render menu items
    function renderMenuItems() {
        const menuContainer = document.querySelector('#categoryMenu .space-y-0');
        if (!menuContainer) return;

        menuContainer.innerHTML = menuItems.map(item => `
            <a
                href="${item.href}"
                class="flex items-center justify-between px-4 py-3 text-[#2f604a] bg-[#fdfbf7] shadow-md hover:bg-gray-100 transition-all duration-300 border-b border-dotted border-gray-300"
                style="opacity: 0; transform: translateY(-20px); transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);"
            >
                <span class="font-medium">${item.name}</span>
                ${item.hasArrow ? '<i class="fas fa-chevron-right text-gray-400 text-sm"></i>' : ''}
            </a>
        `).join('');
    }

    // Set active navigation link
    function setActiveNavLink() {
        const currentPath = window.location.pathname;

        // Desktop navigation
        const desktopNav = document.querySelector('.hidden.md\\:flex.items-center.space-x-6');
        if (desktopNav) {
            setActiveForContainer(desktopNav);
        }

        // Mobile navigation
        const mobileNav = document.querySelector('#mobile-menu .flex.flex-col.space-y-2');
        if (mobileNav) {
            setActiveForContainer(mobileNav);
        }
    }

    // Helper function to set active state for a container
    function setActiveForContainer(container) {
        const currentPath = window.location.pathname;

        // Clear all active states
        container.querySelectorAll('a').forEach(link => {
            link.classList.remove('nav-link-active');
        });

        // Find and set active link
        container.querySelectorAll('a').forEach(link => {
            const href = link.getAttribute('href');

            // Exact match for root
            if (currentPath === '/' && href === '/') {
                link.classList.add('nav-link-active');
            }
            // Match for other paths (starts with)
            else if (currentPath !== '/' && href !== '/' && currentPath.startsWith(href)) {
                link.classList.add('nav-link-active');
            }
        });
    }

    // Initialize menu and active states
    renderMenuItems();
    setActiveNavLink();
});
