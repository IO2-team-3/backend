package com.team3.central.controllers;

import com.amazonaws.HttpMethod;
import com.team3.central.services.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthCheck {

  @Autowired
  private final AwsS3Service awsS3Service;
  @GetMapping("/ping")
  String getPing() {
    return "pong";
  }

  @GetMapping("/url")
  String getUrlToS3() {
    // we allow for methods : GET, PUT and DELETE
    // fila path should be like "/some/place/filename.type" eg "/events/1/photo.png"
    return awsS3Service.generatePreSignedUrl("test.png","io2-central-photos", HttpMethod.DELETE);
  }

}
