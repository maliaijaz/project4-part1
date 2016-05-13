package test;

import java.io.IOException;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class JSONDemo {

	public static void main(String[] args) {

		// create database connection
		int id;
		DBProc dbp = null;
		dbp = new DBProc();

		StringWriter out = new StringWriter();

		// array will be stored in here
		JSONObject obj = new JSONObject();

		// array will store each movie title
		JSONArray array = new JSONArray();

		ArrayList<Movie> movies = new ArrayList<Movie>();

		// The value passed into this function will be from user input
		// so whatever the user types in the search bar is immediately grabbed
		// by AJAX, AJAX sends this value
		// via a HTTP request to this function.
		movies = dbp.getMoviesByTitleForAjax("Spi");

		for (int i = 0; i < movies.size(); i++) {
			System.out.println("Here is the movie title: " + movies.get(i).getTitle());
		}

		for (Movie mv : movies) {
			array.add(mv.getTitle());
		}

		// add array to JSONObject obj
		obj.put("titles", array);

		try {
			obj.writeJSONString(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// write the JSON object to an external file to later be used in the
			// JavaScript file
			// search your comp for this file
			FileWriter file = new FileWriter("JSONDemo.json");
			file.write(obj.toJSONString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print(obj);

	}

}
