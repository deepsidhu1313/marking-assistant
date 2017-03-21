/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class SessionSettings implements Serializable {

    private String session_name, lastStudentMarked="\t", CurrentStudent="\t";
    private int numberOfCompilerSettings, numberOfRunSettings;
    private ArrayList<CompilerSetting> compilerSettings;
    private ArrayList<RunSetting> runSettings;
    private String feedbackTemplate;

    public SessionSettings(String session_name, String lastStudentMarked, String CurrentStudent, int numberOfCompilerSettings, int numberOfRunSettings, ArrayList<CompilerSetting> compilerSettings, ArrayList<RunSetting> runSettings, String feedbackTemplate) {
        this.session_name = session_name;
        this.lastStudentMarked = lastStudentMarked;
        this.CurrentStudent = CurrentStudent;
        this.numberOfCompilerSettings = numberOfCompilerSettings;
        this.numberOfRunSettings = numberOfRunSettings;
        this.compilerSettings = compilerSettings;
        this.runSettings = runSettings;
        this.feedbackTemplate = feedbackTemplate;
    }

    public String getSession_name() {
        return session_name;
    }

    public void setSession_name(String session_name) {
        this.session_name = session_name;
    }

    public String getLastStudentMarked() {
        return lastStudentMarked;
    }

    public void setLastStudentMarked(String lastStudentMarked) {
        this.lastStudentMarked = lastStudentMarked;
    }

    public String getCurrentStudent() {
        return CurrentStudent;
    }

    public void setCurrentStudent(String CurrentStudent) {
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
        if (!Objects.equals(this.compilerSettings, other.compilerSettings)) {
            return false;
        }
        if (!Objects.equals(this.runSettings, other.runSettings)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + "session_name:" + session_name + ", lastStudentMarked:" + lastStudentMarked + ", CurrentStudent:" + CurrentStudent + ", numberOfCompilerSettings:" + numberOfCompilerSettings + ", numberOfRunSettings:" + numberOfRunSettings + ", compilerSettings:" + compilerSettings + ", runSettings:" + runSettings + ", feedbackTemplate:" + feedbackTemplate + '}';
    }

    public SessionSettings(JSONObject obj) {
        session_name = obj.getString("session_name");
        lastStudentMarked = obj.getString("lastStudentMarked");
        CurrentStudent = obj.getString("CurrentStudent");
        numberOfCompilerSettings = obj.getInt("numberOfCompilerSettings");
        numberOfRunSettings = obj.getInt("numberOfRunSettings");
        compilerSettings = (ArrayList<CompilerSetting>) obj.get("compilerSettings");
        runSettings = (ArrayList<RunSetting>) obj.get("runSettings");
        feedbackTemplate = obj.getString("feedbackTemplate");
    }

}
