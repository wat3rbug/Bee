package command;

import org.w3c.dom.*;
import java.io.*;

/**
 * This class is used for the java XML tag that is found in the build file.
 */

public class JavaCommand implements Command {

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.  This particular XML node
	 * has many arguments and possibilities.  It will grow with complexity as time
	 * moves on.
	 */

	private String jar;
	private String classpath;
	private String classname;
	private String executable;
	private boolean fork;

	/**
	 * This constructor parses the java XML node that is passed to it and creates
	 * what this tool needs to perform the command. Debugging is turned off.
	 * @param currentNode the XML node that is parsed for this command.
	 */

	public JavaCommand(Node currentNode) {
		this(currentNode, false);
	}

	/**
	 * This constructor parses the java XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode the XML node that is parsed for this command.
	 * @param debug the boolean flag that is used to determine if debugging is turned on.
	 */
	
	public JavaCommand(Node currentNode, boolean debug) { 
		NamedNodeMap attribs = currentNode.getAttributes();
		if (attribs.getNamedItem("classname") == null) {
			System.out.println("Java task does not have classname attribute defined.");
			System.exit(0);
		} else {
			classname = attribs.getNamedItem("classname").getNodeValue();
		}
		if (attribs.getNamedItem("jar") != null) {
			jar = attribs.getNamedItem("jar").getNodeValue();
		}
		if (attribs.getNamedItem("classpath") != null) {
			classpath = attribs.getNamedItem("classpath").getNodeValue();
		}
		String temp = attribs.getNamedItem("fork").getNodeValue();
		if (temp != null && temp.toLowerCase().matches("true")) {
			fork = true;
		} else {
			fork = false;
		}
		if (attribs.getNamedItem("executable") != null) {
			executable = attribs.getNamedItem("executable").getNodeValue();
		}
		if (debug) System.out.println("java command: " + buildCmdString());
	}

	/**
	 * This method builds the command line that will be used by the system outside of the virtual
	 * system to run the specified application.
	 * @return A String representation of the command that will be used.
	 */

	private String buildCmdString() {
		StringBuilder buffer = new StringBuilder();
		if (executable != null) buffer.append(executable + " ");
		else buffer.append("java ");
		if (jar != null) buffer.append("-jar ");
		if (classpath != null) buffer.append("-cp " + classpath + " ");
		buffer.append(classname);
		if (jar != null && !classname.endsWith(".jar")) buffer.append(".jar"); // will be sore spot later
		return buffer.toString();
	}

	/**
	 * This method takes the dictionary of build properties and passes those to this command to use the ones
	 * applicable to it.
	 * @param dict is the dictionary of key /value pairs with the build properties to use on this command.
	 */

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			jar = CommandFactory.adjust(jar, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			classpath = CommandFactory.adjust(classpath, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			classname = CommandFactory.adjust(classname, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			executable = CommandFactory.adjust(executable, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}

	/**
     * This function executes the java command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptice details about the failure and like to have something applicable.
     */

	public void execute() {
		execute(false);
	}

	/**
     * This function executes the java command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptice details about the failure and like to have something applicable.
	 * @param debug the boolean flag for enabling debugging.
     */

	public void execute(boolean debug) {
		boolean usualFailure = false;
		if (jar != null && !fork) {
			usualFailure = true;
			throw new FailExecException("When running jar files, fork must be set to true");
		}
		try {
			System.out.println("[ java ] " + buildCmdString());
			Runtime.getRuntime().exec(buildCmdString());
    	} catch (IOException ioe) {
			if (!usualFailure) {
        		ioe.printStackTrace();
			}
		}
	}

	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command. See Command interface.
     */
    
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.JAVA;
	}

	/**
     * This function returns a String that represents the MkDirCommand and the directory
	 * that the object has.
     * @return A String value representing the command for the MkDirCommand object.
     */

	public String toString() {
		return buildCmdString();
	}
}