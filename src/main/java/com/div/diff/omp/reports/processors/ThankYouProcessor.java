package com.div.diff.omp.reports.processors;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.div.diff.omp.reports.model.Transaction;

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
		PDDocument thankYou = new PDDocument();
		PDPage tyPage = new PDPage();
		PDPageContentStream contentStream = new PDPageContentStream(thankYou, tyPage);
		contentStream.beginText();
		Pattern cyrillic = Pattern.compile("[А-яЁё]+");
		Matcher match = cyrillic.matcher(donor);
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
		contentStream.setLeading(14.5f);
		contentStream.newLineAtOffset(25, 750);

		// Don't create report for General Withdrawals
		if (!donor.equals("")) {
			if (match.find()) {
				donor = transliterate(donor);
			}

			String greet = "Dear " + donor + ",";
			String line1 = "On behalf of the Orthodox Mission in Pakistan, we thank you for your support of OMP in "
					+ year + " in the amount of $" + annualTotal.toString() + ".";
			String line2 = "Your donation provides critical support to Fr John Tanveer and the Orthodox "
					+ "faithful of Pakistan. Fr John relies on the ";
			String line3 = "generosity of donors like you to carry out his ministry. Your financial "
					+ "support purchases food and medicine for widows";
			String line4 = "and the poor, funds the operating costs of the women’s sewing center, "
					+ "provides material help to students, offers legal ";
			String line5 = "aid to Orthodox Christians who are falsely accused of blasphemy (speaking "
					+ "against Islam), provides emergency ";
			String line6 = "assistance to those in dire need, and enables Fr John to carry out his pastoral duties. "
					+ "Fr John writes, \"Although I am not ";
			String line7 = "so strong yet due to your great support I am trying and go on trying with God’s help to "
					+ "work for the needy and save the ";
			String line8 = "people who are in trouble.\"";
			String finalThanks = "Again, we thank you.";
			String prayer = "Please keep Fr John, Presvytera Rosy, their children, and all of the Orthodox faithful of Pakistan in your prayers.";

			contentStream.showText(greet);
			contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
			contentStream.newLine();
			contentStream.newLine();
			contentStream.showText(line1);
			contentStream.newLine();
			contentStream.showText(line2);
			contentStream.newLine();
			contentStream.showText(line3);
			contentStream.newLine();
			contentStream.showText(line4);
			contentStream.newLine();
			contentStream.showText(line5);
			contentStream.newLine();
			contentStream.showText(line6);
			contentStream.newLine();
			contentStream.showText(line7);
			contentStream.newLine();
			contentStream.showText(line8);
			contentStream.newLine();
			contentStream.newLine();
			contentStream.showText(finalThanks);
			contentStream.newLine();
			contentStream.newLine();
			contentStream.showText(prayer);

			System.out.println("Content added for donor " + donor);
		}
		contentStream.endText();
		contentStream.close();
		thankYou.addPage(tyPage);
		thankYou.save(outDir + "/" + donor + "-" + year + "-thank-you.pdf");
		thankYou.close();
	}
}
