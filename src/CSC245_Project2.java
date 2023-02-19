import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;

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
        //Normalize the filename to prevent unicode attacks
        String normalizedFilename = Normalizer.normalize(new File(filename).getCanonicalPath(), Normalizer.Form.NFKC);

        FileValidationResult res = FileValidator.fileNameIsValid(Path.of(normalizedFilename));
        if (!res.okay) {
            System.err.println(res.msg);
            return;
        }

        String fileLine;
        // WHY ARE THERE TWO TRY/CATCH BLOCKS? I removed one but why was it there in the first place?
        // Loop through email addresses and check if they are valid
        try (BufferedReader inputStream = new BufferedReader(new FileReader(normalizedFilename))) {   // try-with-resources
            System.out.println("Email Addresses:");
            String content = "";
            while ((fileLine = inputStream.readLine()) != null) {
                try{
                    //normalize file line to prevent unicode attacks
                    String normalizedFileLine = Normalizer.normalize(fileLine, Form.NFKC);
                    if (ValidateEmail.emailIsValid(normalizedFileLine)) {
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