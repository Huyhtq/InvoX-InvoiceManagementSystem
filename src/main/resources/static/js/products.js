document.addEventListener('DOMContentLoaded', function () {
    console.log("products.js loaded");

    // Toggle sidebar
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarClose = document.getElementById('sidebarclose');
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content');

    if (sidebarToggle) {
        console.log("Sidebar toggle found");
        sidebarToggle.addEventListener('click', function () {
            console.log("Toggle clicked");
            sidebar.classList.toggle('hidden');
            content.classList.toggle('expanded');
        });
    } else {
        console.log("Sidebar toggle NOT found");
    }

    if (sidebarClose) {
        console.log("Sidebar close found");
        sidebarClose.addEventListener('click', function () {
            console.log("Close clicked");
            sidebar.classList.toggle('hidden');
            content.classList.toggle('expanded');
        });
    } else {
        console.log("Sidebar close NOT found");
    }

    // Make sidebar links active
    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.addEventListener('click', function () {
            document.querySelectorAll('.sidebar-link').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    const path = window.location.pathname;

    document.querySelectorAll('.sidebar-link').forEach(link => {
        // So sánh đường dẫn hiện tại với href của từng link
        if (link.getAttribute('href') === path) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });

    // --- 1. Chức năng chuyển đổi giữa Table View và Grid View ---
    const tableViewBtn = document.getElementById('tableViewBtn');
    const gridViewBtn = document.getElementById('gridViewBtn');
    const tableResponsive = document.querySelector('.table-responsive'); // Chứa bảng
    const gridView = document.querySelector('.grid-view'); // Chứa chế độ lưới

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

    // --- 2. Chức năng tìm kiếm và lọc (tương tác UI) ---
    // (Lưu ý: Logic lọc thực tế sẽ được xử lý ở ProductPageController khi form được gửi)
    // Script này chỉ là để hiển thị trạng thái đã chọn trên dropdown
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const categoryFilter = document.getElementById('categoryFilter');

    // Nếu có giá trị searchTerm từ server (th:value="${searchTerm}"), giữ nó trong input
    // Nếu có giá trị status từ server, chọn option tương ứng
    if (statusFilter) {
        const selectedStatus = statusFilter.dataset.selectedStatus; // Giả sử bạn thêm dataset attribute trong Thymeleaf
        if (selectedStatus) {
            statusFilter.value = selectedStatus;
        }
    }

    // Nếu có giá trị categoryId từ server, chọn option tương ứng
    if (categoryFilter) {
        const selectedCategoryId = categoryFilter.dataset.selectedCategoryId; // Giả sử bạn thêm dataset attribute
        if (selectedCategoryId) {
            categoryFilter.value = selectedCategoryId;
        }
    }

    // Bạn có thể thêm các chức năng JavaScript phức tạp hơn tại đây,
    // ví dụ như gửi yêu cầu AJAX để lọc/tìm kiếm mà không cần tải lại trang.
    // Tuy nhiên, với thiết kế hiện tại (form submit GET), điều đó chưa cần thiết.

    // --- 3. Xử lý thông báo thành công/lỗi từ RedirectAttributes (nếu có) ---
    // Spring Boot với RedirectAttributes thêm các flash attributes vào Model,
    // nhưng chúng có thể không được hiển thị trực tiếp nếu không có logic JS.
    // Đây là một ví dụ đơn giản để hiển thị chúng.
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('successMessage')) {
        alert(urlParams.get('successMessage'));
        // Xóa tham số khỏi URL sau khi hiển thị
        window.history.replaceState(null, null, window.location.pathname);
    }
    if (urlParams.has('errorMessage')) {
        alert(urlParams.get('errorMessage'));
        // Xóa tham số khỏi URL sau khi hiển thị
        window.history.replaceState(null, null, window.location.pathname);
    }
});