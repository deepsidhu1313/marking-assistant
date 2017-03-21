/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import in.co.s13.marking.assistant.meta.FeedBackEntry;
import in.co.s13.syntaxtextareafx.SyntaxTextAreaFX;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.json.JSONObject;
import org.json.JSONWriter;

/**
 *
 * @author nika
 */
public class FeedbackArea {

    private BorderPane feedbackArea;
    private ObjectProperty<ListCell<FeedBackEntry>> dragSource = new SimpleObjectProperty<>();
    private DataFormat datFormat = new DataFormat("feedback/entry");
    private int itemEnter = 0;
    private int stuFeedBackCounter = 0;
    private ContextMenu cm = new ContextMenu();
    private SyntaxTextAreaFX synTA;
    private File feedBackFile;

    public FeedbackArea(String pathToFeedbackFile) {
        feedBackFile = new File(pathToFeedbackFile);
        feedbackArea = new BorderPane();
        TabPane tabPane = new TabPane();
        Tab feedbackView = new Tab("Graphic");
        SplitPane sp = new SplitPane();
        VBox studentVBox = new VBox(10);
        Label studentLabel = new Label("Enter Student Feedback Below:");
        studentLabel.setStyle(" -fx-font-weight: bold;");
        studentLabel.setPadding(new Insets(5, 10, 0, 10));
        studentLabel.setAlignment(Pos.CENTER);
        ListView<FeedBackEntry> feedbackStu = new ListView<>();

        studentVBox.getChildren().addAll(studentLabel, feedbackStu);
        feedbackStu.setCellFactory(lv -> {
            ListCell<FeedBackEntry> cell = new ListCell<FeedBackEntry>() {
                @Override
                public void updateItem(FeedBackEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText("");
                    } else {
                        setText(item.toString());
                        setTextFill(item.getColor());
                        if (item.getType() != FeedBackEntry.EntryType.FEEDBACK) {
                            setStyle(" -fx-font-weight: bold;");
                        }
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(datFormat, cell.getItem());
                    db.setContent(cc);
                    dragSource.set(cell);
                    //  feedbackStu.getItems().remove(cell.getItem());
                }
            });

//            cell.setOnDragOver(event -> {
//                Dragboard db = event.getDragboard();
//                if (db.hasContent(datFormat)) {
//                    event.acceptTransferModes(TransferMode.MOVE);
//                }
//            });
            cell.setOnDragDone(event -> feedbackStu.getItems().remove(cell.getItem()));
            cell.setOnDragDropped(event -> {
                System.out.println("Item Dropped Here");
                Dragboard db = event.getDragboard();
                if (event.getGestureSource() == feedbackStu) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    ListCell<FeedBackEntry> dragSourceCell = dragSource.get();
                    if (feedbackStu.getItems() == null || dragSourceCell == null) {
                        return;
                    }
                    ObservableList<FeedBackEntry> temp = feedbackStu.getItems();
                    int thisIdx = temp.indexOf(cell.getItem());
                    List<FeedBackEntry> beforeList = temp.subList(0, thisIdx - 1);
                    List<FeedBackEntry> afterList = temp.subList(itemEnter, temp.size());
                    feedbackStu.getItems().clear();
                    feedbackStu.getItems().addAll(beforeList);
                    feedbackStu.getItems().add(new FeedBackEntry(stuFeedBackCounter, dragSourceCell.getItem()));
                    feedbackStu.getItems().addAll(afterList);
//                    event.setDropCompleted(true);
                    System.out.println("Inner Item added at " + itemEnter);
                    stuFeedBackCounter++;
                    itemEnter = 0;
//                    event.setDropCompleted(true);
                    dragSource.set(null);

                }

                if (db.hasContent(datFormat) && dragSource.get() != null) {
                    // in this example you could just do
                    // listView.getItems().add(db.getString());
                    // but more generally:

                    ListCell<FeedBackEntry> dragSourceCell = dragSource.get();
                    feedbackStu.getItems().add(dragSourceCell.getItem());
                    event.setDropCompleted(true);
                    dragSource.set(null);
                } else {
                    event.setDropCompleted(false);
                }
            });

            cell.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    //   itemEnter = feedbackStu.getItems().indexOf(cell.getItem());
                    //  System.out.println("" + itemEnter);
                }
            });
            cell.setOnDragEntered(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    itemEnter = feedbackStu.getItems().indexOf(cell.getItem());
                    System.out.println("" + itemEnter);
                }
            });

            cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.SECONDARY) {

                        MenuItem moveup = new MenuItem("Move Up");
                        moveup.setOnAction((ActionEvent event1) -> {
                            int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                            if (itemno > 0) {
                                FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                                feedbackStu.getItems().remove(itemno);
                                feedbackStu.getItems().add(itemno - 1, fb);
                                feedbackStu.getSelectionModel().select(itemno - 1);
                            }
                        });
                        MenuItem movedown = new MenuItem("Move Down");
                        movedown.setOnAction((ActionEvent event1) -> {
                            int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                            if (itemno < feedbackStu.getItems().size()) {
                                FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                                feedbackStu.getItems().remove(itemno);
                                feedbackStu.getItems().add(itemno + 1, fb);
                                feedbackStu.getSelectionModel().select(itemno + 1);
                            }
                        });
                        MenuItem remove = new MenuItem("Remove");
                        remove.setOnAction((ActionEvent event1) -> {
                            int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                            feedbackStu.getItems().remove(itemno);
                        });
                        cm.getItems().clear();
                        cm.getItems().addAll(moveup, movedown, remove);
                        cm.show(feedbackStu, event.getScreenX(), event.getScreenY());

                    }
                }
            });

            return cell;
        });

        feedbackStu.setOnDragOver((DragEvent event) -> {
            // System.out.println("Item Dropped Here 2");
            /* data is dragged over the target */
 /* accept it only if it is not dragged from the same node
            * and if it has a string data */
            if (event.getGestureSource() != feedbackStu) {
                /* allow for moving */
                //feedbackStu.setFill(Color.GREEN);
                event.acceptTransferModes(TransferMode.COPY);
                ListCell<FeedBackEntry> dragSourceCell = dragSource.get();
                if (feedbackStu.getItems() == null || dragSourceCell == null) {
                    return;
                }
                if (itemEnter < 0) {
                    itemEnter = feedbackStu.getItems().size();
                }
                feedbackStu.getItems().add(itemEnter, new FeedBackEntry(stuFeedBackCounter, dragSourceCell.getItem()));
//                    event.setDropCompleted(true);
                System.out.println("Item added at " + itemEnter);
                stuFeedBackCounter++;
                itemEnter = 0;
                dragSource.set(null);
            } else {
//                event.acceptTransferModes(TransferMode.MOVE);
//                ListCell<FeedBackEntry> dragSourceCell = dragSource.get();
//                if (feedbackStu.getItems() == null || dragSourceCell == null) {
//                    return;
//                }
//                List<FeedBackEntry> beforeList = feedbackStu.getItems().subList(0, itemEnter - 1);
//                List<FeedBackEntry> afterList = feedbackStu.getItems().subList(itemEnter, feedbackStu.getItems().size());
//                feedbackStu.getItems().clear();
//                feedbackStu.getItems().addAll(beforeList);
//                feedbackStu.getItems().add(new FeedBackEntry(stuFeedBackCounter, dragSourceCell.getItem()));
//                feedbackStu.getItems().addAll(afterList);
////                    event.setDropCompleted(true);
//                System.out.println("Item added at " + itemEnter);
//                stuFeedBackCounter++;
//                itemEnter = 0;
////                    event.setDropCompleted(true);
//                dragSource.set(null);
            }

            event.consume();
        });

        VBox templateVBox = new VBox(10);
        Label templateLabel = new Label("Drag Feedbacks from here");
        templateLabel.setStyle(" -fx-font-weight: bold;");
        templateLabel.setPadding(new Insets(5, 10, 0, 10));
        templateLabel.setAlignment(Pos.CENTER);
        ListView<FeedBackEntry> feedbackDB = new ListView<>();
        feedbackDB.setCellFactory(lv -> {
            ListCell<FeedBackEntry> cell = new ListCell<FeedBackEntry>() {
                @Override
                public void updateItem(FeedBackEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText("");
                    } else {
                        setText(item.toString());
                    }
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(datFormat, cell.getItem());
                    db.setContent(cc);
                    dragSource.set(cell);
                }
            });

            cell.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            });

            //cell.setOnDragDone(event -> feedbackDB.getItems().remove(cell.getItem()));
            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(datFormat) && dragSource.get() != null) {
                    // in this example you could just do
                    // listView.getItems().add(db.getString());
                    // but more generally:

                    ListCell<FeedBackEntry> dragSourceCell = dragSource.get();
                    feedbackDB.getItems().add(dragSourceCell.getItem());
                    event.setDropCompleted(true);
                    dragSource.set(null);
                } else {
                    event.setDropCompleted(false);
                }
            });

            return cell;
        });
        feedbackDB.getItems().addAll(new FeedBackEntry(0, 10, 0, 5, "work hard", true, FeedBackEntry.EntryType.FEEDBACK, 0),
                new FeedBackEntry(1, 10, 0, 4, "work hard!!", true, FeedBackEntry.EntryType.FEEDBACK, 0),
                new FeedBackEntry(1, 10, 0, 7, "work hard??", true, FeedBackEntry.EntryType.FEEDBACK, 0));

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

        Label addNewFeedBack = new Label("Add New FeedBack");
        addNewFeedBack.setStyle(" -fx-font-weight: bold;");
        addNewFeedBack.setPadding(new Insets(5, 10, 0, 10));
        addNewFeedBack.setAlignment(Pos.CENTER);

        Label maxMarksLabel = new Label("Maximum Marks");
        TextField maxMTF = new TextField();
        grid.add(maxMarksLabel, 0, 0);
        grid.add(maxMTF, 1, 0);

        Label minMarksLabel = new Label("Minimum Marks");
        TextField minMTF = new TextField();
        grid.add(minMarksLabel, 0, 1);
        grid.add(minMTF, 1, 1);

        Label obtMarksLabel = new Label("Obtained Marks");
        TextField obtMTF = new TextField();
        grid.add(obtMarksLabel, 0, 2);
        grid.add(obtMTF, 1, 2);

        Label feedComLabel = new Label("Feedback Comment");
        TextField feedCommTF = new TextField();
        grid.add(feedComLabel, 0, 03);
        grid.add(feedCommTF, 1, 3);

        Label duplicableLabel = new Label("Clonable");
        ComboBox<Boolean> dupCB = new ComboBox<>();
        dupCB.getItems().addAll(true, false);
        dupCB.getSelectionModel().selectFirst();
        grid.add(duplicableLabel, 0, 4);
        grid.add(dupCB, 1, 4);

        Label feedTypeLabel = new Label("Type");
        ComboBox<FeedBackEntry.EntryType> typCB = new ComboBox<>();
        typCB.getItems().addAll(FeedBackEntry.EntryType.FEEDBACK,
                FeedBackEntry.EntryType.SECTION_START, FeedBackEntry.EntryType.SECTION_END);
        typCB.getSelectionModel().selectFirst();
        grid.add(feedTypeLabel, 0, 5);
        grid.add(typCB, 1, 5);

        Button addButton = new Button("Add");

        Button clearButton = new Button("Clear");
        grid.add(addButton, 0, 6);
        grid.add(clearButton, 1, 6);

        templateVBox.getChildren().addAll(templateLabel, feedbackDB, addNewFeedBack, grid);
        templateVBox.setAlignment(Pos.CENTER);
        studentVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(feedbackDB, Priority.ALWAYS);
        VBox.setVgrow(feedbackStu, Priority.ALWAYS);
        sp.getItems().addAll(studentVBox, templateVBox);
        feedbackView.setContent(sp);
        feedbackView.setClosable(false);

        Tab feedbackTextView = new Tab("Text");
        feedbackTextView.setClosable(false);
        synTA = new SyntaxTextAreaFX(pathToFeedbackFile);
        feedbackTextView.setContent(synTA.getNode());
        tabPane.getTabs().addAll(feedbackView, feedbackTextView);
        feedbackArea.setCenter(tabPane);
    }

    public Node getNode() {
        return feedbackArea;
    }

    public SyntaxTextAreaFX getSynTA() {
        return synTA;
    }

    public void setSynTA(SyntaxTextAreaFX synTA) {
        this.synTA = synTA;
    }

    public File getFeedBackFile() {
        return feedBackFile;
    }

    public void setFeedBackFile(File feedBackFile) {
        this.feedBackFile = feedBackFile;
    }

}
