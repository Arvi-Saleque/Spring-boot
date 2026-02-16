package com.sepm.crud_assignemnt.controller;

import com.sepm.crud_assignemnt.entity.Course;
import com.sepm.crud_assignemnt.service.CourseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


@Controller
@RequestMapping("/ui/courses")
public class CourseUiController {

    private final CourseService courseService;

    public CourseUiController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Show list page
    @GetMapping
    public String list(Model model, Authentication auth) {
        model.addAttribute("courses", courseService.getAllCourses());

        boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        model.addAttribute("isTeacher", isTeacher);

        return "courses";
    }

    // Show create form (TEACHER only)
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "course-form";
    }

    // Handle create (TEACHER only)
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public String create(@ModelAttribute Course course) {
        courseService.createCourse(course);
        return "redirect:/ui/courses";
    }

    // Show edit form (TEACHER only)
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "course-form";
    }

    // Handle update (TEACHER only)

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Course course) {
        courseService.updateCourse(id, course);
        return "redirect:/ui/courses";
    }

    // Handle delete (TEACHER only)
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/ui/courses";
    }
}
