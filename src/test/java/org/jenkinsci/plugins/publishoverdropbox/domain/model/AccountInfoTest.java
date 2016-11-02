package org.jenkinsci.plugins.publishoverdropbox.domain.model;

import com.google.gson.Gson;
import org.jenkinsci.plugins.publishoverdropbox.domain.DropboxV2;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AccountInfoTest {

    private final String personalAccountInfo = "{\n" +
            "    \"account_id\": \"dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc\",\n" +
            "    \"name\": {\n" +
            "        \"given_name\": \"Franz\",\n" +
            "        \"surname\": \"Ferdinand\",\n" +
            "        \"familiar_name\": \"Franz\",\n" +
            "        \"display_name\": \"Franz Ferdinand (Personal)\",\n" +
            "        \"abbreviated_name\": \"FF\"\n" +
            "    },\n" +
            "    \"email\": \"franz@gmail.com\",\n" +
            "    \"email_verified\": false,\n" +
            "    \"disabled\": false,\n" +
            "    \"locale\": \"en\",\n" +
            "    \"referral_link\": \"https://db.tt/ZITNuhtI\",\n" +
            "    \"is_paired\": false,\n" +
            "    \"account_type\": {\n" +
            "        \".tag\": \"basic\"\n" +
            "    },\n" +
            "    \"profile_photo_url\": \"https://dl-web.dropbox.com/account_photo/get/dbid%3AAAH4f99T0taONIb-OurWxbNQ6ywGRopQngc?vers=1453416673259&size=128x128\",\n" +
            "    \"country\": \"US\"\n" +
            "}\n";
    private Gson gson;

    @Before
    public void setUp() {
        gson = DropboxV2.createGson();
    }

    @Test
    public void testExampleJson() {
        // Arrange
        Reader reader = new StringReader(personalAccountInfo);
        // Act
        AccountInfo model = gson.fromJson(reader, AccountInfo.class);
        // Assert
        assertThat(model.getAccountId(), equalTo("dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc"));
        assertThat(model.isDisabled(), equalTo(false));
    }
}