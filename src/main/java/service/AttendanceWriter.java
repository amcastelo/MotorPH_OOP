



package service;

import model.Employee;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;






/**
 * Represents the attendance writer component used in the service layer.
 */
public class AttendanceWriter {

    private static final String HEADER = "Employee #,Last Name,First Name,Date,Time-in,Time-out";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final Path filePath;

    /**
     * Creates a new AttendanceWriter instance.
     * @param filename input value needed by this method.
     */
    public AttendanceWriter(String filename) {
        this.filePath = Paths.get(filename);
    }

    /**
     * Handles time in.
     * @param e input value needed by this method.
     */
    public synchronized void timeIn(Employee e) {
        ensureFileExists();

        String date = LocalDate.now().format(DATE_FMT);
        String now = LocalTime.now().format(TIME_FMT);

        if (hasOpenRecordForToday(e, date)) {
            return;
        }

        String line = csv(
                safe(getEmployeeNumber(e)),
                safe(getLastName(e)),
                safe(getFirstName(e)),
                date,
                now,
                ""
        );

        appendLine(line);
    }

    /**
     * Handles time out.
     * @param e input value needed by this method.
     * @return resulting value produced by this method.
     */
    public synchronized boolean timeOut(Employee e) {
        ensureFileExists();

        String date = LocalDate.now().format(DATE_FMT);
        String now = LocalTime.now().format(TIME_FMT);

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return false;

            List<String> updated = new ArrayList<>(lines.size());
            updated.add(lines.get(0));

            boolean updatedOne = false;

            int openIndex = -1;
            for (int i = lines.size() - 1; i >= 1; i--) {
                String line = lines.get(i);
                String[] parts = splitCsv(line);

                if (parts.length < 6) continue;

                String empNo = parts[0];
                String last = parts[1];
                String first = parts[2];
                String recDate = parts[3];
                String timeIn = parts[4];
                String timeOut = parts[5];

                boolean isMatch =
                        empNo.equals(safe(getEmployeeNumber(e))) &&
                        last.equals(safe(getLastName(e))) &&
                        first.equals(safe(getFirstName(e))) &&
                        recDate.equals(date);

                if (isMatch && (timeOut == null || timeOut.isBlank())) {
                    openIndex = i;
                    break;
                }
            }

            if (openIndex == -1) return false;

            for (int i = 1; i < lines.size(); i++) {
                if (i == openIndex && !updatedOne) {
                    String[] parts = splitCsv(lines.get(i));

                    parts[5] = now;
                    updated.add(csv(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                    updatedOne = true;
                } else {
                    updated.add(lines.get(i));
                }
            }

            Files.write(filePath, updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to write attendance file", ex);
        }
    }

    /**
     * Checks whether open record for today is available.
     * @param e input value needed by this method.
     * @param date input value needed by this method.
     * @return resulting value produced by this method.
     */
    private boolean hasOpenRecordForToday(Employee e, String date) {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = lines.size() - 1; i >= 1; i--) {
                String[] parts = splitCsv(lines.get(i));
                if (parts.length < 6) continue;

                boolean isMatch =
                        parts[0].equals(safe(getEmployeeNumber(e))) &&
                        parts[1].equals(safe(getLastName(e))) &&
                        parts[2].equals(safe(getFirstName(e))) &&
                        parts[3].equals(date);

                if (isMatch && (parts[5] == null || parts[5].isBlank())) {
                    return true;
                }
            }
            return false;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read attendance file", ex);
        }
    }

    /**
     * Handles ensure file exists.
     */
    private void ensureFileExists() {
        System.out.println("[AttendanceWriter] Using file: " + filePath.toAbsolutePath());

        try {
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            } else {

                List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                if (lines.isEmpty()) {
                    Files.writeString(filePath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create attendance file", ex);
        }
    }

/**
 * Handles append line.
 * @param line input value needed by this method.
 */
    private void appendLine(String line) {
        try {
            Files.writeString(filePath, line + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND, StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to append attendance record", ex);
        }
    }

    /**
     * Handles csv.
     * @param parts input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String csv(String... parts) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escapeCsv(parts[i]));
        }
        return sb.toString();
    }

    /**
     * Handles escape csv.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String escapeCsv(String s) {
        if (s == null) s = "";
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!needs) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    /**
     * Handles split csv.
     * @param line input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String[] splitCsv(String line) {

        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else {
                if (ch == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else if (ch == '"') {
                    inQuotes = true;
                } else {
                    cur.append(ch);
                }
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }

    /**
     * Handles safe.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }


    /**
     * Returns employee number.
     * @param e.getEmployeeNumber( input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String getEmployeeNumber(Employee e) { return e.getEmployeeNumber(); }
    
    /**
     * Returns last name.
     * @param e.getLastName( input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String getLastName(Employee e) { return e.getLastName(); }
    
    /**
     * Returns first name.
     * @param e.getFirstName( input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String getFirstName(Employee e) { return e.getFirstName(); }
}
