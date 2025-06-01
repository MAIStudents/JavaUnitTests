package ru.mai.lessons.rpks.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mai.lessons.rpks.dto.mappers.StudentMapper;
import ru.mai.lessons.rpks.dto.requests.StudentCreateRequest;
import ru.mai.lessons.rpks.dto.requests.StudentUpdateRequest;
import ru.mai.lessons.rpks.dto.respones.StudentResponse;
import ru.mai.lessons.rpks.models.Student;
import ru.mai.lessons.rpks.repositories.StudentRepository;
import ru.mai.lessons.rpks.services.impl.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentMapper mapper;

  @InjectMocks
  private StudentServiceImpl service;

  @Test
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    Long studentId = 1L;
    StudentResponse expectedResponse = new StudentResponse(1L, "Irbitskiy", "МXX-XXXБ");
    Student expectedModel = new Student(1L, "Irbitskiy", "МXX-XXXБ");

    when(repository.findById(studentId)).thenReturn(Optional.of(expectedModel));
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.getStudent(studentId);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNonExistingStudentId_whenGetStudent_thenReturnStudentResponse() {
    Long studentId = 1337L;

    when(repository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(org.webjars.NotFoundException.class, () -> service.getStudent(studentId));
  }

  @Test
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("Aboba", "М8О-313Б");
    Student student = new Student(1L, "Ivanov", "М8О-313Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Aboba", "М8О-313Б");

    when(mapper.requestToModel(request)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(student);
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnUpdatedStudentResponse() {
    Long studentId = 1L;
    StudentUpdateRequest request = new StudentUpdateRequest(studentId, "Aboba", "М8О-313Б");
    Student student = new Student(studentId, "Aboba", "М8О-313Б");
    StudentResponse expectedResponse = new StudentResponse(studentId, "Aboba", "М8О-313Б");

    when(mapper.requestToModel(request)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(student);
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на успешное удаление студента по его идентификатору")
  void givenExistingStudentId_whenDeleteStudent_thenReturnDeletedStudentResponse() {
    Long studentId = 1L;
    Student student = new Student(studentId, "Aboba", "М8О-313Б");
    StudentResponse expectedResponse = new StudentResponse(studentId, "Aboba", "М8О-313Б");

    when(repository.findById(studentId)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.deleteStudent(studentId);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void givenNonExistingStudentId_whenDeleteStudent_thenThrowNotFoundException() {
    Long studentId = 1337L;
    when(repository.findById(studentId)).thenReturn(Optional.empty());
    assertThrows(org.webjars.NotFoundException.class, () -> service.deleteStudent(studentId));
  }
}
