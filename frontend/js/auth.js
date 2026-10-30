// Visakha Tourism Auth Helper (Admin Only Management)

import { api, showToast } from './api.js';

export function getToken() {
  return localStorage.getItem('vt_token');
}

export function getUser() {
  const user = localStorage.getItem('vt_user');
  return user ? JSON.parse(user) : null;
}

export function isAdmin() {
  const user = getUser();
  return !!getToken() && user && user.role === 'ROLE_ADMIN';
}

export function setSession(token, user) {
  localStorage.setItem('vt_token', token);
  localStorage.setItem('vt_user', JSON.stringify(user));
}

export function clearSession() {
  localStorage.removeItem('vt_token');
  localStorage.removeItem('vt_user');
}

export async function logout() {
  try {
    await api.post('/auth/logout', {});
  } catch (err) {
    // Ignore error on logout
  } finally {
    clearSession();
    showToast('Administrator signed out successfully', 'info');
    setTimeout(() => {
      window.location.href = '/index.html';
    }, 500);
  }
}

export function requireAdmin() {
  if (!isAdmin()) {
    window.location.href = '/admin-login.html';
    return false;
  }
  return true;
}

export function updateNavAuth() {
  const navActions = document.querySelector('.nav-actions');
  if (!navActions) return;

  if (isAdmin()) {
    const user = getUser() || { name: 'Admin' };
    navActions.innerHTML = `
      <a href="/admin-dashboard.html" class="btn btn-sm btn-outline" style="background:#0284c7;color:#fff;border-color:#0284c7;">⚙ Admin Dashboard</a>
      <button id="admin-nav-logout-btn" class="btn btn-sm btn-outline" style="color:var(--danger);cursor:pointer;">Sign Out</button>
    `;

    document.getElementById('admin-nav-logout-btn')?.addEventListener('click', (e) => {
      e.preventDefault();
      logout();
    });
  } else {
    navActions.innerHTML = `
      <a href="/admin-login.html" class="btn btn-sm btn-outline" title="Administrator Portal">Admin Login</a>
    `;
  }
}

// Auto-run nav update and mobile menu handler on DOM load
document.addEventListener('DOMContentLoaded', () => {
  updateNavAuth();

  const toggle = document.querySelector('.mobile-toggle');
  const navLinks = document.querySelector('.nav-links');
  if (toggle && navLinks) {
    toggle.addEventListener('click', () => {
      navLinks.classList.toggle('active');
    });
  }
});
