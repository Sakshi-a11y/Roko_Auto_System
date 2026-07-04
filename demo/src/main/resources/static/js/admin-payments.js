initAdminPage();
setActiveNav('payments');

const paymentsBody = document.getElementById('paymentsBody');

async function loadPayments() {
  try {
    const response = await fetch(`${window.location.origin}/admin/api/payments`);
    if (!response.ok) throw new Error('Unable to load payments');

    const payments = await response.json();
    if (!payments.length) {
      paymentsBody.innerHTML = '<tr><td colspan="8" class="muted">No payment records found.</td></tr>';
      return;
    }

    paymentsBody.innerHTML = payments.map(payment => `
      <tr>
        <td>${formatValue(payment.paymentId)}</td>
        <td>${formatValue(payment.vehicleNumber)}</td>
        <td>${formatValue(payment.ownerName)}</td>
        <td>${formatAmount(payment.amount)}</td>
        <td><span class="${statusClass(payment.status)}">${formatValue(payment.status)}</span></td>
        <td>${formatValue(payment.paymentDate)}</td>
        <td>${formatValue(payment.razorpayOrderId)}</td>
        <td>${formatValue(payment.razorpayPaymentId)}</td>
      </tr>
    `).join('');
  } catch (error) {
    paymentsBody.innerHTML = '<tr><td colspan="8" class="muted">Failed to load payments.</td></tr>';
    console.error(error);
  }
}

loadPayments();
