/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.tools;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author nika
 */
public class SCPFrom {

    private String hostName;
    private String userName;
    private String password;
    private int port;

    private String remoteFile, localFile;

    public SCPFrom(String hostName, String userName, String password, int port, String remoteFile, String localFile) {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.remoteFile = remoteFile;
        this.localFile = localFile;
    }

    public void execute() throws JSchException, IOException {
        FileOutputStream fos = null;
        String prefix = null;
        if (new File(localFile).isDirectory()) {
            prefix = localFile + File.separator;
        }

        JSch jsch = new JSch();
        jsch.setKnownHosts("/home/" + System.getProperty("user.name") + "/.ssh/known_hosts");

        Session session = jsch.getSession(userName, hostName, port);
        session.setPassword(password);
        session.connect();

        // exec 'scp -f rfile' remotely
        String command = "scp -f " + remoteFile;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkAck(in);
            if (c != 'C') {
                break;
            }

            // read '0644 '
            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ') {
                    break;
                }
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }

            String file = null;
            for (int i = 0;; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            //System.out.println("filesize="+filesize+", file="+file);
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            fos = new FileOutputStream(prefix == null ? localFile : prefix + file);
            int foo;
            while (true) {
                if (buf.length < filesize) {
                    foo = buf.length;
                } else {
                    foo = (int) filesize;
                }
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // error 
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L) {
                    break;
                }
            }
            fos.close();
            fos = null;

            if (checkAck(in) != 0) {
//                System.exit(0);
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
        }

        session.disconnect();
        System.out.println("SCP " + localFile + " FROM " + remoteFile);
    }

    public static void main(String[] arg) {

    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
