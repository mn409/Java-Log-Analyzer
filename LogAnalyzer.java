import java.io.*;
import java.util.*;
import java.util.regex.*;

class LogProcessor implements Runnable {
    private String filePath;
    private String logLevel;
    private String searchTimeStamp;
    private Pattern timestampPattern;
    private Pattern ipPattern;

    public LogProcessor(String filePath, String logLevel, String searchTimeStamp, Pattern timestampPattern, Pattern ipPattern) {
        this.filePath = filePath;
        this.logLevel = logLevel;
        this.searchTimeStamp = searchTimeStamp;
        this.timestampPattern = timestampPattern;
        this.ipPattern = ipPattern;
    }

    @Override
    public void run() {
        List<String> logEntries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().contains(logLevel)) {
                    logEntries.add(line);

                    if (searchTimeStamp.equals("yes")) {
                        Matcher timestampMatcher = timestampPattern.matcher(line);
                        if (timestampMatcher.find()) {
                            System.out.println("Timestamp found: " + timestampMatcher.group());
                        }
                    }

                    Matcher ipMatcher = ipPattern.matcher(line);
                    if (ipMatcher.find()) {
                        System.out.println("IP found: " + ipMatcher.group());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Sort log entries by timestamp
        logEntries.sort(Comparator.comparing(this::extractTimestamp));

        for (String log : logEntries) {
            System.out.println(log);
        }
    }

    private String extractTimestamp(String log) {
        Matcher matcher = timestampPattern.matcher(log);
        return matcher.find() ? matcher.group() : "";
    }
}

public class LogAnalyzer {
    public static void main(String[] args) {
        String[] filePaths = {"/home/asus/Desktop/sample.log", "/home/asus/Desktop/sample1.log"};
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the log level to filter (INFO, ERROR, WARN): ");
        String logLevel = scanner.nextLine().toUpperCase();

        System.out.println("Do you want to search for a timestamp? (yes/no): ");
        String searchTimeStamp = scanner.nextLine().toLowerCase();

        Pattern timestampPattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        Pattern ipPattern = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");

        for (String filePath : filePaths) {
            Thread thread = new Thread(new LogProcessor(filePath, logLevel, searchTimeStamp, timestampPattern, ipPattern));
            thread.start();
        }

        scanner.close();
    }
}
