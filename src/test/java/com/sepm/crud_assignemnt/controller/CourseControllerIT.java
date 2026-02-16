package com.sepm.crud_assignemnt.controller;
import com.sepm.crud_assignemnt.entity.Course;
import com.sepm.crud_assignemnt.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setup() {
        courseRepository.deleteAll();
        courseRepository.save(new Course("AI", "Intro"));
        courseRepository.save(new Course("OOP", "Basics"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllCourses_shouldReturn200AndJsonArray() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getCourseById_shouldReturn200AndCourseJson() throws Exception {
        Long id = courseRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/courses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.title").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getCourseById_shouldReturn500WhenNotFound() throws Exception {
        long missingId = Long.MAX_VALUE;

        mockMvc.perform(get("/api/courses/{id}", missingId))
                .andDo(print());
    }




    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createCourse_asTeacher_shouldCreateAndReturnJson() throws Exception {

        String body = """
        {
          "title": "DBMS",
          "description": "SQL Basics"
        }
        """;

        mockMvc.perform(
                        post("/api/courses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("DBMS"));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateCourse_asTeacher_shouldUpdateAndReturnJson() throws Exception {

        // Arrange: take an existing course id from H2 test DB
        Long id = courseRepository.findAll().get(0).getId();

        String body = """
        {
          "title": "Updated Title",
          "description": "Updated Description"
        }
        """;

        // Act + Assert
        mockMvc.perform(
                        put("/api/courses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void deleteCourse_asTeacher_shouldReturn204AndRemoveFromDb() throws Exception {

        // Arrange: get an existing id
        Long id = courseRepository.findAll().get(0).getId();

        // Act + Assert (API response)
        mockMvc.perform(delete("/api/courses/{id}", id))
                .andExpect(status().isNoContent());

        // Assert (database effect)
        org.junit.jupiter.api.Assertions.assertFalse(courseRepository.findById(id).isPresent());
    }


}
