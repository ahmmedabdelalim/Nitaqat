// dashboard.js

document.addEventListener('DOMContentLoaded', function () {
    const adminName = document.getElementById('admin-name');

    // You can dynamically set the admin name from session/localStorage or API
    const storedAdminName = localStorage.getItem('adminName') || 'Admin';
    adminName.textContent = storedAdminName;

    // Add more client-side interactivity here if needed
});

