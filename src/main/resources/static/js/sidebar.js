function toggleMenu() {
    var menu = document.getElementById("sideMenu");
    
//    new
    var content = document.querySelector('.main-content');
    
    menu.classList.toggle('menu-active');
    
//    new
    content.classList.toggle('content-expand');
}

// Function to format a number with commas and two decimal places
function formatPrice(number) {
  // Handle non-numeric input
  if (isNaN(number)) {
    return 'Invalid Price';
  }

  // Convert to a fixed point string with two decimals
  const formattedPrice = number.toFixed(2);

  // Split the number into integer and decimal parts
  const [wholePart, decimalPart] = formattedPrice.split('.');

  // Add commas to the integer part (reverse the string for easier processing)
  const reversedInteger = wholePart.split('').reverse().join('');
  const formattedInteger = reversedInteger.replace(/(\d{3})(?!$)/g, '$1,');
  const reversedFormattedInteger = formattedInteger.split('').reverse().join('');

  // Combine the formatted integer and decimal parts
  return '$' + reversedFormattedInteger + '.' + decimalPart;
}

// Apply formatting to price elements
const priceElements = document.querySelectorAll('.price-element');
priceElements.forEach(element => {
  const price = parseFloat(element.textContent);
  const formattedPrice = formatPrice(price);
  element.textContent = formattedPrice;
});

function printAndToggleMenu() {
//	setFilterToAllAndSubmit();
  toggleMenu(); // Assuming toggleMenu() is already defined and toggles the sidebar visibility.
  window.print(); // This will trigger the print dialog.
}



//function setFilterToAllAndSubmit() {
//    // Set the transaction type filter to 'all'
//    var transactionTypeSelect = document.querySelector('select[name="transactionType"]');
//    if (transactionTypeSelect) {
//        transactionTypeSelect.value = 'all';
//    }
//
//    // Find the form that contains the transaction type select and submit it
//    var filterTypeForm = document.getElementById('filterTypeForm');
//    if (filterTypeForm) {
//		console.log("submitting form...");
//        filterTypeForm.submit();
//        console.log("form submitted");
//    }
//    window.print();
//}



//function setFilterToAllAndSubmit() {
//    // Set the transaction type filter to 'all'
//    var transactionTypeSelect = document.querySelector('select[name="transactionType"]');
//    if (transactionTypeSelect) {
//        transactionTypeSelect.value = 'all';
//    }
//
//    // Find the form that contains the transaction type select and submit it
//    var filterTypeForm = document.getElementById('filterTypeForm');
//    if (filterTypeForm) {
//		console.log("Submitting form...");
//        filterTypeForm.submit();
//        console.log("Form submitted");
//
//        // Add a delay before printing
//        setTimeout(function() {
//			console.log("Opening print dialog...");
//            window.print(); // This will trigger the print dialog.
//            console.log(window.print());
//            console.log("opened print dialog");
//        }, 10); // Adjust the delay time (in milliseconds) as needed
//    }
//}




