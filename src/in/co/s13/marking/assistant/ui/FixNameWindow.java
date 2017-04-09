/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.tools.Tools;
import java.applet.Applet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class FixNameWindow extends Application {

    int dirCount = 0, fileCount = 0, totFiles = 0;
    int nameType = 0, actionType = 0;

    public FixNameWindow() {
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

        Label label1 = new Label("FileName Type");
        grid.add(label1, 0, 0);
        ToggleGroup group = new ToggleGroup();
        RadioButton rb11 = new RadioButton("Files With \"M774-50017 - John Doe - Mar 7, 2017 1215 PM - Class.java\"");
        rb11.setUserData(0);
        grid.add(rb11, 1, 1);

        Label label2 = new Label("Action To Be Performed");
        grid.add(label2, 0, 2);

        ToggleGroup group2 = new ToggleGroup();
        RadioButton rb21 = new RadioButton("Create Directory of Username and Move Files ");
        rb21.setUserData(0);
        grid.add(rb21, 1, 3);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (group.getSelectedToggle() != null) {
                    nameType = Integer.parseInt(group.getSelectedToggle().getUserData().toString());
                }

            }
        });
        group2.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (group2.getSelectedToggle() != null) {
                    actionType = Integer.parseInt(group2.getSelectedToggle().getUserData().toString());
                }

            }
        });
        Button previewButton = new Button("Preview");
        previewButton.setOnAction((ActionEvent event) -> {
            preview(nameType, actionType);
        });
        grid.add(previewButton, 0, 4);

        Button applyButton = new Button("Apply");
        applyButton.setOnAction((ActionEvent event) -> {
            apply(nameType, actionType);
            primaryStage.close();
        });
        grid.add(applyButton, 1, 4);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });
        grid.add(cancelButton, 2, 4);

        primaryStage.setScene(new Scene(grid));
        primaryStage.setTitle("Fix Names In ASSIGNMENT Folder");
        primaryStage.show();
    }

    private void preview(int fileNameSelection, int actionSelection) {
        dirCount = 0;
        fileCount = 0;
        File assFile = new File("ASSIGNMENTS");
        File files[] = assFile.listFiles();
        totFiles = files.length;
        TreeView<String> tree = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("ASSIGNMENTS");
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory()) {
                String name = file.getName();
                System.out.println("" + name);
                String newName = "";
                String dirName = "";
                System.out.println("is renaming " + name);
                switch (fileNameSelection) {
                    case 0:
                        int firstOccur = name.indexOf("-");
                        int secondOccur = name.indexOf("-", firstOccur + 1);
                        int thirdOccur = name.indexOf("-", secondOccur + 1);
                        dirName = name.substring(secondOccur + 1, thirdOccur).trim();
                        newName = name.substring(name.lastIndexOf("-") + 1).trim();
                        break;
                }
                switch (actionSelection) {
                    case 0:
                        if (!hasChildWithSameName(root, dirName)) {
                            root.getChildren().add(new TreeItem<String>(dirName));
                            dirCount++;

                        }
                        TreeItem<String> dirtreeItem = getChildWithSameName(root, dirName);
                        if (!hasChildWithSameName(dirtreeItem, newName)) {
                            dirtreeItem.getChildren().add(new TreeItem<>(newName));
                            fileCount++;
                        }
                        break;
                }

            }
        }
        VBox preVBox = new VBox(10);
        Label prevLabel = new Label("Total Files: " + totFiles + "\t New Dirs Created: "
                + dirCount + "\t Files Moved: " + fileCount);

        VBox.setVgrow(tree, Priority.ALWAYS);
        tree.setRoot(root);
        preVBox.getChildren().addAll(prevLabel, tree);
        Stage stage = new Stage();
        stage.setScene(new Scene(preVBox));
        stage.showAndWait();
    }

    private void apply(int fileNameSelection, int actionSelection) {
        dirCount = 0;
        fileCount = 0;
        File assFile = new File("ASSIGNMENTS");
        File files[] = assFile.listFiles();
        totFiles = files.length;

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.isDirectory()) {
                String name = file.getName();
                String newName = "";
                String dirName = "";
                switch (fileNameSelection) {
                    case 0:

                        int firstOccur = name.indexOf("-");
                        int secondOccur = name.indexOf("-", firstOccur + 1);
                        int thirdOccur = name.indexOf("-", secondOccur + 1);
                        dirName = name.substring(secondOccur + 1, thirdOccur).trim();
                        newName = name.substring(name.lastIndexOf("-") + 1).trim();
                        break;
                }
                switch (actionSelection) {
                    case 0:
//                    if (new File("ASSIGNMENTS/" + dirName).exists()) {
//                        System.out.println(" Dir Created: " + new File("ASSIGNMENTS/" + dirName).mkdirs());
//                      
//
//                    }
                        System.out.println(" Dir Created: " + Tools.createDirectory("ASSIGNMENTS/" + dirName));
                        System.out.println(" Renamed: " + Tools.renameFile(file, new File("ASSIGNMENTS/" + dirName + "/" + newName)));

                        break;
                }

            }
        }
    }

    private boolean hasChildWithSameName(TreeItem<String> root, String name) {
        boolean b = false;
        List<TreeItem<String>> children = root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeItem<String> get = children.get(i);
            if (get.getValue().trim().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return b;
    }

    private TreeItem<String> getChildWithSameName(TreeItem<String> root, String name) {
        List<TreeItem<String>> children = root.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TreeItem<String> get = children.get(i);
            if (get.getValue().trim().equalsIgnoreCase(name.trim())) {
                return get;
            }
        }
        return null;
    }

}
