package model;

/**
 * Represents the available leave status values used in the model layer.
 */
public enum LeaveStatus {
    PENDING,
    APPROVED,
    DENIED;

    /**
     * Handles from string.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static LeaveStatus fromString(String s) {
        if (s == null) return PENDING;
        String v = s.trim().toUpperCase();
        try {
            return LeaveStatus.valueOf(v);
        } catch (Exception ignored) {
            return PENDING;
        }
    }

    /**
     * Handles display.
     * @return resulting value produced by this method.
     */
    public String display() {
        return switch (this) {
            case PENDING -> "Pending";
            case APPROVED -> "Approved";
            case DENIED -> "Denied";
        };
    }
}
