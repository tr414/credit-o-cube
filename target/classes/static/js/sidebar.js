function toggleMenu() {
  var menu = document.getElementById("sideMenu");
  var menuBtn = document.getElementsByClassName("menu-btn")[0];

  if (menu.style.left === '0px') {
    menu.style.left = '-250px';
  } else {
    menu.style.left = '0px';
  }
}