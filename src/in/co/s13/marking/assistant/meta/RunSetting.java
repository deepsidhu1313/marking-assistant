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
public class RunSetting implements Serializable {

    private int id;
    private String location, runCommand, OS, inputSequence;
    private ArrayList<File> runReqFiles;
    private File sampleOutPutFile;

    public RunSetting(int id, String location, String runCommand, String OS, ArrayList<File> runReqFiles, File sampleOutPutFile, String inputSequence) {
        this.id = id;
        this.location = location;
        this.runCommand = runCommand;
        this.OS = OS;
        this.runReqFiles = runReqFiles;
        this.sampleOutPutFile = sampleOutPutFile;
        this.inputSequence = inputSequence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public ArrayList<File> getRunReqFiles() {
        return runReqFiles;
    }

    public void setRunReqFiles(ArrayList<File> runReqFiles) {
        this.runReqFiles = runReqFiles;
    }

    public File getSampleOutPutFile() {
        return sampleOutPutFile;
    }

    public void setSampleOutPutFile(File sampleOutPutFile) {
        this.sampleOutPutFile = sampleOutPutFile;
    }

    public String getInputSequence() {
        return inputSequence;
    }

    public void setInputSequence(String inputSequence) {
        this.inputSequence = inputSequence;
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
        final RunSetting other = (RunSetting) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (!Objects.equals(this.runCommand, other.runCommand)) {
            return false;
        }
        if (!Objects.equals(this.OS, other.OS)) {
            return false;
        }
        if (!Objects.equals(this.runReqFiles, other.runReqFiles)) {
            return false;
        }
        if (!Objects.equals(this.sampleOutPutFile, other.sampleOutPutFile)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + "id:" + id + ", location:" + location + ", runCommand:" + runCommand + ", OS:" + OS + ", runReqFiles:" + runReqFiles + ", sampleOutPutFile:" + sampleOutPutFile + ",inputSequence:" + inputSequence + '}';
    }

    public RunSetting(JSONObject obj) {
        id = obj.getInt("id");
        location = obj.getString("location");
        runCommand = obj.getString("runCommand");
        OS = obj.getString("OS");
        runReqFiles = (ArrayList<File>) obj.get("runReqFiles");
        sampleOutPutFile = (File) obj.get("sampleOutPutFile");
        inputSequence = obj.getString("inputSequence:");
    }

}
