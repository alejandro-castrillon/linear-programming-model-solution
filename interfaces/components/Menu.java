package interfaces.components;

import interfaces.ModelInterface;
import interfaces.Utilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;

    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem renameItem;
    private JMenuItem deleteItem;
    private JMenuItem exitItem;

    private JMenuItem calculateSolutionItem;
    private JMenuItem setMValueItem;
    private JMenuItem clearModelItem;

    private JCheckBox rationalsCheckBox;
    private JCheckBox selectableCellsCheckBox;
    private JCheckBox editableCellsCheckBox;

    private JMenuItem aboutItem;

    private final ModelInterface modelInterface;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public Menu(ModelInterface modelInterface) {
        this.modelInterface = modelInterface;

        // Init Menu
        setBackground(Color.white);
        setBorder(null);

        initComponents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        fileMenu = new JMenu("File");
        editMenu = new JMenu("Edit");
        viewMenu = new JMenu("View");
        helpMenu = new JMenu("Help");

        initEvents();
        // <editor-fold defaultstate="collapsed" desc="File Menu">
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        fileMenu.addSeparator();

        fileMenu.add(renameItem);
        fileMenu.add(deleteItem);

        fileMenu.addSeparator();

        fileMenu.add(exitItem);

        fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Edit Menu">
        editMenu.add(calculateSolutionItem);
        editMenu.add(setMValueItem);

        editMenu.addSeparator();

        editMenu.add(clearModelItem);

        editMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        editMenu.setMnemonic(KeyEvent.VK_E);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="View Menu">
        // CheckBoxes
        rationalsCheckBox = new JCheckBox("Rationals", true);
        selectableCellsCheckBox = new JCheckBox("Selectable", false);
        editableCellsCheckBox = new JCheckBox("Editable", false);

        viewMenu.add(Utilities.setCheckBox(rationalsCheckBox, 'R'));
        viewMenu.add(Utilities.setCheckBox(selectableCellsCheckBox, 'S'));
        viewMenu.add(Utilities.setCheckBox(editableCellsCheckBox, 'E'));

        viewMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        viewMenu.setMnemonic(KeyEvent.VK_V);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Help Menu">
        helpMenu.add(aboutItem);

        helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
        helpMenu.setMnemonic(KeyEvent.VK_H);
        // </editor-fold>

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(helpMenu);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        newItem = Utilities.setMenuItem("New", ae -> {
            new ModelInterface().setVisible(true);
        }, 'N', 'N', "/images/new.png");
        openItem = Utilities.setMenuItem("Open", ae -> {
            modelInterface.openModelAction();
        }, 'O', 'O', "/images/open.png");
        saveItem = Utilities.setMenuItem("Save", ae -> {
            modelInterface.saveModelAction();
        }, 'S', 'S', "/images/save.png");
        saveAsItem = Utilities.setMenuItem("Save As", ae -> {
            modelInterface.saveAsModelAction();
        }, 'A');
        renameItem = Utilities.setMenuItem("Rename", ae -> {
            // renameModel();
        }, 'R', 'R', "/images/rename.png");
        deleteItem = Utilities.setMenuItem("Delete", ae -> {
            // deleteModel();
        }, 'D', 'D', "/images/delete.png");
        exitItem = Utilities.setMenuItem("Exit", ae -> {
            modelInterface.dispose();
        }, 'E', KeyEvent.VK_F4, ActionEvent.ALT_MASK, "/images/close.png");

        calculateSolutionItem = Utilities.setMenuItem("Calculate Solution", ae -> {
            modelInterface.calculateSolutionAction();
        }, 'C', 'I');
        setMValueItem = Utilities.setMenuItem("Set M Value", ae -> {
            modelInterface.setMValueAction();
        }, 'M', 'M');
        clearModelItem = Utilities.setMenuItem("Clear Model", ae -> {
            modelInterface.clearModelAction();
        }, 'L', 'L', "/images/clear.png");

        aboutItem = Utilities.setMenuItem("About", ae -> {
        }, 'A');
    }

    /* ______________________________________________________________________ */
    public boolean isEditable() {
        return editableCellsCheckBox.isSelected();
    }

    /* ______________________________________________________________________ */
    public boolean isSelectable() {
        return selectableCellsCheckBox.isSelected();
    }

    /* ______________________________________________________________________ */
    public boolean isRational() {
        return rationalsCheckBox.isSelected();
    }
}
