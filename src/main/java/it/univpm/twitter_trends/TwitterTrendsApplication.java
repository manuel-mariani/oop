package it.univpm.twitter_trends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TwitterTrendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterTrendsApplication.class, args);
    }

    @GetMapping("/helloworld")
    public String helloworld(@RequestParam(value = "name", defaultValue = "World") String name){
        return "Hello " + name;
    }

}
