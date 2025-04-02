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


  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidRequest_whenSaveStudent_thenReturnOk() {
    StudentCreateRequest request = new StudentCreateRequest("Fedorkov", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");
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
  @DisplayName("Тест на неуспешное сохранение студента с невалидными данными")
  void givenInvalidRequest_whenSaveStudent_thenReturnUnprocessableEntity() {
    StudentCreateRequest invalidRequest = new StudentCreateRequest(null, null);
    mockMvc
            .perform(
                    post("/student/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(invalidRequest))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");
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
  @DisplayName("Тест на неуспешный поиск студента с несуществующим ID")
  void givenInvalidId_whenGetStudent_thenReturnUnprocessableEntity() {
    when(service.getStudent(1L)).thenThrow(NotFoundException.class);

    mockMvc
            .perform(
                    get("/student/get")
                            .param("id", "1")
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnOk() {
    StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Fedorkov Alex", "M8O-311Б");
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");
    when(service.updateStudent(updateRequest)).thenReturn(expectedResponse);

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное обновление несуществующего студента")
  void givenInvalidRequest_whenUpdateStudent_thenReturnUnprocessableEntity() {
    StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Fedorkov Alex", "M8O-311Б");
    when(service.updateStudent(updateRequest)).thenThrow(NotFoundException.class);

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(updateRequest))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("тест на успешное удаление студента")
  void givenValidId_whenDeleteStudent_thenReturnOk() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Fedorkov", "M8O-311Б");
    when(service.deleteStudent(1L)).thenReturn(expectedResponse);

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", "1")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("тест на неуспешное удаление несуществующего студента")
  void givenInvalidId_whenDeleteStudent_thenReturnUnprocessableEntity() {
    when(service.deleteStudent(1L)).thenThrow(NotFoundException.class);

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", "1")
            )
            .andExpect(status().isUnprocessableEntity());
  }
}
