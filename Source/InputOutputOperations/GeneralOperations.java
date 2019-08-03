package InputOutputOperations;

import java.util.Vector;

/* Contains general input/output relevant algorithms utilised throughout this
 * package */
public class GeneralOperations {

	/* Checks whether a given char is alphanumeric */
	public static boolean alphaNumeric(char input){
		if((input<'0' || input>'9') &&
				(input<'A' || input>'Z') &&
				(input<'a' || input>'z') &&
				(input!='\'')){
			return false;
		}
		return true;
	}
	
	/* Checks whether a given string contains only 
	 * alphanumberic chars (and optionally spaces also) */
	public static boolean alphaNumeric(String input, boolean spaces){
		boolean valid=true;
		for(int i=0;i<input.length();i++){
			if((input.charAt(i)<'0' || input.charAt(i)>'9') &&
					(input.charAt(i)<'A' || input.charAt(i)>'Z') &&
					(input.charAt(i)<'a' || input.charAt(i)>'z') &&
					(input.charAt(i)!='\'')){
				if(spaces){
					if(input.charAt(i)!=' '){
						valid=false;
						break;
					}
				}
				else{
					valid=false;
					break;
				}
			}
		}
		return valid;
	}
	
	/* Converts a vector of chars into a string */
	public static String buildString(Vector<Character> string){
		char[] tempCharArray = new char[string.size()];
		for(int i=0;i<string.size();i++){
			tempCharArray[i]=string.get(i).charValue();
		}
		return new String(tempCharArray);
	}
}
