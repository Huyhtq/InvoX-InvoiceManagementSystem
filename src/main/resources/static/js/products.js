document.addEventListener('DOMContentLoaded', function () {
    console.log("products.js loaded for product form interaction");

    // --- CÁC THÀNH PHẦN LIÊN QUAN ĐẾN FORM SẢN PHẨM ---
    const categorySelect = document.getElementById('categorySelect');
    const skuInput = document.getElementById('skuInput');
    const productForm = document.getElementById('productForm'); // Để xác định action của form

    // Biến 'productId' được truyền từ Thymeleaf thông qua một thẻ script inline.
    // Ví dụ: <script th:inline="javascript"> const productId = /*[[${product?.id}]]*/ null; </script>
    // Phải đảm bảo biến 'productId' này đã được định nghĩa TRƯỚC KHI file products.js được load.
    // Nếu 'productId' không được định nghĩa toàn cục theo cách trên, cần tìm cách khác để lấy.
    // Hiện tại, chúng ta sẽ giả định 'productId' đã tồn tại trong scope global từ HTML.
    
    let isEditMode = false;
    if (typeof productId !== 'undefined' && productId !== null) {
        isEditMode = true;
    } else if (productForm) { 
        // Fallback: Kiểm tra action của form nếu productId không có sẵn (ít tin cậy hơn)
        const formAction = productForm.getAttribute('action');
        if (formAction && formAction.includes('/edit/')) {
            isEditMode = true;
        }
    }

    console.log("Product Form - Edit Mode:", isEditMode, "(Based on productId:", (typeof productId !== 'undefined' ? productId : 'not defined'), "or form action)");

    /**
     * Hàm gọi API để lấy SKU gợi ý và cập nhật vào ô input.
     */
    function fetchAndSetSku() {
        // Đảm bảo categorySelect tồn tại trước khi truy cập 'value'
        if (!categorySelect) {
            console.warn("Category select element not found.");
            return;
        }
        const categoryId = categorySelect.value;

        // Chỉ tự động điền/thay đổi SKU khi:
        // 1. Đang ở chế độ tạo mới (!isEditMode)
        // 2. Đã chọn một category hợp lệ (categoryId có giá trị)
        if (categoryId && !isEditMode) {
            console.log("Fetching SKU for categoryId:", categoryId);
            // Đảm bảo URL API endpoint là chính xác
            fetch(`/api/products/suggest-sku?categoryId=${categoryId}`)
                .then(response => {
                    if (!response.ok) {
                        // Nếu server trả về lỗi, cố gắng đọc text lỗi và ném ra Error
                        return response.text().then(text => {
                            console.error("Server error response for SKU suggestion:", text);
                            throw new Error(text || 'Error fetching SKU suggestion from server. Status: ' + response.status);
                        });
                    }
                    return response.text(); // API trả về SKU dưới dạng text thuần
                })
                .then(suggestedSku => {
                    console.log("Suggested SKU received:", suggestedSku);
                    if (skuInput) {
                        skuInput.value = suggestedSku;
                    } else {
                        console.warn("SKU input element not found.");
                    }
                })
                .catch(error => {
                    console.error('Error during fetchAndSetSku:', error.message);
                    if (skuInput) {
                        skuInput.value = ''; // Xóa SKU nếu có lỗi
                    }
                    // Cân nhắc hiển thị thông báo lỗi này cho người dùng nếu cần,
                    // ví dụ thông qua một div thông báo trên form.
                    // alert('Could not suggest SKU: ' + error.message);
                });
        } else if (!categoryId && !isEditMode && skuInput) {
            // Nếu không chọn category (và đang ở mode tạo mới), xóa SKU
            console.log("No category selected in new mode, clearing SKU.");
            skuInput.value = '';
        } else if (isEditMode) {
            console.log("In edit mode, SKU auto-fill is disabled on category change.");
        }
    }

    // Gắn sự kiện và kiểm tra khi DOM đã sẵn sàng
    if (categorySelect && skuInput) {
        // Lắng nghe sự kiện thay đổi lựa chọn category
        categorySelect.addEventListener('change', fetchAndSetSku);

        // Xử lý khi trang được load (đặc biệt quan trọng khi form load lại sau lỗi validation ở chế độ tạo mới):
        // Nếu không phải edit mode VÀ đã có một category được chọn sẵn (ví dụ từ giá trị th:field)
        if (!isEditMode && categorySelect.value) {
            console.log("Initial SKU fetch on page load for new product with category pre-selected:", categorySelect.value);
            fetchAndSetSku(); // Gọi để điền SKU nếu category đã được chọn sẵn
        }
    } else {
        if (!categorySelect) console.warn("Element with ID 'categorySelect' not found. SKU auto-fill on category change will not work.");
        if (!skuInput) console.warn("Element with ID 'skuInput' not found. SKU auto-fill will not work.");
    }

    // --- CÁC CHỨC NĂNG KHÁC CHO TRANG PRODUCT (NẾU CÓ) ---
    // Ví dụ: Toggle sidebar, chuyển đổi view table/grid, filter UI (nếu có trên trang form này)
    // Lưu ý: Các chức năng này có thể nằm ở file JS chung nếu được sử dụng ở nhiều trang.

    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarClose = document.getElementById('sidebarclose');
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content'); // Giả sử #content là vùng nội dung chính

    function toggleSidebar() {
        if (sidebar && content) {
            sidebar.classList.toggle('hidden'); // Giả sử 'hidden' là class để ẩn sidebar
            content.classList.toggle('expanded'); // Giả sử 'expanded' là class cho content khi sidebar ẩn
            console.log("Sidebar toggled. Sidebar hidden:", sidebar.classList.contains('hidden'));
        } else {
            if (!sidebar) console.warn("Sidebar element not found for toggle.");
            if (!content) console.warn("Content element not found for toggle.");
        }
    }

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebar);
    }
    if (sidebarClose) {
        sidebarClose.addEventListener('click', toggleSidebar);
    }

    // Make sidebar links active based on current path
    // (Phần này phù hợp hơn nếu products.js là script chung cho nhiều trang có sidebar)
    try {
        const currentPath = window.location.pathname;
        document.querySelectorAll('.sidebar-link').forEach(link => {
            if (link.getAttribute('href') === currentPath) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });
    } catch (e) {
        console.warn("Error setting active sidebar link:", e);
    }

    // --- Chức năng chuyển đổi giữa Table View và Grid View (nếu có trên trang này) ---
    // Thông thường chức năng này ở trang danh sách sản phẩm, không phải trang form.
    // Nếu bạn có nó ở đây, hãy đảm bảo các element tồn tại.
    const tableViewBtn = document.getElementById('tableViewBtn');
    const gridViewBtn = document.getElementById('gridViewBtn');
    const tableResponsive = document.querySelector('.table-responsive');
    const gridView = document.querySelector('.grid-view');

    if (tableViewBtn && gridViewBtn && tableResponsive && gridView) {
        tableViewBtn.addEventListener('click', function () {
            tableResponsive.style.display = 'block';
            gridView.style.display = 'none';
            tableViewBtn.classList.add('active');
            gridViewBtn.classList.remove('active');
        });

        gridViewBtn.addEventListener('click', function () {
            tableResponsive.style.display = 'none';
            gridView.style.display = 'block';
            gridViewBtn.classList.add('active');
            tableViewBtn.classList.remove('active');
        });
    }

    // --- Xử lý hiển thị lại filter (nếu có trên trang form) ---
    // Thông thường chức năng này ở trang danh sách sản phẩm.
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter && statusFilter.dataset.selectedStatus) {
        statusFilter.value = statusFilter.dataset.selectedStatus;
    }

    const categoryFilterForList = document.getElementById('categoryFilter'); // Đổi tên để tránh trùng
    if (categoryFilterForList && categoryFilterForList.dataset.selectedCategoryId) {
        categoryFilterForList.value = categoryFilterForList.dataset.selectedCategoryId;
    }

});