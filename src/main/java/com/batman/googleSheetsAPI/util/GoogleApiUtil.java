package com.batman.googleSheetsAPI.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleApiUtil {

    // Application name for the Sheets API
    private static final String APPLICATION_NAME = "Google Sheets API Java Integration";

    // Specify the JSON factory used by Google
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Sheets API client instance (Singleton pattern)
    private static Sheets sheetsService;

    /**
     * Initializes and returns the Sheets API client using Service Account credentials.
     *
     * @return Sheets API service instance.
     * @throws IOException, GeneralSecurityException If credentials or API initialization fails.
     */
    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            // Load the JSON key from the resources directory
            InputStream in = GoogleApiUtil.class.getClassLoader()
                    .getResourceAsStream("ivory-program-439802-u3-1e79862d03dc.json");


            if (in == null) {
                throw new IOException("Resource not found: ivory-program-439802-u3-1e79862d03dc.json");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

            sheetsService = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(APPLICATION_NAME).build();
        }
        return sheetsService;
    }

    /**
     * Appends data to the specified Google Sheet.
     *
     * @param spreadsheetId The ID of the spreadsheet.
     * @param range The range to which data will be appended.
     * @param values The data to append.
     * @throws IOException, GeneralSecurityException If an error occurs while accessing the Sheets API.
     */
    public static void appendData(String spreadsheetId, String range, List<List<Object>> values)
            throws IOException, GeneralSecurityException {
        Sheets service = getSheetsService();
        var body = new com.google.api.services.sheets.v4.model.ValueRange().setValues(values);

        service.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW") // Insert data as-is
                .execute();
    }
}
