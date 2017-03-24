/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import in.co.s13.marking.assistant.meta.FeedBackEntry;
import in.co.s13.marking.assistant.meta.GlobalValues;
import static in.co.s13.marking.assistant.meta.GlobalValues.datFormat;
import in.co.s13.marking.assistant.meta.Tools;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author nika
 */
public class FeedbackArea extends BorderPane {

//    private BorderPane this;
    private ObjectProperty<ListCell<FeedBackEntry>> dragSource = new SimpleObjectProperty<>();
    private int itemEnter = 0;
    private int stuFeedBackCounter = 0;
    private ContextMenu cm = new ContextMenu();
    private SyntaxTextAreaFX synTA;
    private File feedBackFile;
    ListView<FeedBackEntry> feedbackStu = new ListView<>();
    int lastFocused = 0;

    public FeedbackArea(String pathToFeedbackFile) {
        feedBackFile = new File(pathToFeedbackFile);

//        this = new BorderPane();
        TabPane tabPane = new TabPane();
        Tab feedbackView = new Tab("Graphic");
        SplitPane sp = new SplitPane();
        VBox studentVBox = new VBox(10);
        Label studentLabel = new Label("Enter Student Feedback Below:");
        studentLabel.setStyle(" -fx-font-weight: bold;");
        studentLabel.setPadding(new Insets(5, 10, 0, 10));
        studentLabel.setAlignment(Pos.CENTER);

        studentVBox.getChildren().addAll(studentLabel, feedbackStu);
        feedbackStu.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                lastFocused = 1;
            }
        });
        feedbackStu.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        feedbackStu.setCellFactory(lv -> {
            ListCell<FeedBackEntry> cell = new ListCell<FeedBackEntry>() {
                @Override
                public void updateItem(FeedBackEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText("");
                    } else {
                        setText(item.toString());
                        if (item.getType() == FeedBackEntry.EntryType.SECTION_END) {

                            setTextFill(Color.CORNFLOWERBLUE);
                        } else {
                            setTextFill(item.getColor());

                        }
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
                    FeedBackEntry fb = cell.getItem();
                    cell.setText("" + fb.toString());
                    if (fb.getType() == FeedBackEntry.EntryType.SECTION_END) {

                        cell.setTextFill(Color.CORNFLOWERBLUE);
                    } else {
                        cell.setTextFill(fb.getColor());

                    }
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
                            List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                            for (int i = 0; i < items.size(); i++) {
                                FeedBackEntry get = items.get(i);
                                feedbackStu.getItems().remove(get);

                            }

                        });
                        MenuItem calc = new MenuItem("Calculate");
                        calc.setOnAction((ActionEvent event1) -> {
                            if (feedbackStu.getItems().size() > 0) {
                                setIndent();

                                calculateMarks(feedbackStu.getItems().get(0));
                            }

                        });
                        MenuItem incItem = new MenuItem("Increment Score");
                        incItem.setOnAction((ActionEvent event1) -> {
                            if (lastFocused == 1) {
                                List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                                for (int i = 0; i < items.size(); i++) {
                                    FeedBackEntry get = items.get(i);
                                    get.setObtainedMarks(get.getObtainedMarks() + 0.25);

                                }
                            }
                        });

                        MenuItem decItem = new MenuItem("Decrement Score");
                        decItem.setOnAction((ActionEvent event1) -> {
                            if (lastFocused == 1) {
                                List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                                for (int i = 0; i < items.size(); i++) {
                                    FeedBackEntry get = items.get(i);
                                    get.setObtainedMarks(get.getObtainedMarks() - 0.25);

                                }
                            }
                        });
                        cm.getItems().clear();
                        cm.getItems().addAll(moveup, movedown, remove, calc, incItem, decItem);
                        cm.show(feedbackStu, event.getScreenX(), event.getScreenY());

                    } else if (cm != null && cm.isShowing()) {
                        cm.hide();
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
        if (feedBackFile.exists()) {
            feedbackStu.getItems().addAll((ArrayList<FeedBackEntry>) Tools.readObject(feedBackFile.getAbsolutePath()));
        } else {
            ArrayList<FeedBackEntry> list = GlobalValues.templateFeedback;
            for (int i = 0; i < list.size(); i++) {
                FeedBackEntry get = list.get(i);
                feedbackStu.getItems().add(new FeedBackEntry(i, get));
            }

        }
        VBox templateVBox = new VBox(10);
        Label templateLabel = new Label("Drag Feedbacks from here");
        templateLabel.setStyle(" -fx-font-weight: bold;");
        templateLabel.setPadding(new Insets(5, 10, 0, 10));
        templateLabel.setAlignment(Pos.CENTER);
        ListView<FeedBackEntry> feedbackDB = new ListView<>();
        feedbackDB.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    lastFocused = 2;
                }
            }
        });
        feedbackDB.setCellFactory(lv -> {
            ListCell<FeedBackEntry> cell = new ListCell<FeedBackEntry>() {
                @Override
                public void updateItem(FeedBackEntry item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText("");
                    } else {
                        setText(item.toString());
                        if (item.getType() == FeedBackEntry.EntryType.SECTION_END) {

                            setTextFill(Color.CORNFLOWERBLUE);
                        } else {
                            setTextFill(item.getColor());

                        }
                        if (item.getType() != FeedBackEntry.EntryType.FEEDBACK) {
                            setStyle(" -fx-font-weight: bold;");
                        }
                    }
                }
            };

            cell.setOnDragDetected(event
                    -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(datFormat, cell.getItem());
                    db.setContent(cc);
                    dragSource.set(cell);
                }
            }
            );

            cell.setOnDragOver(event
                    -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            );

            //cell.setOnDragDone(event -> feedbackDB.getItems().remove(cell.getItem()));
            cell.setOnDragDropped(event
                    -> {
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
            }
            );

            return cell;
        }
        );
        feedbackDB.getItems().addAll(GlobalValues.feedbackDBArray);
        feedbackDB.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(12);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        column1.setHgrow(Priority.ALWAYS);
        column1.setMinWidth(GridPane.USE_PREF_SIZE);
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
        TextField minMTF = new TextField("" + Double.MIN_VALUE);
        grid.add(minMarksLabel, 0, 1);
        grid.add(minMTF, 1, 1);

        Label obtMarksLabel = new Label("Obtained Marks");
        TextField obtMTF = new TextField();
        grid.add(obtMarksLabel, 0, 2);
        grid.add(obtMTF, 1, 2);

        Label feedComLabel = new Label("Feedback Comment");
        TextArea feedCommTF = new TextArea();
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
        Button updateButton = new Button("Update");
        grid.add(updateButton, 0, 7);

        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                maxMTF.setText("");
                minMTF.setText("");
                obtMTF.setText("" + Double.MIN_VALUE);
                feedCommTF.setText("");
                dupCB.getSelectionModel().selectFirst();
                typCB.getSelectionModel().selectFirst();
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double maxm = Double.parseDouble(maxMTF.getText());
                double minm = Double.parseDouble(minMTF.getText());
                double obtm = Double.parseDouble(obtMTF.getText());
                boolean val = dupCB.getSelectionModel().getSelectedItem();
                FeedBackEntry.EntryType et = typCB.getSelectionModel().getSelectedItem();
                String feedBack = feedCommTF.getText();
                GlobalValues.feedbackDBArray.add(new FeedBackEntry(itemEnter, maxm, minm, obtm, feedBack, val, et, 0));
                feedbackDB.getItems().clear();
                feedbackDB.getItems().addAll(GlobalValues.feedbackDBArray);
                feedbackDB.getSelectionModel().select(feedbackDB.getItems().size() - 1);
                maxMTF.setText("");
                minMTF.setText("");
                obtMTF.setText("" + Double.MIN_VALUE);
                feedCommTF.setText("");
                dupCB.getSelectionModel().selectFirst();
                typCB.getSelectionModel().selectFirst();
                save();
            }
        });

        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double maxm = Double.parseDouble(maxMTF.getText());
                double minm = Double.parseDouble(minMTF.getText());
                double obtm = Double.parseDouble(obtMTF.getText());
                boolean val = dupCB.getSelectionModel().getSelectedItem();
                FeedBackEntry.EntryType et = typCB.getSelectionModel().getSelectedItem();
                String feedBack = feedCommTF.getText();
                int index = feedbackDB.getSelectionModel().getSelectedIndex();
                FeedBackEntry fbe = feedbackDB.getSelectionModel().getSelectedItem();
                fbe.setMaximumMarks(maxm);
                fbe.setMinimumMarks(minm);
                fbe.setObtainedMarks(obtm);
                fbe.setDuplicateAllowed(val);
                fbe.setType(et);
                fbe.setFeedBack(feedBack);
                feedbackDB.getItems().remove(index);
                feedbackDB.getItems().add(index, fbe);
                maxMTF.setText("");
                minMTF.setText("");
                obtMTF.setText("" + Double.MIN_VALUE);
                feedCommTF.setText("");
                dupCB.getSelectionModel().selectFirst();
                typCB.getSelectionModel().selectFirst();
                save();
            }
        });

        feedbackDB.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<FeedBackEntry>() {
                    public void changed(ObservableValue<? extends FeedBackEntry> observable,
                            FeedBackEntry oldValue, FeedBackEntry newValue) {
                        if (newValue != null) {
                            maxMTF.setText("" + newValue.getMaximumMarks());
                            minMTF.setText("" + newValue.getMinimumMarks());
                            obtMTF.setText("" + newValue.getObtainedMarks());
                            feedCommTF.setText("" + newValue.getFeedBack());
                            dupCB.getSelectionModel().select(newValue.isDuplicateAllowed());
                            typCB.getSelectionModel().select(newValue.getType());

                        }
                    }
                });

        templateVBox.getChildren().addAll(templateLabel, feedbackDB, addNewFeedBack, grid);
        templateVBox.setAlignment(Pos.CENTER);
        studentVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(feedbackDB, Priority.ALWAYS);
        VBox.setVgrow(feedbackStu, Priority.ALWAYS);

        GridPane altButtonGP = new GridPane();

        Button incAltButton = new Button("Increment Score");
        Button decrAltButton = new Button("Decrement Score");
        Button giveZeroAltButton = new Button("Give 0 Score");
        Button giveFullAltButton = new Button("Give Full Score");
        Button addAltButton = new Button("Add");
        Button remAltButton = new Button("Remove");
        Button upAltButton = new Button("Move Up");
        Button downAltButton = new Button("Move Down");
        Button calcAltButton = new Button("Calculate");
        Button setThisAsTempAltButton = new Button("Save This feedback As Template");
        Button resetTemplateAltButton = new Button("Reset Feedback To Template");
        Button defaultTemplateAltButton = new Button("Load Default Template");
        Button reParseTemplateAltButton = new Button("Reparse Default Template");
        defaultTemplateAltButton.setTextFill(Color.RED);
        reParseTemplateAltButton.setTextFill(Color.RED);
        incAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < items.size(); i++) {
                        FeedBackEntry get = items.get(i);
                        get.setObtainedMarks(get.getObtainedMarks() + 0.25);

                    }
                }
                if (lastFocused == 2) {
                    List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        FeedBackEntry get = selectedItems.get(i);
                        get.setObtainedMarks(get.getObtainedMarks() + 0.25);
                    }
                }
            }
        });
        decrAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < items.size(); i++) {
                        FeedBackEntry get = items.get(i);
                        get.setObtainedMarks(get.getObtainedMarks() - 0.25);

                    }
                }
                if (lastFocused == 2) {
                    List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        FeedBackEntry get = selectedItems.get(i);
                        get.setObtainedMarks(get.getObtainedMarks() - 0.25);
                    }
                }
            }
        });

        giveZeroAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < items.size(); i++) {
                        FeedBackEntry get = items.get(i);
                        get.setObtainedMarks(0);

                    }
                }
                if (lastFocused == 2) {
                    List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        FeedBackEntry get = selectedItems.get(i);
                        get.setObtainedMarks(0);
                    }
                }
            }
        });

        giveFullAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < items.size(); i++) {
                        FeedBackEntry get = items.get(i);
                        get.setObtainedMarks(get.getMaximumMarks());

                    }
                }
                if (lastFocused == 2) {
                    List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        FeedBackEntry get = selectedItems.get(i);
                        get.setObtainedMarks(get.getMaximumMarks());
                    }
                }
            }
        });

        addAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                int index = feedbackStu.getSelectionModel().getSelectedIndex();
                if (index < 0) {
                    index = 0;
                }
                for (int i = 0; i < selectedItems.size(); i++) {
                    FeedBackEntry get = selectedItems.get(i);
                    feedbackStu.getItems().add(index + i, new FeedBackEntry(feedbackStu.getItems().size(), get));

                }
                //feedbackStu.getItems().add(feedbackStu.getSelectionModel().getSelectedIndex(), new FeedBackEntry(feedbackStu.getItems().size(), feedbackDB.getSelectionModel().getSelectedItem()));

            }
        });

        remAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    List<FeedBackEntry> items = feedbackStu.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < items.size(); i++) {
                        FeedBackEntry get = items.get(i);
                        feedbackStu.getItems().remove(get);

                    }
                }
                if (lastFocused == 2) {
                    List<FeedBackEntry> selectedItems = feedbackDB.getSelectionModel().getSelectedItems();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        FeedBackEntry get = selectedItems.get(i);
                        GlobalValues.feedbackDBArray.remove(get);
                        feedbackDB.getItems().remove(get);
                    }
                }

            }
        });

        upAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                    if (itemno > 0) {
                        FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                        feedbackStu.getItems().remove(itemno);
                        feedbackStu.getItems().add(itemno - 1, fb);
                        feedbackStu.getSelectionModel().select(itemno - 1);
                    }
                }
                if (lastFocused == 2) {
                    int itemno = feedbackDB.getSelectionModel().getSelectedIndex();
                    if (itemno > 0) {
                        FeedBackEntry fb = feedbackDB.getItems().get(itemno);
                        feedbackDB.getItems().remove(itemno);
                        feedbackDB.getItems().add(itemno - 1, fb);
                        feedbackDB.getSelectionModel().select(itemno - 1);
                    }
                }

            }
        });

        downAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (lastFocused == 1) {
                    int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                    if (itemno < feedbackStu.getItems().size() - 1) {
                        FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                        feedbackStu.getItems().remove(itemno);
                        feedbackStu.getItems().add(itemno + 1, fb);
                        feedbackStu.getSelectionModel().select(itemno + 1);
                    }
                }
                if (lastFocused == 2) {
                    int itemno = feedbackDB.getSelectionModel().getSelectedIndex();
                    if (itemno < feedbackDB.getItems().size() - 1) {
                        FeedBackEntry fb = feedbackDB.getItems().get(itemno);
                        feedbackDB.getItems().remove(itemno);
                        feedbackDB.getItems().add(itemno + 1, fb);
                        feedbackDB.getSelectionModel().select(itemno + 1);
                    }
                }

            }
        });

        calcAltButton.setOnAction((ActionEvent event) -> {
            if (feedbackStu.getItems().size() > 0) {
                
                calculateMarks(feedbackStu.getItems().get(0));
                setIndent();
            }
        });
        setThisAsTempAltButton.setOnAction((ActionEvent event) -> {
            List<FeedBackEntry> list = feedbackStu.getItems();
            GlobalValues.templateFeedback.clear();
            for (int i = 0; i < list.size(); i++) {
                FeedBackEntry get = list.get(i);
                GlobalValues.templateFeedback.add(new FeedBackEntry(i, get));
            }
            Tools.writeObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-template.obj"), GlobalValues.templateFeedback);
        });

        resetTemplateAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                feedbackStu.getItems().clear();
                feedbackStu.getItems().addAll(GlobalValues.templateFeedback);

            }
        });

        defaultTemplateAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                feedbackStu.getItems().clear();
                feedbackStu.getItems().addAll(GlobalValues.defaultTemplateFeedback);

            }
        });
        reParseTemplateAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Tools.parseFeedbackTemplate();
            }
        });
        Label marksLabel = new Label("Marks");
        marksLabel.setStyle(" -fx-font-weight: bold;");
        altButtonGP.add(marksLabel, 0, 0);
        altButtonGP.add(incAltButton, 0, 1);
        altButtonGP.add(decrAltButton, 0, 2);
        altButtonGP.add(giveZeroAltButton, 0, 3);
        altButtonGP.add(giveFullAltButton, 0, 4);
        Label feedbackLabel = new Label("Feedback");
        feedbackLabel.setStyle(" -fx-font-weight: bold;");
        altButtonGP.add(feedbackLabel, 0, 5);
        altButtonGP.add(addAltButton, 0, 6);
        altButtonGP.add(remAltButton, 0, 7);
        altButtonGP.add(upAltButton, 0, 8);
        altButtonGP.add(downAltButton, 0, 9);
        altButtonGP.add(calcAltButton, 0, 10);
        Label tempControlLabel = new Label("Template Controls");
        tempControlLabel.setStyle(" -fx-font-weight: bold;");
        altButtonGP.add(tempControlLabel, 0, 11);
        altButtonGP.add(setThisAsTempAltButton, 0, 12);
        altButtonGP.add(resetTemplateAltButton, 0, 13);
        altButtonGP.add(defaultTemplateAltButton, 0, 14);
        altButtonGP.add(reParseTemplateAltButton, 0, 15);
        altButtonGP.setAlignment(Pos.CENTER);
        altButtonGP.setVgap(15);
        grid.getColumnConstraints().add(column1);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHalignment(HPos.CENTER);
        column3.setHgrow(Priority.ALWAYS);
        column3.setMinWidth(GridPane.USE_PREF_SIZE);
        column3.setMaxWidth(GridPane.USE_PREF_SIZE);
        altButtonGP.getColumnConstraints().add(column3);
        sp.getItems().addAll(studentVBox, altButtonGP, templateVBox);
        feedbackView.setContent(sp);
        feedbackView.setClosable(false);

        Tab feedbackTextView = new Tab("Text");
        feedbackTextView.setClosable(false);
        if (!new File(pathToFeedbackFile + ".txt").exists()) {
            Tools.write(new File(pathToFeedbackFile + ".txt"), "");
        }
        synTA = new SyntaxTextAreaFX(pathToFeedbackFile + ".txt");
        feedbackTextView.setContent(synTA.getNode());
        tabPane.getTabs().addAll(feedbackView, feedbackTextView);
        this.setCenter(tabPane);
    }

    public Node getNode() {
        return this;
    }

    public SyntaxTextAreaFX getSynTA() {
        return synTA;
    }

    public void setSynTA(SyntaxTextAreaFX synTA) {
        this.synTA = synTA;
    }

    public void save() {
        // setIndent();
        // calculateMarks(feedbackStu.getItems().get(0));
        synTA.save();
        List<FeedBackEntry> dat = feedbackStu.getItems();
        ArrayList<FeedBackEntry> dat2 = new ArrayList<>();
        for (int i = 0; i < dat.size(); i++) {
            FeedBackEntry get = dat.get(i);
            dat2.add(get);
        }

        Tools.writeObject(feedBackFile.getAbsolutePath(), dat2);
        Tools.dumpFeedbackDBForThisSession();
    }

    private void setFeedBackToTextArea() {
        synTA.setText("");
        StringBuilder sb = new StringBuilder();
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        for (int i = 0; i < items.size(); i++) {
            FeedBackEntry get = items.get(i);
            if (get.getType() != FeedBackEntry.EntryType.SECTION_END) {
                sb.append(get.toString() + "\n");
            }
        }
        synTA.setText(sb.toString());
    }

    private void setIndent() {
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        int indent = 0;
        for (int i = 0; i < items.size(); i++) {
            FeedBackEntry get = items.get(i);
            if (get.getType() == FeedBackEntry.EntryType.SECTION_START) {
                indent++;
            }
            get.setIndent(indent);
            if (get.getType() == FeedBackEntry.EntryType.SECTION_END) {
                indent--;
            }

        }
        setFeedBackToTextArea();
    }

    private Double calculateMarks(FeedBackEntry fb) {
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        int sectioncounter = 1;
        double score = 0;
        for (int i = items.indexOf(fb); i < items.size(); i++) {
            FeedBackEntry get = items.get(i);
            System.out.println("Parent : " + fb + " Child:" + get);
            if (get.equals(fb)) {
                continue;
            }
            if (get.getType() == FeedBackEntry.EntryType.SECTION_END) {
                sectioncounter--;
            }
            if (get.getType() == FeedBackEntry.EntryType.SECTION_START) {
                sectioncounter++;
            }
            if (sectioncounter == 0) {
                break;
            }

            if (get.getType() == FeedBackEntry.EntryType.SECTION_START && !hasNoChildren(get)) {
                // sectioncounter++;
                score += calculateMarks(get);
                i = getSectionEndIndex(get) - 1;
            } else if (get.getType() == FeedBackEntry.EntryType.SECTION_START && hasNoChildren(get)) {
                //sectioncounter++;
                score += (get.getObtainedMarks());
            } else if (get.getType() == FeedBackEntry.EntryType.FEEDBACK) {
                score += (get.getObtainedMarks());
                // System.out.println("Score for " + get);
            }
        }
        if (!hasNoChildren(fb)) {
            fb.setObtainedMarks(score);
        }
        System.out.println("Score for " + fb);
        return score;
    }

    public File getFeedBackFile() {
        return feedBackFile;
    }

    public void setFeedBackFile(File feedBackFile) {
        this.feedBackFile = feedBackFile;
    }

    private boolean hasNoChildren(FeedBackEntry fb) {
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        if (fb.getType() == FeedBackEntry.EntryType.SECTION_START) {
            if (items.get(items.indexOf(fb) + 1).getType() == FeedBackEntry.EntryType.SECTION_END) {
                return true;
            }
        }
        return false;
    }

    private int getSectionEndIndex(FeedBackEntry fb) {
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        int sectioncounter = 1, index = Integer.MIN_VALUE;
        for (int i = items.indexOf(fb) + 1; i < items.size(); i++) {
            FeedBackEntry get = items.get(i);
            // System.out.println("Parent : " + fb + " Child:" + get);
            if (get.equals(fb)) {
                continue;
            }
            if (get.getType() == FeedBackEntry.EntryType.SECTION_END) {
                sectioncounter--;
            }
            if (get.getType() == FeedBackEntry.EntryType.SECTION_START) {
                sectioncounter++;
            }
            if (sectioncounter == 0) {
                index = i;
                break;
            }
        }
        //System.out.println("Section "+fb+" starts at :"+items.indexOf(fb)+" ends at "+index);
        return index;
    }

}
