package com.batman.googleSheetsAPI.controller;

import com.batman.googleSheetsAPI.util.GoogleApiUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")  // Allow CORS from React App
public class DashboardController {

    private static final String SPREADSHEET_ID = "1MfzpDPOMSNx-2tuBostivQNNikezzxhnN1LdzrlbHWI";
    private static final String RANGE = "Sheet1!A2:C";  // Adjust based on where you want to add data

    @GetMapping("/check")
    public String check()
    {
        return "Checked";
    }

    @PostMapping("/addRow")
    public String addRow(@RequestBody UserData userData) {
        try {
            // Prepare data to be added to Google Sheet
            List<List<Object>> values = Arrays.asList(
                    Arrays.asList(userData.getName(), userData.getEmail(), userData.getPhone())
            );

            System.out.println(userData);

            // Call GoogleApiUtil to append the data
            GoogleApiUtil.appendData(SPREADSHEET_ID, RANGE, values);

            return "Data added successfully!";
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return "Failed to add data: " + e.getMessage();
        }
    }
}
