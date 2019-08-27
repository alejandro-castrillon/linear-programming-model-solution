package interfaces;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    /* ATTRIBUTES ___________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private JTabbedPane tabbedPane;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public MainFrame() {
        initComponents(); 
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        tabbedPane = new JTabbedPane();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Utilities.setIcon("/images/icon.png").getImage());
        setLayout(new GridLayout());
        setMinimumSize(new Dimension(580, 490));
        setLocation((screen.width / 2) - (getWidth() / 2), (screen.height / 2) - (getHeight() / 2));
        setResizable(true);
        setTitle("Linear Programming Model Solution");
        setUndecorated(false);

        tabbedPane.addTab("Model Interface", new ModelInterface().getContentPane());
        tabbedPane.addTab("Simplex Table Interface", new SimplexTableInterface().getContentPane());

        add(tabbedPane);
    }
}
