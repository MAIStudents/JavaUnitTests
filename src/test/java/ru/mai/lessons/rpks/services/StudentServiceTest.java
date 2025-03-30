package ru.mai.lessons.rpks.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
  @DisplayName("Тест на получение студента по идентификатору, но неуспешно")
  void givenInvalidId_whenGetStudent_thenThrowNotFoundException() {
    Long studentId = 1234L;

    when(repository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getStudent(studentId));
  }


  @Test
  @DisplayName("Тест на создание студента")
  void givenValidRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("Zaslavtsev", "М8О-311Б-22");
    Student student = new Student(null, "Zaslavtsev", "М8О-311Б-22");
    Student savedStudent = new Student(1L, "Zaslavtsev", "М8О-311Б-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Zaslavtsev", "М8О-311Б-22");

    when(mapper.requestToModel(request)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(savedStudent);
    when(mapper.modelToResponse(savedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.saveStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на создание студента c ошибкой при сохранении")
  void givenInvalidRequest_whenSaveStudent_thenThrowException() {
    StudentCreateRequest request = new StudentCreateRequest(null, null);
    when(mapper.requestToModel(request)).thenThrow(new IllegalArgumentException("Invalid student data"));

    assertThrows(IllegalArgumentException.class, () -> service.saveStudent(request));
  }

  @Test
  @DisplayName("Тест на обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnUpdatedStudentResponse() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Zaslavtsev", "М8О-311Б-22");
    Student student = new Student(1L, "Zaslavtsev", "М8О-311Б-22");
    Student updatedStudent = new Student(1L, "Zaslavtsev", "М8О-311Б-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Zaslavtsev", "М8О-311Б-22");

    when(mapper.requestToModel(request)).thenReturn(student);
    when(repository.saveAndFlush(student)).thenReturn(updatedStudent);
    when(mapper.modelToResponse(updatedStudent)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.updateStudent(request);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на обновление студента (студент не найден)")
  void givenInvalidStudentId_whenUpdateStudent_thenThrowException() {

    StudentUpdateRequest request = new StudentUpdateRequest(1234L, "WHOIS", "KKK");

    Student student = new Student(999L, "WHOIS", "KKK");
    when(mapper.requestToModel(request)).thenReturn(student);

    when(repository.saveAndFlush(student)).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> service.updateStudent(request));
  }


  @Test
  @DisplayName("Тест на успешное удаление студента")
  void givenValidStudentId_whenDeleteStudent_thenReturnDeletedStudentResponse() {
    Student student = new Student(1L, "Zaslavtsev", "М8О-311Б-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Zaslavtsev", "М8О-311Б-22");

    when(repository.findById(1L)).thenReturn(Optional.of(student));
    when(mapper.modelToResponse(student)).thenReturn(expectedResponse);

    StudentResponse actualResponse = service.deleteStudent(1L);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @DisplayName("Тест на неуспешное удаление студента (не существует)")
  void givenInvalidStudentId_whenDeleteStudent_thenThrowNotFoundException() {
    Long nonExistingId = 1234L;

    when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteStudent(nonExistingId));
  }




}
