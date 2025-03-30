package ru.mai.lessons.rpks.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import ru.mai.lessons.rpks.dto.requests.StudentUpdateRequest;
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
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudent_whenFindById_thenReturnStudent() {
    Student studentToSave = new Student(null, "Domoroschenov", "М8О-411Б");
    Student savedStudent = repository.save(studentToSave);
    assertNotNull(savedStudent.getId(), "ID студента должен быть назначен");

    Student studentById = repository.findById(savedStudent.getId()).orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }


  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenInvalidId_whenFindById_thenReturnEmpty() {
    Student foundStudent = repository.findById(1234L).orElse(null);
    assertNull(foundStudent);
  }

  @Test
  @DisplayName("Тест на сохранение студента")
  void givenStudent_whenSave_thenStudentIsPersisted() {
    Student student = new Student(null, "Zaslavtsev", "М8О-311Б-22");
    Student savedStudent = repository.save(student);

    assertNotNull(savedStudent.getId());
    assertEquals(student.getFullName(), savedStudent.getFullName());
    assertEquals(student.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на неуспешное сохранение студента (null)")
  void givenNullStudent_whenSave_thenThrowException() {
    assertThrows(InvalidDataAccessApiUsageException.class, () -> repository.save(null));
  }

  @Test
  @DisplayName("Тест на удаление студента")
  void givenStudent_whenDelete_thenStudentIsRemoved() {
    Student student = new Student(null, "Zaslavstev", "М8О-311Б-22");
    Student savedStudent = repository.save(student);

    repository.deleteById(savedStudent.getId());

    assertTrue(repository.findById(savedStudent.getId()).isEmpty());
  }

  @Test
  @DisplayName("Тест на неуспешное удаление студента (не существует)")
  void givenNonExistingStudentId_whenDelete_thenDoNothing() {
    Long nonExistingId = 1234L;

    Student student = new Student(null, "Zaslavstev", "М8О-311Б-22");
    repository.save(student);

    repository.deleteById(nonExistingId);

    Optional<Student> foundStudent = repository.findById(student.getId());
    assertTrue(foundStudent.isPresent());
  }

  @Test
  @DisplayName("Тест на успешное обновление студента")
  void givenExistingStudent_whenUpdate_thenReturnUpdatedStudent() {
    Student existingStudent = new Student(null, "Zaslavstev", "М8О-311Б-22");
    Student savedStudent = repository.save(existingStudent);

    StudentUpdateRequest updateRequest = new StudentUpdateRequest(savedStudent.getId(), "Zaslavtsev", "М8О-411Б-33");

    savedStudent.setFullName(updateRequest.getFullName());
    savedStudent.setGroupName(updateRequest.getGroupName());

    Student updatedStudent = repository.save(savedStudent);

    assertNotNull(updatedStudent);
    assertEquals(updateRequest.getFullName(), updatedStudent.getFullName());
    assertEquals(updateRequest.getGroupName(), updatedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на неуспешное обновление студента (не существует)")
  void givenNonExistingStudent_whenUpdate_thenThrowException() {
    Long nonExistingId = 1234L;
    Student student = new Student(nonExistingId, "WHOIS", "М8О-311Б-11");

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
      repository.save(student);
    });
  }

}
