package interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class Utilities {

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic1, int mnemonic2, int mnemonic3, String iconPath) {
        JMenuItem menuItem = new JMenuItem(name, setIcon(iconPath));
        menuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        menuItem.setForeground(Color.BLACK);
        menuItem.setMnemonic(mnemonic1);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic2, mnemonic3));
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic1, int mnemonic2, String iconPath) {
        JMenuItem menuItem = new JMenuItem(name, setIcon(iconPath));
        menuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        menuItem.setForeground(Color.BLACK);
        menuItem.setMnemonic(mnemonic1);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic2, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic, String iconPath) {
        JMenuItem menuItem = new JMenuItem(name, setIcon(iconPath));
        menuItem.setFont(new Font("Dialog", Font.PLAIN, 12));
        menuItem.setForeground(Color.BLACK);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic1, int mnemonic2, int mnemonic3) {
        return setMenuItem(name, actionListener, mnemonic1, mnemonic2, mnemonic3, null);
    }

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic1, int mnemonic2) {
        return setMenuItem(name, actionListener, mnemonic1, mnemonic2, null);
    }

    /* ______________________________________________________________________ */
    public static JMenuItem setMenuItem(String name, ActionListener actionListener, int mnemonic) {
        return setMenuItem(name, actionListener, mnemonic, null);
    }

    /* ______________________________________________________________________ */
    public static JCheckBox setCheckBox(JCheckBox checkBox, Runnable trueRun, Runnable falseRun, int mnemonic) {
        checkBox.setFont(new Font("Dialog", Font.PLAIN, 12));
        checkBox.setForeground(Color.BLACK);
        checkBox.setMnemonic(mnemonic);
        checkBox.addActionListener(ae -> {
            if (checkBox.isSelected() && trueRun != null) {
                trueRun.run();
            } else if (falseRun != null) {
                falseRun.run();
            }
        });
        return checkBox;
    }

    /* ______________________________________________________________________ */
    public static JCheckBox setCheckBox(String text, boolean selected, Runnable trueRun, Runnable falseRun, int mnemonic) {
        return setCheckBox(new JCheckBox(text, selected), trueRun, falseRun, mnemonic);
    }

    /* ______________________________________________________________________ */
    public static JCheckBox setCheckBox(JCheckBox checkBox, int mnemonic) {
        return setCheckBox(checkBox, null, null, mnemonic);
    }

    /* ______________________________________________________________________ */
    public static JCheckBox setCheckBox(String text, boolean selected, int mnemonic) {
        return setCheckBox(new JCheckBox(text, selected), null, null, mnemonic);
    }

    /* ______________________________________________________________________ */
    public static ImageIcon setIcon(String iconPath) { // /Package/Image.ext
        return iconPath != null ? new ImageIcon(Utilities.class.getResource(iconPath)) : null;
    }

    /* ______________________________________________________________________ */
    public static void output(String string) {
        Calendar c = Calendar.getInstance();
        System.out.println("[" + c.get(Calendar.HOUR_OF_DAY) + ":"
                + c.get(Calendar.MINUTE) + ":"
                + c.get(Calendar.SECOND) + "] "
                + string);
    }

    /* ______________________________________________________________________ */
    public static ImageIcon setImage(JLabel label, String source) { // /Package/Image.ext
        final ImageIcon imageIcon;
        imageIcon = new ImageIcon(Utilities.class.getResource(source));
        label.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(
                label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH)));
        return imageIcon;
    }

    /* ______________________________________________________________________ */
    public static void setClipboard(String string) {
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new java.awt.datatransfer.StringSelection(string),
                (java.awt.datatransfer.Clipboard clpbrd, Transferable t) -> {
                });
    }

    /* ______________________________________________________________________ */
    public static String getClipboard() {
        String result = "";
        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                System.out.println(ex);
            }
        }
        return result;
    }

    /* ______________________________________________________________________ */
    public static void sortStringListGrowing(ArrayList<String> arrayList) {
        arrayList.sort((String s1, String s2) -> {
            Character c1 = s1.charAt(0);
            Character c2 = s2.charAt(0);
            return c1.compareTo(c2);
        });
    }

    /* ______________________________________________________________________ */
    public static void sortStringListDecreasing(ArrayList<String> arrayList) {
        arrayList.sort((String s1, String s2) -> {
            Character c1 = s1.charAt(0);
            Character c2 = s2.charAt(0);
            return c2.compareTo(c1);
        });
    }

    /* ______________________________________________________________________ */
    public static boolean isNumber(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }
}
