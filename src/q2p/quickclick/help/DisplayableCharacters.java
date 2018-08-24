package q2p.quickclick.help;

public final class DisplayableCharacters {
	// TODO: диапазоны
	public static final String S_VALID_INLINE_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя _-+=\"'!@#$%^&*()[]{}<>:;/\\?,.|";
	public static boolean validInline(final CharSequence string) {
		for(int i = string.length() - 1; i != 0; i--)
			if(S_VALID_INLINE_CHARACTERS.indexOf(string.charAt(i)) == -1)
				return false;
		
		return true;
	}
}