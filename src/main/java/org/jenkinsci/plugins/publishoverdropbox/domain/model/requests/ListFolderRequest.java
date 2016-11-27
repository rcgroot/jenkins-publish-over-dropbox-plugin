package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListFolderRequest {
    @Expose
    private String path = "";
    @Expose
    private boolean recursive = false;
    @Expose
    @SerializedName("include_media_info")
    private boolean includeMediaInfo = false;
    @Expose
    @SerializedName("include_deleted")
    private boolean includeDeleted = false;
    @Expose
    @SerializedName("include_has_explicit_shared_members")
    private boolean includeHasExplicitShareMembers = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
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

    public boolean isIncludeHasExplicitShareMembers() {
        return includeHasExplicitShareMembers;
    }

    public void setIncludeHasExplicitShareMembers(boolean includeHasExplicitShareMembers) {
        this.includeHasExplicitShareMembers = includeHasExplicitShareMembers;
    }
}
