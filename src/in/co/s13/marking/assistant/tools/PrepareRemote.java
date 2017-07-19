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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nika
 */
public class PrepareRemote {
    
    public PrepareRemote() {
        try {
            RemoteExec rexec = new RemoteExec(GlobalValues.sessionSettings.getRemoteHost(), GlobalValues.sessionSettings.getRemoteUserName(),
                    GlobalValues.sessionSettings.getRemotePassword(), GlobalValues.sessionSettings.getRemotePort(), "mkdir MarkingAssistant && mkdir MarkingAssistant/ASSIGNMENTS "
                            + " && mkdir MarkingAssistant/SCRIPTS", "app/logs/", "prep-remote");
            rexec.execute(0);
        } catch (JSchException ex) {
            Logger.getLogger(PrepareRemote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PrepareRemote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
