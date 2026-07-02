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
const vehicleTypeEl = document.getElementById('vehicleType');
const strikeCountEl = document.getElementById('strikeCount');
const vehicleStatusEl = document.getElementById('vehicleStatus');


if(ownerName) ownerName.textContent = currentUser.fullName || currentUser.vehicleNumber;
if(nameEl) nameEl.textContent = currentUser.fullName || '-';
if(contactEl) contactEl.textContent = currentUser.contact || '-';
if(addressEl) addressEl.textContent = currentUser.address || '-';
if(vehicleNumberEl) vehicleNumberEl.textContent = currentUser.vehicleNumber || '-';
if(vehicleTypeEl) vehicleTypeEl.textContent = currentUser.vehicleType || currentUser.vehicletype || 'N/A';
if(strikeCountEl) strikeCountEl.textContent = currentUser.strikeCount || '0';
if(vehicleStatusEl) {
  const strikeCount = currentUser.strikeCount || 0;
  vehicleStatusEl.textContent = currentUser.status || (strikeCount >= 3 ? 'blocked' : 'active');
}

async function refreshVehicleDetails(vehicleNumber) {
  if (!vehicleNumber) return;

  try {
    const response = await fetch(`${window.location.origin}/violation/details?vehicleNumber=${encodeURIComponent(vehicleNumber)}`);
    if (!response.ok) return;

    const data = await response.json();
    if (strikeCountEl) strikeCountEl.textContent = data.strikeCount ?? '0';
    if (vehicleStatusEl) vehicleStatusEl.textContent = data.status || (data.strikeCount >= 3 ? 'blocked' : 'active');
    if (vehicleTypeEl) vehicleTypeEl.textContent = data.vehicleType || currentUser.vehicleType || currentUser.vehicletype || 'N/A';

    const updatedUser = {
      ...currentUser,
      strikeCount: data.strikeCount,
      status: data.status,
      vehicleType: data.vehicleType || currentUser.vehicleType || currentUser.vehicletype
    };
    sessionStorage.setItem('vo_currentUser', JSON.stringify(updatedUser));
  } catch (error) {
    console.error('Unable to refresh vehicle details:', error);
  }
}

refreshVehicleDetails(currentUser.vehicleNumber);

// logout
const logoutBtn = document.getElementById('logoutBtn');
logoutBtn && logoutBtn.addEventListener('click', function(){
  sessionStorage.removeItem('vo_currentUser');
  window.location.href = 'login.html';
});
