document.querySelector('.google-btn').addEventListener('click', function() {
    alert('Sign up with Google clicked!');
});
document.querySelector('.microsoft-btn').addEventListener('click', function() {
    alert('Sign up with Microsoft clicked!');
});
document.querySelector('.email-btn').addEventListener('click', function() {
    alert('Sign up with Email clicked!');
});

document.querySelector('.login-link a').addEventListener('click', function(e) {
    e.preventDefault();
    alert('Redirect to login page!');
}); 