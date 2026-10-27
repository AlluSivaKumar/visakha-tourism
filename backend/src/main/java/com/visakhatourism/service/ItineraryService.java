package com.visakhatourism.service;

import com.visakhatourism.dto.ItineraryDtos.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ItineraryService {

    public List<ItineraryPlanDto> getAllItineraries() {
        return Arrays.asList(
                createTwoDayPlan(),
                createThreeDayPlan(),
                createFourDayPlan(),
                createFiveDayPlan()
        );
    }

    public ItineraryPlanDto getItineraryByDays(int days) {
        return switch (days) {
            case 2 -> createTwoDayPlan();
            case 3 -> createThreeDayPlan();
            case 4 -> createFourDayPlan();
            case 5 -> createFiveDayPlan();
            default -> createThreeDayPlan();
        };
    }

    // ==========================================
    // 2-DAY PLAN: COASTAL & HERITAGE WEEKEND
    // ==========================================
    private ItineraryPlanDto createTwoDayPlan() {
        ItineraryPlanDto plan = new ItineraryPlanDto();
        plan.setDays(2);
        plan.setId("2-days-coastal-heritage");
        plan.setTitle("2-Day Coastal & Heritage Weekend");
        plan.setTagline("The essential seaside highlights, maritime naval marvels, and Bheemili scenic marine drive.");
        plan.setOverview("Perfect for weekend travelers. Covers the continuous Beach Road promenade on Day 1 without transit backtracking, and the breathtaking Vizag-Bheemili coastal drive on Day 2.");
        plan.setTargetAudience("Couples, solo travelers, and weekend trippers from Hyderabad, Odisha, and Bengaluru.");

        // Budget breakdown for 2 days
        BudgetEstimateDto budget = new BudgetEstimateDto(
                new TierBudgetDto("Budget / Backpacker", 2800, 1200, 500, 800, 300,
                        "Budget hotel / AC dormitory near RTC Complex or Beach Road", "City RTC buses and shared autos"),
                new TierBudgetDto("Mid-Range (Most Popular)", 6500, 3200, 1500, 1400, 400,
                        "3-Star sea-view hotel on Beach Road or Waltair Uplands", "Rented scooter/two-wheeler or auto-rickshaw hire"),
                new TierBudgetDto("Luxury / Comfort", 15000, 8500, 3500, 2400, 600,
                        "4/5-Star beachfront resort (Novotel / Radisson Blu)", "Dedicated chauffeur-driven AC sedan")
        );
        plan.setBudget(budget);

        List<DayPlanDto> days = new ArrayList<>();

        // Day 1
        DayPlanDto d1 = new DayPlanDto();
        d1.setDayNumber(1);
        d1.setTitle("Day 1: Central Coastal Promenade & Naval Heritage Circuit");
        d1.setGeographicZone("South to North along Beach Road (4 km continuous strip)");
        d1.setRouteDescription("Start south at Ramakrishna Beach and progress northward toward Kailasagiri Hill without any backtracking.");
        d1.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Ramakrishna Beach (RK Beach)", "ramakrishna-beach",
                        "6:30 AM - 8:30 AM", "2 hrs", "Morning sunrise over the Bay of Bengal, seaside tea, beach promenade walk",
                        "Start at the southern anchor of the coastal promenade when weather is delightfully cool.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7143000,83.3236000"),
                new ItineraryStopDto(2, "Matsyadarshini Aquarium", "matsyadarshini-aquarium",
                        "9:00 AM - 10:15 AM", "1.25 hrs", "Marine biodiversity of Bay of Bengal and freshwater river ecosystems",
                        "Located 200m north of RK Beach. An easy 3-minute walk along the promenade pavement.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7153000,83.3256000"),
                new ItineraryStopDto(3, "Victory at Sea War Memorial", "victory-at-sea-memorial",
                        "10:30 AM - 11:15 AM", "45 mins", "Honors the 1971 sinking of PNS Ghazi submarine; military naval relics",
                        "Right across the road heading north; zero transit delay.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7158000,83.3275000"),
                new ItineraryStopDto(4, "INS Kursura Submarine & TU-142 Aircraft Museum", "ins-kursura-submarine-museum",
                        "2:00 PM - 4:30 PM", "2.5 hrs", "Walk inside an actual Cold War Soviet submarine and naval flight cockpit",
                        "Directly adjacent to each other on the beach sands. Museums reopen at 2:00 PM.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7172000,83.3315000"),
                new ItineraryStopDto(5, "Kailasagiri Hilltop Park (Ropeway)", "kailasagiri",
                        "5:00 PM - 7:30 PM", "2.5 hrs", "Take the cable car ropeway for 360-degree sunset vistas of the ocean and port city",
                        "Located 3 km north of the submarine museum; end your day with sunset lights.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7490000,83.3423000")
        ));
        d1.setTransitAdvice(new TransitAdviceDto(
                "Walking + Auto-rickshaw (or hired two-wheeler)",
                "Beach Road is linear. Walk between museums, take an auto from Submarine Museum to Kailasagiri foothills (INR 80-120).",
                "INR 150 - 250 for the entire day",
                "Visit Kailasagiri ropeway by 4:45 PM to avoid weekend cable car queues and catch the golden hour."
        ));
        d1.setFoodRecommendations("Lunch: Authentic spicy Andhra fish curry and Royyala vepudu at Sea Inn (Raju Gari Dhaba) or Beach Road cafés. Evening: Piping hot murri mixture and sweet roasted corn on RK Beach sands.");
        days.add(d1);

        // Day 2
        DayPlanDto d2 = new DayPlanDto();
        d2.setDayNumber(2);
        d2.setTitle("Day 2: Bheemili Marine Drive & Buddhist Coastal Heritage");
        d2.setGeographicZone("Northeast Coastal Highway (Vizag to Bheemunipatnam: 25 km)");
        d2.setRouteDescription("Travel along the famous marine highway, stopping at cliffs, Blue Flag surf beach, 2000-year-old Buddhist stupas, and colonial Dutch town.");
        d2.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Tenneti Park", "tenneti-park",
                        "7:00 AM - 8:30 AM", "1.5 hrs", "Cliff-side sea park overlooking rocky beaches and coastal waves",
                        "First stop leaving the city northward along Bheemili Beach Road.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7592000,83.3533000"),
                new ItineraryStopDto(2, "Rushikonda Beach", "rushikonda-beach",
                        "9:00 AM - 12:00 PM", "3 hrs", "International Blue Flag certified sands; jet skiing, speed boating, sea swimming",
                        "Continues 3 km north along the coastline. Safe swimming zones with lifeguards.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7825000,83.3852000"),
                new ItineraryStopDto(3, "Thotlakonda Buddhist Complex", "thotlakonda-buddhism",
                        "2:00 PM - 3:30 PM", "1.5 hrs", "2,000-year-old hilltop Buddhist monastic stupas and rainwater cisterns",
                        "Perched atop a coastal hillock just 4 km north of Rushikonda overlooking the ocean.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.8286000,83.4147000"),
                new ItineraryStopDto(4, "Erra Matti Dibbalu (Red Sand Dunes)", "erra-matti-dibbalu",
                        "3:45 PM - 5:00 PM", "1.25 hrs", "Rare geological heritage marvel of dramatic crimson coastal canyons",
                        "Situated 3 km north of Thotlakonda before Bheemili town.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.8481000,83.4286000"),
                new ItineraryStopDto(5, "Bheemili Beach & Dutch Heritage Town", "bheemili-beach",
                        "5:15 PM - 7:00 PM", "1.75 hrs", "Second oldest municipality in India; 17th-century Dutch cemetery, 1868 lighthouse, calm sunset",
                        "Northern terminus of the coastal drive where Gosthani River meets the Bay of Bengal.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.8895000,83.4548000")
        ));
        d2.setTransitAdvice(new TransitAdviceDto(
                "Rented Two-Wheeler (Activa/Bike) or Private Cab",
                "Drive along the Vizag-Bheemili Beach Road (NH16 bypass). Smooth four-lane highway with breathtaking ocean views on the right.",
                "INR 400 - 500 for scooter rental/petrol, or INR 1,200 - 1,800 for dedicated taxi",
                "Start early to avoid noon heat at the Buddhist ruins, as hilltop shade is minimal."
        ));
        d2.setFoodRecommendations("Lunch: Fresh coastal seafood and crab fry at beach shacks in Rushikonda or Haritha Rushikonda Resort. Evening: Tea and hot mirchi bajji at Bheemili beach.");
        days.add(d2);

        plan.setDayPlans(days);
        plan.setTransitSummary(new TransitSummaryDto(
                "Rented Scooter/Motorcycle (INR 400-500/day) or Ola/Uber outstation cab",
                Arrays.asList(
                        "Beach Road is very flat and easy to navigate; two-wheelers provide complete freedom.",
                        "Bheemili Marine Drive has continuous ocean views and multiple designated viewpoints.",
                        "Swimming is prohibited at RK Beach due to undertows; always swim at Blue Flag Rushikonda."
                )
        ));
        return plan;
    }

    // ==========================================
    // 3-DAY PLAN: COAST, SACRED HILLS & CAVES
    // ==========================================
    private ItineraryPlanDto createThreeDayPlan() {
        ItineraryPlanDto plan = new ItineraryPlanDto();
        plan.setDays(3);
        plan.setId("3-days-coast-temples-caves");
        plan.setTitle("3-Day Coast, Sacred Hills & Karst Caves");
        plan.setTagline("The classic Visakhapatnam tour adding ancient hilltop shrines and million-year-old subterranean caves.");
        plan.setOverview("Combines the full coastal experience of Vizag city with the spiritual majesty of Simhachalam Temple and a day excursion into the Eastern Ghats to explore Borra Caves.");
        plan.setTargetAudience("Families, cultural explorers, and first-time visitors seeking the complete city and nature blend.");

        BudgetEstimateDto budget = new BudgetEstimateDto(
                new TierBudgetDto("Budget / Backpacker", 4200, 1800, 900, 1100, 400,
                        "Budget hotel / homestay near Dwaraka Nagar (RTC complex)", "Local RTC buses (Route 28A, 6A) + passenger train to Borra"),
                new TierBudgetDto("Mid-Range (Most Popular)", 9800, 4800, 2400, 2000, 600,
                        "3-Star hotel in Siripuram / Beach Road", "Self-drive car or day taxi hire for Borra Caves day"),
                new TierBudgetDto("Luxury / Comfort", 22500, 12500, 5500, 3600, 900,
                        "Luxury sea-facing resort in Vizag", "Private AC Innova with experienced ghat driver")
        );
        plan.setBudget(budget);

        List<DayPlanDto> days = new ArrayList<>(createTwoDayPlan().getDayPlans());

        // Day 3: Simhachalam & Borra Caves
        DayPlanDto d3 = new DayPlanDto();
        d3.setDayNumber(3);
        d3.setTitle("Day 3: Simhachalam Temple & Borra Karst Caves Excursion");
        d3.setGeographicZone("Northwest Inland towards Eastern Ghats (Simhachalam 16km, Borra Caves 88km)");
        d3.setRouteDescription("Head northwest to the sacred Simhachalam hill range early morning, then ascend the Ananthagiri Ghats to Borra Caves.");
        d3.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Sri Varaha Lakshmi Narasimha Temple (Simhachalam)", "simhachalam-temple",
                        "6:30 AM - 9:30 AM", "3 hrs", "11th-century medieval Kalinga architecture, ornate stone pillars, sandalwood darshan",
                        "Located 16 km northwest of city center. Visit early to avoid pilgrim queues and heat.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7663000,83.2505000"),
                new ItineraryStopDto(2, "Kambalakonda Wildlife Sanctuary", "kambalakonda-wildlife",
                        "10:30 AM - 12:00 PM", "1.5 hrs", "Dry evergreen forest reserve, hanging canopy bridge, barking deer and leopards",
                        "Situated on NH16 on the route connecting Simhachalam to the ghat highway.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7889000,83.3389000"),
                new ItineraryStopDto(3, "Tyda Jungle Bells & Ananthagiri Viewpoint", "tyda-jungle-bells",
                        "1:30 PM - 2:30 PM", "1 hr", "Lush pine mountain passes, coffee plantation aroma, forest lodge stopover",
                        "Midway point on the Vizag-Araku ghat road at 60 km mark; ideal lunch break.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2197000,83.0531000"),
                new ItineraryStopDto(4, "Borra Caves (Ananthagiri)", "borra-caves",
                        "3:00 PM - 5:30 PM", "2.5 hrs", "Million-year-old stalactite and stalagmite limestone cave formations, Gosthani river source",
                        "Deepest caves in India, located at 705m elevation; illuminated with artistic lights.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2809000,83.0396000")
        ));
        d3.setTransitAdvice(new TransitAdviceDto(
                "Private Day Cab (Sedan / Innova) or Vistadome Train + Local Taxi",
                "Take NH16 to Simhachalam, then follow Araku Road (SH9) through Srungavarapukota (S.Kota) up the Ananthagiri ghats.",
                "INR 2,500 - 3,200 for a round-trip day cab from Vizag to Borra Caves",
                "Ghat road has 22 sharp hairpin curves. If prone to motion sickness, take medication before ascending."
        ));
        d3.setFoodRecommendations("Lunch: Try herbal meals at Tyda Jungle Bells or fresh local bamboo chicken prepared on coal outside Borra Caves. Taste pure Araku filter coffee at the mountain viewpoints.");
        days.add(d3);

        plan.setDayPlans(days);
        plan.setTransitSummary(new TransitSummaryDto(
                "Combination of City Rental for Days 1-2 + Hired Cab for Day 3",
                Arrays.asList(
                        "Dress code for Simhachalam Temple: Traditional attire (dhoti/kurta for men, saree/salwar for women).",
                        "Borra Caves closes ticket counters by 4:30 PM; ensure you arrive by 3:00 PM.",
                        "Book the scenic Vistadome train (Vizag to Borra Caves) 30 days in advance as seats fill rapidly."
                )
        ));
        return plan;
    }

    // ==========================================
    // 4-DAY PLAN: COAST & ARAKU VALLEY EXPEDITION
    // ==========================================
    private ItineraryPlanDto createFourDayPlan() {
        ItineraryPlanDto plan = new ItineraryPlanDto();
        plan.setDays(4);
        plan.setId("4-days-coast-araku-valley");
        plan.setTitle("4-Day Coast & Araku Valley Highland Expedition");
        plan.setTagline("Experience golden beaches followed by an overnight mountain escape in the Eastern Ghats.");
        plan.setOverview("The quintessential Andhra travel experience. Spend 2 days on the coastline and 2 days immersed in the cool mountain climate of Araku Valley, staying overnight in highland coffee country.");
        plan.setTargetAudience("Families, nature enthusiasts, photographers, and vacationers seeking both mountain and coastal serenity.");

        BudgetEstimateDto budget = new BudgetEstimateDto(
                new TierBudgetDto("Budget / Backpacker", 6200, 2600, 1400, 1600, 600,
                        "Budget hotel in Vizag + tribal valley homestay in Araku", "Vistadome / passenger train to Araku + shared jeeps"),
                new TierBudgetDto("Mid-Range (Most Popular)", 14500, 6800, 3800, 2800, 1100,
                        "Haritha Valley Resort (AP Tourism) or eco-cottage in Araku", "Hired AC Sedan for 4 days or self-drive vehicle"),
                new TierBudgetDto("Luxury / Comfort", 32000, 17000, 8500, 4800, 1700,
                        "Luxury coastal resort + luxury valley villa/cottage in Araku", "Dedicated private SUV (Innova Crysta) throughout the 4 days")
        );
        plan.setBudget(budget);

        List<DayPlanDto> days = new ArrayList<>(createTwoDayPlan().getDayPlans());

        // Day 3: Vizag to Araku via Borra Caves & Katiki Waterfalls
        DayPlanDto d3 = new DayPlanDto();
        d3.setDayNumber(3);
        d3.setTitle("Day 3: Ascend to Araku Valley via Borra Caves & Katiki Falls");
        d3.setGeographicZone("Eastern Ghats Highway (Vizag $\\rightarrow$ S.Kota $\\rightarrow$ Ananthagiri $\\rightarrow$ Araku: 114 km)");
        d3.setRouteDescription("Journey from sea level to 1,000 meters altitude. Explore million-year-old caves and a thrilling forest waterfall jeep trek before checking into your Araku valley stay.");
        d3.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Vistadome Train Journey into Eastern Ghats", "borra-caves",
                        "6:45 AM - 10:00 AM", "3.25 hrs", "Panoramic glass roof train through 58 mountain tunnels and deep gorge bridges",
                        "Board Train #18551 from Visakhapatnam Railway Station to Borra Caves or Araku.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2809000,83.0396000"),
                new ItineraryStopDto(2, "Borra Caves", "borra-caves",
                        "10:30 AM - 12:30 PM", "2 hrs", "Illuminated stalactites, stalagmites, natural subterranean Shiva lingam",
                        "Located 4 km from Borra railway station. Tour before the afternoon tourist rush.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2809000,83.0396000"),
                new ItineraryStopDto(3, "Katiki Waterfalls (Jeep Safari & Trek)", "katiki-waterfalls",
                        "1:30 PM - 4:00 PM", "2.5 hrs", "50-foot cascading mountain falls, natural swimming pool, off-road forest track",
                        "Jeep stands are located near Borra Caves gate; 20-minute off-road trail.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2917000,83.0117000"),
                new ItineraryStopDto(4, "Ananthagiri Coffee Plantations & Viewpoint", "ananthagiri-hills",
                        "4:30 PM - 5:45 PM", "1.25 hrs", "Misty valley viewpoints, spice gardens, organic coffee shrubs under shade trees",
                        "On the ascent to Araku town at 1,168 meters altitude.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.2435000,83.0076000")
        ));
        d3.setTransitAdvice(new TransitAdviceDto(
                "Vistadome Train to Borra + Local 4x4 Jeep for Katiki + Private Cab to Araku",
                "The Vistadome train provides world-class views. For Katiki falls, only local 4x4 commercial jeeps are permitted due to boulder terrain.",
                "Train ticket: INR 650 (Vistadome) / Jeep safari: INR 250-300 per head",
                "Carry extra clothes for Katiki Waterfalls if you plan to wade in the cold natural pools."
        ));
        d3.setFoodRecommendations("Lunch: Authentic spicy chicken or vegetable bamboo dishes cooked over wood charcoal at Katiki. Evening: Fresh hot organic Araku Arabica coffee.");
        days.add(d3);

        // Day 4: Araku Valley Indigenous Heritage & Cascades
        DayPlanDto d4 = new DayPlanDto();
        d4.setDayNumber(4);
        d4.setTitle("Day 4: Araku Tribal Heritage & Chaparai Cascades $\\rightarrow$ Return to Vizag");
        d4.setGeographicZone("Araku Valley Basin & Descent to Visakhapatnam (115 km)");
        d4.setRouteDescription("Explore indigenous culture, exotic botanical gardens, natural water cascades, then descend the scenic ghats back to Vizag.");
        d4.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Padmapuram Botanical Gardens", "padmapuram-gardens",
                        "8:30 AM - 10:00 AM", "1.5 hrs", "Historic World War II gardens, suspended tree huts, nursery flowers, toy train",
                        "Located in Araku town center. Stroll through early morning mountain dew.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.3361000,82.8711000"),
                new ItineraryStopDto(2, "Araku Valley Tribal Museum", "araku-tribal-museum",
                        "10:15 AM - 12:00 PM", "1.75 hrs", "Indigenous Eastern Ghats lifestyle, Dhimsa dance, archery range, organic coffee outlet",
                        "Directly adjacent to the main highway in Araku.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.3333000,82.8833000"),
                new ItineraryStopDto(3, "Chaparai Water Cascades (Dumbriguda)", "chaparai-waterfalls",
                        "12:30 PM - 2:30 PM", "2 hrs", "Gentle mountain stream flowing over smooth, sloping granite rock beds",
                        "Located 15 km northwest of Araku town in Dumbriguda mandal.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.3150000,82.7690000"),
                new ItineraryStopDto(4, "Tatipudi Reservoir Scenic View (En Route Vizag)", "tatipudi-reservoir",
                        "5:00 PM - 6:00 PM", "1 hr", "Emerald mountain reservoir built across Gosthani river, speed boating, sunset",
                        "Located at the foothills on the descent towards Vizag near Gantyada.",
                        "https://www.google.com/maps/dir/?api=1&destination=18.1722000,83.1889000")
        ));
        d4.setTransitAdvice(new TransitAdviceDto(
                "Private Cab or Self-Drive Car descending via S.Kota Ghat Road",
                "Begin your descent from Araku by 3:00 PM to negotiate the ghat section during daylight hours before sunset fog rolls in.",
                "Estimated cab cost: INR 3,500 - 4,500 for full Araku 2-day hire",
                "Stock up on authentic certified Araku organic coffee and pure forest honey directly at the Tribal Co-operative store."
        ));
        d4.setFoodRecommendations("Lunch: Traditional Andhra meals at Haritha Valley restaurant or bamboo biryani near Chaparai cascades. Evening: Snacks at Tatipudi dam viewpoint.");
        days.add(d4);

        plan.setDayPlans(days);
        plan.setTransitSummary(new TransitSummaryDto(
                "Private Taxi / Self-Drive SUV + Vistadome Train One-Way",
                Arrays.asList(
                        "Take the morning Vistadome train up to Araku and hire a car to return down the ghats for the best of both worlds.",
                        "Araku temperatures drop to 12°C - 16°C in evenings; carry light woolens or jackets.",
                        "Pre-book Haritha Valley Resort or private homestays in Araku at least 2 weeks in advance."
                )
        ));
        return plan;
    }

    // ==========================================
    // 5-DAY PLAN: THE GRAND EXPEDITION
    // ==========================================
    private ItineraryPlanDto createFiveDayPlan() {
        ItineraryPlanDto plan = new ItineraryPlanDto();
        plan.setDays(5);
        plan.setId("5-days-grand-expedition");
        plan.setTitle("5-Day Grand Vizag, Araku & Southern Heritage Expedition");
        plan.setTagline("The definitive tour covering beaches, naval museums, sacred shrines, Araku Valley, and Lambasingi or freshwater lotus wetlands.");
        plan.setOverview("No stone left unturned. Experience the vibrant city coast, Bheemili heritage, high-altitude Eastern Ghats, and the south coastal wetlands of Kondakarla Ava and Bojjannakonda rock-cut monasteries.");
        plan.setTargetAudience("Long-vacation travelers, families, corporate retreats, and avid travel explorers.");

        BudgetEstimateDto budget = new BudgetEstimateDto(
                new TierBudgetDto("Budget / Backpacker", 8500, 3600, 1900, 2100, 900,
                        "Budget hotels / homestays in Vizag, Araku, and Anakapalle", "RTC express buses, shared autos, and local jeeps"),
                new TierBudgetDto("Mid-Range (Most Popular)", 19500, 9000, 5200, 3800, 1500,
                        "Comfortable 3-star sea-view and valley-view stays", "Dedicated rental vehicle with driver for 5 days"),
                new TierBudgetDto("Luxury / Comfort", 44000, 23000, 12000, 6500, 2500,
                        "Premium 5-star beachfront suites & luxury valley resorts", "Private luxury SUV (Innova Crysta) throughout with dedicated chauffeur")
        );
        plan.setBudget(budget);

        List<DayPlanDto> days = new ArrayList<>(createFourDayPlan().getDayPlans());

        // Day 5: Southern Coast, Kondakarla Ava Lake & Buddhist Monasteries
        DayPlanDto d5 = new DayPlanDto();
        d5.setDayNumber(5);
        d5.setTitle("Day 5: Kondakarla Ava Lotus Sanctuary, Bojjannakonda & Dolphin's Nose");
        d5.setGeographicZone("South Visakhapatnam District (Gajuwaka $\\rightarrow$ Anakapalle $\\rightarrow$ Kondakarla Ava: 45 km)");
        d5.setRouteDescription("Journey south along the coastal corridor: dawn boat ride among lotus beds at Kondakarla Ava, 4th-century Buddhist monasteries, and panoramic port views from Dolphin's Nose Lighthouse.");
        d5.setStops(Arrays.asList(
                new ItineraryStopDto(1, "Kondakarla Ava Freshwater Lake & Bird Sanctuary", "kondakarla-ava",
                        "6:30 AM - 9:30 AM", "3 hrs", "Andhra's 2nd largest freshwater lake; country raft boat ride gliding through blooming pink lotuses and migratory birds",
                        "Located 45 km south of Vizag. Early dawn is magical with mist and pink lotus blooms.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.5925000,82.9972000"),
                new ItineraryStopDto(2, "Bojjannakonda & Lingalakonda Buddhist Rock-Cut Caves", "bojjannakonda-caves",
                        "10:15 AM - 12:30 PM", "2.25 hrs", "4th-9th century CE monolithic stupas and meditation caves carved into living hills",
                        "Situated 18 km north of Kondakarla Ava near Anakapalle along the return route.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7139000,83.0161000"),
                new ItineraryStopDto(3, "Sri Kanaka Mahalakshmi Temple & Old Town Heritage", "kanaka-mahalakshmi-temple",
                        "2:30 PM - 3:45 PM", "1.25 hrs", "Beloved open-air protector deity of Visakhapatnam in the historic Burujupeta quarter",
                        "On the inner approach into old Vizag port town.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.7011000,83.2981000"),
                new ItineraryStopDto(4, "Dolphin's Nose & Blackmores Hill Lighthouse", "dolphins-nose",
                        "4:15 PM - 6:00 PM", "1.75 hrs", "358-meter ocean cliff headland, functioning 64-km optical maritime lighthouse, harbor breakwater views",
                        "Southern headland guarding Vizag harbor entrance. Lighthouse is open 3:00 PM - 5:00 PM.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.6749000,83.2941000"),
                new ItineraryStopDto(5, "Yarada Beach Sunset", "yarada-beach",
                        "6:15 PM - 7:30 PM", "1.25 hrs", "Secluded golden sands tucked behind Yarada hills, crashing surf, peaceful dusk",
                        "Directly below Dolphin's Nose on the southern seaward slope.",
                        "https://www.google.com/maps/dir/?api=1&destination=17.6548000,83.2704000")
        ));
        d5.setTransitAdvice(new TransitAdviceDto(
                "Private Cab or Self-Drive Car via NH16 South & Scindia Port Road",
                "Take NH16 towards Anakapalle/Atchutapuram for the morning lake and caves. In the afternoon, return via Scindia / Yarada Ghat Road.",
                "Estimated cab cost: INR 2,200 - 2,800 for full day southern circuit",
                "Lighthouse entry closes strictly at 5:00 PM; reach Dolphin Nose summit by 4:15 PM."
        ));
        d5.setFoodRecommendations("Breakfast: Hot idlis and ghee dosas near Atchutapuram / Anakapalle. Lunch: Authentic Andhra thali in Gajuwaka. Dinner: Beachside dinner in Vizag celebrating your 5-day tour.");
        days.add(d5);

        plan.setDayPlans(days);
        plan.setTransitSummary(new TransitSummaryDto(
                "Dedicated Cab / Self-Drive Vehicle for Maximum Comfort across 5 Days",
                Arrays.asList(
                        "Alternative for Day 5: Travelers seeking chilly weather can replace the southern lake circuit with Lambasingi (Kashmir of AP) and Kothapalli Waterfalls.",
                        "Kondakarla Ava country boat rides use traditional tied palm trunks; sit in the middle and wear life jackets.",
                        "Always check lighthouse operational timings before making the drive up Dolphin Nose."
                )
        ));
        return plan;
    }
}
