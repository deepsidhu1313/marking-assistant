/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.marking.assistant.meta;

import java.io.Serializable;

/**
 *
 * @author nika
 */
public class SettingsEntry implements Serializable{
    private boolean showFeedBackInSeparateWindow;

    public SettingsEntry() {
    }

    public SettingsEntry(boolean showFeedBackInSeparateWindow) {
        this.showFeedBackInSeparateWindow = showFeedBackInSeparateWindow;
    }

    public boolean isShowFeedBackInSeparateWindow() {
        return showFeedBackInSeparateWindow;
    }

    public void setShowFeedBackInSeparateWindow(boolean showFeedBackInSeparateWindow) {
        this.showFeedBackInSeparateWindow = showFeedBackInSeparateWindow;
    }

    @Override
    public String toString() {
        return "{" + "showFeedBackInSeparateWindow:" + showFeedBackInSeparateWindow + '}';
    }
    
    
}
