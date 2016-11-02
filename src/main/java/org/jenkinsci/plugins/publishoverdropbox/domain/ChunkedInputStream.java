package org.jenkinsci.plugins.publishoverdropbox.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Limits access to a chunk of given size of an existing InputStream
 */
public class ChunkedInputStream extends InputStream {

    private final InputStream source;
    private final long chunkSize;
    private long progress;

    public ChunkedInputStream(InputStream source, long chunkSize) {
        this.source = source;
        this.chunkSize = chunkSize;
        this.progress = 0;
    }

    @Override
    public int read() throws IOException {
        int read = -1;
        if (progress < chunkSize) {
            progress++;
            read = source.read();
        }
        return read;
    }

    @Override
    public int available() throws IOException {
        int limitTo = (int) (chunkSize - progress);
        return Math.min(limitTo, super.available());
    }
}
