package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionFinish {
    @Expose
    final public Cursor cursor = new Cursor();
    @Expose
    final public Commit commit = new Commit();

    public static class Commit {
        @Expose
        private String path;
        @Expose
        private String mode = "add";
        @Expose
        @SerializedName("autoRename")
        private boolean autoRename = false;
        @Expose
        private boolean mute = false;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public boolean isAutoRename() {
            return autoRename;
        }

        public void setAutoRename(boolean autoRename) {
            this.autoRename = autoRename;
        }

        public boolean isMute() {
            return mute;
        }

        public void setMute(boolean mute) {
            this.mute = mute;
        }
    }
}
