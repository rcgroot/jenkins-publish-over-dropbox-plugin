package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;

public class SessionFinish {
    @Expose
    final public Cursor cursor = new Cursor();
    @Expose
    final public Commit commit = new Commit();

    public class Commit {
        @Expose
        private String path;
        @Expose
        private String mode = "add";
        @Expose
        private boolean autorename = false;
        @Expose
        private boolean mute = false;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
