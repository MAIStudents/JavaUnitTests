package ru.mai.lessons.rpks.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.webjars.NotFoundException;
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
    StudentResponse expectedResponse = new StudentResponse(1L, "Domoroschenov", "М8О-411Б");
    Student expectedModel = new Student(1L, "Domoroschenov", "М8О-411Б");
    when(repository.findById(studentId)).thenReturn(Optional.of(expectedModel));
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.getStudent(studentId);

    assertEquals(expectedResponse, actualResponse);
  }


  @Test
  @DisplayName("Test for successful student creation.")
  void givenValidRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("fullName", "groupName");
    Student savedStudent = new Student(1L, "fullName", "groupName");
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");

    when(mapper.requestToModel(request)).thenReturn(savedStudent);
    when(repository.saveAndFlush(savedStudent)).thenReturn(savedStudent);
    when(mapper.modelToResponse(savedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }


  @Test
  @DisplayName("Test for successful student update.")
  void givenValidRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "fullName", "groupName");
    Student updatedStudent = new Student(1L, "fullName", "groupName");
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");

    when(mapper.requestToModel(request)).thenReturn(updatedStudent);
    when(repository.saveAndFlush(updatedStudent)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }


  @Test
  @DisplayName("Test for successful deletion of a student by ID.")
  void givenValidStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    Student student = new Student(1L, "fullName", "groupName");
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");

    when(repository.findById(1L)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.deleteStudent(1L);

    assertEquals(expectedResponse, actualResponse);
  }


  @Test
  @DisplayName("Test for unsuccessful student creation.")
  void givenInvalidRequest_whenSaveStudent_thenThrow() {
    StudentCreateRequest request = new StudentCreateRequest(null, null);
    when(mapper.requestToModel(request)).thenThrow(new IllegalArgumentException());

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(request));
  }


  @Test
  @DisplayName("Test for retrieving a student with an invalid identifier.")
  void givenInvalidId_whenGetStudent_thenThrow() {
    Long invalidID = 1L;

    when(repository.findById(invalidID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(invalidID));
  }


  @Test
  @DisplayName("Test for deleting a non-existent student.")
  void givenInvalidStudentId_whenDeleteStudent_thenThrow() {
    Long invalidID = 1L;

    when(repository.findById(invalidID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(invalidID));
  }
}