package ru.mai.lessons.rpks.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidCreateRequest_whenSaveStudent_thenReturnResponse() {
    StudentCreateRequest createRequest = new StudentCreateRequest("Fedorkov", "M8O-311Б");
    Student student = new Student(1L, "Fedorkov", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");

    when(mapper.requestToModel(createRequest)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(student);
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse result = service.saveStudent(createRequest);

    assertEquals(expectedResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное сохранение студента ввиду невалидности его данных")
  void givenInvalidCreateRequest_whenSaveStudent_thenThrowException() {
    StudentCreateRequest invalidCreateRequest = new StudentCreateRequest(null, null);
    Student invalidStudent = new Student(null, null, null);

    when(mapper.requestToModel(invalidCreateRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(invalidCreateRequest));
  }

  @Test
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    Student student = new Student(1L, "Fedorkov", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");

    when(repository.findById(1L)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.getStudent(1L);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNotExistingStudentId_whenGetStudent_thenThrowException() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(1L));
  }

  @Test
  @DisplayName("Тест на успешное обновление")
  void givenValidUpdateRequest_whenUpdateStudent_thenReturnUpdatedResponse() {
    StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Fedorkov Alex", "M8O-311Б");
    Student updatedStudent = new Student(1L, "Fedorkov Alex", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov Alex", "M8O-311Б");

    when(mapper.requestToModel(updateRequest)).thenReturn(updatedStudent);
    when(repository.saveAndFlush(updatedStudent)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(expectedResponse);

    StudentResponse result = service.updateStudent(updateRequest);

    assertEquals(expectedResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное обновление студента")
  void givenInvalidUpdateRequest_whenUpdateStudent_thenThrowException() {
    StudentUpdateRequest invalidUpdateRequest = new StudentUpdateRequest(1L, null, null);
    Student invalidStudent = new Student(null, null, null);

    when(mapper.requestToModel(invalidUpdateRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.updateStudent(invalidUpdateRequest));
  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenExistingStudentId_whenDeleteStudent_thenReturnDeletedResponse() {
    Student student = new Student(1L, "Fedorkov", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");

    when(repository.findById(1L)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse result = service.deleteStudent(1L);

    assertEquals(expectedResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное удаление студента")
  void givenNotExistingId_whenDeleteStudent_thenThrowException() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(1L));
  }
}
