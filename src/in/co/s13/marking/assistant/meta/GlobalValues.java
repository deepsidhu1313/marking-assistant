/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author nika
 */
public class GlobalValues {

    public static String OS = System.getProperty("os.name").toLowerCase();
    public static String selectedParentFolder = "", lastSelectedParentFolder = "";
    public static ArrayList<File> dirsInAssignmentFolder= new ArrayList<>();
}
