package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;

public class CreateFolderRequest {
    @Expose
    private String path;
    @Expose
    private boolean autorename = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
