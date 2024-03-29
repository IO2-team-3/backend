package com.team3.central.services;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.team3.central.repositories.EventRepository;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.PhotoExist;
import com.team3.central.services.exceptions.PhotoNotExist;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwsS3Service {

  private final AmazonS3 amazonS3;
  private final EventRepository eventRepository;
  final String bucketName = "io2-central-photos";

  @Autowired
  public AwsS3Service(AmazonS3 amazonS3, EventRepository eventRepository) {
    this.amazonS3 = amazonS3;
    this.eventRepository = eventRepository;
  }

  public List<String> getBucketNames(String eventId) throws NoSuchElementException {
    eventRepository.findById(Long.valueOf(eventId)).orElseThrow();

    ListObjectsRequest request = new ListObjectsRequest();
    request.setBucketName(bucketName);
    String key = "event/" + eventId;
    request.setPrefix(key);
    ObjectListing listing = amazonS3.listObjects(request);
    return listing.getObjectSummaries().stream()
        .map(summary -> "https://" + bucketName + ".s3.amazonaws.com/" + summary.getKey())
        .collect(Collectors.toList());
  }

  public String addPhoto(String eventId, String email, String path)
      throws NoSuchElementException, BadIdentificationException, PhotoExist {
    var event = eventRepository.findById(Long.valueOf(eventId)).orElseThrow();
    if (!Objects.equals(event.getOrganizer().getEmail(), email)) {
      throw new BadIdentificationException("You are not the organizer of this event");
    }
    String key = "event/" + eventId + "/" + path;
    // Check if the photo already exists
    if (amazonS3.doesObjectExist(bucketName, key)) {
      throw new PhotoExist("Photo already exists");
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, 10); //validity of 10 minutes
    return amazonS3.generatePresignedUrl(bucketName, key, calendar.getTime(), HttpMethod.PUT).toString();
  }


  public void deletePhoto(String eventId, String email, String path)
      throws NoSuchElementException, BadIdentificationException, PhotoNotExist {
    String key = "event/" + eventId + "/" + path;
    var event = eventRepository.findById(Long.valueOf(eventId)).orElseThrow();
    if (!Objects.equals(event.getOrganizer().getEmail(), email)) {
      throw new BadIdentificationException("You are not the organizer of this event");
    }
    // Check if the photo exists
    if (!amazonS3.doesObjectExist(bucketName, key)) {
      throw new PhotoNotExist("Photo does not exist");
    }
    amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
  }

}