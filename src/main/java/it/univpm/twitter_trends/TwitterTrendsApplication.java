package it.univpm.twitter_trends;

import it.univpm.twitter_trends.models.Metadata;
import it.univpm.twitter_trends.models.Trend;
import it.univpm.twitter_trends.models.TrendList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    public static final String API_URL = "https://wd4hfxnxxa.execute-api.us-east-2.amazonaws.com/" +
                                         "dev/api/1.1/trends/available.json";
    private static DataStorer dataStorer = new DataStorer(24, "./data/");

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
        Filter filter = new Filter<>(getTrendsFromURL()[0]);
        filter.setExpression("parentid:$lte:0");
        System.out.println(filter.filter());
        //dataStorer.start();   //TODO: remove comment
    }

    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        model.addAttribute("trends", getTrendsFromURL());
        model.addAttribute("metadata", Metadata.getMetadataNoType(Trend.class));
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
    public static TrendList getTrendList() {
        return new TrendList(getTrendsFromURL());
    }

    @GetMapping("/twittertrends/api/metadata")
    @ResponseBody
    public static Object getMetadata() {
        return Metadata.get(Trend.class);
    }

    private static Trend[] getTrendsFromURL(){
        RestTemplate rt = new RestTemplateBuilder().build();
        ResponseEntity<Trend[]> response =
                rt.getForEntity(
                        API_URL,
                        Trend[].class);

        return response.getBody();
    }

}
