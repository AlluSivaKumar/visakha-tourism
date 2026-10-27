// Visakha Tourism Itineraries & Budget Planner Controller

import { api, showToast } from './api.js';

let allPlans = [];
let selectedDays = 3; // Default to 3 days
let selectedTier = 'mid'; // 'budget', 'mid', 'luxury'
let groupSize = 2; // Default couple (2 persons)

document.addEventListener('DOMContentLoaded', () => {
  loadItineraries();
  initBudgetControls();
});

async function loadItineraries() {
  const container = document.getElementById('itinerary-display-container');

  try {
    const res = await api.get('/itineraries');
    allPlans = res.data || [];

    if (allPlans.length === 0) {
      container.innerHTML = '<div class="state-message"><p>No itinerary plans available.</p></div>';
      return;
    }

    // Check if URL has a specific day parameter (e.g. ?days=4)
    const params = new URLSearchParams(window.location.search);
    const dayParam = parseInt(params.get('days'), 10);
    if ([2, 3, 4, 5].includes(dayParam)) {
      selectedDays = dayParam;
    }

    renderPlanTabs();
    renderCurrentPlan();

  } catch (err) {
    console.error('Failed to load itineraries:', err);
    if (container) {
      container.innerHTML = `
        <div class="state-message">
          <p>Failed to load travel plans. Please check server connection.</p>
          <button onclick="window.location.reload()" class="btn btn-primary btn-sm">Retry</button>
        </div>
      `;
    }
  }
}

function renderPlanTabs() {
  const tabsContainer = document.getElementById('itinerary-tabs');
  if (!tabsContainer) return;

  const tabConfigs = [
    { days: 2, label: '2 Days Weekend', icon: '⚡' },
    { days: 3, label: '3 Days Classic', icon: '🌊' },
    { days: 4, label: '4 Days Coast & Araku', icon: '⛰️' },
    { days: 5, label: '5 Days Grand Expedition', icon: '🌟' }
  ];

  tabsContainer.innerHTML = tabConfigs.map(tab => `
    <button class="itinerary-tab-btn ${tab.days === selectedDays ? 'active' : ''}" data-days="${tab.days}">
      <span style="font-size:1.2rem;">${tab.icon}</span>
      <span>${tab.label}</span>
    </button>
  `).join('');

  tabsContainer.querySelectorAll('.itinerary-tab-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const days = parseInt(btn.dataset.days, 10);
      if (days !== selectedDays) {
        selectedDays = days;
        tabsContainer.querySelectorAll('.itinerary-tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        // Update URL without reload
        const newUrl = `${window.location.pathname}?days=${days}`;
        window.history.replaceState({}, '', newUrl);

        renderCurrentPlan();
      }
    });
  });
}

function initBudgetControls() {
  // Tier buttons
  document.querySelectorAll('.tier-toggle-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tier-toggle-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      selectedTier = btn.dataset.tier;
      updateBudgetDisplay();
    });
  });

  // Group size buttons
  document.querySelectorAll('.group-size-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.group-size-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      groupSize = parseInt(btn.dataset.size, 10);
      updateBudgetDisplay();
    });
  });
}

function renderCurrentPlan() {
  const plan = allPlans.find(p => p.days === selectedDays);
  if (!plan) return;

  // Header Details
  document.getElementById('plan-title').textContent = plan.title;
  document.getElementById('plan-tagline').textContent = plan.tagline;
  document.getElementById('plan-overview').textContent = plan.overview;
  document.getElementById('plan-audience').textContent = plan.targetAudience;

  // Transit Summary Tips
  const transitTipsContainer = document.getElementById('transit-tips-container');
  if (transitTipsContainer && plan.transitSummary) {
    document.getElementById('primary-transport-recommendation').textContent = plan.transitSummary.primaryTransportRecommendation;
    transitTipsContainer.innerHTML = (plan.transitSummary.travelTips || []).map(tip => `
      <li style="margin-bottom:6px;">✓ ${tip}</li>
    `).join('');
  }

  // Update Budget Calculator
  updateBudgetDisplay();

  // Render Day-by-Day Timeline
  renderDayTimeline(plan.dayPlans);
}

function updateBudgetDisplay() {
  const plan = allPlans.find(p => p.days === selectedDays);
  if (!plan || !plan.budget) return;

  let tierData;
  if (selectedTier === 'budget') {
    tierData = plan.budget.budgetTier;
  } else if (selectedTier === 'luxury') {
    tierData = plan.budget.luxuryTier;
  } else {
    tierData = plan.budget.midRangeTier;
  }

  const perPerson = tierData.totalPerPerson;
  const totalCost = perPerson * groupSize;

  document.getElementById('budget-total-cost').textContent = `₹${totalCost.toLocaleString('en-IN')}`;
  document.getElementById('budget-per-person').textContent = `(Approx. ₹${perPerson.toLocaleString('en-IN')} per person for ${groupSize} ${groupSize === 1 ? 'traveler' : 'travelers'})`;

  // Itemized breakdown
  const staySubtotal = tierData.stayCost * groupSize;
  const transportSubtotal = tierData.transportCost * groupSize;
  const foodSubtotal = tierData.foodCost * groupSize;
  const activitiesSubtotal = tierData.ticketsAndActivities * groupSize;

  document.getElementById('cost-stay').textContent = `₹${staySubtotal.toLocaleString('en-IN')}`;
  document.getElementById('cost-transport').textContent = `₹${transportSubtotal.toLocaleString('en-IN')}`;
  document.getElementById('cost-food').textContent = `₹${foodSubtotal.toLocaleString('en-IN')}`;
  document.getElementById('cost-activities').textContent = `₹${activitiesSubtotal.toLocaleString('en-IN')}`;

  document.getElementById('desc-stay').textContent = tierData.stayDescription;
  document.getElementById('desc-transport').textContent = tierData.transportDescription;
}

function renderDayTimeline(dayPlans) {
  const timelineContainer = document.getElementById('day-plans-timeline');
  if (!timelineContainer) return;

  timelineContainer.innerHTML = dayPlans.map(day => `
    <div class="day-plan-card">
      <div class="day-plan-header">
        <div class="day-badge">DAY ${day.dayNumber}</div>
        <div style="flex:1;">
          <h3 class="day-title">${day.title}</h3>
          <div class="day-zone">🗺️ <strong>Geographic Corridor:</strong> ${day.geographicZone}</div>
        </div>
      </div>

      <p class="day-route-desc">${day.routeDescription}</p>

      <!-- Geographic Sequence List -->
      <div class="stops-list">
        <h4 style="font-size:0.92rem; text-transform:uppercase; letter-spacing:0.05em; color:var(--text-muted); margin-bottom:14px;">
          Sequential Visiting Order (Minimizes Driving Time)
        </h4>

        ${day.stops.map(stop => `
          <div class="stop-item">
            <div class="stop-number">${stop.sequence}</div>
            <div class="stop-body">
              <div class="stop-top-bar">
                <a href="/destination-details.html?slug=${stop.slug}" class="stop-name" target="_blank">${stop.placeName}</a>
                <span class="stop-time">⏱️ ${stop.timeSlot} (${stop.duration})</span>
              </div>
              <div class="stop-highlight">${stop.highlight}</div>
              <div class="stop-geo-note">
                <span style="color:#0284c7;font-weight:700;">Geographic Strategy:</span> ${stop.geographicOrderNote}
              </div>
              <div style="margin-top:8px;">
                <a href="${stop.directionsUrl}" target="_blank" rel="noopener noreferrer" class="card-directions-btn" style="font-size:0.78rem;padding:4px 10px;">
                  🗺️ Google Maps Navigation
                </a>
              </div>
            </div>
          </div>
        `).join('')}
      </div>

      <!-- Transport Advice for this Day -->
      <div class="day-transit-card">
        <div style="font-weight:700; color:#0369a1; margin-bottom:6px; display:flex; align-items:center; gap:8px;">
          <span>🚗</span> Best Way to Travel This Day:
        </div>
        <div style="font-size:0.95rem; font-weight:600; color:var(--text-main); margin-bottom:4px;">
          ${day.transitAdvice.recommendedMode}
        </div>
        <p style="font-size:0.88rem; color:var(--text-muted); line-height:1.5; margin-bottom:8px;">
          ${day.transitAdvice.routeDirections}
        </p>
        <div style="display:flex; flex-wrap:wrap; gap:16px; font-size:0.85rem; border-top:1px solid #bae6fd; padding-top:8px;">
          <span style="color:#0284c7;"><strong>Est. Day Transit Cost:</strong> ${day.transitAdvice.estimatedTransitCost}</span>
          <span style="color:#0f766e;"><strong>Pro Tip:</strong> ${day.transitAdvice.proTip}</span>
        </div>
      </div>

      <!-- Food Recommendations -->
      <div class="day-food-box">
        🍽️ <strong>Cuisine & Dining Recommendation:</strong> ${day.foodRecommendations}
      </div>
    </div>
  `).join('');
}
