package command;

/**
 * This class is an abbreviated version of a Dictionary, used constantly in Objective-C.
 * It is essentially a forward facing linked list of tuples ( key /value pairs).  It 
 * only has the methods needed for this application and nothing further.
 */

public class Dictionary {
    
    public Tuple root;
    private int count;

    /**
     * An empty constructor, only used to supply basic compiler requirements for an
     * object.
     */

    public Dictionary() { }

    /**
     * This method is what is used to populate the dictionary.  You add the tuple, which is
     * also a simplified structure.  It is attached to the end of the dictionary.
     * @param tuple a basic data structure used by this structure.  It is added to the end of
     * the list and the count is increased.
     */

    public void add(Tuple tuple) {
        if (root == null) {
            root = tuple;
            count++;
        } else {
            Tuple current = root;
            Tuple previous = root;
            while (current != null) {
                if (current == previous) {
                    current = current.next;
                } else {
                    previous = current;
                    current = current.next;
                }
            }
            previous.next = tuple;
            count++;
        }
    }

    /**
     * This method returns the count of the tuples stored within the data structure.
     * @return an integer value for the number of nodes within the structure.  Needed for
     * the for loops in the application.
     */

    public int size() {
        return count;
    }

    /**
     * This method returns an array of String for all of the keys in this dictionary.
     * @return an array of Strings that represent all the keys stored in this dictionary.
     */

    public String[] getKeys() {
        Tuple current = root;
        String[] results = new String[count];
        for (int i = 0; i < count; i++) {
            results[i] = current.key;
            current = current.next;
        }
        return results;
    }

    /**
     * This method goes through the dictionary at O(n) to find they pair which matches the
     * key that was provided, and then the value paired with the key is returned.
     * @param key the String which represents the key in the dictionary in which to look
     * for its representative value.
     * @return The String value as part of the key /value pair in the dictionary.
     */
     
    public String getValueForKey(String key) {
        Tuple current = root;
        while (current != null) {
            if (current.key.matches(key)) return current.value;
            current = current.next;
        }
        return null;
    }

}