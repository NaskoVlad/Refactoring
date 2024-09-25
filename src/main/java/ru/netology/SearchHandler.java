//package ru.netology;
//
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class SearchHandler {
//    Request request;
//
//    public void search(ConcurrentHashMap handlerBase,BufferedReader in, BufferedOutputStream out) throws IOException {
//        Request request;
//
//        final var requestLine = in.readLine();
//        final var parts =   requestLine.split(" ");
//
//        if (parts.length != 3) {
//            out.write((
//                    "HTTP/1.1 400 Bad Request\r\n" +
//                            "Content-Length: 0\r\n" +
//                            "Connection: close\r\n" +
//                            "\r\n"
//            ).getBytes());
//            out.flush();
//            return;
//        }
//        request = new Request(parts[0], parts[1], in);
//
//
//        if (handlerBase.containsKey(parts[0])) {
//            if (handlerBase.get(parts[0]).containsKey(parts[1])) {
//                handlerBase.get(parts[0]).get(parts[1]).handle(request, out);
//            }
//        } else {
//            out.write((
//                    "HTTP/1.1 404 Not Found\r\n" +
//                            "Content-Length: 0\r\n" +
//                            "Connection: close\r\n" +
//                            "\r\n"
//            ).getBytes());
//            out.flush();
//            return;
//        }
//    }
//}
