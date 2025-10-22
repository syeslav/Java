package util;

public class Validator {

    // Шаблон для проверки email: должен содержать @ и .
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    /**
     * Проверяет, соответствует формату email.
     */
    public static boolean isValidEmail(String email) {
        // Проверяем, что строка не null и соответствует регулярному выражению
        return email != null && email.matches(EMAIL_REGEX);
    }

    /**
     * Проверяет, находится ли возраст пределах.
     */
    public static boolean isValidAge(int age) {
        // Возраст должен быть от 16 до 100 для студента
        return age >= 16 && age <= 100;
    }

    /**
     * Проверяет, что строка не пустая.
     */
    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}