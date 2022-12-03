package command;

/**
 * This class is a basic tuple structure for use as a build property for this application.
 * It does not hold all methods needed for general use and is minimized to conserve space
 * , however bloated java may be.
 */

public class Tuple {

    public String key;
    public String value;
    public Tuple next;

    /**
     * This is the most basic and only constructor for the class.  You add a key and a value
     * and it gives you a Tuple object.  Its essentially a linked list tuple.
     * @param key is the String for a key.
     * @param value is the String for a value as part of the key / value pair.
     */

    public Tuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * A basic toString() method.  It only gives the key = value of the structure.
     * @return a String with the key = value format.
     */
     
    public String toString() {
        return key + " = " + value;
    }
}