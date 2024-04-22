function toggleMenu() {
    var menu = document.getElementById("sideMenu");
    
//    new
    var content = document.querySelector('.main-content');
    
    menu.classList.toggle('menu-active');
    
//    new
    content.classList.toggle('content-expand');
}

// Formatting price amounts to be two decimal places
const priceElements = document.querySelectorAll('.price-element');
priceElements.forEach(element => {
  const price = parseFloat(element.textContent);
  element.textContent = '$ ' + price.toFixed(2);
});
