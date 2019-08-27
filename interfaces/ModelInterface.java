package interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import worldclasses.FileModel;
import worldclasses.Model;
import worldclasses.Rational;
import interfaces.components.ObjetiveFunctionPanel;
import interfaces.components.RestrictionsPanel;
import interfaces.components.SimplexTablesPanel;
import interfaces.components.Menu;
import javax.swing.JPanel;

public final class ModelInterface extends JFrame {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private Menu menu;
    private ObjetiveFunctionPanel objetiveFunctionPanel;
    private RestrictionsPanel restrictionsPanel;
    private SimplexTablesPanel simplexTablesPanel;

    private Model model;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public ModelInterface() {
        model = new Model();

        // Init Frame
        setIconImage(Utilities.setIcon("/images/icon.png").getImage());
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(300, 250));
        setTitle("Model Interface");

        setSize(600, 500);
        setLocationRelativeTo(null);

        initComponents();
        initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {

        menu = new Menu(this);
        objetiveFunctionPanel = new ObjetiveFunctionPanel(model);
        restrictionsPanel = new RestrictionsPanel(model);
        simplexTablesPanel = new SimplexTablesPanel(this);

        setJMenuBar(menu);
        add(objetiveFunctionPanel, BorderLayout.NORTH, 0);
        add(restrictionsPanel, BorderLayout.CENTER, 1);
        add(simplexTablesPanel, BorderLayout.SOUTH, 2);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        // Frame Events
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                simplexTablesPanel.resizePanelAtResizeOrMoveFrame(getSize());
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
                simplexTablesPanel.resizePanelAtResizeOrMoveFrame(getSize());
            }
        });
    }

    /* ______________________________________________________________________ */
    public void calculateSolutionAction() {

        simplexTablesPanel.removeTables();

        if (model.getObjetiveFunction() == null || model.getRestrictionsCount() == 0) {
            return;
        }

        // Calculate initial colution of model
        model.calculateInitialSolution();

        // Get new objetive function
        objetiveFunctionPanel.setNewObjetiveFunctionFieldText(model.getNewObjetiveFunction() + "");

        // Get ecuations model
        //restrictionsPanel.setEcuations(model.getEcuations());
        restrictionsPanel.updateEcuations();

        addSimplexTable(); // Add initial solution

        if (model.getArtificialVariablesFunctions().size() > 1) {
            model.penalizeArtificialVariables();
            addSimplexTable();  // Add penalized objetive function solution
        }

        // While model is nor optimal calculate a new solution and add a new simplexTable
        while (!model.isOptimal()) {
            model.calculateSolution();
            System.out.println("Basic Variables:\t" + model.getBasicVariables());
            System.out.println("Non-Basic Variables:\t" + model.getNonBasicVariables());
            addSimplexTable();
        }

        ArrayList<String> basicVariables = model.getBasicVariables();
        ArrayList<String> nonBasicVariables = model.getNonBasicVariables();

        String solution = "";
        ArrayList<String> variables = model.getVariables();

        for (int i = 0; i < variables.size(); i++) {
            String variable = variables.get(i);
            if (nonBasicVariables.indexOf(variable) != -1) {
                if (!variable.isEmpty()) {
                    solution += variable + " = 0\n";
                }
            } else if (!variable.isEmpty()) {
                for (int j = 0; j < basicVariables.size(); j++) {
                    if (basicVariables.get(j).equals(variable)) {
                        solution += variable + " = " + model.getSimplexTable()[j][variables.size() - 1] + "\n";
                        break;
                    }
                }
            }
        }
        System.out.println(solution);
        DialogPane.show(solution, "Optimal Solution");
        
        ((JPanel)getContentPane()).updateUI();
    }

    /* ______________________________________________________________________ */
    public void clearModelAction() {
        model = new Model();

        objetiveFunctionPanel.setModel(this.model);
        restrictionsPanel.setModel(this.model);

        objetiveFunctionPanel.setModelTypeLabelText("");
        objetiveFunctionPanel.setObjetiveFunctionFieldText("");
        objetiveFunctionPanel.setNewObjetiveFunctionFieldText("");
        objetiveFunctionPanel.setObjetiveFunctionButtonText("Set");

        restrictionsPanel.clearRestrictionsTable();

        simplexTablesPanel.removeTables();

        setTitle("Model Interface");
    }

    /* ______________________________________________________________________ */
    public void setMValueAction() {
        String mString = DialogPane.input("Set M", "Enter M Value:", model.getMValue());
        if (mString != null && !mString.isEmpty()) {
            model.setMValue(Integer.parseInt(mString));
        }
    }

    /* ______________________________________________________________________ */
    public void openModelAction() {
        Model newModel = FileModel.openFile();

        if (newModel != null) {
            clearModelAction();
            setModel(newModel);
            setTitle(newModel.getName());
        }
    }

    /* ______________________________________________________________________ */
    public void saveModelAction() {
        if (model != null) {
            if (model.getObjetiveFunction() != null && model.getRestrictionsCount() != 0) {
                String modelName = FileModel.saveFile(model);
                if (modelName != null) {
                    setTitle(modelName);
                }
            }
        }
    }

    /* ______________________________________________________________________ */
    public void saveAsModelAction() {
        if (model != null) {
            if (model.getObjetiveFunction() != null && model.getRestrictionsCount() != 0) {
                String modelName = FileModel.saveFileAs(model);
                if (modelName != null) {
                    setTitle(modelName);
                }
            }
        }
    }

    /* ______________________________________________________________________ */
    public void addSimplexTable() {
        // JTable
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return menu.isEditable();
            }
        };

        // Variables and restrictions count
        int variablesCount = model.getVariablesCount();
        int restrictionsCount = model.getRestrictionsCount();

        // ArrayList variables
        ArrayList<String> nonBasicVariables = model.getNonBasicVariables();
        ArrayList<String> basicVariables = model.getBasicVariables();

        // Simplex Table
        Rational[][] simplexTable = model.getSimplexTable();
        Object value;

        // Init table
        table.setModel(tableModel);
        table.getTableHeader().setBackground(Color.white);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    table.clearSelection();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.clearSelection();
            }
        });

        table.setCellSelectionEnabled(menu.isSelectable());

        // Add Basics and Non-Basic variables
        getTableModel(table).addColumn("Non-Basics");
        getTableModel(table).addColumn("Basics");

        // Add Variable's columns
        for (int i = 0; i < model.getVariablesCount() - 1; i++) {
            getTableModel(table).addColumn(model.getVariables().get(i));
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
                    value = menu.isRational() ? simplexTable[i][j] : simplexTable[i][j].value();

                    getTableModel(table).setValueAt(basicVariables.get(i), i, 1);
                    getTableModel(table).setValueAt(value, i, j + 2);
                }
            }
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = simplexTablesPanel.getSimplexTablesCount();
        simplexTablesPanel.addComponent(table.getTableHeader(), constraints);

        constraints.insets = new Insets(0, 0, 5, 0);
        constraints.gridy = simplexTablesPanel.getSimplexTablesCount();
        simplexTablesPanel.addComponent(table, constraints);

        simplexTablesPanel.update();
    }

    /* GETTERS ______________________________________________________________ */
    public DefaultTableModel getTableModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    /* SETTERS ______________________________________________________________ */
    public void setModel(Model model) {
        this.model = model;
        objetiveFunctionPanel.setModel(this.model);
        restrictionsPanel.setModel(this.model);
    }
}
