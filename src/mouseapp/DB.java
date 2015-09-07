package mouseapp;

import java.sql.*;

public class DB {

	private static String dbURL = "jdbc:derby://localhost:1527/myDB;create=true;user=me;password=mine";
	private static String tableName = "username";
	// jdbc Connection
	private static Connection conn = null;
	private static Statement stmt = null;

	public static void main(String[] args) {
		createConnection();
		System.out.println(selectRestaurants());
		insertRestaurants(1, "malakaaaa");
		System.out.println(selectRestaurants());
		shutdown();
		
	}

	public DB() {
		createConnection();
	}

	public static void dropTable() {
		try {
			stmt = conn.createStatement();

			String sql = "DROP TABLE " + tableName;

			stmt.executeUpdate(sql);
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	private static void createConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			// Get a connection
			conn = DriverManager.getConnection(dbURL);
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	public static void insertRestaurants(int id, String username) {
		try {
			stmt = conn.createStatement();
			stmt.execute("insert into " + tableName + " values (" + id + ",'"
					+ username + "')");
			stmt.close();
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
	}

	public static void deleteRec() {
		try {
			stmt = conn.createStatement();

			String sql = "DELETE FROM " + tableName + " WHERE id = 1";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String selectRestaurants() {
		String username = "";
		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery("select * from " + tableName);
			ResultSetMetaData rsmd = results.getMetaData();
			int numberCols = rsmd.getColumnCount();
			for (int i = 1; i <= numberCols; i++) {
				// print Column Names
				System.out.print(rsmd.getColumnLabel(i) + "\t\t");
			}

			System.out
					.println("\n-------------------------------------------------");

			while (results.next()) {
				int id = results.getInt(1);
				username = results.getString(2);
				System.out.println(id + "\t\t" + username);
			}
			results.close();
			stmt.close();
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}

		return username;
	}

	private static void shutdown() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				DriverManager.getConnection(dbURL + ";shutdown=true");
				conn.close();
			}
		} catch (SQLException sqlExcept) {

		}

	}

}
