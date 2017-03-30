/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.CompilerSetting;
import in.co.s13.marking.assistant.meta.FeedBackEntry;
import in.co.s13.marking.assistant.meta.GlobalValues;
import static in.co.s13.marking.assistant.meta.GlobalValues.showFeedBackInSeprateWindow;
import in.co.s13.marking.assistant.meta.RunSetting;
import in.co.s13.marking.assistant.meta.SessionSettings;
import in.co.s13.marking.assistant.tools.ExecuteOverNetwork;
import in.co.s13.marking.assistant.tools.PrepareRemote;
import in.co.s13.marking.assistant.tools.Tools;
import static in.co.s13.marking.assistant.tools.Tools.write;
import static in.co.s13.marking.assistant.tools.Tools.writeObject;
import in.co.s13.marking.assistant.tools.TransferFilesToRemote;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import in.co.s13.marking.assistant.ui.CustomTree;
import in.co.s13.syntaxtextareafx.SyntaxTextAreaFX;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jdk.nashorn.internal.objects.Global;
import org.controlsfx.control.action.Action;
import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author NAVDEEP SINGH SIDHU <navdeepsingh.sidhu95@gmail.com>
 */
public class MainWindow extends Application {

    static CustomTree AllNodes = new CustomTree();
    public static SplitPane LeftSplitPane;
    public static TabPane bottomTabPane;
    public static BorderPane bp;
    static TabPane centerTabPane;
    //public static int selectedtab = 0;
    public static TextArea consoleArea = new TextArea();
    private static Stage feedBackStage = new Stage();
    static Tab filesTab = new Tab("Files");
    // public static TreeView tree = new TreeView();
    //public static CheckBoxTreeItem[] root = new CheckBoxTreeItem[1000];
    public static boolean ideStarted = false;
    public static ListView<String> logArea = new ListView<>();
    public static ProgressIndicator pi = new ProgressBar();
    public static Tab selectedTab;//= new Tab[100];
    public static HBox statusBar;
    public static Tab tab21;
    public static Tab tab22;
    public static Tab tab23;
    public static Tab tab24;
    public static AtomicInteger tabcounter = new AtomicInteger(0);

    public static synchronized void IncrementTabcounter() {
        tabcounter.incrementAndGet();
    }

    public static synchronized void addTab(File selectedFile, int tabcounter) {
        if (selectedFile.getName().equalsIgnoreCase("feedback.txt")) {
            return;
        }
        if (!GlobalValues.listOpenedFiles.isEmpty() && GlobalValues.listOpenedFiles.contains(selectedFile.getAbsolutePath())) {
            if (selectedFile.getName().equalsIgnoreCase("feedback") && showFeedBackInSeprateWindow) {
                feedBackStage.show();
                return;
            }
            ObservableList<Tab> templs = centerTabPane.getTabs();
//            System.out.println("" + templs);
            for (int i = 0; i < templs.size(); i++) {
//                System.out.println("" + templs.get(i).getTooltip());
                if ((templs.get(i).getTooltip() != null) && templs.get(i).getTooltip().getText().equalsIgnoreCase(selectedFile.getAbsolutePath())) {
                    final int r = i;
                    Platform.runLater(() -> {
                        centerTabPane.getSelectionModel().select(r);
                    });
                }
            }
        } else {
            Tab newTab = new Tab(selectedFile.getName());

            newTab.setId("" + tabcounter);
            newTab.setTooltip(new Tooltip(selectedFile.getAbsolutePath()));
            newTab.setOnClosed((Event e) -> {
                GlobalValues.listOpenedFiles.clear();
                ObservableList<Tab> tl = centerTabPane.getTabs();
                tl.stream().forEach((tl1) -> {
                    GlobalValues.listOpenedFiles.add(tl1.getTooltip().getText());
                });
            });
            GlobalValues.listOpenedFiles.add(selectedFile.getAbsolutePath().trim());
            if (selectedFile.getName().equalsIgnoreCase("feedback")) {
                FeedbackArea fba = new FeedbackArea(selectedFile.getAbsolutePath());

                if (showFeedBackInSeprateWindow) {
                    addToFeedBackWindow(fba);
                } else {
                    newTab.setContent(fba.getNode());
                    centerTabPane.getTabs().add(newTab);
                }

            } else {
                System.out.println("Adding Tab: " + selectedFile.getAbsolutePath());
                SyntaxTextAreaFX ta = new SyntaxTextAreaFX(selectedFile.getAbsolutePath());

                try (PrintStream out = new PrintStream(selectedFile.getAbsolutePath())) {
                    out.print(ta.getText());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

                newTab.setContent(ta.getNode());
                centerTabPane.getTabs().add(newTab);
            }

            IncrementTabcounter();
        }
//

    }

    private static void addToFeedBackWindow(FeedbackArea fba) {
        if (feedBackStage == null) {
            feedBackStage = new Stage();
        }
        feedBackStage.setTitle("Feedback of " + fba.getFeedBackFile().getParentFile().getName() + " - " + fba.getFeedBackFile().getAbsolutePath());
        feedBackStage.setScene(new Scene((fba)));
        feedBackStage.hide();
        if (!feedBackStage.isShowing()) {
            feedBackStage.show();
        }

        if (!feedBackStage.isFocused()) {
            feedBackStage.setFocused(true);
        }
        
    }

    public static void appendLogToLogArea(String Text) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                logArea.getItems().addAll(Text.split("\n"));
//            }
//        });

    }

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

        MainWindow.launch(args);
    }

    ExecutorService executorService = Executors.newFixedThreadPool(8);
    Stage st = new Stage();
    Stage stage = new Stage();

    void closeAllTabs() {
        centerTabPane.getTabs().clear();
        GlobalValues.listOpenedFiles.clear();
    }

    public void doCompareAll() {
        showLogArea();
        File assFile = new File("ASSIGNMENTS");
        File files[] = assFile.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            doCompareThis(file);
        }

    }

    public void doCompareThis(File studentDir) {
        showLogArea();
        ArrayList<RunSetting> runSettings = GlobalValues.sessionSettings.getRunSettings();
        for (int i = 0; i < runSettings.size(); i++) {

            RunSetting get = runSettings.get(i);
            int id = get.getId();
            if (get.getSampleOutPutFile() != null) {
                ArrayList<File> complierReqFiles = get.getRunReqFiles();
                String scriptName = "SCRIPTS/"
                        + GlobalValues.sessionSettings.getSession_name()
                        + "-diff-"
                        + id;
                if (get.getOS().trim().equalsIgnoreCase("linux")) {
                    scriptName += ".sh";

                    write(new File(scriptName),
                            "#!/bin/bash\ncd \"${1}\"\n diff -u -B -b \"${2}\" \"${3}\"");
                } else {

                }

                File file = studentDir;

                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    Tools.runFiles(executorService, id, "diff", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath(), get.getSampleOutPutFile().getAbsolutePath(), "run-out-" + id + ".log");
                }

            }
        }

    }

    public void doCompileAll() {
        showLogArea();
        File assFile = new File("ASSIGNMENTS");
        File files[] = assFile.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            if (file.isDirectory()) {
                doCompileThis(file);
            }
        }

    }

    public void doCompileThis(File studentDir) {
        showLogArea();
        ArrayList<CompilerSetting> compilerSettings = GlobalValues.sessionSettings.getCompilerSettings();
        for (int i = 0; i < compilerSettings.size(); i++) {
            CompilerSetting get = compilerSettings.get(i);
            int id = get.getId();

            ArrayList<File> complierReqFiles = get.getCompilerReqFiles();
            String scriptName = "SCRIPTS/"
                    + GlobalValues.sessionSettings.getSession_name()
                    + "-compile-"
                    + id;
            if (get.getLocation().equalsIgnoreCase("local")) {
                if (get.getOS().trim().equalsIgnoreCase("linux")) {
                    scriptName += ".sh";
                    write(new File(scriptName),
                            "#!/bin/bash\ncd \"${1}\"\n" + get.getCompilerCommand());
                } else {

                }
                File file = studentDir;
                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    Tools.runFiles(executorService, id, "compile", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
                }
            } else {
                //PrepareRemote pepRem = new PrepareRemote();
                File file = studentDir;

                if (get.getOS().trim().equalsIgnoreCase("linux")) {
                    scriptName += ".sh";
                    write(new File(scriptName),
                            "#!/bin/bash\ncd \"${1}\"\n" + get.getCompilerCommand());
                } else {

                }

                if (file.isDirectory()) {

//                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
//                    TransferFilesToRemote tftr = new TransferFilesToRemote(file,executorService);
//                    tftr.transfer();
//                    TransferFilesToRemote tftr2 = new TransferFilesToRemote(new File(scriptName),executorService);
//                    tftr2.transfer();
                    ExecuteOverNetwork execOvrNet2 = new ExecuteOverNetwork(executorService, i, "compile-net", file.getAbsolutePath(),
                            "/bin/bash MarkingAssistant/" + scriptName + " MarkingAssistant/" + file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(System.getProperty("user.dir")) + System.getProperty("user.dir").length() + 1));
                    execOvrNet2.execute();
                }

            }
        }

    }

    public boolean doContinueSession() {
        synchroniseUi();
        FileChooser jfc = new FileChooser();
        jfc.setTitle("Select Session File");
        jfc.setInitialDirectory(new File("./app/sessions/"));
        File choice = jfc.showOpenDialog(new Stage());
        if (choice == null) {
            return false;
        }
        GlobalValues.sessionSettings = (SessionSettings) Tools.readObject(choice.getAbsolutePath());
        try {
            GlobalValues.feedbackDBArray = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-db.fdb"));
            GlobalValues.templateFeedback = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-template.obj"));
            GlobalValues.defaultTemplateFeedback = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-default-template.obj"));
        } catch (Exception e) {
            Tools.parseFeedbackTemplate();
        }
//        GlobalValues.sessionSettings.getRunSettings().get(2).setLocation("local");
//        System.out.println(""+GlobalValues.sessionSettings.getRunSettings().get(2));
//        Tools.writeObject(choice.getAbsolutePath(), GlobalValues.sessionSettings);

        System.out.println("Last Session" + GlobalValues.sessionSettings);
        if (GlobalValues.sessionSettings.getCurrentStudent().getName().length() < 1) {
            if (GlobalValues.sessionSettings.getLastStudentMarked().getName().length() < 1) {
                startFromFirstStudent();
                return true;
            } else {
                GlobalValues.sessionSettings.setCurrentStudent(GlobalValues.sessionSettings.getLastStudentMarked());

            }

        }

        if (GlobalValues.sessionSettings.getLastStudentMarked().listFiles() == null) {
            startFromFirstStudent();
            return true;
        }

        //Tools.parseFeedbackTemplate();
        openFilesForStudent(GlobalValues.sessionSettings.getCurrentStudent());
        return true;
    }

    private void doExit(Event e) {
        e.consume();
        Tools.saveSettings();
        Dialog dialog = new Alert(Alert.AlertType.WARNING, "Do you want to exit ??", ButtonType.YES, ButtonType.NO);
        dialog.initOwner(stage);
        dialog.showAndWait()
                .filter(response -> response == ButtonType.YES).ifPresent(response -> {
            stage.close();
            System.exit(0);
            executorService.shutdown();
            try {
                executorService.awaitTermination(120, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public void doNewSession() {

        synchroniseUi();
        NewSessionWindow nsw = new NewSessionWindow();
        Platform.runLater(() -> {
            try {
                nsw.start(new Stage());

                if (nsw.isSelection()) {
                    startFromFirstStudent();

                }
            } catch (Exception ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //   return nsw.isSelection();
    }

    void doNextStudent() {
        doSaveAll();
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            // System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().getName().equalsIgnoreCase(get.getName()) && i < students.size() - 1) {
                GlobalValues.sessionSettings.setLastStudentMarked(get);
                FilesTree.collapseFolder(get);
                openFilesForStudent(students.get(i + 1));

                break;
            }
        }
    }

    void doPreviousStudent() {
        doSaveAll();
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            //System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().getName().equalsIgnoreCase(get.getName()) && i > 0) {
                GlobalValues.sessionSettings.setLastStudentMarked(get);
                FilesTree.collapseFolder(get);

                openFilesForStudent(students.get(i - 1));

                break;
            }
        }
    }

    public void doRunAll() {
        showLogArea();
        File assFile = new File("ASSIGNMENTS");
        File files[] = assFile.listFiles();
        for (int j = 0; j < files.length; j++) {
            File file = files[j];
            if (file.isDirectory()) {
                doRunThis(file);
            }
        }

    }

    public void doRunThis(File studentDir) {
        showLogArea();
        ArrayList<RunSetting> runSettings = GlobalValues.sessionSettings.getRunSettings();
        for (int i = 0; i < runSettings.size(); i++) {
            RunSetting get = runSettings.get(i);
            int id = get.getId();

            ArrayList<File> complierReqFiles = get.getRunReqFiles();
            String scriptName = "SCRIPTS/"
                    + GlobalValues.sessionSettings.getSession_name()
                    + "-run-"
                    + id;
            if (get.getOS().trim().equalsIgnoreCase("linux")) {
                scriptName += ".sh";
                String redirInp = "";
                if (get.getInputSequence().length() > 0) {
                    redirInp = " < input-" + id;

                }
                write(new File(scriptName),
                        "#!/bin/bash\ncd \"${1}\"\n" + get.getRunCommand() + redirInp);
            } else {

            }

            if (get.getLocation().trim().toLowerCase().equalsIgnoreCase("local".trim())) {
                File file = studentDir;

                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    if (get.getInputSequence().length() > 0) {
                        Tools.write(new File(file.getAbsolutePath() + "/input-" + id), get.getInputSequence());
                    }
                    System.out.println("Ran locally " + id);
                    Tools.runFiles(executorService, id, "run", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
                }
            } else {
//                PrepareRemote pepRem = new PrepareRemote();
                File file = studentDir;
                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
//                    new TransferFilesToRemote(file,executorService);
//                    new TransferFilesToRemote(new File(scriptName),executorService);
                    ExecuteOverNetwork execOvrNet = new ExecuteOverNetwork(executorService, id, "run", file.getAbsolutePath(), "/bin/bash MarkingAssistant/" + scriptName + " MarkingAssistant/" + file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(System.getProperty("user.dir")) + System.getProperty("user.dir").length() + 1));
                    execOvrNet.execute();
                }

            }
            System.out.println(get.getLocation().trim() + " Ran locally ::: " + id + " equals " + get.getLocation().trim().toLowerCase().equalsIgnoreCase("local".trim()));
        }

    }

    private boolean doSave() {
        Tools.writeObject(("app/sessions/" + GlobalValues.sessionSettings.getSession_name() + ".obj"), GlobalValues.sessionSettings);
        File file2 = new File(selectedTab.getTooltip().getText());
        if (file2.exists()) {
            file2.delete();
        }
        Node contentNode = selectedTab.getContent();
        if (contentNode instanceof SyntaxTextAreaFX) {
            ((SyntaxTextAreaFX) contentNode).save();
        } else if (contentNode instanceof FeedbackArea) {
            ((FeedbackArea) contentNode).save();

        }

        if (showFeedBackInSeprateWindow) {
            Parent root = feedBackStage.getScene().getRoot();
            if (root instanceof FeedbackArea) {
                ((FeedbackArea) root).save();

            }
        }
        Tools.writeObject("app/sessions/" + GlobalValues.sessionSettings.getSession_name() + ".obj", GlobalValues.sessionSettings);

//        PrintStream out = null;
//        try {
//            out = new PrintStream(selectedTab[selectedtab].getTooltip().getText()); //new AppendFileStream
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        out.print(GlobalValues.ta[selectedtab].getText());
//        out.close();
        return true;
// return doSaveAs();
    }

    public void doSaveAll() {
        List<Tab> tabs = centerTabPane.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            Tab get = tabs.get(i);
            Node contentNode = get.getContent();
            if (contentNode instanceof SyntaxTextAreaFX) {
                ((SyntaxTextAreaFX) contentNode).save();
            } else if (contentNode instanceof FeedbackArea) {
                ((FeedbackArea) contentNode).save();

            }
        }

        if (showFeedBackInSeprateWindow) {
            Parent root = feedBackStage.getScene().getRoot();
            if (root instanceof FeedbackArea) {
                ((FeedbackArea) root).save();

            }
        }
        Tools.writeObject("app/sessions/" + GlobalValues.sessionSettings.getSession_name() + ".obj", GlobalValues.sessionSettings);

    }

    void firstDiag() {
        Stage s = new Stage();
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        Button newSessButton = new Button("New Session");
        newSessButton.setOnAction((ActionEvent event) -> {
            doNewSession();
            s.close();

        });
        Button conSessButton = new Button("Resume Session");
        conSessButton.setOnAction((ActionEvent event) -> {
            if (doContinueSession()) {
                s.close();
            }
        });
        hBox.getChildren().addAll(newSessButton, conSessButton);
        s.setTitle("Choose");
        s.setScene(new Scene(hBox));
        s.showAndWait();
    }

    public void generateCSVReport() {
        ArrayList<File> dirs = Tools.getDirsInAssignmentDir();
        StringBuilder sb = new StringBuilder("Name,Total Marks, Bonus\n");
        new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "/").mkdirs();
        for (int i = 0; i < dirs.size(); i++) {
            File get = dirs.get(i);
            sb.append(get.getName()).append(",");
            File[] files = get.listFiles();
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                if (!file.isDirectory() && file.getName().endsWith("feedback")) {
                    StringBuilder feedbackContent = new StringBuilder();
                    ArrayList<FeedBackEntry> content = (ArrayList<FeedBackEntry>) Tools.readObject(file.getAbsolutePath());
                    for (int k = 0; k < content.size(); k++) {
                        FeedBackEntry line = content.get(k);
                        if (line.getFeedBack().trim().equalsIgnoreCase("ASSIGNMENT TOTAL")) {
                            System.out.println("" + line.toString());
                            sb.append(line.getObtainedMarks()).append(", ");
                            feedbackContent.append(line.toString()).append("\n");
                        } else if (line.getFeedBack().trim().equalsIgnoreCase("Total WIth Bonus")) {

                        }else if (line.getFeedBack().trim().equalsIgnoreCase("Section End")) {

                        } else if (line.getFeedBack().trim().equalsIgnoreCase("Bonus")) {

                            {
                                sb.append("X, ");
                            }
                        } else {
                            feedbackContent.append(line.toString()).append("\n");

                        }
                    }
                    
                    Tools.write(new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "/"+get.getName()+"-feedback.txt"), feedbackContent.toString());
                }

            }
            sb.append("\n");
        }

        Tools.write(new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "/score-list.csv"), sb.toString());
    }

    String getFirstStudent() {
        File files[] = new File("ASSIGNMENTS").listFiles();
        if (files.length > 0) {
            return files[0].getName();
        }

        return "";
    }

    public void moveFeedbacks() {
        ArrayList<File> dirs = Tools.getDirsInAssignmentDir();
        new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "/").mkdirs();
        for (int i = 0; i < dirs.size(); i++) {
            File get = dirs.get(i);
            File[] files = get.listFiles();
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                if (!file.isDirectory() && file.getName().equalsIgnoreCase("feedback.txt")) {

                    Tools.copyFolder(file, new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "/" + get.getName() + "-feedback.txt"));
                }
            }
        }
    }

    void openFilesForStudent(File get) {
        File files[] = get.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory()) {
                if (!file.getName().endsWith(".class")) {
                    try {
                        if (Tools.read(file).length() > 1) {
                            addTab(file, tabcounter.get());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        if (!Arrays.asList(files).contains(new File(get.getAbsolutePath() + "/feedback"))) {
            addTab(new File(get.getAbsolutePath() + "/feedback"), tabcounter.get());
        }
        GlobalValues.sessionSettings.setCurrentStudent(get);
        FilesTree.expandFolder(get);
        synchroniseUi();
        FilesTree.scrollTo(get);
    }

    void refresh() {
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            // System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().equals(get)) {
                openFilesForStudent(get);

                break;
            }
        }
    }

    public void showLogArea() {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                st.setTitle("Log");
//                st.setScene(new Scene(new BorderPane(logArea)));
//                st.show();
//            }
//        });

    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setTitle("Marking Assistant");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

//VBox vb1= new VBox();
//GridPane vb1 = new GridPane();
        MenuBar menuBar = new MenuBar();

// --- Menu File
        Menu menuFile = new Menu("File");

        MenuItem newSessionItem = new MenuItem("\tNew Session");
        newSessionItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doNewSession();
            }
        });
        newSessionItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuFile.getItems().add(newSessionItem);

        MenuItem continueSessionItem = new MenuItem("\tResume Session");
        continueSessionItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doContinueSession();
            }
        });
        continueSessionItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuFile.getItems().add(continueSessionItem);

        MenuItem sync = new MenuItem("\tSync");
        sync.setOnAction(new EventHandler() {
            public void handle(Event t) {

                synchroniseUi();
            }
        });
        menuFile.getItems().add(sync);

        MenuItem save = new MenuItem("\tSave");
        save.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doSave();
            }
        });
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuFile.getItems().add(save);

        MenuItem saveAll = new MenuItem("\tSave All");
        saveAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doSaveAll();
            }
        });
        saveAll.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));
        menuFile.getItems().add(saveAll);

        MenuItem reloadAll = new MenuItem("\tReload All");
        reloadAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                refresh();
            }
        });
        reloadAll.setAccelerator(new KeyCodeCombination(KeyCode.F5, KeyCombination.CONTROL_DOWN));
        menuFile.getItems().add(reloadAll);

        MenuItem exit = new MenuItem("\tExit");
        exit.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doExit(t);

            }
        });
        menuFile.getItems().add(exit);

        Menu menuView = new Menu("View");
        CheckMenuItem showFeedBackInSeprateWindowMenuItem = new CheckMenuItem("Show Feedback In Seprate Window");
        showFeedBackInSeprateWindowMenuItem.setSelected(showFeedBackInSeprateWindow);
        showFeedBackInSeprateWindowMenuItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                showFeedBackInSeprateWindow = newValue;
                toggleFeedbackArea(newValue);

            }
        });
        menuView.getItems().add(showFeedBackInSeprateWindowMenuItem);
        Menu menuFix = new Menu("Fix");
        MenuItem fixName = new MenuItem("Fix Dir Names");
        fixName.setOnAction((ActionEvent event) -> {
            synchroniseUi();
            try {
                FixNameWindow fnw = new FixNameWindow();
                fnw.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        menuFix.getItems().add(fixName);

        MenuItem renameItem = new MenuItem("Fix File Names");
        renameItem.setOnAction((ActionEvent event) -> {
            synchroniseUi();
            try {
                RenameFilesWindow fnw = new RenameFilesWindow();
                fnw.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        menuFix.getItems().add(renameItem);
        Menu menuRun = new Menu("Run");
        MenuItem itemCompileThis = new MenuItem("Compile This Folder");
        itemCompileThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doCompileThis(GlobalValues.sessionSettings.getCurrentStudent());
            }
        });
        MenuItem itemCompileAll = new MenuItem("Compile All");
        itemCompileAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doCompileAll();
            }
        });
        MenuItem itemRunThis = new MenuItem("Run This");
        itemRunThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doRunThis(GlobalValues.sessionSettings.getCurrentStudent());
            }
        });
        MenuItem itemRunAll = new MenuItem("Run All");
        itemRunAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doRunAll();
            }
        });
        MenuItem itemDiffThis = new MenuItem("Compare This");
        itemDiffThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doCompareThis(GlobalValues.sessionSettings.getCurrentStudent());
            }
        });
        MenuItem itemDiffAll = new MenuItem("Compare All");
        itemDiffAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doCompareAll();
            }
        });

        menuRun.getItems().addAll(itemCompileThis, itemCompileAll,
                itemRunThis, itemRunAll, itemDiffThis, itemDiffAll);

        Menu menuReport = new Menu("Report");
        MenuItem itemMoveFeedbacks = new MenuItem("Move Feedback");
        itemMoveFeedbacks.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveFeedbacks();
            }
        });

        MenuItem itemGenerateScoreList = new MenuItem("Generate Score List");
        itemGenerateScoreList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateCSVReport();
            }
        });

        menuReport.getItems().addAll(itemMoveFeedbacks, itemGenerateScoreList);
        menuBar.getMenus().addAll(menuFile, menuView, menuFix, menuRun, menuReport);
//Setup Center and Right
// TabPaneWrapper wrapper = new TabPaneWrapper(Orientation.HORIZONTAL, .9);
        centerTabPane = new TabPane();

        this.stage.setTitle("Marking Assistant");

//wrapper.addNodes(centerTabPane);
//Add bottom
        SplitPane centerSplitPane = new SplitPane();
        centerSplitPane.setDividerPositions(0.7f);
        centerSplitPane.getItems().addAll(centerTabPane);
        centerSplitPane.setOrientation(Orientation.VERTICAL);
//Add left
        LeftSplitPane = new SplitPane();
//VBox LeftVbox= new VBox(10);
        TabPane leftTabPane = new TabPane();

        filesTab.setClosable(false);
        filesTab.setContent(new TextArea());

        leftTabPane.getTabs().addAll(filesTab);
        TabPaneWrapper wrapperleft = new TabPaneWrapper(Orientation.HORIZONTAL, .1);

        LeftSplitPane.getItems().add(leftTabPane);
// LeftSplitPane.getItems().add(tree);

        centerTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> tab, Tab oldTab, final Tab newTab) {
                if (newTab != null) {
                    selectedTab = newTab;
                    if (newTab.getText() == null) {
                        stage.setTitle("");

                    } else {
                        stage.setTitle("Marking Assisstant - " + GlobalValues.sessionSettings.getCurrentStudent() + " - " + newTab.getText());
                        //selectedtab = Integer.parseInt(newTab.getId());
                        synchroniseUi();

                        //  GlobalValues.selectedParentFolder = FilesTree.getProjectName(new File(newTab.getTooltip().getText()));
                        if (GlobalValues.selectedParentFolder != null && (!GlobalValues.lastSelectedParentFolder.trim().equalsIgnoreCase(GlobalValues.selectedParentFolder.trim()))) {
                            //   loadManifest(new File("workspace/" + GlobalValues.selectedParentFolder));
                        }

                    }
                }
            }
        });
//        FeedbackArea fba = new FeedbackArea();
//        Tab demo = new Tab("Demo");
//        demo.setId("1");
//        demo.setContent(fba.getNode());
//        centerTabPane.getTabs().add(demo);
        LeftSplitPane.setOrientation(Orientation.VERTICAL);
        wrapperleft.addNodes(LeftSplitPane);
        LeftSplitPane.prefHeight(100);
        LeftSplitPane.prefWidth(100);
//wrapperleft.addNodes(LeftSplitPane, wrapperBottom.getNode());

// wrapperleft.getNode()
        bp = new BorderPane();
        VBox topBox = new VBox(10);
        HBox buttonBar = new HBox(10);
        Button prevButton = new Button("Previous");
        prevButton.setOnAction((ActionEvent event) -> {
            doPreviousStudent();
        });
        Button compileButton = new Button("Compile");
        compileButton.setOnAction((ActionEvent event) -> {
            doCompileThis(GlobalValues.sessionSettings.getCurrentStudent());
        });
        Button runButton = new Button("Run");
        runButton.setOnAction((ActionEvent event) -> {
            doRunThis(GlobalValues.sessionSettings.getCurrentStudent());
        });
        Button diffButton = new Button("Compare");
        diffButton.setOnAction((ActionEvent event) -> {
            doCompareThis(GlobalValues.sessionSettings.getCurrentStudent());
        });
        Button reloadButton = new Button("Reload");
        reloadButton.setOnAction((ActionEvent event) -> {
            refresh();
        });
        Button nextButton = new Button("Next");
        nextButton.setOnAction((ActionEvent event) -> {
            doNextStudent();
        });
        Region leftspace = new Region();
        leftspace.setMinWidth(50);
//buttonBar.getChildren().add(leftspace);
        HBox.setHgrow(leftspace, Priority.ALWAYS);
        buttonBar.getChildren().addAll(prevButton, compileButton, runButton,
                diffButton, reloadButton, nextButton);
        Region rightspace = new Region();
        rightspace.setMinWidth(50);

//buttonBar.getChildren().add(rightspace);
        buttonBar.setPadding(new Insets(0, 10, 10, 10));
        buttonBar.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(menuBar, buttonBar);
        bp.setTop(topBox);
//bp.setCenter(centerTabPane);
//bp.setBottom(wrapperBottom.getNode());
//bp.setLeft(wrapperleft.getNode());
        pi.maxHeight(10);
        statusBar = new HBox();
        statusBar.setSpacing(5);
        statusBar.maxHeight(10);

        statusBar.setAlignment(Pos.CENTER_RIGHT);
        statusBar.getChildren().add(pi);
// bp.setBottom(statusBar);
        SplitPane t1 = new SplitPane();
        t1.prefHeight(0);
        t1.maxWidth(0);

//  TabPaneWrapper wrapperRight = new TabPaneWrapper(Orientation.VERTICAL, .7);
//  SplitPane rsplitpane = new SplitPane();
// rsplitpane.setOrientation(Orientation.VERTICAL);
// rsplitpane.setDividerPositions(0.6);
// wrapperRight.addNodes(rsplitpane);
        SplitPane bigTabPane = new SplitPane();

        bigTabPane.getItems().add(LeftSplitPane);
// bigTabPane.getItems().add(centerTabPane);
        bigTabPane.getItems().add(centerSplitPane);
// bigTabPane.getItems().add(rsplitpane);
        bigTabPane.setDividerPositions(0.13f);
        bp.setCenter(bigTabPane);
//bp.setRight(wrapperRight.getNode());
        Scene myScene = new Scene(bp);
        this.stage.setScene(myScene);
        this.stage.sizeToScene();
        this.stage.setWidth(primaryScreenBounds.getWidth() - 100);
        this.stage.setHeight(primaryScreenBounds.getHeight() - 100);
        this.stage.setX(100);
        this.stage.setY(100);
        this.stage.show();
        firstDiag();
        ideStarted = true;
        this.stage.setOnCloseRequest(new EventHandler() {
            public void handle(Event t) {
                doExit(t);
            }
        });

        synchroniseUi();
    }

    void startFromFirstStudent() {
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        if (students.size() > 0) {
            openFilesForStudent(students.get(0));
        }
    }

    public void synchroniseUi() {
        Thread t = new Thread(new FilesTree(this));
        t.start();

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                filesTab.setContent(FilesTree.tv);
                // filesTab.setContent(FilesTree.tv);
                //           settings.outPrintln("" + Scanner.NetScanner.livehosts);

            }
        });

    }

    void toggleFeedbackArea(boolean b) {
        if (feedBackStage.isShowing() && !b) {
            Parent root = feedBackStage.getScene().getRoot();

            if (root instanceof FeedbackArea) {
                GlobalValues.listOpenedFiles.clear();
                ObservableList<Tab> tl = centerTabPane.getTabs();
                tl.stream().forEach((tl1) -> {
                    GlobalValues.listOpenedFiles.add(tl1.getTooltip().getText());
                });
                addTab(((FeedbackArea) root).getFeedBackFile(), 1);

            }

            feedBackStage.close();

        } else if (b && feedBackStage != null && !feedBackStage.isShowing()) {
            {
                ObservableList<Tab> templs = centerTabPane.getTabs();
                for (int i = 0; i < templs.size(); i++) {
                    if ((templs.get(i).getTooltip() != null) && templs.get(i).getTooltip().getText().equalsIgnoreCase(new File(GlobalValues.sessionSettings.getCurrentStudent() + "/feedback").getAbsolutePath())) {
                        Tab get = templs.get(i);
                        Node contentNode = get.getContent();
                        centerTabPane.getTabs().remove(i);
                        GlobalValues.listOpenedFiles.clear();
                        ObservableList<Tab> tl = centerTabPane.getTabs();
                        tl.stream().forEach((tl1) -> {
                            GlobalValues.listOpenedFiles.add(tl1.getTooltip().getText());
                        });

                        if (contentNode instanceof FeedbackArea) {
                            addTab(((FeedbackArea) contentNode).getFeedBackFile(), 1);
                            //  addToFeedBackWindow((FeedbackArea) contentNode);

                        }

                        break;
                    }
                }

            }

//            if (!feedBackStage.isShowing()) {
//                feedBackStage.show();
//            }
//
//            if (!feedBackStage.isFocused()) {
//                feedBackStage.setFocused(true);
//            }
        }

    }

    public static class TabPaneWrapper {

        SplitPane split;

        public TabPaneWrapper(Orientation o, double splitLocation) {
            split = new SplitPane();

            //Change the CSS (uncomment if using an external css)
            //split.getStylesheets().add("test.css");
            split.setOrientation(o);
            split.setDividerPosition(0, splitLocation);
        }

        public void addNodes(final Node node1, final Node node2) {
            //Add to the split pane
            split.getItems().addAll(node1, node2);
        }

        public void addNodes(final Node node1) {
            //Add to the split pane
            split.getItems().add(node1);
        }

        public Parent getNode() {
            // split.setResizableWithParent(split, Boolean.TRUE);
            return split;
        }

    }

}
