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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nika
 */
public class SFTP {

    private String hostName;
    private String userName;
    private String password;
    private int port;
    private String localFile, remoteFile;

    public SFTP(String hostName, String userName, String password, int port, String localFile, String remoteFile) {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.localFile = localFile;
        this.remoteFile = remoteFile;
    }

    public void connect() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(userName, hostName, port);
            session.setPassword(password);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp) channel;
            
        } catch (JSchException ex) {
            Logger.getLogger(SFTP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void lsFolderCopy(String sourcePath, String destPath,
            ChannelSftp sftpChannel) throws SftpException, FileNotFoundException {
        File localFile = new File(sourcePath);

        if (localFile.isFile()) {

            //copy if it is a file
            sftpChannel.cd(destPath);

            if (!localFile.getName().startsWith(".")) {
                sftpChannel.put(new FileInputStream(localFile), localFile.getName(), ChannelSftp.OVERWRITE);
            }
        } else {
            System.out.println("inside else " + localFile.getName());
            File[] files = localFile.listFiles();

            if (files != null && files.length > 0 && !localFile.getName().startsWith(".")) {

                sftpChannel.cd(destPath);
                SftpATTRS attrs = null;

                //check if the directory is already existing
                try {
                    attrs = sftpChannel.stat(destPath + "/" + localFile.getName());
                } catch (Exception e) {
                    System.out.println(destPath + "/" + localFile.getName() + " not found");
                }

                //else create a directory   
                if (attrs != null) {
                    System.out.println("Directory exists IsDir=" + attrs.isDir());
                } else {
                    System.out.println("Creating dir " + localFile.getName());
                    sftpChannel.mkdir(localFile.getName());
                }

                //System.out.println("length " + files.length);
                for (int i = 0; i < files.length; i++) {

                    lsFolderCopy(files[i].getAbsolutePath(), destPath + "/" + localFile.getName(), sftpChannel);

                }

            }
        }

    }
}
