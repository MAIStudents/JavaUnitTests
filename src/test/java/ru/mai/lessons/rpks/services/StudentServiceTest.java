package ru.mai.lessons.rpks.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivanov", "M8O-411B");
    Student expectedModel = new Student(1L, "Ivanov", "M8O-411B");
    when(repository.findById(studentId)).thenReturn(Optional.of(expectedModel));
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.getStudent(studentId);

    assertEquals(expectedResponse, actualResponse);
    verify(repository).findById(studentId);
    verify(mapper).modelToResponse(expectedModel);
  }

  @Test
  @DisplayName("Тест на поиск несуществующего студента по идентификатору")
  void givenNonExistentStudentId_whenGetStudent_thenThrowNotFoundException() {
    Long studentId = 999L;
    when(repository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(studentId));
    verify(repository).findById(studentId);
  }

  @Test
  @DisplayName("Тест на создание студента")
  void givenStudentCreateRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("Ivanov", "M8O-411B");
    Student studentToSave = new Student(null, "Ivanov", "M8O-411B");
    Student savedStudent = new Student(1L, "Ivanov", "M8O-411B");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivanov", "M8O-411B");

    when(mapper.requestToModel(request)).thenReturn(studentToSave);
    when(repository.saveAndFlush(studentToSave)).thenReturn(savedStudent);
    when(mapper.modelToResponse(savedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
    verify(mapper).requestToModel(request);
    verify(repository).saveAndFlush(studentToSave);
    verify(mapper).modelToResponse(savedStudent);
  }

  @Test
  @DisplayName("Тест на обновление студента")
  void givenStudentUpdateRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Ivanov Updated", "M8O-411B");
    Student studentToUpdate = new Student(1L, "Ivanov Updated", "M8O-411B");
    Student updatedStudent = new Student(1L, "Ivanov Updated", "M8O-411B");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivanov Updated", "M8O-411B");

    when(mapper.requestToModel(request)).thenReturn(studentToUpdate);
    when(repository.saveAndFlush(studentToUpdate)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
    verify(mapper).requestToModel(request);
    verify(repository).saveAndFlush(studentToUpdate);
    verify(mapper).modelToResponse(updatedStudent);
  }

  @Test
  @DisplayName("Тест на удаление студента по идентификатору")
  void givenStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    Long studentId = 1L;
    Student studentToDelete = new Student(1L, "Ivanov", "M8O-411B");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivanov", "M8O-411B");

    when(repository.findById(studentId)).thenReturn(Optional.of(studentToDelete));
    when(mapper.modelToResponse(studentToDelete)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.deleteStudent(studentId);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
    verify(repository).findById(studentId);
    verify(repository).delete(studentToDelete);
    verify(mapper).modelToResponse(studentToDelete);
  }

  @Test
  @DisplayName("Тест на удаление несуществующего студента по идентификатору")
  void givenNonExistentStudentId_whenDeleteStudent_thenThrowNotFoundException() {
    Long studentId = 999L;
    when(repository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(studentId));
    verify(repository).findById(studentId);
  }
}