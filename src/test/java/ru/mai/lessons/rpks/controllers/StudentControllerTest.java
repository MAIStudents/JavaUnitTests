package ru.mai.lessons.rpks.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Raj", "M8O-411B");
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
  @DisplayName("Тест на поиск несуществующего студента по идентификатору")
  void givenNonExistentStudentId_whenGetStudent_thenReturnNotFound() {
    when(service.getStudent(999L)).thenThrow(new NotFoundException("Студент не найден"));

    mockMvc
            .perform(
                    get("/student/get")
                            .param("id", "999")
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на создание студента")
  void givenStudentCreateRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest request = new StudentCreateRequest("Marshall Mathers", "42");
    StudentResponse expectedResponse = new StudentResponse(1L, "Marshall Mathers", "42");
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
  @DisplayName("Тест на создание студента с невалидными данными")
  void givenInvalidStudentCreateRequest_whenSaveStudent_thenReturnBadRequest() {
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
  @DisplayName("Тест на обновление студента")
  void givenStudentUpdateRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentUpdateRequest request = new StudentUpdateRequest(1L, "Updated", "123");
    StudentResponse expectedResponse = new StudentResponse(1L, "Updated", "123");
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
  @DisplayName("Тест на обновление студента с невалидными данными")
  void givenInvalidStudentUpdateRequest_whenUpdateStudent_thenReturnBadRequest() {
    StudentUpdateRequest invalidRequest = new StudentUpdateRequest(null, null, null);

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(invalidRequest))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на удаление студента по идентификатору")
  void givenStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivanov", "M8O-411B");
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
  @DisplayName("Тест на удаление несуществующего студента по идентификатору")
  void givenNonExistentStudentId_whenDeleteStudent_thenReturnNotFound() {
    when(service.deleteStudent(999L)).thenThrow(new NotFoundException("Студент не найден"));

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", "999")
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на получение студента без идентификатора")
  void givenNoStudentId_whenGetStudent_thenReturnBadRequest() {
    mockMvc
            .perform(get("/student/get"))
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на удаление студента без идентификатора")
  void givenNoStudentId_whenDeleteStudent_thenReturnBadRequest() {
    mockMvc
            .perform(delete("/student/delete"))
            .andExpect(status().isUnprocessableEntity());
  }
}