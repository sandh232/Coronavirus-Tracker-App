/**
 * Author: Manpreet Sandhu
 * File: CoronaVirusDataService.java
 **/
package com.manpreetsandhu.coronavirustracker.services;

import com.manpreetsandhu.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class CoronaVirusDataService {

    private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //method to fetch Data from url
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats = new ArrayList<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());

        StringReader csvBodyReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            if(record.get("Province/State").isEmpty()){
                locationStat.setState("(State name not available)");
            }else{
                locationStat.setState(record.get("Province/State"));
            }
            locationStat.setCountry(record.get("Country/Region"));

            int latestCases = Integer.parseInt(record.get(record.size()-1));
            int previousDayCases = Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPreviousDate(latestCases-previousDayCases);

            //System.out.println(locationStat);
            newStats.add(locationStat);
        }

        this.allStats = newStats;
    }
}
