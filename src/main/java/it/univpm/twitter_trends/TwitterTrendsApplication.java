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

@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    private static final DataManagerService DMS = new DataManagerService(24, "./data/");

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
        DMS.start();
    }

    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        // Get unfiltered trends and add data and meta to model
        TrendCollection trendCollection = DMS.getTrendCollection();
        model.addAttribute("trends", trendCollection.trends);
        model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
        // Add dates and available and selected dates
        model.addAttribute("availableDates", DMS.getAvailableDates());
        model.addAttribute("selectedDate", trendCollection.dateString);
        return "home";
    }

    @GetMapping(value = "/twittertrends/home", params = {"date", "filter"})
    public String homePage(Model model,
                           @RequestParam("date") String date,
                           @RequestParam("filter") String expression){
        try {
            TrendCollection filtered = DMS.getFilteredTrendCollection(date, expression);
            model.addAttribute("trends", filtered.trends);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: remove
            model.addAttribute("errorMsg", e.getMessage());
        } finally {
            model.addAttribute("query", expression);
            model.addAttribute("availableDates", DMS.getAvailableDates());
            model.addAttribute("selectedDate", date);
            model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
        }
        return "home";
    }

    @GetMapping("/twittertrends/api/trends")
    @ResponseBody
    public static TrendCollection getTrendList() {
        return DMS.getTrendCollection();
    }

    @GetMapping("/twittertrends/api/metadata")
    @ResponseBody
    public static Object getMetadata() {
        return Metadata.get(Trend.class);
    }

    @GetMapping(value = "/twittertrends/api/trends", params = {"date", "filter"})
    @ResponseBody
    public static Object getFilteredTrends(@RequestParam("date") String date,
                                           @RequestParam("filter") String expression){
        try {
            return DMS.getFilteredTrendCollection(date, expression);
        } catch (Exception e) {
            e.printStackTrace(); //TODO: remove
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("errorMsg", e.getMessage());
            return hashMap;
        }
    }

}
