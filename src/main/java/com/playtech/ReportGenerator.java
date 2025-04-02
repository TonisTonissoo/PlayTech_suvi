package com.playtech;

import com.playtech.report.CSVReader;
import com.playtech.report.JsonlOutputGenerator;
import com.playtech.report.Report;
import com.playtech.report.TypedRecord;
import com.playtech.report.transformer.Transformer;
import com.playtech.util.xml.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Application should have 3 paths as arguments: csv file path, xml file path, and output directory path");
            System.exit(1);
        }

        String csvDataFilePath = args[0];
        String reportXmlFilePath = args[1];
        String outputDirectoryPath = args[2];

        try {
            Report report = XmlParser.parseReport(reportXmlFilePath);

            CSVReader reader = new CSVReader();
            List<TypedRecord> csvData = reader.readCsv(csvDataFilePath, report.getInputs());

            List<Map<String, Object>> transformedData = new ArrayList<>();
            for (TypedRecord record : csvData) {
                transformedData.add(record.getAll());
            }

            for (Transformer transformer : report.getTransformers()) {
                transformer.transform(report, transformedData);
            }

            List<Object> outputData = new ArrayList<>(transformedData);
            JsonlOutputGenerator jsonlGenerator = new JsonlOutputGenerator();
            String outputFilePath = outputDirectoryPath + "/" + report.getReportName() + ".jsonl";
            jsonlGenerator.writeToJsonl(outputData, outputFilePath);

            System.out.println("Report successfully written to: " + outputFilePath);

        } catch (Exception e) {
            System.err.println("Error occurred:");
            e.printStackTrace();
        }
    }
}
