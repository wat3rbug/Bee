package command;

/**
 * This interface provides a standard for using the commands, once they are parsed for
 * build engine. */

public interface Command {

    public enum COMMAND_TYPE {
        MKDIR,
        JAVA,
        JAR,
        ECHO,
        DELETE,
        COPY,
        JAVAC,
        PROPERTY,
        PROJECT
    };

    /**
     * This method provides a means of updating the environmental variables
     * of various commands that use this interface.  It's a basic search and
     * replace function for the particular command.
     * @param dict is the Dictionary of key / value pairs to use for searching
     * through the command variables.
     */

    public void update(Dictionary dict);

    /**
     * This function provides a way to get the name of the command token that is created.
     * This, in theory, should allow for expansion of commands that can be used as part
     * of the build process.  It is possible to use reflection to get the name because of the
     * class names used, but it is dependent on other developers keeping to the protocol
     * and the expense and unneeded complexity involved in using reflection.
     * @return The enum that represents the name of the specific command.
     */

    public COMMAND_TYPE commandName();

    /**
     * This function executes the command according to the XML node that was provided
     * for the constructor.  This version allows a debug flag to enable debugging.
     * @param debug the boolean flag to toggle debugging on or off.
     */

     public void execute(boolean debug);

    /**
     * This function executes the command according to the XML node that was provided
     * for the constructor.
     */

    public void execute();

    /**
     * This function returns a String that represents the Command and its parameters
     * @return A String value representing the parameters of the particular Command
     * object.
     */

    public String toString();
}