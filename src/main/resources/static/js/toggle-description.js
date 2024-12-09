function toggleDescription(button) {
    // Find the parent div that contains the description and button
    const descriptionContainer = button.closest('td');
    
    // Find the short and full description elements within this container
    const shortDesc = descriptionContainer.querySelector('.shortDesc');
    const fullDesc = descriptionContainer.querySelector('.fullDesc');
    
    // Toggle visibility of short and full descriptions
    fullDesc.classList.toggle('hidden');
    shortDesc.classList.toggle('hidden');
    
    // Change button text based on whether it's expanded or collapsed
    if (button.textContent === 'Read More...') {
        button.textContent = 'Read Less...';
    } else {
        button.textContent = 'Read More...';
    }
}
