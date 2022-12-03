package command;

import org.w3c.dom.*;

/**
 * This class was built after some I figured I my FileFactory class was getting unwieldy.  It seemed
 * natural to make another Factory class that produced the Commands, based on the nodes that were
 * provided to it.  Its basically 2 functions.  1 of those functions is just an enhanced version of
 * the other one.  Nice and simple.  They way I prefer to write code.
 */

public class CommandFactory {

	private static final int NOT_FOUND = -1;

    /**
	 * This factory function makes a command to based on the xml node that is passed to it.
	 * @param currentNode the xml node that is the target, to be used as the base for 
	 * retrieving the command. The boolean flag is disabled debugging during this function.
	 * @return the command node that is result of the parsing.
	 */

    public static Command getCommandForNode(Node currentNode) {
        return getCommandForNode(currentNode, false);
    }

	/**
	 * This method performs the actual adjustment of the String.  Example in
	 * key/value src.dir = src, and String that has ${src.dir} will be replaced with src if it is
	 * contained in that String.
	 * @param base String to perform a search and replace on.
	 * @param key the actual enclosed key to look for in the base.
	 * @param value the acutal String to place inside the base, if found.
	 * @return the result of the search and replace of the key /value pair in the String.
	 */

	public static String adjust(String base, String key, String value) {
		int index = 0;
		while (base != null && (index = base.indexOf(key)) != NOT_FOUND) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(base.substring(0, index));
			buffer.append(value);
			buffer.append(base.substring(index + key.length()));
			base = buffer.toString();
		}
          return base;
	}

    /**
	 * This factory function makes a command to based on the xml node that is passed to it.
	 * @param currentNode the xml node that is the target, to be used as the base for 
	 * retrieving the command.
	 * @param debug the boolean flag for enabling debugging during this function.
	 * @return the command node that is result of the parsing.
	 */

    public static Command getCommandForNode(Node currentNode, boolean debug) {
        if (debug) System.out.println("Starting getCommandForNode()");
		Element elem = (Element) currentNode;
		String check = elem.getTagName().toLowerCase();
		if (debug) System.out.println("check is " + check);
		boolean same = false;
		Command result = null;
		if (check.matches("mkdir")) {
			if (debug) System.out.println("making mkdir command");
			result = new MkDirCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("jar")) {
			if (debug) System.out.println("making jar command");
			result = new JarCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("java")) {
			if (debug) System.out.println("making java command");
			result = new JavaCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("delete")) {
			if (debug) System.out.println("making delete command");
			result = new DeleteCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("javac")) {
			if (debug) System.out.println("making compile command");
			result = new CompileCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("copy")) {
			if (debug) System.out.println("making copy command");
			result = new CopyCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("property")) {
			if (debug) System.out.println("making build properties command");
			result = new BuildPropertyCommand(currentNode, debug);
			same = !same;
		}
		if (check.matches("echo")) {
			if (debug) System.out.println("making echo command");
			result = new EchoCommand(currentNode, debug);
			same = !same;
		}
		System.out.println("command ->" + result.commandName());
		return result;
    }
}