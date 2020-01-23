package com.pack.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriterService {

	public void writeUsersInFile(String path, String userInfo) {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);

			bw.write(userInfo);

		} catch (IOException e) {
			System.out.println("error while writing output.");
		} finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {
				System.out.println("error while closing io.");
			}
		}
	}

}
