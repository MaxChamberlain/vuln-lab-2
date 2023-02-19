import java.util.regex.Pattern;

public class FileValidator {
	private static Pattern filenameRegex = Pattern.compile("^[a-zA-Z0-9_]+\\.[a-zA-Z]+$");
	public static boolean fileNameIsValid(String filename) {
		return filenameRegex.matcher(filename).matches();
	}
}
