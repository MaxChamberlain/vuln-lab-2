import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSC245_Project2 {

    /*  DISCOVERED VULNERABILITIES
     * 1. Does not check email address for script tags
     *      solved with regex validation
     *
     * 2. Does not check email address for unescaped "illegal" characters
     *      ( # ! = @ % $ . )
     *          solved with regex validation
     *
     * 3. Does not check validity of domain carrier
     *      (.com => .comnfg)
     *          solved with regex validation
     *
     * 4. Does not check validity of domain host
     *      (@arapahoe.edu => @..edu)
     *          solved with regex validation
     *
     * 5. Does not check length of Email name
     *      (ed@...)
     *          solved with regex validation
     *
     * 6. Filename is never checked for validity (null, empty, traversal, etc.)
     *          solved with regex to check filename validity
     *
     * 7. Input stream is never closed if there is an error (memory leak)
     *          solved by using try-with-resources instead of try-catch-finally
     */

    public static void main(String[] args) {
        // Check for filename
        String filename = args[0];
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("No filename");
        if(!FileValidator.fileNameIsValid(filename)) throw new IllegalArgumentException("Invalid filename");

        String fileLine;
        // WHY ARE THERE TWO TRY/CATCH BLOCKS? I removed one but why was it there in the first place?
        // Loop through email addresses and check if they are valid
        try (BufferedReader inputStream = new BufferedReader(new FileReader(filename))) {   // try-with-resources
            System.out.println("Email Addresses:");
            while ((fileLine = inputStream.readLine()) != null) {
                if (ValidateEmail.emailIsValid(fileLine)) {
                    System.out.println(fileLine);
                } else {
                    System.out.println("Invalid Email Address: " + fileLine);
                }
            }
        } catch (IOException io) {
            System.out.println("File IO exception" + io.getMessage());
        }
    }
}
