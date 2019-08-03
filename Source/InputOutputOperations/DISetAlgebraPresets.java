package InputOutputOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import GUI.ConsoleWindow;
import GUI.DISetAlgebraMainScreen;
import GUIListeners.DISetAlgebraMainListener;


/* Contains operations for saving/loading presets in the DI-Set Algebra tab's Main Screen.
 * Files use the .dsa extension (for DI-Set algebra) */
public class DISetAlgebraPresets {
	
	/* Loads the list of files containing .dsa extension into the preset list
	 * in the DI-Set Algebra tab's Main screen */
	public static void loadFileList(){
		String extension=".dsa";
		ConsoleWindow.output("Loading presets for DI-Set algebra");
		DISetAlgebraMainScreen.instance.Preset1Box.removeAllItems();
		DISetAlgebraMainScreen.instance.Preset1Box.addItem("");
		DISetAlgebraMainScreen.instance.Preset2Box.removeAllItems();
		DISetAlgebraMainScreen.instance.Preset2Box.addItem("");

		File directoryHandle = new File("./");
		File[] fileList =directoryHandle.listFiles();
		for (int i=0;i<fileList.length;i++){
			String name = fileList[i].getName();
				if((!(name.length()<4)) && name.substring(name.length()-4).equals(extension)){
					DISetAlgebraMainScreen.instance.Preset1Box.addItem(name.substring(0,name.length()-4));
					DISetAlgebraMainScreen.instance.Preset2Box.addItem(name.substring(0,name.length()-4));
				}
		}
	}
	
	/* Loads the network definition from the given .dsa file into the DI-Set Algebra tab Main screen, then utilises
	 * existing parsing functionality (see ParseDISetAlgebra) to build object representation */
	public static boolean loadPreset(String name, int network) {
		try {
			String extension=".dsa";
			
			File fileHandle = new File(name+extension);
			if (fileHandle.exists()){
				FileReader fileReader = new FileReader(fileHandle);
				BufferedReader bufferedReader=new BufferedReader(fileReader);
				String parsedString =bufferedReader.readLine();
				DISetAlgebraMainListener.instance.loadNetwork(parsedString,network);
				bufferedReader.close();
				fileReader.close();
			}
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/* Saves the given network's definition to a new .dsa file */
	public static boolean savePreset(String name, int network) {
		try {
			String extension=".dsa";
			
			File fileHandle = new File(name+extension);
			if(!fileHandle.exists()){
				fileHandle.createNewFile();
			}
			if (fileHandle.exists()){
				FileWriter fileWriter = new FileWriter(fileHandle);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

				String stringToPrint;
					
				if(network==1){
					stringToPrint=DISetAlgebraMainScreen.instance.Network1TextArea.getText();
				}
				else{
					stringToPrint=DISetAlgebraMainScreen.instance.Network2TextArea.getText();
				}

				stringToPrint=stringToPrint.replaceAll("\n", "");
				stringToPrint=stringToPrint.replaceAll("\r", "");
				
				bufferedWriter.write(stringToPrint);
					
				bufferedWriter.close();
				fileWriter.close();
			}
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
