function toggleProducts(button) {
    // Find the parent container of the button and the corresponding product list
    const productListContainer = button.closest('td').querySelector('.productList');
    const products = productListContainer.querySelectorAll('li');
    
    // Check the current state based on the button text
    const isExpanded = button.textContent === 'See Less...';

    // Toggle visibility of products beyond the first 3
    products.forEach((product, index) => {
        if (index >= 3) {
            product.classList.toggle('hidden', isExpanded); // Hide if expanded, show otherwise
        }
    });

    // Update button text
    button.textContent = isExpanded ? 'See More...' : 'See Less...';
}
