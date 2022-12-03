package command;

import org.w3c.dom.*;
import java.io.*;

/**
 * This class is used for the copy XML tag that is found in the build file.
 */

public class CopyCommand implements Command {

	private String file;
	private String toFile;
	private String toDir;

	/**
	 * This is the constructor used for creating the command node for the 
	 * copy xml tag.  Debugging is turned off.
	 * @param currentNode the XML node to be parsed for this command.
	 */

	public CopyCommand(Node currentNode) {
		this(currentNode, false);
	}

	/**
	 * This is the constructor used for creating the command node for the 
	 * copy xml tag
	 * @param currentNode the XML node to be parsed for this command.
	 * @param debug the boolean flag that determines if debugging is turned on.
	 */
	
	public CopyCommand(Node currentNode, boolean debug) { 
		NamedNodeMap attribs = currentNode.getAttributes();
		if (attribs.getNamedItem("file") != null) {
			file = attribs.getNamedItem("file").getNodeValue();
		} else {
			System.out.println("Copy task does not have file attribute defined.");
			System.exit(0);
		}
		if (attribs.getNamedItem("tofile") != null) {
			toFile = attribs.getNamedItem("tofile").getNodeValue();
		} else{
			if (attribs.getNamedItem("todir") != null) {
				toDir = currentNode.getAttributes().getNamedItem("todir").getNodeValue();
			} else {
				System.out.println("Copy task does not have tofile or todir attribute defined.");
				System.exit(0);
			}
		}
		if (debug) System.out.println("copy command: " + buildCmdString());
	}

	/**
	 * This method creates the command line that will be used for copying files / directories.
	 * @return The command as a string that will be executed by the vivrtual system.
	 */

	private String buildCmdString() {
		if (toDir != null) {
			return "copy " + file + " " + toDir + "/";
		} else {
			return "copy " + file + " " + toFile;
		}
	}

	/**
	 * This method is used to update all environmental variables associated with the copy command
	 * appropriatedly based on what was passed to the dictionary of key /value pairs that represent
	 * the build properties for this particular build.
	 * @param dict the dictionary of build properties that will be used to modify the variables in
	 * the copy command.
	 */

	public void update(Dictionary dict) {
		String[] keys = dict.getKeys();
		for (int i = 0; i < keys.length; i++) {
			toDir = CommandFactory.adjust(toDir, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			toFile = CommandFactory.adjust(toFile, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
			file = CommandFactory.adjust(file, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}

	/**
	 * This function executes the command according to the XML node that was provided
	 * for the the constructor.  This version has a boolean flag that is passed in order
	 * to toggling debuggin durin this method.
	 * @param debug the flag for toggling debugging during this method.
	 */

	public void execute(boolean debug) {
		System.out.println("[ copy ] " + buildCmdString());
		if (toFile != null) copy(file, toFile, debug);
		else copy(file, toDir, debug);
	 }
	/**
     * This function executes the command according to the XML node that was provided
     * for the constructor.
     */

	public void execute()  {
		execute(false);
	}
    
	/**
     * This function provides a way to get the name of the command token that is created.
     * This, in theory, should allow for expansion of commands that can be used as part
     * of the build process.  It is possible to use reflection to get the name because of the
     * class names used, but it is dependent on other developers keeping to the protocol
     * and the expense and unneeded complexity involved in using reflection.
     * @return The enum that represents the name of the specific command. See Command interface.
     */

	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.COPY;
	}

	/**
     * This function returns a String that represents the Command and its parameters
     * @return A String value representing the parameters of the particular Command
     * object.
     */

	public String toString() {
		return buildCmdString();
	}

	/**
	 * This method is used for copying a file or a directory into a differnt directory.
	 * There are plans for it to be recursive in nature, but we shall see how complicated
	 * it gets when implementing it.  Since this is for a build application, I expect the 
	 * number of files to be relatively small.
	 * @param origin is the base file or directory to copy.
	 * @param destination is the destination directory that the origin will be copied into.
	 * @param debug is the boolean flag for testing purposes.
	 */

	private void copy(String origin, String destination, boolean debug) throws FailExecException {
		File orig = new File(origin);
		if (!orig.exists()) {
			throw new FailExecException("unable from copy \'" + origin + "\' since it doesnt exist");
		}
		if (debug) System.out.println(origin + " is a file");
		copySingleFile(origin, destination, debug);
	}

	private void copySingleFile(String orig, String dest, boolean debug) throws FailExecException {
		InputStream in;
		FileOutputStream out;
		if (debug) System.out.println("origin: " + orig + "\ndestination: " + dest);
		try {
			if (debug) System.out.println("opening input");
			in = new FileInputStream(new File(orig));
		} catch (FileNotFoundException fnfe) {
			throw new FailExecException("unable to copy since source \'" + orig + "\' doesnt exist");
		}
		try {
			if (debug) System.out.println("opening output");
			out = new FileOutputStream(dest);
		} catch (FileNotFoundException fnfe) {
			throw new FailExecException("unable to copy since destination \'" + dest + "\\" 
				+ orig + "\' doesnt exist");
		}
		try {
			if (debug) System.out.println("reading and writing");
			byte[] buffer = new byte[1024];
			int len;
			while((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException ioe) {
			System.out.println("unable to perform copy function");
			System.exit(-1);
		}
		if (debug) System.out.println("done with copy \'" + orig + "\' to \'" 
			+ dest + "\'");
	}
}