import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

public class FindLogsDirectory {
    public static void main(String[] args) {
        File DDrive = new File("D:");
        if (DDrive.exists()) {
            File logsDirectory = findLogsDirectory(DDrive);

            if (logsDirectory != null) {
                System.out.println("Znaleziono katalog 'logs' w lokalizacji: " + logsDirectory.getAbsolutePath());
                displayLogs(logsDirectory);
            } else {
                System.out.println("Katalog 'logs' nie został znaleziony na dysku D.");
            }
        } else {
            System.out.println("Dysk D nie istnieje lub nie jest dostępny.");
        }
    }
    public static File findLogsDirectory(File directory) {
        File[] fileList = directory.listFiles();

        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    if (file.getName().equalsIgnoreCase("logs")) {
                        return file;
                    } else {
                        File nestedLogsDirectory = findLogsDirectory(file);
                        if (nestedLogsDirectory != null) {
                            return nestedLogsDirectory;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void displayLogs(File directory){
        File[] logFiles = findLogFiles(directory);
        ReadLog readLog = new ReadLog();

        if (logFiles.length > 0) {
            Arrays.sort(logFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

            System.out.println("Posortowane pliki z logami:");
            for (File file : logFiles) {
                SimpleDateFormat fileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
                System.out.println(file.getName() + " - data modyfikacji: " + fileDate.format(file.lastModified()));
                String filePath = file.getPath();
                readLog.readLogFile(filePath);
                System.out.println();
            }
        } else {
            System.out.println("Brak plików z logami w katalogu 'logs'.");
        }
    }

    public static File[] findLogFiles(File directory) {
        FileFilter fileFilter = pathname -> !pathname.isDirectory() && pathname.getName().toLowerCase().endsWith(".log");
        return directory.listFiles(fileFilter);
    }
}