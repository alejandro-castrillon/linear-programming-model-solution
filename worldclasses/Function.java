package worldclasses;

import interfaces.Utilities;
import java.util.ArrayList;

public final class Function {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    public static final String EQUAL = "=";
    public static final String LESS = "<=";
    public static final String GREATER = ">=";

    private ArrayList<Term> terms;
    private String symbol;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public Function() {
        terms = new ArrayList<>();
        //symbol = EQUAL;
    }

    /* ______________________________________________________________________ */
    public Function(Function function) {
        terms = new ArrayList<>();
        function.getLeftTerms().forEach((leftTerm) -> {
            addLeftTerm(leftTerm);
        });
        function.getRightTerms().forEach((rightTerm) -> {
            addRightTerm(rightTerm);
        });
        setSymbol(function.getSymbol());
    }

    /* ______________________________________________________________________ */
    public Function(String functionString) {
        this(parseFunction(functionString));
    }

    /* METHODS ______________________________________________________________ */
    public void addLeftTerm(Term term) {
        term.setSide(Term.LEFT);
        terms.add(term);
    }

    /* ______________________________________________________________________ */
    public void addLeftTerm(Double coefficient, String literal) {
        addLeftTerm(new Term(coefficient, literal, Term.LEFT));
    }

    /* ______________________________________________________________________ */
    public void addLeftTerm(Rational coefficient, String literal) {
        addLeftTerm(new Term(coefficient, literal, Term.LEFT));
    }

    /* ______________________________________________________________________ */
    public void addLeftTerm(Double coefficient) {
        addLeftTerm(coefficient, "");
    }

    /* ______________________________________________________________________ */
    public void addLeftTerm(Rational coefficient) {
        addLeftTerm(coefficient, "");
    }

    /* ______________________________________________________________________ */
    public void addLeftTerm(String literal) {
        addLeftTerm(1.0, literal);
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(Term term) {
        term.setSide(Term.RIGHT);
        terms.add(term);
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(Double coefficient, String literal) {
        addRightTerm(new Term(coefficient, literal, Term.RIGHT));
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(Rational coefficient, String literal) {
        addRightTerm(new Term(coefficient, literal, Term.RIGHT));
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(Double coefficient) {
        addRightTerm(coefficient, "");
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(Rational coefficient) {
        addRightTerm(coefficient, "");
    }

    /* ______________________________________________________________________ */
    public void addRightTerm(String literal) {
        addRightTerm(1.0, literal);
    }

    /* ______________________________________________________________________ */
    public void simplify() {
        Term it, jt;

        //moveRightToLeft();
        moveLeftToRight();
        moveRightToLeft();
        removeZeros();
        for (int i = 0; i < terms.size(); i++) {
            it = terms.get(i);
            for (int j = i + 1; j < terms.size(); j++) {
                jt = terms.get(j);
                if (it.getVariable().equals(jt.getVariable())) {
                    it.setCoefficient(it.getCoefficient().value() + jt.getCoefficient().value());
                    terms.remove(jt);
                    j--;
                }
            }
        }
        sortTerms();
    }

    /* ______________________________________________________________________ */
    public void removeZeros() {
        Term term;
//        for (int i = 0; i < terms.size(); i++) {
//            term = terms.get(i);
//            if (term.getCoefficient().value() == 0) {
//                terms.remove(term);
//            }
//        }
        if (getLeftTerms().size() < 1) {
            addLeftTerm(0.0);
        }
        if (getRightTerms().size() < 1) {
            addRightTerm(0.0);
        }
    }

    /* ______________________________________________________________________ */
    public void moveRightToLeft() {
        for (Term rightTerm : getRightTerms()) {
            if (!rightTerm.getVariable().isEmpty()) {
                addLeftTerm(rightTerm.getCoefficient().negate(), rightTerm.getVariable());
                terms.remove(rightTerm);
            }
        }
//        System.out.println("Right: " + getRightTerms());
//        System.out.println("Left: " + getLeftTerms());
    }

    /* ______________________________________________________________________ */
    public void moveLeftToRight() {
        for (Term leftTerm : getLeftTerms()) {
            addRightTerm(leftTerm.getCoefficient().negate(), leftTerm.getVariable());
            terms.remove(leftTerm);
        }
//        System.out.println("Left: " + getLeftTerms());
//        System.out.println("Right: " + getRightTerms());
    }

    /* ______________________________________________________________________ */
    public void sortTerms() {
        terms.sort((Term t1, Term t2) -> {
            Character c1 = 0, c2 = 0;
            if (!t1.getVariable().isEmpty() && !t2.getVariable().isEmpty()) {
                c1 = t1.getVariable().charAt(0);
                c2 = t2.getVariable().charAt(0);
            }
            return c2.compareTo(c1);
        });
    }

    /* ______________________________________________________________________ */
    public static Function parseFunction(String functionString) {
        Function function = new Function();

        functionString = functionString.replaceAll(" ", "");
        int symbolIndex = functionString.indexOf("<=");
        if (symbolIndex == -1) {
            symbolIndex = functionString.indexOf(">=");
            if (symbolIndex == -1) {
                symbolIndex = functionString.indexOf(EQUAL);
                function.setSymbol(EQUAL);
//                if (symbolIndex == -1) {
//                    DialogPane.show("Symbol don't found", "Function do not have symbol",DialogPane.E);
//                }
            } else {
                function.setSymbol(GREATER);
            }
        } else {
            function.setSymbol(LESS);
        }

        functionString = functionString.replaceAll("<", "");
        functionString = functionString.replaceAll(">", "");
        String[] string = functionString.split("=");
        String left = string[0], right = string[1];

        method(function, left, Term.LEFT);
        method(function, right, Term.RIGHT);

        //function.simplify();
        return function;
    }

    /* ______________________________________________________________________ */
    private static void method(Function function, String terms, boolean side) {
        String coefficient, variable;
        char kar;
        boolean sign;
        int i = 0;

        while (i < terms.length()) {
            sign = true;
            coefficient = "1";
            variable = "";

            kar = terms.charAt(i);
            if (kar == '+' || kar == '-') {
                sign = kar != '-';
                i++;
                kar = terms.charAt(i);
            }

            if (Utilities.isNumber(kar)) {
                coefficient = "";
                while (i < terms.length()) {
                    kar = terms.charAt(i);
                    if (Utilities.isNumber(kar)) {
                        coefficient += kar;
                        i++;
                    } else {
                        break;
                    }
                }
            }

            if (!Utilities.isNumber(kar)) {
                while (i < terms.length()) {
                    kar = terms.charAt(i);
                    if (kar != '+' && kar != '-') {
                        variable += kar;
                        i++;
                    } else {
                        break;
                    }
                }
            }

            if (side == Term.LEFT) {
                function.addLeftTerm(Rational.parseRational((sign ? "" : "-") + coefficient), variable);
            } else {
                function.addRightTerm(Rational.parseRational((sign ? "" : "-") + coefficient), variable);
            }
        }
    }

    /* ______________________________________________________________________ */
    public static String parseString(Function function) {
        String string = "";
        ArrayList<Term> leftTerms = function.getLeftTerms();
        ArrayList<Term> rightTerms = function.getRightTerms();
        Term term;

        for (int i = 0; i < leftTerms.size(); i++) {
            term = leftTerms.get(i);
            string += (term.getCoefficient().value() < 0 ? "-" : i == 0 ? "" : "+")
                    + (term.getCoefficient().absValue() == 1 ? "" : term.getCoefficient().abs() + "")
                    + term.getVariable();
        }
        string += function.getSymbol();
        for (int i = 0; i < rightTerms.size(); i++) {
            term = rightTerms.get(i);
            string += (term.getCoefficient().value() < 0 ? "-" : i == 0 ? "" : "+")
                    + (term.getCoefficient().abs())
                    //+ (term.getCoefficient().absValue() == 1 ? "" : term.getCoefficient().abs() + "")
                    + term.getVariable();
        }
        return string;
    }

    /* GETTERS ______________________________________________________________ */
    public String getSymbol() {
        return symbol;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Term> getLeftTerms() {
        ArrayList<Term> function = new ArrayList<>();
        for (Term term : terms) {
            if (term.getSide() == Term.LEFT) {
                function.add(term);
            }
        }
        return function;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Term> getRightTerms() {
        ArrayList<Term> function = new ArrayList<>();
        for (Term term : terms) {
            if (term.getSide() == Term.RIGHT) {
                function.add(term);
            }
        }
        return function;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Term> getTerms() {
        return terms;
    }

    /* ______________________________________________________________________ */
    @Override
    public String toString() {
        String function = "";
        ArrayList<Term> leftTerms;
        ArrayList<Term> rightTerms;
        Rational coefficient;
        String variable;

        removeZeros();
        leftTerms = getLeftTerms();
        rightTerms = getRightTerms();

        for (int i = 0; i < leftTerms.size(); i++) {
            coefficient = leftTerms.get(i).getCoefficient();
            variable = leftTerms.get(i).getVariable();

            if (coefficient.value() != 0) {
                if (i == 0) {
                    function += coefficient.value() < 0 ? "-" : "";
                } else {
                    function += coefficient.value() < 0 ? " - " : " + ";
                }
            }
            if (i == 0 && coefficient.value() == 0 && variable.isEmpty()) {
                function += "(" + coefficient.abs() + ")";
            } else if (coefficient.value() != 0) {
                if (coefficient.absValue() != 1) {
                    function += "(" + coefficient.abs() + ")";
                }
                function += variable;
            }
        }

        function += " " + symbol + " ";

        for (int i = 0; i < rightTerms.size(); i++) {
            coefficient = rightTerms.get(i).getCoefficient();
            variable = rightTerms.get(i).getVariable();

            if (coefficient.value() != 0) {
                if (i == 0) {
                    function += coefficient.value() < 0 ? "-" : "";
                } else {
                    function += coefficient.value() < 0 ? " - " : " + ";
                }
            }
            if (i == 0 && coefficient.value() == 0 && variable.isEmpty()) {
                function += "(" + coefficient.abs() + ")";
            } else if (coefficient.value() != 0) {
                if (variable.isEmpty()) {
                    function += "(" + coefficient.abs() + ")";
                } else if (coefficient.absValue() != 1) {
                    function += "(" + coefficient.abs() + ")";
                }
                function += variable;
            }
        }

        return function;
    }

    /* SETTERS ______________________________________________________________ */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /* ______________________________________________________________________ */
    public void set(Function function) {
        terms = new ArrayList<>();

        function.getLeftTerms().forEach((leftTerm) -> {
            addLeftTerm(leftTerm);
        });
        function.getRightTerms().forEach((rightTerm) -> {
            addRightTerm(rightTerm);
        });
        symbol = function.getSymbol();
    }

    /* MAIN _________________________________________________________________ */
    public static void main(String[] args) {

        long startTime = System.nanoTime();

        // ...
        Function f = new Function("-5X1 +6X2 <= 1");
        System.out.println(f);
        System.out.println(f.toString());
        for (Term term : f.getTerms()) {
            System.out.println("(" + term.getCoefficient() + ")(" + term.getVariable() + ")");
        }
        // El resto del código

        long endTime = System.nanoTime();

        System.out.println(endTime - startTime);

    }

}
