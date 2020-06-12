package it.univpm.twitter_trends;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.univpm.twitter_trends.filter.CollectionFilter;
import it.univpm.twitter_trends.models.Trend;
import it.univpm.twitter_trends.models.TrendCollection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataManagerService {
    public static final String API_URL =
            "https://wd4hfxnxxa.execute-api.us-east-2.amazonaws.com/dev/api/1.1/trends/available.json";
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private ObjectMapper objectMapper = new ObjectMapper();
    private ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());

    HashMap<String, TrendCollection> cachedTrendCollections = new HashMap<>();
    private Set<String> availableDates = new TreeSet<>();

    private String path;
    private long period;

    public DataManagerService(long period, String path){
        this.period = period;
        this.path = path;
        this.path += (!path.endsWith("/")) ? "/" : "";
    }

    private Runnable storeService = new Runnable() {
        public void run() {

            RestTemplate rt = new RestTemplateBuilder().build();
            ResponseEntity<Trend[]> trendsResponse = rt.getForEntity(API_URL, Trend[].class);
            TrendCollection trendsCollection = new TrendCollection(trendsResponse.getBody());
            trendsCollection.setDate(DTF.format(LocalDateTime.now()));
            cachedTrendCollections.put(fileNameNow(), trendsCollection);

            try {
                File directory = Paths.get(path).toFile();
                directory.mkdir();

                File file = Paths.get(path + fileNameNow()).toFile();
                file.createNewFile();

                objectWriter.writeValue(file, trendsCollection);
                updateAvailableDates();
                System.out.println("File successfully saved at " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void start(){
        scheduler.scheduleAtFixedRate(storeService, 0, period, TimeUnit.HOURS);
    }

    public void stop(){
        scheduler.shutdownNow();
    }

    private static String fileNameNow(){
        return DTF.format(LocalDateTime.now()) + ".json";
    }

    public TrendCollection getTrendCollection(String date){
        if (cachedTrendCollections.get(date) != null)
            return cachedTrendCollections.get(date);
        else {
            try {
                File file = Paths.get(path + date + ".json").toFile();
                TrendCollection tc = objectMapper.readValue(file, TrendCollection.class);
                cachedTrendCollections.put(date, tc);  //TODO max cache size
                return tc;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null; //TODO exception
        }
    }

    public TrendCollection getTrendCollection(){
        String date = DTF.format(LocalDateTime.now());
        return getTrendCollection(date);
    }

    public TrendCollection getFilteredTrendCollection(String date, String expression) throws Exception{
        if (date == null || date.equals(""))
            date = DTF.format(LocalDateTime.now());
        if (expression == null || expression.equals(""))
            return getTrendCollection(date);
        Collection<Trend> trendList = getTrendCollection(date).trends;
        CollectionFilter<Trend> collectionFilter = new CollectionFilter<>(trendList, expression);
        Collection<Trend> res = collectionFilter.getFiltered();
        return new TrendCollection(res, date);
    }

    public TrendCollection getFilteredTrendCollection(String expression) throws Exception{
        return getFilteredTrendCollection(DTF.format(LocalDateTime.now()), expression);
    }

    private void updateAvailableDates(){
        File[] availableFiles = Paths.get(path).toFile().listFiles();
        if (availableFiles == null) return;
        for(File f : availableFiles) {
            availableDates.add(f.getName().replaceAll(".json", ""));
        }
    }

    public Set<String> getAvailableDates() { return availableDates; }
}
