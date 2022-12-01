package command;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import java.lang.*;

/**
 * This class is used for the mkdir XML tag that is found in the build file.
 */

public class MkDirCommand implements Command {

	private String dir;
	private Node currentNode;
	private boolean debug;

	private static final int NOT_FOUND = -1;

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command. Debugging is turned off.
	 * @param currentNode the XML node that is parsed for the command.
	 */

	public MkDirCommand(Node currentNode) {
		this(currentNode, false);
	}
	
	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode the XML node that is parsed for the command.
	 * @param debug the boolean flag that determines if debugging is turned on.
	 */

	public MkDirCommand(Node currentNode, boolean debug) {
		this.currentNode = currentNode;
		this.debug = debug;
		if (currentNode.getAttributes().getNamedItem("dir") != null) {
			dir = currentNode.getAttributes().getNamedItem("dir").getNodeValue();
		} else {
			System.out.println("MkDir task does not have dir attribute defined.");
			System.exit(0);
		} 
		if (debug) System.out.println("mkdir command: " + buildCmdString());

	}

	private String buildCmdString() {
		return "mkdir " + dir;
	}

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			dir = CommandFactory.adjust(dir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}

	/**
     * This function executes the mkdir command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptic details about the failure and like to have something applicable.
     */

	public void execute() throws FailExecException {
		String current = System.getProperty("user.dir") + "\\" + dir;
		File fh = new File(current);
		if (fh.mkdir()) {
			System.out.println("[ mkdir " + dir + " complete ]");
		} else {
			System.out.println("[ mkdir " + dir + " already exists ]");
		}
	}

	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command.  See Command interface.
     */
	
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.MKDIR;
	}

	/**
     * This function returns a String that represents the MkDirCommand and the directory
	 * that the object has.
     * @return A String value representing the command for the MkDirCommand object.
     */

	public String toString() {
		return "mkdir " + dir;
	}
}