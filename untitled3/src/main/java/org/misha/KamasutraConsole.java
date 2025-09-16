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
