package service;

import java.text.DecimalFormat;

/**
 * Defines the deduction calculation contract used in the service layer.
 */
public interface DeductionCalculation {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    double calculate(double gross);

}
