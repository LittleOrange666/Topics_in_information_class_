package net.orange.game.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import net.orange.game.Main;
import net.orange.game.data.exception.DataIOException;
import net.orange.game.data.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NetworkDataManager {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE_APPDATA);
    private static final String TOKENS_DIRECTORY_PATH = "resources/data/tokens";
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    public static boolean check(){
        return new java.io.File(TOKENS_DIRECTORY_PATH+"/StoredCredential").isFile();
    }
    public static @Nullable NetworkDataManager create(){
        NetworkDataManager r;
        try {
            r = new NetworkDataManager();
        } catch (IOException | GeneralSecurityException e) {
            return null;
        }
        return r;
    }

    public static void logout() {
        new java.io.File(TOKENS_DIRECTORY_PATH+"/StoredCredential").delete();
    }
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = NetworkDataManager.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void test() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }
    private final Drive service;
    public NetworkDataManager() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    private void upload(String name, String filepath) throws IOException {
        try {
            // File's metadata.
            File fileMetadata = new File();
            fileMetadata.setName(name);
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File(filepath);
            FileContent mediaContent = new FileContent("application/octet-stream", filePath);
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (GoogleJsonResponseException e) {
            throw new DataIOException("Unable to create file: " + e.getDetails(),e);
        }
    }
    public void upload(String name, JsonObject object){
        Main.log("Uploading " + name);
        try {
            Path temp = Files.createTempFile(null,null);
            temp.toFile().deleteOnExit();
            String filePath = temp.toAbsolutePath().toString();
            DataTool.write_zipfile(filePath,object);
            upload(name,filePath);
        } catch (IOException e) {
            throw new DataIOException("Unable to upload file \""+name+"\"",e);
        }
        Main.log("completed upload");
    }
    private void download(String fileId, String filePath) throws IOException {
        try {
            OutputStream outputStream = new FileOutputStream(filePath);

            service.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
            outputStream.close();

        } catch (GoogleJsonResponseException e) {
            throw new DataIOException("Unable to move file: " + e.getDetails(),e);
        }
    }
    public JsonObject download(String name) {
        Main.log("Downloading " + name);
        try {
            Path temp = Files.createTempFile(null,null);
            temp.toFile().deleteOnExit();
            String filePath = temp.toAbsolutePath().toString();
            download(findID(name),filePath);
            JsonObject r = DataTool.read_zipfile(filePath);
            Main.log("completed download");
            return r;
        } catch (IOException e) {
            throw new DataIOException("Unable to download file \""+name+"\"",e);
        }
    }
    public void list() throws IOException {
        try {
            FileList files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(10)
                    .execute();
            for (File file : files.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
            }
        } catch (GoogleJsonResponseException e) {
            throw new DataIOException("Unable to list files: " + e.getDetails(),e);
        }
    }
    public boolean hasFile(String name){
        try {
            FileList files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(10)
                    .execute();
            for (File file : files.getFiles()) {
                if (Objects.equals(file.getName(), name)){
                    return true;
                }
            }
            return false;
        } catch (GoogleJsonResponseException e) {
            throw new DataIOException("Unable to list files: " + e.getDetails(),e);
        }catch (IOException e) {
            throw new DataIOException("Unable to check if file exist",e);
        }
    }
    public String findID(String name) throws IOException {
        try {
            FileList files = service.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(10)
                    .execute();
            for (File file : files.getFiles()) {
                if (Objects.equals(file.getName(), name)){
                    return file.getId();
                }
            }
            throw new FileNotFoundException("File " +name + " not found");
        } catch (GoogleJsonResponseException e) {
            throw new DataIOException("Unable to list files: " + e.getDetails(),e);
        }
    }
}
