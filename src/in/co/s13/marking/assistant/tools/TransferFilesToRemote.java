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
package in.co.s13.marking.assistant.tools;

import com.jcraft.jsch.JSchException;
import in.co.s13.marking.assistant.meta.GlobalValues;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nika
 */
public class TransferFilesToRemote {
    
    File dirOrFile;
    ExecutorService executorService;
    
    public TransferFilesToRemote(File dirOrFile, ExecutorService executorService) {
        this.dirOrFile = dirOrFile;
        this.executorService = executorService;
    }
    
    public void transfer() {
        rscp(dirOrFile);
    }
    
    private void scp(File file) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SCPTo scp = new SCPTo(GlobalValues.sessionSettings.getRemoteHost(), GlobalValues.sessionSettings.getRemoteUserName(),
                            GlobalValues.sessionSettings.getRemotePassword(), GlobalValues.sessionSettings.getRemotePort(), file.getAbsolutePath(), "MarkingAssistant/" + file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(System.getProperty("user.dir")) + System.getProperty("user.dir").length() + 1));
                    scp.execute();
                } catch (JSchException ex) {
                    Logger.getLogger(TransferFilesToRemote.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TransferFilesToRemote.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.setDaemon(true);
        executorService.submit(t);
    }
    
    private void mkdir(File file) {
        ExecuteOverNetwork execOvrNet = new ExecuteOverNetwork(executorService, 1, "mkdir", "app/logs/", " mkdir  MarkingAssistant/" + file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(System.getProperty("user.dir")) + System.getProperty("user.dir").length() + 1));
        execOvrNet.execute();
        
    }
    
    private void rscp(File dir) {
        if (dir.isDirectory()) {
            File files[] = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    mkdir(file);
                    rscp(file);
                } else {
                    
                    scp(file);
                }
            }
        } else {
            
            scp(dir);
        }
    }
    
}
