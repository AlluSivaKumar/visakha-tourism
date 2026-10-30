// Visakha Tourism Google Sign-In & Instant Authentication Module
import { api, showToast } from './api.js';
import { setSession, isAuthenticated } from './auth.js';

export const GOOGLE_ICON_SVG = `
<svg width="20" height="20" viewBox="0 0 24 24" style="vertical-align:middle;margin-right:8px;flex-shrink:0;">
  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
</svg>
`;

export function setupGoogleSignInButton(buttonId, options = {}) {
  const btn = document.getElementById(buttonId);
  if (!btn) return;

  btn.addEventListener('click', (e) => {
    e.preventDefault();
    openGoogleSignInModal(options);
  });
}

export function openGoogleSignInModal(options = {}) {
  const redirectUrl = options.redirectUrl || getQueryRedirect() || '/destinations.html';
  const onSuccess = options.onSuccess;

  // Remove existing modal if any
  document.getElementById('google-signin-modal')?.remove();

  const modalOverlay = document.createElement('div');
  modalOverlay.id = 'google-signin-modal';
  modalOverlay.style.cssText = `
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(15, 23, 42, 0.7);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    padding: 20px;
    animation: fadeIn 0.2s ease-out;
  `;

  const rememberedEmail = localStorage.getItem('vt_google_last_email') || '';
  const rememberedName = localStorage.getItem('vt_google_last_name') || '';

  modalOverlay.innerHTML = `
    <div style="background:#ffffff; border-radius:16px; width:100%; max-width:440px; box-shadow:0 25px 50px -12px rgba(0,0,0,0.25); overflow:hidden; border:1px solid #e2e8f0;">
      <!-- Header -->
      <div style="padding:24px 28px 18px; border-bottom:1px solid #f1f5f9; display:flex; justify-content:space-between; align-items:center;">
        <div style="display:flex; align-items:center; gap:10px;">
          ${GOOGLE_ICON_SVG}
          <div style="font-weight:700; font-size:1.15rem; color:#1e293b;">Sign in with Google</div>
        </div>
        <button id="google-modal-close" style="background:none; border:none; font-size:1.4rem; line-height:1; color:#94a3b8; cursor:pointer; padding:4px 8px;">✕</button>
      </div>

      <!-- Body -->
      <div style="padding:24px 28px;">
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px; padding:12px; background:#f0fdf4; border:1px solid #bbf7d0; border-radius:10px;">
          <span style="font-size:1.4rem;">⚡</span>
          <div style="font-size:0.85rem; color:#166534; line-height:1.4;">
            <strong>Instant Access:</strong> No email OTP verification required. Your account is activated instantly!
          </div>
        </div>

        <div id="google-modal-error" style="display:none; margin-bottom:16px; padding:10px 14px; background:#fef2f2; border:1px solid #fecaca; border-radius:8px; font-size:0.85rem; color:#b91c1c;"></div>

        <form id="google-auth-form">
          <div style="margin-bottom:16px;">
            <label style="display:block; font-size:0.85rem; font-weight:600; color:#334155; margin-bottom:6px;">Full Name</label>
            <input type="text" id="google-input-name" required value="${rememberedName}" placeholder="e.g. Siva Kumar" style="width:100%; padding:10px 14px; border:1px solid #cbd5e1; border-radius:8px; font-size:0.95rem; outline:none; box-sizing:border-box;">
          </div>

          <div style="margin-bottom:22px;">
            <label style="display:block; font-size:0.85rem; font-weight:600; color:#334155; margin-bottom:6px;">Google Email Address</label>
            <input type="email" id="google-input-email" required value="${rememberedEmail}" placeholder="yourname@gmail.com" style="width:100%; padding:10px 14px; border:1px solid #cbd5e1; border-radius:8px; font-size:0.95rem; outline:none; box-sizing:border-box;">
          </div>

          <button type="submit" id="google-modal-submit-btn" style="width:100%; display:flex; align-items:center; justify-content:center; gap:8px; background:#1a73e8; color:#ffffff; font-weight:600; font-size:0.95rem; padding:12px; border:none; border-radius:8px; cursor:pointer; transition:background 0.2s;">
            ${GOOGLE_ICON_SVG}
            <span>Continue with Google</span>
          </button>
        </form>

        <div style="text-align:center; margin-top:16px; font-size:0.78rem; color:#64748b;">
          By continuing, you connect securely to Visakha Tourism and agree to our traveler guidelines.
        </div>
      </div>
    </div>
  `;

  document.body.appendChild(modalOverlay);

  // Close handlers
  const closeBtn = document.getElementById('google-modal-close');
  closeBtn?.addEventListener('click', () => modalOverlay.remove());
  modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) modalOverlay.remove();
  });

  // Submit handler
  const form = document.getElementById('google-auth-form');
  const errorBox = document.getElementById('google-modal-error');
  const submitBtn = document.getElementById('google-modal-submit-btn');

  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (errorBox) errorBox.style.display = 'none';

    const name = document.getElementById('google-input-name').value.trim();
    const email = document.getElementById('google-input-email').value.trim();

    if (!email) {
      if (errorBox) {
        errorBox.textContent = 'Please enter your Google email address.';
        errorBox.style.display = 'block';
      }
      return;
    }

    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<span class="spinner" style="display:inline-block;width:16px;height:16px;border:2px solid #fff;border-top-color:transparent;border-radius:50%;animation:spin 0.8s linear infinite;margin-right:8px;"></span> Signing in with Google...';
    }

    try {
      const res = await api.post('/auth/google', { email, name });
      const authData = res.data;

      // Save credentials in session
      setSession(authData.token, {
        id: authData.userId,
        name: authData.name,
        email: authData.email,
        role: authData.role
      });

      // Remember Google account on this device
      localStorage.setItem('vt_google_last_email', email);
      localStorage.setItem('vt_google_last_name', name);

      showToast(`Welcome, ${authData.name}! Signed in successfully with Google.`, 'success');
      modalOverlay.remove();

      if (typeof onSuccess === 'function') {
        onSuccess(authData);
      } else {
        setTimeout(() => {
          window.location.href = redirectUrl;
        }, 400);
      }

    } catch (err) {
      console.error('Google Sign-In Error:', err);
      if (errorBox) {
        errorBox.textContent = err.message || 'Google sign-in could not be completed. Please try again.';
        errorBox.style.display = 'block';
      }
    } finally {
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.innerHTML = `${GOOGLE_ICON_SVG}<span>Continue with Google</span>`;
      }
    }
  });
}

function getQueryRedirect() {
  const params = new URLSearchParams(window.location.search);
  return params.get('redirect');
}
