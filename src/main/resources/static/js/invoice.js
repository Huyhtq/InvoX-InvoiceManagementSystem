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
    const taxElement = document.getElementById('tax'); // Sẽ là TỔNG THUẾ
    const totalElement = document.getElementById('total');
    const customerSelect = document.getElementById('customerSelect');

    // --- Elements cho Logic Thêm Sản Phẩm Mới ---
    const productSearchInput = document.getElementById('productSearchInput');
    const autocompleteSuggestions = document.getElementById('autocompleteSuggestions');
    const selectedProductIdInput = document.getElementById('selectedProductId');
    const selectedProductDetailsDiv = document.getElementById('selectedProductDetails');
    const productQuantityInput = document.getElementById('productQuantityInput');
    const invoiceTaxRateInput = document.getElementById('invoiceTaxRateInput'); // Input cho % thuế chung
    const invoiceTaxLabel = document.getElementById('invoiceTaxLabel'); // Nhãn hiển thị % thuế chung
    const directAddProductBtn = document.getElementById('directAddProductBtn');
    const addProductError = document.getElementById('addProductError');

    // Elements mới cho thông tin điểm của khách hàng
    const customerPointDetailsSection = document.getElementById('customerPointDetailsSection');
    const customerRankDisplay = document.getElementById('customerRankDisplay');
    const customerPointsDisplay = document.getElementById('customerPointsDisplay');
    const pointsToRedeemInput = document.getElementById('pointsToRedeemInput');
    const pointsRedeemError = document.getElementById('pointsRedeemError');

    // --- Dữ liệu từ Thymeleaf (đã được parse trong HTML) ---
    const availableProducts = window.allProductsData || [];
    const initialInvoiceItems = window.initialInvoiceItems || [];
    const DEFAULT_INVOICE_TAX_PERCENTAGE = 10; // Thuế mặc định là 10%

    // --- Trạng thái nội bộ ---
    let cartItems = []; // Mỗi item: { product: {...}, quantity: X }
    let currentSelectedProductForDirectAdd = null;

    // --- Helper Functions ---
    function formatCurrency(amount) {
        if (amount === null || isNaN(amount)) {
            return '0 ₫';
        }
        return Number(amount).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    // --- Quản lý Giỏ hàng và Hiển thị ---
    function updateCartDisplay() {
        // Kiểm tra các element DOM cần thiết
        if (!productTable || !productTable.querySelector('tbody') || !emptyProductList) {
            console.warn("Một số element DOM của bảng sản phẩm không tìm thấy. updateCartDisplay() có thể không hoạt động đúng.");
            // Không gọi updateTotals() nếu các element chính bị thiếu để tránh lỗi thêm
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
            
            const subtotalCell = row.cells[3]; // Tên(0) - Giá(1) - SL(2) - Thành Tiền(3)
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
        if (!subtotalElement || !taxElement || !totalElement) {
            // console.warn("Một số element hiển thị tổng tiền không tìm thấy. updateTotals() có thể không hoạt động đúng.");
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
             } else {
                 // Nếu giá trị nhập không hợp lệ nhưng không trống, giữ lại giá trị đó trong input
                 // nhưng dùng giá trị mặc định để tính. Hoặc có thể báo lỗi.
                 // Tạm thời, nếu nhập sai thì vẫn giữ giá trị sai trong ô input nhưng tính bằng default
                 // Hoặc tốt hơn là reset về default nếu sai:
                 // invoiceTaxRateInput.value = DEFAULT_INVOICE_TAX_PERCENTAGE;
                 // currentInvoiceTaxPercentage = DEFAULT_INVOICE_TAX_PERCENTAGE;
                 // Để đơn giản, nếu nhập sai thì cứ dùng default, input giữ nguyên
             }
        } else if (invoiceTaxRateInput && invoiceTaxRateInput.value.trim() === '') {
            // Nếu để trống, dùng mặc định
            currentInvoiceTaxPercentage = DEFAULT_INVOICE_TAX_PERCENTAGE;
        }


        const invoiceTaxRateValue = currentInvoiceTaxPercentage / 100;
        const totalInvoiceTax = subtotal * invoiceTaxRateValue;
        
        let amountBeforePointsRedeemed = subtotal + totalInvoiceTax;

        let pointsToRedeemValue = 0;
        if (pointsToRedeemInput && pointsToRedeemInput.value) {
            const rawPoints = parseInt(pointsToRedeemInput.value);
            const maxPoints = parseInt(pointsToRedeemInput.max) || 0;
            if (!isNaN(rawPoints) && rawPoints >= 0 && rawPoints % 1000 === 0 && rawPoints <= maxPoints ) {
                 pointsToRedeemValue = rawPoints;
            }
        }
        
        const finalAmount = amountBeforePointsRedeemed - pointsToRedeemValue;

        subtotalElement.textContent = formatCurrency(subtotal);
        taxElement.textContent = formatCurrency(totalInvoiceTax);
        totalElement.textContent = formatCurrency(finalAmount >= 0 ? finalAmount : 0);

        if (invoiceTaxLabel) {
            invoiceTaxLabel.textContent = `Thuế (${currentInvoiceTaxPercentage.toFixed(0)}%):`;
        }
    }
    
    // --- Logic Thêm Sản Phẩm Mới ---
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

    if (productSearchInput && autocompleteSuggestions) {
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
            // Không cần reset invoiceTaxRateInput ở đây, nó là thuế chung
            if(productSearchInput) productSearchInput.focus();
        });
    }

    // --- Khởi tạo ban đầu ---
    function initializeInvoiceItems() {
        if (initialInvoiceItems && initialInvoiceItems.length > 0) {
            initialInvoiceItems.forEach(initItem => {
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
        updateCartDisplay();
    }
    
    // --- Event Listener cho việc chọn Khách hàng ---
    if (customerSelect) {
        customerSelect.addEventListener('change', async function () {
            const customerId = this.value;
            
            // Luôn reset thông tin điểm và input điểm khi khách hàng thay đổi (kể cả chọn "Khách lẻ")
            if (customerPointDetailsSection) customerPointDetailsSection.style.display = 'none';
            if (customerRankDisplay) customerRankDisplay.textContent = '';
            if (customerPointsDisplay) customerPointsDisplay.textContent = '';
            if (pointsToRedeemInput) {
                pointsToRedeemInput.value = 0;
                pointsToRedeemInput.removeAttribute('max');
            }
            if(pointsRedeemError) pointsRedeemError.style.display = 'none';

            if (customerId && customerId !== "") {
                if (customerRankDisplay) customerRankDisplay.textContent = 'Đang tải...';
                if (customerPointsDisplay) customerPointsDisplay.textContent = 'Đang tải...';
                if (customerPointDetailsSection) customerPointDetailsSection.style.display = 'flex'; // Hiện trước khi có data

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
                        pointsToRedeemInput.value = 0;
                        pointsToRedeemInput.max = customerData.availablePoints || 0;
                    }
                } catch (error) {
                    console.error('Lỗi khi lấy chi tiết khách hàng:', error);
                    if (customerRankDisplay) customerRankDisplay.textContent = 'Lỗi tải';
                    if (customerPointsDisplay) customerPointsDisplay.textContent = 'Lỗi tải';
                    // Không ẩn section để người dùng biết có lỗi
                    alert(error.message || 'Không thể tải thông tin điểm của khách hàng.');
                }
            }
            updateTotals(); 
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
        if (!pointsToRedeemInput) return;

        let points = parseInt(pointsToRedeemInput.value);
        const maxPoints = parseInt(pointsToRedeemInput.max) || 0;
        let errorMessage = "";

        if (pointsToRedeemInput.value.trim() === '' && resetIfEmpty) {
            pointsToRedeemInput.value = 0;
            points = 0;
        } else if (pointsToRedeemInput.value.trim() !== '' && (isNaN(points) || points < 0)) { // Chỉ validate nếu có nhập gì đó mà không phải số hoặc là số âm
            errorMessage = "Số điểm phải là số không âm.";
             // Không tự động reset về 0 nếu người dùng đang gõ sai, chỉ báo lỗi
        } else if (points > 0 && points % 1000 !== 0) { // Chỉ kiểm tra bội số 1000 nếu điểm > 0
            errorMessage = "Số điểm sử dụng phải là bội số của 1000.";
        } else if (points > maxPoints) {
            errorMessage = `Chỉ có thể dùng tối đa ${maxPoints.toLocaleString('vi-VN')} điểm.`;
        }

        if (pointsRedeemError) {
            pointsRedeemError.textContent = errorMessage;
            pointsRedeemError.style.display = errorMessage ? 'block' : 'none';
        }
        updateTotals();
    }

    // --- Khởi tạo khi tải trang ---
    if (invoiceTaxRateInput) {
        invoiceTaxRateInput.value = DEFAULT_INVOICE_TAX_PERCENTAGE;
        // Event listeners cho invoiceTaxRateInput
        invoiceTaxRateInput.addEventListener('input', function() {
            let taxVal = parseFloat(this.value);
            if (this.value.trim() !== '' && isNaN(taxVal)) { // Nếu nhập chữ
                 // Có thể không làm gì hoặc xóa ký tự không hợp lệ
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
             let taxVal = parseFloat(this.value); // Validate lại sau khi có thể đã set default
             if (isNaN(taxVal) || taxVal < 0) this.value = 0;
             if (taxVal > 100) this.value = 100;
            updateTotals();
        });
    }

    initializeInvoiceItems(); // Sẽ gọi updateCartDisplay -> updateTotals
    
    // Nếu không có initialItems, updateTotals có thể chưa được gọi với giá trị thuế mặc định
    // để cập nhật nhãn thuế. Gọi lại ở đây.
    if (cartItems.length === 0 && (productTable || invoiceTaxRateInput)) { 
        updateTotals(); 
    }


    // --- Xử lý Submit Form ---
    if(invoiceForm) {
        invoiceForm.addEventListener('submit', function (event) {
            // Validate lần cuối điểm sử dụng trước khi submit
            if (pointsToRedeemInput && pointsToRedeemInput.value.trim() !== '') {
                const points = parseInt(pointsToRedeemInput.value);
                const maxPoints = parseInt(pointsToRedeemInput.max) || 0;
                if (isNaN(points) || points < 0 || points % 1000 !== 0 || points > maxPoints) {
                    alert('Số điểm sử dụng không hợp lệ. Vui lòng kiểm tra lại.');
                    event.preventDefault();
                    pointsToRedeemInput.focus();
                    return;
                }
            }

            if (cartItems.length === 0) {
                alert('Vui lòng thêm ít nhất một sản phẩm vào hóa đơn trước khi tạo.');
                event.preventDefault();
                return;
            }
            
            // Nếu input invoiceTaxRateInput không có thuộc tính `name` trong HTML, 
            // và bạn muốn gửi giá trị này lên server khi submit form truyền thống,
            // bạn cần tạo một input hidden ở đây.
            // Tuy nhiên, bạn đã thêm name="invoiceTaxRatePercentage" vào HTML, nên không cần đoạn này nữa.
            /*
            const existingHiddenTax = this.querySelector('input[name="invoiceTaxRatePercentage"]');
            if (!existingHiddenTax) { // Chỉ thêm nếu chưa có (tránh lặp lại khi form submit lại do lỗi)
                const hiddenInvoiceTaxRate = document.createElement('input');
                hiddenInvoiceTaxRate.type = 'hidden';
                hiddenInvoiceTaxRate.name = 'invoiceTaxRatePercentage';
                hiddenInvoiceTaxRate.value = invoiceTaxRateInput ? invoiceTaxRateInput.value : DEFAULT_INVOICE_TAX_PERCENTAGE;
                this.appendChild(hiddenInvoiceTaxRate);
            } else {
                existingHiddenTax.value = invoiceTaxRateInput ? invoiceTaxRateInput.value : DEFAULT_INVOICE_TAX_PERCENTAGE;
            }
            */
        });
    }
});