package org.main.hackerthon.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageService {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucketName}")
  private String bucketName;

  public String upload(MultipartFile image) throws IOException {
    if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
      throw new IllegalArgumentException("Invalid image file: empty or null filename");
    }
    return this.uploadImage(image);
  }

  private String uploadImage(MultipartFile image) throws IOException {
    this.validateImageFileExtension(image.getOriginalFilename());
    return this.uploadImageToS3(image);
  }

  private void validateImageFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf(".");
    if (lastDotIndex == -1) {
      throw new IllegalArgumentException("Invalid file extension: no dot found in filename");
    }

    String extension = filename.substring(lastDotIndex + 1).toLowerCase();
    List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

    if (!allowedExtensionList.contains(extension)) {
      throw new IllegalArgumentException("Invalid file extension: " + extension);
    }
  }

  private String uploadImageToS3(MultipartFile image) throws IOException {
    String originalFilename = image.getOriginalFilename();
    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

    String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;

    InputStream is = image.getInputStream();
    byte[] bytes = IOUtils.toByteArray(is);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("image/" + extension);
    metadata.setContentLength(bytes.length);
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

    try {
      PutObjectRequest putObjectRequest =
          new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
              .withCannedAcl(CannedAccessControlList.PublicRead);
      amazonS3.putObject(putObjectRequest);
    } catch (AmazonS3Exception e) {
      throw new IOException("Error uploading image to S3: " + e.getMessage(), e);
    } finally {
      byteArrayInputStream.close();
      is.close();
    }

    return amazonS3.getUrl(bucketName, s3FileName).toString();
  }

  private String getKeyFromImageAddress(String imageAddress) throws IOException {
    try {
      URL url = new URL(imageAddress);
      String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
      return decodingKey.substring(1); // Remove leading '/'
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new IOException("Error decoding image address: " + e.getMessage(), e);
    }
  }
}