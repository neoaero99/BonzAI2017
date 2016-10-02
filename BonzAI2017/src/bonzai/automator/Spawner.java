package bonzai.automator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/*Use an ssh key
 * 1.) in a terminal, enter ssh-keygen
 * 2.) Use an empty name
 * 3.) Use an empty passphrase
 * 4.) Verify that the files id_rsa and id_rsa.pub are in the directory ~/.ssh
 * 5.) If a file called "authorized_keys" doesn't already exist, create it in the ~/.ssh folder
 * 		with the command "touch authorized_keys".
 * 6.) Append the contents of id_rsa.pub to authorized_keys
 * 		You can do that with the command "cat id_rsa.pub > authorized_keys"
 * 
 * Quick and lazy guide (terminal commands)
 * ssh-keygen; cd ~/.ssh; cat id_rsa.pub > authorized_keys
 * 
 * If you get an auth error.  Try deleting the file ~/.ssh/known_hosts
 */

/*
 * Class that spawns automator clients using ssh
 * On various computers across campus
 */
public class Spawner extends Thread {
	static PrintWriter errorLog;
	static PrintWriter consoleLog;	//Non error messages that don't need to be shown in stdin

	//SETTING THIS TO TRUE WILL PURGE ALL YOUR PROCESSES FROM THESE COMPUTERS
	//MAKE SURE THAT THE COMPUTER YOU ARE USING IS NOT IN THE LIST OF CLIENT IPs
	private static boolean DEBUG_KILL = false;
	
	//private static String EXEC_STRING = "java -jar ./workspace/bonzai-2016/bonzai2016/lazers.jar";
	private static String CD_STRING = "cd ~/bonzai/bonzai-2016/bonzai2016";
	private static String EXEC_STRING = CD_STRING;
	private static int EXECID = 1;	//Increments for every exec() we call

	public static void main(String[] args) throws Exception {
		// Start up the server
		//Process server = Runtime.getRuntime().exec("java -jar lazers.jar");

		// Get my ip address and put it into the serverip file
		// Get my password for ssh communication
		//JPasswordField pwd = new JPasswordField(20);
		//JOptionPane.showConfirmDialog(null, pwd, "Enter Password", JOptionPane.OK_CANCEL_OPTION);

		// Spawn an ssh instance
		JSch jsch = new JSch();
		String host = Inet4Address.getLocalHost().getHostAddress();
		//String host = JOptionPane.showInputDialog("Enter IP of this computer");		

		System.out.println("Our hostname is: " + host);

		//Add more commands to the exec string
		EXEC_STRING += "; java -jar lazers.jar " + host + " ";
		
		if (DEBUG_KILL) { EXEC_STRING = "kill -1 -1; "; }

		System.out.println("Clients will execute: " + EXEC_STRING);
		String username = System.getProperty("user.name");
		//String username = JOptionPane.showInputDialog("Enter username for the computers you're ssh-ing into");	
		System.out.println("Using system username for ssh username: " + username);

		//Uncomment this to manually enter password

		errorLog = new PrintWriter("spawner_errors.txt");
		consoleLog = new PrintWriter("spawner_output.txt");	//Not important enough for stdin

		LinkedList<Thread> threads = new LinkedList<>();

		//Connect to each computer
		Scanner scanner = new Scanner(new File("clientips.txt"));
		while(scanner.hasNextLine()) {
			String ip = scanner.nextLine().trim();
			if (ip.startsWith("#")) { continue; }
			if (ip.length()==0) 	{ continue; }
			try {
				Spawner spawnerThread = new Spawner(jsch,username,ip);
				threads.add(spawnerThread);
				spawnerThread.start();

				//This part is important
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		scanner.close();

		Thread.sleep(3000);//This helps keep the output organized while threads spin up

		System.out.println("All children (client) threads started.  Starting up automation server now");

		System.out.println("***********************************************");
		System.out.println("***********************************************");
		System.out.println("***********************************************");
		System.out.println("* Clients are now running!  You must manually *");
		System.out.println("* start the server (ant run)                  *");
		System.out.println("***********************************************");
		System.out.println("***********************************************");
		System.out.println("***********************************************");
		
		
		//Spin up the automator server
		//DONT UNCOMMENT THIS!  If you can't kill this process, you will have bind exceptions when you try to start another server
		//startAutomationServer(jsch,username);

		System.out.println("Server finished.  Waiting for ssh threads to finish (you may safely close the program now)");

		//Wait for all threads to finish
		while (!threads.isEmpty()) {
			System.out.println(threads.size() + " threads still running.  Waiting for death");
			Thread t = threads.removeFirst();
			t.join();
		}

		errorLog.close();
		consoleLog.close();

		System.out.println("Program terminated.  All threads finished");

	}

	JSch jsch;
	String username;
	String host;
	public Spawner(JSch jsch, String username, String host) {
		this.jsch = jsch;
		this.username = username;
		this.host = host;
	}

	public static void startAutomationServer(JSch jsch,String username) {
		try {
			//We need to do this on an ssh server BECAUSE it needs to die when this java program does
			String command = CD_STRING + "; java -jar lazers.jar";
			String host = "127.0.0.1";
			Session session = jsch.getSession(username,"127.0.0.1",22);
			
			//This chunk is copy/pasted from run()
			jsch.addIdentity("~/.ssh/id_rsa","");
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			
			session.connect();
			exec(command,session,host,true);
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run()  {
		try {
			System.out.println("Connecting to " + username + "@" + host);
			Session session = jsch.getSession(username, host, 22);

			//Uncomment this and comment out the next step to use a manual password instead of an ssh key
			//JPasswordField pwd = new JPasswordField(20);
			//JOptionPane.showConfirmDialog(null, pwd, "Enter Password", JOptionPane.OK_CANCEL_OPTION);
			//session.setUserInfo(new SimpleUserInfo(new String(pwd.getPassword())));

			jsch.addIdentity("~/.ssh/id_rsa","");

			//This tells jsch to not care about unknown host keys
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			exec(EXEC_STRING, session,host, false);
			session.disconnect();
		} catch (Exception e) {
			errorLog.println("Could not connect to " + host + "\n" + e.getMessage());
			errorLog.flush();
		}
	}

	public static void exec(String command, Session session, String host, boolean printToScreen) {
		EXECID++;

		if (!printToScreen) {
			command += EXECID;
		}

		ChannelExec channel = null;
		try {
			channel = (ChannelExec)session.openChannel("exec");
		} catch (JSchException e1) {
			e1.printStackTrace();
		}

		if (printToScreen) {
			System.out.println("\tExecuting " + command);
		}

		consoleLog.println(host + "\tExecuting " + command);
		consoleLog.flush();

		channel.setCommand(command);
		try {
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
		}

		//Get the input stream!
		Scanner scanner = null;
		Scanner error = null;
		try {
			scanner = new Scanner(channel.getInputStream());
			error = new Scanner(channel.getErrStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			consoleLog.println(host + "\t # " + line);
			consoleLog.flush();
			if(!printToScreen) {
				System.out.println(line);
			}
		}
		while (error.hasNextLine()) {
			String line = error.nextLine();
			errorLog.println(host + "\t # " + line);
			errorLog.flush();
			if(!printToScreen) {
				System.err.println(line);
			}
		}

		channel.disconnect();
		consoleLog.println(host + "\tDone!");
		consoleLog.flush();
	}
}

class SimpleUserInfo implements UserInfo{
	String password;

	public SimpleUserInfo(String password) {
		this.password = password;
	}

	public String getPassphrase() {
		return password;
	}

	public String getPassword() {
		return password;
	}

	public boolean promptPassphrase(String arg0) {
		return false;
	}

	public boolean promptPassword(String arg0) {
		return true;
	}

	public boolean promptYesNo(String arg0) {
		return true;
	}

	public void showMessage(String arg0) {
		System.out.println("showMessage() is showing:\n" + arg0);
	}

} 
