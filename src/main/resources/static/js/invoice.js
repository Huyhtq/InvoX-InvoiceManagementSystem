document.addEventListener('DOMContentLoaded', function () {
    console.log("DOM loaded");

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

    // --- Elements Form Chính ---
    const invoiceForm = document.getElementById('invoiceForm');
    const productTable = document.getElementById('productTable');
    const emptyProductList = document.getElementById('emptyProductList');
    const subtotalElement = document.getElementById('subtotal');
    const taxElement = document.getElementById('tax');
    const totalElement = document.getElementById('total');

    // --- Elements cho Customer Autocomplete ---
    const customerSearchInput = document.getElementById('customerSearchInput');
    const customerAutocompleteSuggestions = document.getElementById('customerAutocompleteSuggestions');
    const selectedCustomerIdInput = document.getElementById('selectedCustomerId');
    const selectedCustomerDetailsDiv = document.getElementById('selectedCustomerDetails');
    let currentSelectedCustomerForDirectAdd = null; // To store the selected customer object

    // --- Elements cho Logic Thêm Sản Phẩm Mới ---
    const productSearchInput = document.getElementById('productSearchInput');
    const autocompleteSuggestions = document.getElementById('autocompleteSuggestions'); // For products
    const selectedProductIdInput = document.getElementById('selectedProductId');
    const selectedProductDetailsDiv = document.getElementById('selectedProductDetails');
    const productQuantityInput = document.getElementById('productQuantityInput');
    const invoiceTaxRateInput = document.getElementById('invoiceTaxRateInput');
    const invoiceTaxLabel = document.getElementById('invoiceTaxLabel');
    const directAddProductBtn = document.getElementById('directAddProductBtn');
    const addProductError = document.getElementById('addProductError');

    // Elements mới cho thông tin điểm của khách hàng
    const customerPointDetailsSection = document.getElementById('customerPointDetailsSection');
    const customerRankDisplay = document.getElementById('customerRankDisplay');
    const customerPointsDisplay = document.getElementById('customerPointsDisplay');
    const pointsToRedeemInput = document.getElementById('pointsToRedeemInput');
    const pointsRedeemError = document.getElementById('pointsRedeemError');

    // --- Dữ liệu từ Thymeleaf ---
    const availableProducts = window.allProductsData || [];
    const availableCustomers = window.allCustomersData || []; // NEW
    const initialInvoiceItems = window.initialInvoiceItems || [];
    const DEFAULT_INVOICE_TAX_PERCENTAGE = 10;

    // --- Trạng thái nội bộ ---
    let cartItems = [];
    let currentSelectedProductForDirectAdd = null;

    // --- Helper Functions ---
    function formatCurrency(amount) {
        if (amount === null || isNaN(amount)) {
            return '0 ₫';
        }
        return Number(amount).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    // ... (updateCartDisplay, handleTableQuantityChange, handleTableQuantityInputValidation, handleDeleteItem, updateTotals functions remain largely the same) ...
    // Ensure `updateTotals` is called when customer changes, especially for points logic.

    // --- Quản lý Giỏ hàng và Hiển thị ---
    function updateCartDisplay() {
        if (!productTable || !productTable.querySelector('tbody') || !emptyProductList) {
            console.warn("Một số element DOM của bảng sản phẩm không tìm thấy. updateCartDisplay() có thể không hoạt động đúng.");
            return; 
        }
        const tbody = productTable.querySelector('tbody');
        tbody.innerHTML = '';
        if (cartItems.length === 0) {
            emptyProductList.style.display = 'block';
            productTable.style.display = 'none';
        } else {
            emptyProductList.style.display = 'none';
            productTable.style.display = 'table';
            cartItems.forEach((item, index) => {
                const row = document.createElement('tr');
                const itemSubtotal = item.product.price * item.quantity;
                row.innerHTML = `
                    <input type="hidden" name="items[${index}].productId" value="${item.product.id}">
                    <input type="hidden" name="items[${index}].quantity" value="${item.quantity}">
                    <td>${item.product.name}</td>
                    <td>${formatCurrency(item.product.price)}</td>
                    <td>
                        <input type="number" class="form-control form-control-sm quantity-input-in-table" 
                               value="${item.quantity}" 
                               min="1" data-item-index="${index}"
                               max="${item.product.quantity > 0 ? item.product.quantity : 1}"> 
                        <div class="form-text text-muted small">Tồn kho: ${item.product.quantity}</div>
                    </td>
                    <td>${formatCurrency(itemSubtotal)}</td> 
                    <td class="text-end">
                        <button type="button" class="btn btn-danger btn-sm delete-item-btn" data-item-index="${index}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                `;
                tbody.appendChild(row);
            });
            tbody.querySelectorAll('.quantity-input-in-table').forEach(input => {
                input.addEventListener('change', handleTableQuantityChange);
                input.addEventListener('input', handleTableQuantityInputValidation);
            });
            tbody.querySelectorAll('.delete-item-btn').forEach(button => {
                button.addEventListener('click', handleDeleteItem);
            });
        }
        updateTotals();
    }

    function handleTableQuantityChange(e) {
        const index = parseInt(e.target.dataset.itemIndex);
        if (index < 0 || index >= cartItems.length) return;
        let newQuantity = parseInt(e.target.value);
        const item = cartItems[index];
        const maxQuantity = item.product.quantity > 0 ? item.product.quantity : 1;
        if (isNaN(newQuantity) || newQuantity < 1) newQuantity = 1;
        if (newQuantity > maxQuantity) {
            newQuantity = maxQuantity;
            alert(`Số lượng sản phẩm "${item.product.name}" không được vượt quá tồn kho (${maxQuantity}).`);
        }
        e.target.value = newQuantity;
        item.quantity = newQuantity;
        const row = e.target.closest('tr');
        if (row) {
            const hiddenQuantityInput = row.querySelector(`input[name="items[${index}].quantity"]`);
            if (hiddenQuantityInput) hiddenQuantityInput.value = newQuantity;
            const subtotalCell = row.cells[3];
            if(subtotalCell) subtotalCell.textContent = formatCurrency(item.product.price * newQuantity);
        }
        updateTotals();
    }
    
    function handleTableQuantityInputValidation(e){
        const maxQuantity = parseInt(e.target.max);
        const enteredQuantity = parseInt(e.target.value);
        if (isNaN(enteredQuantity) || enteredQuantity > maxQuantity || enteredQuantity < 1) {
            e.target.classList.add('is-invalid');
        } else {
            e.target.classList.remove('is-invalid');
        }
    }

    function handleDeleteItem(e) {
        const button = e.target.closest('.delete-item-btn');
        if (!button) return;
        const index = parseInt(button.dataset.itemIndex);
        cartItems.splice(index, 1);
        updateCartDisplay();
    }

    function updateTotals() {
    if (!subtotalElement || !taxElement || !totalElement || !pointsRedeemError) { // Thêm pointsRedeemError vào kiểm tra
        return;
    }
    let subtotal = 0;
    cartItems.forEach(item => {
        subtotal += item.product.price * item.quantity;
    });

    let currentInvoiceTaxPercentage = DEFAULT_INVOICE_TAX_PERCENTAGE;
    if (invoiceTaxRateInput && invoiceTaxRateInput.value.trim() !== "") {
        let parsedTax = parseFloat(invoiceTaxRateInput.value);
        if (!isNaN(parsedTax) && parsedTax >= 0 && parsedTax <= 100) {
            currentInvoiceTaxPercentage = parsedTax;
        }
    } else if (invoiceTaxRateInput && invoiceTaxRateInput.value.trim() === '') {
        currentInvoiceTaxPercentage = DEFAULT_INVOICE_TAX_PERCENTAGE;
    }

    const invoiceTaxRateValue = currentInvoiceTaxPercentage / 100;
    const totalInvoiceTax = subtotal * invoiceTaxRateValue;
    const amountPayableAfterTax = subtotal + totalInvoiceTax; // Tổng tiền sau thuế, trước khi đổi điểm

    let pointsToRedeemValue = 0; // Đây là SỐ ĐIỂM, không phải giá trị tiền
    let isValidPointsFormat = true; // Cờ kiểm tra định dạng điểm ban đầu

    if (pointsToRedeemInput && pointsToRedeemInput.value) {
        const rawPoints = parseInt(pointsToRedeemInput.value);
        // Kiểm tra sơ bộ định dạng điểm (những lỗi này đã được validateAndProcessPointsRedeemed xử lý chính)
        if (isNaN(rawPoints) || rawPoints < 0 || (rawPoints > 0 && rawPoints % 1000 !== 0)) {
            isValidPointsFormat = false; // Sẽ không xét lỗi vượt quá tổng tiền nếu định dạng điểm đã sai
        }
        // Chỉ lấy giá trị điểm nếu định dạng cơ bản đúng và không vượt quá điểm tối đa của khách
        const maxPointsAllowedByCustomer = parseInt(pointsToRedeemInput.max) || 0;
        if (isValidPointsFormat && rawPoints <= maxPointsAllowedByCustomer) {
            pointsToRedeemValue = rawPoints;
        } else if (isValidPointsFormat && rawPoints > maxPointsAllowedByCustomer) {
            // Nếu điểm nhập vượt quá điểm khách có, không cần check lỗi vượt tổng tiền nữa
            isValidPointsFormat = false; 
        }
    }

    const redeemedAmountEquivalent = pointsToRedeemValue; 
    
    let pointsExceedTotalError = "";
    if (isValidPointsFormat && // Chỉ kiểm tra nếu định dạng điểm và số điểm trong giới hạn của khách là ổn
        redeemedAmountEquivalent > amountPayableAfterTax &&
        amountPayableAfterTax > 0) { // Chỉ báo lỗi nếu tổng tiền > 0
        pointsExceedTotalError = `Số điểm sử dụng (tương đương ${formatCurrency(redeemedAmountEquivalent)}) không được lớn hơn tổng tiền phải thanh toán (${formatCurrency(amountPayableAfterTax)}).`;
    }

    // Hiển thị lỗi (ưu tiên lỗi vượt quá tổng tiền nếu có)
    if (pointsExceedTotalError) {
        pointsRedeemError.textContent = pointsExceedTotalError;
        pointsRedeemError.style.display = 'block';
    } else if (!pointsRedeemError.textContent && pointsRedeemError.style.display === 'block') {
        // Nếu không có lỗi vượt quá tổng tiền và pointsRedeemError đang hiển thị lỗi khác (từ validateAndProcessPointsRedeemed)
        // thì không làm gì, để lỗi đó hiển thị. Nếu pointsRedeemError trống thì ẩn đi.
    } else if (!pointsRedeemError.textContent) { // Nếu không có lỗi nào khác từ validateAndProcessPointsRedeemed
        pointsRedeemError.style.display = 'none';
    }


    const finalAmount = amountPayableAfterTax - (pointsExceedTotalError ? 0 : redeemedAmountEquivalent); // Nếu lỗi thì không trừ điểm

    subtotalElement.textContent = formatCurrency(subtotal);
    taxElement.textContent = formatCurrency(totalInvoiceTax);
    totalElement.textContent = formatCurrency(finalAmount >= 0 ? finalAmount : 0);

    if (invoiceTaxLabel) {
        invoiceTaxLabel.textContent = `Thuế (${currentInvoiceTaxPercentage.toFixed(0)}%):`;
    }
}
    
    // --- Logic Thêm Sản Phẩm Mới (Product Autocomplete) ---
    if (productSearchInput && autocompleteSuggestions && selectedProductIdInput && selectedProductDetailsDiv && addProductError) {
        productSearchInput.addEventListener('input', function () {
            const searchTerm = this.value.trim().toLowerCase();
            autocompleteSuggestions.innerHTML = '';
            currentSelectedProductForDirectAdd = null;
            selectedProductIdInput.value = '';
            selectedProductDetailsDiv.style.display = 'none';
            selectedProductDetailsDiv.innerHTML = '';
            addProductError.style.display = 'none';

            if (searchTerm.length < 1) {
                autocompleteSuggestions.style.display = 'none';
                return;
            }

            const filteredProducts = availableProducts.filter(product =>
                (product.name && product.name.toLowerCase().includes(searchTerm)) ||
                (product.sku && product.sku.toLowerCase().includes(searchTerm))
            );

            if (filteredProducts.length > 0) {
                filteredProducts.slice(0, 10).forEach(product => {
                    const suggestionItem = document.createElement('a');
                    suggestionItem.href = '#';
                    suggestionItem.className = 'list-group-item list-group-item-action py-2 px-3';
                    suggestionItem.innerHTML = `
                        <div class="d-flex w-100 justify-content-between">
                            <h6 class="mb-1">${product.name}</h6>
                            <small class="text-muted">Tồn: ${product.quantity}</small>
                        </div>
                        <small class="text-muted">SKU: ${product.sku || 'N/A'} - Giá: ${formatCurrency(product.price)}</small>
                    `;
                    suggestionItem.addEventListener('click', function (e) {
                        e.preventDefault();
                        selectProductForDirectAdd(product);
                    });
                    autocompleteSuggestions.appendChild(suggestionItem);
                });
                autocompleteSuggestions.style.display = 'block';
            } else {
                autocompleteSuggestions.innerHTML = '<div class="list-group-item text-muted py-2 px-3">Không tìm thấy sản phẩm.</div>';
                autocompleteSuggestions.style.display = 'block';
            }
        });
    }

    function selectProductForDirectAdd(product) {
        currentSelectedProductForDirectAdd = product;
        if(productSearchInput) productSearchInput.value = product.name;
        if(selectedProductIdInput) selectedProductIdInput.value = product.id;
        if(selectedProductDetailsDiv) {
            selectedProductDetailsDiv.innerHTML = `
                <strong>Đã chọn:</strong> ${product.name} (SKU: ${product.sku}) <br>
                <strong>Giá:</strong> ${formatCurrency(product.price)} | <strong>Tồn kho:</strong> ${product.quantity}
            `;
            selectedProductDetailsDiv.style.display = 'block';
        }
        if(autocompleteSuggestions) {
            autocompleteSuggestions.innerHTML = '';
            autocompleteSuggestions.style.display = 'none';
        }
        if(productQuantityInput) {
            productQuantityInput.value = 1;
            productQuantityInput.max = product.quantity > 0 ? product.quantity : 1;
            productQuantityInput.focus();
        }
        if(addProductError) addProductError.style.display = 'none';
    }

    if (productSearchInput && autocompleteSuggestions) { // For product
        document.addEventListener('click', function(event) {
            if (!productSearchInput.contains(event.target) && !autocompleteSuggestions.contains(event.target)) {
                if(autocompleteSuggestions) autocompleteSuggestions.style.display = 'none';
            }
        });
    }
    
    if (directAddProductBtn) {
        directAddProductBtn.addEventListener('click', function() {
            if(addProductError) addProductError.style.display = 'none';
            if (!currentSelectedProductForDirectAdd) {
                if(addProductError) {
                    addProductError.textContent = 'Vui lòng tìm và chọn một sản phẩm từ gợi ý.';
                    addProductError.style.display = 'block';
                }
                if(productSearchInput) productSearchInput.focus();
                return;
            }
            const quantity = parseInt(productQuantityInput.value);
            const stock = currentSelectedProductForDirectAdd.quantity;
            if (isNaN(quantity) || quantity < 1) {
                if(addProductError) {
                    addProductError.textContent = 'Số lượng phải là số dương.';
                    addProductError.style.display = 'block';
                }
                if(productQuantityInput) productQuantityInput.focus();
                return;
            }
            if (quantity > stock) {
                if(addProductError) {
                    addProductError.textContent = `Sản phẩm "${currentSelectedProductForDirectAdd.name}" chỉ còn ${stock} trong kho.`;
                    addProductError.style.display = 'block';
                }
                if(productQuantityInput) {
                    productQuantityInput.value = stock > 0 ? stock : 1;
                    productQuantityInput.focus();
                }
                return;
            }
            const existingItemIndex = cartItems.findIndex(item => item.product.id === currentSelectedProductForDirectAdd.id);
            if (existingItemIndex !== -1) {
                const newQuantity = cartItems[existingItemIndex].quantity + quantity;
                if (newQuantity > stock) {
                    if(addProductError) {
                        addProductError.textContent = `Tổng số lượng sản phẩm "${currentSelectedProductForDirectAdd.name}" trong hóa đơn (${newQuantity}) sẽ vượt quá tồn kho (${stock}).`;
                        addProductError.style.display = 'block';
                    }
                    return;
                }
                cartItems[existingItemIndex].quantity = newQuantity;
            } else {
                cartItems.push({
                    product: currentSelectedProductForDirectAdd,
                    quantity: quantity
                });
            }
            updateCartDisplay();
            if(productSearchInput) productSearchInput.value = '';
            if(productQuantityInput) {
                productQuantityInput.value = 1;
                productQuantityInput.removeAttribute('max');
            }
            currentSelectedProductForDirectAdd = null;
            if(selectedProductIdInput) selectedProductIdInput.value = '';
            if(selectedProductDetailsDiv) {
                selectedProductDetailsDiv.style.display = 'none';
                selectedProductDetailsDiv.innerHTML = '';
            }
            if(productSearchInput) productSearchInput.focus();
        });
    }

    // --- NEW: Logic cho Customer Autocomplete ---
if (customerSearchInput && customerAutocompleteSuggestions && selectedCustomerIdInput && selectedCustomerDetailsDiv) {
    let customerSearchTimeout; // Để debounce việc gọi API

    customerSearchInput.addEventListener('input', function () {
        const searchTerm = this.value.trim().toLowerCase();
        customerAutocompleteSuggestions.innerHTML = '';
        currentSelectedCustomerForDirectAdd = null;
        selectedCustomerIdInput.value = '';
        selectedCustomerDetailsDiv.style.display = 'none';
        selectedCustomerDetailsDiv.innerHTML = '';

        resetCustomerPointsSection();
        // Không cần updateTotals() ngay ở đây vì chưa có gì thay đổi tổng tiền

        if (searchTerm.length < 2) { // Có thể đặt điều kiện độ dài tối thiểu, ví dụ 2 ký tự
            customerAutocompleteSuggestions.style.display = 'none';
            if (customerSearchTimeout) clearTimeout(customerSearchTimeout); // Hủy timeout nếu có
            return;
        }

        // Debounce: Chờ người dùng gõ xong rồi mới gọi API
        if (customerSearchTimeout) clearTimeout(customerSearchTimeout);

        customerSearchTimeout = setTimeout(async () => {
            try {
                // Hiển thị trạng thái đang tải (tùy chọn)
                customerAutocompleteSuggestions.innerHTML = '<div class="list-group-item text-muted py-2 px-3">Đang tìm kiếm...</div>';
                customerAutocompleteSuggestions.style.display = 'block';

                const response = await fetch(`/api/customers/search?term=${encodeURIComponent(searchTerm)}`);
                if (!response.ok) {
                    // Có thể hiển thị lỗi cụ thể hơn từ response.status hoặc response.text()
                    throw new Error('Lỗi khi tìm kiếm khách hàng.');
                }
                const filteredCustomers = await response.json(); // Giả sử API trả về mảng CustomerResponseDTO

                customerAutocompleteSuggestions.innerHTML = ''; // Xóa "Đang tìm kiếm..."

                if (filteredCustomers.length > 0) {
                    filteredCustomers.slice(0, 10).forEach(customer => { // customer ở đây là CustomerResponseDTO
                        const suggestionItem = document.createElement('a');
                        suggestionItem.href = '#';
                        suggestionItem.className = 'list-group-item list-group-item-action py-2 px-3';
                        // Đảm bảo customer object từ API có các thuộc tính 'name' và 'phone'
                        // hoặc điều chỉnh cho phù hợp với cấu trúc DTO của bạn
                        suggestionItem.innerHTML = `
                            <h6 class="mb-1">${customer.name}</h6> 
                            <small class="text-muted">Phone: ${customer.phone || 'N/A'}</small>
                        `;
                        suggestionItem.addEventListener('click', function (e) {
                            e.preventDefault();
                            // Khi chọn, customer object này có thể cần thêm thông tin (như memberRank, availablePoints)
                            // mà API /api/customers/search có thể chưa trả về hết.
                            // Hàm selectCustomer có thể cần gọi thêm API chi tiết khách hàng /api/customers/{id}
                            // hoặc API /search của bạn trả về đủ thông tin cần thiết ban đầu.
                            // Tạm thời, ta giả định `customer` object từ search API có đủ `id`, `name`, `phone`.
                            selectCustomer(customer); 
                        });
                        customerAutocompleteSuggestions.appendChild(suggestionItem);
                    });
                    customerAutocompleteSuggestions.style.display = 'block';
                } else {
                    customerAutocompleteSuggestions.innerHTML = '<div class="list-group-item text-muted py-2 px-3">Không tìm thấy khách hàng.</div>';
                    customerAutocompleteSuggestions.style.display = 'block';
                }
            } catch (error) {
                console.error('Lỗi khi tìm kiếm khách hàng:', error);
                customerAutocompleteSuggestions.innerHTML = '<div class="list-group-item text-danger py-2 px-3">Lỗi tìm kiếm. Vui lòng thử lại.</div>';
                customerAutocompleteSuggestions.style.display = 'block';
            }
        }, 300); // Thời gian chờ (ms) trước khi gọi API, ví dụ 300ms
    });
}

// NEW: Function to handle selecting a customer
// Hàm này vẫn giữ nguyên, nhưng đối tượng `customer` truyền vào giờ đây đến từ kết quả API search
async function selectCustomer(customer) { // `customer` ở đây là object từ kết quả API search
    currentSelectedCustomerForDirectAdd = customer; // Lưu lại để có thể dùng sau nếu cần
    if (customerSearchInput) customerSearchInput.value = customer.name;
    if (selectedCustomerIdInput) selectedCustomerIdInput.value = customer.id; // Quan trọng: Lấy ID từ customer object

    if (selectedCustomerDetailsDiv) {
        selectedCustomerDetailsDiv.innerHTML = `
            <strong>Đã chọn:</strong> ${customer.name} (Phone: ${customer.phone || 'N/A'})
        `;
        selectedCustomerDetailsDiv.style.display = 'block';
    }

    if (customerAutocompleteSuggestions) {
        customerAutocompleteSuggestions.innerHTML = '';
        customerAutocompleteSuggestions.style.display = 'none';
    }

    // Bây giờ mới fetch chi tiết điểm của khách hàng bằng ID
    // Hàm fetchAndDisplayCustomerPoints sẽ gọi API /api/customers/{customerId}
    await fetchAndDisplayCustomerPoints(customer.id); 
    // updateTotals(); // fetchAndDisplayCustomerPoints đã gọi updateTotals rồi
}

// NEW: Listener to hide customer suggestions when clicking outside (giữ nguyên)
if (customerSearchInput && customerAutocompleteSuggestions) {
    document.addEventListener('click', function(event) {
        if (!customerSearchInput.contains(event.target) && !customerAutocompleteSuggestions.contains(event.target)) {
            if (customerAutocompleteSuggestions) customerAutocompleteSuggestions.style.display = 'none';
        }
    });
}

// Function to fetch and display customer points (Hàm này vẫn dựa vào API /api/customers/{id})
// Đảm bảo hàm này vẫn hoạt động đúng khi được gọi từ selectCustomer
async function fetchAndDisplayCustomerPoints(customerId) {
    resetCustomerPointsSection(); 

    if (customerId && customerId !== "") {
        if (customerRankDisplay) customerRankDisplay.textContent = 'Đang tải...';
        if (customerPointsDisplay) customerPointsDisplay.textContent = 'Đang tải...';
        if (customerPointDetailsSection) customerPointDetailsSection.style.display = 'flex';

        try {
            const response = await fetch(`/api/customers/${customerId}`); // API này lấy chi tiết khách hàng, bao gồm điểm
            if (!response.ok) {
                let errorMsg = `Lỗi ${response.status}: Không thể lấy thông tin khách hàng.`;
                if (response.status === 404) errorMsg = 'Không tìm thấy khách hàng.';
                throw new Error(errorMsg);
            }
            const customerData = await response.json(); // Đây là customer DTO đầy đủ

            if (customerRankDisplay) customerRankDisplay.textContent = (customerData.memberRank && customerData.memberRank.name) ? customerData.memberRank.name : 'Chưa có hạng';
            if (customerPointsDisplay) customerPointsDisplay.textContent = customerData.availablePoints !== null ? customerData.availablePoints.toLocaleString('vi-VN') : '0';
            if (pointsToRedeemInput) {
                pointsToRedeemInput.value = 0;
                pointsToRedeemInput.max = customerData.availablePoints || 0;
            }
        } catch (error) {
            console.error('Lỗi khi lấy chi tiết khách hàng:', error);
            if (customerRankDisplay) customerRankDisplay.textContent = 'Lỗi tải';
            if (customerPointsDisplay) customerPointsDisplay.textContent = 'Lỗi tải';
            // alert(error.message || 'Không thể tải thông tin điểm của khách hàng.'); // Có thể comment dòng này nếu không muốn alert
        }
    }
    updateTotals();
}
    
    // MODIFIED: Function to reset customer points section
    function resetCustomerPointsSection() {
        if (customerPointDetailsSection) customerPointDetailsSection.style.display = 'none';
        if (customerRankDisplay) customerRankDisplay.textContent = '';
        if (customerPointsDisplay) customerPointsDisplay.textContent = '';
        if (pointsToRedeemInput) {
            pointsToRedeemInput.value = 0;
            pointsToRedeemInput.removeAttribute('max');
        }
        if (pointsRedeemError) pointsRedeemError.style.display = 'none';
    }

    // MODIFIED: Function to fetch and display customer points
    async function fetchAndDisplayCustomerPoints(customerId) {
        resetCustomerPointsSection(); // Reset first

        if (customerId && customerId !== "") {
            if (customerRankDisplay) customerRankDisplay.textContent = 'Đang tải...';
            if (customerPointsDisplay) customerPointsDisplay.textContent = 'Đang tải...';
            if (customerPointDetailsSection) customerPointDetailsSection.style.display = 'flex';

            try {
                const response = await fetch(`/api/customers/${customerId}`);
                if (!response.ok) {
                    let errorMsg = `Lỗi ${response.status}: Không thể lấy thông tin khách hàng.`;
                    if (response.status === 404) errorMsg = 'Không tìm thấy khách hàng.';
                    throw new Error(errorMsg);
                }
                const customerData = await response.json();

                if (customerRankDisplay) customerRankDisplay.textContent = (customerData.memberRank && customerData.memberRank.name) ? customerData.memberRank.name : 'Chưa có hạng';
                if (customerPointsDisplay) customerPointsDisplay.textContent = customerData.availablePoints !== null ? customerData.availablePoints.toLocaleString('vi-VN') : '0';
                if (pointsToRedeemInput) {
                    pointsToRedeemInput.value = 0; // Default to 0 when new customer is selected
                    pointsToRedeemInput.max = customerData.availablePoints || 0;
                }
            } catch (error) {
                console.error('Lỗi khi lấy chi tiết khách hàng:', error);
                if (customerRankDisplay) customerRankDisplay.textContent = 'Lỗi tải';
                if (customerPointsDisplay) customerPointsDisplay.textContent = 'Lỗi tải';
                alert(error.message || 'Không thể tải thông tin điểm của khách hàng.');
            }
        }
        updateTotals(); // Important to update totals after points info is loaded/reset
    }

    // --- Event Listener cho việc chọn Khách hàng (OLD - REMOVE OR COMMENT OUT) ---
    // if (customerSelect) {
    //     customerSelect.addEventListener('change', async function () {
    //         const customerId = this.value;
    //         await fetchAndDisplayCustomerPoints(customerId);
    //     });
    // }
    
    // Event Listener for "Walk-in" or empty customer selection
    if (customerSearchInput) {
        customerSearchInput.addEventListener('blur', function() {
            // If the input is cleared and loses focus, and no customer is selected, treat as walk-in
            if (this.value.trim() === '' && !selectedCustomerIdInput.value) {
                resetCustomerPointsSection();
                currentSelectedCustomerForDirectAdd = null;
                selectedCustomerIdInput.value = ''; // Ensure it's empty for walk-in
                selectedCustomerDetailsDiv.style.display = 'none';
                updateTotals();
            }
        });
    }


    // --- Event Listener cho việc nhập Điểm muốn dùng ---
    if (pointsToRedeemInput) {
        pointsToRedeemInput.addEventListener('input', function () {
            validateAndProcessPointsRedeemed();
        });
        pointsToRedeemInput.addEventListener('change', function () {
            validateAndProcessPointsRedeemed(true); 
        });
    }
    
    function validateAndProcessPointsRedeemed(resetIfEmpty = false) {
        if (!pointsToRedeemInput || !pointsRedeemError) return;

        let points = parseInt(pointsToRedeemInput.value);
        const maxPoints = parseInt(pointsToRedeemInput.max) || 0;
        let errorMessage = ""; // Lỗi của riêng hàm này

        if (pointsToRedeemInput.value.trim() === '' && resetIfEmpty) {
            pointsToRedeemInput.value = 0;
            points = 0;
        } else if (pointsToRedeemInput.value.trim() !== '' && (isNaN(points) || points < 0)) {
            errorMessage = "Số điểm phải là số không âm.";
        } else if (points > 0 && points % 1000 !== 0) {
            errorMessage = "Số điểm sử dụng phải là bội số của 1000.";
        } else if (points > maxPoints) {
            errorMessage = `Chỉ có thể dùng tối đa ${maxPoints.toLocaleString('vi-VN')} điểm khả dụng.`;
        }

        // Hiển thị lỗi của hàm này. updateTotals() có thể ghi đè nếu có lỗi vượt quá tổng tiền.
        pointsRedeemError.textContent = errorMessage;
        pointsRedeemError.style.display = errorMessage ? 'block' : 'none';
        
        updateTotals(); // Luôn gọi updateTotals để kiểm tra lại tất cả, bao gồm cả lỗi vượt quá tổng tiền
    }

    // --- Khởi tạo ban đầu ---
    if (invoiceTaxRateInput) {
        invoiceTaxRateInput.value = DEFAULT_INVOICE_TAX_PERCENTAGE;
        invoiceTaxRateInput.addEventListener('input', function() {
            let taxVal = parseFloat(this.value);
            if (this.value.trim() !== '' && isNaN(taxVal)) {
            } else if (taxVal < 0) {
                this.value = 0;
            } else if (taxVal > 100) {
                this.value = 100;
            }
            updateTotals();
        });
        invoiceTaxRateInput.addEventListener('change', function() {
            if (this.value.trim() === '') {
                this.value = DEFAULT_INVOICE_TAX_PERCENTAGE;
            }
            let taxVal = parseFloat(this.value);
            if (isNaN(taxVal) || taxVal < 0) this.value = 0;
            if (taxVal > 100) this.value = 100;
            updateTotals();
        });
    }

    // --- Khởi tạo ban đầu ---
    function initializeInvoiceItems() {
        if (initialInvoiceItems && initialInvoiceItems.length > 0) {
            initialInvoiceItems.forEach(initItem => {
                // ... (logic for initializing items based on initialInvoiceItems)
                 if (initItem.productId === undefined || initItem.productId === null) {
                    console.warn('Một initialItem không có productId:', initItem);
                    return; 
                }
                const productData = availableProducts.find(p => String(p.id) === String(initItem.productId));
                if (productData) {
                    const productDetails = {
                        ...productData,
                        name: initItem.productNameSnapshot || productData.name,
                        price: initItem.unitPriceSnapshot !== undefined ? initItem.unitPriceSnapshot : productData.price
                    };
                    
                    const alreadyInCartIndex = cartItems.findIndex(ci => String(ci.product.id) === String(initItem.productId));

                    if(alreadyInCartIndex === -1) {
                         cartItems.push({
                            product: productDetails, 
                            quantity: initItem.quantity
                        });
                    } else {
                        console.log(`Item ${initItem.productId} đã có trong giỏ hàng từ initial items, bỏ qua.`);
                    }
                } else {
                    console.warn(`Không tìm thấy thông tin sản phẩm cho productId: ${initItem.productId} từ initialInvoiceItems.`);
                }
            });
        }
        updateCartDisplay(); // This will call updateTotals()
    }
    
    initializeInvoiceItems();
    
    // Initial call to update totals, especially for tax label
    if (cartItems.length === 0 && (productTable || invoiceTaxRateInput)) { 
        updateTotals(); 
    }

    // --- Xử lý Submit Form ---
    if (invoiceForm) {
        invoiceForm.addEventListener('submit', function (event) {
            let pointsToRedeemNum = 0;
            let maxPointsForCustomer = 0;
            let currentPointsErrorMessage = "";
            
            if (pointsToRedeemInput && pointsToRedeemInput.value.trim() !== '') {
                pointsToRedeemNum = parseInt(pointsToRedeemInput.value);
                maxPointsForCustomer = parseInt(pointsToRedeemInput.max) || 0;

                if (isNaN(pointsToRedeemNum) || pointsToRedeemNum < 0) {
                    currentPointsErrorMessage = 'Số điểm sử dụng không hợp lệ (phải là số không âm).';
                } else if (pointsToRedeemNum > 0 && pointsToRedeemNum % 1000 !== 0) {
                    currentPointsErrorMessage = 'Số điểm sử dụng phải là bội số của 1000.';
                } else if (pointsToRedeemNum > maxPointsForCustomer) {
                    currentPointsErrorMessage = `Số điểm sử dụng vượt quá ${maxPointsForCustomer} điểm khả dụng.`;
                }
            }
            
            if (currentPointsErrorMessage) {
                alert(currentPointsErrorMessage);
                event.preventDefault();
                pointsToRedeemInput.focus();
                return;
            }

            // Tính toán lại tổng tiền trước khi trừ điểm để validate
            let currentSubtotal = 0;
            cartItems.forEach(item => {
                currentSubtotal += item.product.price * item.quantity;
            });
            let currentTaxPercentage = DEFAULT_INVOICE_TAX_PERCENTAGE;
            if (invoiceTaxRateInput && invoiceTaxRateInput.value.trim() !== "") {
                let parsedTax = parseFloat(invoiceTaxRateInput.value);
                if (!isNaN(parsedTax) && parsedTax >= 0 && parsedTax <= 100) {
                    currentTaxPercentage = parsedTax;
                }
            }
            const currentTaxAmount = currentSubtotal * (currentTaxPercentage / 100);
            const totalPayableBeforePoints = currentSubtotal + currentTaxAmount;

            const redeemedAmountEquivalentOnSubmit = pointsToRedeemNum;

            if (redeemedAmountEquivalentOnSubmit > totalPayableBeforePoints && totalPayableBeforePoints > 0) {
                alert(`Số điểm quy đổi (${formatCurrency(redeemedAmountEquivalentOnSubmit)}) không được vượt quá tổng tiền hóa đơn (${formatCurrency(totalPayableBeforePoints)}). Vui lòng điều chỉnh lại.`);
                event.preventDefault();
                pointsToRedeemInput.focus();
                return;
            }
            
            if (cartItems.length === 0) {
                alert('Vui lòng thêm ít nhất một sản phẩm vào hóa đơn trước khi tạo.');
                event.preventDefault();
                return;
            }
            // Ensure customerId is set or explicitly empty for walk-in
            if (!selectedCustomerIdInput.value) {
                 console.log("No customer selected, submitting as walk-in.");
                 // selectedCustomerIdInput.value will be empty, which is fine for walk-in backend logic.
            }
        });
    }
});