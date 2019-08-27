package interfaces.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import interfaces.ModelInterface;

public class SimplexTablesPanel extends JPanel {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JPanel simplexPanel;
    private JPanel simplexMainPanel;
    private JScrollPane simplexPanelScrollPane;
    private JSeparator separator;

    private final ModelInterface parent;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public SimplexTablesPanel(ModelInterface parent) {
        this.parent = parent;

        // Init Panel
        setBackground(Color.white);
        setLayout(new BorderLayout());

        initComponents();
        initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        // Panels
        simplexMainPanel = new JPanel(new GridBagLayout());

        simplexPanel = new JPanel(new BorderLayout(5, 5));

        // Other Components
        simplexPanelScrollPane = new JScrollPane(simplexMainPanel);

        separator = new JSeparator();

        separator.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));

        simplexPanel.setBorder(new TitledBorder("Simplex Tables"));
        simplexPanel.setBackground(Color.white);
        simplexPanel.add(simplexPanelScrollPane, BorderLayout.CENTER, 0);
        simplexMainPanel.setBackground(Color.white);

        add(separator, BorderLayout.NORTH);
        add(simplexPanel, BorderLayout.CENTER);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        separator.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                resizePanelAtSeparatorDragged(me);
            }
        });

        simplexMainPanel.addMouseWheelListener(mwe -> {
            int scrollUnits = mwe.getUnitsToScroll();

            if (mwe.isShiftDown()) {
                int horizontalValue = simplexPanelScrollPane.getHorizontalScrollBar().getValue();
                simplexPanelScrollPane.getHorizontalScrollBar().setValue(horizontalValue + scrollUnits * 10);
            } else {
                int verticalValue = simplexPanelScrollPane.getVerticalScrollBar().getValue();
                simplexPanelScrollPane.getVerticalScrollBar().setValue(verticalValue + scrollUnits * 10);
            }
        });
    }

    /* ______________________________________________________________________ */
    public void resizePanelAtSeparatorDragged(MouseEvent me) {
        Dimension size = simplexPanel.getSize();
        if (size.height <= parent.getSize().height - 215) {
            int height = (int) (size.height - me.getY());
            if (height < parent.getSize().height - 215 && height > 100) {
                simplexPanel.setPreferredSize(new Dimension(size.width, height));
                simplexPanel.updateUI();
            }
        }
    }

    /* ______________________________________________________________________ */
    public void resizePanelAtResizeOrMoveFrame(Dimension size) {
        simplexPanel.setPreferredSize(new Dimension(size.width, size.height - 215));
        simplexPanel.updateUI();
    }

    /* ______________________________________________________________________ */
    public void removeTables() {
        simplexMainPanel.removeAll();
    }

    /* ______________________________________________________________________ */
    public void addComponent(Component component, Object constrains) {
        simplexMainPanel.add(component, constrains);
    }

    /* ______________________________________________________________________ */
    public void update() {
        simplexMainPanel.updateUI();
    }

    /* GETTERS ______________________________________________________________ */
    public Integer getSimplexTablesCount() {
        return simplexMainPanel.getComponentCount();
    }
}
