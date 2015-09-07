package mouseapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CMD {

	/**
	 * Returns null if it failed for some reason.
	 */
	public static ArrayList<String> command(final String cmdline,
			final String directory) {
		try {
			Process process = new ProcessBuilder(new String[] { "bash", "-c",
					cmdline }).redirectErrorStream(true)
					.directory(new File(directory)).start();

			ArrayList<String> output = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null)
				output.add(line);

			// There should really be a timeout here.
			if (0 != process.waitFor())
				return null;

			return output;

		} catch (Exception e) {
			// Warning: doing this is no good in high quality applications.
			// Instead, present appropriate error messages to the user.
			// But it's perfectly fine for prototyping.

			return null;
		}
	}

	static final Path currentRelativePath = Paths.get("");
	static String directory = currentRelativePath.toAbsolutePath().toString();

	static ArrayList<String> Excecute(String cmdline) {

		if (cmdline.contains("cd")) {
			
			cmdline = cmdline.replace("cd", "");
			if (cmdline.equalsIgnoreCase(" ..") || cmdline.equals("..")) {
				String[] dir = directory.split("/");
				
			//	System.out.println(dir.length);
				if(dir.length<=1){
				ArrayList<String>	ma= new ArrayList<String>();
				ma.add("\n\n\t\tCOMMAND FAILED: " + cmdline);
					return ma;
				}
			String	bckdirectory = "";
				for (int i = 0; i < dir.length - 1; i++) {
					bckdirectory += dir[i]+"/";
				}
				File f= new File(bckdirectory);
				if(f.exists()){
					directory=f.getAbsolutePath();
				}
			//	System.out.println(directory + " cd.. ");
			} else {
				if(cmdline.startsWith(" ")){
					cmdline=	cmdline.substring(1, cmdline.length());
				}
				File f2 = new File(cmdline);
				if(f2.exists()){
					directory = f2.getAbsolutePath();
				}else{
				File f = new File(directory+"/"+cmdline);
				if (f.exists()) {

					directory = f.getAbsolutePath();
				//	System.out.println(directory + " cd ");
				}else{
					ArrayList<String> list= new ArrayList<String>();
					list.add("Directory does not exists");
					return list;
				}
				}

			}
			ArrayList<String> output = command("pwd", directory);
			return output;
		} else {
			ArrayList<String> output = command(cmdline, directory);
			if (null == output){
			//	System.out.println("\n\n\t\tCOMMAND FAILED: " + cmdline);
				output= new ArrayList<String>();
				output.add("\n\n\t\tCOMMAND FAILED: " + cmdline);
			}
			
				return output;
		}

	}
}
