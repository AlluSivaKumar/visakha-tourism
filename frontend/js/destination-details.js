// Visakha Tourism Destination Details Page Controller (Instant Cache-First Architecture)

import { api, showToast } from './api.js';
import { FALLBACK_DESTINATIONS } from './destinations-data.js';

let currentDestination = null;

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

function findLocalDestination(slug) {
  try {
    const cached = localStorage.getItem('vt_cached_destinations_v1');
    if (cached) {
      const list = JSON.parse(cached);
      const found = list.find(d => d.slug === slug);
      if (found) return found;
    }
  } catch (e) {}
  return FALLBACK_DESTINATIONS.find(d => d.slug === slug);
}

function renderDestinationData(dest) {
  currentDestination = dest;

  // Set page title
  document.title = `${dest.name} - Visakha Tourism`;

  // Render Hero Banner
  const heroImg = document.getElementById('detail-hero-img');
  if (heroImg) heroImg.src = dest.primaryImageUrl;

  const titleEl = document.getElementById('detail-title');
  if (titleEl) titleEl.textContent = dest.name;

  const catBadge = document.getElementById('detail-category-badge');
  if (catBadge) catBadge.textContent = dest.categoryName || dest.category?.name || 'Attraction';

  const addrEl = document.getElementById('detail-address');
  if (addrEl) addrEl.textContent = dest.address;

  // Directions Button
  const directionsBtn = document.getElementById('detail-directions-btn');
  if (directionsBtn) {
    directionsBtn.href = dest.directionsUrl || (`https://www.google.com/maps/dir/?api=1&destination=${dest.latitude},${dest.longitude}`);
    directionsBtn.title = `Get directions to ${dest.name} on Google Maps`;
  }

  // Description
  const descEl = document.getElementById('detail-description');
  if (descEl) descEl.textContent = dest.fullDescription || dest.shortDescription;

  // Key facts
  const hoursEl = document.getElementById('fact-hours');
  if (hoursEl) hoursEl.textContent = dest.openingHours || 'Open daily';

  const priceEl = document.getElementById('fact-price');
  if (priceEl) priceEl.textContent = dest.ticketPrice || 'Free entry / Nominal';

  const bestTimeEl = document.getElementById('fact-best-time');
  if (bestTimeEl) bestTimeEl.textContent = dest.bestTimeToVisit || 'October to March';

  // GPS Coordinates
  const coordsEl = document.getElementById('fact-coords');
  if (coordsEl) {
    if (dest.latitude && dest.longitude) {
      coordsEl.textContent = `${dest.latitude}° N, ${dest.longitude}° E (Verified GPS)`;
    } else {
      coordsEl.textContent = '17.7° N, 83.3° E';
    }
  }

  // Things to Do Card
  const thingsCard = document.getElementById('things-to-do-card');
  const thingsText = document.getElementById('detail-things-to-do');
  if (thingsCard && thingsText) {
    if (dest.thingsToDo) {
      thingsText.textContent = dest.thingsToDo;
      thingsCard.style.display = 'block';
    } else {
      thingsCard.style.display = 'none';
    }
  }

  // Travel Tips Card
  const tipsCard = document.getElementById('travel-tips-card');
  const tipsText = document.getElementById('detail-travel-tips');
  if (tipsCard && tipsText) {
    if (dest.travelTips) {
      tipsText.textContent = dest.travelTips;
      tipsCard.style.display = 'block';
    } else {
      tipsCard.style.display = 'none';
    }
  }

  // Photo Gallery
  renderGallery(dest.photos, dest.primaryImageUrl, dest.name);

  // Curated Tour Packages
  renderTourPackages(dest);

  // Nearby attractions
  loadNearbyAttractions(dest.id, dest.latitude, dest.longitude);
}

async function loadDestinationDetails(slug) {
  // 1. Instant render from verified cache (0ms!)
  const localDest = findLocalDestination(slug);
  if (localDest) {
    renderDestinationData(localDest);
  }

  // 2. Background sync with backend for fresh media / admin updates
  try {
    const res = await api.get(`/destinations/${slug}`);
    const serverDest = res.data;

    if (serverDest) {
      renderDestinationData(serverDest);
    } else if (!localDest) {
      showErrorState('Destination Not Found', 'The requested travel destination could not be found.');
    }
  } catch (err) {
    if (!localDest) {
      showErrorState('Failed to Load Destination', 'We could not connect to the server. Please try again later.');
    }
  }
}

function renderTourPackages(dest) {
  const container = document.getElementById('packages-list');
  if (!container) return;

  const adminWhatsApp = '916281429422';
  const adminEmails = 'sivakumarallu4@gmail.com,rakeshparagada2004@gmail.com';

  const packages = [
    {
      badge: 'Popular Day Tour',
      badgeClass: 'package-badge-popular',
      title: 'Vizag City Heritage & Coastal Day Tour',
      duration: '1 Full Day (8:00 AM – 7:30 PM)',
      price: '₹1,499',
      priceSub: 'per person (Private AC Cab included)',
      highlights: [
        'Explore Ramakrishna Beach, Submarine Museum & Aircraft Museum',
        'Panoramic views from Kailasagiri Hill via passenger ropeway',
        'Visit ancient Buddhist monastery Thotlakonda & Rushikonda Beach',
        'All entry passes, AC private transport & coastal lunch included'
      ],
      whatsappMsg: `Hello Siva Kumar & Rakesh, I want to book the "Vizag City Heritage & Coastal Day Tour" for ${dest.name}. Please share availability.`
    },
    {
      badge: 'Hill Station & Nature',
      badgeClass: 'package-badge-adventure',
      title: 'Araku Valley & Borra Caves Mountain Expedition',
      duration: '2 Days / 1 Night Hill Resort Stay',
      price: '₹3,999',
      priceSub: 'per person (Scenic Vistadome Train + Resort)',
      highlights: [
        'Scenic Vistadome glass-ceiling train through 54 mountain tunnels',
        'Explore million-year-old Borra Caves and Katiki forest waterfalls',
        'Coffee plantation walk, Padmapuram Botanical Gardens & Tribal Museum',
        'Hilltop resort stay with campfire and authentic Andhra meals'
      ],
      whatsappMsg: `Hello Siva Kumar & Rakesh, I am interested in the 2-Day "Araku Valley & Borra Caves Expedition" related to ${dest.name}. Please provide details.`
    },
    {
      badge: 'Winter Special',
      badgeClass: 'package-badge-special',
      title: 'Eastern Ghats Mist & Lambasingi Winter Circuit',
      duration: '3 Days / 2 Nights Scenic Adventure',
      price: '₹6,499',
      priceSub: 'per person (Valley Cottages + Sunrise Trek)',
      highlights: [
        'Experience Lambasingi mist orchards ("Kashmir of Andhra Pradesh")',
        'Vanjangi cloud-ocean sunrise trek with experienced guide',
        'Visit sacred Sri Varaha Lakshmi Narasimha Temple at Simhachalam',
        'All transfers in private SUV, valley cottage stay & gourmet feasts'
      ],
      whatsappMsg: `Hello Siva Kumar & Rakesh, I want to inquire about the 3-Day "Eastern Ghats Mist & Lambasingi Tour" for ${dest.name}.`
    }
  ];

  container.innerHTML = packages.map(pkg => `
    <div class="package-card">
      <div class="package-header">
        <div>
          <span class="package-badge ${pkg.badgeClass}">${pkg.badge}</span>
          <h3 class="package-title">${pkg.title}</h3>
          <div class="package-duration">⏱ ${pkg.duration}</div>
        </div>
        <div class="package-price">
          <div class="package-price-val">${pkg.price}</div>
          <div class="package-price-sub">${pkg.priceSub}</div>
        </div>
      </div>

      <ul class="package-highlights">
        ${pkg.highlights.map(h => `<li>${h}</li>`).join('')}
      </ul>

      <div class="package-actions">
        <a href="https://wa.me/${adminWhatsApp}?text=${encodeURIComponent(pkg.whatsappMsg)}" target="_blank" rel="noopener noreferrer" class="btn btn-sm btn-primary" style="background:#25D366; border-color:#25D366; display:inline-flex; align-items:center; gap:6px;">
          💬 Book / Inquire via WhatsApp
        </a>
        <a href="mailto:${adminEmails}?subject=Tour%20Package%20Inquiry:%20${encodeURIComponent(pkg.title)}&body=${encodeURIComponent(pkg.whatsappMsg)}" class="btn btn-sm btn-outline">
          ✉ Inquire via Email
        </a>
        <a href="tel:+916281429422" class="btn btn-sm btn-outline" title="Call Visakha Tourism Concierge">
          📞 +91 6281429422
        </a>
      </div>
    </div>
  `).join('');
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

  if (galleryCard) galleryCard.style.display = 'block';
  galleryContainer.innerHTML = allPhotos.map(photo => `
    <div class="gallery-item" onclick="window.openLightbox('${photo.imageUrl}')">
      <img src="${photo.imageUrl}" alt="${photo.altText || destName}" loading="lazy" onerror="this.src='${primaryUrl}'">
    </div>
  `).join('');
}

function loadNearbyAttractions(destinationId, currentLat, currentLng) {
  const container = document.getElementById('nearby-grid');
  if (!container) return;

  // Immediate local calculation using Haversine formula
  const others = FALLBACK_DESTINATIONS.filter(d => d.id !== destinationId);
  if (currentLat && currentLng) {
    others.forEach(d => {
      d._dist = haversineDistance(currentLat, currentLng, d.latitude, d.longitude);
      d._distFormatted = d._dist < 1 ? `${Math.round(d._dist * 1000)} m away` : `${d._dist.toFixed(1)} km away`;
    });
    others.sort((a, b) => a._dist - b._dist);
  }

  const nearby = others.slice(0, 4);
  renderNearbyItems(nearby, container);

  // Background server fetch
  api.get(`/destinations/${destinationId}/nearby?limit=4`).then(res => {
    if (res.data && res.data.length > 0) {
      renderNearbyItems(res.data, container);
    }
  }).catch(() => {});
}

function renderNearbyItems(items, container) {
  container.innerHTML = items.map(item => `
    <div class="nearby-card">
      <div class="nearby-media">
        <img src="${item.primaryImageUrl}" alt="${item.name}" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80'">
        <span class="nearby-distance-badge">📍 ${item.distanceFormatted || item._distFormatted || 'Nearby'}</span>
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
}

function haversineDistance(lat1, lon1, lat2, lon2) {
  const R = 6371; // Earth's radius in km
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  return R * c;
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
