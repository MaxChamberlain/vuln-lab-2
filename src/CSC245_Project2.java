import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void main(String[] args) throws IOException {

        // Check for filename
        String filename = "./assets/" + args[0];
        String canonicalFilename = new File(filename).getCanonicalPath();
        if(!canonicalFilename.endsWith(".txt")) throw new IllegalArgumentException("Invalid File Type");
        if (canonicalFilename == null || canonicalFilename.isEmpty()) throw new IllegalArgumentException("No filename");
        String regex = "^[a-zA-Z0-9_\\./:]+.txt$"; // Regex for valid characters
        Pattern filenameRegex = Pattern.compile(regex);
        Matcher filenameMatcher = filenameRegex.matcher(canonicalFilename);
        boolean matches = filenameMatcher.matches();

        if(matches) throw new IllegalArgumentException("Invalid filename");

        String currentDirectory = System.getProperty("user.dir");
        String assetsFolderPath = currentDirectory + File.separator + "assets";
        if (!canonicalFilename.startsWith(assetsFolderPath)) throw new IllegalArgumentException("Provided path outside of assets folder");


        String fileLine;
        // WHY ARE THERE TWO TRY/CATCH BLOCKS? I removed one but why was it there in the first place?
        try (BufferedReader inputStream = new BufferedReader(new FileReader(canonicalFilename))) {   // try-with-resources
            System.out.println("Email Addresses:");
            // Regex for email address
            Pattern emailRegex = Pattern.compile("^[a-zA-Z0-9_.+-]{3,}@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]{2,}$");
            while ((fileLine = inputStream.readLine()) != null) {
                if (emailRegex.matcher(fileLine).matches()) {
                    System.out.println(fileLine);
                } else {
                    System.out.println("Invalid Email Address: " + fileLine);
                }
            }
        } catch (IOException io) {
            System.out.println("File IO exception: " + io.getMessage());
        }
    }
}