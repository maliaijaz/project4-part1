package test;

import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class DBProc {
	private Connection conn;
	private Statement stmt;

	public DBProc() {
		DBConnection dbc = new DBConnection();
		conn = (Connection) dbc.getConnection();
		try {
			stmt = (Statement) conn.createStatement();
			System.out.println("Got connection");
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		}
	}

	public Movie getMovie(ResultSet rs) {
		Movie mv = new Movie();

		try {
			mv.setId(rs.getInt("id"));
			mv.setTitle(rs.getString("title"));
			mv.setYear(rs.getInt("year"));
			mv.setDirector(rs.getString("director"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mv;
	}

	public ArrayList<Movie> getMoviesByTitleForAjax(String title) {
		ArrayList<Movie> list = new ArrayList<Movie>();
		String query = "select * from movies WHERE title LIKE \"" + "%" + title + "%" + "\"";

		System.out.println("Query is: " + query);
		try {
			ResultSet rs = (ResultSet) stmt.executeQuery(query);
			while (rs.next()) {
				list.add(getMovie(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

}
