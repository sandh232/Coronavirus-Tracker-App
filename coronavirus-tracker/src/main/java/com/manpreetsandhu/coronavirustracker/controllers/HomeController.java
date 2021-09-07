/**
 * Author: Manpreet Sandhu
 * File: HomeController.java
 **/
package com.manpreetsandhu.coronavirustracker.controllers;

import com.manpreetsandhu.coronavirustracker.models.LocationStats;
import com.manpreetsandhu.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        int totalCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDate()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalCases);
        model.addAttribute("totalNewCases",totalNewCases);

        return "home";
    }
}
