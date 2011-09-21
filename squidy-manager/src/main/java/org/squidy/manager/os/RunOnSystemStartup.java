package org.squidy.manager.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;

/**
 * <code>RunOnSystemStartup</code>.
 * 
 * <pre>
 * Date: Aug 6, 2010
 * Time: 7:53:42 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public class RunOnSystemStartup {
	/*
	 * Constants
	 */
	protected final static String osName = System.getProperty("os.name");
	protected final static String fileSeparator = System
			.getProperty("file.separator");
	protected final static String javaHome = System.getProperty("java.home");
	protected final static String userHome = System.getProperty("user.home");

	/*
	 * Debugging
	 */
	protected static boolean debugOutput = false;

	protected static void debug(String message) {
		if (debugOutput) {
			System.err.println(message);
			System.err.flush();
		}
	}

	/*
	 * Helpers
	 */
	protected static File getJarFile() throws URISyntaxException {
		return new File(RunOnSystemStartup.class.getProtectionDomain()
				.getCodeSource().getLocation().toURI());
	}

	protected static File getStartupFile() throws Exception {
		debug("RunOnSystemStartup.getStartupFile: osName=\"" + osName + "\"");
		if (osName.startsWith("Windows")) {
			Process process = Runtime
					.getRuntime()
					.exec(
							"reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Startup");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String result = "", line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			result = result.replaceAll(".*REG_SZ[ ]*", "");
			debug("RunOnSystemStartup.getStartupFile: Startup Directory=\""
					+ result + "\"");

			return new File(result + fileSeparator
					+ getJarFile().getName().replaceFirst(".jar", ".bat"));
		} else if (osName.startsWith("Mac OS")) {
			return new File(userHome + "/Library/LaunchAgents/com.mksoft."
					+ getJarFile().getName().replaceFirst(".jar", ".plist"));
		} else {
			throw new Exception("Unknown Operating System Name \"" + osName
					+ "\"");
		}
	}

	/*
	 * Methods
	 */

	/**
	 * Returns whether this JAR file is installed to run on system startup.
	 */
	public static boolean isInstalled() throws Exception {
		return getStartupFile().exists();
	}

	/**
	 * Install the specified class from the current JAR file to run on system
	 * startup.
	 * 
	 * @param className
	 *            Name of class within the current JAR file to run on system
	 *            startup.
	 * @param windowTitle
	 *            Title to display in window title bar, if applicable.
	 */
	public static void install(String className, String windowTitle)
			throws Exception {
		File startupFile = getStartupFile();
		PrintWriter out = new PrintWriter(new FileWriter(startupFile));
		if (osName.startsWith("Windows")) {
			out.println("@echo off");
			out.println("start \"" + windowTitle + "\" \"" + javaHome
					+ fileSeparator + "bin" + fileSeparator + "java.exe\" -cp "
					+ getJarFile() + " " + className);
		} else if (osName.startsWith("Mac OS")) {
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        out.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
	        out.println("<plist version=\"1.0\">");
	        out.println("<dict>");
	        out.println("   <key>Label</key>");
	        out.println("   <string>com.mksoft."+getJarFile().getName().replaceFirst(".jar","")+"</string>");
	        out.println("   <key>ProgramArguments</key>");
	        out.println("   <array>");
	        out.println("      <string>"+javaHome+fileSeparator+"bin"+fileSeparator+"java</string>");
	        out.println("      <string>-cp</string>");
	        out.println("      <string>"+getJarFile()+"</string>");
	        out.println("      <string>"+className+"</string>");
	        out.println("   </array>");
	        out.println("   <key>RunAtLoad</key>");
	        out.println("   <true/>");
	        out.println("</dict>");
	        out.println("</plist>");
		} else {
			throw new Exception("Unknown Operating System Name \"" + osName
					+ "\"");
		}
		out.close();
	}

	/**
	 * Uninstall this JAR file from the system startup process.
	 */
	public static void uninstall() throws Exception {
		File startupFile = getStartupFile();
		if (startupFile.exists()) {
			startupFile.delete();
		}
	}
}
