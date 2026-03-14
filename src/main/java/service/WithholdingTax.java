package service;

import service.LatePenalty;

/**
 * Represents the withholding tax component used in the service layer.
 */
    public class WithholdingTax {
        public static double tax, taxableIncome, afterTax;

        private final DeductionCalculation sss;
        private final DeductionCalculation philhealth;
        private final DeductionCalculation pagibig;
        private final LatePenalty latePenalty;

/**
 * Creates a new WithholdingTax instance.
 * @param sss input value needed by this method.
 * @param philhealth input value needed by this method.
 * @param pagibig input value needed by this method.
 * @param latePenalty input value needed by this method.
 */
        public WithholdingTax(
                DeductionCalculation sss,
                DeductionCalculation philhealth,
                DeductionCalculation pagibig,
                LatePenalty latePenalty
        ) {
            this.sss = sss;
            this.philhealth = philhealth;
            this.pagibig = pagibig;
            this.latePenalty = latePenalty;
        }

/**
 * Calculates the required data.
 * @param gross input value needed by this method.
 * @param empID input value needed by this method.
 * @param month input value needed by this method.
 * @param hourly input value needed by this method.
 * @return resulting value produced by this method.
 */
        public double calculate(double gross, String empID, int month, double hourly) {
            double totalDeduction =
                    sss.calculate(gross)
                    + philhealth.calculate(gross)
                    + pagibig.calculate(gross)
                    + latePenalty.calculate(empID, month);

            taxableIncome = gross - totalDeduction;

            if (taxableIncome <= 20832) {
                    tax = 0;

                } else if (taxableIncome > 20832 && taxableIncome <= 33333) {
                    tax = (taxableIncome - 20832) * 0.20;

                } else if (taxableIncome > 33333 && taxableIncome <= 66667) {
                    tax = 2500 + (taxableIncome - 33333) * 0.25;

                } else if (taxableIncome > 66667 && taxableIncome <= 166667) {
                    tax = 10833 + (taxableIncome - 66667) * 0.30;

                } else if (taxableIncome > 166667 && taxableIncome <= 666667) {
                    tax = 40833.33 + (taxableIncome - 166667) * 0.32;

                } else {
                    tax = 200833.33 + (taxableIncome - 666667) * 0.35;
                }

            afterTax = taxableIncome - tax;
            return afterTax;
        }


/**
 * Calculates net pay (after tax) using already-computed deduction amounts.
 *
 * <p>This overload is intended for date-range payroll computation where
 * SSS/PhilHealth/Pag-IBIG and late penalty amounts are computed elsewhere.</p>
 *
 * @param gross gross pay
 * @param sssAmount SSS contribution
 * @param philhealthAmount PhilHealth contribution
 * @param pagibigAmount Pag-IBIG contribution
 * @param latePenaltyAmount late penalty
 * @return net pay after withholding tax
 */
public double calculateFromAmounts(double gross,
                                   double sssAmount,
                                   double philhealthAmount,
                                   double pagibigAmount,
                                   double latePenaltyAmount) {

    double totalDeduction = sssAmount + philhealthAmount + pagibigAmount + latePenaltyAmount;
    taxableIncome = gross - totalDeduction;

    tax = computeTax(taxableIncome);
    afterTax = taxableIncome - tax;

    return afterTax;
}

/**
 * Computes withholding tax from taxable income using the same bracket rules
 * as {@link #calculate(double, String, int, double)}.
 *
 * @param taxableIncome taxable income
 * @return withholding tax amount
 */
public static double computeTax(double taxableIncome) {
    if (taxableIncome <= 20832) {
        return 0;
    } else if (taxableIncome <= 33333) {
        return (taxableIncome - 20832) * 0.20;
    } else if (taxableIncome <= 66667) {
        return 2500 + (taxableIncome - 33333) * 0.25;
    } else if (taxableIncome <= 166667) {
        return 10833 + (taxableIncome - 66667) * 0.30;
    } else if (taxableIncome <= 666667) {
        return 40833.33 + (taxableIncome - 166667) * 0.32;
    } else {
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }
}
    }
