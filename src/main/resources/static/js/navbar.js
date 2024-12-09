document.getElementById('profile-dropdown').onclick = function(e) {
    e.preventDefault();
    var menu = document.getElementById('dropdown-menu');
    menu.classList.toggle('hidden');
};

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    var button = document.getElementById('profile-dropdown');
    var menu = document.getElementById('dropdown-menu');
    if (!button.contains(event.target) && !menu.contains(event.target)) {
        menu.classList.add('hidden');
    }
});