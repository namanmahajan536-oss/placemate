package placement.eligibility;

/**
 * Checks backlog eligibility criteria.
 */
public class BacklogChecker {
    public boolean checkActive(int activeBacklogs, int maxActive) {
        return activeBacklogs <= maxActive;
    }

    public boolean checkHistorical(int historicalBacklogs, int maxHistorical) {
        return historicalBacklogs <= maxHistorical;
    }

    public String getActiveReason(int active, int max) {
        return String.format("Active backlogs (%d) exceed maximum allowed (%d)", active, max);
    }

    public String getHistoricalReason(int historical, int max) {
        return String.format("Historical backlogs (%d) exceed maximum allowed (%d)", historical, max);
    }
}
