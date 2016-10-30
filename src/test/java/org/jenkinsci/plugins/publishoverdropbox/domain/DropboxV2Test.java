package org.jenkinsci.plugins.publishoverdropbox.domain;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.FolderMetadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.Metadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.RestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assume.assumeTrue;

/**
 * Integration tests for the Dropbox V2 API. Require a manual generated and inserted access token to run.
 */
public class DropboxV2Test {

    private final static String accessToken = "ZPlLplRir6wAAAAAAAACVOBl6XfLjwDyYSqZJ9CEqeoBCM7gSRJyHGijhBYdJOmu";
    private DropboxV2 sut;

    @Before
    public void setUp() throws IOException {
        assumeTrue(StringUtils.isNotEmpty(accessToken));
        sut = new DropboxV2(accessToken);
        boolean exists = sut.changeWorkingDirectory("/tests");
        if (exists) {
            sut.delete(sut.getWorkingFolder());
        }
    }

    @After
    public void tearDown() throws RestException {
        boolean exists = sut.changeWorkingDirectory("/tests");
        if (exists) {
            sut.delete(sut.getWorkingFolder());
        }
    }

    @Test
    public void setSmallTimeout() throws Exception {
        // Act
        sut.setTimeout(213);
        // Assert
        assertThat(sut.getTimeout(), is(-1));
    }

    @Test
    public void setBigTimeout() throws Exception {
        // Act
        sut.setTimeout(60001);
        // Assert
        assertThat(sut.getTimeout(), is(60001));
    }

    @Test
    public void testStartsDisconnected() {
        // Assert
        assertThat(sut.isConnected(), is(false));
    }

    @Test
    public void testCanConnect() throws IOException {
        // Act
        sut.connect();
        // Assert
        assertThat(sut.isConnected(), is(true));
    }

    @Test
    public void testCanDisconnect() throws IOException {
        // Arrange
        sut.connect();
        // Act
        sut.disconnect();
        // Assert
        assertThat(sut.isConnected(), is(false));
    }

    @Test
    public void testMakeDirectory() throws RestException {
        // Act
        FolderMetadata dir = sut.makeDirectory("tests");
        // Assert
        assertThat(dir, notNullValue());
        assertThat(dir.getName(), is("tests"));
    }

    @Test
    public void testMakeExistingDirectory() throws RestException {
        // Arrange
        sut.makeDirectory("tests");
        // Act
        FolderMetadata dir = sut.makeDirectory("tests");
        // Assert
        assertThat(dir, notNullValue());
        assertThat(dir.getName(), is("tests"));
    }

    @Test
    public void testChangeWorkingDirectory() throws RestException {
        // Arrange
        sut.makeDirectory("tests");
        // Act
        sut.changeWorkingDirectory("tests");
        // Assert
        assertThat(sut.getWorkingFolder().getPathLower(), is("/tests"));
    }

    @Test
    public void testStoreSmallFile() throws RestException, UnsupportedEncodingException {
        // Arrange
        sut.makeDirectory("tests");
        sut.changeWorkingDirectory("tests");
        final byte[] bytes = "Hello world".getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // Act
        sut.storeFile("simplefile.txt", inputStream, bytes.length);
        // Assert
        Metadata metaData = sut.retrieveMetaData("/tests/simplefile.txt");
        assertThat(metaData.getName(), is("simplefile.txt"));
        assertThat(metaData.getPathLower(), is("/tests/simplefile.txt"));
        assertThat(metaData.getSize(), is((long) bytes.length));
    }
}