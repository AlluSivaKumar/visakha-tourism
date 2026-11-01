// Theme Manager: Dark / Light Mode with Instant Bootstrapping and Local Persistence

export function getStoredTheme() {
  try {
    return localStorage.getItem('vt_theme');
  } catch (e) {
    return null;
  }
}

export function getSystemTheme() {
  return (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) ? 'dark' : 'light';
}

export function getCurrentTheme() {
  const currentAttr = document.documentElement.getAttribute('data-theme');
  if (currentAttr === 'dark' || currentAttr === 'light') {
    return currentAttr;
  }
  return getStoredTheme() || getSystemTheme();
}

export function updateThemeButtons(theme) {
  const buttons = document.querySelectorAll('.theme-toggle-btn');
  const isDark = theme === 'dark';
  buttons.forEach(btn => {
    btn.setAttribute('aria-label', isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode');
    btn.setAttribute('title', isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode');
    btn.innerHTML = `<span class="theme-icon" aria-hidden="true">${isDark ? '&#x2600;&#xFE0F;' : '&#x1F319;'}</span>`;
  });
}

export function applyTheme(theme, save = true) {
  const targetTheme = (theme === 'dark') ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', targetTheme);
  if (save) {
    try {
      localStorage.setItem('vt_theme', targetTheme);
    } catch (e) {
      // Ignore private browsing storage errors
    }
  }
  updateThemeButtons(targetTheme);
}

export function toggleTheme() {
  const current = getCurrentTheme();
  const next = (current === 'dark') ? 'light' : 'dark';
  applyTheme(next, true);
  return next;
}

// Expose globally for direct execution
if (typeof window !== 'undefined') {
  window.toggleTheme = toggleTheme;
  window.applyTheme = applyTheme;
  window.getCurrentTheme = getCurrentTheme;
}

// Immediate execution & listener binding
const initialTheme = getCurrentTheme();
applyTheme(initialTheme, false);

document.addEventListener('DOMContentLoaded', () => {
  updateThemeButtons(getCurrentTheme());

  // Listen for clicks on any theme toggle button
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.theme-toggle-btn');
    if (btn) {
      e.preventDefault();
      toggleTheme();
    }
  });

  // Listen to OS theme changes if user hasn't chosen manually
  if (window.matchMedia) {
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (!getStoredTheme()) {
        applyTheme(e.matches ? 'dark' : 'light', false);
      }
    });
  }
});
