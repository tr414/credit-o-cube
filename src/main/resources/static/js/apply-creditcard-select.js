document.addEventListener("DOMContentLoaded", function() {
    const cards = document.querySelectorAll('.card-info');

    cards.forEach(card => {
        card.addEventListener('click', function() {
            cards.forEach(c => c.classList.remove('selected')); // Remove the 'selected' class from all cards
            card.classList.add('selected'); // Add 'selected' class to the clicked card
        });
    });
});

document.addEventListener('DOMContentLoaded', function() {
  var requirementsElements = document.querySelectorAll('.requirements-list');

  requirementsElements.forEach(function(element) {
    var requirements = element.getAttribute('data-requirements');
    if (requirements) {
      // Split the requirements into sentences. This is a simple approach
      // and might need to be adjusted depending on how your sentences are structured.
      var sentences = requirements.match(/[^\.!\?]+[\.!\?]+/g);

      if (sentences) {
        sentences.forEach(function(sentence) {
          var li = document.createElement('li');
          li.textContent = sentence.trim();
          element.appendChild(li);
        });
      }
    }
  });
});

document.querySelectorAll('.card-info input[type="radio"]').forEach(radio => {
  radio.addEventListener('change', function() {
    document.getElementById('creditCardLimit').scrollIntoView({ behavior: 'smooth' });
  });
});


