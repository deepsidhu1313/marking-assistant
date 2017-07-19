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
public class CompilerSetting implements Serializable{

    private int id;
    private String location, compilerCommand, OS;
    private ArrayList<File> compilerReqFiles;

    public CompilerSetting(int id, String location, String compilerCommand, String OS, ArrayList<File> compilerReqFiles) {
        this.id = id;
        this.location = location;
        this.compilerCommand = compilerCommand;
        this.OS = OS;
        this.compilerReqFiles = compilerReqFiles;
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

    public String getCompilerCommand() {
        return compilerCommand;
    }

    public void setCompilerCommand(String compilerCommand) {
        this.compilerCommand = compilerCommand;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public ArrayList<File> getCompilerReqFiles() {
        return compilerReqFiles;
    }

    public void setCompilerReqFiles(ArrayList<File> compilerReqFiles) {
        this.compilerReqFiles = compilerReqFiles;
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
        final CompilerSetting other = (CompilerSetting) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (!Objects.equals(this.compilerCommand, other.compilerCommand)) {
            return false;
        }
        if (!Objects.equals(this.OS, other.OS)) {
            return false;
        }
        if (!Objects.equals(this.compilerReqFiles, other.compilerReqFiles)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + "id:" + id + ", location:" + location + ", compilerCommand:" + compilerCommand + ", OS:" + OS + ", compilerReqFiles:" + compilerReqFiles + '}';
    }

    public CompilerSetting(JSONObject obj) {
        id = obj.getInt("id");
        location = obj.getString("location");
        compilerCommand = obj.getString("compilerCommand");
        OS = obj.getString("OS");
        compilerReqFiles = (ArrayList<File>) obj.get("compilerReqFiles");

    }

}
