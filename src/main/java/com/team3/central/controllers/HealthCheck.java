package com.team3.central.controllers;

import com.amazonaws.HttpMethod;
import com.team3.central.services.AwsS3Service;
import java.util.List;
import javax.websocket.server.PathParam;
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

  @GetMapping("/url/{id}")
  List<String> getUrlToS3(@PathParam("id") String id) {
    // names event/{id}/photo1.png
    return awsS3Service.getBucketNames(id);
  }
}
