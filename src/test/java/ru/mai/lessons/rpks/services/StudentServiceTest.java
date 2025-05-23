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
import org.webjars.NotFoundException;
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
    StudentResponse expectedResponse = new StudentResponse(1L, "Domoroschenov", "М8О-411Б");
    Student expectedModel = new Student(1L, "Domoroschenov", "М8О-411Б");
    when(repository.findById(studentId)).thenReturn(Optional.of(expectedModel));
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.getStudent(studentId);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на сохранение студента")
  void givenValidRequest_whenSaveStudent_thenReturnOk() {
    StudentCreateRequest request = new StudentCreateRequest("Ivkovich", "M8O-311Б");
    Student modelNoId = new Student(null, "Ivkovich", "M8O-311Б");
    Student modelWithId = new Student(1L, "Ivkovich", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivkovich", "M8O-311Б");

    when(mapper.requestToModel(request)).thenReturn(modelNoId);
    when(repository.saveAndFlush(modelNoId)).thenReturn(modelWithId);
    when(mapper.modelToResponse(modelWithId)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnOk() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Ivkovich", "M8O-311Б");
    Student model = new Student(1L, "Ivkovich", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivkovich", "M8O-311Б");

    when(mapper.requestToModel(request)).thenReturn(model);
    when(repository.saveAndFlush(model)).thenReturn(model);
    when(mapper.modelToResponse(model)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на удаление студента")
  void givenStudentId_whenDeleteStudent_thenReturnOk() {
    Student model = new Student(1L, "Ivkovich", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivkovich", "M8O-311Б");

    when(repository.findById(1L)).thenReturn(Optional.of(model));
    when(mapper.modelToResponse(model)).thenReturn(expectedResponse);

    StudentResponse result = service.deleteStudent(1L);

    assertEquals(expectedResponse, result);
  }

  // Negative tests

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNonexistentId_whenGetStudent_thenThrowNotFoundException() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(1L));
  }

  @Test
  @DisplayName("Тест на сохранение невалидного студента")
  void givenInvalidRequest_whenSaveStudent_thenThrowIllegalArgumentException() {
    StudentCreateRequest invalidRequest = new StudentCreateRequest(null, null);
    Student invalidStudent = new Student(null, null, null);

    when(mapper.requestToModel(invalidRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(invalidRequest));
  }

  @Test
  @DisplayName("Тест на обновление невалидного студента")
  void givenInvalidRequest_whenUpdateStudent_thenThrowIllegalArgumentException() {
    StudentUpdateRequest invalidRequest = new StudentUpdateRequest(null, null, null);
    Student invalidStudent = new Student(null, null, null);

    when(mapper.requestToModel(invalidRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.updateStudent(invalidRequest));
  }

  @Test
  @DisplayName("Тест на удаление невалидного студента")
  void givenInvalidRequest_whenDeleteStudent_thenThrowNotFoundException() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(1L));
  }
}
