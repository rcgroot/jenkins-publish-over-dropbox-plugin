package org.jenkinsci.plugins.publishoverdropbox.domain;

import org.jenkinsci.plugins.publishoverdropbox.domain.model.FolderMetadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.RestException;

import java.io.IOException;
import java.io.InputStream;

public interface DropboxAdapter {
    void setTimeout(int timeout);

    int getTimeout();

    boolean isConnected();

    boolean connect() throws IOException;

    boolean disconnect() throws IOException;

    FolderMetadata makeDirectory(String directory) throws RestException;

    boolean changeWorkingDirectory(String path) throws RestException;

    void storeFile(String name, InputStream content, long length) throws RestException;

    void 2() throws IOException;

    void pruneFolder(String path, int pruneRootDays) throws RestException;
}
