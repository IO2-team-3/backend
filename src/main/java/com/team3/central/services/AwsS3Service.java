package com.team3.central.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.team3.central.repositories.EventRepository;
import com.team3.central.services.exceptions.BadIdentificationException;
import com.team3.central.services.exceptions.PhotoExist;
import com.team3.central.services.exceptions.PhotoNotExist;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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

  public List<String> getBucketNames(String eventId) throws NotFoundException {
    eventRepository.findById(Long.valueOf(eventId)).orElseThrow(NotFoundException::new);

    ListObjectsRequest request = new ListObjectsRequest();
    request.setBucketName(bucketName);
    String key = "event/" + eventId;
    request.setPrefix(key);
    ObjectListing listing = amazonS3.listObjects(request);
    return listing.getObjectSummaries().stream()
        .map(summary -> "https://" + bucketName + ".s3.amazonaws.com/" + summary.getKey())
        .collect(Collectors.toList());
  }

  public void addPhoto(String eventId, String email, String path)
      throws NotFoundException, BadIdentificationException, PhotoExist {
    var event = eventRepository.findById(Long.valueOf(eventId)).orElseThrow(NotFoundException::new);
    if (event.getOrganizer().getEmail() != email) {
      throw new BadIdentificationException("You are not the organizer of this event");
    }
    String key = "event/" + eventId + "/" + path;
    // Check if the photo already exists
    if (amazonS3.doesObjectExist(bucketName, key)) {
      throw new PhotoExist("Photo already exists");
    }
  }

  public void deletePhoto(String eventId, String email, String path)
      throws NotFoundException, BadIdentificationException, PhotoNotExist {
    String key = "event/" + eventId + "/" + path;
    var event = eventRepository.findById(Long.valueOf(eventId)).orElseThrow(NotFoundException::new);
    if (event.getOrganizer().getEmail() != email) {
      throw new BadIdentificationException("You are not the organizer of this event");
    }
    // Check if the photo exists
    if (!amazonS3.doesObjectExist(bucketName, key)) {
      throw new PhotoNotExist("Photo does not exist");
    }
  }

}