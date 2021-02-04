package com.div.diff.omp.reports;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		System.out.println("Starting " + year + " data processing...");

		DonorGiftUtil dgu = new DonorGiftUtil();
		Map<String, List<Transaction>> donorGifts = dgu.assembleDonorData(dataPath);
		if (makeThankYous) {
			System.out.println("Starting " + year + " thank you letter ");
			ThankYouProcessor typ = new ThankYouProcessor();
			typ.createThankYous(donorGifts, new Integer(year).intValue(), outPath);
			System.out.println("Completed thank you letter creation");
		}
		if (makeReports) {
			ReportsProcessor rp = new ReportsProcessor();
			System.out.println("Starting report generation");
			rp.generateMonthlyReports(dataPath, outPath, year, false);
			System.out.println("Completed report generation");
		}
	}

	private static void loadProps(String propsPath) throws IOException {
		Properties p = new Properties();
		BufferedReader read = new BufferedReader(new FileReader(propsPath));
		try {
			p.load(read);
			outPath = p.getProperty("out.path");
			dataPath = p.getProperty("data.path");
			year = p.getProperty("year");
			makeThankYous = p.getProperty("generate.thank.yous").equals("true") ? true : false;
			makeReports = p.getProperty("generate.report").equals("true") ? true : false;
		} finally {
			read.close();
		}
	}
}
