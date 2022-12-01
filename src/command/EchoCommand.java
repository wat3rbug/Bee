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
 * This object is used for the Echo command that is used in the build process.
 */

public class EchoCommand implements Command {

	private String message;
     private Node currentNode;
     private boolean debug;



	/**
      * The constructor uses an XML node and sets up the EchoCommand for use by the
      * build tool. Debugging is turned off.
      * @param currentNode - The XML node that will be used to create the EchoCommand
      * object.
      */

     public EchoCommand(Node currentNode) {
          this(currentNode, false);
     }

	/**
      * The constructor uses an XML node and sets up the EchoCommand for use by the
      * build tool.
      * @param currentNode - The XML node that will be used to create the EchoCommand
      * @param debug the boolean flag that determines if debugging is to be turned on.
      * object.
      */

	public EchoCommand(Node currentNode, boolean debug) {
          this.debug = debug;
          this.currentNode = currentNode;
          if (currentNode.getAttributes().getNamedItem("message") != null) {
               message = currentNode.getAttributes().getNamedItem("message").getNodeValue();
          } else {
               System.out.println("Echo task does not have message attribute defined.");
          }
          if (debug) System.out.println("echo command: " + buildCmdString());
	}

     /**
      * This method builds the command String based on the environmental variables it cares about.
      * @return A String that represents the entire command that needs to be performed.
      */

     private String buildCmdString() {
          return message;
     }

     /**
      * This method allows a dictionary of build properties to be used to update the environmental
      * variables for this command.
      * @param dict is the dictionary of build properties to be used for updating the command.
      */

	public void update(Dictionary dict) {
          String[] keys = dict.getKeys();
		for (int i = 0; i < dict.size(); i++) {
			message = CommandFactory.adjust(message, "${" + keys[i] + "}", dict.getValueForKey(keys[i]));
		}
	}

 	/**
      * This function executes the command according to the XML node that was provided
      * for the constructor.
      */

	public void execute() {
          System.out.println("[ echo ] " + buildCmdString());
	}

	/**
      * This function provides a way to get the name of the command token that is created.
      * This, in theory, should allow for expansion of commands that can be used as part
      * of the build process.  It is possible to use reflection to get the name because of the
      * class names used, but it is dependent on other developers keeping to the protocol
      * and the expense and unneeded complexity involved in using reflection.
      * @return The enum that represents the name of the specific command, in this case "echo".
      * See Command interface.
      */
    
	public COMMAND_TYPE commandName() {
		return COMMAND_TYPE.ECHO;
	}

     /**
      * This function returns a String that represents the EchoCommand and its parameter
      * @return A String value representing the parameter of the EchoCommand object.
      */

     public String toString() {
		return buildCmdString();
	}
}