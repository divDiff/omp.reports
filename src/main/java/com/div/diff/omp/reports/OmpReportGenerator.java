package com.div.diff.omp.reports;

import java.io.IOException;

import com.div.diff.omp.report.processors.ThankYouProcessor;

/**
 * Hello world!
 *
 */
public class OmpReportGenerator {
	public static void main(String[] args) throws NumberFormatException, IOException {
		String csvPath = args[0];
		String year = args[1];
		String tyPath = args[2];
		System.out.println("Starting " + year + " thank you letter generation...");
		ThankYouProcessor typ = new ThankYouProcessor();
		typ.createThankYous(csvPath, new Integer(year).intValue(), tyPath);
	}
}
