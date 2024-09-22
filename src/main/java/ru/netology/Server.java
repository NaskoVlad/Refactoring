package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlerBase = new ConcurrentHashMap<>();

    public void start(int port) {
        Runnable runnable = () -> {
            try (final var serverSocket = new ServerSocket(port)) {
                while (true) {
                    try (final var socket = serverSocket.accept();
                         final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         final var out = new BufferedOutputStream(socket.getOutputStream());) {
                        server(in, out);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        final ExecutorService threadPool = Executors.newFixedThreadPool(64);
        threadPool.execute(runnable);
    }

    public void server(BufferedReader in, BufferedOutputStream out) throws IOException {
        Handler handler;
        Request request;
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            // just close socket
            return;
        }

        request = new Request(parts[0], parts[1], in);
        if (handlerBase.containsKey(parts[0])) {
            if (handlerBase.get(parts[0]).containsKey(parts[1])) {
                handler = handlerBase.get(parts[0]).get(parts[1]);
                handler.handle(request, out);
            }
        }

        final var path = parts[1];
        if (!validPaths.contains(path)) {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
            return;
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public void addHandler(String requestMethod, String path, Handler handler) {
        if (!handlerBase.containsKey(requestMethod)) {
            handlerBase.put(requestMethod, new ConcurrentHashMap<>());
        }
        handlerBase.get(requestMethod).put(path, handler);
    }
}
