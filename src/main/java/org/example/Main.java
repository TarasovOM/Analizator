package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static BlockingQueue<String> maxA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> maxB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> maxC = new ArrayBlockingQueue<>(100);
    public static Thread textGenerator;

    public static void main(String[] args) throws InterruptedException {

        textGenerator = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String text = generateText("abc", 100_000);
                try {
                    maxA.put(text);
                    maxB.put(text);
                    maxC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread a = createNewThread(maxA, 'a');
        Thread b = createNewThread(maxB, 'b');
        Thread c = createNewThread(maxC, 'c');

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread createNewThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = findMaxCharCount(queue, letter);
            System.out.println("Максимальное количество символов " + letter + " = " + max);
        });
    }

    static int findMaxCharCount(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            text = queue.take();
            for (char c : text.toCharArray()) {
                if (c == letter) {
                    count++;
                }
            }
            if (count > max) {
                max = count;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted");
            return -1;
        }
        return max;
    }
}