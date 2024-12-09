
document.addEventListener('DOMContentLoaded', function () {
    const drawerButton = document.querySelector('[data-drawer-toggle="default-sidebar"]');
    const drawer = document.getElementById('default-sidebar');
    const container = document.getElementById('container');

    drawerButton.addEventListener('click', function () {
      drawer.classList.toggle('-translate-x-full');
      drawer.classList.toggle('lg:hidden');
      container.classList.toggle('lg:ml-64');
 
  
    const currentTitle = drawerButton.getAttribute('title');

    if (currentTitle === 'Open Sidebar') {
        drawerButton.setAttribute('title', 'Close Sidebar');
    } else if(currentTitle === 'Close Sidebar') {
        drawerButton.setAttribute('title', 'Open Sidebar');
    }

    });

    // Close sidebar when clicking outside of it
    document.addEventListener('click', function (event) {
      if (!drawer.contains(event.target) && !drawerButton.contains(event.target)) {
        drawer.classList.add('-translate-x-full');
      }
      
    });

    const dropdownInventory = document.getElementById("dropdown-list-inv");
    const dropdownSupplier = document.getElementById("dropdown-list-sup");
    const dropdownBuy = document.getElementById("dropdown-list-buy");

    const activeItemInventory = dropdownInventory.querySelector('.active-item-inv');
    const activeItemSupplier = dropdownSupplier.querySelector('.active-item-sup');
    const activeItemBuy = dropdownBuy.querySelector('.active-item-buy');

    const dropdownSell = document.getElementById("dropdown-list-sell");
    const activeItemSell = dropdownSell.querySelector('.active-item-sell');
   
    if (!activeItemInventory) {
      dropdownInventory.classList.add("hidden");
    } else {
      dropdownInventory.classList.remove("hidden");
    }

    if (!activeItemSupplier) {
      dropdownSupplier.classList.add("hidden");
    } else {
      dropdownSupplier.classList.remove("hidden");
    }

    if (!activeItemBuy) {
      dropdownBuy.classList.add("hidden");
    } else {
      dropdownBuy.classList.remove("hidden");
    }

    if (!activeItemSell) {
      dropdownSell.classList.add("hidden");
    } else {
      dropdownSell.classList.remove("hidden");
    }

  });
  
  document.getElementById("toggle-dropdown-inv").addEventListener("click", function() {
    const dropdown = document.getElementById("dropdown-list-inv");

    dropdown.classList.toggle("hidden");
});

document.getElementById("toggle-dropdown-sup").addEventListener("click", function() {
  const dropdown = document.getElementById("dropdown-list-sup");

  dropdown.classList.toggle("hidden");
});

document.getElementById("toggle-dropdown-buy").addEventListener("click", function() {
  const dropdown = document.getElementById("dropdown-list-buy");

  dropdown.classList.toggle("hidden");
});
  
document.getElementById("toggle-dropdown-sell").addEventListener("click", function() {
  const dropdown = document.getElementById("dropdown-list-sell");

  dropdown.classList.toggle("hidden");
});

