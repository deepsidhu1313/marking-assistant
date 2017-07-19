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
