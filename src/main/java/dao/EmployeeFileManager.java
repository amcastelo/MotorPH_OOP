package dao;

import model.Employee;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Represents the employee file manager component used in the dao layer.
 */
public class EmployeeFileManager implements FileLoader<Employee> {

    private static String employeeFilePath = "src/main/resources/Data.csv";
    private static final String removedFilePath = "src/main/resources/removedEmployees.csv";
    private static final String credentialsFilePath = "src/main/resources/credentials.csv";

    private static final List<Employee> employees = new ArrayList<>();


    private static final List<List<String>> cachedCSVData = new ArrayList<>();
    private static String[] cachedHeaders;

    private final EmployeeFactory employeeFactory = new EmployeeFactory();

    /**
     * Loads file.
     * @return resulting value produced by this method.
     */
    @Override
    public List<Employee> loadFile() {
        employees.clear();
        cachedCSVData.clear();
        cachedHeaders = null;

        Map<String, String[]> credentialsMap = loadCredentials();

        try (BufferedReader reader = new BufferedReader(new FileReader(employeeFilePath))) {

            String headerLine = reader.readLine();
            if (headerLine != null) {
                cachedHeaders = headerLine.split(",");
            }

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {

                List<String> employeeData = parseTxtLine(currentLine);

                if (employeeData.size() >= 19) {

                    String employeeNumber = employeeData.get(0);
                    String[] credentialData = credentialsMap.get(employeeNumber);

                    Employee employee = employeeFactory.create(
                            employeeData.toArray(new String[0]),
                            credentialData
                    );

                    employees.add(employee);
                    cachedCSVData.add(new ArrayList<>(employeeData));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }
    
    private Map<String, String[]> loadCredentials() {
        Map<String, String[]> credentialsMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {

            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {

                List<String> credentialData = parseTxtLine(line);
                String employeeNumber = credentialData.get(0);

                credentialsMap.put(employeeNumber, credentialData.toArray(new String[0]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return credentialsMap;
    }
    
    /**
     * Returns cached table data.
     * @return resulting value produced by this method.
     */
    public TableData getCachedTableData() {
        if (cachedHeaders == null || cachedCSVData.isEmpty()) {
            return new TableData(new String[0], List.of());
        }
        return new TableData(
            Arrays.copyOf(cachedHeaders, cachedHeaders.length),
            deepCopyRows(cachedCSVData)
        );
    }


    /**
     * Loads removed table data.
     * @return resulting value produced by this method.
     */
    public TableData loadRemovedTableData() {
        try {
            if (cachedHeaders == null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(employeeFilePath))) {
                    String headerLine = reader.readLine();
                    if (headerLine != null) {
                        cachedHeaders = headerLine.split(",");
                    }
                }
            }

            List<List<String>> rows = new ArrayList<>();
            File f = new File(removedFilePath);
            if (!f.exists()) {
                return new TableData(cachedHeaders == null ? new String[0] : Arrays.copyOf(cachedHeaders, cachedHeaders.length), rows);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(removedFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    List<String> tokens = parseTxtLine(line);
                    if (tokens.isEmpty()) continue;

                    if ("Employee #".equalsIgnoreCase(tokens.get(0).trim())) continue;
                    rows.add(tokens);
                }
            }

            return new TableData(
                    cachedHeaders == null ? new String[0] : Arrays.copyOf(cachedHeaders, cachedHeaders.length),
                    rows
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new TableData(new String[0], List.of());
        }
    }

    /**
     * Returns selected identifiers table data.
     * @return resulting value produced by this method.
     */
    public TableData getSelectedIdentifiersTableData() {

        List<Integer> selectedIndexes = Arrays.asList(0, 1, 2, 6, 7, 8, 9);

        if (cachedHeaders == null || cachedCSVData.isEmpty()) {
            return new TableData(new String[0], List.of());
        }

        String[] selectedHeaders = new String[selectedIndexes.size()];
        for (int i = 0; i < selectedIndexes.size(); i++) {
            selectedHeaders[i] = cachedHeaders[selectedIndexes.get(i)];
        }

        List<List<String>> selectedRows = new ArrayList<>();
        for (List<String> row : cachedCSVData) {
            List<String> filteredRow = new ArrayList<>();
            for (int index : selectedIndexes) {
                filteredRow.add(index < row.size() ? row.get(index) : "");
            }
            selectedRows.add(filteredRow);
        }

        return new TableData(selectedHeaders, selectedRows);
    }

    /**
     * Deletes record.
     * @param empID input value needed by this method.
     * @return resulting value produced by this method.
     */
    public Result deleteRecord(String empID) {
        String tempFilePath = "src/main/resources/temp.txt";
        String idToDelete = empID == null ? "" : empID.trim();

        if (idToDelete.isEmpty()) {
            return Result.fail("Please enter an Employee ID.");
        }

        boolean recordDeleted = false;

        try (
            BufferedReader reader = new BufferedReader(new FileReader(employeeFilePath));
            BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempFilePath));
            BufferedWriter removedWriter = new BufferedWriter(new FileWriter(removedFilePath, true))
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(idToDelete)) {
                    removedWriter.write(line);
                    removedWriter.newLine();
                    recordDeleted = true;
                    continue;
                }
                tempWriter.write(line);
                tempWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("Error processing file: " + e.getMessage());
        }

        if (!recordDeleted) {
            new File(tempFilePath).delete();
            return Result.fail("Record not found.");
        }

        File originalFile = new File(employeeFilePath);
        File tempFile = new File(tempFilePath);

        boolean deleted = originalFile.delete();
        boolean renamed = tempFile.renameTo(originalFile);

        if (!deleted || !renamed) {
            return Result.fail("Error updating file.");
        }

        return Result.ok("Record deleted and moved to removedEmployees.txt");
    }

    /**
     * Updates record.
     * @param empData input value needed by this method.
     * @return resulting value produced by this method.
     */
    public Result updateRecord(String[] empData) {
        if (empData == null || empData.length < 19) {
            return Result.fail("Invalid employee data.");
        }

        for (int i = 0; i < empData.length; i++) {
            empData[i] = safeTrim(empData[i]);
        }

        String employeeId = empData[0];
        String lastName = empData[1];
        String firstName = empData[2];
        String birthday = empData[3];
        String address = empData[4];
        String phoneNumber = empData[5];
        String sss = empData[6];
        String philHealth = empData[7];
        String tin = empData[8];
        String pagibig = empData[9];
        String status = empData[10];
        String position = empData[11];
        String immediateSupervisor = empData[12];
        String basicSalary = empData[13];
        String riceSubsidy = empData[14];
        String phoneAllowance = empData[15];
        String clothingAllowance = empData[16];
        String grossSemiMonthlyRate = empData[17];
        String hourlyRate = empData[18];

        if (employeeId.isEmpty() || lastName.isEmpty() || firstName.isEmpty() ||
            birthday.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() ||
            sss.isEmpty() || philHealth.isEmpty() || tin.isEmpty() || pagibig.isEmpty() ||
            status.isEmpty() || position.isEmpty() || immediateSupervisor.isEmpty() ||
            basicSalary.isEmpty() || riceSubsidy.isEmpty() || phoneAllowance.isEmpty() ||
            clothingAllowance.isEmpty() || grossSemiMonthlyRate.isEmpty() || hourlyRate.isEmpty()) {
            return Result.fail("All fields must be filled in.");
        }

        if (!isNumeric(basicSalary)) {
            return Result.fail("Basic Salary must be numeric.");
        }
        if (!isNumeric(riceSubsidy)) {
            return Result.fail("Rice Subsidy must be numeric.");
        }
        if (!isNumeric(phoneAllowance)) {
            return Result.fail("Phone Allowance must be numeric.");
        }
        if (!isNumeric(clothingAllowance)) {
            return Result.fail("Clothing Allowance must be numeric.");
        }
        if (!isNumeric(grossSemiMonthlyRate)) {
            return Result.fail("Gross Semi-monthly Rate must be numeric.");
        }
        if (!isNumeric(hourlyRate)) {
            return Result.fail("Hourly Rate must be numeric.");
        }
        
        if (!isNumeric(phoneNumber)) {
            return Result.fail("Hourly Rate must be numeric.");
        }

        if (!isValidSSS(sss)) {
            return Result.fail("Invalid SSS format. Format 12-3456789-0.");
        }

        if (!isValidPhilHealth(philHealth)) {
            return Result.fail("Invalid PhilHealth format. Format 012345678912");
        }

        if (!isValidTIN(tin)) {
            return Result.fail("Invalid TIN format. Format 123-456-789.");
        }

        if (!isValidPagibig(pagibig)) {
            return Result.fail("Invalid Pag-IBIG format. Format 012345678912");
        }

        String updatedLine = String.join(",",
            csvSafe(employeeId),
            csvSafe(lastName),
            csvSafe(firstName),
            csvSafe(birthday),
            csvSafe(address), 
            csvSafe(phoneNumber),
            csvSafe(sss),
            csvSafe(philHealth),
            csvSafe(tin),
            csvSafe(pagibig),
            csvSafe(status),
            csvSafe(position),
            csvSafe(immediateSupervisor),
            csvSafe(basicSalary),
            csvSafe(riceSubsidy),
            csvSafe(phoneAllowance),
            csvSafe(clothingAllowance),
            csvSafe(grossSemiMonthlyRate),
            csvSafe(hourlyRate)
        );

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(employeeFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length > 0 && safeTrim(parts[0]).equals(employeeId)) {
                    updatedLines.add(updatedLine);
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("Error reading file: " + e.getMessage());
        }

        if (!found) {
            return Result.fail("Employee ID not found.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(employeeFilePath))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("Error writing file: " + e.getMessage());
        }

        return Result.ok("Employee updated successfully.");
    }

    /**
     * Returns next employee id.
     * @return resulting value produced by this method.
     */
    public String getNextEmployeeId() {
        int max = 0;
        max = Math.max(max, maxIdFromFile(employeeFilePath, true));
        max = Math.max(max, maxIdFromFile(removedFilePath, false));
        return Integer.toString(max + 1);
    }

    /**
     * Handles add record.
     * @param empData input value needed by this method.
     * @return resulting value produced by this method.
     */
    public Result addRecord(String[] empData) {
        if (empData == null || empData.length < 19) {
            return Result.fail("Invalid employee data.");
        }

        for (int i = 0; i < empData.length; i++) {
            empData[i] = safeTrim(empData[i]);
        }

        String employeeId = empData[0];
        String lastName = empData[1];
        String firstName = empData[2];
        String birthday = empData[3];
        String address = empData[4];
        String phoneNumber = empData[5];
        String sss = empData[6];
        String philHealth = empData[7];
        String tin = empData[8];
        String pagibig = empData[9];
        String status = empData[10];
        String position = empData[11];
        String immediateSupervisor = empData[12];
        String basicSalary = empData[13];
        String riceSubsidy = empData[14];
        String phoneAllowance = empData[15];
        String clothingAllowance = empData[16];
        String grossSemiMonthlyRate = empData[17];
        String hourlyRate = empData[18];

        if (employeeId.isEmpty() || lastName.isEmpty() || firstName.isEmpty() ||
            birthday.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() ||
            sss.isEmpty() || philHealth.isEmpty() || tin.isEmpty() || pagibig.isEmpty() ||
            status.isEmpty() || position.isEmpty() || immediateSupervisor.isEmpty() ||
            basicSalary.isEmpty() || riceSubsidy.isEmpty() || phoneAllowance.isEmpty() ||
            clothingAllowance.isEmpty() || grossSemiMonthlyRate.isEmpty() || hourlyRate.isEmpty()) {
            return Result.fail("All fields must be filled in.");
        }

        if (employeeIdExists(employeeId, employeeFilePath) || employeeIdExists(employeeId, removedFilePath)) {
            return Result.fail("Employee ID already exists.");
        }

        if (!isNumeric(basicSalary)) {
            return Result.fail("Basic Salary must be numeric.");
        }
        if (!isNumeric(riceSubsidy)) {
            return Result.fail("Rice Subsidy must be numeric.");
        }
        if (!isNumeric(phoneAllowance)) {
            return Result.fail("Phone Allowance must be numeric.");
        }
        if (!isNumeric(clothingAllowance)) {
            return Result.fail("Clothing Allowance must be numeric.");
        }
        if (!isNumeric(grossSemiMonthlyRate)) {
            return Result.fail("Gross Semi-monthly Rate must be numeric.");
        }
        if (!isNumeric(hourlyRate)) {
            return Result.fail("Hourly Rate must be numeric.");
        }
        
        if (!isNumeric(phoneNumber)) {
            return Result.fail("Hourly Rate must be numeric.");
        }

        if (!isValidSSS(sss)) {
            return Result.fail("Invalid SSS format. Format 12-3456789-0.");
        }

        if (!isValidPhilHealth(philHealth)) {
            return Result.fail("Invalid PhilHealth format. Format 012345678912");
        }

        if (!isValidTIN(tin)) {
            return Result.fail("Invalid TIN format. Format 123-456-789.");
        }

        if (!isValidPagibig(pagibig)) {
            return Result.fail("Invalid Pag-IBIG format. Format 012345678912");
        }

        String line = String.join(",",
                employeeId,
                lastName,
                firstName,
                birthday,
                address,
                phoneNumber,
                sss,
                philHealth,
                tin,
                pagibig,
                status,
                position,
                immediateSupervisor,
                basicSalary,
                riceSubsidy,
                phoneAllowance,
                clothingAllowance,
                grossSemiMonthlyRate,
                hourlyRate
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(employeeFilePath, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("Error writing file: " + e.getMessage());
        }

        return Result.ok("Employee added successfully.");
    }
    
    /**
     * Validate numeric fields.
     * @param value
     * @return 
     */
    private boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            Double.parseDouble(value.replace(",", "").trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate SSS format.
     * @param sss
     * @return 
     */
    private boolean isValidSSS(String sss) {
        if (sss == null || sss.trim().isEmpty()) return false;
        return sss.matches("^\\d{2}-\\d{7}-\\d$");
    }

    /**
     * Validate TIN format.
     * @param tin
     * @return 
     */
    private boolean isValidTIN(String tin) {
        if (tin == null || tin.trim().isEmpty()) return false;
        return tin.matches("^\\d{3}-\\d{3}-\\d{3}$");
    }

    /**
     * Validate Pag-Ibig format.
     * @param pagibig
     * @return 
     */
    private boolean isValidPagibig(String pagibig) {
        if (pagibig == null || pagibig.trim().isEmpty()) return false;
        return pagibig.matches("^\\d{12}$|^\\d{4}-\\d{4}-\\d{4}$");
    }

    /**
     * Validate Philhealth format.
     * @param philHealth
     * @return 
     */
    private boolean isValidPhilHealth(String philHealth) {
        if (philHealth == null || philHealth.trim().isEmpty()) return false;
        return philHealth.matches("^\\d{12}$|^\\d{2}-\\d{9}-\\d$");
    }


    /**
     * Parses txt line.
     * @param line input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static List<String> parseTxtLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(buffer.toString());
                buffer = new StringBuilder();
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
        }
        return tokens;
    }
    
    private String csvSafe(String value) {
        if (value == null) return "";

        value = value.trim();

        // Escape quotes
        value = value.replace("\"", "\"\"");

        // Wrap in quotes if needed
        if (value.contains(",") || value.contains("\"")) {
            value = "\"" + value + "\"";
        }

        return value;
    }


    /**
     * Returns employee model list.
     * @return resulting value produced by this method.
     */
    public static List<Employee> getEmployeeModelList() {
        return List.copyOf(employees);
    }

    /**
     * Returns cached headers.
     * @return resulting value produced by this method.
     */
    public static String[] getCachedHeaders() {
        return cachedHeaders == null ? null : Arrays.copyOf(cachedHeaders, cachedHeaders.length);
    }

    /**
     * Returns cached csvdata.
     * @return resulting value produced by this method.
     */
    public static List<List<String>> getCachedCSVData() {
        return deepCopyRows(cachedCSVData);
    }

    /**
     * Returns cached headers snapshot.
     * @return resulting value produced by this method.
     */
    public String[] getCachedHeadersSnapshot() {
        return getCachedHeaders();
    }

    /**
     * Returns cached csvdata snapshot.
     * @return resulting value produced by this method.
     */
    public List<List<String>> getCachedCSVDataSnapshot() {
        return getCachedCSVData();
    }



    /**
     * Represents the table data component used in the dao layer.
     */
    public static final class TableData {
        private final String[] headers;
        private final List<List<String>> rows;

    /**
     * Creates a new TableData instance.
     * @param headers input value needed by this method.
     * @param rows input value needed by this method.
     */
        public TableData(String[] headers, List<List<String>> rows) {
            this.headers = headers;
            this.rows = rows;
        }

/**
 * Returns headers.
 * @return resulting value produced by this method.
 */
        public String[] getHeaders() {
            return headers == null ? null : Arrays.copyOf(headers, headers.length);
        }

/**
 * Returns rows.
 * @return resulting value produced by this method.
 */
        public List<List<String>> getRows() {
            return deepCopyRows(rows);
        }
    }

    /**
     * Represents the result component used in the dao layer.
     */
    public static final class Result {
        private final boolean ok;
        private final String message;

        /**
         * Creates a new Result instance.
         * @param ok input value needed by this method.
         * @param message input value needed by this method.
         */
        private Result(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        /**
         * Handles ok.
         * @param Result(true input value needed by this method.
         * @param message input value needed by this method.
         * @return resulting value produced by this method.
         */
        public static Result ok(String message) { return new Result(true, message); }
        /**
         * Handles fail.
         * @param Result(false input value needed by this method.
         * @param message input value needed by this method.
         * @return resulting value produced by this method.
         */
        public static Result fail(String message) { return new Result(false, message); }

        /**
         * Checks whether ok is true.
         * @return resulting value produced by this method.
         */
        public boolean isOk() { return ok; }
        /**
         * Returns message.
         * @return resulting value produced by this method.
         */
        public String getMessage() { return message; }
    }

    /**
     * Handles deep copy rows.
     * @param rows input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static List<List<String>> deepCopyRows(List<List<String>> rows) {
        List<List<String>> copy = new ArrayList<>();
        if (rows == null) return copy;

        for (List<String> row : rows) {
            copy.add(row == null ? new ArrayList<>() : new ArrayList<>(row));
        }
        return copy;
    }

    /**
     * Handles safe trim.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Handles max id from file.
     * @param path input value needed by this method.
     * @param hasHeader input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static int maxIdFromFile(String path, boolean hasHeader) {
        int max = 0;
        File f = new File(path);
        if (!f.exists()) return 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            if (hasHeader) reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length == 0) continue;
                try {
                    int v = Integer.parseInt(parts[0].trim());
                    if (v > max) max = v;
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
        return max;
    }

    /**
     * Handles employee id exists.
     * @param id input value needed by this method.
     * @param path input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static boolean employeeIdExists(String id, String path) {
        File f = new File(path);
        if (!f.exists()) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {

                    if (line.toLowerCase().startsWith("employee #")) {
                        first = false;
                        continue;
                    }
                    first = false;
                }
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(id)) return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }
}
