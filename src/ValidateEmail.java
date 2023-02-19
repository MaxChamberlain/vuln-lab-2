import java.util.regex.Pattern;


public class ValidateEmail {
	private static Pattern emailRegex = Pattern.compile("^[a-zA-Z0-9_.+-]{3,}@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]{2,}$");

	/**
	 * @param email A string of unknown email likeness
	 * @return TRUE if the email is valid according to most popular schemas, FALSE otherwise
	 * @implNote This is not entirely RFC5322 compliant. Hostnames (without TLDNs, .com etc) will fail.
	 */
	public static boolean emailIsValid(String email) {
		return emailRegex.matcher(email).matches();
	}
}
