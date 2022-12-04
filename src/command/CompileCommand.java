package command;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * This class is used for the javac XML tag that is found in the build file.
 */

public class CompileCommand implements Command {

	private String srcdir;
	private String destdir;
	private String classpath;
	private String executable;

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.  There is no debugging by
	 * default for this constructor.
	 * @param currentNode is the XML node that is used for this command
	 */

	public CompileCommand(Node currentNode) {
		this(currentNode, false);
	}

	/**
	 * This method goes through all the variables for this command and substitutes
	 * the sections where the keys are used, with the actual values found int the 
	 * dictionary.
	 * @param dict the dictionary of build properties that are to be parsed and 
	 * used for command variables.
	 */

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			srcdir = CommandFactory.adjust(srcdir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			destdir = CommandFactory.adjust(destdir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			classpath = CommandFactory.adjust(classpath, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			executable = CommandFactory.adjust(executable, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode is the XML node that is used for this command
	 * @param debug the boolean flag for debugging purposes.
	 */
	
	public CompileCommand(Node currentNode, boolean debug) { 
		NamedNodeMap attribs = currentNode.getAttributes();
		if (attribs.getNamedItem("srcdir") != null) {
				srcdir = attribs.getNamedItem("srcdir").getNodeValue();
				srcdir =" " + srcdir;
		}
		if (attribs.getNamedItem("destdir") != null) {
			destdir = attribs.getNamedItem("destdir").getNodeValue();
			destdir = " -d " + destdir;
		}
		if (attribs.getNamedItem("classpath") != null) {
			classpath = attribs.getNamedItem("classpath").getNodeValue();
			classpath = " -cp " + classpath;
		}
		if (attribs.getNamedItem("executable") != null) {
			executable = attribs.getNamedItem("executable").getNodeValue();
		}
		if (debug) System.out.println("compile command: " + buildCmdString());
	}

	/**
	 * This method takes the parameters of this command and builds the needed
	 * command to use by the ProcessBuilder.
	 * @return a String comprised of all parameters, if provided, for use in the
	 * execution of this command.
	 */

	private String buildCmdString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("cmd.exe");
		if (executable != null) buffer.append(" " + executable);
		else buffer.append(" javac");
		buffer.append(srcdir);
		if (destdir != null) buffer.append(destdir);
		if (classpath != null) buffer.append(classpath);
		return buffer.toString();
	}

	/**
     * This function executes the compile command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptic details about the failure and like to have  something applicable.
	 * @param debug the flag for turning debugging statements on during method.
     */

	public void execute(boolean debug) {
		try {
			ArrayList<String> commandArgs = new ArrayList<>();
			commandArgs.add("javac ");
			srcdir ="" + srcdir;
			commandArgs.add(srcdir);
			destdir = "-d " + destdir;
			commandArgs.add(destdir);
			classpath = "-cp " + classpath;
			commandArgs.add(classpath);
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < commandArgs.size(); i++) {
				buffer.append(commandArgs.get(i));
			}
			String filteredArgs = buffer.toString();
			Runtime.getRuntime().exec(filteredArgs);
			System.out.println("[ javac ] " + buildCmdString());
			Runtime.getRuntime().exec(buildCmdString());
		} catch (IOException ioe) {
			if (srcdir != null || destdir != null || classpath != null) {
				if (debug) {
					throw new FailExecException("Unable to compile.  See parameters\n" + this.toString());
				} else {
					System.out.println("Unable to compile.  See parameters\n" + this.toString());
					System.exit(0);
				}
			} else {
				ioe.printStackTrace();
			}
		}
	}

	/**
     * This function executes the compile command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * Debug is disabled by default. I hate cryptic details about the failure and like to have 
	 * something applicable.
     */

	public void execute() {
		execute(false);
	}
	
	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command.  See Command interface.
     */
    
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.JAVAC;
	}

	/**
     * This function returns a String that represents the CompilerCommand and the directory
	 * that the object has.
     * @return A String value representing the command for the CompilerCommand object.
     */

	public String toString() {
		return buildCmdString();
	}
}