package report;

public class Generator {

    public static void main(String[] args) throws Exception {
        ParserXML parserXML = new ParserXML();
        parserXML.parseXML(args[0]);
        WriterReport writerReport = new WriterReport(parserXML.getMapSettingXML());
        writerReport.writeReport(args[1], args[2]);

    }

}
