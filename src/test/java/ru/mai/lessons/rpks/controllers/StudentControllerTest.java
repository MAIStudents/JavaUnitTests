package ru.mai.lessons.rpks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

  @Test
  @SneakyThrows
  @DisplayName("Тест на сохранение студента")
  void givenValidRequest_whenSaveStudent_thenReturnOk() {
    StudentCreateRequest request = new StudentCreateRequest("Ivkovich", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivkovich", "M8O-311Б");
    when(service.saveStudent(request)).thenReturn(expectedResponse);

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnOk() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Ivkovich", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivkovich", "M8O-311Б");
    when(service.updateStudent(request)).thenReturn(expectedResponse);

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

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
  @DisplayName("Тест на удаление студента")
  void givenStudentId_whenDeleteStudent_thenReturnOk() {
    StudentResponse expectedResponse = new StudentResponse();
    when(service.deleteStudent(1L)).thenReturn(expectedResponse);

    mockMvc
        .perform(
            delete("/student/delete")
                .param("id", "1")
        )
        .andExpect(status().isOk())
        .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  // Negative tests

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное добавление студента")
  void givenInvalidRequest_whenSaveStudent_thenThrowUnprocessableEntity() {
    StudentCreateRequest request = new StudentCreateRequest(null, "M8O-311Б");

    mockMvc
        .perform(
            post("/student/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request))
        )
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное обновление студента")
  void givenInvalidRequest_whenUpdateStudent_thenThrowUnprocessableEntity() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, null, "M8O-311Б");
    when(service.updateStudent(request)).thenThrow(NotFoundException.class);

    mockMvc
        .perform(
            put("/student/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request))
        )
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное удаление студента")
  void givenInvalidRequest_whenDeleteStudent_thenThrowUnprocessableEntity() {
    when(service.deleteStudent(1L)).thenThrow(NotFoundException.class);

    mockMvc
        .perform(
            delete("/student/delete")
                .param("id", "1")
        )
        .andExpect(status().isUnprocessableEntity());
  }
}
