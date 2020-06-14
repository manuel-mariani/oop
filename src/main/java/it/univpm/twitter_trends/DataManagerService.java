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
import java.io.IOException;
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

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Json mappers
    private ObjectMapper objectMapper = new ObjectMapper();
    private ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());

    // Trends saved in memory and available dates for the user to choose
    HashMap<String, TrendCollection> cachedTrendCollections = new HashMap<>();
    private Set<String> availableDates = new TreeSet<>();

    private String path;
    private long period;

    public DataManagerService(long hoursPeriod, String path){
        this.period = hoursPeriod;
        this.path = path;
        this.path += (!path.endsWith("/")) ? "/" : "";
    }

    private Runnable storeService = new Runnable() {
        public void run() {
            // Get json object from the URL and build object
            RestTemplate rt = new RestTemplateBuilder().build();
            ResponseEntity<Trend[]> trendsResponse = rt.getForEntity(API_URL, Trend[].class);
            TrendCollection trendsCollection = new TrendCollection(trendsResponse.getBody());
            // Set current date in the object and save to cache
            trendsCollection.setDate(DTF.format(LocalDateTime.now()));
            cachedTrendCollections.put(fileNameNow(), trendsCollection);

            try {
                // Get directory and file. If not present, create them
                File directory = Paths.get(path).toFile();
                directory.mkdir();
                File file = Paths.get(path + fileNameNow()).toFile();
                file.createNewFile();

                // Write object to files and update the available dates
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

    public TrendCollection getTrendCollection(String date) throws Exception {
        // If the trends of the selected date is present in cache, retrieve it directly
        if (cachedTrendCollections.get(date) != null)
            return cachedTrendCollections.get(date);
        // Else read it from file (if present), save it to cache and return it
        else {
            try {
                File file = Paths.get(path + date + ".json").toFile();
                TrendCollection tc = objectMapper.readValue(file, TrendCollection.class);
                cachedTrendCollections.put(date, tc);  //TODO max cache size
                return tc;
            } catch (IOException e) {
                throw new Exception("Trends of "+ date+ " not found");
            }
        }
    }

    public TrendCollection getTrendCollection() throws Exception{
        String date = DTF.format(LocalDateTime.now());
        return getTrendCollection(date);
    }

    public TrendCollection getFilteredTrendCollection(String date, String expression) throws Exception{
        // If date and expression or expression are null, rispectively set date to current or return unfiltered
        if (date == null || date.equals(""))
            date = DTF.format(LocalDateTime.now());
        if (expression == null || expression.equals(""))
            return getTrendCollection(date);

        // Create the filtered colletion using the Filter
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
