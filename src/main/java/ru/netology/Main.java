package ru.netology;

import java.io.*;


public class Main {
    public static void main(String[] args) {
        final var server = new Server();

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Добавлен метод ГЕТ");
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Добавлен метод ПОСТ");
            }
        });

        server.start(9999);

    }
}


