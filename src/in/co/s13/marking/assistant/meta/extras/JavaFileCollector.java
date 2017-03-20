/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose JavaFileCollector | Templates
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
public class JavaFileCollector implements Callable<ArrayList<File>> {

    private ArrayList<File> list;
    private String path;

    public JavaFileCollector(String path) {
        this.path = path;
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
            } else if (file.getName().endsWith(".java")) {
                list.add(file);
            }
        }

        return list;
    }

    public static ArrayList<File> callGetJavaFiles(String dir, ExecutorService executorService) {
        ArrayList<File> list = new ArrayList<>();
        JavaFileCollector jfc = new JavaFileCollector(dir);
        Future<ArrayList<File>> ts = executorService.submit(jfc);
        try {
            list = ts.get();
//                System.out.println("" + list);
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaFileCollector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(JavaFileCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

}
