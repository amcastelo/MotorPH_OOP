package service;

/**
 * Represents the pagibig component used in the service layer.
 */
public class Pagibig implements DeductionCalculation {
    private static double pagibigDeduction;

    
    /**
     * Calculates the required data.
     * @param gross input value needed by this method.
     * @return resulting value produced by this method.
     */
    @Override
    public double calculate(double gross){
        double pagibig;

        if (gross > 1000.00 && gross <= 1500.00) {
            pagibig = gross * 0.03;
        } else {
            pagibig = gross * 0.04;
        }

        if (pagibig > 100) {
            pagibig = 100;
        }

        return pagibigDeduction = pagibig;
    }

}
