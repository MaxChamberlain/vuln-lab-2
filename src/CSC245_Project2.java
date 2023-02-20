/*   Max, Jason, AJ, Nathan, Austin and Ronan - CSC245 Project 2

 *   This program reads a file of email addresses and checks if they are valid
 *   It then writes the valid email addresses to a new html file which shows the email addresses in a table
 *   The program also checks if the file is too large, if the filename is valid, and if the filename contains invalid naming conventions
 *   The program also prevents path traversal attacks and unicode attacks
 *   The program also prevents buffer overflow attacks
 *   The program also prevents resource exhaustion attacks
 *   The program also prevents regular expression attacks
 *   The program also prevents JavaScript injection attacks
 *   The program also prevents cross-site scripting attacks

 *   Covering the following known vulnerabilities:
 *   IDS01-J - Prevent Unicode Attacks
 *   IDS08-J - Sanitize Untrusted Data included in a regular expression
 *   IDS50-J - Use Conservative File Naming conventions
 *   IDS51-J - Properly encode or escape output
 *   FIO16-J - Canonicalize Path Names before validating them
 *   ERROR01-J - Do not suppress or ignore checked exceptions (IOException)
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;

public class CSC245_Project2 {

    public static void main(String[] args) throws IOException {

        // Check for filename
        String filename = args[0];
        // Check if filename null or empty
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("No filename");

        /* Normalize Strings before validating them (IDS01-J) - AJ
         * This prevents unicode attacks
         */
        /* Canonicalize Path Names before validating them (FIO16-J) - Max, Jason
         * This prevents path traversal attacks (e.g. ../../etc/passwd)
         */
        String normalizedFilename = Normalizer.normalize(new File(filename).getCanonicalPath(), Normalizer.Form.NFKC);
        //Check if filename contains invalid naming conventions
        if(FileValidator.filenameConvention(filename.toString())[0].equals("false")){
            System.err.println(FileValidator.filenameConvention(filename.toString())[1]);
            return;
        }
        //Check if filename is valid
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
            ArrayList<TableRow> content = new ArrayList<>();
            while ((fileLine = inputStream.readLine()) != null) {
                try{
                    //normalize file line to prevent unicode attacks
                    String normalizedFileLine = Normalizer.normalize(fileLine, Form.NFKC);
                    if (ValidateEmail.emailIsValid(normalizedFileLine)) {
                        System.out.println(normalizedFileLine);
                        content.add(new TableRow(normalizedFileLine, true));
                    } else {
                        System.out.println("Invalid Email Address: " + normalizedFileLine);
                        content.add(new TableRow(normalizedFileLine, false));
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            createHTMLFile(normalizedFilename, content);
        } catch (IOException io) {
            System.out.println("File IO exception: " + io.getMessage());
        }
    }

    /*
     * Create HTML file with email addresses and validity
     * @param filename The name of the file to be created
     * @param content The content of the file
     * @implNote This displays the email addresses in a table with the valid email addresses in green and the invalid email addresses in red
     */
    private static void createHTMLFile(String filename, ArrayList<TableRow> content) {
        String CSS_VAR = """
            <style>
                body{
                    background-color: hsl(240, 0%, 90%);
                    font-family: arial, sans-serif;
                    padding: 1rem;
                }
                table {
                    font-family: arial, sans-serif;
                    border-collapse: collapse;
                    width: 100%;
                    border-radius: 5px;
                    padding: 1rem;
                    background-color: white;
                }
                
                td, th {
                    border: 1px solid #dddddd;
                    text-align: left;
                    padding: 0.5rem;
                }
                
                tr:nth-child(even) {
                    background-color: #dddddd;
                }
                
                tr{
                    padding: 0.5rem;
                }
            </style>
        """;
        String htmlFilename = filename + ".html";
        StringBuilder html = new StringBuilder("<html><head>" + CSS_VAR + "<title>" + filename + "</title></head><body><table><thead><tr><th>Email</th><th>Validity</th></tr></thead><tbody>");
        for(TableRow line : content){
            html.append("<tr style=\"" + (line.valid ? "background-color: hsl(90, 80%, 80%)" : "background-color: hsl(0, 80%, 80%)") + "\"><td>").append(HTMLEntityEncode(line.email)).append("</td><td>").append(line.valid ? "Valid" : "Invalid").append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        try {
            FileWriter myWriter = new FileWriter(htmlFilename);
            myWriter.write(html.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the html file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }

    /* Properly Encode or escape output (IDS51-J) - Austin
     * This prevents XSS attacks and script injection
     */
    private static String HTMLEntityEncode(String input) {
        StringBuilder returnedString = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentCharacter = input.charAt(i);
            if (Character.isLetterOrDigit(currentCharacter) || Character.isWhitespace(currentCharacter)) {
                returnedString.append(currentCharacter);
            } else {
                returnedString.append("&#").append((int) currentCharacter).append(";");
            }
        }
        return returnedString.toString();
    }

}

/*
 * Class to hold email address and validity
 */
class TableRow {
    String email;
    boolean valid;
    public TableRow(String email, boolean valid) {
        this.email = email;
        this.valid = valid;
    }
}