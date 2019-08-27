package worldclasses;

import java.math.BigDecimal;

public final class Rational {

    /* ATTRIBUTES ___________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private Long numerator;
    private Long denominator;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public Rational() {
        this(0L, 1L);
    }

    /* ______________________________________________________________________ */
    public Rational(Long numerator) {
        this(numerator, 1L);
    }

    /* ______________________________________________________________________ */
    public Rational(Long numerator, Long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /* ______________________________________________________________________ */
    public Rational(Double number) {
        if (number - (long) (double) number == 0) {
            numerator = (long) (double) number;
            denominator = 1L;
        } else {
            Rational rational = parseRational(number);
            numerator = rational.getNumerator();
            denominator = rational.getDenominator();
        }
    }

    /* ______________________________________________________________________ */
    public Rational(String number) {
        Rational rational = parseRational(number);
        numerator = rational.getNumerator();
        denominator = rational.getDenominator();
    }

    /* METHODS ______________________________________________________________ */
    public Rational simplify() {
        long mcm;
        long num = Math.abs(numerator);
        long den = Math.abs(denominator);
        if (den == 0) {
            mcm = num;
        } else {
            while (den != 0) {
                mcm = num % den;
                num = den;
                den = mcm;
            }
            mcm = num;
        }
        numerator /= mcm;
        denominator /= mcm;
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        if ((numerator + "").length() > 10) {
            numerator += 1;
        }
        if (numerator == 0) {
            denominator = 1L;
        }
        return this;
    }

    /* ______________________________________________________________________ */
    @Deprecated
    public Rational simplify2() {
        long absNumerator = Math.abs(numerator);
        long absDenominator = Math.abs(denominator);
        long less = absNumerator < absDenominator ? absNumerator : absDenominator;

        for (long i = less; i > 0; i--) {
            if (numerator % i == 0 && denominator % i == 0) {
                numerator /= i;
                denominator /= i;
                break;
            }
        }
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        if ((numerator + "").length() > 13) {
            numerator += 1;
        }
        if (numerator == 0) {
            denominator = 1L;
        }
        return this;
    }

    /* ______________________________________________________________________ */
    public static Rational parseRational(double number) {
        Long num = 1L;
        Long den = 1L;

        int largestRightOfDecimal = 10;
        long sign = 1;
        if (number < 0) {
            number = -number;
            sign = -1;
        }

        final long SECOND_MULTIPLIER_MAX = (long) Math.pow(10, largestRightOfDecimal - 1);
        final long FIRST_MULTIPLIER_MAX = SECOND_MULTIPLIER_MAX * 10L;
        final double ERROR = Math.pow(10, -largestRightOfDecimal - 1);
        long firstMultiplier = 1;
        long secondMultiplier = 1;
        boolean notIntOrIrrational = false;
        long truncatedNumber = (long) number;
        num = (long) (sign * number * FIRST_MULTIPLIER_MAX);
        den = FIRST_MULTIPLIER_MAX;

        double error = number - truncatedNumber;
        while ((error >= ERROR) && (firstMultiplier <= FIRST_MULTIPLIER_MAX)) {
            secondMultiplier = 1;
            firstMultiplier *= 10;
            while (secondMultiplier <= SECOND_MULTIPLIER_MAX && secondMultiplier < firstMultiplier) {
                double difference = (number * firstMultiplier) - (number * secondMultiplier);
                truncatedNumber = (long) difference;
                error = difference - truncatedNumber;
                if (error < ERROR) {
                    notIntOrIrrational = true;
                    break;
                }
                secondMultiplier *= 10;
            }
        }

        if (notIntOrIrrational) {
            num = sign * truncatedNumber;
            den = firstMultiplier - secondMultiplier;
        }

        return new Rational(num, den);
    }

    /* ______________________________________________________________________ */
    public static Rational parseRational(String number) {
        String[] rational;
        Long num, den;
        if (number.contains("/")) {
            rational = number.split("/");
            num = Long.parseLong(rational[0]);
            den = Long.parseLong(rational[1]);
            return new Rational(num, den);
        } else if (number.contains(".")) {
            return parseRational(Double.parseDouble(number));
        } else {
            num = Long.parseLong(number);
            return new Rational(num);
        }
    }

    /* GETTERS ______________________________________________________________ */
    @Override
    public String toString() {
        simplify();
        return denominator == 1 ? numerator + "" : numerator + "/" + denominator;
    }

    /* ______________________________________________________________________ */
    public double value() {
        //simplify();
        return (double) numerator / (double) denominator;
    }

    /* ______________________________________________________________________ */
    public BigDecimal bigValue() {
        return new BigDecimal(value());
    }

    /* ______________________________________________________________________ */
    public Rational negate() {
        return new Rational(-numerator, denominator);
    }

    /* ______________________________________________________________________ */
    public Rational abs() {
        return new Rational(Math.abs(value()));
    }

    /* ______________________________________________________________________ */
    public Double absValue() {
        return new Rational(Math.abs(value())).value();
    }

    /* ______________________________________________________________________ */
    public Long getNumerator() {
        return numerator;
    }

    /* ______________________________________________________________________ */
    public Long getDenominator() {
        return denominator;
    }

    /* SETTERS ______________________________________________________________ */
    public void setNumerator(Long numerator) {
        this.numerator = numerator;
    }

    /* ______________________________________________________________________ */
    public void setDenominator(Long denominator) {
        this.denominator = denominator;
    }

}
