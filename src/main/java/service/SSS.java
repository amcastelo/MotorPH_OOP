package service;

import dao.SSSFileManager;
import java.util.List;

/**
 * Represents the sss component used in the service layer.
 */
public class SSS implements DeductionCalculation {

    private String compensationRange;
    private double contribution;

    private static final List<SSS> sssDeductionRecords;

    private static double sssDeduction;


    /**
     * Creates a new SSS instance.
     * @param compensationRange input value needed by this method.
     * @param contribution input value needed by this method.
     */
    public SSS(String compensationRange, double contribution) {
        this.compensationRange = compensationRange;
        this.contribution = contribution;
    }

    /**
     * Creates a new SSS instance.
     */
    public SSS(){}


    static {
        SSSFileManager sssFile = new SSSFileManager();
        sssDeductionRecords = sssFile.loadFile();
    }

    
    /**
     * Calculates the required data.
     * @param gross input value needed by this method.
     * @return resulting value produced by this method.
     */
    @Override
    public double calculate(double gross){
        for (SSS record : sssDeductionRecords) {
            double[] range = parseSssCompensationRange(record.getCompensationRange());
                if (gross > range[0] && gross <= range[1]) {

                    sssDeduction = record.getContribution();
                    break;
                }
            }
        return sssDeduction;
    }

    /**
     * Parses sss compensation range.
     * @param compensationRange input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static double[] parseSssCompensationRange(String compensationRange) {

        compensationRange = compensationRange.trim();
        String[] rangeParts = compensationRange.split("-");

        if (rangeParts.length != 2) {
            throw new IllegalArgumentException("Invalid compensation range format: " + compensationRange);
        }

        try {
            double start = Double.parseDouble(rangeParts[0].trim());
            double end = Double.parseDouble(rangeParts[1].trim());

            return new double[]{start, end};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric format in compensation range: " + compensationRange, e);
        }
    }

    /**
     * Returns compensation range.
     * @return resulting value produced by this method.
     */
    public String getCompensationRange() {
        return compensationRange;
    }

    /**
     * Returns contribution.
     * @return resulting value produced by this method.
     */
    public double getContribution() {
        return contribution;
    }

    /**
     * Returns sss deduction.
     * @return resulting value produced by this method.
     */
    public static double getSssDeduction() {
        return sssDeduction;
    }

}
