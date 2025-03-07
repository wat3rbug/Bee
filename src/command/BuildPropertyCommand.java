package command;

import org.w3c.dom.*;

/**
 * This class is used for the build properties XML tags that are found in the build file.
 */

public class BuildPropertyCommand implements Command, Property {
    
	private Tuple tuple;

	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.  There is no debugging by
	 * default for this constructor.
	 * @param currentNode is the XML node that is used for this command
	 */

	public BuildPropertyCommand(Node currentNode) {
		this(currentNode, false);
	}
	
	/**
	 * This constructor parses the XML node that is passed to it and creates
	 * what this tool needs to perform the command.
	 * @param currentNode is the XML node that is used for this command
	 * @param debug the boolean flag for debugging purposes.
	 */

	public BuildPropertyCommand(Node currentNode, boolean debug) { 
		String name = null;
		String value = null;
		try {
			name = currentNode.getAttributes().getNamedItem("name").getNodeValue();			
			value = currentNode.getAttributes().getNamedItem("value").getNodeValue();
		} catch (NullPointerException npe) {
			name = "file";
			value = currentNode.getAttributes().getNamedItem("file").getNodeValue();
		}
		tuple = new Tuple(name, value);
		if (debug) System.out.println("build property command: " + this.toString());
	}

	/**
	 * This method is not used because this command is only a command for parsing ease. 
	 * Its really a tuple but hasnt been converted yet.  However, it is part of the
	 * command interface so it must be included.
	 * @param dict is not used.
 	 */

	public void update(Dictionary dict) {
		// not implemented 
	}

	/**
     * This function supplies the build properties according to the XML node that was 
	 * provided. Custom exceptions are built so that the application can fail with 
	 * the right details. I hate cryptic details about the failure and like to have 
	 * something applicable.
	 * @param debug not used.
     */
	
	public void execute(boolean debug) {
		// not implemented
	}

	/**
     * This function supplies the build properties according to the XML node that was 
	 * provided. Custom exceptions are built so that the application can fail with 
	 * the right details. I hate cryptic details about the failure and like to have 
	 * something applicable.
     */
	
	public void execute() {
		// not implemented
	}

	/**
	 * This methods returns a tuple for this key /value pair.
	 * @return the name of the key for this key/value pair.
	 */

	public Tuple getTuple() {
		return tuple;
	}

	/**
     * This function provides a way to get the name of the command token that is created.
	 * This is used in the parsing function for the build tool to differentiate between a
	 * target and the sub nodes within that target object.
     * @return The enum that represents the name of the this command.  See Command interface.
     */
	
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.PROPERTY;
	}

	public String getValueForKey(String key) {
		return tuple.value;
	}

	/**
     * This function returns a String that represents the BuildPropertyCommand and the 
	 * directory that the object has.
     * @return A String value representing the command for the BuildPropertyCommand object.
     */

	public String toString() {
		return tuple.toString();
	}
}