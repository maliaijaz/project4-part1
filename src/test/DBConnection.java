package test;

import java.sql.*;

public class DBConnection {

	private Connection conn;
	private Statement stmt;
	private String url;
	private String login;
	private String password;

	/** Creates a new instance of DBConnection */
	public DBConnection() {
		conn = null;
		stmt = null;
		getConnectionInfo();
	}

	public void getConnectionInfo() {

		this.url = "jdbc:mysql://localhost:3306/moviedb";
		this.login = "root";
		this.password = "poop";
	}

	public Connection getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, login, password);
			System.out.println("Login success");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return conn;
	}
}