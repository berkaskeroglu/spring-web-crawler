package com.example.Crawler.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory; // GsonFactory'yi import edin
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class GoogleSheetsService {

    private static final String APPLICATION_NAME = "testSheet";
    private static final String SPREADSHEET_ID = "1NahYGeTlwQK3AgLx4zqrIE_SSU3sAW5EFtxPQ4apBaE";
    //private static final String SHEET_NAME = "Sheet1"; 

    private final Sheets sheetsService;
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    public GoogleSheetsService() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream("C:\\Users\\berka\\Desktop\\Crawler\\src\\main\\java\\com\\example\\Crawler\\service\\berkapp-d5d21-b3401481f35a.json"))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        this.sheetsService = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void addUrl(String url) throws IOException {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
    
        ValueRange response = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID,  "A:B")
            .execute();
        List<List<Object>> values = response.getValues();
        int nextRow = (values == null || values.isEmpty()) ? 1 : values.size() + 1;
    

        List<List<Object>> data = Collections.singletonList(
            List.of(url, timestamp)
        );
        ValueRange body = new ValueRange().setValues(data);
    
        AppendValuesResponse result = sheetsService.spreadsheets().values()
            .append(SPREADSHEET_ID, "!A" + nextRow, body)
            .setValueInputOption("RAW")
            .execute();
        
    }
}
