package com.div.diff.omp.reports.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.div.diff.omp.reports.model.Transaction;

public class DonorGiftUtil {

	public Map<String, List<Transaction>> assembleDonorData(String dataPath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataPath));
		Map<String, List<Transaction>> donorGifts = new HashMap<>();
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
		return donorGifts;
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
}
