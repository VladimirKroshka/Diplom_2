package utils;

import java.util.Random;

public class RandomGenerator {

    private static final Random random = new Random();

    public static String generateRandomNumber(int length) {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < length; i++) {
            number.append(random.nextInt(10)); // Генерация случайной цифры от 0 до 9
        }
        return number.toString();
    }
}
