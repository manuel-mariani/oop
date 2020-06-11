package it.univpm.twitter_trends;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.univpm.twitter_trends.models.TrendCollection;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataStorer {
    private ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

    private String path;
    private long period;

    public DataStorer(long period, String path){
        this.period = period;
        this.path = path;
        this.path += (!path.endsWith("/")) ? "/" : "";
    }

    private Runnable storeService = new Runnable() {
        public void run() {

            TrendCollection trends = TwitterTrendsApplication.getTrendList();
            try {
                File directory = Paths.get(path).toFile();
                directory.mkdir();

                File file = Paths.get(path+fileName()).toFile();
                file.createNewFile();

                writer.writeValue(file, trends);
                System.out.println("File successfully saved at " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
        }

        private String fileName(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return dtf.format(LocalDateTime.now()) + ".json";
        }
    };

    public void start(){
        scheduler.scheduleAtFixedRate(storeService, 0, period, TimeUnit.HOURS);
    }

    public void stop(){
        scheduler.shutdownNow();
    }
}
