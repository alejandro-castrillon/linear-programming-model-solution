package interfaces.components;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import interfaces.DialogPane;
import worldclasses.Function;
import worldclasses.Model;

public class ObjetiveFunctionPanel extends JPanel {

    /* ATTRIBUTES ___________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JPanel objetiveTextFieldsPanel;

    private JLabel modelTypeLabel;
    private JTextField objetiveFunctionField;
    private JTextField newObjetiveFunctionField;
    private JButton objetiveFunctionButton;

    private Model model;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public ObjetiveFunctionPanel(Model model) {
        this.model = model;

        // Init Panel
        setBackground(Color.white);
        setBorder(new TitledBorder("Objetive Function"));
        setLayout(new BorderLayout(5, 5));

        initComponents();
        initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {

        // Model Type Label
        modelTypeLabel = new JLabel();

        // Objetive Function Text Field
        objetiveFunctionField = new JTextField();
        objetiveFunctionField.setBackground(Color.white);
        objetiveFunctionField.setEditable(false);

        // New Objetive Funtion Text Field
        newObjetiveFunctionField = new JTextField();
        newObjetiveFunctionField.setBackground(Color.white);
        newObjetiveFunctionField.setEditable(false);

        // Set Objetive Function Button
        objetiveFunctionButton = new JButton("Set");

        // Objetive Text Fields Panel
        objetiveTextFieldsPanel = new JPanel();
        objetiveTextFieldsPanel.setLayout(new BoxLayout(objetiveTextFieldsPanel, BoxLayout.X_AXIS));
        objetiveTextFieldsPanel.add(objetiveFunctionField);
        objetiveTextFieldsPanel.add(newObjetiveFunctionField);
        // <editor-fold defaultstate="collapsed" desc="Separator">
//        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
//        separator.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
//        separator.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent me) {
//                Dimension size = newObjetiveFunctionTextField.getSize();
//                if (size.width >= 0) {
//                    int width = size.width - me.getX();
//                    if (width > 10 && width < getSize().width / 2) {
//                        newObjetiveFunctionTextField.setPreferredSize(new Dimension(width, size.height));
//                        objetiveTextFieldsPanel.updateUI();
//                    }
//                }
//                System.out.println(size.width + " " + me.getX());
//            }
//        });
//        objetiveTextFieldsPanel.add(separator);
        // </editor-fold>

        add(modelTypeLabel, BorderLayout.WEST);
        add(objetiveTextFieldsPanel, BorderLayout.CENTER);
        add(objetiveFunctionButton, BorderLayout.EAST);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        objetiveFunctionButton.addActionListener(al -> {
            objetiveFunctionAction();
        });
    }

    /* ______________________________________________________________________ */
    public void objetiveFunctionAction() {
        String objetiveString = "Z=";

        String objetiveFunctionButtonText = objetiveFunctionButton.getText();

        Function objetiveFunction = model.getObjetiveFunction();

        // Capture new objetive function
        String message = objetiveFunctionButtonText.equals("Set") ? "Z=" : Function.parseString(objetiveFunction);
        String title = objetiveFunctionButtonText.equals("Set") ? "Set" : "Edit";
        objetiveString = captureFunction(message, title + " Objetive Function");

        if (objetiveString != null && !objetiveString.isEmpty()) {

            model.setObjetiveFunction(objetiveString);
            setTypeModel();

            objetiveFunctionField.setText(model.getObjetiveFunction() + "");

            if (objetiveFunctionButtonText.equals("Set")) {
                objetiveFunctionButton.setText("Edit");
            }
            newObjetiveFunctionField.setText("");
        }
        updateUI();
    }

    /* ______________________________________________________________________ */
    public String captureFunction(String defaultString, String title) {
        return DialogPane.input(title, "Enter a function", defaultString);
    }

    /* ______________________________________________________________________ */
    public void setTypeModel() {
        int type;
        String[] typeOptions = {"Maximize", "Minimize"};
        type = DialogPane.option("Model Type", "Choose the model type:", typeOptions, typeOptions[0]);

        if (type == 0 || type == 1) {
            model.setTypeModel(type == 0);
            modelTypeLabel.setText(type == 0 ? "MAX:" : "MIN:");
        }
    }

    /* SETTERS ______________________________________________________________ */
    public void setModelTypeLabelText(String string) {
        modelTypeLabel.setText(string);
    }

    /* ______________________________________________________________________ */
    public void setObjetiveFunctionFieldText(String string) {
        objetiveFunctionField.setText(string);
    }

    /* ______________________________________________________________________ */
    public void setNewObjetiveFunctionFieldText(String string) {
        newObjetiveFunctionField.setText(string);
    }

    /* ______________________________________________________________________ */
    public void setObjetiveFunctionButtonText(String string) {
        objetiveFunctionButton.setText(string);
    }

    /* ______________________________________________________________________ */
    public void setModel(Model model) {
        this.model = model;
        setModelTypeLabelText(model.getTypeModel() == Model.MAXIMIZE ? "MAX:" : "MIN:");
        setObjetiveFunctionFieldText(model.getObjetiveFunction() + "");
        setNewObjetiveFunctionFieldText("");
        setObjetiveFunctionButtonText("Edit");
    }
}
