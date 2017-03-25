/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
