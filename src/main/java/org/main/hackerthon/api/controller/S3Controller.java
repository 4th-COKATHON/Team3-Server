package org.main.hackerthon.api.controller;

import lombok.RequiredArgsConstructor;
import org.main.hackerthon.api.service.S3ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller("/v1/api")
@RequiredArgsConstructor
public class S3Controller {

  private final S3ImageService s3ImageService;

  @PostMapping("/s3/upload")
  public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    String profileImage = s3ImageService.upload(image);
    return ResponseEntity.ok(profileImage);
  }
}