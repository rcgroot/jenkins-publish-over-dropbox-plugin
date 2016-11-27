package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetadataRequest {
    @Expose
    private String path;
    @Expose
    @SerializedName("include_media_info")
    private boolean includeMediaInfo = false;
    @Expose
    @SerializedName("include_deleted")
    private boolean includeDeleted = false;
    @Expose
    @SerializedName("include_has_explicit_shared_members")
    private boolean includeHasExplicitSharedMembers = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIncludeMediaInfo() {
        return includeMediaInfo;
    }

    public void setIncludeMediaInfo(boolean includeMediaInfo) {
        this.includeMediaInfo = includeMediaInfo;
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public boolean isIncludeHasExplicitSharedMembers() {
        return includeHasExplicitSharedMembers;
    }

    public void setIncludeHasExplicitSharedMembers(boolean includeHasExplicitSharedMembers) {
        this.includeHasExplicitSharedMembers = includeHasExplicitSharedMembers;
    }
}
