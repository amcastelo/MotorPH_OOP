package service;

import model.AttendanceEntry;
import model.Employee;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents the attendance service component used in the service layer.
 */
public class AttendanceService {

    private final AttendanceWriter writer;
    private final Path filePath;

    /**
     * Creates a new AttendanceService instance.
     * @param filename input value needed by this method.
     */
    public AttendanceService(String filename) {
        this.writer = new AttendanceWriter(filename);
        this.filePath = Paths.get(filename);
    }

    /**
     * Handles time in.
     * @param e input value needed by this method.
     */
    public void timeIn(Employee e) {
        writer.timeIn(e);
    }

    /**
     * Handles time out.
     * @param e input value needed by this method.
     * @return resulting value produced by this method.
     */
    public boolean timeOut(Employee e) {
        return writer.timeOut(e);
    }

    /**
     * Returns recent for.
     * @param emp input value needed by this method.
     * @param limit input value needed by this method.
     * @return resulting value produced by this method.
     */
    public List<AttendanceEntry> getRecentFor(Employee emp, int limit) {
        if (!Files.exists(filePath)) return List.of();

        String empNo = safe(emp.getEmployeeNumber());
        String last = safe(emp.getLastName());
        String first = safe(emp.getFirstName());

        List<AttendanceEntry> out = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;


                if (i == 0 && line.toLowerCase().contains("employee")) continue;


                String[] parts = line.split("\\s*,\\s*", -1);
                if (parts.length < 6) continue;

                if (!safe(parts[0]).equals(empNo)) continue;

                String date = safe(parts[3]);
                String tin = safe(parts[4]);
                String tout = safe(parts[5]);

                String duration = computeDuration(tin, tout);
                out.add(new AttendanceEntry(date, tin, tout, duration));
            }
        } catch (Exception ignored) {
            return List.of();
        }

        Collections.reverse(out);
        if (limit > 0 && out.size() > limit) {
            return out.subList(0, limit);
        }
        return out;
    }

    /**
     * Returns all for.
     * @param emp input value needed by this method.
     * @return resulting value produced by this method.
     */
    public List<AttendanceEntry> getAllFor(Employee emp) {
        if (!Files.exists(filePath)) return List.of();

        String empNo = safe(emp.getEmployeeNumber());
        List<AttendanceEntry> out = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String raw : lines) {
                if (raw == null) continue;
                String line = raw.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s*,\\s*", -1);
                if (parts.length < 4) continue;

                if (!safe(parts[0]).equals(empNo)) continue;

                String date = parts.length > 3 ? safe(parts[3]) : "";
                String tin  = parts.length > 4 ? safe(parts[4]) : "";
                String tout = parts.length > 5 ? safe(parts[5]) : "";

                String duration = computeDuration(tin, tout);
                out.add(new AttendanceEntry(date, tin, tout, duration));
            }

        } catch (Exception ex) {
            return List.of();
        }


        Collections.reverse(out);
        return out;
    }

    /**
     * Computes duration.
     * @param timeIn input value needed by this method.
     * @param timeOut input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String computeDuration(String timeIn, String timeOut) {
        LocalTime t1 = parseTime(timeIn);
        LocalTime t2 = parseTime(timeOut);
        if (t1 == null || t2 == null) return "";

        Duration d = Duration.between(t1, t2);
        long minutes = d.toMinutes();
        if (minutes < 0) minutes = 0;

        long h = minutes / 60;
        long m = minutes % 60;
        return String.format("%dh %02dm", h, m);
    }

    /**
     * Parses time.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static LocalTime parseTime(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;


        try { return LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm:ss")); } catch (Exception ignored) {}
        try { return LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm")); } catch (Exception ignored) {}
        return null;
    }

    /**
     * Handles safe.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

}
