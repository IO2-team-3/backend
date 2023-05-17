package com.team3.central.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsS3Service {

  private final AmazonS3 amazonS3;
  @Autowired
  public AwsS3Service(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  public List<String> getBucketNames(String eventId) {
    final String bucketName = "io2-central-photos";
    ListObjectsRequest request = new ListObjectsRequest();
    request.setBucketName(bucketName);
    request.setPrefix("event/"+eventId);
    ObjectListing listing = amazonS3.listObjects(request);
    return listing.getObjectSummaries()
        .stream()
        .map(summary -> "https://"+bucketName+".s3.amazonaws.com/"+summary.getKey())
        .collect(Collectors.toList());
  }
}