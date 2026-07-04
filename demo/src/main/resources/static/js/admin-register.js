const adminRegisterForm = document.getElementById('adminRegisterForm');

adminRegisterForm?.addEventListener('submit', async function (e) {
  e.preventDefault();

  const fullName = document.getElementById('fullName').value.trim();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  const confirmPassword = document.getElementById('confirmPassword').value;

  if (!fullName || !email || !password || !confirmPassword) {
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

  const submitBtn = adminRegisterForm.querySelector('button[type="submit"]');
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = 'Registering...';
  }

  try {
    const response = await fetch(`${window.location.origin}/admin/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: fullName,
        email: email,
        password: password
      })
    });

    const result = await response.text();

    if (response.ok) {
      alert('Registration successful. Please login.');
      window.location.href = 'admin-login.html';
      return;
    }

    alert('Registration failed: ' + (result || response.statusText));
  } catch (error) {
    console.error('Error:', error);
    alert(
      'Unable to connect to backend server. Make sure the app is running on ' +
      window.location.origin
    );
  } finally {
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Register';
    }
  }
});
