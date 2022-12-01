import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import command.*;

/**
 * This class provides the mechanism for reading the XML file that is
 * used for the build process.  It is meant to follow the factory design pattern
 * as the call is once or twice for the entire run of the application and
 * would be a waste of resources to keep references of objects no longer used.
 * This is especially true considering the cost of each command tag in the 
 * build file.  Thanksfully build files arent very big, otherwise I would
 * have to find another way of performing the build.
 */

public class FileFactory {

	private static String defaultTarget = null;

	/**
	 * This method returns the default target that is used for the build process.
	 * This version has no boolean argument, which is used to toggle debugging.
	 * @param filename as a String that is to be used for parsing the default
	 * target.
	 * @return The name of the target to use as determined by the filename that
	 * was provided.
	 */

	public static String getDefaultTarget(String filename) {
		return getDefaultTarget(filename, false);
	}

	/**
	 * This method returns the default target that is used for the build process.
	 * This version has a boolean argument, which is used to toggle debugging.
	 * @param filename as a String that is to be used for parsing the default
	 * target.
	 * @param debug is the boolean flag to turn debugging on while using the function.
	 * @return The name of the target to use as determined by the filename that
	 * was provided.
	 */

	public static String getDefaultTarget(String filename, boolean debug) {
		File file = new File(filename);
		ArrayList<Target> result = new ArrayList<>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			// do nothing
		}
		try {
			doc = builder.parse(file);
		} catch (SAXException se) {
			// do nothing
		} catch (IOException ioe) {
			// do nothing
		}
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("project");
		String defaultTarget = list.item(0).getAttributes().getNamedItem("default").getNodeValue();
		if (debug) System.out.println("Default: " + defaultTarget);
		return defaultTarget;
	}

	/**
	 * This method reads all of the targets and creates graphs of the commands needed
	 * to execute them.  This version has no boolean argument, which is used to toggle 
	 * debugging.  This method has debugging off.
	 * @param filename is the file to read, assuming it is an XML file.
	 * @return An array of Targets complete with a graph of the command for each of them.
	 */

	public static ArrayList<Target> readFromFile(String filename) {
		return readFromFile(filename, false);
	}

	

	/**
	 * This method reads all of the targets and creates graphs of the commands needed
	 * to execute them.  This version has a boolean argument, which is used to toggle 
	 * debugging.  
	 * @param filename is the file to read, assuming it is an XML file.
	 * @param debug the boolean flag used to determine is debugging is on or not.
	 * @return An array of Targets complete with a graph of the command for each of them.
	 */

	public static ArrayList<Target> readFromFile(String filename, boolean debug) {
		if (debug) System.out.println("Starting readFromFile()");
		File file = new File(filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			System.exit(0);
		}
		try {
			doc = builder.parse(file);
		} catch (SAXException se) {
			se.printStackTrace();
			System.exit(0);
		} catch (FileNotFoundException fnfe) {
			System.out.println("No build file at location");
			System.exit(0);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("project");

		// this is gnarly loopy stuff..once it works i will reduce the looping to optimize 
		// processing as it is (O)n^3 right now..if possible

		ArrayList<Target> result = new ArrayList<>();
		for (int index = 0; index < list.getLength(); index++) {
			Node node = list.item(index);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
 		 		Element element = (Element) node;
				NodeList currentList = element.getElementsByTagName("target"); 
				System.out.println("targets: " + currentList.getLength());
				for (int i = 0; i < currentList.getLength(); i++) {
					Node newNode = currentList.item(i);
					Target currentTarget = new Target(newNode, debug);
					System.out.println("parsing " + currentTarget.targetName());
					result.add(currentTarget);
					if (debug) System.out.println("target: " + currentTarget.targetName());
					NodeList commands = currentList.item(i).getChildNodes();
					for (int k = 0 ; k < commands.getLength(); k++) {
						Node child = commands.item(k);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element debugger = (Element)child;
							if (debug) System.out.println("converting " + debugger.getTagName().toLowerCase());
							Command resultCmd = CommandFactory.getCommandForNode(child, debug);	
							if (debug) System.out.println(currentTarget.targetName() + " -> " + resultCmd.commandName());
							currentTarget.appendCommand(resultCmd);
						}
					}
				}
			}
		}
		return result;
	}
}