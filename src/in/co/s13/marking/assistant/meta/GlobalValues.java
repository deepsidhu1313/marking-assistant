/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.File;
import java.util.ArrayList;
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
    public static JSONArray feedbackDBArray = new JSONArray();
    public static JSONObject feedbackDBobject = new JSONObject();
    public static File templateFile;
    public static String SESSION_NAME = "";
    public static ArrayList<CompilerSetting> compilerSettingsList = new ArrayList<>();
    public static ArrayList<RunSetting> runSettingsList = new ArrayList<>();
    public static SessionSettings sessionSettings;

}
