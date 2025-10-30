/**
 * Demo script cho module Promotion
 * 
 * Chạy script này trong browser console để test các chức năng
 * 
 * @author Spring Boot Team
 * @version 1.0
 */

console.log('🎯 Promotion Module Demo Script');
console.log('================================');

// Test data mẫu
const samplePromotions = [
    {
        name: "Khuyến mãi Black Friday 2024",
        description: "Giảm giá lớn nhất trong năm - lên đến 70%",
        discountPercent: 70,
        startDate: "2024-11-29T00:00:00",
        endDate: "2024-11-30T23:59:59",
        isActive: true
    },
    {
        name: "Ưu đãi cuối tuần",
        description: "Giảm giá 20% cho tất cả sản phẩm",
        discountPercent: 20,
        startDate: "2024-12-07T00:00:00",
        endDate: "2024-12-08T23:59:59",
        isActive: true
    },
    {
        name: "Khuyến mãi tháng 12",
        description: "Chào mừng tháng 12 với nhiều ưu đãi hấp dẫn",
        discountPercent: 30,
        startDate: "2024-12-01T00:00:00",
        endDate: "2024-12-31T23:59:59",
        isActive: true
    }
];

// Function để tạo khuyến mãi demo
async function createDemoPromotions() {
    console.log('🚀 Bắt đầu tạo khuyến mãi demo...');
    
    const apiTester = new PromotionAPITester();
    
    for (let i = 0; i < samplePromotions.length; i++) {
        const promotion = samplePromotions[i];
        console.log(`\n📝 Tạo khuyến mãi ${i + 1}: ${promotion.name}`);
        
        try {
            const result = await apiTester.testCreatePromotion(promotion);
            console.log(`✅ Thành công! ID: ${result.id}`);
        } catch (error) {
            console.log(`❌ Lỗi: ${error.message}`);
        }
        
        // Delay 1 giây giữa các request
        await new Promise(resolve => setTimeout(resolve, 1000));
    }
    
    console.log('\n🎉 Hoàn thành tạo khuyến mãi demo!');
}

// Function để test tất cả chức năng
async function runFullDemo() {
    console.log('🎬 Chạy demo đầy đủ...');
    
    const apiTester = new PromotionAPITester();
    
    try {
        // 1. Tạo khuyến mãi demo
        await createDemoPromotions();
        
        // 2. Lấy danh sách
        console.log('\n📋 Lấy danh sách khuyến mãi...');
        await apiTester.testGetAllPromotions();
        
        // 3. Tìm kiếm
        console.log('\n🔍 Tìm kiếm khuyến mãi...');
        await apiTester.testSearchPromotions('Black Friday');
        
        // 4. Lấy active
        console.log('\n✅ Lấy khuyến mãi đang hoạt động...');
        await apiTester.testGetActivePromotions();
        
        // 5. Phân trang
        console.log('\n📄 Test phân trang...');
        await apiTester.testGetPromotionsPaged(0, 5);
        
        // 6. Đếm
        console.log('\n🔢 Đếm khuyến mãi...');
        await apiTester.testCountPromotions();
        
        console.log('\n🎉 Demo hoàn thành thành công!');
        
    } catch (error) {
        console.error('💥 Demo thất bại:', error);
    }
}

// Function để xóa tất cả khuyến mãi demo
async function cleanupDemoPromotions() {
    console.log('🧹 Dọn dẹp khuyến mãi demo...');
    
    try {
        const apiTester = new PromotionAPITester();
        const promotions = await apiTester.testGetAllPromotions();
        
        for (const promotion of promotions) {
            if (samplePromotions.some(sample => sample.name === promotion.name)) {
                console.log(`🗑️ Xóa khuyến mãi: ${promotion.name}`);
                await apiTester.testDeletePromotion(promotion.id);
            }
        }
        
        console.log('✅ Dọn dẹp hoàn thành!');
        
    } catch (error) {
        console.error('❌ Lỗi dọn dẹp:', error);
    }
}

// Function để hiển thị menu demo
function showDemoMenu() {
    console.log('\n📋 MENU DEMO:');
    console.log('1. createDemoPromotions() - Tạo khuyến mãi demo');
    console.log('2. runFullDemo() - Chạy demo đầy đủ');
    console.log('3. cleanupDemoPromotions() - Dọn dẹp khuyến mãi demo');
    console.log('4. showDemoMenu() - Hiển thị menu này');
    console.log('\n💡 Sử dụng: Gõ tên function trong console để chạy');
}

// Function để test giao diện
function testUI() {
    console.log('🎨 Test giao diện...');
    
    // Kiểm tra xem có đang ở trang admin không
    if (!window.location.pathname.includes('/admin/')) {
        console.log('⚠️ Vui lòng truy cập trang admin trước');
        console.log('🔗 Link: http://localhost:8080/admin/promotions');
        return;
    }
    
    // Kiểm tra các element cần thiết
    const elements = [
        { selector: '#adminSidebarNav', name: 'Sidebar navigation' },
        { selector: '.promotion-card', name: 'Promotion cards' },
        { selector: '.form-section', name: 'Form sections' },
        { selector: '.preview-card', name: 'Preview card' }
    ];
    
    elements.forEach(element => {
        const el = document.querySelector(element.selector);
        if (el) {
            console.log(`✅ ${element.name}: Tìm thấy`);
        } else {
            console.log(`❌ ${element.name}: Không tìm thấy`);
        }
    });
    
    // Kiểm tra menu khuyến mãi
    const promotionMenu = document.querySelector('a[href="/admin/promotions"]');
    if (promotionMenu) {
        console.log('✅ Menu "Quản lý khuyến mãi": Tìm thấy');
    } else {
        console.log('❌ Menu "Quản lý khuyến mãi": Không tìm thấy');
    }
    
    const createMenu = document.querySelector('a[href="/admin/promotions/create"]');
    if (createMenu) {
        console.log('✅ Menu "Thêm khuyến mãi": Tìm thấy');
    } else {
        console.log('❌ Menu "Thêm khuyến mãi": Không tìm thấy');
    }
}

// Auto-run khi load
document.addEventListener('DOMContentLoaded', function() {
    console.log('🎯 Promotion Demo Script đã sẵn sàng!');
    showDemoMenu();
});

// Export functions
window.createDemoPromotions = createDemoPromotions;
window.runFullDemo = runFullDemo;
window.cleanupDemoPromotions = cleanupDemoPromotions;
window.showDemoMenu = showDemoMenu;
window.testUI = testUI;

console.log('🎯 Promotion Demo Script loaded successfully!');
