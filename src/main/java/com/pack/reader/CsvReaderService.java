package com.pack.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.pack.ratelimiter.User;

public class CsvReaderService {

	public static List<User> readUsersFromCSV(String fileName) {
		List<User> userList = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			// read the first line from the text file
			String line = br.readLine();

			// loop until all lines are read
			while (line != null) {

				// use string.split to load a string array with the values from
				// each line of
				// the file, using a comma as the delimiter
				String[] attributes = line.split(",");

				User user = createUserObject(attributes);
				userList.add(user);

				// read next line before looping
				// if end of file reached, line would be null
				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("error while reading input.");
		}

		return userList;
	}

	private static User createUserObject(String[] metadata) {
		String firstName = metadata[0];
		String lastName = metadata[1];
		String location = "";
		if (metadata.length >= 3) {
			location = metadata[2];
		}
		// create and return book of this metadata
		return new User(firstName, lastName, location);
	}
}
