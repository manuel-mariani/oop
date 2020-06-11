package it.univpm.twitter_trends;

import it.univpm.twitter_trends.filter.CollectionFilter;
import it.univpm.twitter_trends.models.Metadata;
import it.univpm.twitter_trends.models.Trend;
import it.univpm.twitter_trends.models.TrendCollection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    public static final String API_URL =
            "https://wd4hfxnxxa.execute-api.us-east-2.amazonaws.com/dev/api/1.1/trends/available.json";
    private static DataStorer dataStorer = new DataStorer(24, "./data/");

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
        //dataStorer.start();   //TODO: remove comment
    }

    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        model.addAttribute("trends", getTrendsFromURL());
        model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
        return "home";
    }

    @GetMapping(value = "/twittertrends/home", params = {"expression"})
    public String homePage(Model model,
                           @RequestParam("expression") String expression){
        if (expression == null || expression.equals("")) return homePage(model);
        try {
            TrendCollection currentTrends = getTrendList();
            CollectionFilter<Trend> collectionFilter = new CollectionFilter<>(currentTrends.trends, expression);
            model.addAttribute("trends", collectionFilter.getFiltered());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error_msg", e.getMessage());
        } finally {
            model.addAttribute("query", expression);
            model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
        }
        return "home";
    }

    @GetMapping("/trends")
    public String trends(){
        StringBuilder output = new StringBuilder();
        for (Trend t : getTrendsFromURL()){
            output.append(t.toString());
        }
        return output.toString();
    }

    @GetMapping("/twittertrends/api/trends")
    @ResponseBody
    public static TrendCollection getTrendList() {
        return new TrendCollection(getTrendsFromURL());
    }

    @GetMapping("/twittertrends/api/metadata")
    @ResponseBody
    public static Object getMetadata() {
        return Metadata.get(Trend.class);
    }

    private static Trend[] getTrendsFromURL(){
        RestTemplate rt = new RestTemplateBuilder().build();
        ResponseEntity<Trend[]> response =
                rt.getForEntity(API_URL, Trend[].class);
        return response.getBody();
    }

    @GetMapping("/twittertrends/filter/trends")
    @ResponseBody
    public static Object getFilteredTrends(@RequestParam String expression){
        try {
            CollectionFilter<Trend> collectionFilter = new CollectionFilter<>(getTrendList().trends, expression);
            Collection<Trend> res = collectionFilter.getFiltered();
            TrendCollection filtered = new TrendCollection(res);
            return filtered;
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
