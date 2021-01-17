package com.productengine.test.task3;

import com.productengine.test.task3.navigator.Navigator;
import com.productengine.test.task3.navigator.ParallelIteratorNavigator;
import com.productengine.test.task3.console.Console;
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class Server {
    private static final int MAX_CONNECTIONS = 15;
    private static final Semaphore semaphore = new Semaphore(10);
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static int port;

    public static void main(String[] args) {
        String rootPath = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter port: ");
            port = Integer.parseInt(reader.readLine());
            System.out.print("Enter root path: ");
            rootPath = reader.readLine();
            File file = new File(rootPath);
            if (!file.exists()) {
                throw new IllegalArgumentException("Incorrect file path. Directory does not exist");
            }
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("You need to enter a path to a directory.");
            }

        } catch (IOException | IllegalArgumentException e) {
            logger.severe(e.getMessage());
        }

        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Server has started.");

            ExecutorService executorService = Executors.newCachedThreadPool();
            CountDownLatch countDownLatch = new CountDownLatch(MAX_CONNECTIONS);

            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                Console console = new Console(server);
                String finalRootPath = rootPath;
                executorService.execute(() -> {
                    console.writeLine("Connected to to the server.");
                    console.write("\nEnter depth: ");
                    int depth = Integer.parseInt(console.readLine());
                    console.write("Enter mask: ");
                    String mask = console.readLine();

                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        logger.severe(e.getMessage());
                    }

                    Navigator navigator = new ParallelIteratorNavigator();
                    console.writeLine("\nFound files: ");
                    navigator.getFilesThatMatch(finalRootPath, depth, mask, console::writeLine);

                    console.writeLine("\nDisconnected from the server.");
                    countDownLatch.countDown();
                    semaphore.release();
                });
            }
            executorService.shutdown();
            countDownLatch.await();
            logger.info("Server has stopped.");

        } catch (IOException | InterruptedException e) {
            logger.severe(e.getMessage());
        }
    }
}

