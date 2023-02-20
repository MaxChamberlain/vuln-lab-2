import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class FileValidator {

	/* Sanitize Untrusted Data included in a regular expression (IDS08-J) - Max, Ronan
	 * WHITELISTED_PATH should be changed to whatever path the user is accessing during running for data file (e.g. Email_addresses_20210205.txt) - Max
	 * This method checks if the filename is valid
	 * @param filename The filename to be checked
	 * @return A FileValidationResult object containing the result of the validation
	 */

	private static final Pattern filenameRegex = Pattern.compile("^[a-zA-Z0-9\\\\/_\\-.:]+\\.txt$");
	public static FileValidationResult fileNameIsValid(Path filename) {
		filename = filename.normalize();
		// Check if file is a .txt file
		if(!filename.toString().endsWith(".txt"))
			return new FileValidationResult(false, "Invalid Filetype");
		// Check file name for invalid characters
		if (!filenameRegex.matcher(filename.toString()).matches())
			return new FileValidationResult(false, "Invalid Filename");

		//Validate Target Directory (assets)
		String currentDirectory = System.getProperty("user.dir");
		String assetsFolderPath = currentDirectory + File.separator + "assets";
		if (!filename.startsWith(assetsFolderPath))
				return new FileValidationResult(false, "Provided path outside of assets folder");

		// Check if file is too large
		try {
			if (isFileTooLarge(filename)) return new FileValidationResult(false, "File Too Large");
		} catch (IOException e) {
			return new FileValidationResult(false, e.getMessage());
		}

		return new FileValidationResult(true);
	}

	/* Buffer Overflow / Resource Exhaustion by file size - Jason
	 * This method checks if the file is too large
	 * @param filename The filename to be checked
	 * @return A boolean indicating if the file is too large
	 */
	private static boolean isFileTooLarge(Path filename) throws IOException {
		long fileSize = filename.toFile().length();
		long maxFileSize = 1000000; // 1 MB
		return fileSize > maxFileSize;
	}

	/* Use Conservative File Naming conventions (IDS50-J) - Nathan
	 * This method checks if the filename contains invalid naming conventions
	 * @param filename The filename to be checked
	 * @return A String array containing the result of the validation
	 */
	public static String[] filenameConvention(String filename) throws IllegalArgumentException {
		Pattern invalidCharPattern = Pattern.compile("^-.*|.*<script>.*|.*</script>.*|.*[\\n\\r\\x1b].*");
		if (invalidCharPattern.matcher(filename).matches())
			return new String[] { "false", "Invalid characters" };
		if (filename.matches(".*\\s.*")) {
			if (!filename.matches("^[\"'].+[\"']")) {
				return new String[] { "false", "Spaces must be in quotes" };
			}
		}
		return new String[] { "true", "Valid" };
	}
}

/* File Validation Result (RES) - Ronan
 * This class is used to return the result of a file validation
 */
class FileValidationResult {
	boolean okay;
	String msg;
	public FileValidationResult(boolean okay) {
		this.okay = okay;
		this.msg = "Okay";
	}
	public FileValidationResult(boolean okay, String msg) {
		this.okay = okay;
		this.msg = msg;
	}
}
