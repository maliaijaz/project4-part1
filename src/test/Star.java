package test;

import java.util.ArrayList;

public class Star {
	private int star_id;
	private String star_first_name;
	private String star_last_name;
	private String star_dob; // Date of Birth is in YYYY-MM-DD ex 1979-4-19 =
								// April 19, 1979
	private String star_photo;
	private ArrayList<Movie> movies;

	public Star(int id, String first_name, String last_name, String birthdate, String photo_url) {
		star_id = id;
		star_first_name = first_name;
		star_last_name = last_name;
		setStar_dob(birthdate);
		star_photo = processUrl(photo_url);
		movies = new ArrayList<Movie>();
	}

	public String processUrl(String url) { // processUrl can also act as setUrl
											// but the fields must first be
											// accessed by their get functions
		String lowercase = url.toLowerCase();
		if (lowercase.matches("^\\w+://.*")) {
			// Do Nothing Because its already formatted as http://url
		} else { // To format it
			lowercase = "http://" + lowercase;
		}
		return lowercase;
	}

	public String getPhoto() {
		return star_photo;
	}

	public String getFirst_name() {
		return star_first_name;
	}

	public void setFirst_name(String first_name) {
		star_first_name = first_name;
	}

	public String getLast_name() {
		return star_last_name;
	}

	public void setLast_name(String last_name) {
		star_last_name = last_name;
	}

	public ArrayList<Movie> getMovies() {
		return movies;
	}

	public void setMovies(ArrayList<Movie> Movies) {
		movies = Movies;
	}

	public int getId() {
		return star_id;
	}

	public void setId(int id) {
		star_id = id;
	}

	public void addMovie(Movie movie) {
		movies.add(movie);
	}

	public void clearMovies() {
		movies.clear();
	}

	public String getStar_dob() {
		return star_dob;
	}

	public void setStar_dob(String birthdate) {
		star_dob = birthdate;
	}

}
