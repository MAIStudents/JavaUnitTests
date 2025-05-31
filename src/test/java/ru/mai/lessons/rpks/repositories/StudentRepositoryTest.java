package ru.mai.lessons.rpks.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  @DisplayName("Test for successful student saving.")
  void givenValidStudent_whenSaveStudent_thenStudentReturnedAndSaved() {
    Student student = new Student(null, "fullName", "groupName");
    Student savedStudent = repository.save(student);

    assertNotNull(savedStudent.getId());
    assertEquals(student.getFullName(), savedStudent.getFullName());
    assertEquals(student.getGroupName(), savedStudent.getGroupName());
  }


  @Test
  @DisplayName("Test for successful student deletion.")
  void givenValidStudent_whenDeleteStudent_thenStudentReturnedAndDeleted() {
    Student student = new Student(null, "fullName", "groupName");
    Student savedStudent = repository.save(student);

    repository.deleteById(savedStudent.getId());

    assertNull(repository.findById(savedStudent.getId()).orElse(null));
  }


  @Test
  @DisplayName("Test for successful student update.")
  void givenValidRequestStudent_whenUpdate_thenStudentReturnedAndUpdated() {
    Student saveRequest = new Student(null, "fullName", "groupName");
    Student savedStudent = repository.save(saveRequest);

    Student updateRequest = new Student(savedStudent.getId(), "fullName", "groupName");
    Student updatedStudent = repository.save(updateRequest);

    assertNotNull(updatedStudent);
    assertEquals(updateRequest.getFullName(), updatedStudent.getFullName());
    assertEquals(updateRequest.getGroupName(), updatedStudent.getGroupName());
  }


  @Test
  @DisplayName("Test for unsuccessful search for a non-existent student.")
  void givenInvalidId_whenFindById_thenReturnNull() {
    Student foundStudent = repository.findById(1L).orElse(null);
    assertNull(foundStudent);
  }


  @Test
  @DisplayName("Test for saving an invalid student.")
  void givenStudentWithInvalidId_whenDelete_thenStudentNotDeleted() {
    assertDoesNotThrow(() -> repository.deleteById(1L));

    assertEquals(0, repository.count());
  }


  @Test
  @DisplayName("Test for updating a non-existent student.")
  void givenStudentWithInvalidId_whenUpdate_thenThrow() {
    Long invalidId = 2L;
    Student student = new Student(invalidId, "fullName", "groupName");

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> repository.save(student));
  }
}