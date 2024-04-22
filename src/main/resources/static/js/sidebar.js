function toggleMenu() {
    var menu = document.getElementById("sideMenu");
    
//    new
    var content = document.querySelector('.main-content');
    
    menu.classList.toggle('menu-active');
    
//    new
    content.classList.toggle('content-expand');
}
