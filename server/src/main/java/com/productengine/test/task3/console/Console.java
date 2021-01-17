package com.productengine.test.task3.console;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Console implements Closeable {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public Console(ServerSocket serverSocket) {
        try {
            this.socket = serverSocket.accept();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void writeLine(T message) {
        try {
            writer.write(message.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void write(T message) {
        try {
            writer.write(message.toString());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader((this.socket.getInputStream())));
    }

    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
