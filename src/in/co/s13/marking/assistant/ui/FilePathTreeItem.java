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
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FilePathTreeItem extends TreeItem<String> {

    public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("ui/folder.png"));
    public static Image folderExpandImage = new Image(ClassLoader.getSystemResourceAsStream("ui/folder-open.png"));
    public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("ui/file.png"));

    //this stores the full path to the file or directory
    private String fullPath;

    public String getFullPath() {
        return (this.fullPath);
    }

    private boolean isDirectory;

    public boolean isDirectory() {
        return (this.isDirectory);
    }

    public FilePathTreeItem(Path file) {
        super(file.toString());
        this.fullPath = file.toString();

        //test if this is a directory and set the icon
        if (Files.isDirectory(file)) {
            this.isDirectory = true;
            this.setGraphic(new ImageView(folderCollapseImage));
        } else {
            this.isDirectory = false;
            this.setGraphic(new ImageView(fileImage));
            //if you want different icons for different file types this is where you'd do it
        }

        //set the value
        if (!fullPath.endsWith(File.separator)) {
            //set the value (which is what is displayed in the tree)
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                this.setValue(value.substring(indexOf + 1));
            } else {
                this.setValue(value);
            }
        }

        this.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
            @Override
            public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (source.isDirectory() && source.isExpanded()) {
                    ImageView iv = (ImageView) source.getGraphic();
                    iv.setImage(folderExpandImage);
                }
                try {
                    if (source.getChildren().isEmpty()) {
                        Path path = Paths.get(source.getFullPath());
                        BasicFileAttributes attribs = Files.readAttributes(path, BasicFileAttributes.class);
                        if (attribs.isDirectory()) {
                            DirectoryStream<Path> dir = Files.newDirectoryStream(path);
                            for (Path file : dir) {
                                FilePathTreeItem treeNode = new FilePathTreeItem(file);
                                source.getChildren().add(treeNode);
                            }
                        }
                    } else {
                        //if you want to implement rescanning a directory for changes this would be the place to do it
                    }
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        });

        this.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
            @Override
            public void handle(Event e) {
                FilePathTreeItem source = (FilePathTreeItem) e.getSource();
                if (source.isDirectory() && !source.isExpanded()) {
                    ImageView iv = (ImageView) source.getGraphic();
                    iv.setImage(folderCollapseImage);
                }
            }
        });

    }
}
