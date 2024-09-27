package ru.netology;

import org.apache.http.NameValuePair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        final var server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Добавлен метод ГЕТ");
            }
        });
        server.addHandler("GET", "/spring.svg", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String path = request.getPath();
                final var filePath = Path.of(".", "/public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });

        server.addHandler("GET", "/spring.png", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String path = request.getPath();
                final var filePath = Path.of(".", "/public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        });

        server.addHandler("GET", "/", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Все ок -1 ");
            }
        });

        server.addHandler("POST", "/", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Все ок - 2");
            }
        });
        server.start(8080);
    }

    private void handle() {
        new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String path = request.getPath();
                final var filePath = Path.of(".", "/public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, responseStream);
                responseStream.flush();
            }
        };
    }
}


