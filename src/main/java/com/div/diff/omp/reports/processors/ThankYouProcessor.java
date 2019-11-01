package com.div.diff.omp.report.processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.div.diff.omp.reports.model.Transaction;

public class ThankYouProcessor {
	private Map<String, List<Transaction>> donorGifts;

	public void createThankYous(String csvPath, int year, String tyOutDir) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(csvPath));
		donorGifts = new HashMap<>();
		String row = "";
		while ((row = reader.readLine()) != null) {
			try {
				if (row.contains("Date\tName")) {
					continue;
				}
				Transaction t = new Transaction(row);
				String name = getName(row);
				List<Transaction> gifts = donorGifts.get(name);
				if (donorGifts.get(name) == null) {
					gifts = new ArrayList<>();
					gifts.add(t);
					donorGifts.put(name, gifts);
				} else {
					// TODO add code to perform smarter duplicate matching
					boolean dupFound = detectDuplicates(gifts, t);
					if (dupFound) {
						continue;
					} else {
						gifts.add(t);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(row);
			}
		}
		reader.close();
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

	private String getName(String row) {
		String[] cells = row.split("\t");
		return cells[1];
	}

	private boolean detectDuplicates(List<Transaction> gifts, Transaction t) {
		for (Transaction gift : gifts) {
			if (gift.toString().equals(t.toString())) {
				return true;
			}
		}
		return false;
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
			contentStream.endText();

			System.out.println("Content added for donor " + donor);
		}
		contentStream.close();
		thankYou.addPage(tyPage);
		thankYou.save(outDir + "/" + donor + "-" + year + "-thank-you.pdf");
		thankYou.close();
	}

	private PrintWriter makeWriter(String pathToFile) throws IOException {
		return new PrintWriter(new BufferedWriter(new FileWriter(pathToFile, true)));
	}

	private String transliterate(String message) {
		char[] abcCyr = { ' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р',
				'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е',
				'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
				'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		String[] abcLat = { " ", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p",
				"r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G",
				"D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts",
				"Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
				"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
				"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			for (int x = 0; x < abcCyr.length; x++) {
				if (message.charAt(i) == abcCyr[x]) {
					builder.append(abcLat[x]);
				}
			}
		}
		return builder.toString();
	}
}
