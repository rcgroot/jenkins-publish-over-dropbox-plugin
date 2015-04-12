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

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.publishoverdropbox.domain.model.*;
import org.jenkinsci.plugins.publishoverdropbox.impl.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;


public class Dropbox {

    private static final String URL_TOKEN = "https://api.dropbox.com/1/oauth2/token";
    private static final String URL_ACCOUNT_INFO = "https://api.dropbox.com/1/account/info";
    private static final String URL_FILE_METADATA = "https://api.dropbox.com/1/metadata/auto";
    private static final String URL_OPS_CREATE_FOLDER = "https://api.dropbox.com/1/fileops/create_folder";
    private static final String URL_FILE_UPLOAD = "https://api-content.dropbox.com/1/files_put/auto";
    private static final String URL_OPS_DELETE_FILE = "https://api.dropbox.com/1/fileops/delete";
    private static final Gson gson = new Gson();
    public static final String PARAM_ROOT = "root";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_LOCALE = "locale";
    public static final String PATH_SEPERATOR = "/";
    public static final String PARAM_OVERWRITE = "overwrite";
    public static final String PARAM_AUTORENAME = "autorename";
    public static final String PARAM_PARENT_REV = "parent_rev";
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String VALUE_AUTHORIZATION_CODE = "authorization_code";
    private final String accessToken;
    private AccountInfo userInfo;
    private int timeout = -1;
    private Folder workingFolder;

    public Dropbox(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean connect() throws IOException {
        userInfo = retrieveAccountInfo(accessToken);
        return isConnected();
    }

    public boolean isConnected() {
        return !StringUtils.isEmpty(accessToken) && userInfo != null;
    }

    public boolean changeWorkingDirectory(String relative) throws IOException {
        boolean hasSuccess = true;
        try {
            workingFolder = retrieveFolderMetaData(relative);
        } catch (IOException e) {
            hasSuccess = false;
        }
        return hasSuccess && workingFolder != null && workingFolder.isDir();
    }

    public boolean disconnect() throws IOException {
        userInfo = null;
        return true;
    }

    private Folder retrieveFolderMetaData(String relative) throws RestException {
        String absolute = createAbsolutePath(relative);
        URL url;
        try {
            url = new URLBuilder(URL_FILE_METADATA)
                    .appendPath(absolute)
                    .build();
        } catch (URISyntaxException e) {
            throw new RestException(Messages.exception_dropbox_url(), e);
        } catch (MalformedURLException e) {
            throw new RestException(Messages.exception_dropbox_url(), e);
        }
        JsonObjectRequest<Folder> request = new JsonObjectRequest<Folder>(url, gson, Folder.class);
        request.setTimeout(timeout);
        request.sign(accessToken);
        final Folder folder;
        try {
            folder = request.execute();
        } catch (IOException e) {
            throw new RestException(Messages.exception_dropbox_folder(url), e);
        }
        return folder;
    }

    private File retrieveFileMetaData(String relative) throws IOException {
        String absolute = createAbsolutePath(relative);
        URL url;
        try {
            url = new URLBuilder(URL_FILE_METADATA)
                    .appendPath(absolute)
                    .build();
        } catch (URISyntaxException e) {
            throw new IOException(Messages.exception_dropbox_url(), e);
        }
        JsonObjectRequest<File> request = new JsonObjectRequest<File>(url, gson, File.class);
        request.setTimeout(timeout);
        request.sign(accessToken);

        return request.execute();
    }

    public Folder makeDirectory(String relative) throws IOException {
        String absolute = createAbsolutePath(relative);
        URL url = getUrl(URL_OPS_CREATE_FOLDER);
        String body = new FormBuilder()
                .appendQueryParameter(PARAM_ROOT, workingFolder.getRoot())
                .appendQueryParameter(PARAM_PATH, absolute)
                .appendQueryParameter(PARAM_LOCALE, getDropboxLocale())
                .build();
        String contentType = FormBuilder.CONTENT_TYPE;

        JsonObjectRequest<Folder> request = new JsonObjectRequest<Folder>(url, body, contentType, gson, Folder.class);
        request.setTimeout(timeout);
        request.sign(accessToken);

        return request.execute();
    }

    public File storeFile(String name, InputStream content) throws IOException {
        String absolute = createAbsolutePath(name);
        URL url;
        String parentRev = null;
        try {
            File file = retrieveFileMetaData(name);
            parentRev = file.getRev();
        } catch (IOException e) {
            // Ignore, asume file doenst exist
        }

        try {
            final URLBuilder builder = new URLBuilder(URL_FILE_UPLOAD);
            builder.appendPath(absolute)
                    .appendQueryParameter(PARAM_LOCALE, getDropboxLocale())
                    .appendQueryParameter(PARAM_OVERWRITE, VALUE_TRUE)
                    .appendQueryParameter(PARAM_AUTORENAME, VALUE_FALSE);
            if (parentRev != null) {
                builder.appendQueryParameter(PARAM_PARENT_REV, parentRev);
            }
            url = builder.build();
        } catch (URISyntaxException e) {
            throw new IOException(Messages.exception_dropbox_url(), e);
        }

        JsonObjectRequest<File> request = new JsonObjectRequest<File>(url, content, null, gson, File.class);
        request.setTimeout(timeout);
        request.sign(accessToken);

        return request.execute();
    }

    public void cleanFolder() throws IOException {
        if (workingFolder.isDir()) {
            for (BaseFile file : workingFolder.getContents()) {
                DeletedFile deletedFile = deleteFile(file);
                if (!deletedFile.isDeleted()) {
                    throw new IOException(Messages.exception_dropbox_delete());
                }

            }
        } else {
            throw new IOException(Messages.exception_dropbox_deleteIsNotFolder());
        }
    }

    private DeletedFile deleteFile(BaseFile file) throws RestException {
        URL url = getUrl(URL_OPS_DELETE_FILE);
        String body;
        try {
            body = new FormBuilder()
                    .appendQueryParameter(PARAM_ROOT, file.getRoot())
                    .appendQueryParameter(PARAM_PATH, file.getPath())
                    .appendQueryParameter(PARAM_LOCALE, getDropboxLocale())
                    .build();
        } catch (UnsupportedEncodingException e) {
            throw new RestException(Messages.exception_dropbox_body(), e);
        }
        JsonObjectRequest<DeletedFile> request = new JsonObjectRequest<DeletedFile>(url, body, FormBuilder.CONTENT_TYPE, gson, DeletedFile.class);
        request.setTimeout(timeout);
        request.sign(accessToken);

        return request.execute();
    }

    private AccountInfo retrieveAccountInfo(String accessToken) throws RestException {
        URL url = getUrl(URL_ACCOUNT_INFO);

        JsonObjectRequest<AccountInfo> request = new JsonObjectRequest<AccountInfo>(url, gson, AccountInfo.class);
        request.setTimeout(timeout);
        request.sign(accessToken);

        return request.execute();
    }

    public static String convertAuthorizationToAccessCode(String authorizationCode) throws IOException {
        if (StringUtils.isEmpty(authorizationCode)) {
            return "";
        }
        URL url = getUrl(URL_TOKEN);
        String body = new FormBuilder()
                .appendQueryParameter("code", authorizationCode)
                .appendQueryParameter("grant_type", VALUE_AUTHORIZATION_CODE)
                .appendQueryParameter("client_id", Config.CLIENT_ID)
                .appendQueryParameter("client_secret", Config.CLIENT_SECRET)
                .build();
        String contentType = FormBuilder.CONTENT_TYPE;
        JsonObjectRequest<TokenResponse> request = new JsonObjectRequest<TokenResponse>(url, body, contentType, gson, TokenResponse.class);
        TokenResponse response = request.execute();
        return response.getAccessToken();
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

    private String getDropboxLocale() {
        return Locale.getDefault().toLanguageTag();
    }

    private String createAbsolutePath(final String path) {
        StringBuilder sb = new StringBuilder();
        if (path.startsWith(PATH_SEPERATOR)) {
            // paths starting with / are already absolute
            sb.append(path);
        } else {
            // relative paths are prefix with the working folder
            if (workingFolder != null) {
                sb.append(workingFolder.getPath());
            }
            sb.append(PATH_SEPERATOR);
            sb.append(path);
        }

        return sb.toString();
    }
}
