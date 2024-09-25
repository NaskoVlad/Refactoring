package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    String method;
    String path;
    final BufferedReader in;
    String[] parts;

    public Request(BufferedReader in) {
        this.in = in;
    }
    public String[] getParts() {
        return parts;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        String newPath = path;
        int indexNumber = path.indexOf('?');
        if (indexNumber >= 0) {
            newPath = path.substring(0,indexNumber);
        }
        return newPath;
    }

    public void parse() throws IOException {
        final var requestLine = in.readLine();
        parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];
    }

    public List<NameValuePair> getQueryParams() throws IOException {
        List<NameValuePair> params = URLEncodedUtils.parse(in.readLine(), StandardCharsets.UTF_8);
        for (NameValuePair param : params) {
            System.out.printf("%s - s\n", param.getName(), param.getValue());
        }
        return params;
    }

    public boolean getQueryParam(String name) throws IOException {
        boolean result = false;
        if (getQueryParams().contains(name)) {
            result = true;
        }
        return result;
    }
}
