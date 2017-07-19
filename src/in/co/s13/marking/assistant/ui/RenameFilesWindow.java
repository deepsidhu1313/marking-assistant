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

import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class RenameFilesWindow extends Application {

    File dir;
    ComboBox<String> cb1 = new ComboBox<>();
    ComboBox<String> cb2 = new ComboBox<>();
    TextField tf1 = new TextField();
    TextField tf2 = new TextField();

    public RenameFilesWindow() {
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

        Label label1 = new Label("Pattern");
        grid.add(label1, 0, 0);
        grid.add(tf1, 1, 0);

        cb1.getItems().addAll("endsWith", "contains");
        grid.add(cb1, 1, 1);
        cb1.getSelectionModel().selectFirst();

        Label label2 = new Label("Action To Be Performed");
        grid.add(label2, 0, 2);
        cb2.getItems().addAll("renameTo", "replace");
        grid.add(cb2, 1, 2);

        Label label3 = new Label("Under Folder");
        grid.add(label3, 0, 3);
        TextField tf3 = new TextField();
        grid.add(tf3, 1, 3);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction((ActionEvent event) -> {
            DirectoryChooser jfc = new DirectoryChooser();
            jfc.setInitialDirectory(new File("ASSIGNMENTS"));
            dir = jfc.showDialog(primaryStage);
        });
        grid.add(browseButton, 2, 3);
        Label label4 = new Label(" New Pattern");
        grid.add(label4, 0, 4);
        grid.add(tf2, 1, 4);

        Button previewButton = new Button("Preview");
        previewButton.setOnAction((ActionEvent event) -> {
        });
        grid.add(previewButton, 0, 5);

        Button applyButton = new Button("Apply");
        applyButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
            apply();
        });
        grid.add(applyButton, 1, 5);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });
        grid.add(cancelButton, 2, 5);

        primaryStage.setScene(new Scene(grid));
        primaryStage.setTitle("Fix Names In ASSIGNMENT Folder");
        primaryStage.show();

    }

    private void apply() {
        ArrayList<File> list = getFilesWithPattern(dir);
        for (int i = 0; i < list.size(); i++) {
            File get = list.get(i);
            if (cb2.getSelectionModel().getSelectedItem().equalsIgnoreCase("renameTo")) {
                get.renameTo(new File(""+get.getParentFile().getAbsolutePath()+"/"+tf2.getText().trim()));
            } else if (cb2.getSelectionModel().getSelectedItem().equalsIgnoreCase("replace")) {
                get.renameTo(new File(""+get.getParentFile().getAbsolutePath()+"/"+get.getName().trim().replace(tf1.getText().trim(), tf2.getText().trim())));

            }
        }
    }

    private ArrayList<File> getFilesWithPattern(File dir) {
        ArrayList<File> list = new ArrayList<>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    getFilesWithPattern(file);
                } else if (file.isFile()) {
                    if (cb1.getSelectionModel().getSelectedItem().equalsIgnoreCase("endsWith")) {
                        if (file.getName().endsWith(tf1.getText().trim())) {
                            list.add(file);
                        }
                    } else if (cb1.getSelectionModel().getSelectedItem().equalsIgnoreCase("contains")) {
                        if (file.getName().contains(tf1.getText().trim())) {
                            list.add(file);
                        }

                    }

                }
            }

        }
        return list;

    }

}
