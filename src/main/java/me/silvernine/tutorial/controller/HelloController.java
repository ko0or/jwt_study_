package me.silvernine.tutorial.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    /* .anyRequest().authenticated(); */ @GetMapping("/study")
    public ResponseEntity<String> study() { return ResponseEntity.ok().body("https://www.inflearn.com/course/lecture?courseSlug=%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-jwt&unitId=65761&tab=curriculum"); }


    /* .antMatchers("/api/hello").permitAll() */ @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("hello :)");
    }




}
