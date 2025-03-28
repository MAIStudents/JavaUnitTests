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

  private final Long studentID = 1L;
  private final Student student = new Student(studentID, "Fedorkov", "M8O-311Б");
  private final Student invalidStudent = new Student(null, null, null);
  private final Student updatedStudent = new Student(studentID, "Fedorkov Alex", "M8O-311Б");
  private final StudentResponse studentResponse = new StudentResponse(studentID, "Fedorkov", "M8O-311Б");
  private final StudentResponse updatedStudentResponse = new StudentResponse(studentID, "Fedorkov Alex", "M8O-311Б");
  private final StudentCreateRequest createRequest = new StudentCreateRequest("Fedorkov", "M8O-311Б");
  private final StudentCreateRequest invalidCreateRequest = new StudentCreateRequest(null, null);
  private final StudentUpdateRequest updateRequest = new StudentUpdateRequest(studentID, "Fedorkov Alex", "M8O-311Б");
  private final StudentUpdateRequest invalidUpdateRequest = new StudentUpdateRequest(studentID, null, null);

  @Test
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidCreateRequest_whenSaveStudent_thenReturnResponse() {
    when(mapper.requestToModel(createRequest)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(student);
    when(mapper.modelToResponse(student)).thenReturn(studentResponse);

    StudentResponse result = service.saveStudent(createRequest);

    assertEquals(studentResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное сохранение студента ввиду невалидности его данных")
  void givenInvalidCreateRequest_whenSaveStudent_thenThrowException() {
    when(mapper.requestToModel(invalidCreateRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(invalidCreateRequest));
  }

  @Test
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    when(repository.findById(studentID)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(studentResponse);

    StudentResponse actualResponse = service.getStudent(studentID);

    assertEquals(studentResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента")
  void givenNotExistingStudentId_whenGetStudent_thenThrowException() {
    when(repository.findById(studentID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(studentID));
  }

  @Test
  @DisplayName("Тест на успешное обновление")
  void givenValidUpdateRequest_whenUpdateStudent_thenReturnUpdatedResponse() {
    when(mapper.requestToModel(updateRequest)).thenReturn(updatedStudent);
    when(repository.saveAndFlush(updatedStudent)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(updatedStudentResponse);

    StudentResponse result = service.updateStudent(updateRequest);

    assertEquals(updatedStudentResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное обновление студента")
  void givenInvalidUpdateRequest_whenUpdateStudent_thenThrowException() {
    when(mapper.requestToModel(invalidUpdateRequest)).thenReturn(invalidStudent);
    when(repository.saveAndFlush(invalidStudent)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.updateStudent(invalidUpdateRequest));
  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenExistingStudentId_whenDeleteStudent_thenReturnDeletedResponse() {
    when(repository.findById(studentID)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(studentResponse);

    StudentResponse result = service.deleteStudent(studentID);

    assertEquals(studentResponse, result);
  }

  @Test
  @DisplayName("Тест на неуспешное удаление студента")
  void givenNotExistingId_whenDeleteStudent_thenThrowException() {
    when(repository.findById(studentID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(studentID));
  }
}
