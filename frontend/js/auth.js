// Visakha Tourism Auth Helper

import { api, showToast } from './api.js';

export function getToken() {
  return localStorage.getItem('vt_token');
}

export function getUser() {
  const user = localStorage.getItem('vt_user');
  return user ? JSON.parse(user) : null;
}

export function isAuthenticated() {
  return !!getToken();
}

export function isAdmin() {
  const user = getUser();
  return user && user.role === 'ROLE_ADMIN';
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
    showToast('You have been logged out successfully', 'info');
    setTimeout(() => {
      window.location.href = '/index.html';
    }, 600);
  }
}

export function requireAuth() {
  if (!isAuthenticated()) {
    const current = encodeURIComponent(window.location.pathname + window.location.search);
    window.location.href = `/login.html?redirect=${current}`;
    return false;
  }
  return true;
}

export function requireAdmin() {
  if (!isAuthenticated() || !isAdmin()) {
    window.location.href = '/admin-login.html';
    return false;
  }
  return true;
}

export function updateNavAuth() {
  const navActions = document.querySelector('.nav-actions');
  if (!navActions) return;

  if (isAuthenticated()) {
    const user = getUser() || { name: 'Traveler' };
    const adminLink = isAdmin()
      ? `<a href="/admin-dashboard.html" class="btn btn-sm btn-outline" style="background:#0284c7;color:#fff;border-color:#0284c7;">⚙ Admin</a>`
      : '';

    navActions.innerHTML = `
      ${adminLink}
      <a href="/profile.html" class="btn btn-sm btn-outline" title="My Profile">👤 ${user.name.split(' ')[0]}</a>
      <button id="logout-btn" class="btn btn-sm btn-outline" style="color:var(--danger);">Logout</button>
    `;

    document.getElementById('logout-btn')?.addEventListener('click', (e) => {
      e.preventDefault();
      logout();
    });
  } else {
    navActions.innerHTML = `
      <a href="/admin-login.html" class="nav-link" style="font-size:0.85rem;color:var(--text-muted);margin-right:6px;" title="Administrator Portal">Admin</a>
      <a href="/login.html" class="btn btn-sm btn-outline">Log In</a>
      <a href="/register.html" class="btn btn-sm btn-primary">Sign Up</a>
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
