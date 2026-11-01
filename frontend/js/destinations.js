// Visakha Tourism Destination Catalog Controller (Instant Cache-First Architecture)

import { api, showToast } from './api.js';
import { FALLBACK_CATEGORIES } from './categories-data.js';
import { FALLBACK_DESTINATIONS } from './destinations-data.js';

// State management
let allDestinations = loadInitialDestinations();
let currentFilteredList = [];
let currentPage = 0;
const pageSize = 18;
let currentCategory = null;
let currentSearch = '';
let currentSort = 'name';
const loadedIds = new Set();

function loadInitialDestinations() {
  try {
    const cached = localStorage.getItem('vt_cached_destinations_v1');
    if (cached) {
      const parsed = JSON.parse(cached);
      if (Array.isArray(parsed) && parsed.length >= FALLBACK_DESTINATIONS.length) {
        return parsed;
      }
    }
  } catch (e) {
    // ignore parse error
  }
  return [...FALLBACK_DESTINATIONS];
}

document.addEventListener('DOMContentLoaded', () => {
  readUrlParams();
  loadCategoriesFilter();
  initSearchAndFilterListeners();
  initLoadMoreListener();

  // Instant render in 0 milliseconds!
  applyFiltersAndRender(false);

  // Background non-blocking sync with backend for any fresh admin additions/edits
  syncWithBackend();
});

function readUrlParams() {
  const params = new URLSearchParams(window.location.search);
  const searchParam = params.get('search');
  const catParam = params.get('category');

  if (searchParam) {
    currentSearch = searchParam.trim();
    const searchInput = document.getElementById('catalog-search-input');
    if (searchInput) searchInput.value = currentSearch;
  }

  if (catParam) {
    currentCategory = catParam;
  }
}

function renderCategoryPills(categories, container) {
  if (!container || !categories) return;
  container.innerHTML = '';

  const allPill = document.createElement('button');
  allPill.className = `filter-pill ${!currentCategory ? 'active' : ''}`;
  allPill.textContent = 'All Destinations';
  allPill.dataset.catId = '';
  allPill.addEventListener('click', () => selectCategory(null));
  container.appendChild(allPill);

  categories.forEach(cat => {
    const pill = document.createElement('button');
    pill.className = `filter-pill ${currentCategory == cat.id ? 'active' : ''}`;
    pill.textContent = cat.name;
    pill.dataset.catId = cat.id;
    pill.addEventListener('click', () => selectCategory(cat.id));
    container.appendChild(pill);
  });
}

async function loadCategoriesFilter() {
  const container = document.getElementById('category-filter-pills');
  if (!container) return;

  // Immediately populate with verified categories
  renderCategoryPills(FALLBACK_CATEGORIES, container);

  try {
    const res = await api.get('/categories');
    const categories = res.data || [];
    if (categories && categories.length > 0) {
      renderCategoryPills(categories, container);
    }
  } catch (err) {
    // Fallback categories already active
  }
}

function selectCategory(catId) {
  currentCategory = catId;
  currentPage = 0;

  // Update pills UI
  document.querySelectorAll('#category-filter-pills .filter-pill').forEach(btn => {
    btn.classList.toggle('active', (btn.dataset.catId === (catId ? String(catId) : '')));
  });

  updateUrl();
  applyFiltersAndRender(false);
}

function initSearchAndFilterListeners() {
  const searchInput = document.getElementById('catalog-search-input');
  const sortSelect = document.getElementById('catalog-sort-select');

  let debounceTimer;
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => {
        currentSearch = e.target.value.trim();
        currentPage = 0;
        updateUrl();
        applyFiltersAndRender(false);
      }, 100); // Super fast 100ms response
    });
  }

  if (sortSelect) {
    sortSelect.addEventListener('change', (e) => {
      currentSort = e.target.value;
      currentPage = 0;
      applyFiltersAndRender(false);
    });
  }
}

function initLoadMoreListener() {
  const loadMoreBtn = document.getElementById('load-more-btn');
  if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', () => {
      currentPage++;
      applyFiltersAndRender(true);
    });
  }
}

function updateUrl() {
  const params = new URLSearchParams();
  if (currentSearch) params.set('search', currentSearch);
  if (currentCategory) params.set('category', currentCategory);

  const newUrl = `${window.location.pathname}${params.toString() ? '?' + params.toString() : ''}`;
  window.history.replaceState({}, '', newUrl);
}

function getFilteredList() {
  let list = allDestinations.filter(d => d.published !== false);

  if (currentCategory) {
    list = list.filter(d => String(d.categoryId) === String(currentCategory));
  }

  if (currentSearch) {
    const q = currentSearch.toLowerCase();
    list = list.filter(d =>
      (d.name && d.name.toLowerCase().includes(q)) ||
      (d.shortDescription && d.shortDescription.toLowerCase().includes(q)) ||
      (d.address && d.address.toLowerCase().includes(q)) ||
      (d.categoryName && d.categoryName.toLowerCase().includes(q))
    );
  }

  if (currentSort === 'name') {
    list.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
  } else if (currentSort === 'name_desc') {
    list.sort((a, b) => (b.name || '').localeCompare(a.name || ''));
  }

  return list;
}

function applyFiltersAndRender(isAppend = false) {
  const grid = document.getElementById('destinations-grid');
  const loadMoreBtn = document.getElementById('load-more-btn');
  const loadMoreContainer = document.getElementById('load-more-container');
  const countIndicator = document.getElementById('results-count-text');

  if (!isAppend) {
    loadedIds.clear();
    currentFilteredList = getFilteredList();
    if (grid) grid.innerHTML = '';
  }

  if (currentFilteredList.length === 0) {
    if (grid) {
      grid.innerHTML = `
        <div class="state-message" style="grid-column: 1/-1;">
          <div class="state-icon">🔍</div>
          <h3 class="state-title">No destinations found</h3>
          <p class="state-desc">We couldn't find any destinations matching "${currentSearch || 'your filter'}". Try adjusting your search query or selecting a different category.</p>
          <button id="reset-filters-btn" class="btn btn-outline">Clear all filters</button>
        </div>
      `;
      document.getElementById('reset-filters-btn')?.addEventListener('click', () => {
        currentSearch = '';
        currentCategory = null;
        const searchInput = document.getElementById('catalog-search-input');
        if (searchInput) searchInput.value = '';
        selectCategory(null);
      });
    }

    if (countIndicator) countIndicator.textContent = 'Showing 0 destinations';
    if (loadMoreContainer) loadMoreContainer.style.display = 'none';
    return;
  }

  const start = currentPage * pageSize;
  const end = start + pageSize;
  const slice = currentFilteredList.slice(start, end);

  slice.forEach(dest => {
    if (!loadedIds.has(dest.id)) {
      loadedIds.add(dest.id);
      const card = createDestinationCard(dest);
      grid?.appendChild(card);
    }
  });

  if (countIndicator) {
    countIndicator.textContent = `Showing ${loadedIds.size} of ${currentFilteredList.length} destinations`;
  }

  // Load More Button handling
  if (loadMoreContainer && loadMoreBtn) {
    const remaining = currentFilteredList.length - loadedIds.size;
    if (remaining <= 0) {
      loadMoreContainer.style.display = 'none';
    } else {
      loadMoreContainer.style.display = 'block';
      loadMoreBtn.disabled = false;
      loadMoreBtn.innerHTML = `Load More Destinations (${remaining} remaining)`;
    }
  }
}

function createDestinationCard(dest) {
  const card = document.createElement('div');
  card.className = 'destination-card';
  card.innerHTML = `
    <div class="card-media">
      <img src="${dest.primaryImageUrl}" alt="${dest.name}" class="card-img" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80'">
      <span class="card-category-badge">${dest.categoryName || 'Attraction'}</span>
      ${dest.featured ? '<span class="card-featured-badge">★ Featured</span>' : ''}
    </div>
    <div class="card-body">
      <h3 class="card-title">
        <a href="/destination-details.html?slug=${dest.slug}">${dest.name}</a>
      </h3>
      <div class="card-location">
        <span>📍</span>
        <span>${dest.address}</span>
      </div>
      <p class="card-desc">${dest.shortDescription}</p>
      <div class="card-footer">
        <a href="${dest.directionsUrl || ('https://www.google.com/maps/dir/?api=1&destination=' + encodeURIComponent(dest.address))}" target="_blank" rel="noopener noreferrer" class="card-directions-btn" title="Open Google Maps directions from your location">
          🗺️ Directions
        </a>
        <a href="/destination-details.html?slug=${dest.slug}" class="btn btn-sm btn-outline">
          View Details →
        </a>
      </div>
    </div>
  `;
  return card;
}

// Background sync with live backend
async function syncWithBackend() {
  try {
    const res = await api.get('/destinations?page=0&size=100');
    const items = res?.data?.content || [];

    if (items.length > 0) {
      // Merge with in-memory destinations
      const map = new Map(allDestinations.map(d => [d.id, d]));
      let hasChanges = false;

      items.forEach(item => {
        const existing = map.get(item.id);
        if (!existing || existing.name !== item.name || existing.updatedAt !== item.updatedAt) {
          hasChanges = true;
          map.set(item.id, {
            ...existing,
            ...item
          });
        }
      });

      if (hasChanges) {
        allDestinations = Array.from(map.values());
        try {
          localStorage.setItem('vt_cached_destinations_v1', JSON.stringify(allDestinations));
        } catch (e) {}

        // Smoothly refresh current view
        applyFiltersAndRender(false);
      }
    }
  } catch (err) {
    // Silent fail in background: verified dataset is already active and displayed
    console.debug('Background sync postponed, local cache fully operational.');
  }
}
