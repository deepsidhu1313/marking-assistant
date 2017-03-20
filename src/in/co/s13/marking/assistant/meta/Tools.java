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
import java.util.Collections;

/**
 *
 * @author nika
 */
public class Tools {

    public static void write(File f, String text) {
        try (FileWriter fw = new FileWriter(f);
                PrintWriter pw = new PrintWriter(fw)) {
            pw.print(text);
            pw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }

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
    public static void runFiles(ExecutorService executorService, String foldername, String main, int i) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", "exec.sh", foldername, main);//, "inputfilename" + i);
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

}
