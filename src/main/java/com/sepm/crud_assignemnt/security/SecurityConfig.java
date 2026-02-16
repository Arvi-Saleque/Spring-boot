package com.sepm.crud_assignemnt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;



@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    // controlls who can access what
    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Everyone logged in (STUDENT or TEACHER) can READ courses
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/courses/**")
                        .hasAnyRole("STUDENT", "TEACHER")

                        // Only TEACHER can CREATE/UPDATE/DELETE
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/courses/**").hasRole("TEACHER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/courses/**").hasRole("TEACHER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/courses/**").hasRole("TEACHER")

                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.defaultSuccessUrl("/ui/courses", true))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails student = User.builder()
                .username("student")
                .password(passwordEncoder.encode("student123"))
                .roles("STUDENT")
                .build();

        UserDetails teacher = User.builder()
                .username("teacher")
                .password(passwordEncoder.encode("teacher123"))
                .roles("TEACHER")
                .build();

        return new InMemoryUserDetailsManager(student, teacher);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
