package app;

import dao.StudentDAO;
import model.Student;
import util.Validator;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== STUDENT MANAGEMENT SYSTEM LAUNCHED ===");
        boolean running = true;

        while (running) {
            printMenu();
            try {
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Очистить буфер после nextInt

                    switch (choice) {
                        case 1: showAllStudents(); break;
                        case 2: findStudentById(); break;
                        case 3: addNewStudent(); break;
                        case 4: updateStudentData(); break;
                        case 5: deleteStudentEntry(); break;
                        case 6: searchStudentsByName(); break;
                        case 7: searchStudentsByEmail(); break;
                        case 8: filterStudentsByAge(); break;
                        case 9: deleteAllStudentsWithConfirmation(); break;
                        case 0: running = false; break;
                        default: System.out.println("Неверный выбор. Попробуйте снова.");
                    }
                } else {
                    System.out.println("\n[ОШИБКА ВВОДА]: Пожалуйста, введите числовой пункт меню.");
                    scanner.nextLine(); // Очистить буфер
                }
            } catch (SQLException e) {
                // Обработка ошибок.
                System.err.println("\n[ОШИБКА БАЗЫ ДАННЫХ]: " + e.getMessage());
            } catch (InputMismatchException e) {
                // Обработка ошибок.
                System.err.println("\n[ОШИБКА ВВОДА]: Неверный формат данных (ожидалось число).");
                scanner.nextLine();
            } catch (Exception e) {
                System.err.println("\n[НЕОЖИДАННАЯ ОШИБКА]: " + e.getMessage());
            }
        }
        System.out.println("Программа завершена.");
    }

    private static void printMenu() {
        System.out.println("\n=== STUDENT MANAGEMENT SYSTEM ===");
        System.out.println("1. Показать всех студентов");
        System.out.println("2. Найти студента по ID");
        System.out.println("3. Добавить нового студента (CREATE)");
        System.out.println("4. Обновить данные студента (UPDATE)");
        System.out.println("5. Удалить студента по ID (DELETE)");
        System.out.println("6. Поиск по имени/фамилии");
        System.out.println("7. Поиск по email");
        System.out.println("8. Фильтр по возрасту");
        System.out.println("9. Удалить ВСЕ записи (с подтверждением)");
        System.out.println("0. Выход");
        System.out.print("Выберите опцию: ");
    }

    // --- 1. СОЗДАТЬ ---

    private static void addNewStudent() throws SQLException {
        try {
            System.out.print("Введите ID студента: ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Введите Имя: ");
            String name = scanner.nextLine();

            System.out.print("Введите Фамилию: ");
            String surname = scanner.nextLine();

            System.out.print("Введите Возраст: ");
            int age = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Введите Телефон (опционально, Enter для пропуска): ");
            String phone = scanner.nextLine();
            if (phone.trim().isEmpty()) phone = null;

            System.out.print("Введите Email: ");
            String email = scanner.nextLine();

            // Проверка входных данных
            if (!Validator.isNotNullOrEmpty(name) || !Validator.isNotNullOrEmpty(surname)) {
                System.out.println("Валидация: Имя и Фамилия не могут быть пустыми.");
                return;
            }
            if (!Validator.isValidAge(age)) {
                System.out.println("Валидация: Некорректный возраст. (От 16 до 100)");
                return;
            }
            if (!Validator.isValidEmail(email)) {
                System.out.println("Валидация: Некорректный формат Email.");
                return;
            }

            Student newStudent = new Student(id, name, surname, age, phone, email);
            studentDAO.addStudent(newStudent);
            System.out.println("\n[УСПЕХ]: Студент успешно добавлен.");

        } catch (InputMismatchException e) {
            // Перехват ошибки, если id или age ввели не числом
            System.err.println("\n[ОШИБКА ВВОДА]: ID и Возраст должны быть числами.");
            scanner.nextLine(); // Очистить буфер
        }
    }

    // --- 2. ЧИТАТЬ ---

    private static void showAllStudents() throws SQLException {
        List<Student> students = studentDAO.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("\nБаза данных студентов пуста.");
            return;
        }
        System.out.println("\n--- СПИСОК СТУДЕНТОВ (" + students.size() + ") ---");
        students.forEach(System.out::println);
    }

    private static void findStudentById() throws SQLException {
        System.out.print("Введите ID студента для поиска: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Некорректный ID.");
            scanner.nextLine();
            return;
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        Student student = studentDAO.getStudentById(id);
        if (student != null) {
            System.out.println("\n--- НАЙДЕН СТУДЕНТ ---");
            System.out.println(student);
        } else {
            System.out.println("Студент с ID " + id + " не найден.");
        }
    }

    private static void searchStudentsByName() throws SQLException {
        System.out.print("Введите часть имени или фамилии для поиска: ");
        String query = scanner.nextLine();
        if (query.trim().isEmpty()) {
            System.out.println("Поисковый запрос не может быть пустым.");
            return;
        }

        List<Student> students = studentDAO.searchByName(query);
        if (students.isEmpty()) {
            System.out.println("По запросу '" + query + "' студенты не найдены.");
            return;
        }
        System.out.println("\n--- РЕЗУЛЬТАТЫ ПОИСКА (" + students.size() + ") ---");
        students.forEach(System.out::println);
    }

    private static void searchStudentsByEmail() throws SQLException {
        System.out.print("Введите Email для поиска: ");
        String email = scanner.nextLine();

        Student student = studentDAO.getStudentByEmail(email);

        if (student != null) {
            System.out.println("\n--- НАЙДЕН СТУДЕНТ ПО EMAIL ---");
            System.out.println(student);
        } else {
            System.out.println("Студент с Email '" + email + "' не найден.");
        }
    }

    private static void filterStudentsByAge() throws SQLException {
        System.out.print("Введите возраст для фильтрации: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Некорректный ввод возраста.");
            scanner.nextLine();
            return;
        }
        int age = scanner.nextInt();
        scanner.nextLine();

        if (!Validator.isValidAge(age)) {
            System.out.println("Валидация: Возраст вне допустимого диапазона (16-100).");
            return;
        }

        List<Student> students = studentDAO.filterByAge(age);
        if (students.isEmpty()) {
            System.out.println("Студенты в возрасте " + age + " не найдены.");
            return;
        }
        System.out.println("\n--- СТУДЕНТЫ В ВОЗРАСТЕ " + age + " (" + students.size() + ") ---");
        students.forEach(System.out::println);
    }


    // --- 3. ОБНОВЛЕНИЕ ---

    private static void updateStudentData() throws SQLException {
        System.out.print("Введите ID студента для обновления: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Некорректный ID.");
            scanner.nextLine();
            return;
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        Student studentToUpdate = studentDAO.getStudentById(id);

        if (studentToUpdate == null) {
            System.out.println("Студент с ID " + id + " не найден для обновления.");
            return;
        }

        System.out.println("\n--- ОБНОВЛЕНИЕ ДАННЫХ СТУДЕНТА " + studentToUpdate.getName() + " (ID: " + id + ") ---");
        System.out.println("Оставьте поле пустым и нажмите Enter, чтобы сохранить старое значение.");

        // Имя
        System.out.printf("Новое Имя (Текущее: %s): ", studentToUpdate.getName());
        String newName = scanner.nextLine();
        if (!newName.trim().isEmpty()) {
            studentToUpdate.setName(newName);
        }

        // Фамилия
        System.out.printf("Новая Фамилия (Текущая: %s): ", studentToUpdate.getSurname());
        String newSurname = scanner.nextLine();
        if (!newSurname.trim().isEmpty()) {
            studentToUpdate.setSurname(newSurname);
        }

        // Возраст
        System.out.printf("Новый Возраст (Текущий: %d): ", studentToUpdate.getAge());
        String ageInput = scanner.nextLine();
        if (!ageInput.trim().isEmpty()) {
            try {
                int newAge = Integer.parseInt(ageInput);
                if (!Validator.isValidAge(newAge)) {
                    System.out.println("Валидация: Некорректный возраст. Обновление отменено.");
                    return;
                }
                studentToUpdate.setAge(newAge);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Возраст должен быть числом. Обновление отменено.");
                return;
            }
        }

        // Телефон
        String currentPhone = studentToUpdate.getPhone() != null ? studentToUpdate.getPhone() : "Нет";
        System.out.printf("Новый Телефон (Текущий: %s): ", currentPhone);
        String newPhone = scanner.nextLine();
        if (!newPhone.trim().isEmpty()) {
            studentToUpdate.setPhone(newPhone);
        } else if (newPhone.isEmpty() && currentPhone.equals("Нет")) {
            studentToUpdate.setPhone(null);
        }

        // Email (проверка уникальности)
        System.out.printf("Новый Email (Текущий: %s): ", studentToUpdate.getEmail());
        String newEmail = scanner.nextLine();
        if (!newEmail.trim().isEmpty()) {
            if (!Validator.isValidEmail(newEmail)) {
                System.out.println("Валидация: Некорректный формат Email. Обновление отменено.");
                return;
            }
            studentToUpdate.setEmail(newEmail);
        }

        if (studentDAO.updateStudent(studentToUpdate)) {
            System.out.println("\n[УСПЕХ]: Данные студента успешно обновлены.");
        } else {
            System.out.println("Обновление не выполнено (либо данные не изменились, либо произошла ошибка).");
        }
    }


    // --- 4. УДАЛЕНИЕ ---

    private static void deleteStudentEntry() throws SQLException {
        System.out.print("Введите ID студента для удаления: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Некорректный ID.");
            scanner.nextLine();
            return;
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        if (studentDAO.deleteStudent(id)) {
            System.out.println("\n[УСПЕХ]: Студент с ID " + id + " успешно удален.");
        } else {
            System.out.println("Студент с ID " + id + " не найден.");
        }
    }

    private static void deleteAllStudentsWithConfirmation() throws SQLException {
        System.out.print("ВНИМАНИЕ! Вы уверены, что хотите удалить ВСЕ записи? (Введите 'ДА' для подтверждения): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("ДА")) {
            int count = studentDAO.deleteAllStudents();
            System.out.println("\n[УСПЕХ]: " + count + " записей студентов было удалено.");
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}