package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;

import static org.jenkinsci.plugins.publishoverdropbox.domain.model.requests.UploadRequest.WriteMode.OVERWRITE;

public class UploadRequest {

    @Expose
    private String path;
    @Expose
    private String mode = OVERWRITE;
    @Expose
    private boolean autorename = true;
    @Expose
    private boolean mute = false;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isAutorename() {
        return autorename;
    }

    public void setAutorename(boolean autorename) {
        this.autorename = autorename;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public interface WriteMode {
        String ADD = "add";
        String OVERWRITE = "overwrite";
        String UPDATE = "update";
    }
}
