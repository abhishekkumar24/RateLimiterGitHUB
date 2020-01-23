package com.pack.ratelimiter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
	public String firstName;
	public String lastName;
	public String location;
	public String userLoginId;
	public JSONObject userProfile;
	public JSONArray userRepository;
	public HashMap<String, Integer> commitValues;

	public User() {
	}

	public User(String firstName, String lastName, String location) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.location = location;
	}

	public ArrayList<String> getRepositoryNames() {
		ArrayList<String> nameOfRepos = new ArrayList<String>();
		if (null == userRepository) {
			System.out.println("No repository present.");
			return null;
		}
		try {
			for (int i = 0; i < userRepository.length(); i++) {
				JSONObject obj = userRepository.getJSONObject(i);
				String name = "";
				if (obj.isNull("name")) {
					name = "";
				} else {
					name = obj.getString("name");
				}
				nameOfRepos.add(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error while parsing repos array.");
		}
		return nameOfRepos;
	}

	public String getEmailId() {
		String emailId = "";
		try {
			if (userProfile.isNull("email")) {
				emailId = "";
			} else {
				emailId = userProfile.getString("email");
			}
		} catch (Exception e) {
			System.out.println("error while parsing email from user profile.");
		}
		return emailId;
	}

	public String getHireableStatus() {
		String hireableStatus = "";
		try {
			if (userProfile.isNull("name")) {
				hireableStatus = "";
			} else {
				hireableStatus = userProfile.getString("name");
			}
		} catch (Exception e) {
			System.out.println("error while parsing hireable from user profile.");
		}
		return hireableStatus;

	}

	public String getBio() {
		String profileBio = "";
		try {
			if (userProfile.isNull("bio")) {
				profileBio = "";
			} else {
				profileBio = userProfile.getString("bio");
			}
		} catch (Exception e) {
			System.out.println("error while parsing bio from user profile");
		}
		return profileBio;
	}

	public String getCompany() {
		String company = "";
		try {
			if (userProfile.isNull("company")) {
				company = "";
			} else {
				company = userProfile.getString("company");
			}
		} catch (Exception e) {
			System.out.println("error while parsing company from user profile");
		}
		return company;
	}

	public String getCreatedAt() {
		String createdAt = "";
		try {
			if (userProfile.isNull("created_at")) {
				createdAt = "";
			} else {
				createdAt = userProfile.getString("created_at");
			}
		} catch (Exception e) {
			System.out.println("error while parsing created from user profile");
		}
		return createdAt;
	}

	public int getFollowing() {
		int totalFollowing = 0;
		try {
			totalFollowing += userProfile.getInt("following");
		} catch (Exception e) {
			System.out.println("error while parsing following from user profile");
		}
		return totalFollowing;
	}

	public int getFollowers() {
		int totalFollowers = 0;
		try {
			totalFollowers += userProfile.getInt("followers");
		} catch (Exception e) {
			System.out.println("error while parsing followers from user profile");
		}
		return totalFollowers;
	}

}