/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.CompilerSetting;
import in.co.s13.marking.assistant.meta.GlobalValues;
import in.co.s13.marking.assistant.meta.RunSetting;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class ExecuteSetupWindow extends Application {

    private int number;
    private ArrayList<File> runReqFiles = new ArrayList<>();
    private File sampleOutFile;

    public ExecuteSetupWindow(int number) {
        this.number = number;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(12);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column2);

        Label addNewFeedBack = new Label("Execution Settings for " + number);
        addNewFeedBack.setStyle(" -fx-font-weight: bold;");
        addNewFeedBack.setPadding(new Insets(5, 10, 0, 10));
        addNewFeedBack.setAlignment(Pos.CENTER);

        Label locationLabel = new Label("Run Location: ");
        ComboBox<String> locationCB = new ComboBox<>();
        locationCB.getItems().addAll("local", "remote");
        locationCB.getSelectionModel().selectFirst();
        grid.add(locationLabel, 0, 0);
        grid.add(locationCB, 1, 0);

        Label runLabel = new Label("Run Command");
        TextField runCommandTF = new TextField();
        runCommandTF.setPromptText("java Main");
        grid.add(runLabel, 0, 1);
        grid.add(runCommandTF, 1, 1);

        Label osLabel = new Label("Operating System: ");
        ComboBox<String> osCB = new ComboBox<>();
        osCB.getItems().addAll("linux", "windows");
        osCB.getSelectionModel().select(GlobalValues.OS);
        grid.add(osLabel, 0, 2);
        grid.add(osCB, 1, 2);

        Label reqFilesLabel = new Label("Copy Required Files");
        TextField reqFilesTF = new TextField();
        grid.add(reqFilesLabel, 0, 3);
        grid.add(reqFilesTF, 1, 3);
        Button browseFilesButton = new Button("Browse");
        grid.add(browseFilesButton, 2, 3);
        browseFilesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser jfc = new FileChooser();
                jfc.setTitle("Choose Files Required");
                jfc.setInitialDirectory(new File("SOLUTION"));
                runReqFiles.clear();
                runReqFiles.addAll(jfc.showOpenMultipleDialog(primaryStage));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < runReqFiles.size(); i++) {
                    File get = runReqFiles.get(i);
                    sb.append("\"" + get.getAbsolutePath() + "\" ");
                }
                reqFilesTF.setText(sb.toString());
            }
        });

        Label sampleOutLabel = new Label("Compare Out With");
        TextField sampleOutFileTF = new TextField();
        grid.add(sampleOutLabel, 0, 4);
        grid.add(sampleOutFileTF, 1, 4);
        Button browseSampleOutFilesButton = new Button("Browse");
        grid.add(browseSampleOutFilesButton, 2, 4);
        browseSampleOutFilesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser jfc = new FileChooser();
                jfc.setTitle("Choose Sample Output");
                jfc.setInitialDirectory(new File("SOLUTION"));
                sampleOutFile = jfc.showOpenDialog(primaryStage);
                sampleOutFileTF.setText(sampleOutFile.getAbsolutePath());
            }
        });

        Label input4runLabel = new Label("Input(s) used by program in runtime");
        TextArea input4runTF = new TextArea();
        input4runTF.setPromptText("Enter each input in new Line\n "
                + "For example for a program which prompts for a filename to read, \n"
                + "So simply enter the file name like\n "
                + "inputfilename.txt\n\n\n "
                + "or the sequence of inputs in each line");
        grid.add(input4runLabel, 0, 5);
        grid.add(input4runTF, 1, 5);

        
        Button okButton = new Button("Ok");

        Button clearButton = new Button("Clear");
        grid.add(okButton, 0, 6);
        grid.add(clearButton, 1, 6);
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                runCommandTF.setText("");
                runReqFiles.clear();
                osCB.getSelectionModel().select(GlobalValues.OS);
                input4runTF.setText("");
                locationCB.getSelectionModel().selectFirst();
                sampleOutFileTF.setText("");
                input4runTF.setText("");
            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                GlobalValues.runSettingsList.add(
                        new RunSetting(number, locationCB.getSelectionModel().getSelectedItem(),
                                runCommandTF.getText().trim(),
                                osCB.getSelectionModel().getSelectedItem(),
                                runReqFiles, sampleOutFile,input4runTF.getText()));

                primaryStage.close();
            }
        });
        primaryStage.setTitle("Run Setting for " + number);
        primaryStage.setScene(new Scene(grid));
        primaryStage.showAndWait();

    }

}
