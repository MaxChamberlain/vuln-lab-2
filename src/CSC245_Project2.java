import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
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
        String filename = args[0];
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("No filename");

        String canonicalFilename = new File(filename).getCanonicalPath();

        //Normalize the filename to prevent unicode attacks
        String normalizedFilename = Normalizer.normalize(canonicalFilename, Form.NFKC);
        
        if(!normalizedFilename.endsWith(".txt")) throw new IllegalArgumentException("Invalid File Type");

        //Validate filename
        String filenameRegex = "^[a-zA-Z0-9_\\./:]+.txt$"; // Regex for valid characters
        Pattern filenamePattern = Pattern.compile(filenameRegex);
        Matcher filenameMatcher = filenamePattern.matcher(normalizedFilename);
        boolean matches = filenameMatcher.matches();
        if(matches) throw new IllegalArgumentException("Invalid filename");

        //Validate Target Directory (assets)
        String currentDirectory = System.getProperty("user.dir");
        String assetsFolderPath = currentDirectory + File.separator + "assets";
        if (!normalizedFilename.startsWith(assetsFolderPath)) throw new IllegalArgumentException("Provided path outside of assets folder");

        // Check if file is too large
        if (isFileTooLarge(normalizedFilename)) throw new IllegalArgumentException("File too large");

        String fileLine;
        // WHY ARE THERE TWO TRY/CATCH BLOCKS? I removed one but why was it there in the first place?
        try (BufferedReader inputStream = new BufferedReader(new FileReader(normalizedFilename))) {   // try-with-resources
            System.out.println("Email Addresses:");
            // Regex for email address
            Pattern emailRegex = Pattern.compile("^[a-zA-Z0-9_.+-]{3,}@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]{2,}$");
            String content = "";
            while ((fileLine = inputStream.readLine()) != null) {
                try{
                    //normalize file line to prevent unicode attacks
                    String normalizedFileLine = Normalizer.normalize(fileLine, Form.NFKC);
                    if (emailRegex.matcher(normalizedFileLine).matches()) {
                        System.out.println(normalizedFileLine);
                        content += HTMLEntityEncode(normalizedFileLine) + ",";
                    } else {
                        System.out.println("Invalid Email Address: " + normalizedFileLine);
                        content += "Invalid Email Address: " + HTMLEntityEncode(normalizedFileLine) + ",";
                    }
                } catch(IllegalArgumentException e){
                    System.out.println(e);
                }
            }
            content = content.substring(0, content.length() - 2);
            createHTMLFile(normalizedFilename, content);
        } catch (IOException io) {
            System.out.println("File IO exception: " + io.getMessage());
        }
    }

    // Check if the file size is too large
    private static boolean isFileTooLarge(String filename) throws IOException {
        long fileSize = Paths.get(filename).toFile().length();
        long maxFileSize = 1000000; // 1 MB
        return fileSize > maxFileSize;
    }

    private static void createHTMLFile(String filename, String content) {
        String htmlFilename = filename + ".html";
        String html = "<html><head><title>" + filename + "</title></head><body>";
        for(String line : content.split(",")){
            html += "<p>" + line + "</p>";
        }
        html += "</body></html>";
        try {
            FileWriter myWriter = new FileWriter(htmlFilename);
            myWriter.write(html);
            myWriter.close(); 
            System.out.println("Successfully wrote to the html file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
    
    private static String HTMLEntityEncode(String input) {
        StringBuffer returnedString = new StringBuffer();

        for (int i = 0; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);
            if (Character.isLetterOrDigit(currentCharacter) || Character.isWhitespace(currentCharacter)) {
                returnedString.append(currentCharacter);
            } else {
                returnedString.append("&#" + (int)currentCharacter + ";");
            }
        }
        return returnedString.toString();
    }

}