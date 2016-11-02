package org.jenkinsci.plugins.publishoverdropbox.domain.model.requests;

import com.google.gson.annotations.Expose;

public class SessionAppend {
    @Expose
    final public Cursor cursor = new Cursor();

    @Expose
    private boolean close = false;
}
