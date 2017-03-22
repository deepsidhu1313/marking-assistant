/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import static in.co.s13.marking.assistant.meta.GlobalValues.OS;
import static in.co.s13.marking.assistant.meta.GlobalValues.dirsInAssignmentFolder;
import in.co.s13.marking.assistant.ui.MainWindow;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class Tools {

    public static String read(File f) throws IOException {
        String content = "";
        if (!f.exists()) {
            return "";
        }
        FileInputStream fstream = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

//Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            content += strLine;
            content += "\n";
            // System.out.println(strLine);
        }

//Close the input stream
        br.close();
        return content;
    }

    public static void write(File f, String text) {
        try (FileWriter fw = new FileWriter(f);
                PrintWriter pw = new PrintWriter(fw)) {
            pw.print(text);
            pw.close();
            fw.close();
            System.out.println("Written " + text + "\n to " + f.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeObject(String path, Object obj) {
        try (FileOutputStream fos = new FileOutputStream(path); GZIPOutputStream gos = new GZIPOutputStream(fos); ObjectOutputStream oos = new ObjectOutputStream(gos)) {
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Object readObject(String path) {
        Object obj = null;
        try (FileInputStream fin = new FileInputStream(path); GZIPInputStream gis = new GZIPInputStream(fin); ObjectInputStream ois = new ObjectInputStream(gis)) {
            obj = ois.readObject();
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    /**
     * *
     * Copy/ folder/file from source to destination
     *
     * @param src : Path to source folder/file
     * @param dest : Path to destination folder/file
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) {

        if (src.isDirectory()) {

            //if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }

        } else {
            try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println("File copied from " + src + " to " + dest);
        }
    }

    /**
     * *
     * Generate difference of new output from original output
     *
     * @param foldername : Path or name of the directory where output files
     * reside
     */
    public static void calculateDiffFromOrigOutput(ExecutorService executorService, String foldername, String firstFile, String secondfile, int i) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "diff.sh", foldername, firstFile, secondfile);
                    Process p = pb.start();
                    try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream())); PrintWriter outputWriter = new PrintWriter(foldername + "diff-out" + i + ".log", "UTF-8")) {

//                    PrintWriter errorWriter = new PrintWriter(foldername + "diff-error" + i + ".log", "UTF-8");
                        StringBuilder errorString = new StringBuilder();
                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                            // System.out.println(s);
                            outputWriter.println(s + "");
                        }
                        while ((s = stdError.readLine()) != null) {
                            //System.out.println(s);
//                        errorWriter.println(s + "");
                            errorString.append(s + "\n");
                        }
                        if (errorString.length() > 0) {
                            write(new File(foldername + "diff-error" + i + ".log"), errorString.toString());
                        }
                    }

//                    errorWriter.close();
                    p.destroy();

                    System.out.println("Diff generated Files in " + foldername);
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);
    }

    public static void dumpFeedbackDBForThisSession() {
        writeObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-db.fdb"), GlobalValues.feedbackDBArray);
    }

    public static void run(ExecutorService executorService, int counter, String foldername, String... commands) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder(commands);
                    Process p = pb.start();
                    try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream())); PrintWriter outputWriter = new PrintWriter(foldername + "command-out" + counter + ".log", "UTF-8"); PrintWriter errorWriter = new PrintWriter(foldername + "command-error" + counter + ".log", "UTF-8")) {

                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                            // System.out.println(s);
                            outputWriter.println(s + "");
                        }
                        while ((s = stdError.readLine()) != null) {
                            //System.out.println(s);
                            errorWriter.println(s + "");
                        }

                    }

                    p.destroy();

                    System.out.println("Command in " + foldername);
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);
    }

    /**
     * *
     * Run java files under the directory
     *
     * @param foldername : path to the directory where all the compiled classes
     * reside
     * @param main : name of the main class
     */
    public static void runFiles(ExecutorService executorService, int i, String type, String path, String... commands) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    System.out.println("" + commands);
                    ProcessBuilder pb = new ProcessBuilder(commands);//, "inputfilename" + i);
                    Process p = pb.start();
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String diffExt = "";
                    if (type.equalsIgnoreCase("diff")) {
                        diffExt = ".diff";
                    }
                    PrintWriter outputWriter = new PrintWriter(path + "/" + type + "-out-" + i + ".log" + diffExt, "UTF-8");
                    PrintWriter errorWriter = new PrintWriter(path + "/" + type + "-error-" + i + ".log" + diffExt, "UTF-8");
                    StringBuilder out = new StringBuilder();
                    int counter = 0;
                    int diff = 1000;
                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        //  System.out.println(s);
                        outputWriter.println(s + "");
                        if (counter % diff == 0) {
//                            MainWindow.appendLogToLogArea(out.toString() + "\n");
                            counter = 0;
                            out = new StringBuilder();
                        } else {
                            counter++;
                            out.append(s + "\n");

                        }
                    }
//                    MainWindow.appendLogToLogArea(out.toString() + "\n");
                    counter = 0;
                    out = new StringBuilder();

                    while ((s = stdError.readLine()) != null) {
                        //System.out.println(s);
                        errorWriter.println(s + "");
                        if (counter % diff == 0) {
//                            MainWindow.appendLogToLogArea(out.toString() + "\n");
                            counter = 0;
                            out = new StringBuilder();
                        } else {
                            counter++;
                            out.append(s + "\n");

                        }
                    }
//                    MainWindow.appendLogToLogArea(out.toString() + "\n");

                    stdError.close();
                    stdInput.close();
                    outputWriter.close();
                    errorWriter.close();
                    //calculateDiffFromOrigOutput(foldername);
                    p.destroy();
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < commands.length; j++) {
                        String object = commands[j];
                        sb.append(object + ", ");
                    }
                    System.out.println("Ran Commands" + sb.toString() + "  ");
                    MainWindow.appendLogToLogArea("Ran Commands" + sb.toString() + "\n");

                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);
    }

    /**
     * *
     * Run java files under the directory
     *
     * @param foldername : path to the directory where all the compiled classes
     * reside
     * @param main : name of the main class
     */
    public static void runFilesWithRedirectInput(ExecutorService executorService, String foldername, String main, String inputFileName, int i) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "exec2.sh", foldername, main, inputFileName);//, "inputfilename" + i);
                    Process p = pb.start();
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    PrintWriter outputWriter = new PrintWriter(foldername + "run-out" + i + ".log", "UTF-8");
                    PrintWriter errorWriter = new PrintWriter(foldername + "run-error" + i + ".log", "UTF-8");

                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        //  System.out.println(s);
                        outputWriter.println(s + "");
                    }
                    while ((s = stdError.readLine()) != null) {
                        //System.out.println(s);
                        errorWriter.println(s + "");
                    }

                    stdError.close();
                    stdInput.close();
                    outputWriter.close();
                    errorWriter.close();
                    //calculateDiffFromOrigOutput(foldername);
                    p.destroy();
                    System.out.println("Ran Files in " + foldername);
                    //generateDiffbetFiles("output", foldername + "run-out.log", foldername);

                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);
    }

    public static void unpackFile(String folder, String packedFileName, String outputDir, ExecutorService executorService) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "unpack.sh", folder, packedFileName, outputDir);
                    Process p = pb.start();
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                        //    outputWriter.println(s + "");
                    }
                    while ((s = stdError.readLine()) != null) {
                        System.out.println(s);
//                        errorWriter.println(s + "");
                    }

                    stdError.close();
                    stdInput.close();
//                    outputWriter.close();
//                    errorWriter.close();
                    p.destroy();
                    System.out.println("Unpacked Files in " + outputDir);
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);

    }

    /**
     * Compiles all the java file in a directory
     *
     * @param foldername : Path or name of the folder where source code
     * reside/** Compiles all the java file in a directory
     * @param foldername : Path or name of the folder where source code reside
     */
    public static void compileFiles(String foldername, ExecutorService executorService, ArrayList<String> alreadyCompiled) {
        if (alreadyCompiled.contains(foldername)) {
            return;
        } else {
            alreadyCompiled.add(foldername);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "compile.sh", foldername);
                    Process p = pb.start();
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    PrintWriter outputWriter = new PrintWriter(foldername + "compiler-out.log", "UTF-8");
                    PrintWriter errorWriter = new PrintWriter(foldername + "compiler-error.log", "UTF-8");

                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        // System.out.println(s);
                        outputWriter.println(s + "");
                    }
                    while ((s = stdError.readLine()) != null) {
                        //System.out.println(s);
                        errorWriter.println(s + "");
                    }

                    stdError.close();
                    stdInput.close();
                    outputWriter.close();
                    errorWriter.close();
                    p.destroy();
                    System.out.println("Compiled Files in " + foldername);
                } catch (IOException ex) {
                    Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }

            }
        });
        executorService.submit(t);
    }

    public static void copyReqFiles(String dir, String[] requiredFiles) {
        for (int i = 0; i < requiredFiles.length; i++) {
            Tools.copyFolder(new File(requiredFiles[i]), new File(dir + "/" + requiredFiles[i]));

        }
    }

    public static void copyReqFiles(String dir, File[] requiredFiles) {
        for (int i = 0; i < requiredFiles.length; i++) {
            Tools.copyFolder((requiredFiles[i]), new File(dir + "/" + requiredFiles[i]));

        }
    }

    public static void copyReqFiles(String dir, ArrayList<File> requiredFiles) {
        for (int i = 0; i < requiredFiles.size(); i++) {
            Tools.copyFolder((requiredFiles.get(i)), new File(dir + "/" + requiredFiles.get(i).getName()));

        }
    }

    /**
     * *
     * This method will create parent directories if they don't exist, otherwise
     * will create the given directory
     *
     * @param name : Name or path of the directory/directories to create
     * @return true if directories are created successfully
     */
    public static boolean createDirectory(String name) {
        return new File(name).mkdirs();
    }

    /**
     * *
     * Renames a file
     *
     * @param filename : Name or Path of the file to be renamed
     * @param newFilename : Name or path of the new file
     * @return true if renaming successful.
     */
    public static boolean renameFile(String filename, String newFilename) {
        return new File(filename).renameTo(new File(newFilename));
    }

    public static String readFile(String path) {
        String str = "";
        if (path.trim().length() > 1) {
            try {
                Charset encoding = Charset.defaultCharset();
                byte[] encoded = Files.readAllBytes(Paths.get(path));
                str = new String(encoded, encoding);
            } catch (IOException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return str;
    }

    /**
     * *
     * Renames a file
     *
     * @param filename : Name or Path of the file to be renamed
     * @param newFilename : Name or path of the new file
     * @return true if renaming successful.
     */
    public static boolean renameFile(File filename, File newFilename) {
        return (filename).renameTo((newFilename));
    }

    /**
     * *
     * Prints the name of the input file such as abc.txt to the file
     * <i><b>inputfilename</b></i>
     *
     * @param folder : Path of the directory where file
     * <i><b>inputfilename</b></i> should be saved
     */
    public static void printInputFile(String folder, String[] inputFilenameForCode) {
        for (int i = 0; i < inputFilenameForCode.length; i++) {
            try {
                PrintWriter fileWriter = new PrintWriter(folder + "inputfilename" + i, "UTF-8");
                fileWriter.append(inputFilenameForCode[i]);
                fileWriter.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

    /**
     * *
     * Create Scripts used to Compile and Run JAVA files, Also creates a script
     * to generate difference of output file from original output file
     */
    public static void createCompilerScript() {
        try {
            try (PrintWriter scriptWriter = new PrintWriter("compile.sh", "UTF-8")) {
                scriptWriter.append("#!/bin/bash \n"
                        + "\n"
                        + "cd \"${1}/\"\n"
                        + "clang -Wall nthword.c -o nthword.out \n"
                        + "clang -Wall -DNDEBUG levenshtein.c -o levenshtein.out");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            try (PrintWriter scriptWriter = new PrintWriter("exec.sh", "UTF-8")) {
                scriptWriter.append("#!/bin/bash \n"
                        + "\n"
                        + "cd \"${1}/\"\n"
                        + "./${2}");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            try (PrintWriter scriptWriter = new PrintWriter("exec2.sh", "UTF-8")) {
                scriptWriter.append("#!/bin/bash \n"
                        + "\n"
                        + "cd \"${1}/\"\n"
                        + "./${2} < ${3}");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            try (PrintWriter scriptWriter = new PrintWriter("diff.sh", "UTF-8")) {
                scriptWriter.append("#!/bin/bash \n"
                        + "\n"
                        + "cd \"${1}\"\n"
                        + "diff -u -B -b ${2} ${3}");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            try (PrintWriter scriptWriter = new PrintWriter("unpack.sh", "UTF-8")) {
                scriptWriter.append("#!/bin/bash \n"
                        + "\n"
                        + "cd \"${1}\"\n"
                        + "if [ ${2: -4} == \".zip\" ];\n"
                        + "then\n"
                        + "unzip ${2} -d ${3} && rm ${2}\n"
                        + "elif [ ${2: -4} == \".rar\" ]; then\n"
                        + "file-roller ${2} -e ${3}  --force && sleep 10 && rm ${2}\n"
                        + "fi");
            }

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static ArrayList<File> getDirsInAssignmentDir() {
        dirsInAssignmentFolder.clear();
        File ass = new File("ASSIGNMENTS");
        File[] files = ass.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                dirsInAssignmentFolder.add(file);
            }
        }
        Collections.sort(dirsInAssignmentFolder);
        return dirsInAssignmentFolder;
    }

    public static void saveSettings() {
        GlobalValues.settings.setShowFeedBackInSeparateWindow(GlobalValues.showFeedBackInSeprateWindow);
        writeObject("app/settings.obj", GlobalValues.settings);
    }

    public static void readSettings() {
        GlobalValues.settings = (SettingsEntry) readObject("app/settings.obj");
        GlobalValues.showFeedBackInSeprateWindow = GlobalValues.settings.isShowFeedBackInSeparateWindow();
    }

    public static void parseFeedbackTemplate() {
        try {
            GlobalValues.feedbackDBArray = new ArrayList<>();
            String templateContent = read(GlobalValues.templateFile);
            FeedBackEntry endSection = new FeedBackEntry(0, 0, 0, 0, "Section End", true, FeedBackEntry.EntryType.SECTION_END, 0);
            GlobalValues.feedbackDBArray.add(endSection);
            GlobalValues.templateFeedback.clear();
            String lines[] = templateContent.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.length() > 0) {
                    if (line.contains("[")) {
                        String marksString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                        String commentString = line.substring(line.indexOf("]") + 1).trim();
                        double maxm = 0;
                        double minm = Double.MIN_VALUE;
                        if (marksString.contains("/")) {
                            String marks[] = marksString.split("/");
                            if (marks.length == 2) {
                                minm = Double.parseDouble(marks[0].trim());
                                maxm = Double.parseDouble(marks[1].trim());
                            } else if (marks.length == 1) {
                                maxm = Double.parseDouble(marks[0].trim());

                            }
                        } else {
                            maxm = Double.parseDouble(marksString.trim());

                        }
                        FeedBackEntry fbe = new FeedBackEntry(i, maxm, minm, 0, commentString, true, FeedBackEntry.EntryType.SECTION_START, 0);
                        GlobalValues.feedbackDBArray.add(fbe);
                        GlobalValues.templateFeedback.add(fbe);
                        FeedBackEntry endSection2 = new FeedBackEntry(i, 0, 0, 0, "Section End", true, FeedBackEntry.EntryType.SECTION_END, 0);
                        GlobalValues.templateFeedback.add(endSection2);

                    }
                }
            }
//            GlobalValues.feedbackDBobject = new JSONObject();
            // GlobalValues.feedbackDBobject.put("DB", GlobalValues.feedbackDBArray);
            writeObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-db.fdb"), GlobalValues.feedbackDBArray);
            writeObject(("FEEDBACK/" + GlobalValues.sessionSettings.getSession_name() + "-feedback-template.obj"), GlobalValues.templateFeedback);

        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
