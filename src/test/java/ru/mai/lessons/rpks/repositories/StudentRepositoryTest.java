package ru.mai.lessons.rpks.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    Student studentToSave = new Student(null, "buddha bless this test", "M8O-411B");
    Student savedStudent = repository.save(studentToSave);

    Student studentById = repository.findById(savedStudent.getId()).orElse(null);

    assertNotNull(studentById);
    assertEquals(savedStudent.getId(), studentById.getId());
    assertEquals(studentToSave.getFullName(), studentById.getFullName());
    assertEquals(studentToSave.getGroupName(), studentById.getGroupName());
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента по идентификатору")
  void givenNonExistentStudentId_whenFindById_thenReturnEmpty() {
    assertFalse(repository.findById(999L).isPresent());
  }

  @Test
  @DisplayName("Тест на сохранение студента")
  void givenStudent_whenSave_thenReturnSavedStudent() {
    Student studentToSave = new Student(null, "Jang Wonyoung", "M8O-slay-22");

    Student savedStudent = repository.save(studentToSave);

    assertNotNull(savedStudent);
    assertNotNull(savedStudent.getId());
    assertEquals(studentToSave.getFullName(), savedStudent.getFullName());
    assertEquals(studentToSave.getGroupName(), savedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на обновление студента")
  void givenStudent_whenUpdate_thenReturnUpdatedStudent() {
    Student studentToSave = new Student(null, "Forty Two", "42");
    Student savedStudent = repository.save(studentToSave);

    savedStudent.setFullName("42 Updated");
    savedStudent.setGroupName("52");
    Student updatedStudent = repository.save(savedStudent);

    assertNotNull(updatedStudent);
    assertEquals(savedStudent.getId(), updatedStudent.getId());
    assertEquals("42 Updated", updatedStudent.getFullName());
    assertEquals("52", updatedStudent.getGroupName());
  }

  @Test
  @DisplayName("Тест на удаление студента по идентификатору")
  void givenStudentId_whenDeleteById_thenStudentShouldNotExist() {
    Student studentToSave = new Student(null, "Ivanov", "M8O-411B");
    Student savedStudent = repository.save(studentToSave);

    repository.deleteById(savedStudent.getId());

    assertFalse(repository.findById(savedStudent.getId()).isPresent());
  }

  @Test
  @DisplayName("Тест на получение всех студентов")
  void givenStudents_whenFindAll_thenReturnAllStudents() {
    Student student1 = new Student(null, "Ivanov", "M8O-411B");
    Student student2 = new Student(null, "Petrov", "M8O-413B");

    repository.save(student1);
    repository.save(student2);

    assertEquals(2, repository.findAll().size());
  }

  @Test
  @DisplayName("Тест на проверку существования студента по идентификатору")
  void givenStudentId_whenExistsById_thenReturnTrue() {
    Student studentToSave = new Student(null, "Ion Fw This", "M8O-411B");
    Student savedStudent = repository.save(studentToSave);

    assertTrue(repository.existsById(savedStudent.getId()));
  }

  @Test
  @DisplayName("Тест на проверку несуществования студента по идентификатору")
  void givenNonExistentStudentId_whenExistsById_thenReturnFalse() {
    assertFalse(repository.existsById(999L));
  }

  @Test
  @DisplayName("Тест на подсчет количества студентов")
  void givenStudents_whenCount_thenReturnCorrectCount() {
    Student student1 = new Student(null, "Ivanov", "M8O-411B");
    Student student2 = new Student(null, "Not really Ivanov", "M8O-411B");

    repository.save(student1);
    repository.save(student2);

    assertEquals(2, repository.count());
  }
}