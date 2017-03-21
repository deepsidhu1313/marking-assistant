/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.GlobalValues;
import in.co.s13.marking.assistant.meta.SessionSettings;
import in.co.s13.marking.assistant.meta.Tools;
import static in.co.s13.marking.assistant.meta.Tools.write;
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
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import org.json.JSONObject;

/**
 *
 * @author NAVDEEP SINGH SIDHU <navdeepsingh.sidhu95@gmail.com>
 */
public class MainWindow extends Application implements Runnable {

    static CustomTree AllNodes = new CustomTree();
    public static Tab tab21, tab22, tab23, tab24;
    static Tab filesTab = new Tab("Files");
    static TabPane centerTabPane;
    public static Tab textTab[] = new Tab[100];
    public static ProgressIndicator pi = new ProgressBar();

    // public static TreeView tree = new TreeView();
    //public static CheckBoxTreeItem[] root = new CheckBoxTreeItem[1000];
    public static boolean ideStarted = false;
    public static int tabcounter = 0;
    public static BorderPane bp;
    public static HBox statusBar;

    Stage stage = new Stage();
    public static SplitPane LeftSplitPane;
    public static int selectedtab = 0;
    public static TextArea consoleArea = new TextArea();
    public static TabPane bottomTabPane;

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

        MenuItem newSessionItem = new MenuItem("\tNew Session\t\t");
        newSessionItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                doNewSession();
            }
        });
        menuFile.getItems().add(newSessionItem);

        MenuItem continueSessionItem = new MenuItem("\tResume Session\t\t");
        continueSessionItem.setOnAction(new EventHandler() {
            public void handle(Event t) {
                doContinueSession();
            }
        });
        menuFile.getItems().add(continueSessionItem);

        MenuItem sync = new MenuItem("\tSync\t\t");
        sync.setOnAction(new EventHandler() {
            public void handle(Event t) {

                synchroniseUi();
            }
        });
        menuFile.getItems().add(sync);

        MenuItem save = new MenuItem("\tSave\t\t");
        save.setOnAction(new EventHandler() {
            public void handle(Event t) {
                doSave();
            }
        });
        menuFile.getItems().add(save);

        MenuItem exit = new MenuItem("\tExit\t\t");
        exit.setOnAction(new EventHandler() {
            public void handle(Event t) {
                doExit(t);

            }
        });
        menuFile.getItems().add(exit);

        Menu menuRun = new Menu("Run");
        MenuItem itemCompileThis = new MenuItem("Compile This Folder");
        itemCompileThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                //   parse();
            }
        });
        MenuItem itemCompileAll = new MenuItem("Compile All");
        itemCompileAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // execute(selectedtab);
            }
        });
        MenuItem itemRunThis = new MenuItem("Run This");
        itemRunThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // execute(selectedtab);
            }
        });
        MenuItem itemRunAll = new MenuItem("Run All");
        itemRunAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // execute(selectedtab);
            }
        });
        MenuItem itemDiffThis = new MenuItem("Compare This");
        itemDiffThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // execute(selectedtab);
            }
        });
        MenuItem itemDiffAll = new MenuItem("Compare All");
        itemDiffAll.setOnAction(new EventHandler() {
            public void handle(Event t) {
                // execute(selectedtab);
            }
        });

        menuRun.getItems().addAll(itemCompileThis, itemCompileAll,
                itemRunThis, itemRunAll, itemDiffThis, itemDiffAll);

        menuBar.getMenus().addAll(menuFile, menuRun);
        //Setup Center and Right
        // TabPaneWrapper wrapper = new TabPaneWrapper(Orientation.HORIZONTAL, .9);
        centerTabPane = new TabPane();

        this.stage.setTitle("ReLinux-IDE");

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
                if (newTab.getText() == null) {
                    stage.setTitle("");

                } else {
                    stage.setTitle("Marking Assisstant - " + GlobalValues.selectedParentFolder + " - " + newTab.getText());
                    selectedtab = Integer.parseInt(newTab.getId());
                    synchroniseUi();

                    //  GlobalValues.selectedParentFolder = FilesTree.getProjectName(new File(newTab.getTooltip().getText()));
                    if (GlobalValues.selectedParentFolder != null && (!GlobalValues.lastSelectedParentFolder.trim().equalsIgnoreCase(GlobalValues.selectedParentFolder.trim()))) {
                        //   loadManifest(new File("workspace/" + GlobalValues.selectedParentFolder));
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
        Button compileButton = new Button("Compile");
        Button runButton = new Button("Run");
        Button diffButton = new Button("Compare");
        Button nextButton = new Button("Next");
        Region leftspace = new Region();
        leftspace.setMinWidth(50);
        //buttonBar.getChildren().add(leftspace);
        HBox.setHgrow(leftspace, Priority.ALWAYS);
        buttonBar.getChildren().addAll(prevButton, compileButton, runButton,
                diffButton, nextButton);
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
        bigTabPane.setDividerPositions(0.15f);
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

        ideStarted = true;
        this.stage.setOnCloseRequest(new EventHandler() {
            public void handle(Event t) {
                doExit(t);
            }
        });

        synchroniseUi();
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

    public void doNewSession() {

        synchroniseUi();
        final TextField sessionName = new TextField();
        TextField compileNo = new TextField();
        TextField runNo = new TextField();

        Stage newProjectDialog = new Stage();
        newProjectDialog.setTitle("New Session");
        BorderPane borderPane = new BorderPane();
        Label sessionExist = new Label("Session app/sessions/" + sessionName.getText() + ".json Already Exist!!");
        sessionExist.setTextFill(Paint.valueOf("Red"));
        sessionExist.setVisible(false);
        sessionName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (new File("app/sessions/" + newValue + ".obj").exists()) {
                sessionExist.setText("Folder Already Exist!!");
                sessionExist.setVisible(true);
            } else {
                sessionExist.setVisible(false);

            }
        });

        //ButtonBar buttonBar = new ButtonBar();
        Button okbutton = new Button("OK");
        Button cancelbutton = new Button("Cancel");
        //buttonBar.getButtons().addAll(okbutton, cancelbutton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column2);

        sessionName.setPromptText("Project Name");

        grid.add(new Label("Session Name: app/sessions/"), 0, 0);
        grid.add(sessionName, 1, 0);
        grid.add(sessionExist, 2, 0);
        grid.add(new Label("No of Question To Compile For:"), 0, 1);
        grid.add(compileNo, 1, 1);

        grid.add(new Label("No of Question To Run For:"), 0, 2);
        grid.add(runNo, 1, 2);
        grid.add(new Label("Feedback Template:"), 0, 3);
        TextField templatePath = new TextField();
        grid.add(templatePath, 1, 3);
        Button browseFeedbackButton = new Button("Browse");
        grid.add(browseFeedbackButton, 2, 3);
        browseFeedbackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser jfc = new FileChooser();
                jfc.setTitle("Choose Feedback template File");
                GlobalValues.templateFile = jfc.showOpenDialog(stage);
                templatePath.setText(GlobalValues.templateFile.getAbsolutePath());
            }
        });

        grid.add(new Label("Use Remote Machine (ssh):"), 0, 4);
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 5);
        Label passLabel = new Label("Password:");
        grid.add(passLabel, 0, 6);
        TextField usernameTF = new TextField();
        usernameTF.setPromptText("user");
        grid.add(usernameTF, 1, 5);
        TextField passwdTF = new PasswordField();
        grid.add(passwdTF, 1, 6);

        CheckBox enableRemoteCB = new CheckBox();
        usernameLabel.setDisable(true);
        usernameTF.setDisable(true);
        passLabel.setDisable(true);
        passwdTF.setDisable(true);

        enableRemoteCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean old_val, Boolean new_val) {
                usernameLabel.setDisable(!new_val);
                usernameTF.setDisable(!new_val);
                passLabel.setDisable(!new_val);
                passwdTF.setDisable(!new_val);
            }
        });
        grid.add(enableRemoteCB, 1, 4);

        grid.add(okbutton, 0, 7);
        grid.add(cancelbutton, 1, 7);
        GridPane.setHgrow(okbutton, Priority.ALWAYS);
        borderPane.setCenter(grid);

        okbutton.setOnAction((ActionEvent event) -> {
            boolean errorFree = true;
            String project = sessionName.getText().trim();

            if (project.length() < 1) {
                sessionExist.setText("Set A Session Name!!");
                errorFree = false;

            }
            newProjectDialog.hide();
            GlobalValues.compilerSettingsList.clear();
            GlobalValues.runSettingsList.clear();
            int runNum = Integer.parseInt(runNo.getText().trim());
            int comNum = Integer.parseInt(compileNo.getText().trim());
            for (int i = 1; i <= comNum; i++) {
                CompilerSetupWindow csw = new CompilerSetupWindow(i);
                try {
                    csw.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            for (int i = 1; i <= runNum; i++) {
                ExecuteSetupWindow csw = new ExecuteSetupWindow(i);
                try {
                    csw.start(new Stage());
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            GlobalValues.sessionSettings = new SessionSettings(sessionName.getText(),
                    "", "", comNum, runNum, GlobalValues.compilerSettingsList, GlobalValues.runSettingsList, templatePath.getText().trim());
            Tools.parseFeedbackTemplate();
            Tools.writeObject(("app/sessions/" + sessionName.getText() + ".obj"), GlobalValues.sessionSettings);
            newProjectDialog.close();
            event.consume();
        });
        cancelbutton.setOnAction((ActionEvent event) -> {
            newProjectDialog.close();
            event.consume();
        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));
        newProjectDialog.setScene(new Scene(borderPane));
        newProjectDialog.initOwner(stage);
        newProjectDialog.show();

    }

    public void doContinueSession() {
        synchroniseUi();
        FileChooser jfc = new FileChooser();
        jfc.setTitle("Select Session File");
        File choice = jfc.showOpenDialog(stage);
        GlobalValues.sessionSettings = (SessionSettings) Tools.readObject(choice.getAbsolutePath());
        try {
            GlobalValues.feedbackDBobject = new JSONObject(Tools.read((new File("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-db.fdb"))));
            GlobalValues.feedbackDBArray = GlobalValues.feedbackDBobject.getJSONArray("DB");
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void doExit(Event e) {
        e.consume();

        Dialog dialog = new Alert(Alert.AlertType.WARNING, "Do you want to exit ??", ButtonType.YES, ButtonType.NO);
        dialog.initOwner(stage);
        dialog.showAndWait()
                .filter(response -> response == ButtonType.YES).ifPresent(response -> {
            stage.close();
            System.exit(0);
        });

    }

    private boolean doSave() {
        File file2 = new File(textTab[selectedtab].getTooltip().getText());
        if (file2.exists()) {
            file2.delete();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(textTab[selectedtab].getTooltip().getText()); //new AppendFileStream
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
//        out.print(GlobalValues.ta[selectedtab].getText());
        out.close();
        return true;
        // return doSaveAs();
    }
//
//    public static void addTab(File selectedFile, int tabcounter) {
//        if (!GlobalValues.listOpenedFiles.isEmpty() && GlobalValues.listOpenedFiles.contains(selectedFile.getAbsolutePath())) {
//            ObservableList<Tab> templs = centerTabPane.getTabs();
//            System.out.println("" + templs);
//            for (int i = 0; i < templs.size(); i++) {
//                System.out.println("" + templs.get(i).getTooltip());
//                if ((templs.get(i).getTooltip() != null) && templs.get(i).getTooltip().getText().equalsIgnoreCase(selectedFile.getAbsolutePath())) {
//                    final int r = i;
//                    Platform.runLater(() -> {
//                        centerTabPane.getSelectionModel().select(r);
//                    });
//                }
//            }
//        } else {
//            textTab[tabcounter] = new Tab(selectedFile.getName());
//
//            textTab[tabcounter].setId("" + tabcounter);
//            textTab[tabcounter].setTooltip(new Tooltip(selectedFile.getAbsolutePath()));
//            textTab[tabcounter].setOnClosed((Event e) -> {
//             //   GlobalValues.listOpenedFiles.clear();
//                ObservableList<Tab> tl = centerTabPane.getTabs();
//                tl.stream().forEach((tl1) -> {
//             //       GlobalValues.listOpenedFiles.add(tl1.getTooltip().getText());
//                });
//            });
//            //GlobalValues.listOpenedFiles.add(selectedFile.getAbsolutePath().trim());
//           // GlobalValues.ta[tabcounter] = new SyntaxTextArea();
//            //TextArea ta= new TextArea();
//            //SwingNode sn = new SwingNode();
//            String line = null;
//            String text = "";
//            try {
//                String temp = read(selectedFile);
//                if (temp != null) {
//                    text += temp;
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            GlobalValues.ta[tabcounter].setText(text);
//
//            try (PrintStream out = new PrintStream(selectedFile.getAbsolutePath())) {
//                out.print(text);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            textTab[tabcounter].setContent(GlobalValues.ta[tabcounter].getNode());
//            textTab[tabcounter].getStyleClass().add(org.fxmisc.richtext.demo.JavaKeywordsAsync.class.getResource("java-keywords.css").toExternalForm());
//            centerTabPane.getTabs().add(textTab[tabcounter]);
//            IncrementTabcounter();
//        }
//    }

    public static void IncrementTabcounter() {
        tabcounter++;
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

    void executeScript(String ScriptLocation, String... args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> commands = new ArrayList<String>();
                    commands.add("/bin/bash");
                    commands.add(ScriptLocation);
                    commands.addAll(Arrays.asList(args));
                    ProcessBuilder pb = new ProcessBuilder(commands);
                    Process p = pb.start();
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    System.out.println("Here is the standard output of the command:\n");

                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        final String fout = "\n" + s;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                consoleArea.appendText(fout);
                            }
                        });
                    }

                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        final String fout = "\n" + s;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                consoleArea.appendText(fout);
                            }
                        });
                    }
                    stdError.close();
                    stdInput.close();
                    p.destroy();

                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    @Override
    public void run() {
        try {

        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
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