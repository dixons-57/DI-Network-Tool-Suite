package InputOutputOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import GUI.DISetAlgebraLTSScreen;
import GUIListeners.DISetAlgebraLTSListener;

/* Contains operations for saving/loading (bi)simulation presets in the DI-Set Algebra tab's LTS screen.
 * Files use the .bsp extension (for (bi)simulation preset) */
public class SimulationPresets {

	/* Loads the list of files containing .bsp extension into the preset list
	 * in the LTS screen */
	public static void loadFileList(){
		DISetAlgebraLTSScreen.instance.PresetBox.removeAllItems();
		DISetAlgebraLTSScreen.instance.PresetBox.addItem("");
		
		File directoryHandle = new File("./");
		File[] fileList =directoryHandle.listFiles();
		for (int i=0;i<fileList.length;i++){
			String name = fileList[i].getName();
				if((!(name.length()<4)) && name.substring(name.length()-4).equals(".bsp")){
					DISetAlgebraLTSScreen.instance.PresetBox.addItem(name.substring(0,name.length()-4));
				}
		}
	}
	
	/* Loads the (bi)simulation from the given .bsp file into the LTS screen, 
	 * then utilises existing parsing functionality (see ParseDISetAlgebraSimulation) to build object
	 * representation */
	public static boolean loadPreset(String name) {
		try {
			File fileHandle = new File(name+".bsp");
			if (fileHandle.exists()){
				FileReader fileReader = new FileReader(fileHandle);
				BufferedReader bufferedReader=new BufferedReader(fileReader);
				String parsedString =bufferedReader.readLine();	
				
				DISetAlgebraLTSListener.instance.loadSimulation(parsedString);

				bufferedReader.close();
				fileReader.close();
			}
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/* Saves the (bi)simulation definition to a new .bsp file */
	public static boolean savePreset(String input) {
		try {
			File fileHandle = new File(input+".bsp");
			if(!fileHandle.exists()){
				fileHandle.createNewFile();
			}
			if (fileHandle.exists()){
				FileWriter fileWriter = new FileWriter(fileHandle);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

				String stringToPrint;
				stringToPrint=DISetAlgebraLTSScreen.instance.SimulationTextArea.getText();		
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
