/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant;

import in.co.s13.marking.assistant.meta.Tools;
import in.co.s13.marking.assistant.ui.MainWindow;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author nika
 */
public class MarkingAssistant extends Application {

    enum ChangeSource {
        INTERNAL, // indicates a change made by this application
        EXTERNAL, // indicates an external change
    };

    @Override
    public void start(Stage primaryStage) {
        new File("ASSIGNMENTS").mkdir();
        new File("SOLUTION").mkdir();
        new File("FEEDBACK").mkdir();
        new File("SCRIPTS").mkdir();
        new File("app/sessions/").mkdirs();
        if(new File("app/settings.obj").exists()){
        Tools.readSettings();
        }
        MainWindow mw = new MainWindow();
        try {
            mw.start(primaryStage);
        } catch (Exception ex) {
            Logger.getLogger(MarkingAssistant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
