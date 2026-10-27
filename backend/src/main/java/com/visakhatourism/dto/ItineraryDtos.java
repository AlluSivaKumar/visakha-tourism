package com.visakhatourism.dto;

import java.util.List;
import java.util.Map;

public class ItineraryDtos {

    public static class ItineraryPlanDto {
        private int days;
        private String id;
        private String title;
        private String tagline;
        private String overview;
        private String targetAudience;
        private BudgetEstimateDto budget;
        private List<DayPlanDto> dayPlans;
        private TransitSummaryDto transitSummary;

        public ItineraryPlanDto() {}

        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getTagline() { return tagline; }
        public void setTagline(String tagline) { this.tagline = tagline; }
        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }
        public String getTargetAudience() { return targetAudience; }
        public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
        public BudgetEstimateDto getBudget() { return budget; }
        public void setBudget(BudgetEstimateDto budget) { this.budget = budget; }
        public List<DayPlanDto> getDayPlans() { return dayPlans; }
        public void setDayPlans(List<DayPlanDto> dayPlans) { this.dayPlans = dayPlans; }
        public TransitSummaryDto getTransitSummary() { return transitSummary; }
        public void setTransitSummary(TransitSummaryDto transitSummary) { this.transitSummary = transitSummary; }
    }

    public static class BudgetEstimateDto {
        private String currency = "INR";
        private TierBudgetDto budgetTier;
        private TierBudgetDto midRangeTier;
        private TierBudgetDto luxuryTier;

        public BudgetEstimateDto() {}

        public BudgetEstimateDto(TierBudgetDto budgetTier, TierBudgetDto midRangeTier, TierBudgetDto luxuryTier) {
            this.budgetTier = budgetTier;
            this.midRangeTier = midRangeTier;
            this.luxuryTier = luxuryTier;
        }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public TierBudgetDto getBudgetTier() { return budgetTier; }
        public void setBudgetTier(TierBudgetDto budgetTier) { this.budgetTier = budgetTier; }
        public TierBudgetDto getMidRangeTier() { return midRangeTier; }
        public void setMidRangeTier(TierBudgetDto midRangeTier) { this.midRangeTier = midRangeTier; }
        public TierBudgetDto getLuxuryTier() { return luxuryTier; }
        public void setLuxuryTier(TierBudgetDto luxuryTier) { this.luxuryTier = luxuryTier; }
    }

    public static class TierBudgetDto {
        private String tierName;
        private int totalPerPerson;
        private int stayCost;
        private int transportCost;
        private int foodCost;
        private int ticketsAndActivities;
        private String stayDescription;
        private String transportDescription;

        public TierBudgetDto() {}

        public TierBudgetDto(String tierName, int totalPerPerson, int stayCost, int transportCost, int foodCost, int ticketsAndActivities, String stayDescription, String transportDescription) {
            this.tierName = tierName;
            this.totalPerPerson = totalPerPerson;
            this.stayCost = stayCost;
            this.transportCost = transportCost;
            this.foodCost = foodCost;
            this.ticketsAndActivities = ticketsAndActivities;
            this.stayDescription = stayDescription;
            this.transportDescription = transportDescription;
        }

        public String getTierName() { return tierName; }
        public void setTierName(String tierName) { this.tierName = tierName; }
        public int getTotalPerPerson() { return totalPerPerson; }
        public void setTotalPerPerson(int totalPerPerson) { this.totalPerPerson = totalPerPerson; }
        public int getStayCost() { return stayCost; }
        public void setStayCost(int stayCost) { this.stayCost = stayCost; }
        public int getTransportCost() { return transportCost; }
        public void setTransportCost(int transportCost) { this.transportCost = transportCost; }
        public int getFoodCost() { return foodCost; }
        public void setFoodCost(int foodCost) { this.foodCost = foodCost; }
        public int getTicketsAndActivities() { return ticketsAndActivities; }
        public void setTicketsAndActivities(int ticketsAndActivities) { this.ticketsAndActivities = ticketsAndActivities; }
        public String getStayDescription() { return stayDescription; }
        public void setStayDescription(String stayDescription) { this.stayDescription = stayDescription; }
        public String getTransportDescription() { return transportDescription; }
        public void setTransportDescription(String transportDescription) { this.transportDescription = transportDescription; }
    }

    public static class DayPlanDto {
        private int dayNumber;
        private String title;
        private String geographicZone;
        private String routeDescription;
        private List<ItineraryStopDto> stops;
        private TransitAdviceDto transitAdvice;
        private String foodRecommendations;

        public DayPlanDto() {}

        public int getDayNumber() { return dayNumber; }
        public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getGeographicZone() { return geographicZone; }
        public void setGeographicZone(String geographicZone) { this.geographicZone = geographicZone; }
        public String getRouteDescription() { return routeDescription; }
        public void setRouteDescription(String routeDescription) { this.routeDescription = routeDescription; }
        public List<ItineraryStopDto> getStops() { return stops; }
        public void setStops(List<ItineraryStopDto> stops) { this.stops = stops; }
        public TransitAdviceDto getTransitAdvice() { return transitAdvice; }
        public void setTransitAdvice(TransitAdviceDto transitAdvice) { this.transitAdvice = transitAdvice; }
        public String getFoodRecommendations() { return foodRecommendations; }
        public void setFoodRecommendations(String foodRecommendations) { this.foodRecommendations = foodRecommendations; }
    }

    public static class ItineraryStopDto {
        private int sequence;
        private String placeName;
        private String slug;
        private String timeSlot;
        private String duration;
        private String highlight;
        private String geographicOrderNote;
        private String directionsUrl;

        public ItineraryStopDto() {}

        public ItineraryStopDto(int sequence, String placeName, String slug, String timeSlot, String duration, String highlight, String geographicOrderNote, String directionsUrl) {
            this.sequence = sequence;
            this.placeName = placeName;
            this.slug = slug;
            this.timeSlot = timeSlot;
            this.duration = duration;
            this.highlight = highlight;
            this.geographicOrderNote = geographicOrderNote;
            this.directionsUrl = directionsUrl;
        }

        public int getSequence() { return sequence; }
        public void setSequence(int sequence) { this.sequence = sequence; }
        public String getPlaceName() { return placeName; }
        public void setPlaceName(String placeName) { this.placeName = placeName; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getHighlight() { return highlight; }
        public void setHighlight(String highlight) { this.highlight = highlight; }
        public String getGeographicOrderNote() { return geographicOrderNote; }
        public void setGeographicOrderNote(String geographicOrderNote) { this.geographicOrderNote = geographicOrderNote; }
        public String getDirectionsUrl() { return directionsUrl; }
        public void setDirectionsUrl(String directionsUrl) { this.directionsUrl = directionsUrl; }
    }

    public static class TransitAdviceDto {
        private String recommendedMode;
        private String routeDirections;
        private String estimatedTransitCost;
        private String proTip;

        public TransitAdviceDto() {}

        public TransitAdviceDto(String recommendedMode, String routeDirections, String estimatedTransitCost, String proTip) {
            this.recommendedMode = recommendedMode;
            this.routeDirections = routeDirections;
            this.estimatedTransitCost = estimatedTransitCost;
            this.proTip = proTip;
        }

        public String getRecommendedMode() { return recommendedMode; }
        public void setRecommendedMode(String recommendedMode) { this.recommendedMode = recommendedMode; }
        public String getRouteDirections() { return routeDirections; }
        public void setRouteDirections(String routeDirections) { this.routeDirections = routeDirections; }
        public String getEstimatedTransitCost() { return estimatedTransitCost; }
        public void setEstimatedTransitCost(String estimatedTransitCost) { this.estimatedTransitCost = estimatedTransitCost; }
        public String getProTip() { return proTip; }
        public void setProTip(String proTip) { this.proTip = proTip; }
    }

    public static class TransitSummaryDto {
        private String primaryTransportRecommendation;
        private List<String> travelTips;

        public TransitSummaryDto() {}

        public TransitSummaryDto(String primaryTransportRecommendation, List<String> travelTips) {
            this.primaryTransportRecommendation = primaryTransportRecommendation;
            this.travelTips = travelTips;
        }

        public String getPrimaryTransportRecommendation() { return primaryTransportRecommendation; }
        public void setPrimaryTransportRecommendation(String primaryTransportRecommendation) { this.primaryTransportRecommendation = primaryTransportRecommendation; }
        public List<String> getTravelTips() { return travelTips; }
        public void setTravelTips(List<String> travelTips) { this.travelTips = travelTips; }
    }
}
