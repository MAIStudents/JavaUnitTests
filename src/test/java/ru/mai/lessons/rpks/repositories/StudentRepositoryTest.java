package ru.mai.lessons.rpks.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mai.lessons.rpks.models.Student;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudentRepositoryTest {

  @Autowired
  private StudentRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudent_whenFindById_thenReturnStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
    repository.saveAndFlush(studentToSave);

    var studentById = repository.findAll().stream().findFirst().orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на сохранение студента")
  void givenStudent_whenSave_thenReturnSavedStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.save(studentToSave);

    assertNotNull(savedStudent);
    assertEquals(studentToSave.getFullName(), savedStudent.getFullName());
    assertEquals(studentToSave.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на обновление студента")
  void givenStudent_whenUpdate_thenReturnUpdatedStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
    repository.saveAndFlush(studentToSave);

    studentToSave.setFullName("Ivkovich");
    Student updatedStudent = repository.saveAndFlush(studentToSave);

    assertNotNull(updatedStudent);
    assertEquals(studentToSave.getFullName(), updatedStudent.getFullName());
    assertEquals(studentToSave.getGroupName(), updatedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на удаление студента")
  void givenStudent_whenDelete_thenReturnDeletedStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
    repository.saveAndFlush(studentToSave);

    repository.delete(studentToSave);

    assertEquals(Collections.emptyList(), repository.findAll());
  }

  // Negative tests

  @Test
  @DisplayName("Тест на сохранение невалидного студента")
  void givenNonexistentId_whenSave_thenReturnNull() {
    Student student = new Student(null, null, "М8О-411Б");

    assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(student));
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNonexistentId_whenFindById_thenReturnNull() {
    var student = repository.findById(1L);
    assertNull(student.orElse(null));
  }

  @Test
  @DisplayName("Тест на неправильное обновление студента")
  void givenNonexistentId_whenUpdate_thenReturnNull() {
    Student student = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.saveAndFlush(student);

    savedStudent.setFullName(null);
    savedStudent.setGroupName(null);

    assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(savedStudent));
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void givenNonexistentId_whenDelete_thenReturnNull() {
    Long countBefore = repository.count();
    Student nonExistingStudent = new Student(20L, "NonExisting", "М8О-411Б");

    repository.delete(nonExistingStudent);
    Long countAfter = repository.count();

    assertEquals(countBefore, countAfter);
  }
}
