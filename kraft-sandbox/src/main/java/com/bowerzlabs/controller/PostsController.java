package com.bowerzlabs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/posts")
public class PostsController {

    @GetMapping
    public ResponseEntity<?> allPosts() {
        return ResponseEntity.ok(List.of(1, 2, 4, 5, 78, 908));
    }

}
