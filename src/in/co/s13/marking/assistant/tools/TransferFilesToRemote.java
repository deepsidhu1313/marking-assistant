/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
