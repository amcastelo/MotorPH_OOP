package model;

import java.util.EnumSet;

/**
 * Represents the employee component used in the model layer.
 */
public abstract class Employee {
        private String employeeNumber;
        private String lastName;
        private String firstName;
        private String birthday;
        private String address;
        private String phoneNumber;
        private String sssNumber;
        private String philhealthNumber;
        private String tinNumber;
        private String pagIbigNumber;
        private String status;
        private String position;
        private String immediateSupervisor;
        private Salary salary;
        
        protected abstract EnumSet<Permission> permissions();



        /**
         * Creates a new Employee instance.
         * @param data input value needed by this method.
         */
        public Employee(String[] data) {
            this.employeeNumber = getValue(data, 0);
            this.lastName = getValue(data, 1);
            this.firstName = getValue(data, 2);
            this.birthday = getValue(data, 3);
            this.address = getValue(data, 4);
            this.phoneNumber = getValue(data, 5);
            this.sssNumber = getValue(data, 6);
            this.philhealthNumber = getValue(data, 7);
            this.tinNumber = getValue(data, 8);
            this.pagIbigNumber = getValue(data, 9);
            this.status = getValue(data, 10);
            this.position = getValue(data, 11);
            this.immediateSupervisor = getValue(data, 12);
            double hourlyRate = getDoubleValue(data, 18);


            double basicSalaryAmount = getMoneyDoubleValue(data, 13, "Basic Salary");
            double riceSubsidyAmount = getMoneyDoubleValue(data, 14, "Rice Subsidy");
            double phoneAllowanceAmount = getMoneyDoubleValue(data, 15, "Phone Allowance");
            double clothingAllowanceAmount = getMoneyDoubleValue(data, 16, "Clothing Allowance");
            double grossSemiMonthlyAmount = getMoneyDoubleValue(data, 17, "Gross Semi-monthly Rate");

            this.salary = new Salary(
                    basicSalaryAmount,
                    riceSubsidyAmount,
                    phoneAllowanceAmount,
                    clothingAllowanceAmount,
                    grossSemiMonthlyAmount,
                    hourlyRate
            );
        }

    /**
     * Creates a new Employee instance.
     */
    protected Employee() {}    
        
    /**
     * Handles can.
     * @param permission input value needed by this method.
     * @return resulting value produced by this method.
     */
    public final boolean can(Permission permission) {
        return permission != null && permissions().contains(permission);
    }    
        
    /**
     * Handles to string.
     * @return resulting value produced by this method.
     */
    @Override
    public String toString() {
        String basicSalaryString = moneyToString(salary.getBasicSalary(), "Basic Salary");
        String riceSubsidyString = moneyToString(salary.getRiceSubsidy(), "Rice Subsidy");
        String phoneAllowanceString = moneyToString(salary.getPhoneAllowance(), "Phone Allowance");
        String clothingAllowanceString = moneyToString(salary.getClothingAllowance(), "Clothing Allowance");
        String grossSemiMonthlyString = moneyToString(salary.getGrossSemiMonthlyRate(), "Gross Semi-monthly Rate");
        String hourlyRateString = moneyToString(salary.getHourlyRate(), "Hourly Rate");
        return String.format("%-15s%-15s%-15s%-15s%-80s%-15s%-15s%-15s%-20s%-15s%-15s%-35s%-25s%-15s%-15s%-20s%-20s%-25s%-15s",
                employeeNumber, lastName, firstName, birthday, address, phoneNumber, sssNumber, philhealthNumber, tinNumber,
                pagIbigNumber, status, position, immediateSupervisor, basicSalaryString, riceSubsidyString, phoneAllowanceString, clothingAllowanceString,
                grossSemiMonthlyString, hourlyRateString);
    }    


    /**
     * Handles to string.
     * @param targetEmpTrue input value needed by this method.
     * @return resulting value produced by this method.
     */
    public String toString(boolean targetEmpTrue) {
        return """
                Employee ID: %s
                Name: %s %s
                Birthday: %s
                Address: %s
                PhoneNumber: %s
                SSSNumber: %s
                PhilHealth: %s
                TIN: %s
                PAG-IBIG: %s
                Status: %s
                Position: %s
                Supervisor: %s
                Basic Salary: %s
                Rice Subsidy: %s
                Phone allowance: %s
                Clothing Allowance: %s
                Gross Semi Monthly Rate: %s
                Hourly Rate: %s
                """.formatted(
                    employeeNumber,
                    lastName,
                    firstName,
                    birthday,
                    address,
                    phoneNumber,
                    sssNumber,
                    philhealthNumber,
                    tinNumber,
                    pagIbigNumber,
                    status,
                    position,
                    immediateSupervisor,
                    salary.getBasicSalary(),
                    salary.getRiceSubsidy(),
                    salary.getPhoneAllowance(),
                    salary.getClothingAllowance(),
                    salary.getGrossSemiMonthlyRate(),
                    salary.getHourlyRate()
                );
    }    

    /**
     * Returns double value.
     * @param data input value needed by this method.
     * @param index input value needed by this method.
     * @return resulting value produced by this method.
     */
    private double getDoubleValue(String[] data, int index) {
        String value = getValue(data, index);

        if (value.equalsIgnoreCase("Hourly Rate")) {
            return Double.NaN;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing double value at index " + index + " for Employee ID " + getEmployeeNumber() + ": " + value);
            return 0.0;
        }
    }

    /**
     * Returns value.
     * @param data input value needed by this method.
     * @param index input value needed by this method.
     * @return resulting value produced by this method.
     */
    private String getValue(String[] data, int index) {
        return (data.length > index) ? data[index] : "";
    }    

    /**
     * Returns employee number.
     * @return resulting value produced by this method.
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }    

    /**
     * Returns last name.
     *
     * @return resulting value produced by this method.
     */
    public String getLastName() {
        return lastName;
    }    

    /**
     * Returns first name.
     * @return resulting value produced by this method.
     */
    public String getFirstName() {
        return firstName;
    }    

    /**
     * Returns birthday.
     * @return resulting value produced by this method.
     */
    public String getBirthday() {
        return birthday;
    }    

    /**
     * Returns address.
     * @return resulting value produced by this method.
     */
    public String getAddress() {
        return address;
    }    

    /**
     * Returns phone number.
     * @return resulting value produced by this method.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }    

    /**
     * Returns sss number.
     * @return resulting value produced by this method.
     */
    public String getSssNumber() {
        return sssNumber;
    }    

    /**
     * Returns philhealth number.
     * @return resulting value produced by this method.
     */
    public String getPhilhealthNumber() {
        return philhealthNumber;
    }    

    /**
     * Returns tin number.
     * @return resulting value produced by this method.
     */
    public String getTinNumber() {
        return tinNumber;
    }    

    /**
     * Returns pag ibig number.
     * @return resulting value produced by this method.
     */
    public String getPagIbigNumber() {
        return pagIbigNumber;
    }
    
    /**
     * Returns status.
     * @return resulting value produced by this method.
     */
    public String getStatus() {
        return status;
    }    

    /**
     * Returns position.
     * @return resulting value produced by this method.
     */
    public String getPosition() {
        return position;
    }    

    /**
     * Returns immediate supervisor.
     * @return resulting value produced by this method.
     */
    public String getImmediateSupervisor() {
        return immediateSupervisor;
    }    

    /**
     * Updates last name.
     * @param lastName input value needed by this method.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    

    /**
     * Updates first name.
     * @param firstName input value needed by this method.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    

    /**
     * Returns salary.
     * @return resulting value produced by this method.
     */
    public Salary getSalary() {
        return salary;
    }    

    /**
     * Returns money double value.
     * @param data input value needed by this method.
     * @param index input value needed by this method.
     * @param headerLabel input value needed by this method.
     * @return resulting value produced by this method.
     */
    private double getMoneyDoubleValue(String[] data, int index, String headerLabel) {
        String value = getValue(data, index);

        if (value == null) {
            return 0.0;
        }

        if (value.equalsIgnoreCase(headerLabel)) {
            return Double.NaN;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing money value at index " + index + " for Employee ID " + getEmployeeNumber() + ": " + value);
            return 0.0;
        }
    }    

    /**
     * Handles money to string.
     * @param value input value needed by this method.
     * @param headerLabel input value needed by this method.
     * @return resulting value produced by this method.
     */
    private String moneyToString(double value, String headerLabel) {
        return (Double.isNaN(value)) ? headerLabel : Double.toString(value);
    }    
}
