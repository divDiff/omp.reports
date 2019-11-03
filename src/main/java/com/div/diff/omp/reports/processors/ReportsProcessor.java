package com.div.diff.omp.reports.processors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
		double monthlyWithdrawal = 0.0;
		int currMonth = 1;
		while ((row = reader.readLine()) != null) {
			try {
				if (row.contains("Date\tName")) {
					if (append) {
						continue;
					}
					pw.println(header);
					continue;
				}
				Transaction t = new Transaction(row);
				String name = transliterate(getName(row));

				if (t.getTransDate().getMonthValue() - currMonth > 0) {
					DecimalFormat df = new DecimalFormat("#.##");
					df.setRoundingMode(RoundingMode.CEILING);
					pw.println("Total\t\t" + df.format(monthlyAmount) + "\t" + df.format(monthlyFees) + "\t"
							+ df.format(monthlyNet));
					pw.println("Monthly PayPal Withdrawal\t\t" + df.format(monthlyWithdrawal));
					pw.println();
					currMonth++;
					monthlyAmount = 0;
					monthlyFees = 0;
					monthlyNet = 0;
				}
				String[] cells = row.split("\\t");
				if (!cells[2].equals("General Withdrawal")) {
					monthlyAmount += t.getGross();
					monthlyFees += t.getCost();
					monthlyNet += t.getNet();
				} else {
					monthlyWithdrawal += t.getGross();
				}

				pw.println("\t" + name + "\t" + t.getGross() + "\t" + t.getCost() + "\t" + t.getNet() + "\t"
						+ "Pay Pal\t" + t.getTransDate().toString());

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
