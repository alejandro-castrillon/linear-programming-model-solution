package interfaces;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import worldclasses.Model;
import worldclasses.Rational;

public class SimplexTableInterface extends JFrame implements ActionListener {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JTextField nonBasicVariablesTextField, basicVariablesTextField;
    private JButton generateInitialTableButton, buildInitialSimplexTableButton;
    private JRadioButton maximizeRadioButton, minimizeRadioButton;

    private JPanel initialTableSimplexPanel;
    private JTable nonBasicVariablesTable, basicVariablesTable, initialSimplexTable;

    private JPanel simplexMainPanel;
    private JButton calculateSolutionButton, newModelButton, setMButton;
    private JRadioButton decimalsRadioButton, rationalsRadioButton;

    private Model model;
    private Integer nonBasicVariablesCount, basicVariablesCount;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public SimplexTableInterface() {
        model = new Model();
        nonBasicVariablesCount = basicVariablesCount = 0;
        initComponents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        initFrame();
        initVariablesPanel();
        initInitialSimplexTablePanel();
        initSymplexTablePanel();
    }

    /* ______________________________________________________________________ */
    private void initFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Utilities.setIcon("/images/icon.png").getImage());
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(580, 470));
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Simplex Table Interface");
        setUndecorated(false);
    }

    /* ______________________________________________________________________ */
    private void initVariablesPanel() {
        JPanel variablesPanel = new JPanel(new GridLayout(2, 4, 2, 2));
        JLabel nonBasicVariablesLabel = new JLabel("Non-Basic");
        JLabel basicVariablesLabel = new JLabel("Basic:");
        ButtonGroup buttonGroup = new ButtonGroup();

        nonBasicVariablesTextField = new JTextField();
        basicVariablesTextField = new JTextField();
        generateInitialTableButton = new JButton("Generate");
        buildInitialSimplexTableButton = new JButton("Build");

        maximizeRadioButton = new JRadioButton("Maximize");
        minimizeRadioButton = new JRadioButton("Minimize");

        generateInitialTableButton.addActionListener(this);
        buildInitialSimplexTableButton.addActionListener(this);
        maximizeRadioButton.setSelected(true);

        buttonGroup.add(maximizeRadioButton);
        buttonGroup.add(minimizeRadioButton);

        variablesPanel.setBorder(new TitledBorder("Variables Count"));
        variablesPanel.add(maximizeRadioButton);
        variablesPanel.add(nonBasicVariablesLabel);
        variablesPanel.add(basicVariablesLabel);
        variablesPanel.add(generateInitialTableButton);
        variablesPanel.add(minimizeRadioButton);
        variablesPanel.add(nonBasicVariablesTextField);
        variablesPanel.add(basicVariablesTextField);
        variablesPanel.add(buildInitialSimplexTableButton);

        add(variablesPanel, BorderLayout.NORTH, 0);
    }

    /* ______________________________________________________________________ */
    private void initInitialSimplexTablePanel() {
        initialTableSimplexPanel = new JPanel(new BorderLayout());

        initialTableSimplexPanel.setBorder(new TitledBorder("Initial Simplex Table"));

        add(initialTableSimplexPanel, BorderLayout.CENTER, 1);
    }

    /* ______________________________________________________________________ */
    private void initSymplexTablePanel() {
        simplexMainPanel = new JPanel(new GridBagLayout());
        JPanel simplexTablePanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();

        calculateSolutionButton = new JButton("    Solution   ");
        newModelButton = new JButton(" New Model ");
        setMButton = new JButton("Set M Value");
        decimalsRadioButton = new JRadioButton("Decimals");
        rationalsRadioButton = new JRadioButton("Rationals", true);

        calculateSolutionButton.addActionListener(this);
        newModelButton.addActionListener(this);
        setMButton.addActionListener(this);

        buttonGroup.add(decimalsRadioButton);
        buttonGroup.add(rationalsRadioButton);

        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(calculateSolutionButton);
        buttonsPanel.add(newModelButton);
        buttonsPanel.add(setMButton);
        buttonsPanel.add(decimalsRadioButton);
        buttonsPanel.add(rationalsRadioButton);
        buttonsPanel.add(new JButton(new AbstractAction("Size") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DialogPane.show(getSize().width + ":" + getSize().height);
            }
        }));

        simplexTablePanel.setPreferredSize(new Dimension(getSize().width, (int) (getSize().height * 0.5)));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                simplexTablePanel.setPreferredSize(new Dimension(getSize().width, (int) (getSize().height * 0.5)));
            }
        });

        simplexTablePanel.setBorder(new TitledBorder("Simplex Tables"));
        simplexTablePanel.add(new JScrollPane(simplexMainPanel), BorderLayout.CENTER, 0);
        simplexTablePanel.add(buttonsPanel, BorderLayout.EAST, 1);

        add(simplexTablePanel, BorderLayout.SOUTH, 2);
    }

    /* ______________________________________________________________________ */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == generateInitialTableButton) {
            generateInitialSimplexTableAction();
        }
        if (ae.getSource() == buildInitialSimplexTableButton) {
            buildInitialSimplexTableAction();
        }
        if (ae.getSource() == calculateSolutionButton) {
            calculateSolutionAction();
        }
        if (ae.getSource() == setMButton) {
            newModelAction();
        }
        if (ae.getSource() == newModelButton) {
            newModelAction();
        }
    }

    /* ______________________________________________________________________ */
    private void generateInitialSimplexTableAction() {
        nonBasicVariablesTable = new JTable();
        basicVariablesTable = new JTable();

        JPanel variablesPanel = new JPanel(new GridLayout(1, 2));
        JTextField textField = new JTextField();
        DefaultCellEditor singleclick = new DefaultCellEditor(textField);

        String colsString1 = nonBasicVariablesTextField.getText();
        String colsString2 = basicVariablesTextField.getText();
        String string = "";

        for (int i = 0; i < colsString1.length(); i++) {
            if (Utilities.isNumber(colsString1.charAt(i))) {
                string += colsString1.charAt(i);
            }
        }
        colsString1 = string;
        string = "";
        for (int i = 0; i < colsString2.length(); i++) {
            if (Utilities.isNumber(colsString2.charAt(i))) {
                string += colsString2.charAt(i);
            }
        }
        colsString2 = string;

        if (!colsString1.isEmpty() && !colsString2.isEmpty()) {

            textField.setBorder(null);
            singleclick.setClickCountToStart(1);

            nonBasicVariablesCount = Integer.parseInt(colsString1);
            basicVariablesCount = Integer.parseInt(colsString2);
            nonBasicVariablesTextField.setText(nonBasicVariablesCount + "");
            basicVariablesTextField.setText(basicVariablesCount + "");

            variablesPanel.setPreferredSize(new Dimension((int) (getWidth() / 2.6), getHeight()));

            nonBasicVariablesTable = new JTable(new DefaultTableModel());
            getTableModel(nonBasicVariablesTable).addColumn("Non-Basic", new String[nonBasicVariablesCount]);
            nonBasicVariablesTable.setDefaultEditor(nonBasicVariablesTable.getColumnClass(0), singleclick);

            basicVariablesTable = new JTable(new DefaultTableModel());
            getTableModel(basicVariablesTable).addColumn("Basic", new String[basicVariablesCount]);
            basicVariablesTable.setDefaultEditor(basicVariablesTable.getColumnClass(0), singleclick);

            variablesPanel.add(new JScrollPane(nonBasicVariablesTable));
            variablesPanel.add(new JScrollPane(basicVariablesTable));

            initialTableSimplexPanel.add(variablesPanel, BorderLayout.WEST, 0);
            initialTableSimplexPanel.updateUI();
        }
    }

    /* ______________________________________________________________________ */
    private void buildInitialSimplexTableAction() {
        initialSimplexTable = new JTable(new DefaultTableModel());
        JTextField textField = new JTextField();
        DefaultCellEditor singleclick = new DefaultCellEditor(textField);
        ArrayList<String> variables = new ArrayList<>();

        textField.setBorder(null);
        singleclick.setClickCountToStart(1);

        for (int i = 0; i < nonBasicVariablesCount; i++) {
            variables.add((String) nonBasicVariablesTable.getValueAt(i, 0));
        }
        for (int i = 0; i < basicVariablesCount; i++) {
            variables.add((String) basicVariablesTable.getValueAt(i, 0));
        }
        Utilities.sortStringListDecreasing(variables);
        variables.add("Solution");

        for (int i = 0; i < variables.size(); i++) {
            getTableModel(initialSimplexTable).addColumn(variables.get(i), new String[basicVariablesCount]);
            initialSimplexTable.setDefaultEditor(initialSimplexTable.getColumnClass(i), singleclick);
        }
        initialTableSimplexPanel.add(new JScrollPane(initialSimplexTable), BorderLayout.CENTER, 1);
        initialTableSimplexPanel.updateUI();
    }

    /* ______________________________________________________________________ */
    public void calculateSolutionAction() {
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<String> nonBasicVariables = new ArrayList<>();
        ArrayList<String> basicVariables = new ArrayList<>();
        int rows = basicVariablesCount;
        int cols = nonBasicVariablesCount + basicVariablesCount + 1;
        Rational[][] simplexTable = new Rational[rows][cols];

        simplexMainPanel.removeAll();

        for (int i = 0; i < nonBasicVariablesCount; i++) {
            nonBasicVariables.add((String) nonBasicVariablesTable.getValueAt(i, 0));
        }
        for (int i = 0; i < basicVariablesCount; i++) {
            basicVariables.add((String) basicVariablesTable.getValueAt(i, 0));
        }
        for (int i = 0; i < cols - 1; i++) {
            variables.add(initialSimplexTable.getColumnName(i));
        }
        variables.add("");
        for (int i = 0; i < rows; i++) {
            System.out.println();
            for (int j = 0; j < cols; j++) {
                simplexTable[i][j] = new Rational((String)initialSimplexTable.getValueAt(i, j));
                System.out.print(simplexTable[i][j] + "\t");
            }
        }

        while (nonBasicVariables.size() < basicVariables.size()) {
            nonBasicVariables.add("");
        }
        while (basicVariables.size() < nonBasicVariables.size()) {
            basicVariables.add("");
        }

        model.setRestrictionsCount(rows - 1);
        model.setVariablesCount(cols);
        model.setTypeModel(maximizeRadioButton.isSelected());
        model.setVariables(variables);
        model.setNonBasicVariables(nonBasicVariables);
        model.setBasicVariables(basicVariables);
        model.setSimplexTable(simplexTable);

        //addSimplexTable();
        if (model.getArtificialVariablesFunctions().size() > 1) {
            model.penalizeArtificialVariables();
            addSimplexTable();
        }
        while (!model.isOptimal()) {
            model.calculateSolution();
            System.out.println("Basic Variables:\t" + model.getBasicVariables());
            System.out.println("Non-Basic Variables:\t" + model.getNonBasicVariables());
            addSimplexTable();
        }
    }

    /* ______________________________________________________________________ */
    public void addSimplexTable() {
        JTable table = new JTable(new DefaultTableModel());
        int variablesCount = model.getVariablesCount();
        int restrictionsCount = model.getRestrictionsCount();
        ArrayList<String> nonBasicVariables = model.getNonBasicVariables();
        ArrayList<String> basicVariables = model.getBasicVariables();
        Rational[][] simplexTable = model.getSimplexTable();
        Object value;

        getTableModel(table).addColumn("Non-Basics");
        getTableModel(table).addColumn("Basics");
        for (int i = 0; i < model.getVariablesCount() - 1; i++) {
            getTableModel(table).addColumn(initialSimplexTable.getColumnName(i));
            table.getColumnModel().getColumn(0).setWidth(40);
        }
        getTableModel(table).addColumn("Solution");

        int higher = nonBasicVariables.size() > basicVariables.size()
                ? nonBasicVariables.size() : basicVariables.size();

        for (int i = 0; i < higher; i++) {
            getTableModel(table).addRow(new String[variablesCount + 2]);

            for (int j = 0; j < variablesCount; j++) {
                if (i < nonBasicVariables.size()) {
                    getTableModel(table).setValueAt(nonBasicVariables.get(i), i, 0);
                }
                if (i < restrictionsCount + 1) {
                    value = decimalsRadioButton.isSelected() ? simplexTable[i][j].value() : simplexTable[i][j];
                    getTableModel(table).setValueAt(basicVariables.get(i), i, 1);
                    getTableModel(table).setValueAt(value, i, j + 2);
                }
            }
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = simplexMainPanel.getComponentCount();
        simplexMainPanel.add(table.getTableHeader(), constraints);
        constraints.insets = new Insets(0, 0, 5, 0);
        constraints.gridy = simplexMainPanel.getComponentCount();

        simplexMainPanel.add(table, constraints);
        simplexMainPanel.updateUI();
    }

    /* ______________________________________________________________________ */
    public void setMAction() {
        String mString = DialogPane.input("Set M", "Enter M Value:", model.getMValue());
        if (mString != null && !mString.isEmpty()) {
            model.setMValue(Integer.parseInt(mString));
        }
    }

    /* ______________________________________________________________________ */
    public void newModelAction() {
        model = new Model();
        nonBasicVariablesTextField.setText("");
        basicVariablesTextField.setText("");
        initialTableSimplexPanel.removeAll();
        simplexMainPanel.removeAll();
        initialTableSimplexPanel.updateUI();
        simplexMainPanel.updateUI();
    }

    /* ______________________________________________________________________ */
    private DefaultTableModel getTableModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    /**
     * @param i1. Simplex table rows
     * @param i2. Simplex table cols
     * @param b. Model type
     * @param as1. Non-basic variables
     * @param as2. Basic variables
     * @param im. Simplex table
     */
    /* ______________________________________________________________________ */
    public void setModel(int i1, int i2, boolean b, String[] as1, String[] as2, Integer[][] im) {
        int nbvc = i2 - i1 - 1;
        int bvc = i1;

        nonBasicVariablesTextField.setText(nbvc + "");
        basicVariablesTextField.setText(bvc + "");
        maximizeRadioButton.setSelected(b);
        minimizeRadioButton.setSelected(!b);
        generateInitialSimplexTableAction();

        for (int i = 0; i < nbvc; i++) {
            nonBasicVariablesTable.setValueAt(as1[i], i, 0);
        }
        for (int i = 0; i < bvc; i++) {
            basicVariablesTable.setValueAt(as2[i], i, 0);
        }
        buildInitialSimplexTableAction();

        for (int i = 0; i < i1; i++) {
            for (int j = 0; j < i2; j++) {
                this.initialSimplexTable.setValueAt(im[i][j].toString(), i, j);
            }
        }
    }
}
