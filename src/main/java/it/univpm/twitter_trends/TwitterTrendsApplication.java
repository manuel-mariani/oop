package it.univpm.twitter_trends;

import it.univpm.twitter_trends.models.Metadata;
import it.univpm.twitter_trends.models.Trend;
import it.univpm.twitter_trends.models.TrendCollection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
/**
 * Main class for handling web requests
 */
@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    private static final DataManagerService DMS = new DataManagerService(1, "./data/");

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
        DMS.start();
    }

    /**
     * Returns the home page of the application, populating the table with the unfiltered trends of the current day
     * @param model the model of the home page (auto handled by Thymeleaf)
     * @return      the home page
     */
    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        try {
            // Get unfiltered trends and selected date to model
            TrendCollection trendCollection = DMS.getTrendCollection();
            model.addAttribute("trends", trendCollection.trends);
            model.addAttribute("selectedDate", trendCollection.dateString);
        } catch (Exception e) {
            // In case of error add error message to model
            model.addAttribute("errorMsg", e.getMessage());
        } finally {
            // Add metadata and available dates to model
            model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
            model.addAttribute("availableDates", DMS.getAvailableDates());
        }
        return "home";
    }

    /**
     * Returns the home page of the application, with the table of trends filtered by an expression and a date
     * @param model       the model of the home page (auto handled by Thymeleaf)
     * @param date        the date of the desired trends. If it's empty, the returned trends will be of the current day
     * @param expression  the filter expression. If it's empty, the returned trends will be unfiltered
     * @return            the home page
     */
    @GetMapping(value = "/twittertrends/home", params = {"date", "filter"})
    public String homePage(Model model,
                           @RequestParam("date") String date,
                           @RequestParam("filter") String expression){
        try {
            // Get filtered collection and add it to model
            TrendCollection filtered = DMS.getFilteredTrendCollection(date, expression);
            model.addAttribute("trends", filtered.trends);
        } catch (Exception e) {
            // In case of error add the error message to model
            model.addAttribute("errorMsg", e.getMessage());
        } finally {
            // Add the query, the metadata and the available and selected dates to model
            model.addAttribute("query", expression);
            model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
            model.addAttribute("availableDates", DMS.getAvailableDates());
            model.addAttribute("selectedDate", date);
        }
        return "home";
    }

    /**
     * Returns the unfiltered trends of the current day, in the HTTP body as JSON format. In case of error an HashMap
     * with the error message in the field "errorMsg" is returned
     * @return response body
     */
    @GetMapping("/twittertrends/api/trends")
    @ResponseBody
    public static Object getTrendList() {
        try {
            // Get the current day collection of trends from the data manager service.
            return DMS.getTrendCollection();
        } catch (Exception e) {
            // In case of error add message to response
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("errorMsg", e.getMessage());
            return hashMap;
        }
    }

    /**
     * Returns the metadata of the Trend class
     * @return response body with metadata
     */
    @GetMapping("/twittertrends/api/metadata")
    @ResponseBody
    public static Object getMetadata() {
        return Metadata.get(Trend.class);
    }

    /**
     * Returns the filtered trends of the selected day, in the HTTP body as JSON format. In case of error an HashMap
     * with the error message in the field "errorMsg" is returned
     * @param date        the date of the desired trends. If it's empty, the returned trends will be of the current day
     * @param expression  the filter expression. If it's empty, the returned trends will be unfiltered
     * @return            response body
     */
    @GetMapping(value = "/twittertrends/api/trends", params = {"date", "filter"})
    @ResponseBody
    public static Object getFilteredTrends(@RequestParam("date") String date,
                                           @RequestParam("filter") String expression){
        try {
            // Get the filtered day collection of trends from the data manager service.
            return DMS.getFilteredTrendCollection(date, expression);
        } catch (Exception e) {
            // In case of error add message to response
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("errorMsg", e.getMessage());
            return hashMap;
        }
    }


}
