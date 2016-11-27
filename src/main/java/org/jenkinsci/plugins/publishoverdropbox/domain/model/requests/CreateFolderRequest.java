package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateFolderRequest {
    @Expose
    private String path;
    @Expose
    @SerializedName("autorename")
    private boolean isAutoRename = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAutoRename() {
        return isAutoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.isAutoRename = autoRename;
    }
}
