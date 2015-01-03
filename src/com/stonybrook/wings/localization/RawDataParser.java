package com.stonybrook.wings.localization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class RawDataParser {
	private String rawDataFolderName = "resources/rawData/";
	private String processedFolderName = "resources/processedData/";
	private String inputArffFile = "resources/input.arff";
	private String prefixFileName = "resources/prefix.txt";
	private HashMap<String, Integer> files = new HashMap<String, Integer>();
	private BufferedWriter writer = null;

	public static void main(String[] args) {
		RawDataParser p = new RawDataParser();
		try {
			p.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		writer = new BufferedWriter(new FileWriter(inputArffFile));
		prepareInputFile(prefixFileName);
		files.put("1", 1);
		files.put("2", 2);
		files.put("3", 3);
		files.put("4", 4);
		files.put("5", 5);
		files.put("6", 6);
		files.put("7", 7);
		files.put("8", 8);
		files.put("9", 9);
		files.put("10", 10);
		files.put("11", 11);
		files.put("12", 12);
		Iterator<String> i = files.keySet().iterator();
		while (i.hasNext()) {
			String entry = i.next();
			parseFile(entry, files.get(entry));
		}
		System.out.println("done");
		writer.close();
	}

	private void prepareInputFile(String fileName) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(
				fileName));
		String line = null;
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.write("\n");
		}
		reader.close();
	}

	private void parseFile(String fileName, Integer locationId)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				rawDataFolderName + fileName + ".arff"));
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(
				processedFolderName + fileName + "_processed.arff"));
		String line = null;
		Integer lineNum = 0;
		while ((line = reader.readLine()) != null) {
			lineNum++;
			// removing extraneous spaces.
			String processed = line.trim();

			// removing line number
			processed = processed.substring(processed.indexOf(":") + 1).trim();
			processed = processed.replace("[", "");
			processed = processed.replace("nan", "?");
			processed = processed.replace("]", "");
			processed = processed.replace(" ", ",");
			processed = processed + "," + locationId;

			fileWriter.write(processed);
			fileWriter.write("\n");
			writer.write(processed);
			writer.write("\n");

			String[] processedArr = processed.split("\\,");

			if (processedArr.length != 217) {
				System.out.println("Error occured at line number: " + lineNum);
			}

		}
		reader.close();
		fileWriter.close();
	}
}
