import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSC245_Project2 {

    /*  DISCOVERED VULNERABILITIES
     * 1. Does not check email address for script tags
     * solved
     * 2. Does not check email address for unescaped "illegal" characters
     *      ( # ! = @ % $ . )
     * solved
     * 3. Does not check validity of domain carrier
     *      (.com => .comnfg)
     * solved
     * 4. Does not check validity of domain host
     *      (@arapahoe.edu => @..edu)
     * solved
     * 5. Does not check length of Email name
     *      (ed@...)
     * solved
     */

    public static void main(String[] args) {
        // Read the filename from the command line argument
        String filename = args[0];
        BufferedReader inputStream = null;
        /*
         * filename is not sanitized
         */
        String fileLine;
        try {
            try {
                /*
                 * Shouldn't this be in its own try/catch?
                 */
                inputStream = new BufferedReader(new FileReader(filename));

                System.out.println("Email Addresses:");

                // Read one Line using BufferedReader

                /*
                 * I feel like there should be more checks here...
                 */
                while ((fileLine = inputStream.readLine()) != null) {
                    boolean valid = false;
                    // Regex for email address
                    String regex = "^[a-zA-Z0-9_.+-]{3,}@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(fileLine);
                    if (matcher.matches()) {
                        valid = true;
                    }
                    if (valid) {
                        System.out.println(fileLine);
                    } else {
                        System.out.println("Invalid Email Address: " + fileLine);
                    }
                }
            } catch (IOException io) {
                System.out.println("File IO exception" + io.getMessage());
            } finally {
                // Need another catch for closing
                // the streams
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException io) {
                    System.out.println("Issue closing the Files" + io.getMessage());
                }

            }
        } catch (Error e) {
            System.out.println(e);
        }

    }
}
