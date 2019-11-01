package com.div.diff.omp.reports.processors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.div.diff.omp.reports.model.Transaction;

/**
 * 
 * @author divdiff
 *
 */
public class ReportsProcessor extends OmpProcessor {
	private String header = "Month\tDonor\tAmount\tFees\tNet\tPayment Method\tPayment Date\tDeposit Date";

	public void generateMonthlyReports(String donorData, String outPath, String year, boolean append)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(donorData));
		String row = "";

		PrintWriter pw = makeWriter(outPath + year + "-omp-report.tsv");
		double monthlyAmount = 0.0;
		double monthlyFees = 0.0;
		double monthlyNet = 0.0;
		int currMonth = 1;
		while ((row = reader.readLine()) != null) {
			try {
				if (row.contains("Date\tName")) {
					if (append) {
						continue;
					}
					pw.println(header);
				}
				Transaction t = new Transaction(row);
				String name = getName(row);
				monthlyAmount += t.getGross();
				monthlyFees += t.getCost();
				monthlyNet += t.getNet();

				pw.println("\t" + name + "\t" + t.getGross() + "\t" + t.getCost() + "\t" + t.getNet() + "\t\t"
						+ t.getTransDate().toString());
				if (t.getTransDate().getMonthValue() - currMonth > 0) {
					pw.println("Total\t\t" + monthlyAmount + "\t" + monthlyFees + "\t" + monthlyNet);
					pw.println();
					currMonth++;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(row);
			}
		}
		pw.flush();
	}

	private String getName(String row) {
		String[] cells = row.split("\t");
		return cells[1];
	}
}
