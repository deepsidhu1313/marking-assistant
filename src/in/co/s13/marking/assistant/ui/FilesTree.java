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

//import db.SQLiteJDBC;
import in.co.s13.marking.assistant.meta.GlobalValues;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Nika
 */
public class FilesTree implements Runnable {

    static CustomTree filetree = new CustomTree();

    public static TreeView<File> tv = new TreeView();
    private ContextMenu cm = new ContextMenu();
    public static Image folderCollapseImage = new Image(ClassLoader.getSystemResourceAsStream("icons/folder.png"));
    public static Image folderExpandImage = new Image(ClassLoader.getSystemResourceAsStream("icons/folder-open.png"));
    public static Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/file.png"));
    private static ArrayList<File> expandedDirs = new ArrayList<>();
    private static File selectedProject;
    private static File selectedFile;
    public static TreeItem<File> root;
    private MainWindow mainWindow;
    //SQLiteJDBC treedb = new SQLiteJDBC();
    String sql;
    ResultSet rs;
    int totalFolder = 0;
    int totalFile = 0;
    File[] filelist;
    File f = new File("ASSIGNMENTS");
    int timeout = 0;

    public FilesTree(MainWindow mw) {
        this.mainWindow = mw;

//  this.getTree();
    }

    private TreeView buildFileSystemBrowser() {
        root = createNode(f);
        root.setGraphic(new ImageView(folderExpandImage));
        return new TreeView<File>(root);
    }

    // This method creates a TreeItem to represent the given File. It does this
    // by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods 
    // anonymously, but this could be better abstracted by creating a 
    // 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
    // for the reader.
    private TreeItem<File> createNode(final File f) {
        return new TreeItem<File>((f)) {
            // We cache whether the File is a leaf or not. A File is a leaf if
            // it is not a directory and does not have any files contained within
            // it. We cache this as isLeaf() is called often, and doing the 
            // actual check on File is expensive.
            private boolean isLeaf;

            // We do the children and leaf testing only once, and then set these
            // booleans to false so that we do not check again during this
            // run. A more complete implementation may need to handle more 
            // dynamic file system situations (such as where a folder has files
            // added after the TreeView is shown). Again, this is left as an
            // exercise for the reader.
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;

                    // First getChildren() call, so we actually go off and 
                    // determine the children of the File contained in this TreeItem.
                    super.setExpanded(true);
                    this.setExpanded(true);
                    super.getChildren().setAll(buildChildren(this));
                }

                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = (File) getValue();
                    isLeaf = f.isFile();
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {

                File f = TreeItem.getValue();
                if (f != null && f.isDirectory()) {
                    // super.setGraphic(new ImageView(folderCollapseImage));
                    File[] files = f.listFiles();
                    Arrays.sort(files);
                    // System.out.println(""+files.toString());
                    TreeItem.setExpanded(true);
                    if (files != null) {
                        ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                        for (int i = 0; i < files.length; i++) {
                            /*boolean notIncluded = false;
                            for (String listOpenedProject : MainWindow.listOpenedProjects) {
                                if (files[i].isDirectory() && !(files[i].getAbsolutePath().contains(listOpenedProject))) {
                                    System.out.println("Directory Name is: " + files[i].getAbsolutePath() + " comparing to " + listOpenedProject);
                                    notIncluded = true;
                                }
                            }
                            if (notIncluded) {
                                System.out.println(files[i].getAbsolutePath() + " is not included");
                                if (i == (files.length - 1)) {
                                    System.out.println("Break");
                                    break;
                                } else {
                                    System.out.println("Continue");
                                    continue;

                                }
                            }*/

                            children.add(createNode(files[i]));
                            if (files[i].isDirectory()) {
                                children.get(i).setGraphic(new ImageView(folderCollapseImage));
                                // children.get(i).setExpanded(true);
                            } else {
                                children.get(i).setGraphic(new ImageView(getIcon(files[i])));

                            }

                            children.get(i).addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
                                @Override
                                public void handle(Event e) {

                                    TreeItem<File> source = (TreeItem<File>) e.getSource();
                                    File source2 = source.getValue();
                                    if (source2.isDirectory() && !source.isExpanded()) {
                                        ImageView iv = (ImageView) source.getGraphic();
                                        iv.setImage(folderCollapseImage);
                                        expandedDirs.remove(source2);
                                    }
                                }
                            });
                            children.get(i).addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
                                @Override
                                public void handle(Event e) {
                                    TreeItem<File> source = (TreeItem<File>) e.getSource();
                                    File source2 = source.getValue();
                                    if (source2.isDirectory() && source.isExpanded()) {
                                        ImageView iv = (ImageView) source.getGraphic();
                                        iv.setImage(folderExpandImage);
                                        if (!expandedDirs.contains(source2)) {
                                            expandedDirs.add(source2);
                                        }
                                        //      System.out.println("" + source2);

                                    }
                                }
                            });
                            children.get(i).setExpanded(expandedDirs.contains(children.get(i).getValue()));

                        }
                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }

    @Override
    public void run() {

        {
            filetree.setSimpleRoot(f.getName());
            tv = this.buildFileSystemBrowser();
            tv.getRoot().setExpanded(true);

            //tv.setEditable(true);
            tv.setCellFactory((TreeView<File> p) -> new FileTreeCell());

            MenuItem openStuItem = new MenuItem("Open Student");
            openStuItem.setOnAction((ActionEvent event) -> {
                mainWindow.closeAllTabs();
                mainWindow.openFilesForStudent(selectedProject);
            });

            MenuItem openFile = new MenuItem("Open File");
            openFile.setOnAction((ActionEvent event) -> {
                if (!selectedFile.isDirectory()) {
                    mainWindow.addTab(selectedFile, 1);
                }
            });

            cm.getItems().addAll(openStuItem, openFile);

            //tv.setSelectionModel(null);
            MultipleSelectionModel msm = tv.getSelectionModel();
            tv.setOnMouseClicked((MouseEvent mouseEvent) -> {
                if (mouseEvent.getClickCount() % 2 == 0) {

                    TreeItem<File> item = (TreeItem<File>) msm.getSelectedItem();
                    System.out.println("Selected Text : " + item.getValue());
                    if (item.getValue().exists() && item.getValue().isFile()) {
                        mainWindow.addTab(item.getValue(), mainWindow.tabcounter.get());
                    } else {
                        System.out.println("File Doesnt exist");
                    }// Create New Tab
                } else {

                    TreeItem<File> item = (TreeItem<File>) msm.getSelectedItem();
                    if (item != null && item.getValue() != null && !item.getValue().getName().equalsIgnoreCase("workspace")) {
                        System.out.println("Selected Text : " + item.getValue().getAbsolutePath());
                        System.out.println("Selected Project Is:" + getProjectName(item));
                        selectedProject = getProjectDir(item);
                        selectedFile = item.getValue();

                        String projectName = getProjectName(item.getValue());
                        if (projectName != null && !(GlobalValues.selectedParentFolder.trim().equalsIgnoreCase(projectName.trim()))) {
                            GlobalValues.selectedParentFolder = projectName.trim();
//                            mainWindow.loadManifest(new File("workspace/" + GlobalValues.selectedParentFolder));
                        }
                    }

                }

                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    if (cm != null) {
                        TreeItem<File> item = (TreeItem<File>) msm.getSelectedItem();
                        boolean isDir = item.getValue().isDirectory();
                        openFile.setDisable(isDir);
                        cm.show(mainWindow.stage, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                } else if (cm != null && cm.isShowing()) {
                    cm.hide();
                }
            });
        }
    }

    public static String getProjectName(File f) {
        if (f.getParentFile() != null) {
            if (f.getParentFile()
                    .getAbsolutePath()
                    .equalsIgnoreCase(
                            (new File("ASSIGNMENTS")
                            .getAbsolutePath()))) {
                return f.getName();
            } else {
                return getProjectName(f.getParentFile());
            }
        } else {
            return "";
        }
    }

    public static String getProjectName(TreeItem<File> item) {
        if (item.getParent() != null) {
            if (item.getParent()
                    .getValue().getName()
                    .equalsIgnoreCase(
                            ("ASSIGNMENTS"))) {
                return item.getValue().getName();
            } else {
                return getProjectName(item.getParent());
            }
        } else {
            return "";
        }

    }

    public static File getProjectDir(TreeItem<File> item) {
        if (item.getParent() != null) {
            if (item.getParent()
                    .getValue().getName()
                    .equalsIgnoreCase(
                            ("ASSIGNMENTS"))) {
                return item.getValue();
            } else {
                return getProjectDir(item.getParent());
            }
        } else {
            return null;
        }

    }

    public static File getProjectDir(File f) {
        if (f.getParentFile() != null) {
            if (f.getParentFile()
                    .getAbsolutePath()
                    .equalsIgnoreCase(
                            (new File("ASSIGNMENTS")
                            .getAbsolutePath()))) {
                return f;
            } else {
                return getProjectDir(f.getParentFile());
            }
        } else {
            return null;
        }
    }

    public static void expandFolder(File dir) {
        //  System.out.println("Expanding: " + dir);
        if (!expandedDirs.contains(dir)) {
            expandedDirs.add(dir);
        }

    }

    public static void collapseFolder(File dir) {
        //  System.out.println("Expanding: " + dir);
        expandedDirs.remove(dir);

    }

    public static void scrollTo(File dir) {
        tv.scrollTo(tv.getRoot().getChildren().indexOf(new TreeItem<File>(dir)));
        tv.getSelectionModel().select(new TreeItem<File>(dir));
    }

    public static Image getIcon(File F) {
        Image fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/file.png"));
        String fileExtension = "";
        if (F.getName().contains(".")) {
            fileExtension = F.getName().substring(F.getName().lastIndexOf(".") + 1).toLowerCase();
        }
        switch (fileExtension) {
            case "c":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/c.png"));
                break;
            case "cpp":
            case "c++":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/c++.png"));
                break;

            case "class":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/class.png"));
                break;
            case "diff":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/diff.png"));
                break;
            case "h":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/c++hdr.png"));
                break;
            case "java":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/java.png"));
                break;
            case "log":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/log.png"));
                break;
            case "txt":
            case "text":
                fileImage = new Image(ClassLoader.getSystemResourceAsStream("icons/text.png"));
                break;

        }

        return fileImage;
    }

}
