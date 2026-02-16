package com.sepm.crud_assignemnt.service;

import com.sepm.crud_assignemnt.entity.Course;
import com.sepm.crud_assignemnt.repository.CourseRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    // CHANGE THIS LINE if your service class name is different
    @InjectMocks
    private CourseService courseService;

    @Test
    void getAllCourses_shouldReturnList() {
        // Arrange
        Course c1 = new Course();
        Course c2 = new Course();
        when(courseRepository.findAll()).thenReturn(List.of(c1, c2));

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseById_shouldReturnCourse() {
        // Arrange
        Long id = 1L;
        Course course = new Course();  // (use your entity's constructor)
        when(courseRepository.findById(id))
                .thenReturn(java.util.Optional.of(course));

        // Act
        Course result = courseService.getCourseById(id);

        // Assert
        org.junit.jupiter.api.Assertions.assertSame(course, result);
        verify(courseRepository, times(1)).findById(id);
    }

    @Test
    void getCourseById_shouldThrowWhenNotFound() {
        Long id = 99L;

        when(courseRepository.findById(id))
                .thenReturn(java.util.Optional.empty());

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> courseService.getCourseById(id)
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                ex.getMessage().contains("Course not found")
        );

        verify(courseRepository, times(1)).findById(id);
    }

    @Test
    void createCourse_shouldSaveAndReturnCourse() {
        // Arrange: make an input course and the "saved" course that repo will return
        Course input = new Course("AI", "Intro");
        Course saved = new Course("AI", "Intro");

        // Mock behavior: when save(input) is called, return saved
        when(courseRepository.save(input)).thenReturn(saved);

        // Act: call the real service method
        Course result = courseService.createCourse(input);

        // Assert: check returned data + repo interaction
        assertEquals("AI", result.getTitle());
        assertEquals("Intro", result.getDescription());
        verify(courseRepository, times(1)).save(input);
    }

    @Test
    void updateCourse_shouldUpdateFieldsAndSave() {
        // Arrange
        Long id = 1L;

        // this is the course already in "DB"
        Course existing = new Course("Old Title", "Old Desc");

        // this is what user sends to update
        Course updated = new Course("New Title", "New Desc");

        // when service tries to load existing course, repo will return it
        when(courseRepository.findById(id))
                .thenReturn(java.util.Optional.of(existing));

        // when service saves, return the same object
        when(courseRepository.save(existing)).thenReturn(existing);

        // Act
        Course result = courseService.updateCourse(id, updated);

        // Assert (1) fields actually changed on the existing object
        assertEquals("New Title", existing.getTitle());
        assertEquals("New Desc", existing.getDescription());

        // Assert (2) returned object is the saved existing object
        org.junit.jupiter.api.Assertions.assertSame(existing, result);

        // Assert (3) repository calls happened
        verify(courseRepository, times(1)).findById(id);
        verify(courseRepository, times(1)).save(existing);
    }

    @Test
    void deleteCourse_shouldCallRepositoryDeleteById() {
        // Arrange
        Long id = 5L;

        // Act
        courseService.deleteCourse(id);

        // Assert
        verify(courseRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(courseRepository);
    }


}
