package placement.eligibility;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates a student's active and historical backlogs against drive limits.
 * Demonstrates the Single Responsibility Principle (SRP).
 */
public class BacklogChecker {
    private String lastReason;

    /**
     * Checks both active and historical backlogs against allowed maxima.
     * Stores a clear explanation in lastReason if either limit is exceeded.
     *
     * @param activeBacklogs     student's current active backlogs
     * @param maxActive          maximum allowed active backlogs
     * @param historicalBacklogs student's total historical backlogs
     * @param maxHistorical      maximum allowed historical backlogs
     * @return true if both active and historical backlogs are within limits
     */
    public boolean check(int activeBacklogs, int maxActive, int historicalBacklogs, int maxHistorical) {
        List<String> failures = getReasons(activeBacklogs, maxActive, historicalBacklogs, maxHistorical);
        if (failures.isEmpty()) {
            this.lastReason = "";
            return true;
        }
        this.lastReason = String.join("; ", failures);
        return false;
    }

    /**
     * Returns a list of specific backlog violation messages (empty if all limits pass).
     */
    public List<String> getReasons(int activeBacklogs, int maxActive, int historicalBacklogs, int maxHistorical) {
        List<String> reasons = new ArrayList<>();
        if (!checkActive(activeBacklogs, maxActive)) {
            reasons.add(getActiveReason(activeBacklogs, maxActive));
        }
        if (!checkHistorical(historicalBacklogs, maxHistorical)) {
            reasons.add(getHistoricalReason(historicalBacklogs, maxHistorical));
        }
        return reasons;
    }

    /**
     * Checks only active backlogs.
     */
    public boolean checkActive(int activeBacklogs, int maxActive) {
        if (maxActive < 0) {
            return true;
        }
        return activeBacklogs <= maxActive;
    }

    /**
     * Checks only historical backlogs.
     */
    public boolean checkHistorical(int historicalBacklogs, int maxHistorical) {
        if (maxHistorical < 0) {
            return true;
        }
        return historicalBacklogs <= maxHistorical;
    }

    public String getActiveReason(int active, int max) {
        return String.format("Active backlogs (%d) exceed maximum allowed (%d)", active, max);
    }

    public String getHistoricalReason(int historical, int max) {
        return String.format("Historical backlogs (%d) exceed maximum allowed (%d)", historical, max);
    }

    /**
     * Returns the combined reason from the most recent check call.
     */
    public String getLastReason() {
        return lastReason != null ? lastReason : "";
    }
}
