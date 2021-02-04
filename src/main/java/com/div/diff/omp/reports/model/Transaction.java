package com.div.diff.omp.reports.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transaction {
	private LocalDate transDate;
	private Float gross;
	private Float cost;
	private Float net;

	public Transaction() {
		super();
	}

	public Transaction(String csvData) {
		String[] data = csvData.split("\t");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		transDate = LocalDate.parse(data[0], formatter);
		try {
			setNumbers(data);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			String reformatted = new String(csvData);
			Pattern numberWithCommas = Pattern.compile("-?([0-9\\.\\,])*");
			Matcher match = numberWithCommas.matcher(csvData);
			while (match.find()) {
				String m = match.group();
				String copy = new String(m);
				copy = copy.replace("\"", "");
				copy = copy.replace(",", "");
				reformatted = reformatted.replace(m, copy);
			}

			data = reformatted.split("\t");
			setNumbers(data);
		}
	}

	public LocalDate getTransDate() {
		return transDate;
	}

	public void setTransDate(LocalDate transDate) {
		this.transDate = transDate;
	}

	public Float getGross() {
		return gross;
	}

	public void setGross(Float gross) {
		this.gross = gross;
	}

	public Float getCost() {
		return cost;
	}

	public void setCost(Float cost) {
		this.cost = cost;
	}

	public Float getNet() {
		return net;
	}

	public void setNet(Float net) {
		this.net = net;
	}

	@Override
	public String toString() {
		return transDate.toString() + " " + gross;
	}

	private String cleanNumber(String num) {
		num = num.replace("\"", "");
		num = num.replace(",", "");
		num = num.replace("$", "");
		return num;
	}

	private void setNumbers(String[] data) {
		gross = new Float(cleanNumber(data[2]));
		cost = new Float(cleanNumber(data[3]));
		net = new Float(cleanNumber(data[4]));
	}
}