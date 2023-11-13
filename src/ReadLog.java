import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadLog {
    private final List<String> lines = new ArrayList<>();
    public void readLogFile(String filePath) {
        long startTime = System.currentTimeMillis();
        long endTime;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.matches("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2},\\d{3}.*")) {
                    lines.add(line);
                }
            }

            reader.close();
            endTime = System.currentTimeMillis();

            analyzeLogTimeDifference();
            severityGroups();
            uniqueLibraries();

        } catch (Exception e) {
            endTime = System.currentTimeMillis();
            e.printStackTrace();
        }

        long readingTime = endTime - startTime;
        System.out.println("Czas odczytu pliku: " + readingTime + " milisekund.");
    }

    public void analyzeLogTimeDifference() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

        String firstLine = lines.get(0);
        String lastLine = lines.get(lines.size()-1);

        if (firstLine != null && lastLine != null) {
            String[] firstParts = firstLine.split(" ");
            String[] lastParts = lastLine.split(" ");
            LocalDateTime firstDateTime = LocalDateTime.parse(firstParts[0] + " " + firstParts[1], formatter);
            LocalDateTime lastDateTime = LocalDateTime.parse(lastParts[0] + " " + lastParts[1], formatter);

            long differenceInSeconds = java.time.Duration.between(lastDateTime, firstDateTime).getSeconds();
            System.out.println("Różnica czasu między pierwszym a ostatnim wpisem (w sekundach): " + differenceInSeconds);
        } else {
            System.out.println("Brak wpisów z datą w pliku logów.");
        }
    }

    public void severityGroups(){
        String severity;
        Map<String, Integer> severityGroups = new HashMap<>();
        int all = 0;
        int dangerous = 0;

        for(String line : lines) {
            severity = line.split(" ")[2];
            severityGroups.merge(severity, 1, Integer::sum);
            all += 1;
            if(severity.equals("ERROR") || severity.equals("FATAL") || severity.equals("ALERT")){
                dangerous += 1;
            }
        }
        for(Map.Entry<String, Integer> entry : severityGroups.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        System.out.println("Stosunek niebezpiecznych logów do wszystkich wynosi " + (double)dangerous/all);
    }

    public void uniqueLibraries(){
        Set<String> libraries = new HashSet<>();
        String library;
        int libraryIndex;

        for(String line : lines) {
            String[] logLine = line.split(" ");
            libraryIndex = logLine[2].length() == 4 ? 4 : 3;
            library = logLine[libraryIndex];
            libraries.add(library);
        }

        System.out.println("Ilość unikalnych bibliotek wynosi " + libraries.size());
    }
}