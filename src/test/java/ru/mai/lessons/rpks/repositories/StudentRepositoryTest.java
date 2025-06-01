package ru.mai.lessons.rpks.repositories;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import ru.mai.lessons.rpks.models.Student;

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
    Student savedStudent = repository.save(studentToSave);

    Student studentById = repository.findById(savedStudent.getId())
            .orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNonExistingStudentId_whenFindById_thenReturnEmpty() {
    var foundStudent = repository.findById(1337L);
    Assertions.assertFalse(foundStudent.isPresent());
  }

  @Test
  @DisplayName("Тест на успешное сохранение студента")
  void givenStudent_whenSavedById_thenReturnStudent_Positive() {
    Student studentToSave = new Student(null, "Aboba", "М8О-313Б");
    Student savedStudent = repository.save(studentToSave);

    assertNotNull(savedStudent.getId());
    assertEquals(studentToSave.getFullName(), savedStudent.getFullName());
    assertEquals(studentToSave.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на сохранение студента с неправильными данными")
  void givenInvalidStudent_whenSave_thenThrowException() {
    Student invalidStudent = new Student(null, null, null);
    assertThrows(DataIntegrityViolationException.class, () -> repository.save(invalidStudent));
  }

  @Test
  @DisplayName("Тест на обновление студента с корректными данными")
  void givenStudent_whenUpdatedById_thenReturnStudent_Positive() {
    Student studentToSave = new Student(null, "Aboba", "М8О-313Б");
    Student savedStudent = repository.save(studentToSave);

    var expectedName = "Boba";
    var expectedGroup = "М8О-1337Б";

    savedStudent.setFullName(expectedName);
    savedStudent.setGroupName(expectedGroup);

    Student updatedStudent = repository.save(savedStudent);

    assertEquals(expectedName, updatedStudent.getFullName());
    assertEquals(expectedGroup, updatedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на обновление несуществующего студента")
  void NonExistingStudent_whenUpdatedById_thenThrowException() {
    Student invalidStudent = new Student(1337L, "Aboba", "М8О-313Б");
    assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
      repository.save(invalidStudent);
    });
  }

  @Test
  @DisplayName("Тест на удаление студента по айди")
  void givenStudentId_whenDeletedById_thenReturnStudent_Positive() {
    Student student = new Student(null, "Aboba", "М8О-313Б");
    Student savedStudent = repository.save(student);

    Assertions.assertTrue(repository.findById(savedStudent.getId()).isPresent());
    repository.deleteById(savedStudent.getId());
    Assertions.assertFalse(repository.findById(savedStudent.getId()).isPresent());
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void NonExistingStudentId_whenDeleteById_thenThrowException() {
    Long invalidId = 1337L;
    long initialCount = repository.count();

    Assertions.assertFalse(repository.findById(invalidId).isPresent());

    repository.deleteById(invalidId);
    long countAfterDelete = repository.count();

    assertEquals(initialCount, countAfterDelete, "Кол-во записей не должно измениться.");
  }
}
