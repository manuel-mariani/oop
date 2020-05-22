package it.univpm.twitter_trends;

import it.univpm.twitter_trends.models.Trend;
import it.univpm.twitter_trends.models.TrendList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@Controller
public class TwitterTrendsApplication {
    public static final String API_URL = "https://wd4hfxnxxa.execute-api.us-east-2.amazonaws.com/" +
                                         "dev/api/1.1/trends/available.json";

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
    }

    @GetMapping("/twittertrends/home")
    public String homePage(Model model){
        return "home";
    }

    @GetMapping("/trends")
    public String trends(){
        RestTemplate rt = new RestTemplateBuilder().build();

        ResponseEntity<Trend[]> response =
                rt.getForEntity(
                        API_URL,
                        Trend[].class);

        Trend[] trends = response.getBody();

        StringBuilder output = new StringBuilder();
        for (Trend t : trends){
            output.append(t.toString());
        }
        return output.toString();
    }

}
