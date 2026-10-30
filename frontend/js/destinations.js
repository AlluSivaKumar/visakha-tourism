// Visakha Tourism Destination Catalog Controller (Server-side Pagination & Load More)

import { api, showToast } from './api.js';
import { FALLBACK_CATEGORIES } from './categories-data.js';

// State management
let currentPage = 0;
const pageSize = 20; // 20 initial and subsequent items
let currentCategory = null;
let currentSearch = '';
let currentSort = 'name';
let isLoading = false;
let isLastPage = false;
const loadedDestinationIds = new Set();

document.addEventListener('DOMContentLoaded', () => {
  readUrlParams();
  loadCategoriesFilter();
  initSearchAndFilterListeners();
  initLoadMoreListener();
  fetchDestinations(false);
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

  // Immediately populate with fallback categories
  renderCategoryPills(FALLBACK_CATEGORIES, container);

  try {
    const res = await api.get('/categories');
    const categories = res.data || [];
    if (categories && categories.length > 0) {
      renderCategoryPills(categories, container);
    }
  } catch (err) {
    console.warn('Backend categories endpoint warming up, fallback active:', err);
  }
}

function selectCategory(catId) {
  currentCategory = catId;
  currentPage = 0;
  loadedDestinationIds.clear();

  // Update pills UI
  document.querySelectorAll('#category-filter-pills .filter-pill').forEach(btn => {
    btn.classList.toggle('active', (btn.dataset.catId === (catId ? String(catId) : '')));
  });

  updateUrl();
  fetchDestinations(false);
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
        loadedDestinationIds.clear();
        updateUrl();
        fetchDestinations(false);
      }, 400);
    });
  }

  if (sortSelect) {
    sortSelect.addEventListener('change', (e) => {
      currentSort = e.target.value;
      currentPage = 0;
      loadedDestinationIds.clear();
      fetchDestinations(false);
    });
  }
}

function initLoadMoreListener() {
  const loadMoreBtn = document.getElementById('load-more-btn');
  if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', () => {
      if (!isLoading && !isLastPage) {
        currentPage++;
        fetchDestinations(true);
      }
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

async function fetchDestinations(isAppend = false) {
  if (isLoading) return;
  isLoading = true;

  const grid = document.getElementById('destinations-grid');
  const loadMoreBtn = document.getElementById('load-more-btn');
  const loadMoreContainer = document.getElementById('load-more-container');
  const countIndicator = document.getElementById('results-count-text');

  if (loadMoreBtn) {
    loadMoreBtn.disabled = true;
    loadMoreBtn.innerHTML = '<span class="spinner"></span> Loading destinations...';
  }

  if (!isAppend && grid) {
    grid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 60px 0;">
        <div class="spinner spinner-primary" style="width:36px;height:36px;"></div>
        <p style="margin-top:14px;color:var(--text-muted);font-weight:600;">Exploring Visakhapatnam destinations...</p>
      </div>
    `;
  }

  try {
    const params = new URLSearchParams({
      page: currentPage,
      size: pageSize,
      sort: currentSort
    });

    if (currentCategory) params.append('categoryId', currentCategory);
    if (currentSearch) params.append('search', currentSearch);

    const res = await api.get(`/destinations?${params.toString()}`);
    const pageData = res.data || { content: [], totalElements: 0, last: true };
    const items = pageData.content || [];
    isLastPage = pageData.last;

    if (!isAppend && grid) {
      grid.innerHTML = '';
    }

    if (countIndicator) {
      countIndicator.textContent = `Showing ${loadedDestinationIds.size + items.length} of ${pageData.totalElements} destinations`;
    }

    if (items.length === 0 && !isAppend) {
      grid.innerHTML = `
        <div class="state-message" style="grid-column: 1/-1;">
          <div class="state-icon">🔍</div>
          <h3 class="state-title">No destinations found</h3>
          <p class="state-desc">We couldn't find any destinations matching "${currentSearch || 'your filter'}". Try adjusting your search or category filter.</p>
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

      if (loadMoreContainer) loadMoreContainer.style.display = 'none';
      return;
    }

    // Append new unique destination cards
    items.forEach(dest => {
      if (!loadedDestinationIds.has(dest.id)) {
        loadedDestinationIds.add(dest.id);
        const card = createDestinationCard(dest);
        grid.appendChild(card);
      }
    });

    // Handle Load More button visibility and state
    if (loadMoreContainer && loadMoreBtn) {
      if (isLastPage || loadedDestinationIds.size >= pageData.totalElements) {
        loadMoreContainer.style.display = 'none';
      } else {
        loadMoreContainer.style.display = 'block';
        loadMoreBtn.disabled = false;
        loadMoreBtn.innerHTML = `Load More Destinations (${pageData.totalElements - loadedDestinationIds.size} remaining)`;
      }
    }

  } catch (err) {
    console.error('Failed to load destinations:', err);
    showToast('Failed to load destinations. Please check your connection.', 'error');
    if (!isAppend && grid) {
      grid.innerHTML = `
        <div class="state-message" style="grid-column: 1/-1;">
          <div class="state-icon">⚠️</div>
          <h3 class="state-title">Error Loading Destinations</h3>
          <p class="state-desc">Something went wrong while retrieving the catalog from the server.</p>
          <button onclick="window.location.reload()" class="btn btn-primary">Try Again</button>
        </div>
      `;
    }
  } finally {
    isLoading = false;
  }
}

function createDestinationCard(dest) {
  const card = document.createElement('div');
  card.className = 'destination-card';
  card.innerHTML = `
    <div class="card-media">
      <img src="${dest.primaryImageUrl}" alt="${dest.name}" class="card-img" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80'">
      <span class="card-category-badge">${dest.categoryName}</span>
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
        <a href="${dest.directionsUrl}" target="_blank" rel="noopener noreferrer" class="card-directions-btn" title="Open Google Maps directions from your location">
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
