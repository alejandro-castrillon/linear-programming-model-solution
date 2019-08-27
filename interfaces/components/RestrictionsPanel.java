package interfaces.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import interfaces.DialogPane;
import worldclasses.Function;
import worldclasses.Model;

public class RestrictionsPanel extends JPanel {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JTable restrictionsTable;
    private JButton addRestrictionButton;
    private JButton removeRestrictionButton;
    private JButton editRestrictionButton;

    private Model model;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public RestrictionsPanel(Model model) {
        this.model = model;

        // Init Panel
        setBackground(Color.white);
        setBorder(new TitledBorder("Restrictions"));
        setLayout(new BorderLayout(5, 5));

        initComponents();
        initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {

        // Add Restriction Button
        addRestrictionButton = new JButton("Add");
        addRestrictionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, addRestrictionButton.getMinimumSize().height));
        // addRestrictionButton = new JButton(new ImageIcon(Utilities.class.getResource("/images/close.png")));

        // Remove Restriction Button
        removeRestrictionButton = new JButton("Remove");
        removeRestrictionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, removeRestrictionButton.getMinimumSize().height));

        // Edit Restriction Button
        editRestrictionButton = new JButton("Edit");
        editRestrictionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, editRestrictionButton.getMinimumSize().height));

        // Restrictions Table
        restrictionsTable = new JTable(new DefaultTableModel(new String[]{"Restrictions", "Ecuations"}, 0) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        restrictionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        restrictionsTable.getTableHeader().setBackground(Color.white);
        restrictionsTable.getTableHeader().setReorderingAllowed(false);
        restrictionsTable.getTableHeader().setResizingAllowed(false);
        restrictionsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    restrictionsTable.clearSelection();
                }
            }
        });

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.white);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(addRestrictionButton);
        buttonsPanel.add(removeRestrictionButton);
        buttonsPanel.add(editRestrictionButton);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(restrictionsTable);
        scrollPane.getViewport().setBackground(Color.white);

        add(scrollPane, BorderLayout.CENTER, 0);
        add(buttonsPanel, BorderLayout.EAST, 1);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        addRestrictionButton.addActionListener(al -> {
            addRestrictionAction();
        });
        removeRestrictionButton.addActionListener(al -> {
            removeRestrictionAction();
        });
        editRestrictionButton.addActionListener(al -> {
            editRestrictionAction();
        });
    }

    /* ______________________________________________________________________ */
    public void addRestrictionAction() {
        String restriction = captureFunction("", "Add Restriction");

        if (restriction != null && !restriction.isEmpty()) {
            String[] row;

            model.addRestriction(restriction);
            row = new String[]{Function.parseFunction(restriction) + "", ""};
            getTableModel(restrictionsTable).addRow(row);
        }
    }

    /* ______________________________________________________________________ */
    public void removeRestrictionAction() {
        int index = restrictionsTable.getSelectedRow();
        if (index != -1) {
            if (DialogPane.option("Remove selected restriction?") == 0) {
                System.out.println("Remove Size = " + model.getRestrictionsCount() + " Index = " + index);
                System.out.println("Remove Restriction: " + model.getRestrictions().get(index) + " " + model.getRestrictionAt(index));
                model.removeRestrictionAt(index);
                getTableModel(restrictionsTable).removeRow(index);
            }
        }
    }

    /* ______________________________________________________________________ */
    public void editRestrictionAction() {
        String functionString, currentFunctionString;
        int index = restrictionsTable.getSelectedRow();

        if (index != -1) {
            currentFunctionString = Function.parseString(model.getRestrictionAt(index));
            functionString = captureFunction(currentFunctionString, "Edit Restriction");

            if (functionString != null && !functionString.isEmpty()) {
                if (!functionString.equals(currentFunctionString) && DialogPane.option("Edit selected restriction?") == 0) {

                    System.out.println("Edit Size = " + model.getRestrictionsCount() + " Index = " + index);
                    System.out.println("Edit Restriction: " + model.getRestrictions().get(index) + " " + model.getRestrictionAt(index));
                    model.setRestrictionAt(functionString, index);

                    getTableModel(restrictionsTable).setValueAt(Function.parseFunction(functionString) + "", index, 0);
                    getTableModel(restrictionsTable).setValueAt("", index, 1);
                }
            }
        }
    }

    /* ______________________________________________________________________ */
    public String captureFunction(String defaultString, String title) {
        return DialogPane.input(title, "Enter a function", defaultString);
    }

    /* ______________________________________________________________________ */
    public void clearRestrictionsTable() {
        getTableModel(restrictionsTable).setRowCount(0);
    }

    /* ______________________________________________________________________ */
    public void updateRestrictions() {
        ArrayList<Function> restrictions = model.getRestrictions();
        restrictions.forEach(restriction -> {
            getTableModel(restrictionsTable).addRow(new String[]{restriction + "", ""});
        });
    }

    /* ______________________________________________________________________ */
    public void updateEcuations() {
        ArrayList<Function> ecuations = model.getEcuations();
        for (int i = 0; i < ecuations.size(); i++) {
            getTableModel(restrictionsTable).setValueAt(ecuations.get(i), i, 1);
        }
    }
    /* GETTERS ______________________________________________________________ */
    public DefaultTableModel getTableModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    /* SETTERS ______________________________________________________________ */
    public void setModel(Model model) {
        this.model = model;
        updateRestrictions();
    }
}
