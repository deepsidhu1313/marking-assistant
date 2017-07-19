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
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author nika
 */
public class FileTreeCell extends TreeCell<File> {

    //private TextField textField;

    public FileTreeCell() {
    
    }

    
//    @Override
//    public void startEdit() {
//        super.startEdit();
//
//        if (textField == null) {
//            createTextField();
//        }
//        setText(null);
//        setGraphic(textField);
//        textField.selectAll();
//    }
//
//    @Override
//    public void cancelEdit() {
//        super.cancelEdit();
//        setText(getItem().getName());
//        setGraphic(getTreeItem().getGraphic());
//    }

    @Override
    public void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
//            if (textField != null) {
//          //      textField.setText(getString());
//            }
            setText(null);
   //         setGraphic(textField);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

//    private void createTextField() {
//        textField = new TextField(getString());
//        textField.setOnKeyReleased((KeyEvent t) -> {
//            if (t.getCode() == KeyCode.ENTER) {
//                File newFile = new File(getItem().getParentFile().getAbsolutePath() + "/" + textField.getText());
//                getItem().renameTo(newFile);
//                commitEdit(newFile);
//            } else if (t.getCode() == KeyCode.ESCAPE) {
//                cancelEdit();
//            }
//        });
//    }

    private String getString() {
        return getItem() == null ? "" : getItem().getName();
    }

}
