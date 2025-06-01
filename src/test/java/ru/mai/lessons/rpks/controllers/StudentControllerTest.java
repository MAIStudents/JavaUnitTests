package ru.mai.lessons.rpks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
  @DisplayName("Неуспешный тест поиска студента по его айди")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse_Negative() {
    when(service.getStudent(32424L)).thenThrow(new RuntimeException("Student not found"));

    mockMvc.perform(
                    get("/student/get")
                            .param("id", "32424")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("Student not found"));
  }

  @Test
  @SneakyThrows
  @DisplayName("Успешное сохранение студента")
  void givenStudent_whenSaveStudent_thenReturnStudentResponse_Positive() {
    StudentCreateRequest request = new StudentCreateRequest("fsfd", "111");
    StudentResponse expectedResponse = new StudentResponse(122L, "fsfd", "111");

    when(service.saveStudent(any())).thenReturn(expectedResponse);

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType("application/json")
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(request)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Сохранение студента с неправильными данными")
  void givenInvalidStudent_whenSaveStudent_thenReturnStudentResponse_Negative() {
    StudentCreateRequest invalid = new StudentCreateRequest("", "");

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType("application/json")
                            .content(JsonUtils.toJson(invalid))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Ошибка сохранения студента")
  void givenValidStudent_whenSaveStudent_thenReturnErrorResponse_Negative() {
    StudentCreateRequest validRequest = new StudentCreateRequest("adasd", "111");

    when(service.saveStudent(any())).thenThrow(new RuntimeException("Error saving student"));

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType("application/json")
                            .content(JsonUtils.toJson(validRequest))
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("Error saving student"));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное обновление студента")
  void givenValidStudentUpdateRequest_whenUpdateStudent_thenReturnStudentResponse_Positive() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Boba", "111");
    StudentResponse expectedResponse = new StudentResponse(1L, "Boba", "111");

    when(service.updateStudent(any())).thenReturn(expectedResponse);

    mockMvc.perform(
                    put("/student/update")
                            .contentType("application/json")
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное обновление студента")
  void givenInvalidUpdateRequest_whenUpdateStudent_thenReturnErrorResponse_Negative() {
    StudentUpdateRequest request = new StudentUpdateRequest(9944L, "Invalid", "Group");

    when(service.updateStudent(any())).thenThrow(new RuntimeException("Student not found"));

    mockMvc.perform(
                    put("/student/update")
                            .contentType("application/json")
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("Student not found"));
  }


  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное удаление студента")
  void givenStudentId_whenDeleteStudent_thenReturnStudentResponse_Positive() {
    StudentResponse response = new StudentResponse();

    when(service.deleteStudent(1321L)).thenReturn(response);

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", "1321")
            )
            .andExpect(status().isOk())
            .andExpect(content().json(JsonUtils.toJson(response)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Удаление несуществующего студента")
  void givenNonExistingStudentId_whenDeleteStudent_thenReturnErrorResponse_Negative() {
    when(service.deleteStudent(1111L)).thenThrow(new RuntimeException("Student not found"));

    mockMvc.perform(
                    delete("/student/delete")
                            .param("id", "1111")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error").value("Student not found"));
  }

}
