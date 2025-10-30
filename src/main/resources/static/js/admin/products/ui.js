// =====================================================
// UI MODULE - Quản lý giao diện người dùng
// =====================================================

// Display products in table
function displayProducts(products) {
    // BẮT ĐẦU ĐO THỜI GIAN HIỂN THỊ - Start display timing
    const displayStartTime = performance.now();
    console.log('🎨 [DISPLAY PRODUCTS] Bắt đầu hiển thị sản phẩm...');
    console.log('📊 [DISPLAY PRODUCTS] Số lượng sản phẩm:', products.length);
    console.log('📦 [DISPLAY PRODUCTS] Dữ liệu sản phẩm:', products);

    // Lưu dữ liệu sản phẩm vào biến global để sử dụng cho edit/delete
    window.productsData = products;

    // Lấy element tbody của table
    const tbody = document.getElementById('productsTableBody');
    if (!tbody) {
        console.error('❌ [DISPLAY PRODUCTS] Không tìm thấy tbody element!');
        return;
    }

    // ĐO THỜI GIAN CHUẨN BỊ DỮ LIỆU - Measure data preparation time
    const prepStartTime = performance.now();
    
    // Pre-calculate common values để tối ưu performance
    const statusClasses = {
        inStock: 'bg-green-100 text-green-800',
        outOfStock: 'bg-red-100 text-red-800'
    };
    const statusTexts = {
        inStock: 'Còn hàng',
        outOfStock: 'Hết hàng'
    };

    const prepEndTime = performance.now();
    console.log(`⚡ [DISPLAY PRODUCTS] Chuẩn bị dữ liệu: ${(prepEndTime - prepStartTime).toFixed(2)}ms`);

    // ĐO THỜI GIAN BUILD HTML - Measure HTML building time
    const buildStartTime = performance.now();
    
    // Build HTML string sử dụng map().join() - nhanh hơn innerHTML từng element
    const html = products.map((product, index) => {
        // Xử lý dữ liệu cho từng sản phẩm
        const isInStock = product.stock > 0;
        const statusClass = isInStock ? statusClasses.inStock : statusClasses.outOfStock;
        const statusText = isInStock ? statusTexts.inStock : statusTexts.outOfStock;
        const price = formatPrice(product.price);
        const categoryName = product.category ? product.category.name : 'Không có danh mục';

        // Tạo HTML row cho sản phẩm
        return `
            <tr class="product-row" data-product-id="${product.id}">
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                        <div class="h-12 w-12 flex-shrink-0">
                            <div class="h-12 w-12 bg-gray-100 rounded-lg flex items-center justify-center">
                                <i class="fas fa-box text-gray-400"></i>
                            </div>
                        </div>
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">${product.name}</div>
                            <div class="text-sm text-gray-500">${categoryName}</div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${price}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${product.stock}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full ${statusClass}">${statusText}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button class="text-blue-600 hover:text-blue-900 mr-3" onclick="editProduct('${product.id}')">
                        <i class="fas fa-edit mr-1"></i>Sửa
                    </button>
                    <button class="text-red-600 hover:text-red-900" onclick="showDeleteModal('${product.id}')">
                        <i class="fas fa-trash mr-1"></i>Xóa
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    const buildEndTime = performance.now();
    console.log(`🔨 [DISPLAY PRODUCTS] Build HTML: ${(buildEndTime - buildStartTime).toFixed(2)}ms`);

    // ĐO THỜI GIAN CẬP NHẬT DOM - Measure DOM update time
    const domStartTime = performance.now();
    
    // Cập nhật DOM một lần duy nhất - tối ưu performance
    tbody.innerHTML = html;
    
    const domEndTime = performance.now();
    console.log(`🌐 [DISPLAY PRODUCTS] Cập nhật DOM: ${(domEndTime - domStartTime).toFixed(2)}ms`);

    // ĐO THỜI GIAN HIỂN THỊ UI - Measure UI display time
    const uiStartTime = performance.now();
    
    // Sử dụng requestAnimationFrame để hiển thị mượt mà
    requestAnimationFrame(() => {
        // Hiển thị table và ẩn các state khác
        document.getElementById('productsTable').classList.remove('hidden');
        document.getElementById('loadingState').classList.add('hidden');
        document.getElementById('errorState').classList.add('hidden');
        document.getElementById('emptyState').classList.add('hidden');
        
        // ĐO THỜI GIAN HOÀN THÀNH - Measure completion time
        const uiEndTime = performance.now();
        const totalDisplayTime = uiEndTime - displayStartTime;
        
        console.log(`🎯 [DISPLAY PRODUCTS] Hiển thị UI: ${(uiEndTime - uiStartTime).toFixed(2)}ms`);
        console.log(`🏁 [DISPLAY PRODUCTS] TỔNG THỜI GIAN HIỂN THỊ: ${totalDisplayTime.toFixed(2)}ms`);
        console.log(`📈 [DISPLAY PRODUCTS] Performance breakdown:`);
        console.log(`   - Chuẩn bị dữ liệu: ${(prepEndTime - prepStartTime).toFixed(2)}ms`);
        console.log(`   - Build HTML: ${(buildEndTime - buildStartTime).toFixed(2)}ms`);
        console.log(`   - Cập nhật DOM: ${(domEndTime - domStartTime).toFixed(2)}ms`);
        console.log(`   - Hiển thị UI: ${(uiEndTime - uiStartTime).toFixed(2)}ms`);
        console.log(`✅ [DISPLAY PRODUCTS] Hoàn thành hiển thị ${products.length} sản phẩm!`);
    });
}

// Show loading state
function showLoadingState() {
    const startTime = performance.now();
    console.log('⏳ [LOADING STATE] Hiển thị loading state...');
    
    // Sử dụng requestAnimationFrame để hiển thị ngay lập tức
    requestAnimationFrame(() => {
        document.getElementById('loadingState').classList.remove('hidden');
        document.getElementById('productsTable').classList.add('hidden');
        document.getElementById('errorState').classList.add('hidden');
        document.getElementById('emptyState').classList.add('hidden');
        
        const endTime = performance.now();
        console.log(`✅ [LOADING STATE] Hoàn thành sau: ${(endTime - startTime).toFixed(2)}ms`);
    });
}

// Show empty state
function showEmptyState() {
    const startTime = performance.now();
    console.log('📭 [EMPTY STATE] Hiển thị empty state...');
    
    document.getElementById('emptyState').classList.remove('hidden');
    document.getElementById('productsTable').classList.add('hidden');
    document.getElementById('loadingState').classList.add('hidden');
    document.getElementById('errorState').classList.add('hidden');
    
    const endTime = performance.now();
    console.log(`✅ [EMPTY STATE] Hoàn thành sau: ${(endTime - startTime).toFixed(2)}ms`);
}

// Show error state
function showErrorState(errorMessage = 'Không thể tải danh sách sản phẩm') {
    const startTime = performance.now();
    console.log('❌ [ERROR STATE] Hiển thị error state:', errorMessage);
    
    const errorState = document.getElementById('errorState');
    const errorText = errorState.querySelector('.error-message');
    
    if (errorText) {
        errorText.textContent = errorMessage;
    }
    
    errorState.classList.remove('hidden');
    document.getElementById('productsTable').classList.add('hidden');
    document.getElementById('loadingState').classList.add('hidden');
    document.getElementById('emptyState').classList.add('hidden');
    
    const endTime = performance.now();
    console.log(`✅ [ERROR STATE] Hoàn thành sau: ${(endTime - startTime).toFixed(2)}ms`);
}

// Format price to Vietnamese currency
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

// Find product by ID
function findProductById(productId) {
    return window.productsData ? window.productsData.find(prod => prod.id === productId) : null;
}

// Edit product function
function editProduct(productId) {
    console.log('Edit product:', productId);

    // Find product data
    const product = findProductById(productId);
    if (!product) {
        showToast('Không tìm thấy sản phẩm', 'error');
        return;
    }

    // Show edit modal
    showEditProductModal(product);
}

// Show edit product modal
function showEditProductModal(product) {
    console.log('Showing edit modal for product:', product);
    
    // Populate form with product data
    document.getElementById('editProductName').value = product.name || '';
    document.getElementById('editProductPrice').value = product.price || 0;
    document.getElementById('editProductStock').value = product.stock || 0;
    document.getElementById('editProductDescription').value = product.description || '';
    
    // Set current product ID
    window.currentEditProductId = product.id;
    
    // Reset button state to ensure clean start
    const saveBtn = document.getElementById('saveEdit');
    if (saveBtn) {
        saveBtn.innerHTML = '<i class="fas fa-save mr-2"></i>Lưu thay đổi';
        saveBtn.disabled = false;
    }
    
    // Load categories for dropdown
    loadCategoriesForEdit();
    
    // Show modal with animation
    const modal = document.getElementById('editProductModal');
    const content = modal.querySelector('.relative');
    
    // Reset CSS properties first
    content.style.transform = 'scale(0.8)';
    content.style.opacity = '0';
    
    // Show modal
    modal.classList.remove('hidden');
    
    // Add zoom in animation
    setTimeout(() => {
        content.style.transform = 'scale(1)';
        content.style.opacity = '1';
    }, 10);
    
    // Add click outside listener for this modal instance
    const clickOutsideHandler = function(e) {
        if (e.target === modal || e.target.hasAttribute('data-modal-backdrop')) {
            console.log('Edit modal clicked outside (instance) - closing');
            hideEditProductModal();
            modal.removeEventListener('click', clickOutsideHandler);
        }
    };
    
    // Add the event listener
    modal.addEventListener('click', clickOutsideHandler);
}

// Load categories for edit modal
async function loadCategoriesForEdit() {
    try {
        const response = await fetch('/api/categories');
        const result = await response.json();
        
        if (result.success && result.data && result.data.items) {
            const categorySelect = document.getElementById('editProductCategory');
            categorySelect.innerHTML = '<option value="">Chọn danh mục...</option>';
            
            result.data.items.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
            
            // Set current category if exists
            const currentProduct = findProductById(window.currentEditProductId);
            if (currentProduct && currentProduct.category) {
                categorySelect.value = currentProduct.category.id;
            }
            
            console.log('Categories loaded for edit:', result.data.items.length);
        } else {
            console.error('Invalid categories response:', result);
            showToast('Lỗi tải danh mục', 'error');
        }
    } catch (error) {
        console.error('Error loading categories:', error);
        showToast('Lỗi tải danh mục', 'error');
    }
}

// Hide edit product modal
function hideEditProductModal() {
    const modal = document.getElementById('editProductModal');
    const content = modal.querySelector('.relative');
    
    // Add zoom out animation
    content.style.transform = 'scale(0.8)';
    content.style.opacity = '0';
    
    setTimeout(() => {
        modal.classList.add('hidden');
        
        // Reset all CSS properties to ensure clean state
        content.style.transform = '';
        content.style.opacity = '';
        
        // Reset button state
        const saveBtn = document.getElementById('saveEdit');
        if (saveBtn) {
            saveBtn.innerHTML = '<i class="fas fa-save mr-2"></i>Lưu thay đổi';
            saveBtn.disabled = false;
        }
        
        // Reset form
        document.getElementById('editProductForm').reset();
        window.currentEditProductId = null;
        
        console.log('Edit modal closed and reset');
    }, 200);
}

// Update product function
async function updateProduct() {
    if (!window.currentEditProductId) {
        showToast('Không tìm thấy ID sản phẩm', 'error');
        return;
    }

    const form = document.getElementById('editProductForm');
    const formData = new FormData(form);
    
    const productData = {
        name: formData.get('name'),
        categoryId: formData.get('categoryId'),
        price: parseFloat(formData.get('price')),
        stock: parseInt(formData.get('stock')),
        description: formData.get('description')
    };

    // Log the data being sent
    console.log('Updating product with data:', productData);
    console.log('Product ID:', window.currentEditProductId);

    // Validate required fields
    if (!productData.name || !productData.categoryId) {
        showToast('Vui lòng điền đầy đủ thông tin bắt buộc', 'error');
        return;
    }

    try {
        // Show loading state
        const saveBtn = document.getElementById('saveEdit');
        const originalText = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Đang lưu...';
        saveBtn.disabled = true;

        const response = await fetch(`/api/products/${window.currentEditProductId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(productData)
        });

        const result = await response.json();

        if (result.success) {
            showToast('Cập nhật sản phẩm thành công!', 'success');
            hideEditProductModal();
            // Reload products to show updated data
            loadProducts();
        } else {
            showToast(result.message || 'Có lỗi xảy ra khi cập nhật sản phẩm', 'error');
        }
    } catch (error) {
        console.error('Error updating product:', error);
        showToast('Lỗi kết nối, vui lòng thử lại', 'error');
    } finally {
        // Reset button state
        const saveBtn = document.getElementById('saveEdit');
        saveBtn.innerHTML = originalText;
        saveBtn.disabled = false;
    }
}

// Show toast notification with stack effect
function showToast(message, type = 'success') {
    const toastId = 'toast-' + Date.now();
    const bgColor = type === 'success' ? 'bg-green-500' : type === 'error' ? 'bg-red-500' : 'bg-blue-500';
    const icon = type === 'success' ? 'fas fa-check' : type === 'error' ? 'fas fa-times' : 'fas fa-info';

    // Count existing toasts to calculate position
    const existingToasts = document.querySelectorAll('.toast-notification').length;
    const topPosition = 16 + (existingToasts * 80); // 16px base + 80px per toast

    const toastHtml = `
        <div id="${toastId}" class="toast-notification fixed right-4 ${bgColor} text-white px-4 py-3 rounded-lg shadow-lg z-50 transform translate-x-full transition-all duration-300 ease-out max-w-sm" style="top: ${topPosition}px;">
            <div class="flex items-center">
                <i class="${icon} mr-2 flex-shrink-0"></i>
                <span class="text-sm">${message}</span>
                <button onclick="closeToast('${toastId}')" class="ml-3 text-white hover:text-gray-200 transition-colors">
                    <i class="fas fa-times text-xs"></i>
                </button>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', toastHtml);

    // Show toast with slide-in effect from right (instant)
    requestAnimationFrame(() => {
        const toast = document.getElementById(toastId);
        if (toast) {
            toast.classList.remove('translate-x-full');
            toast.classList.add('translate-x-0');
        }
    });

    // Auto hide after 1.5 seconds (faster)
    setTimeout(() => {
        closeToast(toastId);
    }, 1500);
}

// Close specific toast
function closeToast(toastId) {
    const toast = document.getElementById(toastId);
    if (toast) {
        // Slide out to left with opacity fade (ultra-fast)
        toast.classList.add('-translate-x-full', 'opacity-0');
        setTimeout(() => {
            toast.remove();
            // Reposition remaining toasts
            repositionToasts();
        }, 50);
    }
}

// Reposition remaining toasts
function repositionToasts() {
    const toasts = document.querySelectorAll('.toast-notification');
    toasts.forEach((toast, index) => {
        const newTop = 16 + (index * 80);
        toast.style.top = newTop + 'px';
    });
}

// Show delete confirmation modal
function showDeleteModal(productId) {
    console.log('Show delete modal for product:', productId);

    // Find product data
    const product = findProductById(productId);
    if (!product) {
        showToast('Không tìm thấy sản phẩm', 'error');
        return;
    }

    // Update modal content with product info
    document.getElementById('deleteProductName').textContent = product.name;
    document.getElementById('deleteProductCategory').textContent = product.category ? product.category.name : 'Không có danh mục';
    
    // Store product ID for deletion
    document.getElementById('deleteModal').setAttribute('data-product-id', productId);

    // Show modal with jQuery smooth animation
    const $modal = $('#deleteModal');
    const $content = $modal.find('.transform');
    
    // Show modal immediately
    $modal.removeClass('hidden').show();
    
    // Smooth zoom in animation
    $content.css({
        'transform': 'scale(0.7)',
        'opacity': '0'
    });
    
    // Animate zoom in
    $content.animate({
        'opacity': '1'
    }, 200, function() {
        $content.css('transform', 'scale(1)');
    });
}

// Hide delete confirmation modal
function hideDeleteModal() {
    const $modal = $('#deleteModal');
    const $content = $modal.find('.transform');
    
    // Smooth zoom out animation
    $content.css({
        'transform': 'scale(0.7)',
        'opacity': '0'
    });
    
    // Hide modal after zoom out
    setTimeout(function() {
        $modal.addClass('hidden').hide();
    }, 200);
}

// Delete product function (called from modal)
function deleteProduct(productId) {
    console.log('Delete product:', productId);

    // Find product data
    const product = findProductById(productId);
    if (!product) {
        showToast('Không tìm thấy sản phẩm', 'error');
        return;
    }

    // Hide modal first
    hideDeleteModal();

    // Show loading state on the product row
    const productRow = document.querySelector(`.product-row[data-product-id="${productId}"]`);
    if (productRow) {
        productRow.classList.add('opacity-50');
    }

    // Call delete API
    fetch(`${API_BASE_URL}/${productId}`, {
        method: 'DELETE'
    })
        .then(response => response.json())
        .then(result => {
            console.log('Product deleted:', result);
            showToast('Xóa sản phẩm thành công!', 'success');

            // Remove the row with ultra-fast animation
            if (productRow) {
                productRow.style.transition = 'opacity 0.08s ease';
                productRow.style.opacity = '0';
                setTimeout(() => {
                    productRow.remove();
                    // Reload products to ensure data is fresh
                    loadProducts();
                }, 80);
            }
        })
        .catch(error => {
            console.error('Error deleting product:', error);
            if (productRow) {
                productRow.classList.remove('opacity-50');
            }
            showToast('Lỗi khi xóa sản phẩm: ' + error, 'error');
        });
}
