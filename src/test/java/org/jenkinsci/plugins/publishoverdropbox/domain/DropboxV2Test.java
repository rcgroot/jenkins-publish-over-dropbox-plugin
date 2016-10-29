package org.jenkinsci.plugins.publishoverdropbox.domain;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.FolderMetadata;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.RestException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assume.assumeTrue;

/**
 * Integration tests for the Dropbox V2 API. Require a manual generated and inserted access token to run.
 */
public class DropboxV2Test {

    private final static String accessToken = "";
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
    public void testMakeDirectory() throws RestException {
        // Act
        FolderMetadata dir = sut.makeDirectory("tests");
        // Assert
        assertThat(dir, notNullValue());
        assertThat(dir.getName(), is("tests"));
    }
}