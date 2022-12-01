package command;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * This class is used for the delete XML tag that is found in the build file.
 */

public class DeleteCommand implements Command {

	private String file;
	private String dir;
	private Node currentNode;
	private boolean debug;

	private static final int NOT_FOUND = -1;

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command. Debugging is turned off.
	 * @param currentNode the XML node that is to be parsed for this command.
	 */

	public DeleteCommand(Node currentNode) {
			this(currentNode, false);
	}

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode the XML node that is to be parsed for this command.
	 * @param debug the boolean flag to determine if debugging is to be turned on.
	 */
	
	public DeleteCommand(Node currentNode, boolean debug) {
		this.debug = debug;
		this.currentNode = currentNode;
		NamedNodeMap attribs = currentNode.getAttributes();
		if (attribs.getNamedItem("file") != null) {
			file = attribs.getNamedItem("file").getNodeValue();
		} else {
			if (attribs.getNamedItem("dir") != null) {
				dir = currentNode.getAttributes().getNamedItem("dir").getNodeValue();
			} else {
				System.out.println("Delete task does not have dir or file attribute defined.");
				System.exit(0);
			}
		}
		if (debug) System.out.println("delete command: " + buildCmdString());
	 }

	 private String buildCmdString() {
		 StringBuilder buffer = new StringBuilder();
		 buffer.append("cmd.exe ");
		 if (file != null) {
			 buffer.append("del -y " + file);
		 } else {
			 buffer.append("del /s /q " + dir);
		 }
		 return buffer.toString();
	 }

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			file = CommandFactory.adjust(file, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			dir = CommandFactory.adjust(dir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}	
	}

	/**
     * This function executes the delete command according to the XML node that was provided.
	 * Custom exceptions are built so that the application can fail with the right details.
	 * I hate cryptic details about the failure and like to have something applicable.
     */

	public void execute() throws FailExecException {
		String current = System.getProperty("user.dir") + "\\";
		if (dir == null) current = current + "\\" + file;
		else current = current + "\\" + dir;
		try {
			Files.deleteIfExists(Paths.get(current));
		} catch (IOException ioe) {
			throw new FailExecException("Unable to delete " + current);
		}
	}

	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command.  See Command interface.
     */
    
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.DELETE;
	}

	/**
     * This function returns a String that represents the DeleteCommand and the directory
	 * that the object has.
     * @return A String value representing the command for the DeleteCommand object.
     */

	public String toString() {
		return buildCmdString();
	}
}