package com.pack.ratelimiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pack.reader.CsvReaderService;
import com.pack.writer.WriterService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Project: Rate Limit.
 * 
 * @author AbhishekK
 */
public class GithubPuplicApiService {

	// d97aa3ba2c62cd18465ff3d23e775f5eda2dd9ae -- private token
	private final String token = "token d97aa3ba2c62cd18465ff3d23e775f5eda2dd9ae";
	private static int remainingCoreCalls = 0;
	private static int remainingSearchCalls = 0;
	private static ArrayList<User> listOfUsers = new ArrayList<>();
	public static String responseToWrite = "";

	public GithubPuplicApiService() {
	}

	public void fetchUserInfo() {
		getRateLimits();
		for (int i = 0; i < listOfUsers.size(); i++) {
			User user = listOfUsers.get(i);
			List<String> userLoginIdList = getUserLoginIdList(user);
			if (userLoginIdList.isEmpty()) {
				System.out.println("User not found : " + user.firstName + " " + user.lastName
						+ "\n----------------------------------------------------------------------------------------------------------------\n");
				responseToWrite += "\n User not found : " + user.firstName + " " + user.lastName
						+ "\n----------------------------------------------------------------------------------------------------------------\n";
			}
			for (String userLoginId : userLoginIdList) {
				user.userLoginId = userLoginId;

				JSONObject userProfile = getUserProfile(userLoginId);
				user.userProfile = userProfile;

				JSONArray userRepository = getUserRepository(userLoginId);
				user.userRepository = userRepository;

				ArrayList<String> listOfRepos = user.getRepositoryNames();

				HashMap<String, Integer> commitValues = getUserCommits(listOfRepos, userLoginId);
				user.commitValues = commitValues;

				printUserInfo(user);
			}
		}
	}

	public void getRateLimits() {
		final String url = "https://api.github.com/rate_limit";
		Request requestRateLimit = new Request.Builder().url(url).get().addHeader("Authorization", token).build();
		OkHttpClient client = new OkHttpClient();
		Response response;
		try {
			response = client.newCall(requestRateLimit).execute();
			final String responseString = response.body().string();
			JSONObject object = new JSONObject(responseString);
			JSONObject resource = object.getJSONObject("resources");
			JSONObject core = resource.getJSONObject("core");
			JSONObject search = resource.getJSONObject("search");
			remainingCoreCalls = core.getInt("remaining");
			remainingSearchCalls = search.getInt("remaining");
		} catch (Exception e) {
			System.out.println("error ocurred in retreiving rate limits");
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> getUserCommits(ArrayList<String> listOfRepos, String userLoginId) {
		HashMap<String, Integer> commitValues = new HashMap<>();
		OkHttpClient client = new OkHttpClient();
		for (int i = 0; i < listOfRepos.size(); i++) {
			String repoName = listOfRepos.get(i);
			Request requestNumberOfCommits = new Request.Builder()
					.url("https://api.github.com/repos/" + userLoginId + "/" + repoName + "/commits").get()
					.addHeader("Authorization", token).build();
			Response response;
			try {
				while (remainingCoreCalls == 0) {
					getRateLimits();
				}
				response = client.newCall(requestNumberOfCommits).execute();
				final String responseString = response.body().string();
				JSONArray arr = new JSONArray(responseString);
				int numberOfCommits = arr.length();
				commitValues.put(repoName, numberOfCommits);
				remainingCoreCalls--;
			} catch (Exception e) {
			}
		}
		return commitValues;
	}

	public JSONObject getUserProfile(String userLoginId) {
		OkHttpClient client = new OkHttpClient();
		Request requestUserProfile = new Request.Builder().url("https://api.github.com/users/" + userLoginId).get()
				.addHeader("Authorization", token).build();
		Response response;
		JSONObject myObject = null;
		try {
			while (remainingCoreCalls == 0) {
				getRateLimits();
			}
			response = client.newCall(requestUserProfile).execute();
			final String responseString = response.body().string();
			myObject = new JSONObject(responseString);
			remainingCoreCalls--;
		} catch (Exception e) {
			System.out.println("error ocurred in retreiving user profile");
		}

		return myObject;
	}

	public JSONArray getUserRepository(String userLoginId) {
		OkHttpClient client = new OkHttpClient();
		Request requestNameOfRepos = new Request.Builder()
				.url("https://api.github.com/users/" + userLoginId + "/repos?page=1&per_page=100").get()
				.addHeader("Authorization", token).build();
		Response response;
		JSONArray myArray = null;
		try {
			while (remainingCoreCalls == 0) {
				getRateLimits();
			}
			response = client.newCall(requestNameOfRepos).execute();
			final String responseString = response.body().string();
			myArray = new JSONArray(responseString);
			remainingCoreCalls--;
		} catch (Exception e) {
			System.out.println("error ocurred in retreiving user repos");
		}

		return myArray;
	}

	public List<String> getUserLoginIdList(User user) {
		String url = "https://api.github.com/search/users?q=" + user.firstName + "%20in:fullname+%20" + user.lastName
				+ "in:fullname";

		if (user.location.length() != 0) {
			url += "location" + user.location;
		}
		url += "?page=1&per_page=100";

		OkHttpClient client = new OkHttpClient();
		Request requestuserLoginId = new Request.Builder().url(url).get().addHeader("Authorization", token).build();

		Response response;

		List<String> userLoginIdList = new ArrayList<>();
		String userLoginId = "";
		try {
			while (remainingSearchCalls == 0) {
				getRateLimits();
			}
			response = client.newCall(requestuserLoginId).execute();
			final String responseString = response.body().string();
			JSONObject myOb = new JSONObject(responseString);
			JSONArray Arr = myOb.getJSONArray("items");
			for (int i = 0; i < Arr.length(); i++) {
				JSONObject myObject = Arr.getJSONObject(i);
				userLoginId = "" + myObject.get("login");
				userLoginIdList.add(userLoginId);
			}
			response.headers("link");

			remainingSearchCalls--;
		} catch (Exception e) {
			System.out.println("error ocurred in retreiving userLoginId");
		}
		return userLoginIdList;
	}

	public void printUserInfo(User user) {

		String profile = "FirstName : " + user.firstName + "\n" + "LastName : " + user.lastName + "\n" + "Location : "
				+ user.location + "\n" + "userLoginId : " + user.userLoginId + "\n" + "Bio : " + user.getBio() + "\n"
				+ "Email : " + user.getEmailId() + "\n";

		System.out.println(profile); // 2

		ArrayList<String> listOfRepos = user.getRepositoryNames();
		String repoCommitInfo = "";
		String format = "%1$4s\t%2$-50S\t\t%3$-20s";
		String headers = String.format(format, "Sno.", "RepoName", "No. of commits");
		if (listOfRepos.size() == 0){
			System.out.println("\nRepo not found.");
			responseToWrite += "\nRepo not found.";
		}
		for (int i = 0; i < listOfRepos.size(); i++) {
			String repoName = listOfRepos.get(i);
			String commitValue = "" + user.commitValues.get(repoName);
			String output = String.format(format, i, repoName, commitValue);
			if (i == 0)
				repoCommitInfo += headers + "\n\n";

			repoCommitInfo += output + "\n\n";
		}
		System.out.println(repoCommitInfo
				+ "\n----------------------------------------------------------------------------------------------------------------"); // 1

		responseToWrite = responseToWrite + profile + "\n" + repoCommitInfo
				+ "\n----------------------------------------------------------------------------------------------------------------\n";
	}

	public GithubPuplicApiService(ArrayList<User> listOfUsers) {
		fetchUserInfo();
	}

	/**
	 * @param: csv
	 *             input file.
	 * 
	 *             Code commences from main.
	 */

	public static void main(String args[]) {
		CsvReaderService reader = new CsvReaderService();
		String readPath = "src\\main\\resources\\usersCSV.csv"; // input data in
																// CSV format
																// (firstName,
																// Lastname,
																// Location).
		String writePath = "src\\main\\resources\\output"; // output file having
															// analytics.

		listOfUsers.addAll(reader.readUsersFromCSV(readPath));
		GithubPuplicApiService apiService = new GithubPuplicApiService(listOfUsers);

		WriterService write = new WriterService();
		write.writeUsersInFile(writePath, responseToWrite);

		System.out.println("\t\t<------------------------- Completed Processing ------------------------->");
		responseToWrite += "\t\t<------------------------- Completed Processing ------------------------->";

	}

}
