package com.div.diff.omp.reports.processors;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.div.diff.omp.reports.model.Transaction;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class ThankYouProcessor extends OmpProcessor {

	public void createThankYous(Map<String, List<Transaction>> donorGifts, int year, String tyOutDir)
			throws IOException {

		String reportPath = tyOutDir + year + "-Donor-Report.csv";
		PrintWriter pw = makeWriter(reportPath);
		pw.println("Donor,Annual Total");
		for (Map.Entry<String, List<Transaction>> donor : donorGifts.entrySet()) {
			@SuppressWarnings("deprecation")
			Float annualTotal = new Float(0.0);
			for (Transaction gift : donor.getValue()) {
				annualTotal += gift.getGross();
			}
			pw.println(donor.getKey() + "," + annualTotal.intValue());
			try {
				generateDonorThankYou(donor.getKey(), annualTotal, tyOutDir, year);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pw.flush();
		System.out.println("Data writen to " + tyOutDir);
	}

	// TODO Consider retooling this to use iText instead of PDFBox
	private void generateDonorThankYou(String donor, Float annualTotal, String outDir, int year) throws IOException {
		Pattern cyrillic = Pattern.compile("[А-яЁё]+");
		Matcher match = cyrillic.matcher(donor);

		// Don't create report for General Withdrawals
		String donorNoSpace = donor.replace(" ", "-");
		if (!donor.equals("")) {
			if (match.find()) {
				donor = transliterate(donor);
			}
			DecimalFormat df = new DecimalFormat("#.##");

			StringBuilder text = new StringBuilder();
			text.append("On behalf of the Orthodox Mission in Pakistan, we thank you for your support of OMP in " + year
					+ " in the ");
			text.append("amount of $" + df.format(annualTotal)
					+ ". Your donation provides critical support to Fr John Tanveer and the Orthodox ");
			text.append("faithful of Pakistan. Fr John relies on the generosity of donors "
					+ "like you to carry out his ministry. Your financial support purchases "
					+ "food and medicine for widows ");
			text.append("and the poor, funds the operating costs of the women’s sewing center, ");
			text.append("provides material help to students, offers legal ");
			text.append("aid to Orthodox Christians who are falsely accused of blasphemy (speaking ");
			text.append("against Islam), provides emergency ");
			text.append("assistance to those in dire need, and enables Fr John to carry out his pastoral duties. ");
			text.append("Fr John writes, \"Although I am not so strong yet due to your great support "
					+ "I am trying and go on trying with God’s help to ");
			text.append("work for the needy and save the people who are in trouble.\"");
			text.append("\n\nAgain, we thank you.\n\n");
			text.append("Please keep Fr John, Presvytera Rosy, their children, and all of the Orthodox "
					+ "faithful of Pakistan in your prayers.");

			PdfWriter writer = new PdfWriter(outDir + "/" + donorNoSpace + "-" + year + "-thank-you.pdf");
			PdfDocument pdf = new PdfDocument(writer);
			Document thankYou = new Document(pdf);
			thankYou.add(new Paragraph("Dear " + donor + ","));
			thankYou.add(new Paragraph(text.toString()));

			System.out.println("Thank you letter created for" + " " + donor);
			thankYou.close();
		}
	}
}
