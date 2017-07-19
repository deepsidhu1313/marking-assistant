/* 
 * Copyright (C) 2017 Navdeep Singh Sidhu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.CompilerSetting;
import in.co.s13.marking.assistant.meta.GlobalValues;
import in.co.s13.marking.assistant.tools.Tools;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class CompilerSetupWindow extends Application {

    private int number;
    private ArrayList<File> compilerReqFiles = new ArrayList<>();
    private String sessionName;
    private boolean remoteEnabled;

    public CompilerSetupWindow(int number, String sessionName, boolean remoteEnabled) {
        this.number = number;
        this.sessionName = sessionName;
        this.remoteEnabled = remoteEnabled;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage primaryStage2 = new Stage();
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

        Label addNewFeedBack = new Label("Compiler Settings for " + number);
        addNewFeedBack.setStyle(" -fx-font-weight: bold;");
        addNewFeedBack.setPadding(new Insets(5, 10, 0, 10));
        addNewFeedBack.setAlignment(Pos.CENTER);

        Label locationLabel = new Label("Compile Location: ");
        ComboBox<String> locationCB = new ComboBox<>();
        locationCB.getItems().addAll("local", "remote");
        if (remoteEnabled) {
            locationCB.getSelectionModel().select(1);
        } else {
            locationCB.getSelectionModel().selectFirst();
        }
        grid.add(locationLabel, 0, 0);
        grid.add(locationCB, 1, 0);

        Label compilerLabel = new Label("Compiler Command");
        TextField compilerCommandTF = new TextField();
        compilerCommandTF.setPromptText("javac *.java");
        grid.add(compilerLabel, 0, 1);
        grid.add(compilerCommandTF, 1, 1);

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
                ArrayList<File> creqFList = new ArrayList();
                creqFList.addAll(jfc.showOpenMultipleDialog(primaryStage2));
                compilerReqFiles.clear();

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < creqFList.size(); i++) {
                    File get = creqFList.get(i);
                    sb.append("\"" + get.getAbsolutePath() + "\" ");
                    new File("SOLUTION/" + sessionName + "/" + number).mkdirs();
                    File dest = new File("SOLUTION/" + sessionName + "/" + number + "/" + get.getName());
                    Tools.copyFolder(get, dest);
                    compilerReqFiles.add(dest);
                }
                reqFilesTF.setText(sb.toString());
            }
        });
        Button okButton = new Button("Ok");

        Button clearButton = new Button("Clear");
        grid.add(okButton, 0, 4);
        grid.add(clearButton, 1, 4);
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reqFilesTF.setText("");
                compilerReqFiles.clear();
                osCB.getSelectionModel().select(GlobalValues.OS);
                compilerCommandTF.setText("");
                locationCB.getSelectionModel().selectFirst();
            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String str = compilerCommandTF.getText().trim();

                GlobalValues.compilerSettingsList.add(
                        new CompilerSetting(number, locationCB.getSelectionModel().getSelectedItem(),
                                str,
                                osCB.getSelectionModel().getSelectedItem(),
                                compilerReqFiles));

                primaryStage2.close();
            }
        });
        primaryStage2.setTitle("Compiler Setting for " + number);
        primaryStage2.setScene(new Scene(new BorderPane(grid)));
        primaryStage2.initOwner(primaryStage);
        primaryStage2.showAndWait();
    }

    public static void main(String[] args) throws Exception {
    }

}
