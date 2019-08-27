package worldclasses;

import interfaces.Utilities;
import interfaces.DialogPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class FileModel {

    /* ATTRIBUTTES __________________________________________________________ */
    // <editor-fold defaultstate="collapsed" desc="Attributes">
    private static File file;
    // </editor-fold>

    /* CONSTRUCTORS _________________________________________________________ */
    public FileModel() {
    }

    /* METHODS ______________________________________________________________ */
    public static Model openFile() {

        Model model = null;
        JFileChooser fileChooser;

        fileChooser = new JFileChooser(file != null ? file.getAbsolutePath() : System.getProperty("user.dir") + "/Files");
        int state = fileChooser.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            System.out.println(file.getName() + " Opened");
            model = readFileText();

        } else if (state == JFileChooser.CANCEL_OPTION) {
            System.out.println("No file selected");
            file = null;
        }

        return model;
    }

    /* ______________________________________________________________________ */
    private static boolean getObjetiveFunction(String text, Model model) {

        if (text != null) {
            if (!text.isEmpty()) {
                try {
                    model.setObjetiveFunction(text);
                    return true;
                } catch (Exception e) {
                    DialogPane.show("Incorrect model structure in file", "Objetive Function do not found", DialogPane.E);
                }
            }
        }
        return false;
    }

    /* ______________________________________________________________________ */
    private static boolean getTypeFunction(String text, Model model) {

        if (text != null) {
            if (!text.isEmpty()) {
                if (text.equals("MAXIMIZE")) {
                    model.setTypeModel(true);
                    return true;
                } else if (text.equals("MINIMIZE")) {
                    model.setTypeModel(false);
                    return true;
                } else {
                    model.setTypeModel(true);
                    return true;
                }
            }
        }

        return false;
    }

    /* ______________________________________________________________________ */
    private static boolean getM(String text, Model model) {

        if (text != null) {
            if (!text.isEmpty()) {
                try {
                    model.setMValue(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    model.setMValue(100);
                }
                return true;
            }
        }

        return false;
    }

    /* ______________________________________________________________________ */
    private static void getRestrictions(BufferedReader bufferedReader, Model model) {
        String text;

        try {
            while (true) {
                text = bufferedReader.readLine();
                if (text != null) {
                    if (!text.isEmpty()) {
                        model.addRestriction(text);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException ex) {
        }
    }

    /* ______________________________________________________________________ */
    private static Model readFileText() {

        Model model = new Model();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            if (getObjetiveFunction(bufferedReader.readLine(), model)) {
                getTypeFunction(bufferedReader.readLine(), model);
                getM(bufferedReader.readLine(), model);
                getRestrictions(bufferedReader, model);
                model.setName(file.getName());
            } else {
                model = null;
            }

        } catch (java.io.FileNotFoundException ex) {
            DialogPane.show(file.getName() + " don't exist", DialogPane.E);
            System.out.println(file.getName() + " don't exist");
            model = null;
        } catch (IOException ex) {
            System.out.println("" + ex);
            model = null;
        }

        return model;
    }

    /* ______________________________________________________________________ */
    public static String saveFile(Model model) {
        if (file != null) {
            if (file.canWrite()) {
                writeText(model);
                Utilities.output(file.getName() + " saved successfully");
            } else {
                DialogPane.show(file.getName() + " couldn't be write", DialogPane.E);
                Utilities.output(file.getName() + " couldn't be write");
            }

            return model.getName();
        } else {
            return saveFileAs(model);
        }
    }

    /* ______________________________________________________________________ */
    public static String saveFileAs(Model model) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/Files");
        fileChooser.showSaveDialog(null);
        file = fileChooser.getSelectedFile();

        if (file != null) {
            if (!file.exists()) {
                create();
                if (file.canWrite()) {
                    DialogPane.show(file.getName() + " saved Successfully", DialogPane.I);
                    Utilities.output(file.getName() + " saved Successfully");

                    writeText(model);
                } else {
                    DialogPane.show(file.getName() + " couldn't be write", DialogPane.E);
                    Utilities.output(file.getName() + " couldn't be write");
                }
            } else {
                DialogPane.show(file.getName() + " is already created", DialogPane.E);
                Utilities.output(file.getName() + " is already created");
            }
        }

        return model.getName();
    }

    /* ______________________________________________________________________ */
    private static void create() {
        try {
            if (file.createNewFile()) {
                Utilities.output(file.getName() + " created successfully");
            } else {
                DialogPane.show(file.getName() + " couldn't be created", DialogPane.E);
                Utilities.output(file.getName() + " couldn't be created");
            }
        } catch (IOException ex) {
            Utilities.output("" + ex);
        }
    }

    /* ______________________________________________________________________ */
    private static void writeText(Model model) {
        try (java.io.FileWriter fileWriter = new java.io.FileWriter(file)) {

            fileWriter.write(Function.parseString(model.getObjetiveFunction()) + "\n");
            fileWriter.write(model.getTypeModel() ? "MAXIMIZE\n" : "MINIMIZE\n");
            fileWriter.write(model.getMValue() + "\n");

            for (int i = 0; i < model.getRestrictionsCount(); i++) {
                fileWriter.write(Function.parseString(model.getRestrictionAt(i)) + "\n");
            }

            if (model.getSimplexTable() != null) {

                fileWriter.write("\nSOLUTION\n");
//
//            fileWriter.write("\nCALCULATE INITIAL SOLUTION\n");
//
//            fileWriter.write("\nCONVERT TO ECUATIONS\n");
//            for (int i = 0; i < model.getRestrictionsCount(); i++) {
//                fileWriter.write("CTE: " + model.getRestrictionAt(i) + "\t->\t" + model.getEcuationAt(i) + "\n");
//            }
//
//            fileWriter.write("\nBUILD SIMPLEX TABLE\n");
//            for (int i = 0; i < model.getRestrictionsCount() + 1; i++) {
//                for (int j = 0; j < model.getVariablesCount(); j++) {
//                    fileWriter.write(model.getSimplexTable()[i][j] + "\t");
//                }
//                fileWriter.write("\n");
//            }
//
//            fileWriter.write("\nFIND BASIC VARIABLES\n");
//            fileWriter.write("FBV: " + model.getBasicVariables() + "\n");
//
//            fileWriter.write("\nFIND NON-BASIC VARIABLES\n");
//            fileWriter.write("FNBV: " + model.getNonBasicVariables() + "\n");
//
//            if (model.getArtificialVariablesCount().size() > 1) {
//                fileWriter.write("\nPENALIZE ARTIFICIAL VARIABLES\n");
//                fileWriter.write("PAV: " + Arrays.toString(model.getSimplexTable()[0]) + "\n");
//
//                fileWriter.write("PAV: " + Arrays.toString(model.getSimplexTable()[0]) + "\n");
//
//                for (int i = 0; i < model.getRestrictionsCount() + 1; i++) {
//                    for (int j = 0; j < model.getVariablesCount(); j++) {
//                        fileWriter.write(model.getSimplexTable()[i][j] + "\t");
//                    }
//                    fileWriter.write("\n");
//                }
//            }
//
//            fileWriter.write("\n");
                // Get Solution Values

                ArrayList<String> basicVariables = model.getBasicVariables();
                ArrayList<String> nonBasicVariables = model.getNonBasicVariables();
                model.getVariables().forEach(i -> {
                    if (nonBasicVariables.indexOf(i) != -1) {
                        if (!i.isEmpty()) {
                            try {
                                fileWriter.write(i + " = 0\n");
                            } catch (IOException ex) {
                            }
                        }
                    } else if (!i.isEmpty()) {
                        for (int j = 0; j < basicVariables.size(); j++) {
                            if (basicVariables.get(j).equals(i)) {
                                try {
                                    fileWriter.write(i + " = " + model.getSimplexTable()[j][model.getVariablesCount() - 1] + "\n");
                                } catch (IOException ex) {
                                }
                                break;
                            }
                        }
                    }
                });

                fileWriter.close();
            }
        } catch (IOException ex) {
            DialogPane.show(file.getName() + " couldn't be writed", DialogPane.E);
            Utilities.output(file.getName() + " couldn't be writed");
        }

    }

}
