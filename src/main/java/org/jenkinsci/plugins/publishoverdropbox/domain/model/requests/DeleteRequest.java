package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;


import com.google.gson.annotations.Expose;

public class DeleteRequest {

    @Expose
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
