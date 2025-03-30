package ru.mai.lessons.rpks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.webjars.NotFoundException;
import ru.mai.lessons.rpks.controllers.impl.StudentControllerImpl;
import ru.mai.lessons.rpks.dto.requests.StudentCreateRequest;
import ru.mai.lessons.rpks.dto.requests.StudentUpdateRequest;
import ru.mai.lessons.rpks.dto.respones.StudentResponse;
import ru.mai.lessons.rpks.services.StudentService;
import ru.mai.lessons.rpks.utils.JsonUtils;

@AutoConfigureMockMvc
@WebMvcTest(StudentControllerImpl.class)
@TestPropertySource(properties = "server.port=8080")
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private StudentService service;

  // Тестим get
  @Test
  @SneakyThrows
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse();
    when(service.getStudent(1L)).thenReturn(expectedResponse);

    mockMvc
        .perform(
            get("/student/get")
                .param("id", "1")
        )
        .andExpect(status().isOk())
        .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на поиск студента по его идентификатору - студент не найден")
  void givenInvalidStudentId_whenGetStudent_thenReturnEmptyStudentResponse() {
    when(service.getStudent(1234L)).thenThrow(new NotFoundException("Студент не найден"));

    mockMvc
            .perform(get("/student/get").param("id", "1234"))
            .andExpect(status().isUnprocessableEntity());
  }

  // Тестим save
  @Test
  @SneakyThrows
  @DisplayName("Тест на создание студента - успешно")
  void givenValidStudentRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("Zaslavtsev", "М8О-311Б-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Zaslavtsev", "М8О-311Б-22");

    when(service.saveStudent(request)).thenReturn(expectedResponse);

    mockMvc
            .perform(post("/student/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на создание студента - неуспешно (null)")
  void givenNullRequest_whenSaveStudent_thenReturnBadRequest() {
    mockMvc
            .perform(post("/student/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(null)))
            .andExpect(status().isUnprocessableEntity());
  }

  // Тестим update
  @Test
  @SneakyThrows
  @DisplayName("Тест на обновление студента - успешно")
  void givenValidRequest_whenUpdateStudent_thenReturnUpdatedStudentResponse() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Zaslavtsev", "М8О-311Б-22");
    StudentResponse expectedResponse = new StudentResponse(1L, "Zaslavtsev", "М8О-311Б-22");

    when(service.updateStudent(request)).thenReturn(expectedResponse);

    mockMvc
            .perform(put("/student/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на обновление студента - неуспешно (null)")
  void givenNullRequest_whenUpdateStudent_thenReturnBadRequest() {
    mockMvc
            .perform(put("/student/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(null)))
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на удаление студента - успешно")
  void givenValidId_whenDeleteStudent_thenReturnDeletedStudentResponse() {
    Long studentId = 1L;
    StudentResponse expectedResponse = new StudentResponse(studentId, "Arnautov", "М8О-211Б-22");

    when(service.deleteStudent(studentId)).thenReturn(expectedResponse);

    mockMvc
            .perform(delete("/student/delete")
                    .param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на удаление студента - неуспешно (студент не найден)")
  void givenInvalidId_whenDeleteStudent_thenReturnNotFound() {
    Long studentId = 1234L;
    when(service.deleteStudent(studentId)).thenThrow(new NotFoundException("Студент не найден"));

    mockMvc
            .perform(delete("/student/delete")
                    .param("id", "1234"))
            .andExpect(status().isUnprocessableEntity());
  }








}
