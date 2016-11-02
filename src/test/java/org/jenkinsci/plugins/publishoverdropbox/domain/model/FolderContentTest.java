package org.jenkinsci.plugins.publishoverdropbox.domain.model;

import com.google.gson.Gson;
import org.jenkinsci.plugins.publishoverdropbox.domain.DropboxV2;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class FolderContentTest {

    private final String example = "{\n" +
            "    \"entries\": [\n" +
            "        {\n" +
            "            \".tag\": \"file\",\n" +
            "            \"name\": \"Prime_Numbers.txt\",\n" +
            "            \"id\": \"id:a4ayc_80_OEAAAAAAAAAXw\",\n" +
            "            \"client_modified\": \"2015-05-12T15:50:38Z\",\n" +
            "            \"server_modified\": \"2015-05-12T15:50:38Z\",\n" +
            "            \"rev\": \"a1c10ce0dd78\",\n" +
            "            \"size\": 7212,\n" +
            "            \"path_lower\": \"/homework/math/prime_numbers.txt\",\n" +
            "            \"path_display\": \"/Homework/math/Prime_Numbers.txt\",\n" +
            "            \"sharing_info\": {\n" +
            "                \"read_only\": true,\n" +
            "                \"parent_shared_folder_id\": \"84528192421\",\n" +
            "                \"modified_by\": \"dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc\"\n" +
            "            },\n" +
            "            \"property_groups\": [\n" +
            "                {\n" +
            "                    \"template_id\": \"ptid:1a5n2i6d3OYEAAAAAAAAAYa\",\n" +
            "                    \"fields\": [\n" +
            "                        {\n" +
            "                            \"name\": \"Security Policy\",\n" +
            "                            \"value\": \"Confidential\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ],\n" +
            "            \"has_explicit_shared_members\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \".tag\": \"folder\",\n" +
            "            \"name\": \"math\",\n" +
            "            \"id\": \"id:a4ayc_80_OEAAAAAAAAAXz\",\n" +
            "            \"path_lower\": \"/homework/math\",\n" +
            "            \"path_display\": \"/Homework/math\",\n" +
            "            \"sharing_info\": {\n" +
            "                \"read_only\": false,\n" +
            "                \"parent_shared_folder_id\": \"84528192421\",\n" +
            "                \"traverse_only\": false,\n" +
            "                \"no_access\": false\n" +
            "            },\n" +
            "            \"property_groups\": [\n" +
            "                {\n" +
            "                    \"template_id\": \"ptid:1a5n2i6d3OYEAAAAAAAAAYa\",\n" +
            "                    \"fields\": [\n" +
            "                        {\n" +
            "                            \"name\": \"Security Policy\",\n" +
            "                            \"value\": \"Confidential\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"cursor\": \"ZtkX9_EHj3x7PMkVuFIhwKYXEpwpLwyxp9vMKomUhllil9q7eWiAu\",\n" +
            "    \"has_more\": false\n" +
            "}";

    private Gson gson;

    @Before
    public void setUp() {
        gson = DropboxV2.createGson();
    }

    @Test
    public void testExampleJson() {
        // Arrange
        Reader reader = new StringReader(example);
        // Act
        FolderContent model = gson.fromJson(reader, FolderContent.class);
        // Assert
        assertThat(model.getEntries().size(), is(2));
        assertThat(model.getEntries().get(0), instanceOf(FileMetadata.class));
        assertThat(model.getEntries().get(1), instanceOf(FolderMetadata.class));
    }
}