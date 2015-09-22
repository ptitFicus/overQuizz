import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateExtractor {
    private static final String filepath = "resources/patrimoine_table.csv";
    private static final String order = "([0-9]+)e(r(e)?)?";
    private static final String decomposition = "(?<decomposition>quart|moitie)";
    private static final String introWord = "(?<introWord>limite|milieu)";
    private static final Pattern p1   = Pattern.compile(
            "" +
                    "(" + introWord + " )?" +
                    "((?<decompositionNumber>" + order + ") " +
                    decomposition + " )?" +
                    "(?<centuryNumber>" + order + ") " +
                    "siecle" +
            "(.*?)$"
    );

    public static void main(String[] args) {
        List<QuizzData> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = br.readLine()) != null) {
                String[] lineParts = line.split("\",\"");
                Optional<TimeInterval> t = extractTimeInterval(lineParts[10]);

                if(!t.isPresent()) {
                    System.out.println("FAILED ON : " + lineParts[10]);
                } else {
                    String type = lineParts[1];
                    String designation = lineParts[2];
                    String name = lineParts[3];
                    String commune = lineParts[4];
                    String description = lineParts[14];
                    String historic = lineParts[11];
                    String location = lineParts[17].trim();
                    location = location.replace("[", "");
                    location = location.replace("]", "");
                    String [] locationParts = location.split(",");
                    double latitude = Double.parseDouble(locationParts[0]);
                    double longitude = Double.parseDouble(locationParts[1]);

                    QuizzData newData = new QuizzData(t.get(), commune, description, designation, historic, latitude, longitude, name, type);

                    //System.out.println(newData.toString());
                    data.add(newData);
                }
            }

            // TODO insert data into a database

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Optional<TimeInterval> extractTimeInterval(final String text) {
        Optional<TimeInterval> result = Optional.empty();
        String text2Process = StringUtils.stripAccents(text).toLowerCase();

        // We are only interested by the oldest datation
        String[] text2ProcessParts = text2Process.split(";");
        int index = 0;

        while (index < text2ProcessParts.length &&
                text2ProcessParts[index].length() > 0 &&
                text2ProcessParts[index].charAt(0) == '?'
        ) {
            index++;

        }

        if(index < text2ProcessParts.length) {
            text2Process = text2ProcessParts[index].trim();
        }
        else
            return result;




        Matcher matcher = p1.matcher(text2Process);

        if(matcher.matches()) {
            // Retrieving century
            int century = Integer.parseInt(matcher.group("centuryNumber").replaceAll("ere|er|e", ""));

            Optional<Integer> startYear = Optional.empty();
            Optional<Integer> endYear   = Optional.empty();

            if(matcher.group("introWord") != null) {
                if(matcher.group("introWord").equals("limite")) {
                    startYear = Optional.of(75);
                    endYear   = Optional.of(125);
                } else if(matcher.group("introWord").equals("milieu")) {
                    startYear = Optional.of(25);
                    endYear   = Optional.of(75);
                }
            }
            else if(matcher.group("decompositionNumber") != null) {
                int number = Integer.parseInt(matcher.group("decompositionNumber").replaceAll("ere|er|e", ""));

                if(matcher.group("decomposition").equals("quart")) {
                    if(number == 1)
                        startYear = Optional.of(0);
                    else if (number == 2)
                        startYear = Optional.of(25);
                    else if (number == 3)
                        startYear = Optional.of(50);
                    else if(number == 4)
                        startYear = Optional.of(75);
                    endYear = Optional.of(startYear.get() + 25);
                } else if(matcher.group("decomposition").equals("moitie")) {
                    if(number == 1)
                        startYear = Optional.of(0);
                    else if(number == 2)
                        startYear = Optional.of(50);
                    endYear = Optional.of(startYear.get() + 50);
                }
            }
            int periodBegin = (century-1) * 100;
            int periodEnd   = century * 100;
            if(startYear.isPresent()) {
                periodEnd = periodBegin + endYear.get();
                periodBegin += startYear.get();
            }

            result = Optional.of(new TimeInterval(periodBegin, periodEnd));
        } else if(text2Process.contains("temps modernes")) {
            // Source : wikipedia
            int periodBegin = 1453;
            int periodEnd   = 1792;

            result = Optional.of(new TimeInterval(periodBegin, periodEnd));
        } else if(text2Process.contains("moyen age")) {
            int periodBegin = 476;
            int periodEnd = 1453;
            if(text2Process.contains("fin")) {
                periodBegin = 1300;
            } else if(text2Process.contains("milieu")) {
                periodBegin = 100;
                periodEnd   = 1300;
            } else if(text2Process.contains("haut")) {
                periodEnd = 1000;
            }

            result = Optional.of(new TimeInterval(periodBegin, periodEnd));
        } else if(text2Process.contains("gallo-romain")) {
            int periodStart = -52;
            int periodEnd   = 476;

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        } else if(text2Process.contains("antiquite")) {
            int periodStart = -3300;
            int periodEnd  = 476;

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        } else if(text2Process.contains("neolithique")) {
            int periodStart = -9000;
            int periodEnd  = -3300;

            if(text2Process.contains("moyen")) {
                periodStart = -4000;
                periodEnd   = -2900;
            }

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        } else if(text2Process.contains("paleolithique")) {
            int periodStart = -2900000;
            int periodEnd   = -12000;

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        } else if(text2Process.contains("protohistoire")) {
            int periodStart = -9000;
            int periodEnd   = -700;

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        } else if(text2Process.contains("age du fer")) {
            int periodStart = -2650;
            int periodEnd   = -700;

            result = Optional.of(new TimeInterval(periodStart, periodEnd));
        }

        return result;
    }
}
