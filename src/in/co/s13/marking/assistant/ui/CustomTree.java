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

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Nika
 */
public class CustomTree {

    TreeItem<String> rootNode;
    TreeView<String> treeView = new TreeView<String>();

    public CustomTree() {

    }

    public void setSimpleRoot(String text) {
        rootNode
                = new TreeItem<String>(text);
        treeView.setRoot(rootNode);

    }

    public void setCheckboxRoot(String text) {
        rootNode
                = new CheckBoxTreeItem<>(text);
        treeView.setRoot(rootNode);

    }

    public void setExpanded(boolean b) {
        rootNode.setExpanded(b);

    }

    public void setRootNode() {
        treeView.setRoot(rootNode);
    }

    public TreeView getTree() {
        return treeView;
    }

    public void setTreeEditable(boolean b) {
        treeView.setEditable(b);

    }

    public void setSimpleBranchNode(TreeItem parent, String text) {
        TreeItem<String> branch = new TreeItem<>(text);
        parent.getChildren().add(branch);
    }

    public void setSimpleLeafNode(TreeItem branch, String text) {
        TreeItem<String> leaf = new TreeItem<>(text);
        branch.getChildren().add(leaf);
    }

    public void setCheckboxBranchNode(TreeItem parent, String text) {
        TreeItem<String> branch = new CheckBoxTreeItem<>(text);
        parent.getChildren().add(branch);
    }

    public void setCheckboxLeafNode(TreeItem branch, String text) {
        TreeItem<String> leaf = new CheckBoxTreeItem<>(text);
        branch.getChildren().add(leaf);
    }

    public void start(Stage stage) {
        /* for () {
         TreeItem<String> empLeaf = new TreeItem<String>(employee.getName());
         boolean found = false;
         for (TreeItem<String> depNode : rootNode.getChildren()) {
         if (depNode.getValue().contentEquals(employee.getDepartment())){
         depNode.getChildren().add(empLeaf);
         found = true;
         break;
         }
         }
         if (!found) {
         //TreeItem depNode = new TreeItem(employee.getDepartment(), 
         //new ImageView(depIcon)
         // );
         //         rootNode.getChildren().add(depNode);
         //      depNode.getChildren().add(empLeaf);
         }
         }
 
 
         TreeView<String> treeView = new TreeView<String>(rootNode);
         treeView.setCellFactory(new Callback<TreeView<String>,TreeCell<String>>(){
         @Override
         public TreeCell<String> call(TreeView<String> p) {
         return new TextFieldTreeCellImpl();
         }
         });
         */
    }

}
