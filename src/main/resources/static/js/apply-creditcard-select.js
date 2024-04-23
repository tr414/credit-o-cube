document.addEventListener("DOMContentLoaded", function() {
    const cards = document.querySelectorAll('.card-info');

    cards.forEach(card => {
        card.addEventListener('click', function() {
            cards.forEach(c => c.classList.remove('selected')); // Remove the 'selected' class from all cards
            card.classList.add('selected'); // Add 'selected' class to the clicked card
        });
    });
});
