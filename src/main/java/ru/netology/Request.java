package ru.netology;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class Request {
    private String method;
    private String path;
    private String[] parts;
    private String body;
    private List<String> headers;
    private BufferedInputStream in;

    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        String newPath = path;
        int indexNumber = newPath.indexOf('?');
        if (indexNumber >= 0) {
            newPath = newPath.substring(0, indexNumber);
        }
        return newPath;
    }

    protected List<NameValuePair> getQueryParams() {
        String newPath = path;
        int indexNumber = newPath.indexOf('?');
        if (indexNumber >= 0) {
            newPath = newPath.substring(indexNumber + 1);
        }
        List<NameValuePair> params = URLEncodedUtils.parse(newPath, StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            System.out.printf("%s - %s\n", param.getName(), param.getValue());
        }
        return params;
    }

    protected boolean getQueryParam(String name) {
        boolean result = false;
        if (getQueryParams().contains(name)) {
            result = true;
        }
        return result;
    }

    protected List<NameValuePair> getPostParams() {
        List<NameValuePair> params = URLEncodedUtils.parse(body, StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            System.out.printf("%s !! %s\n", param.getName(), param.getValue());
        }
        return params;
    }

    protected boolean getPostParam(String name) {
        boolean result = false;
        if (getPostParams().contains(name)) {
            result = true;
        }
        return result;
    }

    protected String getParts() throws Exception {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator((RequestContext) in);
        String result = null;
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            if (!item.isFormField()) {
                InputStream stream = item.openStream();
                result = IOUtils.toString(stream, StandardCharsets.UTF_8);
            } else System.out.printf("%s -- %s\n", item.getName(), item);
        }
        return result;
    }

    protected boolean getPart(String name) throws Exception {
        boolean result = false;
        if (getParts().contains(name)) {
            result = true;
        }
        return result;
    }

}
