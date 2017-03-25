/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose FileCollector | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta.extras;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nika
 */
public class FileCollector implements Callable<ArrayList<File>> {

    private ArrayList<File> list;
    private String path;
    private String pattern;
    private PATTERN expectedTo;

    public static enum PATTERN {
        endsWith, contains
    };

    public FileCollector(String path) {
        this.path = path;
    }

    public FileCollector(String path, String pattern, PATTERN expectedTo) {
        this.path = path;
        this.pattern = pattern;
        this.expectedTo = expectedTo;
    }

    @Override
    public ArrayList<File> call() throws Exception {
        Thread.sleep(1000);

        return getJavaFiles(path);

    }

    public ArrayList<File> getJavaFiles(String dir) {
        ArrayList<File> list = new ArrayList<>();
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {

                list.addAll(getJavaFiles(file.getAbsolutePath()));
            } else if (expectedTo == PATTERN.endsWith) {
                if (file.getName().endsWith(pattern)) {
                    list.add(file);
                }
            } else if (expectedTo == PATTERN.contains) {
                if (file.getName().contains(pattern)) {
                    list.add(file);

                }
            }
        }

        return list;
    }

    public static ArrayList<File> callGetFiles(String dir, String pattern, PATTERN expectedTo, ExecutorService executorService) {
        ArrayList<File> list = new ArrayList<>();
        FileCollector jfc = new FileCollector(dir, pattern, expectedTo);
        Future<ArrayList<File>> ts = executorService.submit(jfc);
        try {
            list = ts.get();
//                System.out.println("" + list);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileCollector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(FileCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

}
