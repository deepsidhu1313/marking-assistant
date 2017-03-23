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
import in.co.s13.marking.assistant.meta.Tools;
import static in.co.s13.marking.assistant.meta.Tools.write;
import static in.co.s13.marking.assistant.meta.Tools.writeObject;
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
public class MainWindow extends Application implements Runnable {

    static CustomTree AllNodes = new CustomTree();
    public static Tab tab21, tab22, tab23, tab24;
    static Tab filesTab = new Tab("Files");
    static TabPane centerTabPane;
    public static Tab selectedTab;//= new Tab[100];
    public static ProgressIndicator pi = new ProgressBar();

    // public static TreeView tree = new TreeView();
    //public static CheckBoxTreeItem[] root = new CheckBoxTreeItem[1000];
    public static boolean ideStarted = false;
    public static AtomicInteger tabcounter = new AtomicInteger(0);
    public static BorderPane bp;
    public static HBox statusBar;

    Stage stage = new Stage();
    private static Stage feedBackStage = new Stage();

    public static SplitPane LeftSplitPane;
    //public static int selectedtab = 0;
    public static TextArea consoleArea = new TextArea();
    public static TabPane bottomTabPane;
    public static TextArea logArea = new TextArea();

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
            }
        });
        menuView.getItems().add(showFeedBackInSeprateWindowMenuItem);
        Menu menuFix = new Menu("Fix");
        MenuItem fixName = new MenuItem("Fix Names");
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
        Menu menuRun = new Menu("Run");
        MenuItem itemCompileThis = new MenuItem("Compile This Folder");
        itemCompileThis.setOnAction(new EventHandler() {
            public void handle(Event t) {
                synchroniseUi();
                doCompileThis();
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
                doRunThis();
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
                doCompareThis();
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

        menuBar.getMenus().addAll(menuFile, menuView, menuFix, menuRun);
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
            doCompileThis();
        });
        Button runButton = new Button("Run");
        runButton.setOnAction((ActionEvent event) -> {
            doRunThis();
        });
        Button diffButton = new Button("Compare");
        diffButton.setOnAction((ActionEvent event) -> {
            doCompareThis();
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
        firstDiag();
        ideStarted = true;
        this.stage.setOnCloseRequest(new EventHandler() {
            public void handle(Event t) {
                doExit(t);
            }
        });

        synchroniseUi();
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
            doContinueSession();
            s.close();
        });
        hBox.getChildren().addAll(newSessButton, conSessButton);
        s.setTitle("Choose");
        s.setScene(new Scene(hBox));
        s.showAndWait();
    }

    private static void addToFeedBackWindow(FeedbackArea fba) {
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

    void doPreviousStudent() {
        doSaveAll();
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().equalsIgnoreCase(get.getName()) && i > 0) {
                GlobalValues.sessionSettings.setLastStudentMarked(get.getName());

                openFilesForStudent(students.get(i - 1));

                break;
            }
        }
    }

    void doNextStudent() {
        doSaveAll();
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().equalsIgnoreCase(get.getName()) && i < students.size() - 1) {
                GlobalValues.sessionSettings.setLastStudentMarked(get.getName());
                openFilesForStudent(students.get(i + 1));

                break;
            }
        }
    }

    void refresh() {
        closeAllTabs();
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        for (int i = 0; i < students.size(); i++) {
            File get = students.get(i);
            System.out.println("Comparing:" + GlobalValues.sessionSettings.getCurrentStudent() + " with " + get.getName());
            if (GlobalValues.sessionSettings.getCurrentStudent().equalsIgnoreCase(get.getName())) {
                openFilesForStudent(get);

                break;
            }
        }
    }

    void closeAllTabs() {
        centerTabPane.getTabs().clear();
        GlobalValues.listOpenedFiles.clear();
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
        GlobalValues.sessionSettings.setCurrentStudent(get.getName());

    }

    void startFromFirstStudent() {
        ArrayList<File> students = Tools.getDirsInAssignmentDir();
        if (students.size() > 0) {
            openFilesForStudent(students.get(0));
        }
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
                    "", "", comNum, runNum, GlobalValues.compilerSettingsList, GlobalValues.runSettingsList,
                    templatePath.getText().trim(), usernameTF.getText().trim(),
                    passwdTF.getText().trim());
            Tools.parseFeedbackTemplate();
            Tools.writeObject(("app/sessions/" + sessionName.getText() + ".obj"), GlobalValues.sessionSettings);

            newProjectDialog.close();
            startFromFirstStudent();
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
        jfc.setInitialDirectory(new File("./app/sessions/"));
        File choice = jfc.showOpenDialog(new Stage());
        if (choice == null) {
            return;
        }
        GlobalValues.sessionSettings = (SessionSettings) Tools.readObject(choice.getAbsolutePath());
        try {
            GlobalValues.feedbackDBArray = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-db.fdb"));
            GlobalValues.templateFeedback = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-template.obj"));
            GlobalValues.defaultTemplateFeedback = (ArrayList<FeedBackEntry>) Tools.readObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-default-template.obj"));
        } catch (Exception e) {
            Tools.parseFeedbackTemplate();
        }
        System.out.println("Last Session" + GlobalValues.sessionSettings);
        if (GlobalValues.sessionSettings.getCurrentStudent().length() < 1) {
            if (GlobalValues.sessionSettings.getLastStudentMarked().length() < 1) {
                startFromFirstStudent();
                return;
            } else {
                GlobalValues.sessionSettings.setCurrentStudent(GlobalValues.sessionSettings.getLastStudentMarked());

            }

        }
        //Tools.parseFeedbackTemplate();
        openFilesForStudent(new File("ASSIGNMENTS/" + GlobalValues.sessionSettings.getCurrentStudent()));

    }

    String getFirstStudent() {
        File files[] = new File("ASSIGNMENTS").listFiles();
        if (files.length > 0) {
            return files[0].getName();
        }

        return "";
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
        });

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
    }

    public void doCompileThis() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<CompilerSetting> compilerSettings = GlobalValues.sessionSettings.getCompilerSettings();
        for (int i = 0; i < compilerSettings.size(); i++) {
            CompilerSetting get = compilerSettings.get(i);
            int id = get.getId();

            ArrayList<File> complierReqFiles = get.getCompilerReqFiles();
            String scriptName = "SCRIPTS/"
                    + GlobalValues.sessionSettings.getSession_name()
                    + "-compile-"
                    + id;
            if (get.getOS().trim().equalsIgnoreCase("linux")) {
                scriptName += ".sh";
                write(new File(scriptName),
                        "#!/bin/bash\ncd \"${1}\"\n" + get.getCompilerCommand());
            } else {

            }
            File file = new File("ASSIGNMENTS/" + GlobalValues.sessionSettings.getCurrentStudent());
            if (file.isDirectory()) {
                Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                Tools.runFiles(executorService, id, "compile", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doCompileAll() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<CompilerSetting> compilerSettings = GlobalValues.sessionSettings.getCompilerSettings();
        for (int i = 0; i < compilerSettings.size(); i++) {
            CompilerSetting get = compilerSettings.get(i);
            int id = get.getId();
            File assFile = new File("ASSIGNMENTS");
            File files[] = assFile.listFiles();
            ArrayList<File> complierReqFiles = get.getCompilerReqFiles();
            String scriptName = "SCRIPTS/"
                    + GlobalValues.sessionSettings.getSession_name()
                    + "-compile-"
                    + id;
            if (get.getOS().trim().equalsIgnoreCase("linux")) {
                scriptName += ".sh";
                write(new File(scriptName),
                        "#!/bin/bash\ncd \"${1}\"\n" + get.getCompilerCommand());
            } else {

            }
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    Tools.runFiles(executorService, id, "compile", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
                }
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doRunAll() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<RunSetting> runSettings = GlobalValues.sessionSettings.getRunSettings();
        for (int i = 0; i < runSettings.size(); i++) {
            RunSetting get = runSettings.get(i);
            int id = get.getId();
            File assFile = new File("ASSIGNMENTS");
            File files[] = assFile.listFiles();
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
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    if (get.getInputSequence().length() > 0) {
                        Tools.write(new File(file.getAbsolutePath() + "/input-" + id), get.getInputSequence());
                    }
                    Tools.runFiles(executorService, id, "run", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
                }
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doRunThis() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
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
            {
                File file = new File("ASSIGNMENTS/" + GlobalValues.sessionSettings.getCurrentStudent());

                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);
                    if (get.getInputSequence().length() > 0) {
                        Tools.write(new File(file.getAbsolutePath() + "/input-" + id), get.getInputSequence());
                    }
                    Tools.runFiles(executorService, id, "run", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath());
                }
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doCompareAll() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<RunSetting> runSettings = GlobalValues.sessionSettings.getRunSettings();
        for (int i = 0; i < runSettings.size(); i++) {
            RunSetting get = runSettings.get(i);
            int id = get.getId();
            File assFile = new File("ASSIGNMENTS");
            File files[] = assFile.listFiles();
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
            for (int j = 0; j < files.length; j++) {
                File file = files[j];
                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);

                    Tools.runFiles(executorService, id, "diff", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath(), "run-out-" + id + ".log", get.getSampleOutPutFile().getAbsolutePath());
                }
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doCompareThis() {
        showLogArea();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<RunSetting> runSettings = GlobalValues.sessionSettings.getRunSettings();
        for (int i = 0; i < runSettings.size(); i++) {
            RunSetting get = runSettings.get(i);
            int id = get.getId();

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
            {
                File file = new File("ASSIGNMENTS/" + GlobalValues.sessionSettings.getCurrentStudent());

                if (file.isDirectory()) {
                    Tools.copyReqFiles(file.getAbsolutePath() + "/", complierReqFiles);

                    Tools.runFiles(executorService, id, "diff", file.getAbsolutePath(), "/bin/bash", scriptName, file.getAbsolutePath(), "run-out-" + id + ".log", get.getSampleOutPutFile().getAbsolutePath());
                }
            }

        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    Stage st = new Stage();

    public void showLogArea() {

//        st.setTitle("Log");
//        st.setScene(new Scene(new BorderPane(logArea)));
//        st.show();
    }

    public static void appendLogToLogArea(String Text) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                logArea.appendText(Text);
//            }
//        });

    }

    public static synchronized void addTab(File selectedFile, int tabcounter) {

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

    public static synchronized void IncrementTabcounter() {
        tabcounter.incrementAndGet();
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
