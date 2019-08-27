package worldclasses;

public class Term {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    public static final boolean LEFT = true;
    public static final boolean RIGHT = false;

    private Rational coefficient;
    private String variable;
    private boolean side;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public Term() {
    }

    /* ______________________________________________________________________ */
    public Term(Double coefficient, String literal) {
        this.coefficient = new Rational(coefficient);
        this.variable = literal;
        this.side = LEFT;
    }

    /* ______________________________________________________________________ */
    public Term(Double coefficient, String literal, boolean side) {
        this.coefficient = new Rational(coefficient);
        this.variable = literal;
        this.side = side;
    }

    /* ______________________________________________________________________ */
    public Term(Rational coefficient, String literal, boolean side) {
        this.coefficient = coefficient;
        this.variable = literal;
        this.side = side;
    }

    /* GETTERS ______________________________________________________________ */
    @Override
    public String toString() {
        return "(" + coefficient + ")" + variable;
    }

    /* ______________________________________________________________________ */
    public Rational getCoefficient() {
        return coefficient;
    }

    /* ______________________________________________________________________ */
    public double getValueCoefficient() {
        return coefficient.value();
    }

    /* ______________________________________________________________________ */
    public String getVariable() {
        return variable;
    }

    /* ______________________________________________________________________ */
    public boolean getSide() {
        return side;
    }

    /* SETTERS ______________________________________________________________ */
    public void setCoefficient(Double coefficient) {
        this.coefficient = new Rational(coefficient);
    }

    /* ______________________________________________________________________ */
    public void setCoefficient(Rational coefficient) {
        this.coefficient = coefficient;
    }

    /* ______________________________________________________________________ */
    public void setVariable(String variable) {
        this.variable = variable;
    }

    /* ______________________________________________________________________ */
    public void setSide(boolean side) {
        this.side = side;
    }
}
