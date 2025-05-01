package ru.mai.lessons.rpks.repositories;

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

import java.util.Collections;

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
  @DisplayName("Тест на успешный поиск студента по его идентификатору")
  void givenStudent_whenFindById_thenReturnStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");

    repository.saveAllAndFlush(Collections.singleton(studentToSave));
    Student studentById = repository.findById(2L).orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidStudent_whenSaveStudent_thenStudentSavedAndReturned() {
    Student student = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.save(student);

    assertNotNull(savedStudent.getId());
    assertEquals(student.getFullName(), savedStudent.getFullName());
    assertEquals(student.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenValidStudent_whenDeleteStudent_thenStudentDeletedAndReturned() {
    Student student = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.save(student);

    repository.deleteById(savedStudent.getId());

    assertNull(repository.findById(savedStudent.getId()).orElse(null));
  }

  @Test
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequestStudent_whenUpdate_thenStudentUpdatedAndReturned() {
    Student saveRequest = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.save(saveRequest);

    Student updateRequest = new Student(savedStudent.getId(), "Brazhkin))", "М8О-411Б");
    Student updatedStudent = repository.save(updateRequest);

    assertNotNull(updatedStudent);
    assertEquals(updateRequest.getFullName(), updatedStudent.getFullName());
    assertEquals(updateRequest.getGroupName(), updatedStudent.getGroupName());
  }


  //  ---------------------------------------- NEGATIVE TEST ----------------------------------------------------


  @Test
  @DisplayName("Тест на неуспешное поиск несуществующего студента")
  void givenInvalidId_whenFindById_thenReturnNull() {
    Student foundStudent = repository.findById(1L).orElse(null);
    assertNull(foundStudent);
  }

  @Test
  @DisplayName("Тест на сохранение невалидного студента")
  void givenNullStudent_whenSave_thenThrowException() {
    Student saveRequest = new Student(null, null, null);
    assertThrows(DataIntegrityViolationException.class, () -> repository.save(saveRequest));
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void givenStudentWithInvalidId_whenDelete_thenNoRecordDeleted() {
    assertDoesNotThrow(() -> repository.deleteById(1L));

    assertEquals(0, repository.count());
  }


  @Test
  @DisplayName("Тест на обновление несуществующего студента")
  void givenStudentWithInvalidId_whenUpdate_thenThrowException() {
    Long invalidId = 2L;
    Student student = new Student(invalidId, "Domoroschenov", "М8О-411Б");

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> repository.save(student));
  }
}
