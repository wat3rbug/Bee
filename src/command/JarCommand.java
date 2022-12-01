package command;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;

/**
 * This class is used for the jar XML tag that is found in the build file.
 */

public class JarCommand implements Command {

	private String destFile;
	private String basedir;
	private String manifest;
	private Node currentNode;
	private boolean debug;

	private static final int NOT_FOUND = -1;

	/**
	 * This constructor parses the jar XML node that is passed to it and creates
	 * what this tool needs to perform the command. Debugging is turned off.
	 * @param currentNode the XML node that is parsed for this command.
	 */

	public JarCommand(Node currentNode) {
		this(currentNode, false);
	}

	/**
	 * This constructor parses the jar XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode the XML node that is parsed for this command.
	 * @param debug the boolean flag that is used to determine if debugging is turned on.
	 */
	
	public JarCommand(Node currentNode, boolean debug) {
		this.currentNode = currentNode;
		this.debug = debug;
		NamedNodeMap attribs = currentNode.getAttributes();
		if (attribs.getNamedItem("manifest") != null) {
			manifest = attribs.getNamedItem("manifest").getNodeValue();
		}
		if (attribs.getNamedItem("destfile") == null) {
			System.out.println("Jar task does not have destfile attribute defined.");
			System.exit(0);
		}
		destFile = attribs.getNamedItem("destfile").getNodeValue();
		if (attribs.getNamedItem("basedir") == null) {
			System.out.println("Jar task does not have basedir attribute defined.");
			System.exit(0);
		}
		basedir = attribs.getNamedItem("basedir").getNodeValue();
		if (debug) System.out.println("jar command: " + buildCmdString());
	 }

	 private String buildCmdString() {
		StringBuilder buffer = new StringBuilder();
		String args = null;
		buffer.append("jar ");
		args = manifest != null ? "-cfm " : "-cf ";
		buffer.append(args + " " + destFile + " ");
		if (manifest != null) buffer.append(manifest + " ");
		buffer.append(basedir);
		return buffer.toString();
	 }

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			basedir = CommandFactory.adjust(basedir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			destFile = CommandFactory.adjust(destFile, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			manifest = CommandFactory.adjust(manifest, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}
    
	/**
     * This function executes the mkdir command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptice details about the failure and like to have something applicable.
     */

	public void execute() {
		boolean usualFailure = false;
		try {
			System.out.println("[ jar ] " + buildCmdString());
			Runtime.getRuntime().exec(buildCmdString());
    	} catch (IOException ioe) {
			if (basedir == null) {
				usualFailure = true;
				throw new FailExecException("unable to jar an unknown directory");
			}
			if (destFile == null) {
				usualFailure = true;
				throw new FailExecException("unable to create jar file since no file was given");
			}
			if (!usualFailure) {
        		ioe.printStackTrace();
			}
		}
	}

	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command, See Command interface.
     */

	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.JAR;
	}

	/**
     * This function returns a String that represents the JarCommand and the directory
	 * that the object has.
     * @return A String value representing the command for the JarCommand object.
     */

	public String toString() {
		return buildCmdString();
	}
}