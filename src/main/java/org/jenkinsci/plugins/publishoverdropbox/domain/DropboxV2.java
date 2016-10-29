/*
 * The MIT License
 *
 * Copyright (C) 2015 by René de Groot
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.publishoverdropbox.domain;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.publishoverdropbox.DropboxToken;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.*;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.requests.*;
import org.jenkinsci.plugins.publishoverdropbox.gson.RuntimeTypeAdapterFactory;
import org.jenkinsci.plugins.publishoverdropbox.impl.Messages;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DropboxV2 implements DropboxAdapter {
    private static final String URL_TOKEN = "https://api.dropbox.com/oauth2/token";
    private static final String URL_ACCOUNT_INFO = "https://api.dropbox.com/2/users/get_current_account";
    private static final String URL_METADATA = "https://api.dropboxapi.com/2/files/get_metadata";
    private static final String URL_LIST_FOLDER = "https://api.dropboxapi.com/2/files/list_folder";
    private static final String URL_LIST_FOLDER_CONTINUE = "https://api.dropboxapi.com/2/files/list_folder/continue";
    private static final String URL_OPS_DELETE_FILE = "https://api.dropboxapi.com/2/files/delete";
    private static final String URL_CREATE_FOLDER = "https://api.dropboxapi.com/2/files/create_folder";

    private static final String URL_UPLOAD = "https://content.dropboxapi.com/2/files/upload";
    private static final String URL_UPLOAD_START = "https://content.dropboxapi.com/2/files/upload_session/start";
    private static final String URL_UPLOAD_APPEND = "https://content.dropboxapi.com/2/files/upload_session/append_v2";
    private static final String URL_UPLOAD_FINISH = "https://content.dropboxapi.com/2/files/upload_session/finish";
    private static final String PATH_SEPARATOR = "/";
    private static final String VALUE_AUTHORIZATION_CODE = "authorization_code";
    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long FOUR_MEGA_BYTE = 4 * 1024;

    private static final String APPLICATION_JSON = "application/json";
    private AccountInfo userInfo;
    private int timeout = -1;
    private final String accessToken;
    public final Gson gson;

    private FolderMetadata workingFolder;

    public DropboxV2(String accessToken) {
        this.accessToken = accessToken;
        RuntimeTypeAdapterFactory<Metadata> metadataAdapterFactory = RuntimeTypeAdapterFactory
                .of(Metadata.class, ".tag")
                .registerSubtype(FolderMetadata.class, "folder")
                .registerSubtype(FileMetadata.class, "file");
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(metadataAdapterFactory)
                .create();
    }

    /**
     * Change the timeout value, values lower then 1000 are ignored and reset to default.
     *
     * @param timeout timeout in miliseconds
     */
    public void setTimeout(int timeout) {
        if (timeout >= 1000) {
            this.timeout = timeout;
        } else {
            this.timeout = -1;
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean connect() throws IOException {
        userInfo = retrieveAccountInfo();
        return isConnected();
    }

    public boolean isConnected() {
        return !StringUtils.isEmpty(accessToken) && userInfo != null && !userInfo.isDisabled();
    }

    public boolean disconnect() throws IOException {
        userInfo = null;
        return true;
    }

    private AccountInfo retrieveAccountInfo() throws RestException {
        URL url = getUrl(URL_ACCOUNT_INFO);
        JsonObjectRequest<AccountInfo> request = buildRequest(url, AccountInfo.class);

        return request.execute();
    }

    public boolean changeWorkingDirectory(@Nonnull String relativePath) throws RestException {
        boolean hasSuccess = true;
        try {
            if (!StringUtils.isEmpty(relativePath)) {
                final Metadata metadata = retrieveMetaData(relativePath);
                if (metadata.isDir() && metadata instanceof FolderMetadata) {
                    workingFolder = (FolderMetadata) metadata;
                }
            }
        } catch (IOException e) {
            hasSuccess = false;
        }
        return hasSuccess && workingFolder != null && workingFolder.isDir();
    }

    public FolderMetadata makeDirectory(@Nonnull String relative) throws RestException {
        URL url = getUrl(URL_CREATE_FOLDER);
        CreateFolderRequest requestContent = new CreateFolderRequest();
        String absolute = createAbsolutePath(relative);
        requestContent.setPath(absolute);
        JsonObjectRequest<FolderMetadata> request = buildRequestWithContent(url, requestContent, FolderMetadata.class);

        final FolderMetadata folder;
        try {
            folder = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_folder_create(relative), e);
        }

        return folder;
    }

    public void cleanWorkingFolder() throws RestException {
        if (workingFolder.isDir()) {
            FolderContent contents = listFiles(workingFolder);
            String cursor = null;
            do {
                if (cursor != null) {
                    contents = listFiles(cursor);
                }
                for (Metadata entry : contents.getEntries()) {
                    delete(entry);
                }
                if (contents.hasMore()) {
                    cursor = contents.getCursor();
                }
            } while (contents.hasMore());

        } else {
            throw new RestException(Messages.exception_dropbox_folder_delete(workingFolder.getName()));
        }
    }

    void delete(@Nonnull Metadata file) throws RestException {
        URL url = getUrl(URL_OPS_DELETE_FILE);
        DeleteRequest requestContent = new DeleteRequest();
        final String path = file.getPathLower();
        requestContent.setPath(path);
        if (StringUtils.isNotEmpty(path) && !"/".equals(path)) {
            JsonObjectRequest<Metadata> request = buildRequestWithContent(url, requestContent, Metadata.class);
            request.execute();
        } else {
            throw new RestException(Messages.exception_dropbox_folder_delete(path));
        }
    }

    public void pruneFolder(@Nonnull String folderPath, int pruneRootDays) throws RestException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        Date cutoff = new Date(System.currentTimeMillis() - pruneRootDays * MILLISECONDS_PER_DAY);
        Metadata metadata = retrieveMetaData(folderPath);
        if (metadata.isDir() && metadata instanceof FolderMetadata) {
            FolderMetadata folderMetadata = (FolderMetadata) metadata;
            FolderContent contents = listFiles(folderMetadata);
            String cursor = null;
            do {
                if (cursor != null) {
                    contents = listFiles(cursor);
                }
                for (Metadata entry : contents.getEntries()) {
                    if (entry.isFile() && entry instanceof FileMetadata) {
                        FileMetadata fileMetadata = (FileMetadata) entry;
                        String serverModified = "";
                        Date lastModified;
                        try {
                            serverModified = fileMetadata.getServerModified();
                            lastModified = df.parse(serverModified);
                        } catch (ParseException e) {
                            throw new RestException(Messages.exception_dropbox_folder_prunedate(serverModified), e);
                        }
                        if (lastModified.before(cutoff)) {
                            delete(fileMetadata);
                        }
                    }
                }
                if (contents.hasMore()) {
                    cursor = contents.getCursor();
                }
            } while (contents.hasMore());
        }
    }

    /**
     * @param name    name of the new file to store the content in
     * @param content data stream of the content
     * @param length  content size in bytes
     */
    public void storeFile(@Nonnull String name, @Nonnull InputStream content, long length) throws RestException {
        if (length < FOUR_MEGA_BYTE) {
            singleStore(name, content, length);
        } else {
            chunkedStore(name, content, length);
        }
    }

    /* *
     * Private helpers
     * */

    private FileMetadata singleStore(@Nonnull String name, @Nonnull InputStream content, long length) throws RestException {
        URL url = getUrl(URL_UPLOAD);
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setPath(createPath(name));
        JsonObjectRequest<FileMetadata> request = new JsonObjectRequest<FileMetadata>(url, content, "application/octet-stream", gson, FileMetadata.class);
        request.setHeader("Dropbox-API-Arg", gson.toJson(uploadRequest));
        request.setHeader("Content-Length", Long.toString(length));

        final FileMetadata fileMetadata;
        try {
            fileMetadata = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_file_upload_simple(name), e);
        }

        return fileMetadata;
    }

    private void chunkedStore(String name, InputStream content, long length) {
        //TODO
    }


    private FolderContent listFiles(@Nonnull FolderMetadata workingFolder) throws RestException {
        URL url = getUrl(URL_LIST_FOLDER);
        ListFolderRequest requestContent = new ListFolderRequest();
        requestContent.setPath(workingFolder.getPathLower());
        JsonObjectRequest<FolderContent> request = buildRequestWithContent(url, requestContent, FolderContent.class);

        final FolderContent content;
        try {
            content = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_folder_list(workingFolder.getName()), e);
        }

        return content;
    }

    private FolderContent listFiles(String cursor) throws RestException {
        URL url = getUrl(URL_LIST_FOLDER_CONTINUE);

        CursorRequest requestContent = new CursorRequest();
        requestContent.setCursor(cursor);
        JsonObjectRequest<FolderContent> request = buildRequestWithContent(url, requestContent, FolderContent.class);

        final FolderContent content;
        try {
            content = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_folder_list_cursor(), e);
        }

        return content;
    }

    private Metadata retrieveMetaData(String relative) throws RestException {
        URL url = getUrl(URL_METADATA);
        MetadataRequest requestContent = new MetadataRequest();
        String absolute = createAbsolutePath(relative);
        requestContent.setPath(absolute);
        JsonObjectRequest<Metadata> request = buildRequestWithContent(url, requestContent, Metadata.class);

        final Metadata metadata;
        try {
            metadata = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_file_metadata(relative), e);
        }

        return metadata;
    }

    private <T> JsonObjectRequest<T> buildRequestWithContent(URL url, Object requestContent, Class<T> classOfT) {
        String content = gson.toJson(requestContent);
        JsonObjectRequest<T> request = new JsonObjectRequest<T>(url, content, APPLICATION_JSON, gson, classOfT);
        request.setErrorClass(ErrorResponse.class);
        if (timeout > 1000) {
            request.setTimeout(timeout);
        }
        request.sign(accessToken);

        return request;
    }

    private <T> JsonObjectRequest<T> buildRequest(URL url, Class<T> classOfT) {
        JsonObjectRequest<T> request = new JsonObjectRequest<T>(url, gson, classOfT);
        request.setErrorClass(ErrorResponse.class);
        if (timeout > 1000) {
            request.setTimeout(timeout);
        }
        request.sign(accessToken);

        return request;
    }

    private static URL getUrl(String urlSource) throws RestException {
        URL url;
        try {
            url = new URLBuilder(urlSource).build();
        } catch (URISyntaxException e) {
            throw new RestException(Messages.exception_dropbox_url(), e);
        } catch (MalformedURLException e) {
            throw new RestException(Messages.exception_dropbox_url(), e);
        }

        return url;
    }

    private String createAbsolutePath(final String path) {
        StringBuilder sb = new StringBuilder();
        if (path.startsWith(PATH_SEPARATOR)) {
            // paths starting with / are already absolute
            sb.append(path);
        } else {
            // relative paths are prefix with the working folder
            if (workingFolder != null) {
                sb.append(workingFolder.getPathLower());
            }
            // When working folder is the root the path could end with '/'
            if (sb.length() == 0 || !PATH_SEPARATOR.equals(sb.substring(sb.length() - 1))) {
                sb.append(PATH_SEPARATOR);
            }
            sb.append(path);
        }

        return sb.toString();
    }

    private String createPath(String name) {
        String path;
        if (workingFolder.getPathLower().endsWith(PATH_SEPARATOR)) {
            path = workingFolder.getPathLower() + name;
        } else {
            path = workingFolder.getPathLower() + PATH_SEPARATOR + name;
        }
        return path;
    }

    /* *
     * Static token helpers
     * */

    public static String convertAuthorizationToAccessCode(String authorizationCode) throws IOException {
        if (StringUtils.isEmpty(authorizationCode)) {
            return "";
        }
        String accessToken = readAccessTokenFromProvider(authorizationCode);
        if (accessToken == null) {
            accessToken = readAccessTokenFromWeb(authorizationCode);
        }

        return accessToken;
    }

    private static String readAccessTokenFromWeb(String authorizationCode) throws RestException, UnsupportedEncodingException {
        String accessToken;
        URL url = getUrl(URL_TOKEN);
        FormBuilder builder = new FormBuilder()
                .appendQueryParameter("code", authorizationCode)
                .appendQueryParameter("grant_type", VALUE_AUTHORIZATION_CODE)
                .appendQueryParameter("client_id", Config.CLIENT_ID);
        try {
            // Apply production config not included in source distribution
            Class privateConfig = Class.forName("org.jenkinsci.plugins.publishoverdropbox.domain.ConfigPrivate");
            Class[] argClass = {builder.getClass()};
            Method method = privateConfig.getDeclaredMethod("append", argClass);
            method.invoke(null, builder);
        } catch (Exception e) {
            // Apply local development parameters
            builder.appendQueryParameter("client_secret", Config.CLIENT_SECRET);
        }
        String body = builder.build();

        String contentType = FormBuilder.CONTENT_TYPE;
        JsonObjectRequest<TokenResponse> request = new JsonObjectRequest<TokenResponse>(url, body, contentType, new Gson(), TokenResponse.class);
        request.setErrorClass(ErrorResponse.class);
        TokenResponse response = request.execute();
        accessToken = response.getAccessToken();

        return accessToken;
    }

    private static String readAccessTokenFromProvider(String authorizationCode) {
        String accessToken = null;
        List<DropboxToken> tokens = CredentialsProvider.lookupCredentials(DropboxToken.class, Jenkins.getInstance(), null, (DomainRequirement) null);
        for (DropboxToken token : tokens) {
            if (token.getAuthorizationCode().equals(authorizationCode)) {
                accessToken = token.getAccessCode();
            }
        }

        return accessToken;
    }

    Metadata getWorkingFolder() {
        return this.workingFolder;
    }
}
