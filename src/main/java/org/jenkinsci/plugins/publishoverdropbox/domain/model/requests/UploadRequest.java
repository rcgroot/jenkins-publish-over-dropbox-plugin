package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;

import static org.jenkinsci.plugins.publishoverdropbox.domain.model.requests.UploadRequest.WriteMode.OVERWRITE;

public class UploadRequest {

    public interface WriteMode {

        String ADD = "add";
        String OVERWRITE = "overwrite";
        String UPDATE = "update";
    }

    @Expose
    private String path;

    @Expose
    private String mode = OVERWRITE;
    @Expose
    private boolean autorename = true;
    @Expose
    private boolean mute = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
