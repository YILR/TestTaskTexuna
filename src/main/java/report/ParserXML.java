package report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParserXML {
    private Map<String, Map<String, String>> mapSettingXML = new HashMap<>();

    public void parseXML(String setting) throws ParserConfigurationException, IOException, SAXException {
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

    private void saveSettingXML(String element, String key, String val) {
        Map<String, String> map = mapSettingXML.computeIfAbsent(key, k -> new HashMap<>());
        map.put(val, element);
    }

    public  Map<String, Map<String, String>> getMapSettingXML() {
        return mapSettingXML;
    }
}
