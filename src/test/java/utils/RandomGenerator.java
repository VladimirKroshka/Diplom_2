package utils;

import java.util.Random;

public class RandomGenerator {

    public static String generateRandomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Генерирует случайное число от 0 до 9
        }
        return sb.toString();
    }
}