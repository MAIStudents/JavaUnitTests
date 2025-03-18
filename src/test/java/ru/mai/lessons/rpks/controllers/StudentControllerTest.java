package ru.mai.lessons.rpks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
  @DisplayName("Тест на неуспешный поиск студента без параметров")
  void givenEmptyParamsRequest_whenGetStudent_thenReturnUnprocessableEntity() {
    String expectedResult = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "error": "Required request parameter 'id' for method parameter type Long is not present"
              }
            """;
    mockMvc
            .perform(
                    get("/student/get")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(expectedResult));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешный поиск студента по несуществующему идентификатору")
  void givenInvalidStudentId_whenGetStudent_thenReturnUnprocessableEntity() {
    when(service.getStudent(1L)).thenThrow(new NotFoundException("Студент не найден"));
    String expectedResult = """
            {
              "status": "UNPROCESSABLE_ENTITY",
              "error": "Студент не найден"
              }
            """;
    mockMvc
            .perform(
                    get("/student/get")
                            .param("id", "1")
            )
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(expectedResult));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное добавление студента")
  void givenValidStudentCreateRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentCreateRequest studentCreateRequest = StudentCreateRequest.builder()
            .groupName("M8O-311B-22")
            .fullName("Heizenburger")
            .build();
    StudentResponse expectedResponse = StudentResponse.builder()
            .id(1L)
            .fullName("Heizenburger")
            .groupName("M8O-311B-22")
            .build();
    when(service.saveStudent(studentCreateRequest)).thenReturn(expectedResponse);
    mockMvc.perform(post("/student/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtils.toJson(studentCreateRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное добавление студента")
  void givenInvalidStudentCreateRequest_whenSaveStudent_thenReturnUnprocessableEntity() {
    StudentCreateRequest studentCreateRequest = StudentCreateRequest.builder()
            .groupName("")
            .fullName("")
            .build();
    mockMvc.perform(post("/student/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(studentCreateRequest)))
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное обновление данных студента")
  void givenInvalidStudentUpdateRequest_whenUpdateStudent_thenReturnUnprocessableEntity() {
    StudentUpdateRequest existStudent = StudentUpdateRequest.builder()
            .id(1L)
            .fullName("")
            .groupName("")
            .build();
    mockMvc.perform(put("/student/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(existStudent))
            )
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное обновление данных студента")
  void givenValidStudentUpdateRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentUpdateRequest existStudent = StudentUpdateRequest.builder()
            .id(1L)
            .fullName("Heizenburger")
            .groupName("M8O-311B-22")
            .build();
    StudentResponse expectedResponse = StudentResponse.builder()
            .id(1L)
            .groupName("M8O-311B-22")
            .fullName("Heizenburger")
            .build();

    when(service.updateStudent(existStudent)).thenReturn(expectedResponse);
    mockMvc.perform(put("/student/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtils.toJson(existStudent))
            )
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на успешное удаление существующего студента")
  void givenValidStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = StudentResponse.builder()
            .id(1L)
            .groupName("M8O-311B-22")
            .fullName("Heizenburger")
            .build();
    when(service.deleteStudent(1L)).thenReturn(expectedResponse);
    mockMvc.perform(delete("/student/delete")
                    .param("id", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное удаление несуществующего студента")
  void givenInvalidStudentId_whenDeleteStudent_thenReturnUnprocessableEntity() {
    when(service.deleteStudent(any(Long.class))).thenThrow(new NotFoundException("Студент не найден"));
    mockMvc.perform(delete("/student/delete")
                    .param("id", "5"))
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  @DisplayName("Тест на неуспешное удаление запроса без параметров")
  void givenEmptyParamsRequest_whenDeleteStudent_thenReturnUnprocessableEntity() {
    mockMvc.perform(delete("/student/delete"))
            .andExpect(status().isUnprocessableEntity());
  }

}
