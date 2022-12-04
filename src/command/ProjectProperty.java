package command;

import org.w3c.dom.*;
import java.io.*;

public class ProjectProperty implements Property {
	
	private Dictionary dict;

	public ProjectProperty(Node currentNode) {
		this(currentNode, false);
	}
	
	public ProjectProperty(Node currentNode, boolean debug) {
		NamedNodeMap attribs = currentNode.getAttributes();
		Dictionary dict = new Dictionary();
		if (attribs.getNamedItem("name") != null) {
			String name = attribs.getNamedItem("name").getNodeValue();
			Tuple tuple = new Tuple("name", name);
			dict.add(tuple);
		}
		String defaultTarget = null;
		if (attribs.getNamedItem("default") != null) {
			defaultTarget = attribs.getNamedItem("default").getNodeValue();
		} else {
			defaultTarget = System.getProperty("user.dir");
		}
		dict.add(new Tuple("default", defaultTarget));
		String basedir = null;
		if (attribs.getNamedItem("basedir") != null) {
			basedir = attribs.getNamedItem("basedir").getNodeValue();
		} else {
			basedir = new File(System.getProperty("user.dir")).getParent();
		}
		dict.add(new Tuple("basedir", basedir));
		if (debug) System.out.println("build property command: " + this.toString());
	}
	public String toString() {
		return "name = " + getValueForKey("name") + "\tdefault = " 
			+ getValueForKey("default") + "\tbasedir = " + this.getValueForKey("basedir");
	}

	/**
	 * Not used here since this gets multiple possible properties in 1 node.  Can use getValue() based on
	 * one of 3 possible things.  The name, default target, and base directory.
	 * @return returns null since it isnt used.
	 */
	public Tuple getTuple() {
		return null;
	}

	/**
	 * Returna the value for the key that is provided.  Since this object can contain up to 3 values,
	 * your criteria rae "name", "default", "basedir".  Otherwise it returns null.
	 * @param key String representation of the key for the requested value.
	 * @return The value based on the key that was provided.
	 */

	 public String getValueForKey(String key) {
		 return dict.getValueForKey(key);
	 }
	
	
}