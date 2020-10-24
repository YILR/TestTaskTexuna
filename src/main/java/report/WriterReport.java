package report;

import java.io.*;
import java.util.Map;

public class WriterReport {
    private int count = 1;
    private boolean isParse = false;
    private Map<String, Map<String, String>> mapSettingXML;

    public WriterReport(Map<String, Map<String, String>> mapSettingXML) {
        this.mapSettingXML = mapSettingXML;
    }

    public void writeReport(String fileReader, String fileWriter) throws Exception {

        String header = "| " + parse("col0", mapSettingXML.get("col0").get("title")) + " | " + parse("col1", mapSettingXML.get("col1").get("title")) + " | " + parse("col2", mapSettingXML.get("col2").get("title")) + " |";
        StringBuilder dataRow = new StringBuilder();
        int height = Integer.parseInt(mapSettingXML.get("page").get("height"));
        String line = line();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileReader), "UTF-16"));
             BufferedWriter writer = new BufferedWriter(new FileWriter(fileWriter))) {
            writeRow(header, writer);

            while (reader.ready()) {
                String s = reader.readLine();
                String[] strings = s.split("\t");

                dataRow.append("| ");
                for (int i = 0; i < strings.length; i++) {
                    dataRow.append(parse("col" + i, strings[i],  strings)).append(" | ");
                    if(isParse)
                        break;
                }
                isParse = false;
                dataRow = new StringBuilder(dataRow.substring(0, dataRow.length() - 1));
                checkRow(header, height, writer);
                writeRow(line, writer);
                checkRow(header, height, writer);
                writeRow(dataRow.toString(), writer);

                dataRow = new StringBuilder();
                writer.flush();
            }
        }
    }

    private void checkRow(String header, int height, BufferedWriter writer) throws IOException {
        if (count >= height) {
            writer.write("~\n");
            count = 1;
            writeRow(header, writer);
        }
    }

    private void writeRow(String row,  BufferedWriter writer) throws IOException {
        writer.write(row + "\n");
        count++;
    }


    private String parse(String key, String value,  String...strings) {
        int width = Integer.parseInt(mapSettingXML.get(key).get("width"));
        String space = "";
        int size = width - value.length();
        if (size < 0) {
            String[] strSplit = value.split(" ");
            if(strSplit.length != 2) {
                int mid = key.equals("col2") ? width : width-1;
                strSplit = new String[]{
                        value.substring(0, mid),
                        value.substring(mid),
                };
            }

            if (key.equals("col2")) {
                String s1 = " ";
                if (strings.length == 1){
                    s1 = strings[0];
                }

                count++;
                return parse(key, strSplit[0]) + " |\n| " + parse("col0", " ") + " | " + parse("col1", s1)  + " | " + parse(key, strSplit[1].trim());
            }else {
                count++;
                isParse =true;
                return parse(key, strSplit[0]) + " | "+ parse("col2", strings[2], strSplit[1]);
            }
        }

        for (int i = 0; i < size; i++)
            space += " ";

        value += space;

        return value;
    }

    private String line() {
        StringBuilder line = new StringBuilder();
        int width = Integer.parseInt(mapSettingXML.get("page").get("width"));
        for (int i = 0; i < width; i++) {
            line.append("-");
        }
        return line.toString();
    }
}
