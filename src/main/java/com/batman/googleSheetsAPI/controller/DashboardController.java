package com.batman.googleSheetsAPI.controller;

import com.batman.googleSheetsAPI.util.GoogleApiUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

package com.batman.googleSheetsAPI.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api") // Base path for all endpoints
public class DashboardController {
    private static final String SPREADSHEET_ID = "1MfzpDPOMSNx-2tuBostivQNNikezzxhnN1LdzrlbHWI";
    private static final String RANGE = "Sheet1!A2:C";

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Google Sheets API Service is running! Use /api/addRow endpoint to add data.");
    }

    @PostMapping("/addRow")
    public ResponseEntity<?> addRow(@RequestBody UserData userData) {
        try {
            // Validate input
            if (userData.getName() == null || userData.getEmail() == null || userData.getPhone() == null) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Name, email, and phone are required fields", false));
            }

            // Prepare data to be added to Google Sheet
            List<List<Object>> values = Arrays.asList(
                Arrays.asList(userData.getName(), userData.getEmail(), userData.getPhone())
            );

            // Call GoogleApiUtil to append the data
            GoogleApiUtil.appendData(SPREADSHEET_ID, RANGE, values);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("Data added successfully!", true));

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Failed to add data: " + e.getMessage(), false));
        }
    }
}

class ApiResponse {
    private String message;
    private boolean success;
    private long timestamp;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public long getTimestamp() { return timestamp; }
}
