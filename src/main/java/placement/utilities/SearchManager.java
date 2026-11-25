package placement.utilities;

import java.util.*;

/**
 * Generic search utilities demonstrating linear and binary search.
 */
public class SearchManager {

    /** Linear search through list of maps */
    public static List<Map<String, String>> linearSearch(List<Map<String, String>> data, String key, String value) {
        List<Map<String, String>> results = new ArrayList<>();
        for (Map<String, String> item : data) {
            if (value.equalsIgnoreCase(item.getOrDefault(key, ""))) results.add(item);
        }
        return results;
    }

    /** Fuzzy search - finds items containing query as substring */
    public static List<String> fuzzySearch(List<String> items, String query) {
        List<String> results = new ArrayList<>();
        String q = query.toLowerCase();
        for (String item : items) {
            if (item.toLowerCase().contains(q)) results.add(item);
        }
        return results;
    }

    /** Binary search on sorted list (requires pre-sorted data) */
    public static int binarySearch(List<String> sortedItems, String target) {
        int low = 0, high = sortedItems.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = sortedItems.get(mid).compareToIgnoreCase(target);
            if (cmp == 0) return mid;
            else if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return -1; // Not found
    }

    /** Filter list of maps by field value */
    public static List<Map<String, String>> filterByField(List<Map<String, String>> data, String field, String value) {
        List<Map<String, String>> results = new ArrayList<>();
        for (Map<String, String> item : data) {
            if (value.equalsIgnoreCase(item.getOrDefault(field, ""))) results.add(item);
        }
        return results;
    }
}
