package test;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

// Change all of the variable, function and parameter names...

public class Search {

	// Main function for movie search
	// Return all the movies in an ArrayList the match parameters given.
	// The parameters can be input in any combination
	// CHANGE LIKE QUERIES. REFER TO SPIDERMAN EXAMPLE
	public ArrayList<Movie> searchMovies(String movieId, String movieTitle, String movieYear, String movieDirector,
			String movieGenre, String starFirstName, String starLastName, String order, boolean substring,
			Connection connection) throws SQLException {
		String conditionalFragments = " WHERE 1=1";
		String orderStyle = " ORDER BY movies.title ASC";

		conditionalFragments = checkId(conditionalFragments, movieId);
		conditionalFragments = checkTitle(conditionalFragments, movieTitle, substring, 1);
		conditionalFragments = checkYear(conditionalFragments, movieYear);
		conditionalFragments = checkDirector(conditionalFragments, movieDirector, substring);
		conditionalFragments = checkGenre(conditionalFragments, movieGenre, substring);
		conditionalFragments = checkFirstName(conditionalFragments, starFirstName, substring);
		conditionalFragments = checkLastName(conditionalFragments, starLastName, substring);

		orderStyle = checkOrder(order);

		return executeSqlQuery(conditionalFragments, orderStyle, connection);

	}

	// Helper fuction for searchMovies and searchStar
	private static String checkOrder(String order) {
		String orderByCondition = " ORDER BY movies.title ASC";
		if (order != null && !order.isEmpty()) {
			if (order.equals("titleasc")) {
				orderByCondition = " ORDER BY movies.title ASC";
			} else if (order.equals("titledsc")) {
				orderByCondition = " ORDER BY movies.year DESC";
			} else if (order.equals("yearasc")) {
				orderByCondition = " ORDER BY movies.year ASC";
			} else if (order.equals("yeardsc")) {
				orderByCondition = " ORDER BY movies.year DESC";
			}
		}

		return orderByCondition;
	}

	// Helper fucntion for searchMovies and browseMovies.
	// No need to touch this
	private static ArrayList<Movie> executeSqlQuery(String conditionalFragments, String orderStyle,
			Connection connection) throws SQLException {
		String sqlStatement = "SELECT movies.id, movies.title, movies.year, movies.director, movies.banner_url, movies.trailer_url, genres.id, genres.name, stars.id, stars.first_name, stars.last_name, stars.dob, stars.photo_url FROM movies";
		sqlStatement += " INNER JOIN stars_in_movies ON stars_in_movies.movie_id = movies.id ";
		sqlStatement += " INNER JOIN stars ON stars_in_movies.star_id = stars.id ";
		sqlStatement += " INNER JOIN genres_in_movies ON genres_in_movies.movie_id = movies.id ";
		sqlStatement += " INNER JOIN genres ON genres.id = genres_in_movies.genre_id ";
		sqlStatement += conditionalFragments;
		sqlStatement += orderStyle;

		Statement search = connection.createStatement();
		ResultSet set = search.executeQuery(sqlStatement);

		HashMap<Integer, Movie> movieMap = new HashMap<Integer, Movie>();

		while (set.next()) {
			String dob = set.getDate(12).toString();
			Star star = new Star(set.getInt(9), set.getString(10), set.getString(11), dob, set.getString(13));
			Genre genre = new Genre(set.getInt(7), set.getString(8));
			if (movieMap.containsKey(set.getInt(1))) {
				Movie movie = movieMap.get(set.getInt(1));

				movie = addStars(movie.getStars(), star, movie);

				movie = addGenres(movie.getGenres(), genre, movie);

				movieMap.put(movie.getId(), movie);
			} else {
				ArrayList<Genre> genres = new ArrayList<Genre>();
				genres.add(genre);

				ArrayList<Star> stars = new ArrayList<Star>();
				stars.add(star);
				Movie movie = new Movie(set.getInt(1), set.getString(2), set.getInt(3), set.getString(4),
						set.getString(5), set.getString(6), genres, stars);
				movieMap.put(movie.getId(), movie);
			}
		}

		ArrayList<Movie> movies = new ArrayList<Movie>(movieMap.values());

		return movies;
	}

	private static Movie addStars(ArrayList<Star> stars, Star star, Movie movie) {
		Movie m = movie;
		boolean toAdd = true;
		for (Star s : stars) {
			if (s.getId() == star.getId()) {
				toAdd = false;
			}
		}
		if (toAdd) {
			m.addStar(star);
		}
		return m;
	}

	private static Movie addGenres(ArrayList<Genre> genres, Genre genre, Movie movie) {
		Movie m = movie;
		boolean toAdd = true;
		for (Genre g : genres) {
			if (g.getId() == genre.getId()) {
				toAdd = false;
			}
		}
		if (toAdd) {
			m.addGenre(genre);
		}
		return m;
	}

	// If login details are invalid in anyway, the object returned will be NULL
	// If login details are correct, object will have a value;
	public static Customer verifyLoginAccount(String email, String password, Connection connection)
			throws SQLException {
		String sqlStatement = "SELECT id, first_name, last_name, cc_id, address, email, password FROM customers WHERE email = ? AND password = ?";

		PreparedStatement checkStatement = connection.prepareStatement(sqlStatement);
		checkStatement.setString(1, email);
		checkStatement.setString(2, password);

		ResultSet set = checkStatement.executeQuery();

		Customer validCustomer = null;

		if (set.next()) {
			validCustomer = new Customer(set.getInt(1), set.getString(2), set.getString(3), set.getString(4),
					set.getString(5), set.getString(6), set.getString(7));
		}

		return validCustomer;
	}

	// browseMovies is called for either Browse by Title or Browse by genre.
	// Simple leave the other feild as ""
	// if browsing genres leave title as ""
	public static ArrayList<Movie> browseMovies(String movieTitle, String movieGenre, String order,
			Connection connection) throws SQLException {
		String conditionalFragments = " WHERE 1=1";
		String orderStyle = " ORDER BY movies.title ASC";

		conditionalFragments += checkTitle(conditionalFragments, movieTitle, true, 2);
		conditionalFragments += checkGenre(conditionalFragments, movieGenre, true);

		orderStyle = checkOrder(order);

		return executeSqlQuery(orderStyle, orderStyle, connection);
	}

	// When traveling Actor's Info page aka SingleActor in the diagram.
	// Application has to call this function to populate the feilds
	public static Star searchStar(String starId, Connection connection) throws SQLException {
		String sqlStatement = "SELECT movies.id, movies.title, movies.year, stars.id, stars.first_name, stars.last_name, stars.dob, stars.photo_url FROM stars";
		sqlStatement += " INNER JOIN stars_in_movies ON stars_in_movies.star_id=stars.id";
		sqlStatement += " INNER JOIN movies ON stars_in_movies.movie_id=movies.id";
		sqlStatement += " WHERE stars.id = ?";

		PreparedStatement checkStatement = connection.prepareStatement(sqlStatement);
		checkStatement.setString(1, starId);

		ResultSet set = checkStatement.executeQuery();
		set.next();

		String dob = set.getDate(7).toString();

		Star star = new Star(set.getInt(4), set.getString(5), set.getString(6), dob, set.getString(8));
		star.addMovie(new Movie(set.getInt(1), set.getString(2), set.getInt(3), "", "", ""));

		while (set.next()) {
			star.addMovie(new Movie(set.getInt(1), set.getString(2), set.getInt(3), "", "", ""));
		}

		return star;
	}

	public static int validCard(String card_num, String first_name, String last_name, String exp, Connection connection)
			throws SQLException {
		String sqlStatement = "SELECT COUNT(*) FROM creditcards WHERE id=? AND first_name=? AND last_name=? AND expiration=?";

		PreparedStatement checkStatement = connection.prepareStatement(sqlStatement);
		checkStatement.setString(1, card_num);
		checkStatement.setString(2, first_name);
		checkStatement.setString(3, last_name);
		checkStatement.setString(4, exp);

		ResultSet set = checkStatement.executeQuery();
		set.next();

		return set.getInt(1);

	}

	//
	// public static void insertSales(SessionCart cart, Customer customer,
	// Connection connection) throws SQLException{
	// for(ItemInCart item: cart.getCartItems_Array()){
	// String sqlStatement = "INSERT INTO sales(customer_id, movie_id,
	// sale_date) VALUES(?, ?, ?)";
	//
	// PreparedStatement preparedStatement =
	// connection.prepareStatement(sqlStatement);
	//
	// Date date = new Date();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
	// String date_format = sdf.format(date).replace('.', '-');;
	//
	// preparedStatement.setInt(1, customer.getId());
	// preparedStatement.setInt(2, item.getMovie().getId());
	// preparedStatement.setString(3, date_format);
	//
	// for (int i = 0; i < item.getQuantity(); i++)
	// {
	// preparedStatement.executeUpdate();
	// }
	// }
	// }

	// HELPER METHOD FOR USE IN BrowseController.java

	public static ArrayList<String> getGenreNames(Connection connection) throws SQLException {
		ArrayList<String> genreNames = new ArrayList<String>();

		String sqlStatement = "SELECT genres.name FROM genres";

		Statement search = connection.createStatement();
		ResultSet set = search.executeQuery(sqlStatement);
		while (set.next()) {
			genreNames.add(set.getString(1));
		}
		return genreNames;
	}

	private static String checkId(String conditionalFragments, String id) {
		String returnString = conditionalFragments;
		if (id.isEmpty()) {

		} else {
			returnString = conditionalFragments + " AND movies.id=\"" + id + "\"";
		}
		return returnString;
	}

	private static String checkTitle(String conditionalFragments, String title, boolean substring, int version) {
		String returnString = conditionalFragments;
		if (title.isEmpty()) {

		} else {
			if (substring) {
				// if(version==1){
				returnString = conditionalFragments + " AND movies.title LIKE \"%" + title + "%\"";
				// }
				// else{
				// returnString = conditionalFragments + " AND movies.title LIKE
				// \"" + title + "%\"";
				// }
			} else {
				returnString = conditionalFragments + " AND movies.title LIKE \"" + title + "\"";
			}
		}
		return returnString;
	}

	private static String checkYear(String conditionalFragments, String year) {
		String returnString = conditionalFragments;
		if (year.isEmpty()) {

		} else {
			returnString = conditionalFragments + " AND movies.year LIKE \"" + year + "\"";
		}
		return returnString;
	}

	private static String checkDirector(String conditionalFragments, String director, boolean substring) {
		String returnString = conditionalFragments;
		if (director.isEmpty()) {

		} else {
			if (substring) {
				returnString = conditionalFragments + " AND movies.director LIKE \"%" + director + "%\"";
			} else {
				returnString = conditionalFragments + " AND movies.director = \"" + director + "\"";
			}
		}
		return returnString;

	}

	private static String checkGenre(String conditionalFragments, String Genre, boolean substring) {
		String returnString = conditionalFragments;
		if (Genre.isEmpty()) {

		} else {
			if (substring) {
				returnString = conditionalFragments + " AND genres.name LIKE \"%" + Genre + "%\"";
			} else {
				returnString = conditionalFragments + " AND genres.name = \"" + Genre + "\"";
			}
		}
		return returnString;
	}

	private static String checkFirstName(String conditionalFragments, String first, boolean substring) {
		String returnString = conditionalFragments;
		if (first.isEmpty()) {

		} else {
			if (substring) {
				returnString = conditionalFragments + " AND stars.first_name LIKE \"%" + first + "%\"";
			} else {
				returnString = conditionalFragments + " AND stars.first_name = \"" + first + "\"";
			}
		}
		return returnString;
	}

	private static String checkLastName(String conditionalFragments, String last, boolean substring) {
		String returnString = conditionalFragments;
		if (last.isEmpty()) {

		} else {
			if (substring) {
				returnString = conditionalFragments + " AND stars.last_name LIKE \"%" + last + "%\"";
			} else {
				returnString = conditionalFragments + " AND stars.last_name = \"" + last + "\"";
			}
		}
		return returnString;
	}

	public static ArrayList<Movie> fastSearchMovies(ArrayList<String> titleTokens, Connection connection)
			throws SQLException {
		String whereConditions = " WHERE ";
		String orderByCondition = " ORDER BY movies.title ASC";
		// SELECT title FROM movies WHERE MATCH (title) AGAINST ('+revenge star
		// of' IN BOOLEAN MODE);
		for (String token : titleTokens) {
			if (!token.equals("") && !token.contains(" ")) {
				if (titleTokens.indexOf(token) == titleTokens.size() - 1) {
					whereConditions += "MATCH (movies.title) AGAINST ('" + token + "*' IN BOOLEAN MODE)";
				} else {
					whereConditions += "MATCH (movies.title) AGAINST ('" + token + "' IN BOOLEAN MODE) AND ";
				}
			}
		}

		String sqlStatement = "SELECT movies.id, movies.title, movies.year, movies.director, movies.banner_url, movies.trailer_url FROM movies "
				+ whereConditions + orderByCondition;
		Statement searchStatement = connection.createStatement();
		ResultSet resultSet = searchStatement.executeQuery(sqlStatement);

		ArrayList<Movie> movies = new ArrayList<Movie>();

		while (resultSet.next()) {
			Movie movie = new Movie(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3),
					resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
			movies.add(movie);
		}

		return movies;
	}

}

//// package mvcdemo.model;
////
//// import java.sql.Connection;
//// import java.util.Date;
//// import java.sql.PreparedStatement;
//// import java.sql.ResultSet;
//// import java.sql.SQLException;
//// import java.sql.Statement;
//// import java.text.SimpleDateFormat;
//// import java.util.ArrayList;
//// import java.util.HashMap;
////
////// Change all of the variable, function and parameter names...
////
//// public class Search{
////
//// //Main function for movie search
//// //Return all the movies in an ArrayList the match parameters given.
//// //The parameters can be input in any combination
//// // CHANGE LIKE QUERIES. REFER TO SPIDERMAN EXAMPLE
//// public ArrayList<Movie> searchMovies(String movieId, String movieTitle,
//// String movieYear, String movieDirector, String movieGenre,
//// String starFirstName, String starLastName, String order, boolean substring,
//// Connection connection) throws SQLException
//// {
//// String conditionalFragments = " WHERE 1=1";
//// String orderStyle = " ORDER BY movies.title ASC";
////
//// conditionalFragments+=checkId(conditionalFragments,movieId);
//// conditionalFragments+=checkTitle(conditionalFragments,movieTitle,substring,1);
//// conditionalFragments+=checkYear(conditionalFragments,movieYear);
//// conditionalFragments+=checkDirector(conditionalFragments,movieDirector,substring);
//// conditionalFragments+=checkGenre(conditionalFragments,movieGenre,substring);
//// conditionalFragments+=checkFirstName(conditionalFragments,starFirstName,substring);
//// conditionalFragments+=checkLastName(conditionalFragments,starLastName,substring);
////
////
//// orderStyle=checkOrder(order);
////
//// return executeSqlQuery(conditionalFragments, orderStyle, connection);
////
//// }
//// //Helper fuction for searchMovies and searchStar
//// private static String checkOrder(String order){
//// String orderByCondition = " ORDER BY movies.title ASC";
//// if (order != null && !order.isEmpty())
//// {
//// if (order.equals("titleasc"))
//// {
//// orderByCondition = " ORDER BY movies.title ASC";
//// }
//// else if (order.equals("titledsc"))
//// {
//// orderByCondition = " ORDER BY movies.year DESC";
//// }
//// else if (order.equals("yearasc"))
//// {
//// orderByCondition = " ORDER BY movies.year ASC";
//// }
//// else if (order.equals("yeardsc"))
//// {
//// orderByCondition = " ORDER BY movies.year DESC";
//// }
//// }
////
//// return orderByCondition;
//// }
//// //Helper fucntion for searchMovies and browseMovies.
//// //No need to touch this
//// private static ArrayList<Movie> executeSqlQuery(String
//// conditionalFragments, String orderStyle, Connection connection) throws
//// SQLException
//// {
//// String sqlStatement = "SELECT movies.id, movies.title, movies.year,
//// movies.director, movies.banner_url, movies.trailer_url, genres.id,
//// genres.name, stars.id, stars.first_name, stars.last_name, stars.dob,
//// stars.photo_url FROM movies";
//// sqlStatement+= " INNER JOIN stars_in_movies ON stars_in_movies.movie_id =
//// movies.id ";
//// sqlStatement+= " INNER JOIN stars ON stars_in_movies.star_id = stars.id ";
//// sqlStatement+= " INNER JOIN genres_in_movies ON genres_in_movies.movie_id =
//// movies.id ";
//// sqlStatement+= " INNER JOIN genres ON genres.id = genres_in_movies.genre_id
//// ";
//// sqlStatement+= conditionalFragments;
//// sqlStatement+= orderStyle;
////
//// Statement search = connection.createStatement();
//// ResultSet set = search.executeQuery(sqlStatement);
////
//// HashMap<Integer, Movie> movieMap = new HashMap<Integer, Movie>();
////
//// while (set.next())
//// {
//// String dob=set.getDate(12).toString();
//// Star star = new Star(set.getInt(9), set.getString(10), set.getString(11),
//// dob, set.getString(13));
//// Genre genre = new Genre(set.getInt(7), set.getString(8));
//// if (movieMap.containsKey(set.getInt(1)))
//// {
//// Movie movie = movieMap.get(set.getInt(1));
////
//// movie=addStars(movie.getStars(),star,movie);
////
//// movie=addGenres(movie.getGenres(),genre,movie);
////
//// movieMap.put(movie.getId(), movie);
//// }
//// else
//// {
//// ArrayList<Genre> genres = new ArrayList<Genre>();
//// genres.add(genre);
////
//// ArrayList<Star> stars = new ArrayList<Star>();
//// stars.add(star);
//// Movie movie = new Movie(set.getInt(1), set.getString(2), set.getInt(3),
//// set.getString(4), set.getString(5), set.getString(6), genres, stars);
//// movieMap.put(movie.getId(), movie);
//// }
//// }
////
//// ArrayList<Movie> movies = new ArrayList<Movie>(movieMap.values());
////
//// return movies;
//// }
////
//// private static Movie addStars(ArrayList<Star> stars, Star star, Movie
//// movie){
//// Movie m=movie;
//// boolean toAdd=true;
//// for (Star s : stars)
//// {
//// if (s.getId() == star.getId())
//// {
//// toAdd=false;
//// }
//// }
//// if(toAdd){
//// m.addStar(star);
//// }
//// return m;
//// }
//// private static Movie addGenres(ArrayList<Genre> genres, Genre genre, Movie
//// movie){
//// Movie m=movie;
//// boolean toAdd=true;
//// for (Genre g : genres)
//// {
//// if (g.getId() == genre.getId())
//// {
//// toAdd=false;
//// }
//// }
//// if(toAdd){
//// m.addGenre(genre);
//// }
//// return m;
//// }
////
//// //If login details are invalid in anyway, the object returned will be NULL
//// //If login details are correct, object will have a value;
//// public static Customer verifyLoginAccount(String email, String password,
//// Connection connection) throws SQLException
//// {
//// String sqlStatement = "SELECT id, first_name, last_name, cc_id, address,
//// email, password FROM customers WHERE email = ? AND password = ?";
////
//// PreparedStatement checkStatement =
//// connection.prepareStatement(sqlStatement);
//// checkStatement.setString(1, email);
//// checkStatement.setString(2, password);
////
//// ResultSet set = checkStatement.executeQuery();
////
//// Customer validCustomer = null;
////
//// if (set.next())
//// {
//// validCustomer = new Customer(set.getInt(1), set.getString(2),
//// set.getString(3), set.getString(4), set.getString(5), set.getString(6),
//// set.getString(7));
//// }
////
//// return validCustomer;
//// }
////
//// //browseMovies is called for either Browse by Title or Browse by genre.
//// Simple leave the other feild as ""
//// //if browsing genres leave title as ""
//// public static ArrayList<Movie> browseMovies(String movieTitle, String
//// movieGenre, String order, Connection connection) throws SQLException
//// {
//// String conditionalFragments = " WHERE 1=1";
//// String orderStyle = " ORDER BY movies.title ASC";
////
//// conditionalFragments+=checkTitle(conditionalFragments,movieTitle,true,2);
//// conditionalFragments+=checkGenre(conditionalFragments,movieGenre,true);
////
//// orderStyle=checkOrder(order);
////
//// return executeSqlQuery(orderStyle, orderStyle, connection);
//// }
////
//// //When traveling Actor's Info page aka SingleActor in the diagram.
//// Application has to call this function to populate the feilds
//// public static Star searchStar(String starId, Connection connection) throws
//// SQLException
//// {
//// String sqlStatement = "SELECT movies.id, movies.title, movies.year,
//// stars.id, stars.first_name, stars.last_name, stars.dob, stars.photo_url
//// FROM stars";
//// sqlStatement+= " INNER JOIN stars_in_movies ON
//// stars_in_movies.star_id=stars.id";
//// sqlStatement+= " INNER JOIN movies ON stars_in_movies.movie_id=movies.id";
//// sqlStatement+= " WHERE stars.id = ?";
////
//// PreparedStatement checkStatement =
//// connection.prepareStatement(sqlStatement);
//// checkStatement.setString(1, starId);
////
//// ResultSet set = checkStatement.executeQuery();
//// set.next();
////
//// String dob=set.getDate(7).toString();
////
//// Star star = new Star(set.getInt(4), set.getString(5), set.getString(6),
//// dob, set.getString(8));
//// star.addMovie(new Movie(set.getInt(1), set.getString(2), set.getInt(3), "",
//// "", ""));
////
//// while (set.next())
//// {
//// star.addMovie(new Movie(set.getInt(1), set.getString(2), set.getInt(3), "",
//// "", ""));
//// }
////
//// return star;
//// }
////
////
////
//// public static int validCard(String card_num, String first_name, String
//// last_name, String exp, Connection connection) throws SQLException{
//// String sqlStatement = "SELECT COUNT(*) FROM creditcards WHERE id=? AND
//// first_name=? AND last_name=? AND expiration=?";
////
//// PreparedStatement checkStatement =
//// connection.prepareStatement(sqlStatement);
//// checkStatement.setString(1, card_num);
//// checkStatement.setString(2, first_name);
//// checkStatement.setString(3, last_name);
//// checkStatement.setString(4, exp);
////
//// ResultSet set = checkStatement.executeQuery();
//// set.next();
////
//// return set.getInt(1);
////
//// }
////
////
//// public static void insertSales(SessionCart cart, Customer customer,
//// Connection connection) throws SQLException{
//// for(CartItem item: cart.getCartItems_Array()){
//// String sqlStatement = "INSERT INTO sales(customer_id, movie_id, sale_date)
//// VALUES(?, ?, ?)";
////
//// PreparedStatement preparedStatement =
//// connection.prepareStatement(sqlStatement);
////
//// Date date = new Date();
//// SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
//// String date_format = sdf.format(date).replace('.', '-');;
////
//// preparedStatement.setInt(1, customer.getId());
//// preparedStatement.setInt(2, item.getMovie().getId());
//// preparedStatement.setString(3, date_format);
////
//// for (int i = 0; i < item.getQuantity(); i++)
//// {
//// preparedStatement.executeUpdate();
//// }
//// }
//// }
////
////
////// HELPER METHOD FOR USE IN BrowseController.java
////
//// public static ArrayList<String> getGenreNames(Connection connection) throws
//// SQLException {
//// ArrayList<String> genreNames = new ArrayList<String>();
////
//// String sqlStatement = "SELECT genres.name FROM genres";
////
////
//// Statement search = connection.createStatement();
//// ResultSet set = search.executeQuery(sqlStatement);
//// while(set.next()){
//// genreNames.add(set.getString(1));
//// }
//// return genreNames;
//// }
////
//// private static String checkId(String conditionalFragments, String id){
//// String returnString= "";
//// if(id.isEmpty()){
////
//// }
//// else{
//// returnString = conditionalFragments + " AND movies.id=\"" + id + "\"";
//// }
//// return returnString;
//// }
////
//// private static String checkTitle(String conditionalFragments, String title,
//// boolean substring,int version){
//// String returnString= "";
//// if(title.isEmpty()){
////
//// }
//// else{
//// if(substring){
//// if(version==1){
//// returnString = conditionalFragments + " AND movies.title LIKE \"%" + title
//// + "%\"";
//// }
//// else{
//// returnString = conditionalFragments + " AND movies.title LIKE \"" + title +
//// "%\"";
//// }
//// }
//// else{
//// returnString = conditionalFragments + " AND movies.title LIKE \"" + title +
//// "\"";
//// }
//// }
//// return returnString;
//// }
//// private static String checkYear(String conditionalFragments, String year){
//// String returnString= "";
//// if(year.isEmpty()){
////
//// }
//// else{
//// returnString = conditionalFragments + " AND movies.year LIKE \"" + year +
//// "\"";
//// }
//// return returnString;
//// }
//// private static String checkDirector(String conditionalFragments, String
//// director, boolean substring){
//// String returnString= "";
//// if(director.isEmpty()){
////
//// }
//// else{
//// if(substring){
//// returnString = conditionalFragments + " AND movies.director LIKE \"%" +
//// director + "%\"";
//// }
//// else{
//// returnString = conditionalFragments + " AND movies.director = \"" +
//// director + "\"";
//// }
//// }
//// return returnString;
////
//// }
//// private static String checkGenre(String conditionalFragments, String Genre,
//// boolean substring){
//// String returnString= "";
//// if(Genre.isEmpty()){
////
//// }
//// else{
//// if(substring){
//// returnString = conditionalFragments + " AND genres.name LIKE \"%" + Genre +
//// "%\"";
//// }
//// else{
//// returnString = conditionalFragments + " AND genres.name = \"" + Genre +
//// "\"";
//// }
//// }
//// return returnString;
//// }
//// private static String checkFirstName(String conditionalFragments, String
//// first, boolean substring){
//// String returnString= "";
//// if(first.isEmpty()){
////
//// }
//// else{
//// if(substring){
//// returnString = conditionalFragments + " AND stars.first_name LIKE \"%" +
//// first + "%\"";
//// }
//// else{
//// returnString = conditionalFragments + " AND stars.first_name = \"" + first
//// + "\"";
//// }
//// }
//// return returnString;
//// }
//// private static String checkLastName(String conditionalFragments, String
//// last, boolean substring){
//// String returnString= "";
//// if(last.isEmpty()){
////
//// }
//// else{
//// if(substring){
//// returnString = conditionalFragments + " AND stars.last_name LIKE \"%" +
//// last + "%\"";
//// }
//// else{
//// returnString = conditionalFragments + " AND stars.last_name = \"" + last +
//// "\"";
//// }
//// }
//// return returnString;
//// }
////
////
//// }
//
// package mvcdemo.model;
//
// import java.sql.Connection;
// import java.util.Date;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.HashMap;
//
//// Change all of the variable, function and parameter names...
//
// public class Search{
//
// //Main function for movie search
// //Return all the movies in an ArrayList the match parameters given.
// //The parameters can be input in any combination
//
// // CHANGE LIKE QUERIES. REFER TO SPIDERMAN EXAMPLE
// public static ArrayList<Movie> searchMovies(String movieId, String
//// movieTitle, String movieYear, String movieDirector, String movieGenre,
// String starFirstName, String starLastName, String order, boolean sub_match,
//// Connection connection) throws SQLException
// {
// String whereConditions = " WHERE 1=1";
// String orderByCondition = " ORDER BY movies.title ASC";
//
// if (movieId != null && !movieId.isEmpty())
// {
// whereConditions = whereConditions + " AND movies.id=\"" + movieId + "\"";
// }
//
// if (movieTitle != null && !movieTitle.isEmpty())
// {
// if (sub_match)
// {
// whereConditions = whereConditions + " AND movies.title LIKE \"%" + movieTitle
//// + "%\"";
// }
// else
// {
// whereConditions = whereConditions +" AND movies.title LIKE \"" + movieTitle +
//// "\"";
// }
// }
//
// if (movieYear != null && !movieYear.isEmpty())
// {
// whereConditions = whereConditions + " AND movies.year LIKE \"" + movieYear +
//// "\"";
// }
//
// if (movieDirector != null && !movieDirector.isEmpty())
// {
// if (!sub_match)
// {
// whereConditions = whereConditions + " AND movies.director = \"" +
//// movieDirector + "\"";
// }
// else
// {
// whereConditions = whereConditions + " AND movies.director LIKE \"%" +
//// movieDirector + "%\"";
// }
// }
//
// if (movieGenre != null && !movieGenre.isEmpty())
// {
// if (!sub_match)
// {
// whereConditions = whereConditions + " AND genres.name = \"" + movieGenre +
//// "\"";
// }
// else
// {
// whereConditions = whereConditions + " AND genres.name LIKE \"%" + movieGenre
//// + "%\"";
// }
// }
//
// if (starFirstName != null && !starFirstName.isEmpty())
// {
// if (!sub_match)
// {
// whereConditions = whereConditions + " AND stars.first_name = \"" +
//// starFirstName + "\"";
// }
// else
// {
// whereConditions = whereConditions + " AND stars.first_name LIKE \"%" +
//// starFirstName + "%\"";
// }
// }
//
// if (starLastName != null && !starLastName.isEmpty())
// {
// if (!sub_match)
// {
// whereConditions = whereConditions + " AND stars.last_name = \"" +
//// starLastName + "\"";
// }
// else
// {
// whereConditions = whereConditions + " AND stars.last_name LIKE \"%" +
//// starLastName + "%\"";
// }
// }
//
// orderByCondition=Order(order);
//
// return executeMoviesFetch(whereConditions, orderByCondition, connection);
//
// }
// //Helper fuction for searchMovies and searchStar
// private static String Order(String order){
// String orderByCondition = " ORDER BY movies.title ASC";
// if (order != null && !order.isEmpty())
// {
// if (order.equals("titleasc"))
// {
// orderByCondition = " ORDER BY movies.title ASC";
// }
// else if (order.equals("titledsc"))
// {
// orderByCondition = " ORDER BY movies.year DESC";
// }
// else if (order.equals("yearasc"))
// {
// orderByCondition = " ORDER BY movies.year ASC";
// }
// else if (order.equals("yeardsc"))
// {
// orderByCondition = " ORDER BY movies.year DESC";
// }
// }
//
// return orderByCondition;
// }
// //Helper fucntion for searchMovies and browseMovies.
// //No need to touch this
// private static ArrayList<Movie> executeMoviesFetch(String whereConditions,
//// String orderByCondition, Connection connection) throws SQLException
// {
// String sqlStatement = "SELECT stars.id, stars.first_name, stars.last_name,
//// stars.dob, stars.photo_url, movies.id, movies.title, movies.year,
//// movies.director, movies.banner_url, movies.trailer_url, "
// + "genres.id, genres.name FROM movies "
// + "INNER JOIN stars_in_movies ON stars_in_movies.movie_id = movies.id "
// + "INNER JOIN stars ON stars_in_movies.star_id = stars.id "
// + "INNER JOIN genres_in_movies ON genres_in_movies.movie_id = movies.id "
// + "INNER JOIN genres ON genres.id = genres_in_movies.genre_id "
// + whereConditions
// + orderByCondition;
//
// Statement searchStatement = connection.createStatement();
// ResultSet resultSet = searchStatement.executeQuery(sqlStatement);
//
// HashMap<Integer, Movie> movieMap = new HashMap<Integer, Movie>();
//
// while (resultSet.next())
// {
// String dob=resultSet.getDate(4).toString();
// Star star = new Star(resultSet.getInt(1), resultSet.getString(2),
//// resultSet.getString(3), dob, resultSet.getString(5));
// Genre genre = new Genre(resultSet.getInt(12), resultSet.getString(13));
//
// if (movieMap.containsKey(resultSet.getInt(6)))
// {
// Movie movie = movieMap.get(resultSet.getInt(6));
// boolean addStar = true;
// boolean addGenre = true;
//
// for (Star existingStar : movie.getStars())
// {
// if (existingStar.getId() == star.getId())
// {
// addStar = false;
// }
// }
//
// if (addStar)
// {
// movie.addStar(star);
// }
//
// for (Genre existingGenre : movie.getGenres())
// {
// if (existingGenre.getId() == genre.getId())
// {
// addGenre = false;
// }
// }
//
// if (addGenre)
// {
// movie.addGenre(genre);
// }
//
// movieMap.put(movie.getId(), movie);
// }
// else
// {
// ArrayList<Genre> genres = new ArrayList<Genre>();
// genres.add(genre);
//
// ArrayList<Star> stars = new ArrayList<Star>();
// stars.add(star);
//
// Movie movie = new Movie(resultSet.getInt(6), resultSet.getString(7),
//// resultSet.getInt(8), resultSet.getString(9), resultSet.getString(10),
//// resultSet.getString(11), genres, stars);
// movieMap.put(movie.getId(), movie);
// }
// }
//
// ArrayList<Movie> movies = new ArrayList<Movie>(movieMap.values());
//
// return movies;
// }
//
// //If login details are invalid in anyway, the object returned will be NULL
// //If login details are correct, object will have a value;
// public static Customer verifyLoginAccount(String email, String password,
//// Connection connection) throws SQLException
// {
// String sql = "SELECT id, first_name, last_name, cc_id, address, email,
//// password FROM customers WHERE email = ? AND password = ?";
//
// PreparedStatement verifyStatement = connection.prepareStatement(sql);
// verifyStatement.setString(1, email);
// verifyStatement.setString(2, password);
//
// ResultSet result = verifyStatement.executeQuery();
//
// Customer validCustomer = null;
//
// if (result.next())
// {
// validCustomer = new Customer(result.getInt(1), result.getString(2),
//// result.getString(3), result.getString(4), result.getString(5),
//// result.getString(6), result.getString(7));
// }
//
// return validCustomer;
// }
//
// //browseMovies is called for either Browse by Title or Browse by genre.
//// Simple leave the other feild as ""
// //if browsing genres leave title as ""
// public ArrayList<Movie> browseMovies(String movieTitle, String movieGenre,
//// String order, Connection connection) throws SQLException
// {
// String whereConditions = " WHERE 1=1";
// String orderByCondition = " ORDER BY movies.title ASC";
//
// if (movieTitle != null && !movieTitle.isEmpty())
// {
// whereConditions += " AND movies.title LIKE \"" + movieTitle + "%\"";
// }
//
// if (movieGenre != null && !movieGenre.isEmpty())
// {
// whereConditions += " AND genres.name LIKE \"%" + movieGenre + "%\"";
// }
//
// orderByCondition=Order(order);
//
// return executeMoviesFetch(whereConditions, orderByCondition, connection);
// }
//
// //When traveling Actor's Info page aka SingleActor in the diagram.
//// Application has to call this function to populate the feilds
// public static Star searchStar(String starId, Connection connection) throws
//// SQLException
// {
// String sqlStatement = "SELECT movies.id, movies.title, movies.year, stars.id,
//// stars.first_name, stars.last_name, stars.dob, stars.photo_url FROM stars"
// + " INNER JOIN stars_in_movies ON stars_in_movies.star_id=stars.id"
// + " INNER JOIN movies ON stars_in_movies.movie_id=movies.id"
// + " WHERE stars.id = ?";
//
// PreparedStatement preparedStatement =
//// connection.prepareStatement(sqlStatement);
// preparedStatement.setString(1, starId);
//
// ResultSet resultSet = preparedStatement.executeQuery();
// resultSet.next();
// String dob=resultSet.getDate(7).toString();
// Star star = new Star(resultSet.getInt(4), resultSet.getString(5),
//// resultSet.getString(6), dob, resultSet.getString(8));
// star.addMovie(new Movie(resultSet.getInt(1), resultSet.getString(2),
//// resultSet.getInt(3), "", "", ""));
//
// while (resultSet.next())
// {
// star.addMovie(new Movie(resultSet.getInt(1), resultSet.getString(2),
//// resultSet.getInt(3), "", "", ""));
// }
//
// return star;
// }
//
//
//
// public static int validCard(String card_num, String first_name, String
//// last_name, String exp, Connection connection) throws SQLException{
// String sql = "SELECT COUNT(*) FROM creditcards WHERE id=? AND first_name=?
//// AND last_name=? AND expiration=?";
// PreparedStatement preparedStatement = connection.prepareStatement(sql);
// preparedStatement.setString(1, card_num);
// preparedStatement.setString(2, first_name);
// preparedStatement.setString(3, last_name);
// preparedStatement.setString(4, exp);
//
// ResultSet rs = preparedStatement.executeQuery();
// rs.next();
//
// return rs.getInt(1);
//
// }
//
//
// public static void insertSales(SessionCart cart, Customer customer,
//// Connection connection) throws SQLException{
// for(ItemInCart item: cart.getCartItems_Array()){
// String sql = "INSERT INTO sales(customer_id, movie_id, sale_date) VALUES(?,
//// ?, ?)";
//
// PreparedStatement preparedStatement = connection.prepareStatement(sql);
//
// Date date = new Date();
// SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
// String date_format = sdf.format(date).replace('.', '-');;
//
// preparedStatement.setInt(1, customer.getId());
// preparedStatement.setInt(2, item.getMovie().getId());
// preparedStatement.setString(3, date_format);
//
// for (int i = 0; i < item.getQuantity(); i++)
// {
// preparedStatement.executeUpdate();
// }
// }
// }
//
//
//// HELPER METHOD FOR USE IN BrowseController.java
//
// public static void getGenreNames(Connection connection) throws SQLException {
//
// }
//
//
//
// }