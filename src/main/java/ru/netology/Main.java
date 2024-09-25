package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


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
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".", "/public",  request.getPath());
                System.out.println(" request.getPath() -   " +  request.getPath());
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
        server.start(8080);
    }

    public void handle(){
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


