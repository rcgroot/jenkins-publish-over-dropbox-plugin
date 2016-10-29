package org.jenkinsci.plugins.publishoverdropbox.domain.model;

import com.google.gson.Gson;
import org.jenkinsci.plugins.publishoverdropbox.domain.DropboxV2;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FileMetadataTest {

    private final String file = "{\n" +
            "    \".tag\": \"file\",\n" +
            "    \"name\": \"Prime_Numbers.txt\",\n" +
            "    \"id\": \"id:a4ayc_80_OEAAAAAAAAAXw\",\n" +
            "    \"client_modified\": \"2015-05-12T15:50:38Z\",\n" +
            "    \"server_modified\": \"2015-05-12T15:50:38Z\",\n" +
            "    \"rev\": \"a1c10ce0dd78\",\n" +
            "    \"size\": 7212,\n" +
            "    \"path_lower\": \"/homework/math/prime_numbers.txt\",\n" +
            "    \"path_display\": \"/Homework/math/Prime_Numbers.txt\",\n" +
            "    \"sharing_info\": {\n" +
            "        \"read_only\": true,\n" +
            "        \"parent_shared_folder_id\": \"84528192421\",\n" +
            "        \"modified_by\": \"dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc\"\n" +
            "    },\n" +
            "    \"property_groups\": [\n" +
            "        {\n" +
            "            \"template_id\": \"ptid:1a5n2i6d3OYEAAAAAAAAAYa\",\n" +
            "            \"fields\": [\n" +
            "                {\n" +
            "                    \"name\": \"Security Policy\",\n" +
            "                    \"value\": \"Confidential\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"has_explicit_shared_members\": false\n" +
            "}";
    private Gson gson;

    @Before
    public void setUp() {
        gson = new DropboxV2("fake").gson;
    }

    @Test
    public void testExampleJson() {
        // Arrange
        Reader reader = new StringReader(file);
        // Act
        FileMetadata model = gson.fromJson(reader, FileMetadata.class);
        // Assert
        assertThat(model.getServerModified(), equalTo("2015-05-12T15:50:38Z"));
        assertThat(model.getName(), equalTo("Prime_Numbers.txt"));
        assertThat(model.getPathLower(), equalTo("/homework/math/prime_numbers.txt"));
    }
}