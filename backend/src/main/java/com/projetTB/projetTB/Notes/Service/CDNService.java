package com.projetTB.projetTB.Notes.Service;

import java.io.IOException;

public interface CDNService {
    void uploadFile(byte[] fileBytes, Long noteId, String fileName, String ownerEmail, String contentType)
            throws IOException;

    void grantAccessToFile(Long noteId, String fileName, String emailAddress) throws IOException;
}
