package com.projetTB.projetTB.Notes.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleCloudStorageCDNService implements CDNService {

    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageCDNService(
            @Value("${gcp.bucket.name}") String bucketName,
            @Value("${gcp.credentials.file}") Resource credentialsResource) throws IOException {

        this.bucketName = bucketName;

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsResource.getInputStream());

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Override
    public void uploadFile(byte[] fileBytes, String noteId, String fileName, String ownerEmail, String contentType) throws IOException {
        BlobId blobId = BlobId.of(bucketName, noteId + "/" + fileName);
        Acl ownerAcl = Acl.of(new Acl.User(ownerEmail), Acl.Role.OWNER);

        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setAcl(Collections.singletonList(ownerAcl))
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, fileBytes);
    }

    @Override
    public void grantAccessToFile(String noteId, String fileName, String emailAddress) throws IOException {
        BlobId blobId = BlobId.of(bucketName, noteId + "/" + fileName);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            throw new IOException("File not found: " + noteId + "/" + fileName);
        }

        Acl newAcl = Acl.of(new Acl.User(emailAddress), Acl.Role.READER);
        blob.createAcl(newAcl);
    }
}