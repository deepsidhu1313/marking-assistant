/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class SessionSettings implements Serializable {

    private String session_name, remoteHost, remoteUserName, remotePassword;
    private int numberOfCompilerSettings, numberOfRunSettings, remotePort;
    private ArrayList<CompilerSetting> compilerSettings;
    private ArrayList<RunSetting> runSettings;
    private String feedbackTemplate;
    private File lastStudentMarked, CurrentStudent;
    private String remoteOS;

    public SessionSettings(String session_name, File lastStudentMarked, File CurrentStudent, int numberOfCompilerSettings, int numberOfRunSettings, ArrayList<CompilerSetting> compilerSettings, ArrayList<RunSetting> runSettings, String feedbackTemplate, String remoteHost, int remotePort, String remoteUserName, String remotePassword, String remoteOS) {
        this.session_name = session_name;
        this.lastStudentMarked = lastStudentMarked;
        this.CurrentStudent = CurrentStudent;
        this.numberOfCompilerSettings = numberOfCompilerSettings;
        this.numberOfRunSettings = numberOfRunSettings;
        this.compilerSettings = compilerSettings;
        this.runSettings = runSettings;
        this.feedbackTemplate = feedbackTemplate;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.remoteUserName = remoteUserName;
        this.remotePassword = remotePassword;
        this.remoteOS = remoteOS;
    }

    public String getSession_name() {
        return session_name;
    }

    public void setSession_name(String session_name) {
        this.session_name = session_name;
    }

    public File getLastStudentMarked() {
        return lastStudentMarked;
    }

    public void setLastStudentMarked(File lastStudentMarked) {
        this.lastStudentMarked = lastStudentMarked;
    }

    public File getCurrentStudent() {
        return CurrentStudent;
    }

    public void setCurrentStudent(File CurrentStudent) {
        this.CurrentStudent = CurrentStudent;
    }

    public int getNumberOfCompilerSettings() {
        return numberOfCompilerSettings;
    }

    public void setNumberOfCompilerSettings(int numberOfCompilerSettings) {
        this.numberOfCompilerSettings = numberOfCompilerSettings;
    }

    public int getNumberOfRunSettings() {
        return numberOfRunSettings;
    }

    public void setNumberOfRunSettings(int numberOfRunSettings) {
        this.numberOfRunSettings = numberOfRunSettings;
    }

    public ArrayList<CompilerSetting> getCompilerSettings() {
        return compilerSettings;
    }

    public void setCompilerSettings(ArrayList<CompilerSetting> compilerSettings) {
        this.compilerSettings = compilerSettings;
    }

    public ArrayList<RunSetting> getRunSettings() {
        return runSettings;
    }

    public void setRunSettings(ArrayList<RunSetting> runSettings) {
        this.runSettings = runSettings;
    }

    public String getFeedbackTemplate() {
        return feedbackTemplate;
    }

    public void setFeedbackTemplate(String feedbackTemplate) {
        this.feedbackTemplate = feedbackTemplate;
    }

    public String getRemoteUserName() {
        return remoteUserName;
    }

    public void setRemoteUserName(String remoteUserName) {
        this.remoteUserName = remoteUserName;
    }

    public String getRemotePassword() {
        return remotePassword;
    }

    public void setRemotePassword(String remotePassword) {
        this.remotePassword = remotePassword;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteOS() {
        return remoteOS;
    }

    public void setRemoteOS(String remoteOS) {
        this.remoteOS = remoteOS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SessionSettings other = (SessionSettings) obj;
        if (this.numberOfCompilerSettings != other.numberOfCompilerSettings) {
            return false;
        }
        if (this.numberOfRunSettings != other.numberOfRunSettings) {
            return false;
        }
        if (!Objects.equals(this.session_name, other.session_name)) {
            return false;
        }
        if (!Objects.equals(this.lastStudentMarked, other.lastStudentMarked)) {
            return false;
        }
        if (!Objects.equals(this.CurrentStudent, other.CurrentStudent)) {
            return false;
        }
        if (!Objects.equals(this.remoteUserName, other.remoteUserName)) {
            return false;
        }
        if (!Objects.equals(this.remotePassword, other.remotePassword)) {
            return false;
        }
        if (!Objects.equals(this.feedbackTemplate, other.feedbackTemplate)) {
            return false;
        }
        if (!Objects.equals(this.compilerSettings, other.compilerSettings)) {
            return false;
        }
        if (!Objects.equals(this.runSettings, other.runSettings)) {
            return false;
        }
        if (!Objects.equals(this.remoteHost, other.remoteHost)) {
            return false;
        }
        if (this.remotePort != other.remotePort) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + "session_name:" + session_name + ", lastStudentMarked:" + lastStudentMarked.getAbsolutePath() + ", CurrentStudent:" + CurrentStudent.getAbsolutePath() + ", numberOfCompilerSettings:" + numberOfCompilerSettings + ", numberOfRunSettings:" + numberOfRunSettings + ", compilerSettings:" + compilerSettings + ", runSettings:" + runSettings + ", feedbackTemplate:" + feedbackTemplate + ", remoteHost:" + remoteHost + ", remotePort:" + remotePort + ", remoteUserName:" + remoteUserName + ", remotePassword:" + remotePassword + ",remoteOS:" + remoteOS + '}';
    }

    public SessionSettings(JSONObject obj) {
        session_name = obj.getString("session_name");
        lastStudentMarked = new File(obj.getString("lastStudentMarked"));
        CurrentStudent = new File(obj.getString("CurrentStudent"));
        numberOfCompilerSettings = obj.getInt("numberOfCompilerSettings");
        numberOfRunSettings = obj.getInt("numberOfRunSettings");
        compilerSettings = (ArrayList<CompilerSetting>) obj.get("compilerSettings");
        runSettings = (ArrayList<RunSetting>) obj.get("runSettings");
        feedbackTemplate = obj.getString("feedbackTemplate");
        remoteHost = obj.getString("remoteHost");
        remotePort = obj.getInt("remotePort");
        remoteUserName = obj.getString("remoteUserName");
        remotePassword = obj.getString("remotePassword");
        remoteOS = obj.getString("remoteOS");
    }

}
