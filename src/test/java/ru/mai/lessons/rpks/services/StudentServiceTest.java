package ru.mai.lessons.rpks.services;


import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Тест на поиск студента по идентификатору")
    void givenInvalidStudentId_whenGetStudent_thenThrowEntityNotFoundException() {

        Long invalidStudentId = 999L;

        when(repository.findById(invalidStudentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getStudent(invalidStudentId));

        assertEquals("Студент не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Тест на удаление студента по идентификатору")
    void givenStudentId_whenDeleteStudent_thenStudentIsDeleted() {
        Long studentId = 1L;
        Student student = new Student(1L, "Lisnyak", "М8О-313Б");

        when(repository.findById(studentId)).thenReturn(Optional.of(student));
        doNothing().when(repository).delete(student);

        assertDoesNotThrow(() -> service.deleteStudent(studentId));


        when(repository.findById(studentId)).thenReturn(Optional.empty());
        Optional<Student> deletedStudent = repository.findById(studentId);

        assertTrue(deletedStudent.isEmpty(), "Студент должен быть удалён");
    }

    @Test
    @DisplayName("Тест на удаление студента по идентификатору")
    void givenInvalidStudentId_whenDeleteStudent_thenThrowNotFoundException() {
        Long invalidStudentId = 999L;

        when(repository.findById(invalidStudentId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.deleteStudent(invalidStudentId));

        assertEquals("Студент не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Тест на успешное обновление данных студента")
    void givenValidStudentUpdateRequest_whenUpdateStudent_thenReturnUpdatedStudent() {

        Student student = new Student(1L, "Lisnyak", "М8О-313Б");


        StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Ivanov", "М8О-311Б");


        when(mapper.requestToModel(updateRequest)).thenReturn(student);
        when(repository.saveAndFlush(student)).thenReturn(student);
        when(mapper.modelToResponse(student)).thenReturn(new StudentResponse(1L, "Ivanov", "М8О-311Б"));


        StudentResponse updatedStudent = service.updateStudent(updateRequest);


        assertNotNull(updatedStudent);
        assertEquals(1L, updatedStudent.getId());
        assertEquals("Ivanov", updatedStudent.getFullName());
        assertEquals("М8О-311Б", updatedStudent.getGroupName());


        verify(mapper).requestToModel(updateRequest);
        verify(repository).saveAndFlush(student);
        verify(mapper).modelToResponse(student);
    }

    @Test
    @DisplayName("Тест на неуспешное обновление данных студента")
    void givenValidStudentUpdateRequest_whenUpdateFails_thenThrowException() {

        Student student = new Student(1L, "Lisnyak", "М8О-313Б");
        StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Ivanov", "М8О-311Б");


        when(mapper.requestToModel(updateRequest)).thenReturn(student);
        when(repository.saveAndFlush(student)).thenThrow(new RuntimeException("Ошибка при обновлении студента"));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.updateStudent(updateRequest));
        assertEquals("Ошибка при обновлении студента", exception.getMessage());


        verify(mapper).requestToModel(updateRequest);
        verify(repository).saveAndFlush(student);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    @DisplayName("Тест на успешное сохранение студента")
    void givenValidStudentRequest_whenSaveStudent_thenReturnSavedStudentResponse() {

        StudentCreateRequest createRequest = new StudentCreateRequest("Lisnyak", "М8О-313Б");


        Student savedStudent = new Student(1L, "Lisnyak", "М8О-313Б");
        StudentResponse expectedResponse = new StudentResponse(1L, "Lisnyak", "М8О-313Б");


        when(mapper.requestToModel(createRequest)).thenReturn(savedStudent);
        when(repository.saveAndFlush(savedStudent)).thenReturn(savedStudent);
        when(mapper.modelToResponse(savedStudent)).thenReturn(expectedResponse);


        StudentResponse actualResponse = service.saveStudent(createRequest);


        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);


        verify(mapper).requestToModel(createRequest);
        verify(repository).saveAndFlush(savedStudent);
        verify(mapper).modelToResponse(savedStudent);
    }

    @Test
    @DisplayName("Тест на неуспешное сохранение студента")
    void givenInvalidStudentRequest_whenSaveStudent_thenThrowException() {

        StudentCreateRequest createRequest = new StudentCreateRequest("", "");


        when(mapper.requestToModel(createRequest)).thenThrow(new IllegalArgumentException("Некорректные данные студента"));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.saveStudent(createRequest));
        assertEquals("Некорректные данные студента", exception.getMessage());


        verify(mapper).requestToModel(createRequest);
        verifyNoInteractions(repository);
        verifyNoMoreInteractions(mapper);
    }


}
