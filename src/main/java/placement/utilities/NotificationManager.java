package placement.utilities;

import java.util.*;

/**
 * In-memory notification system using Queue (LinkedList).
 */
public class NotificationManager {
    public static class Notification {
        public enum NotificationType { INFO, SUCCESS, WARNING, ERROR }

        private String id, userId, message, timestamp;
        private NotificationType type;
        private boolean isRead;

        public Notification(String id, String userId, String message, NotificationType type) {
            this.id = id; this.userId = userId; this.message = message; this.type = type;
            this.timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.isRead = false;
        }

        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
        public NotificationType getType() { return type; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { this.isRead = read; }

        public Map<String, String> toMap() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("id", id); map.put("userId", userId); map.put("message", message);
            map.put("type", type.name()); map.put("timestamp", timestamp);
            map.put("isRead", String.valueOf(isRead));
            return map;
        }
    }

    private final Queue<Notification> notifications = new LinkedList<>();
    private int nextId = 1;

    public synchronized void addNotification(String userId, String message, Notification.NotificationType type) {
        notifications.offer(new Notification("NOTIF" + (nextId++), userId, message, type));
    }

    public void addNotification(String userId, String message) {
        addNotification(userId, message, Notification.NotificationType.INFO);
    }

    public List<Notification> getForUser(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications) if (userId.equals(n.getUserId()) || "ALL".equals(n.getUserId())) result.add(n);
        return result;
    }

    public List<Notification> getUnreadForUser(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : getForUser(userId)) if (!n.isRead()) result.add(n);
        return result;
    }

    public void markAsRead(String notifId) {
        for (Notification n : notifications) if (n.getId().equals(notifId)) { n.setRead(true); return; }
    }

    public int getUnreadCount(String userId) { return getUnreadForUser(userId).size(); }

    public List<Notification> getAllNotifications() { return new ArrayList<>(notifications); }
}
