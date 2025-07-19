package com.studybuddy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class StudyBuddyApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyBuddyApplication.class, args);
    }
}
