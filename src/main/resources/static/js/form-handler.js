document.addEventListener('DOMContentLoaded', function() {
    var accountTypeRadios = document.querySelectorAll('input[name="accountType"]');
    var internalAccountSelect = document.getElementById('internalAccountSelect');
    var externalAccountInput = document.getElementById('externalAccountInput');

    function toggleAccountInput() {
        if (document.getElementById('internal').checked) {
            internalAccountSelect.style.display = '';
            externalAccountInput.style.display = 'none';
        } else if (document.getElementById('external').checked) {
            internalAccountSelect.style.display = 'none';
            externalAccountInput.style.display = '';
        }
    }

    accountTypeRadios.forEach(function(radio) {
        radio.addEventListener('change', toggleAccountInput);
    });

    toggleAccountInput(); // Initial call to set the correct display on page load
});
