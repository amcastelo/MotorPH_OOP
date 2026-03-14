package model;

/**
 * Represents the attendance entry component used in the model layer.
 */
public final class AttendanceEntry {
    private final String date;
    private final String timeIn;
    private final String timeOut;
    private final String duration;

    /**
     * Creates a new AttendanceEntry instance.
     * @param date input value needed by this method.
     * @param timeIn input value needed by this method.
     * @param timeOut input value needed by this method.
     * @param duration input value needed by this method.
     */
    public AttendanceEntry(String date, String timeIn, String timeOut, String duration) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.duration = duration;
    }

    /**
     * Returns date.
     * @return resulting value produced by this method.
     */
    public String getDate() { return date; }
    
    /**
     * Returns time in.
     * @return resulting value produced by this method.
     */
    public String getTimeIn() { return timeIn; }
    
    /**
     * Returns time out.
     * @return resulting value produced by this method.
     */
    public String getTimeOut() { return timeOut; }
    
    /**
     * Returns duration.
     * @return resulting value produced by this method.
     */
    public String getDuration() { return duration; }
}
