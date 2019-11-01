package com.div.diff.omp.reports;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.div.diff.omp.reports.model.Transaction;
import com.div.diff.omp.reports.processors.ReportsProcessor;
import com.div.diff.omp.reports.processors.ThankYouProcessor;
import com.div.diff.omp.reports.util.DonorGiftUtil;

/**
 * Hello world!
 *
 */
public class OmpReportGenerator {

	private static String outPath;
	private static String dataPath;
	private static String year;
	private static boolean makeThankYous;
	private static boolean makeReports;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws NumberFormatException, IOException {
		loadProps(args[0]);
		System.out.println("Starting " + year + " thank you letter generation...");

		DonorGiftUtil dgu = new DonorGiftUtil();
		Map<String, List<Transaction>> donorGifts = dgu.assembleDonorData(dataPath);
		if (makeThankYous) {
			ThankYouProcessor typ = new ThankYouProcessor();
			typ.createThankYous(donorGifts, new Integer(year).intValue(), outPath);
		}
		if (makeReports) {
			ReportsProcessor rp = new ReportsProcessor();
			rp.generateMonthlyReports(dataPath, outPath, year, true);
		}
	}

	private static void loadProps(String propsPath) throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(propsPath));
		String line = "";
		try {
			while ((line = read.readLine()) != null) {
				String[] parts = line.split("=");
				switch (parts[0]) {
				case "out.path":
					outPath = parts[1];
					break;
				case "data.path":
					dataPath = parts[1];
					break;
				case "year":
					year = parts[1];
					break;
				case "generate.thank.yous":
					if ("true".equals(parts[1])) {
						makeThankYous = true;
					} else {
						makeThankYous = false;
					}
					break;
				case "generate.report":
					if ("true".equals(parts[1])) {
						makeReports = true;
					} else {
						makeReports = false;
					}
					break;
				}
			}
		} finally {
			read.close();
		}
	}
}
