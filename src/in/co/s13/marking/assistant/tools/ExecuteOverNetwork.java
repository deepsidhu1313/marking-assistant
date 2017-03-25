/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.tools;

import com.jcraft.jsch.JSchException;
import in.co.s13.marking.assistant.meta.GlobalValues;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nika
 */
public class ExecuteOverNetwork {

    ExecutorService executorService;
    int i;
    String type;
    String path;
    String commands[];

    public ExecuteOverNetwork(ExecutorService executorService, int i, String type, String path, String... commands) {
        this.executorService = executorService;
        this.i = i;
        this.type = type;
        this.path = path;
        this.commands = commands;
    }

    public void execute() {
        FutureTask ft = new FutureTask(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                StringBuilder command = new StringBuilder();
                for (int j = 0; j < commands.length; j++) {
                    String command1 = commands[j];
                    command.append(" " + command1);
                }
                RemoteExec rexec = new RemoteExec(GlobalValues.sessionSettings.getRemoteHost(), GlobalValues.sessionSettings.getRemoteUserName(),
                        GlobalValues.sessionSettings.getRemotePassword(), GlobalValues.sessionSettings.getRemotePort(), command.toString(), path, type);
                try {
                    rexec.execute(i);
                } catch (JSchException ex) {
                    Logger.getLogger(ExecuteOverNetwork.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ExecuteOverNetwork.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 1;
            }
        });
        executorService.submit(ft);
        
    }

}
