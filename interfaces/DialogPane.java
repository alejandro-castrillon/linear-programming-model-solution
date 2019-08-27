package interfaces;

import java.awt.Dimension;
import javax.swing.JOptionPane;

public class DialogPane {

    /* ATTRIBUTES ___________________________________________________________ */
    //<editor-fold defaultstate="collapsed" desc="Attributes">
    public final static int P = -1, E = 0, I = 1, W = 2, Q = 3;
    //</editor-fold>

    /* METHODS ______________________________________________________________ */
    public static void show(String message) {
        show(message, -1);
    }

    public static void show(String message, String title) {
        show(message, title, -1);
    }

    /* ______________________________________________________________________ */
    public static void show(String message, int type) {
        String title = type == E ? "Error" : type == I ? "Information"
                : type == W ? "Warning" : type == Q ? "Question" : "";
        show(message, title, type);
    }

    /* ______________________________________________________________________ */
    public static void show(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }

    /* ______________________________________________________________________ */
    public static int option(String message) {
        return JOptionPane.showOptionDialog(null,
                message, "Option", JOptionPane.YES_NO_OPTION, Q, null, null, 0);
    }

    /* ______________________________________________________________________ */
    public static int option(String title, String message) {
        return JOptionPane.showOptionDialog(null,
                message, title, JOptionPane.YES_NO_CANCEL_OPTION, Q, null, null, 0);
    }

    /* ______________________________________________________________________ */
    public static int option(String title, String message, Object[] selectionValues, Object initialSelectionValue) {
        return JOptionPane.showOptionDialog(null, message, title, 0, Q, null, selectionValues, initialSelectionValue);
    }

    /* ______________________________________________________________________ */
    public static String input(String title, String message, Object text) {
        return (String) JOptionPane.showInputDialog(null, message, title, P, null, null, text);
    }
}
