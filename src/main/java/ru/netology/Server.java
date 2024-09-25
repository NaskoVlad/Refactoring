package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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
                        System.out.println("in - 222222222   " +  in.readLine());
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
        Request request = new Request(in);
        request.parse();

        if (request.getParts().length != 3) {
            out.write((
                    "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }

        if (handlerBase.containsKey(request.getMethod())) {
            if (handlerBase.get(request.getMethod()).containsKey(request.getPath())) {
                handlerBase.get(request.getMethod()).get(request.getPath()).handle(request, out);
                request.getQueryParam(in.readLine());
            } else {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }
        } else {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            return;
        }
    }

    public void addHandler(String requestMethod, String path, Handler handler) {
        if (!handlerBase.containsKey(requestMethod)) {
            handlerBase.put(requestMethod, new ConcurrentHashMap<>());
        }
        handlerBase.get(requestMethod).put(path, handler);
    }
}
