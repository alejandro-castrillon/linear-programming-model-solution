package worldclasses;

import interfaces.Utilities;
import java.util.ArrayList;
import java.util.Arrays;

public class Model implements Cloneable {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    public static final boolean MAXIMIZE = true;
    public static final boolean MINIMIZE = false;

    private Function objetiveFunction;
    private Function newObjetiveFunction;
    private boolean type;
    private int MValue;
    
    private final ArrayList<Function> restrictions;
    private ArrayList<Function> ecuations;
    private int restrictionsCount;

    private ArrayList<String> variables;
    private ArrayList<String> basicVariables;
    private ArrayList<String> nonBasicVariables;
    private int variablesCount;

    private Rational[][] simplexTable;
    private String name;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public Model() {
        // Init variables
        restrictions = new ArrayList<>();
        ecuations = new ArrayList<>();
        MValue = 100;
        variablesCount = restrictionsCount = 0;
    }

    /* METHODS ______________________________________________________________ */
    public void addRestriction(String restrictionString) {
        System.out.println("\nADD RESTRICTION");

        Function restriction = new Function(restrictionString); // Convert string to function(Restriction)
        restrictions.add(restriction); // Add function(Restriction) to model(Restriction's arrayList)
        restrictionsCount++; // Increase restrictions count

        // Show function(Restriction) added and restrictions count
        System.out.println("AR: New Restriction: " + restriction);
        System.out.println("AR: Restriction Cnt: " + restrictionsCount);
    }

    /* ______________________________________________________________________ */
    public void removeRestrictionAt(int index) {
        System.out.println("\nREMOVE RESTRICTION");

        Function restriction = restrictions.get(index); // Find restriction index
        restrictions.remove(index); // Remove restriction from model(Restriction's arrayList)

        if (ecuations != null) { // If ecuation's arrayList is initializated
            if (!ecuations.isEmpty()) { // If ecuation's arraylist have almost one ecuation
                if (index < ecuations.size()) { // If index of restriction is lower than size of ecuations arraylist 
                    ecuations.remove(index); // Then remove ecuation to respective restriction
                }
            }
        }
        restrictionsCount--; // Decrease restrictions count

        // Show restriction removed and restrictions count
        System.out.println("RR: Old Restriction: " + restriction);
        //System.out.println("RR: Restriction Cnt: " + restrictionsCount);
        System.out.println("RR: Restriction Cnt: " + restrictions.size());
    }

    /* ______________________________________________________________________ */
    public void calculateInitialSolution() {
        System.out.println("\nCALCULATE INITIAL SOLUTION");

        // Copy old objetive function to new objetive function
        newObjetiveFunction = new Function(objetiveFunction); // newObjetiveFunction = Function.parseFunction(Function.parseString(objetiveFunction));

        convertToEcuations(); // Convert restrictions to ecuations
        getVariables(); // Search variables(Basics and Non-Basics)
        buildSimplexTable(); // Buitl initial simplex table

        findBasicVariables(); // Search basic variables
        findNonBasicVariables(); // Search non-basic variablers

        // Add shadow variables to variable's arrayList
        while (nonBasicVariables.size() < basicVariables.size()) {
            nonBasicVariables.add("");
        }
        while (basicVariables.size() < nonBasicVariables.size()) {
            basicVariables.add("");
        }
    }

    /* ______________________________________________________________________ */
    public void convertToEcuations() {
        System.out.println("\nCONVERT TO ECUATIONS");

        // Copy each restriction to ecuations arrayList
        ecuations = new ArrayList<>();
        restrictions.forEach(restriction -> {
            ecuations.add(new Function(restriction));
        });

        //  if is necesary generate slack, slurpus or artuficial variables
        generateSlackSlurpusVariable();
        generateArtificialVariables();

        // Set ecuations symbol "="
        ecuations.forEach(ecuation -> {
            ecuation.setSymbol(Function.EQUAL);
        });

        // Show restrictions and ecuations
        for (int i = 0; i < restrictionsCount; i++) {
            System.out.println("CTE: " + restrictions.get(i) + "\t->\t" + ecuations.get(i));
        }
    }

    /* ______________________________________________________________________ */
    /**
     * If restriction's symbol is not "=", then add a new slack or slurpus
     * variable in respective ecuations
     */
    public void generateSlackSlurpusVariable() {
        String symbol;
        int extraVariablesCount = 1;
        Term newTerm;

        for (Function ecuation : ecuations) {
            symbol = ecuation.getSymbol();

            if (!symbol.equals(Function.EQUAL)) {
                newTerm = new Term(symbol.equals(Function.LESS) ? 1.0 : -1.0, "S" + extraVariablesCount);
                ecuation.addLeftTerm(newTerm);
                extraVariablesCount++;
            }
        }
    }

    /* ______________________________________________________________________ */
    /**
     * If restriction's symbol is ">=" or "=" add a new artificial variable in
     * respective ecuation and the objetive function
     */
    public void generateArtificialVariables() {
        String symbol;
        int extraVariablesCount = 1;

        for (Function ecuation : ecuations) {
            symbol = ecuation.getSymbol();

            if (symbol.equals(Function.EQUAL) || symbol.equals(Function.GREATER)) {
                ecuation.addLeftTerm("R" + extraVariablesCount);
                newObjetiveFunction.addRightTerm((double) (type == MAXIMIZE ? -MValue : MValue), "R" + extraVariablesCount);
                extraVariablesCount++;
            }
        }
        newObjetiveFunction.simplify();

        System.out.println("CIS: New Objetive Function: " + newObjetiveFunction);
    }

    /* ______________________________________________________________________ */
    public void buildSimplexTable() {
        System.out.println("\nBUILD SIMPLEX TABLE");

        ArrayList<Function> functions = new ArrayList<>();
        ArrayList<Term> terms;
        double acm;

        simplexTable = new Rational[restrictionsCount + 1][variablesCount];

        // Add all functions to a new arrayList
        functions.add(newObjetiveFunction);
        ecuations.forEach(restriction -> {
            functions.add(restriction);
        });

        // for (int i = 0; i < restrictionsCount + 1; i++) {
        for (int i = 0; i < functions.size(); i++) {
            terms = functions.get(i).getTerms();

            for (int j = 0; j < variablesCount; j++) {
                acm = 0;
                for (Term term : terms) {
                    if (term.getVariable().equals(variables.get(j))) {
                        acm += term.getCoefficient().value();
                    }
                }
                simplexTable[i][j] = new Rational(acm);
            }
        }

        // Show simplex table builded
        for (int i = 0; i < functions.size(); i++) {
            for (int j = 0; j < variablesCount; j++) {
                System.out.print(simplexTable[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /* ______________________________________________________________________ */
    public void findBasicVariables() {
        System.out.println("\nFIND BASIC VARIABLES");

        basicVariables = new ArrayList<>();
        int basicVariableIndex;
        String basicVariable;

        // Add first variable from the new objetive function to basic variables
        basicVariables.add(newObjetiveFunction.getTerms().get(0).getVariable());

        // Get last variable in each ecuation
        for (Function ecuation : ecuations) {
            basicVariableIndex = ecuation.getLeftTerms().size() - 1;
            basicVariable = ecuation.getLeftTerms().get(basicVariableIndex).getVariable();

            if (!basicVariables.contains(basicVariable)) {
                basicVariables.add(basicVariable);
            }
        }
        System.out.println("FBV: " + basicVariables);
    }

    /* ______________________________________________________________________ */
    public void findNonBasicVariables() {
        System.out.println("\nFIND NON-BASIC VARIABLES");

        nonBasicVariables = new ArrayList<>();
        String nonBasicVariable;

        // Get each variable from ecuations that are not basics
        for (Function ecuation : ecuations) {
            for (Term leftTerm : ecuation.getLeftTerms()) {
                nonBasicVariable = leftTerm.getVariable();

                if (!basicVariables.contains(nonBasicVariable) && !nonBasicVariables.contains(nonBasicVariable)) {
                    nonBasicVariables.add(nonBasicVariable);
                }
            }
        }
        System.out.println("FNBV: " + nonBasicVariables);
    }

    /* ______________________________________________________________________ */
    /**
     * Correct Possible error*
     */
    public void penalizeArtificialVariables() {
        System.out.println("\nPENALIZE ARTIFICIAL VARIABLES");
        double sum;

        System.out.println("PAV: " + Arrays.toString(simplexTable[0]));

        ArrayList<Integer> positions = getArtificialVariablesFunctions();
        for (int i = 0; i < variablesCount; i++) {
            sum = 0;
            for (int j = 0; j < positions.size(); j++) {
                sum += simplexTable[positions.get(j) + 1][i].value();
            }
            simplexTable[0][i] = new Rational(simplexTable[0][i].value() + ((double) MValue * sum));
        }

        System.out.println("PAV: " + Arrays.toString(simplexTable[0]));

        System.out.println("PENALIZED SIMPLEX TABLE");
        for (int i = 0; i < restrictionsCount + 1; i++) {
            for (int j = 0; j < variablesCount; j++) {
                System.out.print(simplexTable[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /* ______________________________________________________________________ */
    public void calculateSolution() {
        System.out.println("\nCALCULATE SOLUTION");

        int inputVarPos = inputVariablePosition();
        int outputVarPos = outputVariablePosition(inputVarPos);
        double pibotElement = simplexTable[outputVarPos][inputVarPos].value();

        Rational[][] simplexTableCopy = new Rational[restrictionsCount + 1][variablesCount];

        String inputVar = variables.get(inputVarPos);
        String outputVar = basicVariables.get(outputVarPos);

        basicVariables.set(outputVarPos, inputVar);
        nonBasicVariables.set(inputVarPos - 1, outputVar);

        for (int i = 0; i < variablesCount; i++) {
            simplexTable[outputVarPos][i] = new Rational(simplexTable[outputVarPos][i].value() / (double) pibotElement);
        }

        for (int i = 0; i < restrictionsCount + 1; i++) {
            System.arraycopy(simplexTable[i], 0, simplexTableCopy[i], 0, variablesCount);
        }

        for (int i = 0; i < restrictionsCount + 1; i++) {
            for (int j = 0; j < variablesCount; j++) {
                if (i != outputVarPos) {
                    simplexTable[i][j] = new Rational(
                            simplexTableCopy[i][j].value() - simplexTableCopy[i][inputVarPos].value()
                            * simplexTableCopy[outputVarPos][j].value());
                }
            }
        }

        for (int i = 0; i < restrictionsCount + 1; i++) {
            for (int j = 0; j < variablesCount; j++) {
                System.out.print(simplexTable[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /* ______________________________________________________________________ */
    public int inputVariablePosition() {
        int posM = -1, posm = -1;
        for (int i = 0; i < variablesCount; i++) {
            for (int j = 0; j < restrictionsCount; j++) {
                if (!variables.get(i).isEmpty() && variables.contains(nonBasicVariables.get(j)) && i != 0) {
                    if (type == MAXIMIZE && simplexTable[0][i].value() < 0) {
                        if (posM != -1) {
                            if (simplexTable[0][i].value() < simplexTable[0][posM].value()) {
                                posM = i;
                            }
                        } else {
                            posM = i;
                        }
                    } else if (type == MINIMIZE && simplexTable[0][i].value() > 0) {
                        if (posm != -1) {
                            if (simplexTable[0][i].value() > simplexTable[0][posm].value()) {
                                posm = i;
                            }
                        } else {
                            posm = i;
                        }
                    }
                }
            }
        }
        return type == MAXIMIZE ? posM : posm;
    }

    /* ______________________________________________________________________ */
    public int outputVariablePosition(int inputVarPos) {
        int outputVarPos = 1;
        double less = simplexTable[1][variablesCount - 1].value() / (double) simplexTable[1][inputVarPos].value();
        double possible;

        for (int i = 2; i < restrictionsCount + 1; i++) {
            possible = simplexTable[i][variablesCount - 1].value() / simplexTable[i][inputVarPos].value();
            if (possible < less && possible > 0 && simplexTable[i][inputVarPos].value() > 0) {
                less = simplexTable[i][variablesCount - 1].value() / simplexTable[i][inputVarPos].value();
                outputVarPos = i;
            }
        }
        return outputVarPos;
    }

    /* GETTERS ______________________________________________________________ */
    public Function getObjetiveFunction() {
        return objetiveFunction;
    }

    /* ______________________________________________________________________ */
    public Function getNewObjetiveFunction() {
        return newObjetiveFunction;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Function> getRestrictions() {
        return restrictions;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Function> getEcuations() {
        return ecuations;
    }

    /* ______________________________________________________________________ */
    public Function getRestrictionAt(int index) {
        return restrictions.get(index);
    }

    /* ______________________________________________________________________ */
    public Function getEcuationAt(int index) {
        return ecuations.get(index);
    }

    /* ______________________________________________________________________ */
    public int getRestrictionsCount() {
        return restrictionsCount;
    }

    /* ______________________________________________________________________ */
    public int getEcuationsCount() {
        return ecuations.size();
    }

    /* ______________________________________________________________________ */
    public int getVariablesCount() {
        return variablesCount;
    }

    /* ______________________________________________________________________ */
    public ArrayList<String> getVariables() {
        variables = new ArrayList<>();

        // Extract variables from objetive function
        objetiveFunction.getTerms().forEach(term -> {
            //if (!term.getVariable().equals(newObjetiveFunction.getTerms().get(0).getVariable())) {
            if (!term.getVariable().isEmpty() && !variables.contains(term.getVariable())) {
                variables.add(term.getVariable());
            }
            //}
        });
        //variables.add(newObjetiveFunction.getTerms().get(0).getVariable());

        // Extract variables from ecuation
        ecuations.forEach(ecuation -> {
            for (Term term : ecuation.getTerms()) {
                if (!term.getVariable().isEmpty() && !variables.contains(term.getVariable())) {
                    variables.add(term.getVariable());
                }
            }
        });

        // Sort variables decreasingly and add a solution
        Utilities.sortStringListDecreasing(variables);
        variables.add("");

        variablesCount = variables.size();
        return variables;
    }

    /* ______________________________________________________________________ */
    public ArrayList<String> getBasicVariables() {
        return basicVariables;
    }

    /* ______________________________________________________________________ */
    public ArrayList<String> getNonBasicVariables() {
        return nonBasicVariables;
    }

    /* ______________________________________________________________________ */
    public Rational[][] getSimplexTable() {
        return simplexTable;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Integer> getArtificialVariablesFunctions() {
//        int count = 0;
//        for (String variable : variables) {
//            if (variable.contains("R")) {
//                count++;
//            }
//        }
//        return count;
        ArrayList<Integer> restrictionsWithArtificialVariables = new ArrayList<>();
        for (int i = 0; i < ecuations.size(); i++) {
            ArrayList<Term> terms = ecuations.get(i).getTerms();
            for (int j = 0; j < terms.size(); j++) {
                Term term = terms.get(j);
                if (term.getVariable().contains("R")) {
                    restrictionsWithArtificialVariables.add(i);
                }
            }
        }

        return restrictionsWithArtificialVariables;
    }

    /* ______________________________________________________________________ */
    public boolean isOptimal() {
        boolean optimal = true;
        Rational[] objetiveFunctionCoefficients = simplexTable[0];

        for (int i = 1; i < variablesCount - 1; i++) {
            if (type == MAXIMIZE) {
                if (objetiveFunctionCoefficients[i].value() < 0) {
                    optimal = false;
                    break;
                }
            } else if (objetiveFunctionCoefficients[i].value() > 0) {
                optimal = false;
                break;
            }
        }

        System.out.println(optimal ? "Is Optimal" : "Isn't Optimal");
        return optimal;
    }

    /* ______________________________________________________________________ */
    public boolean getTypeModel() {
        return type;
    }

    /* ______________________________________________________________________ */
    public String getName() {
        return name;
    }

    /* ______________________________________________________________________ */
    public int getMValue() {
        return MValue;
    }

    /* SETTERS ______________________________________________________________ */
    public void setObjetiveFunction(String functionString) {
        System.out.println("\nSET OBJETIVE FUNCTION");

        objetiveFunction = Function.parseFunction(functionString);
        newObjetiveFunction = Function.parseFunction(functionString);
        System.out.println("SOF: Objetive Function: " + objetiveFunction);
    }

    /* ______________________________________________________________________ */
    public void setTypeModel(boolean type) {
        this.type = type;
        System.out.println(type == MAXIMIZE ? "MAXIMIZE" : "MINIMIZE");
    }

    /* ______________________________________________________________________ */
    public void setRestrictionAt(String functionString, int index) {
        restrictions.get(index).set(Function.parseFunction(functionString));
    }

    /* ______________________________________________________________________ */
    public void setEcuationAt(String functionString, int index) {
        ecuations.get(index).set(Function.parseFunction(functionString));
    }

    /* ______________________________________________________________________ */
    public void setMValue(int MValue) {
        this.MValue = MValue;
    }

    /* ______________________________________________________________________ */
    public void setVariablesCount(int variablesCount) {
        this.variablesCount = variablesCount;
    }

    /* ______________________________________________________________________ */
    public void setRestrictionsCount(int restrictionsCount) {
        this.restrictionsCount = restrictionsCount;
    }

    /* ______________________________________________________________________ */
    public void setVariables(ArrayList<String> variables) {
        this.variables = variables;
    }

    /* ______________________________________________________________________ */
    public void setNonBasicVariables(ArrayList<String> nonBasicVariables) {
        this.nonBasicVariables = nonBasicVariables;
    }

    /* ______________________________________________________________________ */
    public void setBasicVariables(ArrayList<String> basicVariables) {
        this.basicVariables = basicVariables;
    }

    /* ______________________________________________________________________ */
    public void setSimplexTable(Rational[][] simplexTable) {
        this.simplexTable = simplexTable;
    }

    /* ______________________________________________________________________ */
    public void setName(String name) {
        this.name = name;
    }
}
