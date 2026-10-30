// Visakha Tourism Centralized API Client

const API_BASE = window.VT_API_BASE || '/api';

class ApiClient {
  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
    const headers = options.headers || {};

    const token = localStorage.getItem('vt_token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

    const config = {
      ...options,
      headers
    };

    try {
      const response = await fetch(url, config);

      // Handle 401 Unauthorized
      if (response.status === 401) {
        if (token && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/admin-login')) {
          localStorage.removeItem('vt_token');
          localStorage.removeItem('vt_user');
          if (window.location.pathname.includes('admin')) {
            window.location.href = '/admin-login.html?expired=true';
          }
        }
      }

      let data;
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        data = await response.json();
      } else {
        const text = await response.text();
        data = { message: text };
      }

      if (!response.ok) {
        let errorMsg = data && data.message ? data.message : '';
        if (!errorMsg || errorMsg.includes('<html') || errorMsg.includes('<!DOCTYPE')) {
          if (response.status === 504 || response.status === 502) {
            errorMsg = 'Cloud server is waking up from sleep. Please wait 15 seconds and try again.';
          } else if (response.status === 403) {
            errorMsg = 'Access forbidden or CORS policy error. Please reload the page.';
          } else {
            errorMsg = `Server response status: ${response.status}. Please try again.`;
          }
        }
        const error = new Error(errorMsg);
        error.data = data;
        error.status = response.status;
        throw error;
      }

      return data;
    } catch (err) {
      console.error(`API Error on [${options.method || 'GET'}] ${endpoint}:`, err);
      throw err;
    }
  }

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  post(endpoint, body) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body)
    });
  }

  put(endpoint, body) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body)
    });
  }

  patch(endpoint, body) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(body)
    });
  }

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }

  upload(endpoint, formData) {
    return this.request(endpoint, {
      method: 'POST',
      body: formData
    });
  }
}

export const api = new ApiClient();

// Toast Notifications System
export function showToast(message, type = 'info', duration = 4000) {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
  toast.innerHTML = `
    <span style="font-weight: 800; font-size: 1.1rem;">${icon}</span>
    <span style="flex: 1;">${message}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// Background warm-up ping for free-tier Render container
if (typeof window !== 'undefined') {
  setTimeout(() => {
    fetch('/api/destinations?page=0&size=1', { method: 'GET' }).catch(() => {});
  }, 200);
}
