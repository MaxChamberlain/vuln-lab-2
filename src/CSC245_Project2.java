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
        if (filename == null || filename.isEmpty()) throw new IllegalArgumentException("No filename");
        //Normalize the filename to prevent unicode attacks
        String normalizedFilename = Normalizer.normalize(new File(filename).getCanonicalPath(), Normalizer.Form.NFKC);
        if(FileValidator.filenameConvention(filename.toString())[0].equals("false")){
            System.err.println(FileValidator.filenameConvention(filename.toString())[1]);
            return;
        }
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

class TableRow {
    String email;
    boolean valid;
    public TableRow(String email, boolean valid) {
        this.email = email;
        this.valid = valid;
    }
}