// login.js - validates credentials, attempts backend login, falls back to localStorage
const loginForm = document.getElementById('loginForm');
loginForm && loginForm.addEventListener('submit', async function(e){
  e.preventDefault();
  const vehicleNumber = document.getElementById('vehicleNumber').value.trim().toUpperCase();
  const password = document.getElementById('password').value;

  if(!vehicleNumber || !password){
    alert('Please enter vehicle number and password.');
    return;
  }

  // Try backend login first (form-encoded to match typical Spring controllers)
  try{
    const res = await fetch('/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ vehicleNumber, password })
    });

    if(res.ok){
      // try to parse JSON user if backend returns it
      try{
        const user = await res.json();
        sessionStorage.setItem('vo_currentUser', JSON.stringify(user));
      }catch(err){
        // backend didn't return JSON — fall back to local user data or minimal session
        const users = JSON.parse(localStorage.getItem('vo_users') || '{}');
        const localUser = users[vehicleNumber];
        if(localUser){
          sessionStorage.setItem('vo_currentUser', JSON.stringify(localUser));
        }else{
          sessionStorage.setItem('vo_currentUser', JSON.stringify({ vehicleNumber }));
        }
      }
      window.location.href = 'dashboard.html';
      return;
    }
  }catch(err){
    // network/backend error — fall back to localStorage below
    console.warn('Backend login failed, falling back to localStorage', err);
  }

  // LocalStorage fallback for demos
  const users = JSON.parse(localStorage.getItem('vo_users') || '{}');
  const user = users[vehicleNumber];
  if(!user){
    alert('No account found for this vehicle number.');
    return;
  }
  if(user.password !== password){
    alert('Incorrect password.');
    return;
  }

  sessionStorage.setItem('vo_currentUser', JSON.stringify(user));
  window.location.href = 'dashboard.html';
});
