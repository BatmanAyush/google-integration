package com.batman.googleSheetsAPI.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleApiUtil {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    // Allow both read and write access to the Google Sheets
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = GoogleApiUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Get an instance of the Sheets API client service.
     *
     * @return Sheets service instance.
     * @throws IOException, GeneralSecurityException If there is an issue creating the service.
     */
    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Reads data from the given range in the Google Sheet.
     *
     * @param spreadsheetId The ID of the Google Sheet.
     * @param range The range of cells to read from.
     * @return List of values from the Google Sheet.
     * @throws IOException, GeneralSecurityException If an error occurs during the operation.
     */
    public static List<List<Object>> readData(String spreadsheetId, String range)
            throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    /**
     * Appends data to the given range in the Google Sheet.
     *
     * @param spreadsheetId The ID of the Google Sheet.
     * @param range The range to which data will be appended.
     * @param values The data to append.
     * @throws IOException, GeneralSecurityException If an error occurs during the operation.
     */
    public static void appendData(String spreadsheetId, String range, List<List<Object>> values)
            throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        ValueRange body = new ValueRange().setValues(values);

        service.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")  // Use RAW to enter the data as-is
                .execute();
    }

    /**
     * Main method for testing reading and appending data.
     */
    public static void main(String... args) {
        try {
            final String spreadsheetId = "1MfzpDPOMSNx-2tuBostivQNNikezzxhnN1LdzrlbHWI";
            final String readRange = "Sheet1!A2:C";  // Adjust range if needed

            // Read and print data from the sheet
            List<List<Object>> values = readData(spreadsheetId, readRange);
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                System.out.println("Row Data:");
                for (List<Object> row : values) {
                    System.out.println(String.join(", ", row.stream()
                            .map(Object::toString)
                            .toArray(String[]::new)));
                }
            }

            // Example of appending data to the sheet
//            List<List<Object>> newRow = Collections.singletonList(
//                    List.of("John Doe", "johndoe@example.com", "1234567890")
//            );
//            appendData(spreadsheetId, "Sheet1!A2:C", newRow);
//            System.out.println("Data appended successfully!");

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
