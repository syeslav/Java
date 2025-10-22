package model;

public class Student {
    private int id;
    private String name;
    private String surname;
    private int age;
    private String phone;
    private String email;

    // Конструктор
    public Student(int id, String name, String surname, int age, String phone, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public int getAge() { return age; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setAge(int age) { this.age = age; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("ID: %d | Имя: %s %s | Возраст: %d | Email: %s | Телефон: %s",
                id, name, surname, age, email, phone != null ? phone : "Нет");
    }
}
