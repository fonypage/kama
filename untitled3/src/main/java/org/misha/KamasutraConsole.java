package org.misha;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public final class KamasutraConsole {

    // Русский алфавит с Ё/ё
    private static final String RU_UPPER = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String RU_LOWER = RU_UPPER.toLowerCase(Locale.ROOT);

    // --- Шифр Камасутры ---
    public static final class KamasutraCipher {
        private final Map<Character, Character> map;
        private final String permUpper;
        private final String permLower;

        public KamasutraCipher() {
            int n = RU_UPPER.length();   // 33
            int half = n / 2;            // 16
            String first  = RU_UPPER.substring(0, half);   // 0..15
            String second = RU_UPPER.substring(half);       // 16..32 (17 шт)

            Map<Character, Character> m = new HashMap<>();
            int secondLen = second.length(); // 17

            // Попарное зеркало: first[i] <-> second[secondLen-1 - i]
            for (int i = 0; i < first.length(); i++) {
                char a = first.charAt(i);
                char b = second.charAt(secondLen - 1 - i);
                m.put(a, b);
                m.put(b, a);
            }

            // «Лишняя» середина второй половины маппится сама на себя
            if (secondLen > first.length()) {
                int leftoverIdx = secondLen - 1 - first.length(); // 0
                char c = second.charAt(leftoverIdx);              // 'П'
                m.put(c, c);
            }

            // Дублируем в нижний регистр
            Map<Character, Character> full = new HashMap<>(m.size() * 2);
            for (Map.Entry<Character, Character> e : m.entrySet()) {
                char upFrom = e.getKey();
                char upTo   = e.getValue();
                full.put(upFrom, upTo);
                full.put(Character.toLowerCase(upFrom), Character.toLowerCase(upTo));
            }
            this.map = full;

            // Готовим «карту подстановки» по алфавиту
            this.permUpper = buildPermutation(RU_UPPER, full);
            this.permLower = buildPermutation(RU_LOWER, full);
        }

        private static String buildPermutation(String alphabet, Map<Character, Character> m) {
            StringBuilder sb = new StringBuilder(alphabet.length());
            for (int i = 0; i < alphabet.length(); i++) {
                char src = alphabet.charAt(i);
                sb.append(m.getOrDefault(src, src));
            }
            return sb.toString();
        }

        private String transform(String text) {
            StringBuilder sb = new StringBuilder(text.length());
            for (char ch : text.toCharArray()) {
                sb.append(map.getOrDefault(ch, ch));
            }
            return sb.toString();
        }

        public String encrypt(String text) { return transform(text); }
        public String decrypt(String text) { return transform(text); }

        public String getPermUpper() { return permUpper; }
        public String getPermLower() { return permLower; }
    }

    // --- Консоль ---
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        KamasutraCipher cipher = new KamasutraCipher();

        // Печатаем исходный и изменённый алфавиты
        System.out.println("SRC LOWER: " + RU_LOWER);
        System.out.println("MAP LOWER: " + cipher.getPermLower());
        System.out.println();

        System.out.println("Камасутра (RU, с Ё/ё). Выберите режим: 1) Шифровать  2) Расшифровать");
        int mode = readInt(sc, 1, 2);

        System.out.print("Введите текст: ");
        String text = sc.nextLine();

        String result = (mode == 1) ? cipher.encrypt(text) : cipher.decrypt(text);
        System.out.println("Результат: " + result);
    }

    private static int readInt(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.print("Повторите ввод (" + min + "-" + max + "): ");
        }
    }
}
import java.util.*;

public class KamastraCipher {
    public static void main(String[] args) {
        String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        System.out.println("Длина алфавита: " + alphabet.length());
        
        // Делим алфавит пополам
        int half = alphabet.length() / 2;
        char[] firstHalf = alphabet.substring(0, half).toCharArray();
        char[] secondHalf = alphabet.substring(half).toCharArray();
        
        // Перемешиваем обе половины
        shuffleArray(firstHalf);
        shuffleArray(secondHalf);
        
        // Создаем словарь для шифрования
        Map<Character, Character> encryptDict = new HashMap<>();
        for (int i = 0; i < firstHalf.length; i++) {
            encryptDict.put(firstHalf[i], secondHalf[i]);
            encryptDict.put(secondHalf[i], firstHalf[i]);
        }
        
        // Вывод ключей (таблицы соответствий)
        System.out.println("=".repeat(60));
        System.out.println("ШИФР КАМАСУТРА - ТАБЛИЦА СООТВЕТСТВИЙ:");
        System.out.println("=".repeat(60));
        System.out.println("Пары букв для замены:");
        System.out.println("-".repeat(30));
        
        for (int i = 0; i < firstHalf.length; i++) {
            System.out.println(firstHalf[i] + " ↔ " + secondHalf[i]);
        }
        
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Компактный вид ключа
        System.out.println("Компактный вид ключа:");
        System.out.println("Первая половина:  " + arrayToString(firstHalf));
        System.out.println("Вторая половина:  " + arrayToString(secondHalf));
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Шифрование
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите текст для шифрования: ");
        String text = scanner.nextLine();
        StringBuilder encrypted = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            char lowerChar = Character.toLowerCase(c);
            if (encryptDict.containsKey(lowerChar)) {
                char replacement = encryptDict.get(lowerChar);
                if (Character.isUpperCase(c)) {
                    encrypted.append(Character.toUpperCase(replacement));
                } else {
                    encrypted.append(replacement);
                }
            } else {
                encrypted.append(c);
            }
        }
        
        System.out.println("Зашифрованный текст: " + encrypted);
        System.out.println();
        
        // Расшифровка (тот же процесс)
        StringBuilder decrypted = new StringBuilder();
        for (char c : encrypted.toString().toCharArray()) {
            char lowerChar = Character.toLowerCase(c);
            if (encryptDict.containsKey(lowerChar)) {
                char replacement = encryptDict.get(lowerChar);
                if (Character.isUpperCase(c)) {
                    decrypted.append(Character.toUpperCase(replacement));
                } else {
                    decrypted.append(replacement);
                }
            } else {
                decrypted.append(c);
            }
        }
        
        System.out.println("Расшифрованный текст: " + decrypted);
        
        // Дополнительная информация
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ:");
        System.out.println("=".repeat(60));
        System.out.println("Полный словарь замен:");
        
        int count = 0;
        for (Map.Entry<Character, Character> entry : encryptDict.entrySet()) {
            if (count % 5 == 0) {
                System.out.println();
            }
            System.out.print(entry.getKey() + "→" + entry.getValue() + "  ");
            count++;
        }
        
        System.out.println("\n" + "=".repeat(60));
        scanner.close();
    }
    
    // Метод для перемешивания массива
    private static void shuffleArray(char[] array) {
        Random random = new Random();
for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    // Метод для преобразования массива в строку с пробелами
    private static String arrayToString(char[] array) {
        StringBuilder sb = new StringBuilder();
        for (char c : array) {
            sb.append(c).append(" ");
        }
        return sb.toString().trim();
    }
}
