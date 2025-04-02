package com.playtech;

import com.playtech.report.CSVReader;
import com.playtech.report.Report;
import com.playtech.util.xml.XmlParser;
import jakarta.xml.bind.JAXBException;

import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Application should have 3 paths as arguments: csv file path, xml file path, and output directory path");
            System.exit(1);
        }
        String csvDataFilePath = args[0], reportXmlFilePath = args[1], outputDirectoryPath = args[2];

        try {
            Report report = XmlParser.parseReport(reportXmlFilePath);
            CSVReader reader = new CSVReader();
            List<String[]> csvData = reader.readCsv(csvDataFilePath);

            // TODO: Implement data transformation based on report configurations

            // For demonstration, assuming 'transformedData' is the list of transformed objects
            List<Object> transformedData = new ArrayList<>(); // This should be filled with actual data

            JsonlOutputGenerator jsonlGenerator = new JsonlOutputGenerator();
            jsonlGenerator.writeToJsonl(transformedData, outputDirectoryPath + "/output.jsonl");
        } catch (Exception e) {
            System.err.println("Error occurred:");
            e.printStackTrace();
        }
    }
}

