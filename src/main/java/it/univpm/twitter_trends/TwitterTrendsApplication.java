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

import javax.xml.crypto.Data;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    public static final String API_URL = "https://wd4hfxnxxa.execute-api.us-east-2.amazonaws.com/" +
                                         "dev/api/1.1/trends/available.json";
    private static DataStorer dataStorer = new DataStorer(24, "./data/");

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
        dataStorer.start();
    }

    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        model.addAttribute("trends", getTrends());
        return "home";
    }

    @GetMapping("/trends")
    public String trends(){
        StringBuilder output = new StringBuilder();
        for (Trend t : getTrends()){
            output.append(t.toString());
        }
        return output.toString();
    }

    @GetMapping("/twittertrends/api/trends")
    @ResponseBody
    public static TrendList getTrendList() {
        return new TrendList(getTrends());
    }

    @GetMapping("/twittertrends/api/metadata")
    @ResponseBody
    public Object getMetadata() {
        return Metadata.get(Trend.class);
    }

    private static Trend[] getTrends(){
        RestTemplate rt = new RestTemplateBuilder().build();
        ResponseEntity<Trend[]> response =
                rt.getForEntity(
                        API_URL,
                        Trend[].class);

        return response.getBody();
    }

}
