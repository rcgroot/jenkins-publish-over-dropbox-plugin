package org.jenkinsci.plugins.publishoverdropbox.domain;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.FolderContent;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.FolderMetadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.Metadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.RestException;
import org.jfree.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assume.assumeTrue;

/**
 * Integration tests for the Dropbox V2 API. Require a manual generated and inserted access token to run.
 */
public class DropboxV2Test {

    private final static String accessToken = "";
    private final static String wrongAccessToken = "wrong";
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
        if (sut == null) {
            return;
        }
        try {
            boolean exists = sut.changeWorkingDirectory("/tests");
            if (exists) {
                sut.delete(sut.getWorkingFolder());
            }
        } catch (Exception e) {
            Log.debug("Teardown failed", e);
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

    @Test
    public void testCleanWorkingFolder() throws RestException, UnsupportedEncodingException {
        // Arrange
        sut.makeDirectory("tests");
        sut.changeWorkingDirectory("tests");
        final byte[] bytes = "Hello world".getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        sut.storeFile("simplefile.txt", inputStream, bytes.length);
        // Act
        sut.cleanWorkingFolder();
        // Assert
        FolderMetadata metaData = (FolderMetadata) sut.retrieveMetaData("/tests");
        assertThat(metaData.getName(), is("tests"));
        FolderContent contents = sut.listFilesOfFolder(metaData);
        assertThat(contents.getEntries().size(), is(0));
    }

    @Test
    public void testUploadTwoChunks() throws RestException, UnsupportedEncodingException {
        // Arrange
        sut.makeDirectory("tests");
        sut.changeWorkingDirectory("tests");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("Hello world");
        }
        final byte[] bytes = sb.toString().getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        sut.chunkSize = (bytes.length / 2) + 1;
        // Act
        sut.storeFile("2chunks-file.txt", inputStream, bytes.length);
        // Assert
        Metadata metaData = sut.retrieveMetaData("/tests/2chunks-file.txt");
        assertThat(metaData.getName(), is("2chunks-file.txt"));
        assertThat(metaData.getPathLower(), is("/tests/2chunks-file.txt"));
        assertThat(metaData.getSize(), is((long) bytes.length));
    }


    @Test
    public void testUploadFourChunks() throws RestException, UnsupportedEncodingException {
        // Arrange
        sut.makeDirectory("tests");
        sut.changeWorkingDirectory("tests");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("Hello world");
        }
        final byte[] bytes = sb.toString().getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        sut.chunkSize = (bytes.length / 3) - 10;
        // Act
        sut.storeFile("2chunks-file.txt", inputStream, bytes.length);
        // Assert
        Metadata metaData = sut.retrieveMetaData("/tests/2chunks-file.txt");
        assertThat(metaData.getName(), is("2chunks-file.txt"));
        assertThat(metaData.getPathLower(), is("/tests/2chunks-file.txt"));
        assertThat(metaData.getSize(), is((long) bytes.length));
    }


    @Test
    public void testPruneFolderLeavesFiles() throws RestException, UnsupportedEncodingException {
        // Arrange
        sut.makeDirectory("tests");
        sut.changeWorkingDirectory("tests");
        final byte[] bytes = "Hello wo§rld".getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        sut.storeFile("simplefile.txt", inputStream, bytes.length);
        // Act
        sut.pruneFolder("/tests", 1);
        // Assert
        FolderMetadata metaData = (FolderMetadata) sut.retrieveMetaData("/tests");
        assertThat(metaData.getName(), is("tests"));
        FolderContent contents = sut.listFilesOfFolder(metaData);
        assertThat(contents.getEntries().size(), is(1));
    }

    @Test
    public void testDateParsing() throws RestException {
        // Act
        Date date = sut.parseDate("2016-11-04T07:42:22Z");
        // Assert
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertThat(cal.get(Calendar.YEAR), is(2016));
        assertThat(cal.get(Calendar.HOUR), is(7));
        assertThat(cal.get(Calendar.SECOND), is(22));
    }

    @Test(expected = RestException.class)
    public void testCantConnectWithWrongToken() throws IOException {
        // Arrange
        sut = new DropboxV2(wrongAccessToken);
        // Act
        sut.connect();
        // Assert
        assertThat(sut.isConnected(), is(false));
    }

}