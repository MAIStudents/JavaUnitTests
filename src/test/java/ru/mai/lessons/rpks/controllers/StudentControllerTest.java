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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
    @DisplayName("Тест на поиск студента с несуществующим идентификатором")
    void givenInvalidStudentId_whenGetStudent_thenReturnUnprocessableEntity() {
        Long invalidId = 999L;

        when(service.getStudent(invalidId)).thenThrow(new RuntimeException("Студент не найден"));

        mockMvc
                .perform(get("/student/get").param("id", invalidId.toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json("{\"status\":\"UNPROCESSABLE_ENTITY\",\"error\":\"Студент не найден\"}"));
    }


    @Test
    @SneakyThrows
    @DisplayName("Тест на удаление студента по его идентификатору")
    void givenStudentId_whenDeleteStudent_thenReturnSuccessResponse() {
        StudentResponse expectedResponse = new StudentResponse();
        when(service.deleteStudent(1L)).thenReturn(expectedResponse);

        mockMvc
                .perform(delete("/student/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(JsonUtils.toJson(expectedResponse)));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест на удаление несуществующего студента")
    void givenInvalidStudentId_whenDeleteStudent_thenReturnNotFound() {
        Long invalidId = 999L;

        when(service.deleteStudent(invalidId)).thenThrow(new RuntimeException("Студент не найден"));

        mockMvc
                .perform(delete("/student/delete").param("id", invalidId.toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json("{\"status\":\"UNPROCESSABLE_ENTITY\",\"error\":\"Студент не найден\"}"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест на успешное создание студента")
    void givenValidStudentRequest_whenCreateStudent_thenReturnOk() {

        StudentCreateRequest createRequest = new StudentCreateRequest("Lisnyak", "M8О-313Б");
        StudentResponse expectedResponse = new StudentResponse(1L, "Lisnyak", "M8О-313Б");

        when(service.saveStudent(any(StudentCreateRequest.class))).thenReturn(expectedResponse);

        mockMvc
                .perform(post("/student/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(createRequest)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));
    }


    @Test
    @SneakyThrows
    @DisplayName("Тест на неуспешное создание студента")
    void givenInvalidStudentRequest_whenCreateStudent_thenReturnUnprocessableEntity() {


        StudentCreateRequest invalidRequest = new StudentCreateRequest("", "");

        mockMvc
                .perform(post("/student/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест на успешное обновление студента")
    void givenValidStudentUpdateRequest_whenUpdateStudent_thenReturnUpdatedStudentResponse() {

        StudentUpdateRequest updateRequest = new StudentUpdateRequest(1L, "Lisnyak", "M8О-313Б");
        StudentResponse updatedResponse = new StudentResponse(1L, "Lisnyak", "M8О-313Б");

        when(service.updateStudent(any(StudentUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/student/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtils.toJson(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(JsonUtils.toJson(updatedResponse)))
                .andExpect(header().exists("Content-Type"));
    }

    @Test
    @SneakyThrows
    @DisplayName("Тест на валидацию при обновлении студента")
    void givenInvalidStudentUpdateRequest_whenUpdateStudent_thenReturnValidationErrors() {
        StudentUpdateRequest invalidRequest = new StudentUpdateRequest(1L, "", "");

        mockMvc.perform(MockMvcRequestBuilders.put("/student/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(invalidRequest)))
                .andExpect(status().isUnprocessableEntity());
    }


}
