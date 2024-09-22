package ru.netology;

import java.io.BufferedReader;
import java.io.InputStream;

public class Request {
    String method;
    String path;
    BufferedReader body;

    public Request(String method, String path, BufferedReader body) {
        this.method = method;
        this.path = path;
        this.body = body;
    }
}
