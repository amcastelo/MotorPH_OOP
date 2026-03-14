package service;

/**
 * Represents the philhealth component used in the service layer.
 */
public class Philhealth implements DeductionCalculation {

    private static double philhealthDeduction;

    /**
     * Calculates the required data.
     * @param gross input value needed by this method.
     * @return resulting value produced by this method.
     */
    @Override
    public double calculate(double gross) {
        double PhilDed;


        if (gross > 60000) {
            PhilDed = 1800;
        } else {
            PhilDed = (gross * 0.03) / 2;
        }

        philhealthDeduction = PhilDed;
        return philhealthDeduction;
    }
}
