package report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Generator {
    private static Map<String, Map<String, String>> mapSettingXML = new HashMap<>();
    private static int count = 0;
    private static boolean isParse = false;

    public static void main(String[] args) throws Exception {

        parseXML(args[0]);
        writeReport(args[1], args[2]);

    }

    private static void parseXML(String setting) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(setting);
        NodeList list = document.getElementsByTagName("page");
        Element element = (Element) list.item(0);

        saveSettingXML(element.getElementsByTagName("width").item(0).getTextContent(), "page", "width");
        saveSettingXML(element.getElementsByTagName("height").item(0).getTextContent(), "page", "height");
        list = document.getElementsByTagName("columns");

        element = (Element) list.item(0);
        for (int j = 0; j < element.getElementsByTagName("column").getLength(); j++) {
            saveSettingXML(element.getElementsByTagName("title").item(j).getTextContent(), "col" + j, "title");
            saveSettingXML(element.getElementsByTagName("width").item(j).getTextContent(), "col" + j, "width");

        }
    }

    private static void writeReport(String fileReader, String fileWriter) throws Exception {

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

    private static void checkRow(String header, int height, BufferedWriter writer) throws IOException {
        if (count >= height) {
            writer.write("~\n");
            count = 0;
            writeRow(header, writer);
        }
    }

    private static void writeRow(String row,  BufferedWriter writer) throws IOException {
        writer.write(row + "\n");
        count++;
    }


    private static void saveSettingXML(String element, String key, String val) {
        Map<String, String> map = mapSettingXML.computeIfAbsent(key, k -> new HashMap<>());
        map.put(val, element);
    }

    private static String parse(String key, String value,  String...strings) {
        int width = Integer.parseInt(mapSettingXML.get(key).get("width"));
        String space = "";
        int size = width - value.length();
        if (size < 0) {
            String[] strSplit = value.split(" ");
            if(strSplit.length != 2) {
                int mid = key.equals("col2") ? 7 : 6;
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

    private static String line() {
        StringBuilder line = new StringBuilder();
        int width = Integer.parseInt(mapSettingXML.get("page").get("width"));
        for (int i = 0; i < width; i++) {
            line.append("-");
        }
        return line.toString();
    }

}
