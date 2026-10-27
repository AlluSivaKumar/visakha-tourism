// Visakha Tourism Destination Details Page Controller

import { api, showToast } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(window.location.search);
  const slug = params.get('slug');

  if (!slug) {
    showErrorState('No destination specified', 'Please select a destination from the catalog.');
    return;
  }

  loadDestinationDetails(slug);
  initShareButton();
  initLightbox();
});

async function loadDestinationDetails(slug) {
  const container = document.getElementById('details-content');

  try {
    const res = await api.get(`/destinations/${slug}`);
    const dest = res.data;

    if (!dest) {
      showErrorState('Destination Not Found', 'The requested travel destination could not be found.');
      return;
    }

    // Set page title
    document.title = `${dest.name} - Visakha Tourism`;

    // Render Hero Banner
    document.getElementById('detail-hero-img').src = dest.primaryImageUrl;
    document.getElementById('detail-title').textContent = dest.name;
    document.getElementById('detail-category-badge').textContent = dest.category?.name || 'Attraction';
    document.getElementById('detail-address').textContent = dest.address;

    // Directions Button
    const directionsBtn = document.getElementById('detail-directions-btn');
    if (directionsBtn) {
      directionsBtn.href = dest.directionsUrl;
      directionsBtn.title = `Get directions to ${dest.name} on Google Maps`;
    }

    // Full Description
    document.getElementById('detail-description').textContent = dest.fullDescription;

    // Key facts
    document.getElementById('fact-hours').textContent = dest.openingHours || 'Information not specified';
    document.getElementById('fact-price').textContent = dest.ticketPrice || 'Free / Not applicable';
    document.getElementById('fact-best-time').textContent = dest.bestTimeToVisit || 'October to March';
    
    // Coordinates
    const coordsEl = document.getElementById('fact-coords');
    if (dest.latitude && dest.longitude) {
      coordsEl.textContent = `${dest.latitude}° N, ${dest.longitude}° E (Verified GPS)`;
    } else {
      coordsEl.textContent = 'Coordinates unavailable';
    }

    // Things to do
    const thingsToDoCard = document.getElementById('things-to-do-card');
    const thingsToDoText = document.getElementById('detail-things-to-do');
    if (dest.thingsToDo) {
      thingsToDoText.textContent = dest.thingsToDo;
    } else if (thingsToDoCard) {
      thingsToDoCard.style.display = 'none';
    }

    // Travel tips
    const travelTipsCard = document.getElementById('travel-tips-card');
    const travelTipsText = document.getElementById('detail-travel-tips');
    if (dest.travelTips) {
      travelTipsText.textContent = dest.travelTips;
    } else if (travelTipsCard) {
      travelTipsCard.style.display = 'none';
    }

    // Render Photo Gallery
    renderGallery(dest.photos, dest.primaryImageUrl, dest.name);

    // Fetch and render Nearby Attractions
    loadNearbyAttractions(dest.id);

  } catch (err) {
    console.error('Failed to load destination details:', err);
    showErrorState('Failed to Load Destination', 'We could not connect to the server. Please try again later.');
  }
}

function renderGallery(photos, primaryUrl, destName) {
  const galleryContainer = document.getElementById('detail-gallery-grid');
  const galleryCard = document.getElementById('gallery-card');
  if (!galleryContainer) return;

  const allPhotos = [...(photos || [])];
  if (allPhotos.length === 0 && primaryUrl) {
    allPhotos.push({ imageUrl: primaryUrl, caption: destName });
  }

  if (allPhotos.length === 0) {
    if (galleryCard) galleryCard.style.display = 'none';
    return;
  }

  galleryContainer.innerHTML = allPhotos.map(photo => `
    <div class="gallery-item" onclick="window.openLightbox('${photo.imageUrl}')">
      <img src="${photo.imageUrl}" alt="${photo.altText || destName}" loading="lazy" onerror="this.src='${primaryUrl}'">
    </div>
  `).join('');
}

async function loadNearbyAttractions(destinationId) {
  const container = document.getElementById('nearby-grid');
  if (!container) return;

  try {
    const res = await api.get(`/destinations/${destinationId}/nearby?limit=4`);
    const nearby = res.data || [];

    if (nearby.length === 0) {
      container.innerHTML = '<p class="text-muted">No nearby attractions recorded within proximity.</p>';
      return;
    }

    container.innerHTML = nearby.map(item => `
      <div class="nearby-card">
        <div class="nearby-media">
          <img src="${item.primaryImageUrl}" alt="${item.name}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80'">
          <span class="nearby-distance-badge">📍 ${item.distanceFormatted}</span>
        </div>
        <div class="nearby-body">
          <h4 class="nearby-title">
            <a href="/destination-details.html?slug=${item.slug}">${item.name}</a>
          </h4>
          <p class="nearby-desc">${item.shortDescription}</p>
          <div style="display:flex;justify-content:space-between;align-items:center;margin-top:auto;">
            <a href="${item.directionsUrl}" target="_blank" rel="noopener noreferrer" style="font-size:0.8rem;font-weight:600;color:var(--primary);">
              🗺️ Map Directions
            </a>
            <a href="/destination-details.html?slug=${item.slug}" class="btn btn-sm btn-outline">
              View
            </a>
          </div>
        </div>
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load nearby destinations:', err);
    container.innerHTML = '<p class="text-muted">Unable to load nearby attractions.</p>';
  }
}

function initShareButton() {
  const shareBtn = document.getElementById('share-btn');
  if (shareBtn) {
    shareBtn.addEventListener('click', async () => {
      if (navigator.share) {
        try {
          await navigator.share({
            title: document.title,
            text: `Explore ${document.title} on Visakha Tourism!`,
            url: window.location.href
          });
        } catch (e) {
          // ignore share cancel
        }
      } else {
        await navigator.clipboard.writeText(window.location.href);
        showToast('Destination link copied to clipboard!', 'success');
      }
    });
  }
}

function initLightbox() {
  window.openLightbox = function(imageUrl) {
    const modal = document.getElementById('lightbox-modal');
    const img = document.getElementById('lightbox-img');
    if (modal && img) {
      img.src = imageUrl;
      modal.classList.add('active');
    }
  };

  const closeBtn = document.getElementById('lightbox-close');
  const modal = document.getElementById('lightbox-modal');
  if (closeBtn && modal) {
    closeBtn.addEventListener('click', () => modal.classList.remove('active'));
    modal.addEventListener('click', (e) => {
      if (e.target === modal) modal.classList.remove('active');
    });
  }
}

function showErrorState(title, message) {
  const main = document.querySelector('main');
  if (main) {
    main.innerHTML = `
      <div class="container" style="padding: 80px 20px;">
        <div class="state-message">
          <div class="state-icon">🏖️</div>
          <h2 class="state-title">${title}</h2>
          <p class="state-desc">${message}</p>
          <a href="/destinations.html" class="btn btn-primary">Browse All Destinations</a>
        </div>
      </div>
    `;
  }
}
