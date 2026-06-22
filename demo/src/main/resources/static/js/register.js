const registerForm = document.getElementById('registerForm');

registerForm?.addEventListener('submit', async function (e) {
    e.preventDefault();

    const fullName = document.getElementById('fullName').value.trim();
    const contact = document.getElementById('contact').value.trim();
    const address = document.getElementById('address').value.trim();
    const vehicleType = document.getElementById('vehicleType').value.trim();
    const registrationNumber = document
        .getElementById('registrationNumber')
        .value.trim()
        .toUpperCase();

    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // Validation
    if (
        !fullName ||
        !contact ||
        !address ||
        !vehicleType ||
        !registrationNumber ||
        !password ||
        !confirmPassword
    ) {
        alert('Please fill in all fields.');
        return;
    }

    if (password.length < 6) {
        alert('Password must be at least 6 characters.');
        return;
    }

    if (password !== confirmPassword) {
        alert('Passwords do not match.');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: fullName,
                contact: contact,
                address: address,
                password: password,
                vehicleNumber: registrationNumber,
                vehicleType: vehicleType
            })
        });

        const result = await response.text();

        if (response.ok) {
            alert('Registration Successful');
            window.location.href = 'login.html';
        } else {
            alert('Registration Failed: ' + result);
        }

    } catch (error) {
        console.error('Error:', error);
        alert('Unable to connect to backend server.');
    }
});