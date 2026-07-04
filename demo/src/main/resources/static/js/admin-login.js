const adminLoginForm = document.getElementById('adminLoginForm');

adminLoginForm?.addEventListener('submit', async function (e) {
  e.preventDefault();

  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;

  if (!email || !password) {
    alert('Please enter email and password.');
    return;
  }

  const submitBtn = adminLoginForm.querySelector('button[type="submit"]');
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = 'Logging in...';
  }

  try {
    const response = await fetch(`${window.location.origin}/admin/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email, password })
    });

    if (response.ok) {
      const admin = await response.json();
      sessionStorage.setItem('admin_currentUser', JSON.stringify(admin));
      window.location.href = 'admin-dashboard.html';
      return;
    }

    let message = 'Invalid email or password.';
    try {
      const error = await response.json();
      if (error.error) {
        message = error.error;
      }
    } catch (parseError) {
      const text = await response.text();
      if (text) {
        message = text;
      }
    }

    alert(message);
  } catch (error) {
    console.error('Error:', error);
    alert(
      'Unable to connect to backend server. Make sure the app is running on ' +
      window.location.origin
    );
  } finally {
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Login';
    }
  }
});
