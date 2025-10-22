package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // --- Методы ---

    /** * Создает объект Student.
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("Id"),
                rs.getString("Name"),
                rs.getString("Surname"),
                rs.getInt("Age"),
                rs.getString("Phone"),
                rs.getString("Email")
        );
    }

    /** * Проверяет, существует ли email в таблице.
     */
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Students WHERE Email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /** * Проверяет, существует ли email в таблице.
     */
    public boolean isEmailExistsForOtherId(String email, int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Students WHERE Email = ? AND Id != ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    // --- 1.Операция Добавление ---

    public void addStudent(Student student) throws SQLException {
        // Проверка уникальности email
        if (isEmailExists(student.getEmail())) {
            throw new SQLException("Email '" + student.getEmail() + "' уже используется.");
        }

        String sql = "INSERT INTO Students (Id, Name, Surname, Age, Phone, Email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getSurname());
            pstmt.setInt(4, student.getAge());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Обработка дубликата ID (MySQL State 23000 - Integrity constraint violation)
            if (e.getSQLState().startsWith("23")) {
                throw new SQLException("Ошибка: Студент с ID " + student.getId() + " или email уже существует (нарушение уникальности).", e);
            }
            throw e;
        }
    }


    // --- 2. Операция Чтение ---

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Students ORDER BY Id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        }
        return students;
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM Students WHERE Id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public Student getStudentByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Students WHERE Email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Student> searchByName(String query) throws SQLException {
        List<Student> students = new ArrayList<>();
        // Поиск по части имени ИЛИ фамилии (нечувствительный к регистру)
        String sql = "SELECT * FROM Students WHERE Name LIKE ? OR Surname LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeQuery = "%" + query + "%";
            pstmt.setString(1, likeQuery);
            pstmt.setString(2, likeQuery);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        }
        return students;
    }

    public List<Student> filterByAge(int age) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Students WHERE Age = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, age);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        }
        return students;
    }


    // --- 3. Операция Обновление) ---

    public boolean updateStudent(Student student) throws SQLException {
        // Проверка уникальности email
        if (isEmailExistsForOtherId(student.getEmail(), student.getId())) {
            throw new SQLException("Email '" + student.getEmail() + "' уже используется другим студентом.");
        }

        String sql = "UPDATE Students SET Name=?, Surname=?, Age=?, Phone=?, Email=? WHERE Id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getSurname());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getPhone());
            pstmt.setString(5, student.getEmail());
            pstmt.setInt(6, student.getId()); // Условие WHERE

            return pstmt.executeUpdate() > 0;
        }
    }


    // --- 4. Операция Удаление ---

    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM Students WHERE Id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    public int deleteAllStudents() throws SQLException {
        String sql = "DELETE FROM Students";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            return pstmt.executeUpdate(); // Возвращает количество удаленных строк
        }
    }
}
