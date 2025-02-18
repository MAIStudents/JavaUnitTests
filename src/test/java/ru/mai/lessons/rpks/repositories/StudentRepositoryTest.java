package ru.mai.lessons.rpks.repositories;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mai.lessons.rpks.models.Student;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Тест на поиск студента по его идентификатору")
    void givenStudent_whenFindById_thenReturnStudent() {
        Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
        repository.save(studentToSave);

        Student studentById = repository.findById(1L).orElse(null);

        assertNotNull(studentById);
        assertEquals(studentToSave.getFullName(), studentById.getFullName());
        assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
    }


    @Test
    @DisplayName("Тест на удаление студента по его идентификатору")
    void givenStudentId_whenDeleteStudent_thenStudentIsDeleted() {
        Student studentToSave = new Student(null, "Lisnyak", "M8О-313Б");
        Student savedStudent = repository.save(studentToSave);

        Long studentId = savedStudent.getId();
        repository.deleteById(studentId);

        assertFalse(repository.findById(studentId).isPresent());
    }

    @Test
    @DisplayName("Тест на удаление студента с некорректным идентификатором")
    void givenInvalidId_whenDeleteStudent_thenNoEffect() {
        Long invalidId = 999L;

        assertFalse(repository.findById(invalidId).isPresent());


        repository.deleteById(invalidId);


        long countBefore = repository.count();
        repository.deleteById(invalidId);
        long countAfter = repository.count();

        assertEquals(countBefore, countAfter, "Количество записей в базе не должно измениться");
    }

    @Test
    @DisplayName("Тест на успешное создание студента")
    void givenValidStudent_whenSave_thenStudentIsCreated() {

        Student studentToSave = new Student(null, "Lisnyak", "M8О-313Б");
        Student savedStudent = repository.save(studentToSave);

        Student studentById = repository.findById(1L).orElse(null);


        assertNotNull(savedStudent.getId(), "ID студента не должен быть null после сохранения");
        assertEquals(studentToSave.getFullName(), studentById.getFullName(), "ФИО должно совпадать");
        assertEquals(studentToSave.getGroupName(), studentById.getGroupName(), "Группа должна совпадать");

        Optional<Student> foundStudent = repository.findById(savedStudent.getId());
        assertTrue(foundStudent.isPresent(), "Студент должен существовать в базе данных");
    }

    @Test
    @DisplayName("Тест на создание студента с некорректными данными")
    void givenInvalidStudent_whenSave_thenThrowException() {

        Student invalidStudent = new Student(null, null, "M8О-313Б");

        assertThrows(Exception.class, () -> repository.save(invalidStudent),
                "Должно выбрасываться исключение при сохранении студента с некорректными данными");
    }

    @Test
    @DisplayName("Тест на успешное обновление студента")
    void givenExistingStudent_whenUpdate_thenStudentIsUpdated() {

        Student studentToSave = new Student(null, "Lisnyak", "M8О-313Б");
        Student savedStudent = repository.save(studentToSave);

        savedStudent.setFullName("Ivanov");
        savedStudent.setGroupName("M8О-202Б");
        Student updatedStudent = repository.save(savedStudent);


        assertNotNull(updatedStudent, "Обновленный студент не должен быть null");
        assertEquals("Ivanov", updatedStudent.getFullName(), "ФИО студента должно обновиться");
        assertEquals("M8О-202Б", updatedStudent.getGroupName(), "Группа студента должна обновиться");

        Optional<Student> foundStudent = repository.findById(updatedStudent.getId());
        assertTrue(foundStudent.isPresent(), "Студент должен существовать в базе");
        assertEquals("Ivanov", foundStudent.get().getFullName(), "ФИО в базе должно совпадать с обновленным");
        assertEquals("M8О-202Б", foundStudent.get().getGroupName(), "Группа в базе должна совпадать с обновленной");
    }

    @Test
    @DisplayName("Тест на обновление несуществующего студента")
    void givenNonExistingStudent_whenUpdate_thenThrowException() {

        Student nonExistingStudent = new Student(999L, "NonExistent", "M8О-999Б");


        Exception exception = assertThrows(Exception.class, () -> repository.save(nonExistingStudent),
                "Должно выбрасываться исключение при обновлении несуществующего студента");

        assertNotNull(exception, "Исключение должно быть выброшено");
    }

    @Test
    @DisplayName("Тест на поиск студента с несуществующим идентификатором")
    void givenNonExistentId_whenFindById_thenReturnNull() {

        Long invalidId = 999L;


        Student studentById = repository.findById(invalidId).orElse(null);


        assertNull(studentById, "Ожидается, что студент с несуществующим ID не будет найден (null)");
    }


}
