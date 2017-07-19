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
package in.co.s13.marking.assistant.meta;

import java.io.File;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class GlobalValues {

    public static String OS = System.getProperty("os.name").toLowerCase();
    public static String selectedParentFolder = "", lastSelectedParentFolder = "";
    public static ArrayList<File> dirsInAssignmentFolder = new ArrayList<>();
    //public static JSONObject feedbackDBobject = new JSONObject();
    public static File templateFile;
    public static String SESSION_NAME = "";
    public static ArrayList<CompilerSetting> compilerSettingsList = new ArrayList<>();
    public static ArrayList<RunSetting> runSettingsList = new ArrayList<>();
    public static SessionSettings sessionSettings;
    public static ArrayList<FeedBackEntry> templateFeedback = new ArrayList<>();
    public static ArrayList<FeedBackEntry> defaultTemplateFeedback = new ArrayList<>();
    public static ArrayList<FeedBackEntry> feedbackDBArray = new ArrayList<>();
    public static ArrayList<String> listOpenedFiles = new ArrayList<>();
    public static DataFormat datFormat = new DataFormat("feedback/entry");
    public static SettingsEntry settings = new SettingsEntry();
    public static boolean showFeedBackInSeprateWindow = false;

    /**
     *
     */
    public static Image appIcon= new Image(("icons/clipboard_16.png"));
    
}
