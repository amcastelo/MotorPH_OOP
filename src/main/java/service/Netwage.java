package service;

/**
 * Represents the netwage component used in the service layer.
 */
public class Netwage {

    private double sssData;
    private double philhealthData;
    private double pagibigData;
    private double lateData;
    private double totalDeduction;
    private double net;
    private double taxableIncome;
    private double tax;
    private String empName;

    private final Grosswage grossWageService;
    private final LatePenalty latePenaltyService;

    /**
     * Creates a new Netwage instance.
     * @param grossWageService input value needed by this method.
     * @param latePenaltyService input value needed by this method.
     */
    public Netwage(Grosswage grossWageService, LatePenalty latePenaltyService) {
        this.grossWageService = grossWageService;
        this.latePenaltyService = latePenaltyService;
    }

    /**
     * Calculates the required data.
     * @param empID input value needed by this method.
     * @param month input value needed by this method.
     * @return resulting value produced by this method.
     */
    public double calculate(String empID, int month) {
        AttendanceService attendanceService =
        new AttendanceService("data/AttendanceRecord5.csv");

        DeductionCalculation sss = new SSS();
        DeductionCalculation philhealth = new Philhealth();
        DeductionCalculation pagibig = new Pagibig();

        LatePenalty latePenalty = new LatePenalty(attendanceService);

        WithholdingTax withholdingTax = new WithholdingTax(
                new SSS(),
                new Philhealth(),
                new Pagibig(),
                latePenalty
        );


        double gross = grossWageService.calculate(empID, month);

        double hourly = grossWageService.getHourly();
        empName = grossWageService.getEmployeeName();

        sssData = sss.calculate(gross);
        philhealthData = philhealth.calculate(gross);
        pagibigData = pagibig.calculate(gross);

        lateData = latePenaltyService.calculate(empID, month);

        totalDeduction = sssData + philhealthData + pagibigData + lateData;

        net = withholdingTax.calculate(gross, empID, month, hourly);

        taxableIncome = WithholdingTax.taxableIncome;
        tax = WithholdingTax.tax;

        return net;
    }


/**
 * Calculates net pay using already-computed components for a given period.
 *
 * <p>This overload is intended for date-range payroll computation where attendance
 * filtering is handled elsewhere. It retains the same flow:</p>
 *
 * <ul>
 *   <li>Total deduction = SSS + PhilHealth + Pag-IBIG + Late Penalty</li>
 *   <li>Taxable income = Gross - Total deduction</li>
 *   <li>Tax computed via {@link WithholdingTax}</li>
 *   <li>Net = Taxable income - Tax</li>
 * </ul>
 *
 * @param employee employee object
 * @param gross gross pay
 * @param hourly hourly rate (for reference / UI)
 * @param sssAmount SSS contribution
 * @param philhealthAmount PhilHealth contribution
 * @param pagibigAmount Pag-IBIG contribution
 * @param latePenaltyAmount late penalty
 * @return net pay
 */
public double calculateFromAmounts(
        model.Employee employee,
        double gross,
        double hourly,
        double sssAmount,
        double philhealthAmount,
        double pagibigAmount,
        double latePenaltyAmount
) {
    // Reuse the same output fields used by the monthly method
    this.empName = (employee == null) ? null : (employee.getLastName() + ", " + employee.getFirstName());

    this.sssData = sssAmount;
    this.philhealthData = philhealthAmount;
    this.pagibigData = pagibigAmount;
    this.lateData = latePenaltyAmount;

    this.totalDeduction = sssAmount + philhealthAmount + pagibigAmount + latePenaltyAmount;

    this.taxableIncome = gross - this.totalDeduction;

    // Use the same tax rules as the existing WithholdingTax service
    this.tax = WithholdingTax.computeTax(this.taxableIncome);
    this.net = this.taxableIncome - this.tax;

    // Keep WithholdingTax static fields consistent for any legacy UI reads
    WithholdingTax.taxableIncome = this.taxableIncome;
    WithholdingTax.tax = this.tax;
    WithholdingTax.afterTax = this.net;

    return this.net;
}



    /**
     * Returns sss data.
     * @return resulting value produced by this method.
     */
    public double getSssData() { return sssData; }
    /**
     * Returns philhealth data.
     * @return resulting value produced by this method.
     */
    public double getPhilhealthData() { return philhealthData; }
    
    /**
     * Returns pagibig data.
     * @return resulting value produced by this method.
     */
    public double getPagibigData() { return pagibigData; }
    
    /**
     * Returns late data.
     * @return resulting value produced by this method.
     */
    public double getLateData() { return lateData; }
    
    /**
     * Returns total deduction.
     * @return resulting value produced by this method.
     */
    public double getTotalDeduction() { return totalDeduction; }
    
    /**
     * Returns net.
     * @return resulting value produced by this method.
     */
    public double getNet() { return net; }
    
    /**
     * Returns taxable income.
     * @return resulting value produced by this method.
     */
    public double getTaxableIncome() { return taxableIncome; }
    
    /**
     * Returns tax.
     * @return resulting value produced by this method.
     */
    public double getTax() { return tax; }
    
    /**
     * Returns emp name.
     * @return resulting value produced by this method.
     */
    public String getEmpName() { return empName; }
}
