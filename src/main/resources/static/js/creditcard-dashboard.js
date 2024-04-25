document.addEventListener('click', function (e) {
  if (e.target && e.target.classList.contains('action-btn')) {
    const cardId = e.target.getAttribute('data-card-id');
    
    const dropdown = document.getElementById(cardId);

    // Check if the dropdown for this button is already visible
    const isDropdownVisible = dropdown.style.display === 'block';

    // Close all dropdowns to reset the state
    closeAllDropdowns();

    // If the dropdown was not visible, show it
    if (!isDropdownVisible) {
      const rect = e.target.getBoundingClientRect();
      dropdown.style.position = 'absolute';
      dropdown.style.visibility = 'hidden'; // Hide dropdown to measure
      dropdown.style.display = 'block'; // Display block to measure it

      let left = rect.left + window.scrollX;
      if (left + dropdown.offsetWidth > window.innerWidth) {
        left -= (left + dropdown.offsetWidth) - window.innerWidth + 20; // Ensure dropdown is within viewport
      }

      dropdown.style.left = left + 'px';
      dropdown.style.top = (rect.bottom + window.scrollY) + 'px';
      dropdown.style.visibility = 'visible'; // Make dropdown visible
    }
    e.stopPropagation(); // Prevent the click from being captured by window.onclick
  }
}, false);

// Function to close all dropdowns
function closeAllDropdowns() {
  document.querySelectorAll('.actions-dropdown').forEach(function(dropdown) {
    dropdown.style.display = 'none';
  });
}

// Close the dropdown if the user clicks outside of it
window.onclick = function(event) {
  closeAllDropdowns();
};
