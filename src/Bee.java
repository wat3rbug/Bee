import java.util.*;

/**
 * This application is my first attempt at building a parser of any sort.  Its limited because its real
 * purpose is to allow me to build stuff at Boeing as their environment is butchered.  I often
 * get issues like the runtime is a different version than the compiler, or changed classpaths, etc.
 * I wanted ant, but they dont support it so I built a lightweight ant, called bee.  I originally called
 * it beaver, cause they build dams, but it sounds like a 70s porno, so it was ditched in favor of bee.
 * They build hives and make honey.
 */
 
public class Bee {

    private static boolean debug = false;

    /**
     * This is the meat of the application.  Phase 1. It parses the build file.  Phase 2.  It then builds
     * a dictionary of build properties and then adjusts all commands to use the new properties.  Phase 3.
     * It gets default target.  Then it executes the build according to the graph that was created from the 
     * parsing of the build file.
     * @param args Only the first argument, which is a special target, is used.  If not argument is provided,
     * then the default as designated by the build file, will be used.
     */

    public static void main(String[] args) {   

        // parse build file

        String defaultTarget = null;
        System.out.println("parsing build file...");
        ArrayList<Target> targets = FileFactory.readFromFile("build.xml", debug);
        if (args.length == 1) {
            defaultTarget = args[0];
        } else {
            defaultTarget = FileFactory.getDefaultTarget("build.xml", debug).toLowerCase();
        }
        boolean found = false;
        int index = 0;
        System.out.println("parsing finished"); 
        printTargetNames(targets);

        // Get build properties

        System.out.println("Setting environment");
        command.Dictionary buildProps = getProperties(targets);
        updateAllTargetProperties(targets, buildProps);
        
        // building the build

        System.out.println("building " + defaultTarget);
        while (!found && index < targets.size()) {
            if (targets.get(index).targetName().matches(defaultTarget)) {
                found = true;
                execute(targets.get(index), targets, debug);
            }
            index++;
        }
        if (!found) System.out.println("No target " + defaultTarget + " to build");
        System.out.println("Build complete");
    }

    /**
     * This method goes through all commands in the build file and updates the variables with the
     * appropriate build properties that were found during phase 2 - getting build properties.  It
     * uses a very rudimentary dictionary structure, specifically written for this application.
     * @param everything is the list of all targets found during phase 1 - parsing the file.
     * @param properties is the dictionary of tuple objects (key/value pairs) to use for modifying
     * the commands
     */

    private static void updateAllTargetProperties(ArrayList<Target> everything, command.Dictionary properties) {
        for (int i = 0; i < everything.size(); i++) {
            Target current = everything.get(i);
            for (int j = 0; j < current.size(); j++) {
                command.Command cmd = current.get(j);
                cmd.update(properties);
            }
        }
    }

    /**
     * This method goes through all the targets and commands found in the build file, creates a tuple
     * for each key / value pair and then adds them to the dictionary.
     * @param everything the list of all targest found during phase 1 - parsing the file.
     * @return the dictionary of all the build properties that were found.  
     */

    private static command.Dictionary getProperties(ArrayList<Target> everything) {
       command.Dictionary buildProperties = new command.Dictionary();
        for (int i = 0; i < everything.size(); i++) {
            Target current = everything.get(i);
            for (int j = 0; j < current.size(); j++) {
                command.Command cmd = current.get(j);
                if (cmd instanceof command.BuildPropertyCommand) {
                    command.Tuple tuple = ((command.BuildPropertyCommand)cmd).getTuple();
                    if (debug) System.out.println("build property: " + tuple.toString());
                    buildProperties.add(tuple);
                }
            }
        }
        return buildProperties;
    }

    /**
     * This method simply lists the number of targets discovered after parsing the build file.
     * It is mostly used for troubleshooting purposes.
     * @param currentTargets is the ArrayList of targets.  There was a problem with null commands
     * and then null targets for various reasons, so this was used.  It can be deleted if needed.
     */

    private static void printTargetNames(ArrayList<Target> currentTargets) {
        for (int i = 0; i < currentTargets.size(); i++) {
            System.out.println("target: " + currentTargets.get(i).targetName());
        }
    }

     /**
     * This method provides a means of recursively executing a target based first on dependencies
     * and then by the Commands in the dependent targets.   Debuggin is turned off.
     * @param currentTarget is the target to parse and see what dependencies need to be done, then
     * the respective commands.
     * @param everythingElse the ArrayList of other targets, for checking dependencies.
     */

    private static void execute(Target currentTarget, ArrayList<Target> everythingElse) {
        execute(currentTarget, everythingElse, false);
    }
    
    /**
     * This method provides a means of recursively executing a target based first on dependencies
     * and then by the Commands in the target.
     * NOTE: There needs to be a check to make sure that loops are formed.
     * @param currentTarget is the target to parse and see what dependencies need to be done, then
     * the respective commands.
     * @param everythingElse the ArrayList of other targets, for checking dependencies.
     * @param debug the boolean that determines if debugging is to be used during this method.
     */

    private static void execute(Target currentTarget, ArrayList<Target> everythingElse, boolean debug) {

        // go through each target and then head to the dependencies to do them first

        System.out.println("[ " + currentTarget.targetName() + " executing...]");
        String[] dependents = currentTarget.getDependencies();
        if (dependents == null) {
            System.out.println("no dependencies");
        } else {
            System.out.println("dependency count:" + dependents.length);
            for (String selected : dependents) {
                for (int i = 0; i < everythingElse.size(); i++) {
                    String currentNodeStr = everythingElse.get(i).targetName();
                    if (selected.matches(currentNodeStr)) {
                        execute(everythingElse.get(i), everythingElse);
                    }
                }
            }
        }
        if (debug) System.out.println("[" + currentTarget.targetName() + ": " + currentTarget.size() + "]");

        // after dependencies are done, execute each command for this target

        for (int i = 0; i < currentTarget.size(); i++) {
            command.Command cmd = currentTarget.get(i);
            if (!(cmd instanceof command.BuildPropertyCommand)) {
                System.out.println("[" + currentTarget.targetName() + ": executing " + cmd.commandName() + "]");
                cmd.execute();
            }
        }
  
    }
}


