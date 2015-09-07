package mouseapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class KindOfDatabase {

	private PrintWriter pw;
	private BufferedReader br;
	private static File file = new File("username_db.txt");

	public KindOfDatabase() {
	
	}

	public synchronized void reset() {
		pw.write("");
		pw.flush();
	}

	public synchronized void addNewUserName(String username) throws FileNotFoundException {
		pw = new PrintWriter(file);
		pw.write(username);
		pw.flush();
		pw.close();
	}

	public synchronized String getUserName() throws FileNotFoundException {
		br = new BufferedReader(new FileReader(file));
		String nm = "";
		try {
			nm = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nm;
	}

	

}
