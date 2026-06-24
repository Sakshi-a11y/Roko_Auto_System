const loginForm = document.getElementById('loginForm');

loginForm?.addEventListener('submit', async function (e) {
  e.preventDefault();

  const vehicleNumber = document.getElementById('vehicleNumber').value.trim().toUpperCase();
  const password = document.getElementById('password').value;

  if (!vehicleNumber || !password) {
    alert('Please enter vehicle number and password.');
    return;
  }

  const submitBtn = loginForm.querySelector('button[type="submit"]');
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = 'Logging in...';
  }

  try {
    const response = await fetch(`${window.location.origin}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ vehicleNumber, password })
    });

    if (response.ok) {
      const user = await response.json();
      sessionStorage.setItem('vo_currentUser', JSON.stringify(user));
      window.location.href = 'dashboard.html';
      return;
    }

    let message = 'Invalid vehicle number or password.';
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
