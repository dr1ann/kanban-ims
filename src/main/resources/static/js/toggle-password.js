document.querySelectorAll('.toggle-password').forEach(function (toggle) {
    toggle.addEventListener('click', function (e) {
        e.preventDefault();
        this.classList.toggle('fa-eye');
        this.classList.toggle('fa-eye-slash');
        const  input = this.parentElement.querySelector('input');
        if (input.getAttribute('type') === 'password') {
            input.setAttribute('type', 'text');
            console.log('first')
        } else {
            console.log('ws')
            input.setAttribute('type', 'password');
        }
    });
});