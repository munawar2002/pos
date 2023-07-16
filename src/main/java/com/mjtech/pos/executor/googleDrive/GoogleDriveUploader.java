package com.mjtech.pos.executor.googleDrive;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.mjtech.pos.constant.DatabaseBackupStatus;
import com.mjtech.pos.util.Util;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;


@Slf4j
public class GoogleDriveUploader {

    private static final String APPLICATION_NAME = "POS-Backup";
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client_secret2.json";


    /**
     * Upload new file.
     *
     * @return Inserted file metadata if successful, {@code null} otherwise.
     * @throws IOException if service account credentials file not found.
     */
    public Map<String, String> uploadBasic(java.io.File file) throws IOException {
        var response = new HashMap<String, String>();
        var uploadStatus = DatabaseBackupStatus.FAILED;
        var sharingStatus = DatabaseBackupStatus.FAILED;
        var apiResponse = new StringBuilder();

        String clientSecretFilePath = Util.getResourceFilePath(CREDENTIALS_FILE_PATH);
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", clientSecretFilePath);

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(clientSecretFilePath))
                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client drive.
        Drive drive = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Upload file photo.jpg on drive.
        File fileMetadata = new File();
        fileMetadata.setName(file.getName());
        FileContent mediaContent = new FileContent("application/octet-stream", file);
        try {
            File uploadedFile = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            String backupUploadResponse = "Backup File ID: " + uploadedFile.getId();
            log.info(backupUploadResponse);
            apiResponse.append(backupUploadResponse);
            response.put("fileId", uploadedFile.getId());
            uploadStatus = DatabaseBackupStatus.PASS;
            apiResponse.append("Upload Successful. FileId = ").append(uploadedFile.getId());

            // downloadFileFromDrive(drive, uploadedFile);

            Permission permission = new Permission()
                    .setType("user")
                    .setRole("writer") // Set the desired role: reader, writer, commenter, etc.
                    .setEmailAddress("posbackup2012@gmail.com");

            permission.set("sendNotificationEmail", false);

            // Add the permission to the file
            drive.permissions().create(uploadedFile.getId(), permission).execute();
            sharingStatus = DatabaseBackupStatus.PASS;
            apiResponse.append("Sharing Successful.").append(uploadedFile.getId());
            return response;
        } catch (GoogleJsonResponseException e) {
            log.error("Unable to upload file: " + e.getDetails(), e);
            apiResponse.append(e.getDetails()).append(Arrays.toString(e.getStackTrace()));
            return response;
        } finally {
            response.put("uploadStatus", uploadStatus.name());
            response.put("sharingStatus", sharingStatus.name());
            response.put("backupResponse", apiResponse.toString());
        }
    }

    private static void downloadFileFromDrive(Drive drive, File uploadedFile) throws IOException {
        OutputStream outputStream = Files.newOutputStream(Path.of("/tmp/downloaded.sql"), StandardOpenOption.CREATE);

        drive.files().get(uploadedFile.getId()).executeMediaAndDownloadTo(outputStream);
    }
}

