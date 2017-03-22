/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.ui;

import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.property.adapter.PropertyDescriptor;
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
public class FeedbackArea extends BorderPane {

//    private BorderPane this;
    private ObjectProperty<ListCell<FeedBackEntry>> dragSource = new SimpleObjectProperty<>();
    private int itemEnter = 0;
    private int stuFeedBackCounter = 0;
    private ContextMenu cm = new ContextMenu();
    private SyntaxTextAreaFX synTA;
    private File feedBackFile;
    ListView<FeedBackEntry> feedbackStu = new ListView<>();

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
                        MenuItem calc = new MenuItem("Calculate");
                        calc.setOnAction((ActionEvent event1) -> {
                            if (feedbackStu.getItems().size() > 0) {
                                setIndent();

                                calculateMarks(feedbackStu.getItems().get(0));
                            }

                        });
                        cm.getItems().clear();
                        cm.getItems().addAll(moveup, movedown, remove, calc);
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
        if (feedBackFile.exists()) {
            feedbackStu.getItems().addAll((ArrayList<FeedBackEntry>) Tools.readObject(feedBackFile.getAbsolutePath()));
        } else {
            feedbackStu.getItems().addAll(GlobalValues.templateFeedback);
        }
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
        feedbackDB.getItems().addAll(GlobalValues.feedbackDBArray);
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
        TextField minMTF = new TextField("" + Double.MIN_VALUE);
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
                GlobalValues.feedbackDBArray.add(new FeedBackEntry(itemEnter, maxm, minm, obtm, feedBack, val, et, itemEnter));
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
        Button addAltButton = new Button("Add");
        Button remAltButton = new Button("Remove");
        Button upAltButton = new Button("Move Up");
        Button downAltButton = new Button("Move Down");
        Button calcAltButton = new Button("Calculate");

        addAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                feedbackStu.getItems().add(feedbackStu.getSelectionModel().getSelectedIndex(), new FeedBackEntry(feedbackStu.getItems().size(), feedbackDB.getSelectionModel().getSelectedItem()));

            }
        });

        remAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (feedbackStu.isFocused()) {
                    feedbackStu.getItems().remove(feedbackStu.getSelectionModel().getSelectedIndex());
                }
                if (feedbackDB.isFocused()) {
                    feedbackDB.getItems().remove(feedbackDB.getSelectionModel().getSelectedIndex());
                }

            }
        });

        upAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (feedbackStu.isFocused()) {
                    int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                    if (itemno > 0) {
                        FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                        feedbackStu.getItems().remove(itemno);
                        feedbackStu.getItems().add(itemno - 1, fb);
                        feedbackStu.getSelectionModel().select(itemno - 1);
                    }
                }
                if (feedbackDB.isFocused()) {
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
                if (feedbackStu.isFocused()) {
                    int itemno = feedbackStu.getSelectionModel().getSelectedIndex();
                    if (itemno < feedbackStu.getItems().size()) {
                        FeedBackEntry fb = feedbackStu.getItems().get(itemno);
                        feedbackStu.getItems().remove(itemno);
                        feedbackStu.getItems().add(itemno + 1, fb);
                        feedbackStu.getSelectionModel().select(itemno + 1);
                    }
                }
                if (feedbackDB.isFocused()) {
                    int itemno = feedbackDB.getSelectionModel().getSelectedIndex();
                    if (itemno < feedbackDB.getItems().size()) {
                        FeedBackEntry fb = feedbackDB.getItems().get(itemno);
                        feedbackDB.getItems().remove(itemno);
                        feedbackDB.getItems().add(itemno + 1, fb);
                        feedbackDB.getSelectionModel().select(itemno + 1);
                    }
                }

            }
        });

        calcAltButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (feedbackStu.getItems().size() > 0) {
                    setIndent();

                    calculateMarks(feedbackStu.getItems().get(0));
                }
            }
        });
        altButtonGP.add(addAltButton, 0, 0);
        altButtonGP.add(remAltButton, 0, 1);
        altButtonGP.add(upAltButton, 0, 2);
        altButtonGP.add(downAltButton, 0, 3);
        altButtonGP.add(calcAltButton, 0, 4);
        altButtonGP.setAlignment(Pos.CENTER);
        altButtonGP.setVgap(15);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHalignment(HPos.CENTER);
        altButtonGP.getColumnConstraints().add(column3);
        sp.getItems().addAll(studentVBox, altButtonGP, templateVBox);
        feedbackView.setContent(sp);
        feedbackView.setClosable(false);

        Tab feedbackTextView = new Tab("Text");
        feedbackTextView.setClosable(false);
        Tools.write(new File(pathToFeedbackFile + ".txt"), "");
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
            sb.append(get.toString() + "\n");
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
            if (get.getType() == FeedBackEntry.EntryType.SECTION_END) {
                indent--;
            }
            get.setIndent(indent);
        }
        setFeedBackToTextArea();
    }

    private Double calculateMarks(FeedBackEntry fb) {
        ObservableList<FeedBackEntry> items = feedbackStu.getItems();
        int sectioncounter = 1;
        double score = 0;
        for (int i = items.indexOf(fb); i < items.size(); i++) {
            FeedBackEntry get = items.get(i);
            if (get.equals(fb)) {
                continue;
            }
            if (get.getType() == FeedBackEntry.EntryType.SECTION_END) {
                sectioncounter--;
            }
            if (sectioncounter == 0) {
                break;
            }

            if (get.getType() == FeedBackEntry.EntryType.SECTION_START) {
                sectioncounter++;
                score += calculateMarks(get);
            } else if (get.getType() == FeedBackEntry.EntryType.FEEDBACK) {
                score += calculateMarks(get);
            }
        }
        return score;
    }

    public File getFeedBackFile() {
        return feedBackFile;
    }

    public void setFeedBackFile(File feedBackFile) {
        this.feedBackFile = feedBackFile;
    }

}
