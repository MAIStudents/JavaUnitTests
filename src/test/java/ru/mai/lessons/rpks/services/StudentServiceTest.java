package ru.mai.lessons.rpks.services;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
  @DisplayName("Тест на неуспешный поиск студента по его идентификатору")
  void givenInvalidStudent_whenFindById_thenThrow() {
    Long studentId = 1L;
    when(repository.findById(any(Long.class))).thenThrow(new NotFoundException("Студент не найден"));
    assertThrows(NotFoundException.class, () -> service.getStudent(studentId));
  }

  @Test
  @DisplayName("Тест на успешное добавление студента")
  void givenValidStudent_whenSave_thenReturnStudentResponse() {

    StudentCreateRequest studentCreateRequest = StudentCreateRequest.builder()
            .fullName("Heizenberg")
            .groupName("M8O-311B-22")
            .build();
    Student expectedModel = new Student(1L, "Heizenberg", "M8O-311B-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Heizenberg", "M8O-311B-22");
    when(repository.saveAndFlush(expectedModel)).thenReturn(expectedModel);
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);
    when(mapper.requestToModel(studentCreateRequest)).thenReturn(expectedModel);
    assertEquals(expectedResponse, service.saveStudent(studentCreateRequest));
  }

  @Test
  @DisplayName("Тест на неуспешное добавление студента")
  void givenInvalidStudent_whenSave_thenThrowException() {
    StudentCreateRequest studentCreateRequest = new StudentCreateRequest();
    Student expectedModel = new Student(1L, null, null);
    when(repository.saveAndFlush(expectedModel)).thenThrow(new IllegalArgumentException());
    when(mapper.requestToModel(studentCreateRequest)).thenReturn(expectedModel);
    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(studentCreateRequest));
  }

  @Test
  @DisplayName("Тест на успешное обновление студента")
  void givenValidStudent_whenUpdate_thenReturnStudentResponse() {

    StudentUpdateRequest studentUpdateRequest = StudentUpdateRequest.builder()
            .fullName("Heizenberg")
            .groupName("M8O-311B-22")
            .build();
    Student expectedModel = new Student(1L, "Heizenberg", "M8O-311B-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Heizenberg", "M8O-311B-22");
    when(repository.saveAndFlush(expectedModel)).thenReturn(expectedModel);
    when(mapper.modelToResponse(expectedModel)).thenReturn(expectedResponse);
    when(mapper.requestToModel(studentUpdateRequest)).thenReturn(expectedModel);
    assertEquals(expectedResponse, service.updateStudent(studentUpdateRequest));
  }

  @Test
  @DisplayName("Тест на неуспешное обновление студента")
  void givenInvalidStudent_whenUpdate_thenThrowException() {
    StudentUpdateRequest studentUpdateRequest = new StudentUpdateRequest();
    Student expectedModel = new Student();
    when(repository.saveAndFlush(expectedModel)).thenThrow(new IllegalArgumentException());
    when(mapper.requestToModel(studentUpdateRequest)).thenReturn(expectedModel);
    assertThrows(IllegalArgumentException.class, () -> service.updateStudent(studentUpdateRequest));
  }

  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenValidStudent_whenDelete_thenReturnStudentResponse() {
    Student expectedModel = new Student(1L, "Heizenberg", "M8O-311B-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Heizenberg", "M8O-311B-22");
    when(repository.findById(expectedModel.getId())).thenReturn(Optional.of(expectedModel));
    assertDoesNotThrow(() -> service.deleteStudent(expectedResponse.getId()));
  }

  @Test
  @DisplayName("Тест на неуспешное удаление студента")
  void givenInvalidStudent_whenDelete_thenThrowException() {
    Student expectedModel = new Student(1L, "Heizenberg", "M8O-311B-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Heizenberg", "M8O-311B-22");
    when(repository.findById(expectedModel.getId())).thenReturn(Optional.empty());
    assertThrows(Exception.class, () -> service.deleteStudent(expectedResponse.getId()));
  }
}
