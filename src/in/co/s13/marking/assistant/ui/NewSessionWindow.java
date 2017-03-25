/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.GlobalValues;
import in.co.s13.marking.assistant.meta.SessionSettings;
import in.co.s13.marking.assistant.tools.Tools;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class NewSessionWindow extends Application {

    boolean selection = false;

    public NewSessionWindow() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField sessionName = new TextField();
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
                jfc.setInitialDirectory(new File("SOLUTION"));
                File file = jfc.showOpenDialog(newProjectDialog);
                File dest = new File("TEMPLATES/" + sessionName.getText().trim() + "/" + file.getName());
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }

                Tools.copyFolder(file, dest);
                GlobalValues.templateFile = dest;
                templatePath.setText(GlobalValues.templateFile.getAbsolutePath());
            }
        });

        grid.add(new Label("Use Remote Machine (ssh):"), 0, 4);
        Label hostnameLabel = new Label("Host:");
        grid.add(hostnameLabel, 0, 5);
        TextField hostnameTF = new TextField();
        hostnameTF.setPromptText(" \"www.example.com\" or \"192.168.1.100\"");
        grid.add(hostnameTF, 1, 5);
        Label portNoLabel = new Label("Port Number:");
        grid.add(portNoLabel, 0, 6);
        Spinner<Integer> portSpinner = new Spinner<>(0, 62555, 22);
        grid.add(portSpinner, 1, 6);
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 7);
        TextField usernameTF = new TextField();
        usernameTF.setPromptText("user");
        grid.add(usernameTF, 1, 7);
        Label passLabel = new Label("Password:");
        grid.add(passLabel, 0, 8);
        TextField passwdTF = new PasswordField();
        grid.add(passwdTF, 1, 8);
        Label osLabel = new Label("Operating System: ");
        grid.add(osLabel, 0, 9);

        ComboBox<String> osCB = new ComboBox<>();
        osCB.getItems().addAll("linux", "windows");
        osCB.getSelectionModel().select(GlobalValues.OS);
        grid.add(osCB, 1, 9);

        CheckBox enableRemoteCB = new CheckBox();
        hostnameLabel.setDisable(true);
        hostnameTF.setDisable(true);
        portNoLabel.setDisable(true);
        portSpinner.setDisable(true);
        usernameLabel.setDisable(true);
        usernameTF.setDisable(true);
        passLabel.setDisable(true);
        passwdTF.setDisable(true);
        osLabel.setDisable(true);
        osCB.setDisable(true);
        enableRemoteCB.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean old_val, Boolean new_val) {
                hostnameLabel.setDisable(!new_val);
                hostnameTF.setDisable(!new_val);
                portNoLabel.setDisable(!new_val);
                portSpinner.setDisable(!new_val);
                usernameLabel.setDisable(!new_val);
                usernameTF.setDisable(!new_val);
                passLabel.setDisable(!new_val);
                passwdTF.setDisable(!new_val);
                osLabel.setDisable(!new_val);
                osCB.setDisable(!new_val);

            }
        });
        grid.add(enableRemoteCB, 1, 4);

        grid.add(okbutton, 0, 10);
        grid.add(cancelbutton, 1, 10);
        GridPane.setHgrow(okbutton, Priority.ALWAYS);
        borderPane.setCenter(grid);

        okbutton.setOnAction((ActionEvent event) -> {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    boolean errorFree = true;
                    String project = sessionName.getText().trim();

                    if (project.length() < 1) {
                        sessionExist.setText("Set A Session Name!!");
                        errorFree = false;

                    }
                    GlobalValues.compilerSettingsList.clear();
                    GlobalValues.runSettingsList.clear();
                    int runNum = Integer.parseInt(runNo.getText().trim());
                    int comNum = Integer.parseInt(compileNo.getText().trim());
                    for (int i = 1; i <= comNum; i++) {
                        CompilerSetupWindow csw = new CompilerSetupWindow(i, sessionName.getText(), enableRemoteCB.isSelected());
                        try {
                            csw.start(new Stage());
                        } catch (Exception ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                    for (int i = 1; i <= runNum; i++) {
                        ExecuteSetupWindow csw = new ExecuteSetupWindow(i, sessionName.getText(), enableRemoteCB.isSelected());
                        try {
                            csw.start(new Stage());
                        } catch (Exception ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    GlobalValues.sessionSettings = new SessionSettings(sessionName.getText(),
                            new File(""), new File(""), comNum, runNum, GlobalValues.compilerSettingsList, GlobalValues.runSettingsList,
                            templatePath.getText().trim(), hostnameTF.getText().trim(), portSpinner.getValue(), usernameTF.getText().trim(),
                            passwdTF.getText().trim(), osCB.getSelectionModel().getSelectedItem());
                    Tools.parseFeedbackTemplate();
                    Tools.writeObject(("app/sessions/" + sessionName.getText() + ".obj"), GlobalValues.sessionSettings);
                    newProjectDialog.hide();
                    newProjectDialog.close();
                    selection = true;
                }
            });

        });
        cancelbutton.setOnAction((ActionEvent event) -> {
            selection = false;
            newProjectDialog.close();

        });

        borderPane.setPadding(new Insets(10, 10, 10, 10));
        newProjectDialog.setScene(new Scene(borderPane));
        newProjectDialog.initOwner(primaryStage);
        newProjectDialog.showAndWait();
    }

    public boolean isSelection() {
        return selection;
    }

}
