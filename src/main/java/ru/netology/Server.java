package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlerBase = new ConcurrentHashMap<>();
    public static final String GET = "GET";
    public static final String POST = "POST";

    public void start(int port) {
        Runnable runnable = () -> {
            try (final var serverSocket = new ServerSocket(port)) {
                while (true) {
                    try (final var socket = serverSocket.accept();
                         final var in = new BufferedInputStream(socket.getInputStream());
                         final var out = new BufferedOutputStream(socket.getOutputStream());
                        ) {
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

    public void server(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        final var allowedMethods = List.of(GET, POST);
        final var limit = 4096;
        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        // ищем request line
        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            badRequest(out);
            return;
        }

        // читаем request line
        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            badRequest(out);
            return;
        }

        final var method = requestLine[0];
        if (!allowedMethods.contains(method)) {
            badRequest(out);
            return;
        }

        final var path = requestLine[1];
        if (!path.startsWith("/")) {
            badRequest(out);
            return;
        }

        // ищем заголовки
        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            badRequest(out);
            return;
        }

        // отматываем на начало буфера
        in.reset();
        // пропускаем requestLine
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));

        // для GET тела нет
        String body = null;
        if (!method.equals(GET)) {
            in.skip(headersDelimiter.length);
            // вычитываем Content-Length, чтобы прочитать body
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);
                body = new String(bodyBytes);
            }
        }
        Request request = new Request(method, path,headers, body);

        if (handlerBase.containsKey(request.getMethod())) {
            if (handlerBase.get(request.getMethod()).containsKey(request.getPath())) {
                handlerBase.get(request.getMethod()).get(request.getPath()).handle(request, out);
                request.getQueryParam(path);
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

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    // from google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
