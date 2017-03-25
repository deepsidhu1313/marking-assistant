/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.tools;

/**
 *
 * @author nika
 */
import com.jcraft.jsch.*;
import in.co.s13.marking.assistant.ui.MainWindow;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.Base64;
import java.util.logging.Level;

public class RemoteExec {

    private String hostName;
    private String userName;
    private String password;
    private int port;
    private String command;
    private String path, type;

    public RemoteExec(String hostName, String userName, String password, int port, String command, String path, String type) {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.command = command;
        this.path = path;
        this.type = type;
    }

    public void execute(int number) throws JSchException, IOException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("/home/" + System.getProperty("user.name") + "/.ssh/known_hosts");
        //System.out.println("/home/" + System.getProperty("user.name") + "/.ssh/known_hosts");
        Session session = jsch.getSession(userName, hostName, port);

        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);

//session.setUserInfo();
        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // X Forwarding
        // channel.setXForwarding(true);
        //channel.setInputStream(System.in);
        channel.setInputStream(null);

        //channel.setOutputStream(System.out);
        PrintWriter outputWriter = new PrintWriter(path + "/" + type + "-out-" + number + ".log", "UTF-8");

        FileOutputStream fos = new FileOutputStream(path + "/" + type + "-err-" + number + ".log");
        ((ChannelExec) channel).setErrStream(fos);
        //((ChannelExec) channel).setErrStream(System.err);

        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                String out = new String(tmp, 0, i);
                System.out.print(out);
                outputWriter.append(out);
                MainWindow.appendLogToLogArea(out);
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        session.disconnect();
        outputWriter.close();
        fos.close();
    }

    public static void main(String[] arg) {
        RemoteExec rex = new RemoteExec("rodents.cs.umanitoba.ca", "sidhusn", "7791197", 22, "ls -al", "app/logs/", "cmd");
        try {
            rex.execute(1);

        } catch (JSchException ex) {
            java.util.logging.Logger.getLogger(RemoteExec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(RemoteExec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
