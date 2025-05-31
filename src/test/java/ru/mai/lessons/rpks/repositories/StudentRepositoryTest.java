package ru.mai.lessons.rpks.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mai.lessons.rpks.models.Student;

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
    Student studentToSave = new Student(null, "Heizenberg", "M8O-311B-22");

    Student savedStudent = repository.save(studentToSave);
    Student studentById = repository.findById(savedStudent.getId()).orElse(null);

    assertNotNull(studentById);
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenEmpty_whenFindById_thenReturnException() {
    Student studentById = repository.findById(1L).orElse(null);
    assertNull(studentById);
  }

  @Test
  @DisplayName("Тест на неуспешное добавление студента")
  void givenInvalidStudent_whenSave_thenThrowException() {
    Student student = Student.builder().fullName("Heizenberg").build();
    assertEquals(0, repository.count());
    try {
      assertThrows(Exception.class, () -> repository.save(student));
    } catch (Exception e) {
      assertEquals(0, repository.count());
    }
  }

  @Test
  @DisplayName("Тест на успешное добавление студента")
  void givenValidStudent_whenSave_thenReturnStudent() {
    Student student = Student.builder().fullName("Heizenberg").groupName("M8O-311B-22").build();
    assertEquals(0, repository.count());
    Student savedStudent = repository.save(student);
    assertNotNull(savedStudent);
    assertEquals(student.getFullName(), savedStudent.getFullName());
    assertEquals(student.getGroupName(), savedStudent.getGroupName());
    assertEquals(1, repository.count());

  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenValidId_whenDelete_thenReturnStudent() {
    Student student = Student.builder()
            .fullName("Heizenberg")
            .groupName("M8O-311B-22")
            .build();
    repository.save(student);
    assertEquals(1, repository.findAll().size());
    assertDoesNotThrow(() -> repository.deleteById(student.getId()));
    assertEquals(0, repository.findAll().size());
  }

  @Test
  @DisplayName("Тест на неуспешное удаление несуществующего студента")
  void givenInvalidId_whenDelete_thenThrowException() {

    repository.saveAndFlush(Student.builder()
            .fullName("Heizenberg")
            .groupName("M8O-311B-22")
            .build());

    assertEquals(1, repository.findAll().size());
    repository.deleteById(999L);
    assertEquals(1, repository.findAll().size());
  }
}
