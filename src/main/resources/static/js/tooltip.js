   const helpIcon = document.getElementById('help-icon');
    const tooltip = document.getElementById('tooltip');

    helpIcon.addEventListener('click', function(e) {
        e.preventDefault();
        tooltip.classList.toggle('hidden');
    });
