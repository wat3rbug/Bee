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

/**
 * This class is the base for parsing the build.  Everything starts with a target and
 * all subsequent commands are done in order, according to the target that is selected.
 */

public class Target  {

    private static final int NOT_FOUND = -1;

    private String name;
    private String[] depends;
    private ArrayList<command.Command> siblings;
    private int index = 0;
    private boolean debug;

    /**
     * The constructor uses and XML node and sets up the Target for use by the
     * build tool.  Debugging is turned off.
     * @param currentNode The XML node that will be used to create the target
     * object.
     */

    public Target(Node currentNode) {
        this(currentNode, false);
    }

    /**
     * The constructor uses and XML node and sets up the Target for use by the
     * build tool.
     * @param currentNode The XML node that will be used to create the target
     * object.
     * @param debug the boolean flag used to determine if debugging is turned on.
     */

    public Target(Node currentNode, boolean debug) {
        this.debug = debug;
        this.name = currentNode.getAttributes().getNamedItem("name").getNodeValue();
        if (debug) System.out.println("target name: " + name);
        siblings = new ArrayList<command.Command>();
        boolean hasDependents = ((Element)currentNode).hasAttribute("depends");
        if (hasDependents) {
            String dependents = currentNode.getAttributes().getNamedItem("depends").getNodeValue();
            if (dependents != null) {
                if (dependents.indexOf(",") == NOT_FOUND) {
                    depends = new String[]{dependents};
                    if (debug) {
                        for (String temp : depends) {
                            System.out.println("dependent: " + temp);
                        }
                    }
                } else {
                    depends = dependents.split(",");
                     if (debug) {
                        for (String temp : depends) {
                            System.out.println("dependent: " + temp);
                        }
                    }
                }
            }
        }  
        if (debug) System.out.println("dependencies: ");
        for (int i = 0; depends != null && i < depends.length; i++) {
            if ((i + 1) == depends.length) {
                System.out.println(depends[i]);
            } else {
                System.out.print(depends[i]);
            }
        }  
        index = 0;
        this.toString();
    }

    /**
     * This function adds command to the target stack. This is used during the parsing phase of 
     * the build.
     * @param child  The command to be added to the stack.
     */

    public void appendCommand(command.Command child) {
        siblings.add(child); 
    }

    /**
     * This method returns the number of Commands that this target has attached to it that
     * must be executed.  If there are no Commands it will return 0.
     * @return An integer value for the number of Commands in this target.
     */

    public int size() {
        return siblings.size();
    }

    /**
     * This method returns the command at the specified index, provided the index is
     * within range.  Otherwise it returns null and must be checked such a result.
     * @param The index value of a 0 based array.
     * @return the command at the specified index.  More than 1 command can be present
     * for a target.
     */

    public command.Command get(int index) {
        if (index <= siblings.size()) return siblings.get(index);
        else return null;
    }

    /**
     * This method returns the target name as described by the name atribute in the
     * XML tag.  Useful for determining which target and subsequent targets to build,
     * as per the build properties.
     * @return The name of the target as a String.
     */

    public String targetName() {
        return name;
    }

    /**
     * This method follows the standard practice of displaying all the parameters,
     * which includes the dependent commands
     * @return The String display of the object parameters.
     */

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("name: " + name + "\n");
        if (depends != null) {
            for (String dependent : depends) {
                buffer.append("depend: " + dependent + "\n");
            }
        }
        buffer.append("commands: " + siblings.size());
        if (siblings.size() != 0) {
           for (int i = 0; i < siblings.size(); i++) {
               buffer.append(siblings.get(i).toString());
            }
        }
        return buffer.toString();
    }

    /**
     * This function returns an array of dependencies discovered during the
     * parsing section of the application.
     * @return an array of string representing the comma seperated values
     */

    public String[] getDependencies() {
        return depends;
    }
}