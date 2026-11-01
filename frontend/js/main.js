// Visakha Tourism Main Homepage Controller (Instant Render Architecture)

import { api } from './api.js';
import { FALLBACK_CATEGORIES } from './categories-data.js';
import { FALLBACK_DESTINATIONS } from './destinations-data.js';

document.addEventListener('DOMContentLoaded', () => {
  initHeroSearch();
  loadHomepageCategories();
  loadFeaturedDestinations();
});

function initHeroSearch() {
  const form = document.getElementById('hero-search-form');
  const input = document.getElementById('hero-search-input');

  if (form && input) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const query = input.value.trim();
      if (query) {
        window.location.href = `/destinations.html?search=${encodeURIComponent(query)}`;
      } else {
        window.location.href = '/destinations.html';
      }
    });
  }
}

const iconMap = {
  'beaches-coastline': '🏖️',
  'hills-nature': '⛰️',
  'heritage-culture': '🏛️',
  'museums-monuments': '🚢',
  'spiritual-temples': '🛕',
  'waterfalls-adventure': '🌊',
  'parks-wildlife': '🐅'
};

function renderCategoriesList(categories, container) {
  if (!categories || categories.length === 0) return;
  container.innerHTML = categories.map(cat => {
    const emoji = iconMap[cat.slug] || '📍';
    return `
      <a href="/destinations.html?category=${cat.id}" class="category-card">
        <div class="category-icon">${emoji}</div>
        <span class="category-name">${cat.name}</span>
      </a>
    `;
  }).join('');
}

async function loadHomepageCategories() {
  const container = document.getElementById('categories-container');
  if (!container) return;

  // Immediately render verified fallback categories so user never sees "Unable to load categories"
  renderCategoriesList(FALLBACK_CATEGORIES, container);

  try {
    const res = await api.get('/categories');
    const categories = res.data || [];
    if (categories && categories.length > 0) {
      renderCategoriesList(categories, container);
    }
  } catch (err) {
    console.warn('Backend categories endpoint warming up, fallback active:', err);
  }
}

function renderFeaturedGrid(destinations, container) {
  if (!container || !destinations || destinations.length === 0) return;

  container.innerHTML = destinations.slice(0, 6).map(dest => `
    <div class="destination-card">
      <div class="card-media">
        <img src="${dest.primaryImageUrl}" alt="${dest.name}" class="card-img" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80'">
        <span class="card-category-badge">${dest.categoryName || 'Attraction'}</span>
        <span class="card-featured-badge">★ Featured</span>
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
          <a href="${dest.directionsUrl || ('https://www.google.com/maps/dir/?api=1&destination=' + encodeURIComponent(dest.address))}" target="_blank" rel="noopener noreferrer" class="card-directions-btn" title="Open Google Maps directions">
            🗺️ Directions
          </a>
          <a href="/destination-details.html?slug=${dest.slug}" class="btn btn-sm btn-outline">
            Explore →
          </a>
        </div>
      </div>
    </div>
  `).join('');
}

async function loadFeaturedDestinations() {
  const container = document.getElementById('featured-grid');
  if (!container) return;

  // 1. Immediately render featured destinations from verified dataset (0ms!)
  const initialFeatured = FALLBACK_DESTINATIONS.filter(d => d.featured);
  renderFeaturedGrid(initialFeatured, container);

  // 2. Background sync with backend
  try {
    const res = await api.get('/destinations/featured');
    const destinations = res.data || [];
    if (destinations.length > 0) {
      renderFeaturedGrid(destinations, container);
    }
  } catch (err) {
    // Verified fallback is already visible and interactive
    console.debug('Background featured sync skipped, fallback active');
  }
}
