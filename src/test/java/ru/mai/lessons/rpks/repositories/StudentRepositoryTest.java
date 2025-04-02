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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
  @DisplayName("Тест на успешное сохранение студента")
  void givenStudent_whenSave_thenReturnStudent() {
    Student studentToSave = new Student(null, "Fedorkov", "М8О-311Б");

    Student savedStudent = repository.save(studentToSave);

    assertNotNull(savedStudent);
    assertEquals(studentToSave.getFullName(), savedStudent.getFullName());
    assertEquals(studentToSave.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на неуспешное сохранение невалидного студента")
  void givenNotValidStudent_whenSave_thenReturnThrowException() {
    Student invalidStudent = new Student(null, null, null);

    assertThrows(DataIntegrityViolationException.class, () -> repository.save(invalidStudent));
  }

  @Test
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudent_whenFindById_thenReturnStudent() {
    Student studentToSave = new Student(null, "Fedorkov", "М8О-311Б");
    studentToSave = repository.save(studentToSave);

    Student studentById = repository.findById(studentToSave.getId()).orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента по его идентификатору")
  void givenNonExistingStudent_whenFindById_thenReturnNull() {
    Student studentToSave = new Student(null, "Fedorkov", "М8О-311Б");
    repository.save(studentToSave);

    Student studentById = repository.findById(2L).orElse(null);

    assertNull(studentById);
  }

  @Test
  @DisplayName("Тест на успешное обновление данных студента")
  void givenStudent_whenUpdate_thenReturnUpdatedStudent() {
    Student student = new Student(null, "Fedorkov", "М8О-311Б");
    Student savedStudent = repository.save(student);
    Student expectedStudent = new Student(savedStudent.getId(), "Fedorkov Alex", "М8О-311Б");

    Student realUpdatedStudent = repository.save(expectedStudent);

    assertNotNull(realUpdatedStudent);
    assertEquals(expectedStudent.getFullName(), realUpdatedStudent.getFullName());
    assertEquals(expectedStudent.getGroupName(), realUpdatedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на неуспешное обновление ввиду невалидных полей студента")
  void givenNotValidStudent_whenUpdate_thenThrowException() {
    Student student = new Student(null, "Fedorkov", "М8О-311Б");
    Student savedStudent = repository.save(student);

    savedStudent.setFullName(null);
    savedStudent.setGroupName(null);

    assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(savedStudent));
  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenStudent_whenDelete_thenStudentNotFound() {
    Student student = new Student(null, "Fedorkov", "М8О-313Б");
    Student savedStudent = repository.save(student);

    repository.delete(savedStudent);

    Optional<Student> deletedStudent = repository.findById(savedStudent.getId());
    assertFalse(deletedStudent.isPresent());
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void givenNonExistingStudent_whenDelete_thenThrowException() {
    Long countBefore = repository.count();
    Student nonExistingStudent = new Student(20L, "NonExisting", "М8О-311Б");

    repository.delete(nonExistingStudent);
    Long countAfter = repository.count();

    assertEquals(countBefore, countAfter);
  }
}
