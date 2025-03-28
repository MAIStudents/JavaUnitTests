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

  private final Long studentId = 1L;
  private final StudentResponse studentResponse = new StudentResponse(studentId, "Fedorkov", "M8O-311Б");
  private final StudentCreateRequest createRequest = new StudentCreateRequest("Fedorkov", "M8O-311Б");
  private final StudentCreateRequest invalidCreateRequest = new StudentCreateRequest(null, null);
  private final StudentUpdateRequest updateRequest = new StudentUpdateRequest(studentId, "Fedorkov Alex", "M8O-311Б");

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное сохранение студента")
  void givenValidRequest_whenSaveStudent_thenReturnOk() {
    when(service.saveStudent(createRequest)).thenReturn(studentResponse);

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(createRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(studentResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное сохранение студента с невалидными данными")
  void givenInvalidRequest_whenSaveStudent_thenReturnUnprocessableEntity() {
    mockMvc
            .perform(
                    post("/student/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(invalidCreateRequest))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на поиск студента по его идентификатору")
  void givenStudentId_whenGetStudent_thenReturnStudentResponse() {
    when(service.getStudent(1L)).thenReturn(studentResponse);

    mockMvc
        .perform(
            get("/student/get")
                .param("id", studentId.toString())
        )
        .andExpect(status().isOk())
        .andExpect(content().string(JsonUtils.toJson(studentResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешный поиск студента с несуществующим ID")
  void givenInvalidId_whenGetStudent_thenReturnUnprocessableEntity() {
    when(service.getStudent(studentId)).thenThrow(NotFoundException.class);

    mockMvc
            .perform(
                    get("/student/get")
                            .param("id", studentId.toString())
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnOk() {
    when(service.updateStudent(updateRequest)).thenReturn(studentResponse);

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(updateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(studentResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное обновление несуществующего студента")
  void givenInvalidRequest_whenUpdateStudent_thenReturnUnprocessableEntity() {
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
    when(service.deleteStudent(studentId)).thenReturn(studentResponse);

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", studentId.toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(studentResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("тест на неуспешное удаление несуществующего студента")
  void givenInvalidId_whenDeleteStudent_thenReturnUnprocessableEntity() {
    when(service.deleteStudent(studentId)).thenThrow(NotFoundException.class);

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", studentId.toString())
            )
            .andExpect(status().isUnprocessableEntity());
  }
}
