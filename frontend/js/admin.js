// Visakha Tourism Admin Dashboard & Management Controller

import { api, showToast } from './api.js';
import { requireAdmin, getUser, logout } from './auth.js';

document.addEventListener('DOMContentLoaded', () => {
  // Enforce admin privileges
  if (!requireAdmin()) return;

  setupAdminUserPill();
  setupAdminLogout();

  // Route page-specific initialization
  const path = window.location.pathname;
  if (path.includes('admin-dashboard.html')) {
    initDashboard();
  } else if (path.includes('admin-destinations.html')) {
    initDestinationsTable();
  } else if (path.includes('admin-destination-form.html')) {
    initDestinationForm();
  }
});

function setupAdminUserPill() {
  const user = getUser();
  const nameEl = document.getElementById('admin-user-name');
  if (nameEl && user) {
    nameEl.textContent = user.name;
  }
}

function setupAdminLogout() {
  document.getElementById('admin-logout-btn')?.addEventListener('click', (e) => {
    e.preventDefault();
    logout();
  });
}

// ==========================================
// 1. DASHBOARD METRICS OVERVIEW
// ==========================================
async function initDashboard() {
  try {
    const res = await api.get('/admin/dashboard');
    const data = res.data;

    // Render Stats Cards
    document.getElementById('stat-total-dest').textContent = data.totalDestinations;
    document.getElementById('stat-published-dest').textContent = data.publishedDestinations;
    document.getElementById('stat-unpublished-dest').textContent = data.unpublishedDestinations;
    document.getElementById('stat-total-photos').textContent = data.totalPhotos;

    // Render Category Distribution
    renderCategoryBars(data.categoryCounts, data.totalDestinations);

    // Render Recent Destinations Table
    renderRecentTable(data.recentDestinations);

  } catch (err) {
    console.error('Failed to load dashboard metrics:', err);
    showToast('Failed to load dashboard metrics', 'error');
  }
}

function renderCategoryBars(counts, total) {
  const container = document.getElementById('category-distribution-container');
  if (!container) return;

  const entries = Object.entries(counts || {});
  if (entries.length === 0) {
    container.innerHTML = '<p class="text-muted">No category data recorded yet.</p>';
    return;
  }

  container.innerHTML = entries.map(([category, count]) => {
    const pct = total > 0 ? Math.round((count / total) * 100) : 0;
    return `
      <div class="cat-bar-item">
        <div class="cat-bar-header">
          <span>${category}</span>
          <span style="color:var(--text-muted);">${count} places (${pct}%)</span>
        </div>
        <div class="cat-bar-track">
          <div class="cat-bar-fill" style="width: ${pct}%;"></div>
        </div>
      </div>
    `;
  }).join('');
}

function renderRecentTable(recent) {
  const tbody = document.getElementById('recent-destinations-tbody');
  if (!tbody) return;

  if (!recent || recent.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="text-center">No destinations recorded yet.</td></tr>';
    return;
  }

  tbody.innerHTML = recent.map(dest => `
    <tr>
      <td>
        <img src="${dest.primaryImageUrl}" alt="${dest.name}" class="table-thumb" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=100&q=80'">
      </td>
      <td><strong>${dest.name}</strong></td>
      <td>${dest.categoryName}</td>
      <td>
        <span class="status-pill ${dest.published ? 'status-published' : 'status-unpublished'}">
          ${dest.published ? 'Published' : 'Hidden'}
        </span>
      </td>
      <td>
        <a href="/admin-destination-form.html?id=${dest.id}" class="btn btn-sm btn-outline">Edit</a>
      </td>
    </tr>
  `).join('');
}

// ==========================================
// 2. DESTINATION MANAGEMENT TABLE
// ==========================================
let tablePage = 0;
const tableSize = 15;
let tablePublished = null;
let tableCategory = null;
let tableSearch = '';

async function initDestinationsTable() {
  await loadCategoriesIntoDropdown('table-category-filter', true);

  const searchInput = document.getElementById('table-search-input');
  const catFilter = document.getElementById('table-category-filter');
  const statusFilter = document.getElementById('table-status-filter');

  let debounce;
  searchInput?.addEventListener('input', (e) => {
    clearTimeout(debounce);
    debounce = setTimeout(() => {
      tableSearch = e.target.value.trim();
      tablePage = 0;
      fetchAdminDestinations();
    }, 400);
  });

  catFilter?.addEventListener('change', (e) => {
    tableCategory = e.target.value ? e.target.value : null;
    tablePage = 0;
    fetchAdminDestinations();
  });

  statusFilter?.addEventListener('change', (e) => {
    tablePublished = e.target.value === '' ? null : (e.target.value === 'true');
    tablePage = 0;
    fetchAdminDestinations();
  });

  fetchAdminDestinations();
}

async function fetchAdminDestinations() {
  const tbody = document.getElementById('destinations-table-body');
  const countText = document.getElementById('table-count-text');
  const paginationControls = document.getElementById('table-pagination');

  if (tbody) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center" style="padding:40px;"><span class="spinner spinner-primary"></span></td></tr>';
  }

  try {
    const params = new URLSearchParams({
      page: tablePage,
      size: tableSize,
      sort: 'updated_desc'
    });
    if (tableCategory) params.append('categoryId', tableCategory);
    if (tablePublished !== null) params.append('published', tablePublished);
    if (tableSearch) params.append('search', tableSearch);

    const res = await api.get(`/admin/destinations?${params.toString()}`);
    const pageData = res.data;
    const items = pageData.content || [];

    if (countText) {
      countText.textContent = `Showing ${items.length} of ${pageData.totalElements} destinations`;
    }

    if (items.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7" class="text-center" style="padding:40px;">No destinations match your filters.</td></tr>';
      if (paginationControls) paginationControls.innerHTML = '';
      return;
    }

    tbody.innerHTML = items.map(dest => `
      <tr>
        <td>
          <img src="${dest.primaryImageUrl}" alt="${dest.name}" class="table-thumb" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=100&q=80'">
        </td>
        <td>
          <div style="font-weight:700;color:var(--text-main);">${dest.name}</div>
          <div style="font-size:0.8rem;color:var(--text-muted);">${dest.address}</div>
        </td>
        <td><span class="status-pill" style="background:#f1f5f9;color:var(--text-main);">${dest.categoryName}</span></td>
        <td>
          <button class="status-pill ${dest.published ? 'status-published' : 'status-unpublished'}"
                  style="cursor:pointer;border:none;"
                  onclick="window.togglePublishStatus(${dest.id}, ${!dest.published})"
                  title="Click to toggle publish status">
            ${dest.published ? '● Published' : '○ Hidden'}
          </button>
        </td>
        <td style="font-size:0.82rem;color:var(--text-muted);">
          ${dest.updatedAt ? new Date(dest.updatedAt).toLocaleDateString() : 'N/A'}
        </td>
        <td>
          <div class="action-buttons">
            <a href="/destination-details.html?slug=${dest.slug}" target="_blank" class="btn btn-sm btn-outline" title="View public page">👁</a>
            <a href="/admin-destination-form.html?id=${dest.id}" class="btn btn-sm btn-outline" title="Edit destination">✏</a>
            <button class="btn btn-sm btn-outline" style="color:var(--danger);" onclick="window.confirmDeleteDestination(${dest.id}, '${dest.name.replace(/'/g, "\\'")}')" title="Delete destination">🗑</button>
          </div>
        </td>
      </tr>
    `).join('');

    renderPagination(pageData, paginationControls);

  } catch (err) {
    console.error('Failed to load admin destinations:', err);
    showToast('Failed to load destinations table', 'error');
  }
}

function renderPagination(pageData, container) {
  if (!container) return;
  const totalPages = pageData.totalPages;
  if (totalPages <= 1) {
    container.innerHTML = '';
    return;
  }

  container.innerHTML = `
    <button class="btn btn-sm btn-outline" ${pageData.first ? 'disabled' : ''} onclick="window.goToPage(${tablePage - 1})">← Previous</button>
    <span style="font-size:0.88rem;color:var(--text-muted);margin:0 12px;">Page ${tablePage + 1} of ${totalPages}</span>
    <button class="btn btn-sm btn-outline" ${pageData.last ? 'disabled' : ''} onclick="window.goToPage(${tablePage + 1})">Next →</button>
  `;
}

window.goToPage = function(p) {
  tablePage = p;
  fetchAdminDestinations();
};

window.togglePublishStatus = async function(id, newStatus) {
  try {
    await api.patch(`/admin/destinations/${id}/status`, { published: newStatus });
    showToast(`Destination is now ${newStatus ? 'Published' : 'Hidden'}`, 'success');
    fetchAdminDestinations();
  } catch (err) {
    showToast('Failed to update status', 'error');
  }
};

window.confirmDeleteDestination = async function(id, name) {
  if (confirm(`Are you sure you want to permanently delete "${name}"? This action cannot be undone.`)) {
    try {
      await api.delete(`/admin/destinations/${id}`);
      showToast('Destination deleted successfully', 'success');
      fetchAdminDestinations();
    } catch (err) {
      showToast('Failed to delete destination', 'error');
    }
  }
};

// ==========================================
// 3. DESTINATION CREATE / EDIT FORM
// ==========================================
async function initDestinationForm() {
  await loadCategoriesIntoDropdown('dest-category', false);

  const params = new URLSearchParams(window.location.search);
  const editId = params.get('id');

  const formTitle = document.getElementById('form-heading');
  const photoSection = document.getElementById('photo-manager-section');

  if (editId) {
    if (formTitle) formTitle.textContent = 'Edit Destination';
    if (photoSection) photoSection.style.display = 'block';
    await loadDestinationIntoForm(editId);
    initPhotoUploadManager(editId);
  } else {
    if (formTitle) formTitle.textContent = 'Add New Destination';
    if (photoSection) photoSection.style.display = 'none';
  }

  const form = document.getElementById('destination-form');
  form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    await saveDestination(editId);
  });
}

async function loadCategoriesIntoDropdown(selectId, includeAllOption) {
  const select = document.getElementById(selectId);
  if (!select) return;

  try {
    const res = await api.get('/categories');
    const categories = res.data || [];

    select.innerHTML = includeAllOption ? '<option value="">All Categories</option>' : '<option value="">Select Category</option>';
    categories.forEach(cat => {
      const opt = document.createElement('option');
      opt.value = cat.id;
      opt.textContent = cat.name;
      select.appendChild(opt);
    });
  } catch (err) {
    console.error('Failed to load categories dropdown:', err);
  }
}

async function loadDestinationIntoForm(id) {
  try {
    const res = await api.get(`/admin/destinations/${id}`);
    const dest = res.data;

    document.getElementById('dest-name').value = dest.name || '';
    document.getElementById('dest-short-desc').value = dest.shortDescription || '';
    document.getElementById('dest-full-desc').value = dest.fullDescription || '';
    document.getElementById('dest-category').value = dest.category?.id || '';
    document.getElementById('dest-address').value = dest.address || '';
    document.getElementById('dest-lat').value = dest.latitude || '';
    document.getElementById('dest-lng').value = dest.longitude || '';
    document.getElementById('dest-hours').value = dest.openingHours || '';
    document.getElementById('dest-price').value = dest.ticketPrice || '';
    document.getElementById('dest-best-time').value = dest.bestTimeToVisit || '';
    document.getElementById('dest-tips').value = dest.travelTips || '';
    document.getElementById('dest-things').value = dest.thingsToDo || '';
    document.getElementById('dest-primary-img').value = dest.primaryImageUrl || '';
    document.getElementById('dest-featured').checked = !!dest.featured;
    document.getElementById('dest-published').checked = !!dest.published;

    renderPhotoManager(dest.photos || []);

  } catch (err) {
    console.error('Failed to load destination for editing:', err);
    showToast('Failed to load destination record', 'error');
  }
}

async function saveDestination(editId) {
  const submitBtn = document.getElementById('dest-submit-btn');
  if (submitBtn) {
    submitBtn.disabled = true;
    submitBtn.textContent = 'Saving Destination...';
  }

  const payload = {
    name: document.getElementById('dest-name').value.trim(),
    shortDescription: document.getElementById('dest-short-desc').value.trim(),
    fullDescription: document.getElementById('dest-full-desc').value.trim(),
    categoryId: parseInt(document.getElementById('dest-category').value, 10),
    address: document.getElementById('dest-address').value.trim(),
    latitude: document.getElementById('dest-lat').value ? parseFloat(document.getElementById('dest-lat').value) : null,
    longitude: document.getElementById('dest-lng').value ? parseFloat(document.getElementById('dest-lng').value) : null,
    openingHours: document.getElementById('dest-hours').value.trim(),
    ticketPrice: document.getElementById('dest-price').value.trim(),
    bestTimeToVisit: document.getElementById('dest-best-time').value.trim(),
    travelTips: document.getElementById('dest-tips').value.trim(),
    thingsToDo: document.getElementById('dest-things').value.trim(),
    primaryImageUrl: document.getElementById('dest-primary-img').value.trim(),
    featured: document.getElementById('dest-featured').checked,
    published: document.getElementById('dest-published').checked
  };

  try {
    let res;
    if (editId) {
      res = await api.put(`/admin/destinations/${editId}`, payload);
      showToast('Destination updated successfully!', 'success');
    } else {
      res = await api.post('/admin/destinations', payload);
      showToast('Destination created successfully!', 'success');
      setTimeout(() => {
        window.location.href = `/admin-destination-form.html?id=${res.data.id}`;
      }, 1000);
      return;
    }
  } catch (err) {
    showToast(err.message || 'Failed to save destination', 'error');
  } finally {
    if (submitBtn) {
      submitBtn.disabled = false;
      submitBtn.textContent = editId ? 'Update Destination' : 'Create Destination';
    }
  }
}

// Photo Upload Manager
function initPhotoUploadManager(destinationId) {
  const fileInput = document.getElementById('photo-file-input');
  const dropzone = document.getElementById('photo-dropzone');

  dropzone?.addEventListener('click', () => fileInput?.click());

  fileInput?.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);
    formData.append('isPrimary', 'false');

    try {
      showToast('Uploading photo...', 'info');
      await api.upload(`/admin/destinations/${destinationId}/photos`, formData);
      showToast('Photo uploaded successfully!', 'success');
      // Refresh destination data
      await loadDestinationIntoForm(destinationId);
    } catch (err) {
      showToast(err.message || 'Upload failed', 'error');
    } finally {
      fileInput.value = '';
    }
  });

  document.getElementById('add-url-photo-btn')?.addEventListener('click', async () => {
    const urlInput = document.getElementById('photo-url-input');
    const url = urlInput?.value.trim();
    if (!url) {
      showToast('Please enter an image URL', 'warning');
      return;
    }

    try {
      await api.post(`/admin/destinations/${destinationId}/photos/url`, {
        imageUrl: url,
        caption: document.getElementById('dest-name').value,
        altText: document.getElementById('dest-name').value,
        isPrimary: false
      });
      showToast('Photo added from URL!', 'success');
      urlInput.value = '';
      await loadDestinationIntoForm(destinationId);
    } catch (err) {
      showToast('Failed to add photo', 'error');
    }
  });
}

function renderPhotoManager(photos) {
  const container = document.getElementById('photo-manager-grid');
  if (!container) return;

  if (photos.length === 0) {
    container.innerHTML = '<p class="text-muted" style="grid-column:1/-1;">No photos in gallery yet. Upload one above.</p>';
    return;
  }

  container.innerHTML = photos.map(photo => `
    <div class="photo-manager-card">
      <img src="${photo.imageUrl}" alt="${photo.altText}" class="photo-manager-thumb">
      ${photo.primary ? '<span class="status-pill status-published" style="position:absolute;top:6px;left:6px;font-size:0.7rem;">Primary</span>' : ''}
      <div class="photo-manager-actions">
        ${!photo.primary ? `<button type="button" class="btn btn-sm btn-outline" style="font-size:0.72rem;padding:3px 8px;" onclick="window.setPhotoPrimary(${photo.id})">Make Primary</button>` : '<span></span>'}
        <button type="button" class="btn btn-sm btn-outline" style="color:var(--danger);font-size:0.72rem;padding:3px 8px;" onclick="window.deleteGalleryPhoto(${photo.id})">Delete</button>
      </div>
    </div>
  `).join('');
}

window.setPhotoPrimary = async function(photoId) {
  try {
    const res = await api.patch(`/admin/photos/${photoId}/primary`, {});
    showToast('Primary photo updated!', 'success');
    document.getElementById('dest-primary-img').value = res.data.imageUrl;
    const params = new URLSearchParams(window.location.search);
    await loadDestinationIntoForm(params.get('id'));
  } catch (err) {
    showToast('Failed to set primary photo', 'error');
  }
};

window.deleteGalleryPhoto = async function(photoId) {
  if (confirm('Delete this photo from the gallery?')) {
    try {
      await api.delete(`/admin/photos/${photoId}`);
      showToast('Photo removed', 'success');
      const params = new URLSearchParams(window.location.search);
      await loadDestinationIntoForm(params.get('id'));
    } catch (err) {
      showToast('Failed to delete photo', 'error');
    }
  }
};
