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
package in.co.s13.marking.assistant;

import in.co.s13.marking.assistant.tools.Tools;
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
        new File("TEMPLATES").mkdirs();
        new File("app/sessions/").mkdirs();
        new File("app/logs/").mkdirs();
        if(new File("app/settings.obj").exists()){
        Tools.readSettings();
        }
        Tools.createUnpackScript();
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
