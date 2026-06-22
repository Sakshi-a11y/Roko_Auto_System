// dashboard.js - load current user from sessionStorage and handle logout
const currentUser = JSON.parse(sessionStorage.getItem('vo_currentUser') || 'null');
if(!currentUser){
  // redirect to login if not authenticated
  window.location.href = 'login.html';
}

// populate fields
const ownerName = document.getElementById('ownerName');
const nameEl = document.getElementById('name');
const contactEl = document.getElementById('contact');
const addressEl = document.getElementById('address');
const vehicleNumberEl = document.getElementById('vehicleNumber');

if(ownerName) ownerName.textContent = currentUser.fullName || currentUser.vehicleNumber;
if(nameEl) nameEl.textContent = currentUser.fullName || '-';
if(contactEl) contactEl.textContent = currentUser.contact || '-';
if(addressEl) addressEl.textContent = currentUser.address || '-';
if(vehicleNumberEl) vehicleNumberEl.textContent = currentUser.vehicleNumber || '-';

// logout
const logoutBtn = document.getElementById('logoutBtn');
logoutBtn && logoutBtn.addEventListener('click', function(){
  sessionStorage.removeItem('vo_currentUser');
  window.location.href = 'login.html';
});
