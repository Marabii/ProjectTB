package com.projetTB.projetTB.Notes.Service;

import java.io.IOException;

public interface CDNService {
    void uploadFile(byte[] fileBytes, String noteId, String fileName, String ownerEmail, String contentType) throws IOException;
    void grantAccessToFile(String noteId, String fileName, String emailAddress) throws IOException;
}
