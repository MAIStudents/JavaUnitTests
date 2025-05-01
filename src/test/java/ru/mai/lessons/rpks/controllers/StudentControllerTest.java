package ru.mai.lessons.rpks.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
  @DisplayName("Тест на успешный поиск студента по его идентификатору")
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
  @DisplayName("Тест на успешное удаление студента по его идентификатору")
  void givenStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivan Ivanov", "M8O-231-31B");
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
  @DisplayName("Тест на успешное добавление студента")
  void givenValidRequest_whenSaveStudent_thenReturnResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivan Ivanov", "M8O-231-31B");
    StudentCreateRequest request = StudentCreateRequest.builder().fullName("Ivan Ivanov").groupName("M8O-231-31B").build();
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
  @DisplayName("Тест на успешное обновление студента")
  void givenValidRequest_whenUpdateStudent_thenReturnResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "Ivan Ivanov", "M8O-231-31B");
    StudentUpdateRequest request = StudentUpdateRequest.builder().id(1L).fullName("Ivan Ivanov").groupName("M8O-231-31B").build();
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


  //  ---------------------------------------- NEGATIVE TEST ----------------------------------------------------


  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешный поиск студента по идентификатору")
  void givenInvalidStudentId_whenGetStudent_thenThrowUnprocessableEntity() {
    when(service.getStudent(1L)).thenThrow(new RuntimeException("Пользователь с таким идентификатором не найден"));

    mockMvc
            .perform(
                    get("/student/get")
                            .param("id", "1")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("Пользователь с таким идентификатором не найден")));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное удаление студента по его идентификатору")
  void givenInvalidStudentId_whenDeleteStudent_thenThrowUnprocessableEntity() {
    when(service.deleteStudent(1L)).thenThrow(new RuntimeException("Пользователь с таким идентификатором не найден"));

    mockMvc
            .perform(
                    delete("/student/delete")
                            .param("id", "1")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("Пользователь с таким идентификатором не найден")));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное добавление студента")
  void givenInvalidStudentRequest_whenSaveStudent_thenThrowUnprocessableEntity() {
    StudentCreateRequest request = StudentCreateRequest.builder().fullName("").groupName("M8O-231-31B").build();

    mockMvc
            .perform(
                    post("/student/save")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isUnprocessableEntity()); // Страшный message возвращается, поэтому проверяем только статус

  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное обновление студента")
  void givenInvalidStudentRequest_whenUpdateStudent_thenThrowUnprocessableEntity() {
    StudentUpdateRequest request = StudentUpdateRequest.builder().id(4L).fullName("Ivan Ivanov").groupName("M8O-231-31B").build();
    when(service.updateStudent(request)).thenThrow(new RuntimeException("Пользователь с таким идентификатором не найден"));

    mockMvc
            .perform(
                    put("/student/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtils.toJson(request))
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string(containsString("Пользователь с таким идентификатором не найден")));
  }
}
