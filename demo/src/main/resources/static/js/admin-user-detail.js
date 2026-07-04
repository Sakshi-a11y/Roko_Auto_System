initAdminPage();

const userDetailContent = document.getElementById('userDetailContent');
const params = new URLSearchParams(window.location.search);
const vehicleId = params.get('vehicleId');

if (!vehicleId) {
  userDetailContent.innerHTML = '<p class="muted">No vehicle selected. <a href="admin-vehicles.html">Go back to vehicles</a></p>';
} else {
  loadVehicleDetails(vehicleId);
}

async function loadVehicleDetails(id) {
  try {
    const response = await fetch(`${window.location.origin}/admin/api/vehicles/${id}`);
    const data = await response.json();

    if (!response.ok) {
      userDetailContent.innerHTML = `<p class="muted">${data.error || 'Vehicle not found.'}</p>`;
      return;
    }

    const vehicle = data.vehicle || {};

    const paymentsHtml = (data.payments || []).length
      ? (data.payments || []).map(payment => `
          <tr>
            <td>${formatValue(payment.paymentId)}</td>
            <td>${formatAmount(payment.amount)}</td>
            <td><span class="${statusClass(payment.status)}">${formatValue(payment.status)}</span></td>
            <td>${formatValue(payment.paymentDate)}</td>
            <td>${formatValue(payment.razorpayOrderId)}</td>
          </tr>
        `).join('')
      : '<tr><td colspan="5" class="muted">No payments found.</td></tr>';

    userDetailContent.innerHTML = `
      <h4>Owner Information</h4>
      <div class="detail-grid">
        <p><strong>Name:</strong> ${formatValue(data.name)}</p>
        <p><strong>Contact:</strong> ${formatValue(data.contact)}</p>
        <p><strong>Address:</strong> ${formatValue(data.address)}</p>
      </div>

      <h4>Vehicle Information</h4>
      <div class="detail-grid">
        <p><strong>Vehicle No:</strong> ${formatValue(vehicle.vehicleNumber)}</p>
        <p><strong>Type:</strong> ${formatValue(vehicle.vehicleType)}</p>
        <p><strong>Registration Date:</strong> ${formatValue(vehicle.registrationDate)}</p>
        <p><strong>Strikes:</strong> ${formatValue(vehicle.strikeCount)}</p>
        <p><strong>Status:</strong> <span class="${statusClass(vehicle.status)}">${formatValue(vehicle.status)}</span></p>
      </div>

      <h4>Payment History</h4>
      <div class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>Payment ID</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Date</th>
              <th>Order ID</th>
            </tr>
          </thead>
          <tbody>${paymentsHtml}</tbody>
        </table>
      </div>
    `;
  } catch (error) {
    userDetailContent.innerHTML = '<p class="muted">Failed to load vehicle details.</p>';
    console.error(error);
  }
}
