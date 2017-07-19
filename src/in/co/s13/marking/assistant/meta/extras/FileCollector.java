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

        return getFiles(path);

    }

    public ArrayList<File> getFiles(String dir) {
        ArrayList<File> list = new ArrayList<>();
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {

                list.addAll(getFiles(file.getAbsolutePath()));
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
