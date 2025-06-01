document.addEventListener('DOMContentLoaded', function() {
    // Modal elements
    const modal = document.getElementById('productModal');
    const addProductBtn = document.getElementById('addProductBtn');
    const closeBtn = document.querySelector('.close');
    const cancelAddBtn = document.getElementById('cancelAddBtn');
    const addToInvoiceBtn = document.getElementById('addToInvoiceBtn');
    
    // Product search elements
    const productSearch = document.getElementById('productSearch');
    const productSearchResults = document.getElementById('productSearchResults');
    const selectedProduct = document.getElementById('selectedProduct');
    const selectedProductName = document.getElementById('selectedProductName');
    const selectedProductPrice = document.getElementById('selectedProductPrice');
    const selectedProductSku = document.getElementById('selectedProductSku');
    
    // Quantity control
    const productQuantity = document.getElementById('productQuantity');
    const decreaseQuantity = document.getElementById('decreaseQuantity');
    const increaseQuantity = document.getElementById('increaseQuantity');
    
    let currentSelectedProduct = null;

    // Open modal
    addProductBtn.addEventListener('click', function() {
        modal.style.display = 'block';
        productSearch.focus();
        resetModal();
    });

    // Close modal
    function closeModal() {
        modal.style.display = 'none';
        resetModal();
    }

    closeBtn.addEventListener('click', closeModal);
    cancelAddBtn.addEventListener('click', closeModal);

    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            closeModal();
        }
    });

    // Reset modal state
    function resetModal() {
        productSearch.value = '';
        productSearchResults.innerHTML = '';
        selectedProduct.classList.add('hidden');
        currentSelectedProduct = null;
        addToInvoiceBtn.disabled = true;
        productQuantity.value = 1;
    }

    // Product search
    productSearch.addEventListener('input', function() {
        const searchValue = this.value.toLowerCase().trim();
        
        if (searchValue === '') {
            productSearchResults.innerHTML = '';
            return;
        }
        
        const filteredProducts = products.filter(product => 
            product.name.toLowerCase().includes(searchValue) || 
            product.sku.toLowerCase().includes(searchValue)
        );
        
        renderSearchResults(filteredProducts);
    });

    // Render search results
    function renderSearchResults(products) {
        productSearchResults.innerHTML = '';
        
        if (products.length === 0) {
            productSearchResults.innerHTML = '<li>Không tìm thấy sản phẩm</li>';
            return;
        }
        
        products.forEach(product => {
            const li = document.createElement('li');
            li.innerHTML = `
                <div><strong>${product.name}</strong></div>
                <div class="product-info">SKU: ${product.sku} | Giá: ${formatCurrency(product.price)}</div>
            `;
            li.addEventListener('click', () => selectProduct(product));
            productSearchResults.appendChild(li);
        });
    }

    // Select product
    function selectProduct(product) {
        currentSelectedProduct = product;
        selectedProductName.textContent = product.name;
        selectedProductPrice.textContent = formatCurrency(product.price);
        selectedProductSku.textContent = product.sku;
        selectedProduct.classList.remove('hidden');
        addToInvoiceBtn.disabled = false;
        
        // Clear search
        productSearch.value = '';
        productSearchResults.innerHTML = '';
    }

    // Quantity controls
    decreaseQuantity.addEventListener('click', function() {
        let value = parseInt(productQuantity.value);
        if (value > 1) {
            productQuantity.value = value - 1;
        }
    });

    increaseQuantity.addEventListener('click', function() {
        let value = parseInt(productQuantity.value);
        productQuantity.value = value + 1;
    });

    productQuantity.addEventListener('change', function() {
        let value = parseInt(this.value);
        if (isNaN(value) || value < 1) {
            this.value = 1;
        }
    });

    // Add product to invoice
    addToInvoiceBtn.addEventListener('click', function() {
        if (!currentSelectedProduct) return;
        
        const quantity = parseInt(productQuantity.value);
        
        // Call the function from script.js to add the product
        addProductToInvoice(currentSelectedProduct, quantity);
        
        // Close the modal
        closeModal();
    });
});