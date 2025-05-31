package ru.mai.lessons.rpks.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import ru.mai.lessons.rpks.controllers.impl.StudentControllerImpl;
import ru.mai.lessons.rpks.dto.respones.StudentResponse;
import ru.mai.lessons.rpks.dto.requests.StudentCreateRequest;
import ru.mai.lessons.rpks.dto.requests.StudentUpdateRequest;
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
  @DisplayName("Test for successful deletion of a student by their identifier.")
  void givenStudentId_whenDeleteStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");
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
  @DisplayName("Test for successful addition of a student.")
  void givenValidRequest_whenSaveStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");
    StudentCreateRequest request = StudentCreateRequest.builder().fullName("fullName").groupName("groupName").build();
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
  @DisplayName("Test for successful updating of a student.")
  void givenValidRequest_whenUpdateStudent_thenReturnStudentResponse() {
    StudentResponse expectedResponse = new StudentResponse(1L, "fullName", "groupName");
    StudentUpdateRequest request = StudentUpdateRequest.builder().id(1L).fullName("fullName").groupName("groupName").build();
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
  @DisplayName("Test for unsuccessful student search by identifier.")
  void givenInvalidStudentId_whenGetStudent_thenThrow() {
    when(service.getStudent(1L)).thenThrow(new RuntimeException("Student with such an identifier not found."));

    mockMvc
        .perform(
            get("/student/get")
                .param("id", "1")
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().string(containsString("Student with such an identifier not found.")));
  }


  @Test
  @SneakyThrows
  @DisplayName("Test for successful deletion of a student by their identifier.")
  void givenInvalidStudentId_whenDeleteStudent_thenThrow() {
    when(service.deleteStudent(1L)).thenThrow(new RuntimeException("Student with such an identifier not found."));

    mockMvc
        .perform(
            delete("/student/delete")
                .param("id", "1")
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().string(containsString("Student with such an identifier not found.")));
  }


  @Test
  @SneakyThrows
  @DisplayName("Test for unsuccessful student addition.")
  void givenInvalidStudentRequest_whenSaveStudent_thenThrow() {
    StudentCreateRequest request = StudentCreateRequest.builder().fullName("32AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").groupName("groupName").build();
    when(service.saveStudent(request)).thenThrow(new RuntimeException("The student's name is too long."));

    mockMvc
        .perform(
            post("/student/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().string(containsString("The student's name is too long.")));
  }


  @Test
  @SneakyThrows
  @DisplayName("Test for successful student update.")
  void givenInvalidStudentRequest_whenUpdateStudent_thenThrow() {
    StudentUpdateRequest request = StudentUpdateRequest.builder().id(2L).fullName("fullName").groupName("groupName").build();
    when(service.updateStudent(request)).thenThrow(new RuntimeException("Student with such an identifier not found."));

    mockMvc
        .perform(
            put("/student/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJson(request))
        )
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().string(containsString("Student with such an identifier not found.")));
  }
}