package test;

import java.util.ArrayList;

public class Movie {

	private int movie_id;
	private String movie_title;
	private int year;
	private String director;
	private String banner;
	private String trailer;
	private ArrayList<Genre> genres;
	private ArrayList<Star> stars;

	public Movie(int id, String title, int release, String guy_who_made_the_film, String poster_url,
			String trailer_url) {
		movie_id = id;
		movie_title = title;
		year = release;
		director = guy_who_made_the_film;
		banner = poster_url;
		trailer = trailer_url;
		genres = new ArrayList<Genre>();
		stars = new ArrayList<Star>();
	}

	public Movie(int id, String title, int release, String guy_who_made_the_film, String poster_url, String trailer_url,
			ArrayList<Genre> Genres, ArrayList<Star> Stars) {
		movie_id = id;
		movie_title = title;
		year = release;
		director = guy_who_made_the_film;
		banner = processUrl(poster_url);
		trailer = processUrl(trailer_url);
		genres = Genres;
		stars = Stars;
	}

	public Movie() {
		// TODO Auto-generated constructor stub
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

	public String getBanner() {
		return banner;
	}

	public String getTrailer() {
		return trailer;
	}

	public void addGenre(Genre g) {
		genres.add(g);
	}

	public void addStar(Star s) {
		stars.add(s);
	}

	public int getId() {
		return movie_id;
	}

	public void setId(int id) {
		this.movie_id = id;
	}

	public String getTitle() {
		return movie_title;
	}

	public void setTitle(String title) {
		this.movie_title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int release) {
		year = release;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String guy_who_made_the_film) {
		director = guy_who_made_the_film;
	}

	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<Genre> Genres) {
		genres = Genres;
	}

	public ArrayList<Star> getStars() {
		return stars;
	}

	public void setStars(ArrayList<Star> Stars) {
		stars = Stars;
	}
}
