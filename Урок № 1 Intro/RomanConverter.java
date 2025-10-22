public class RomanConverter {

    public static String intToRoman(int num) {
        if (num <= 0 || num > 3999) {
            throw new IllegalArgumentException("Число должно быть в диапазоне 1..3999");
        }

        int[] values =    {1000, 900, 500, 400, 100,  90,  50,  40,  10,   9,   5,   4,  1};
        String[] romans = {"M",  "CM","D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV","I"};

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            // пока текущее значение помещается в num, добавляем соответствующий символ
            while (num >= values[i]) {
                sb.append(romans[i]);
                num -= values[i];
            }
        }

        return sb.toString();
    }

    //main для демонстрации
    public static void main(String[] args) {
        int[] tests = {15};
        for (int t : tests) {
            System.out.printf("%d -> %s%n", t, intToRoman(t));
        }
    }
}
