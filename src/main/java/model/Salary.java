package model;

/**
 * Represents the salary component used in the model layer.
 */
public class Salary {
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    /**
     * Creates a new Salary instance.
     * @param basicSalary input value needed by this method.
     * @param riceSubsidy input value needed by this method.
     * @param phoneAllowance input value needed by this method.
     * @param clothingAllowance input value needed by this method.
     * @param grossSemiMonthlyRate input value needed by this method.
     * @param hourlyRate input value needed by this method.
     */
    public Salary(double basicSalary,
                  double riceSubsidy,
                  double phoneAllowance,
                  double clothingAllowance,
                  double grossSemiMonthlyRate,
                  double hourlyRate) {

        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate = hourlyRate;
    }

    /**
     * Returns basic salary.
     * @return resulting value produced by this method.
     */
    public double getBasicSalary() {
        return basicSalary;
    }

    /**
     * Returns rice subsidy.
     * @return resulting value produced by this method.
     */
    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    /**
     * Returns phone allowance.
     * @return resulting value produced by this method.
     */
    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    /**
     * Returns clothing allowance.
     * @return resulting value produced by this method.
     */
    public double getClothingAllowance() {
        return clothingAllowance;
    }

    /**
     * Returns gross semi monthly rate.
     * @return resulting value produced by this method.
     */
    public double getGrossSemiMonthlyRate() {
        return grossSemiMonthlyRate;
    }

    /**
     * Returns hourly rate.
     * @return resulting value produced by this method.
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Updates hourly rate.
     * @param hourlyRate input value needed by this method.
     */
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }


    /**
     * Returns gross monthly compensation.
     * @return resulting value produced by this method.
     */
    public double getGrossMonthlyCompensation() {
        return basicSalary + riceSubsidy + phoneAllowance + clothingAllowance;
    }

}
