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
        model.addAttribute("trends", DMS.getTrendCollection().trends);
        model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
        return "home";
    }

    @GetMapping(value = "/twittertrends/home", params = {"expression"})
    public String homePage(Model model,
                           @RequestParam("expression") String expression){
        if (expression == null || expression.equals("")) return homePage(model);
        try {
            TrendCollection filtered = DMS.getFilteredTrendCollection(expression);
            model.addAttribute("trends", filtered.trends);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error_msg", e.getMessage());
        } finally {
            model.addAttribute("query", expression);
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

    @GetMapping("/twittertrends/filter/trends")
    @ResponseBody
    public static Object getFilteredTrends(@RequestParam String expression){
        try {
            return DMS.getFilteredTrendCollection(expression);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
