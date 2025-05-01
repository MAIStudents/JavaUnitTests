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
  @DisplayName("Тест на успешный поиск студента по идентификатору")
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
  @DisplayName("Тест на успешное создание студента")
  void givenValidRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request     = new StudentCreateRequest(  "Domoroschenov", "М8О-411Б");
    Student savedStudent             = new Student(1L,         "Domoroschenov", "М8О-411Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Domoroschenov", "М8О-411Б");

    when(mapper.requestToModel(request)).thenReturn(savedStudent);
    when(repository.saveAndFlush(savedStudent)).thenReturn(savedStudent);
    when(mapper.modelToResponse(savedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentUpdateRequest request     = new StudentUpdateRequest(1L, "Domoroschenov", "М8О-411Б");
    Student updatedStudent           = new Student(1L, "Domoroschenov", "М8О-411Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Domoroschenov", "М8О-411Б");

    when(mapper.requestToModel(request)).thenReturn(updatedStudent);
    when(repository.saveAndFlush(updatedStudent)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на успешное удаление студента по идентификатору")
  void givenValidStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    Student student = new Student(1L, "Domoroschenov", "М8О-411Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Domoroschenov", "М8О-411Б");

    when(repository.findById(1L)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.deleteStudent(1L);

    assertEquals(expectedResponse, actualResponse);
  }


  //  ---------------------------------------- NEGATIVE TEST ----------------------------------------------------


  @Test
  @DisplayName("Тест на неуспешное создание студента")
  void givenInvalidRequest_whenSaveStudent_thenThrowException() {
    StudentCreateRequest request = new StudentCreateRequest(null, null);
    when(mapper.requestToModel(request)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(request));
  }

  @Test
  @DisplayName("Тест на обновление несуществующего студента")
  void givenInvalidStudentId_whenUpdateStudent_thenThrowException() {
    StudentUpdateRequest request = new StudentUpdateRequest(null, "Domoroschenov", "М8О-411Б");
    Student student = new Student(2L, "Brazhkin))", "М8О-411Б");

    when(mapper.requestToModel(request)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenThrow(new NotFoundException("Студент по данному идентификатору не найден"));

    assertThrows(NotFoundException.class, () -> service.updateStudent(request));
  }

  @Test
  @DisplayName("Тест на получение студента невалидному по идентификатору")
  void givenInvalidId_whenGetStudent_thenThrowException() {
    Long invalidID = 1L;

    when(repository.findById(invalidID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(invalidID));
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента")
  void givenInvalidStudentId_whenDeleteStudent_thenThrowException() {
    Long invalidID = 1L;

    when(repository.findById(invalidID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(invalidID));
  }
}
