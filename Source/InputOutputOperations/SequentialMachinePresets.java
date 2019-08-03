package InputOutputOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import GUI.ConversionTab;
import GUIListeners.ConversionTabListener;


/* Contains operations for saving/loading sequential machine module presets in the Conversion tab.
 * Files use the .sqmp extension (for sequential machine preset) */
public class SequentialMachinePresets {

	/* Loads the list of files containing .sqmp extension into the sequential machine preset list
	 * in the Conversion tab */
	public static void loadFileList(){
		ConversionTab.instance.SeqModulePresetBox.removeAllItems();
		ConversionTab.instance.SeqModulePresetBox.addItem("");
		
		File directoryHandle = new File("./");
		File[] fileList =directoryHandle.listFiles();
		for (int i=0;i<fileList.length;i++){
			String name = fileList[i].getName();
				if((!(name.length()<5)) && name.substring(name.length()-5).equals(".sqmp")){
					ConversionTab.instance.SeqModulePresetBox.addItem(name.substring(0,name.length()-5));
				}
		}
	}

	/* Loads the sequential machine module definition from the given .sqmp file 
	 * into the Conversion tab, then utilises existing parsing functionality 
	 * (see ParseSequentialMachine) to build object representation */
	public static boolean loadPreset(String name) {
		try {
			File fileHandle = new File(name+".sqmp");
			if (fileHandle.exists()){
				FileReader fileReader = new FileReader(fileHandle);
				BufferedReader bufferedReader=new BufferedReader(fileReader);
				
				String parsedString =bufferedReader.readLine();	
				String[] bothComponents = parsedString.split("\\Q?\\E");					
				ConversionTabListener.instance.loadSeq(bothComponents[0],bothComponents[1]);
				bufferedReader.close();
				fileReader.close();
			}
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/* Saves the present sequential machine definition to a new .sqmp file, in
	 * the format described in the above method */
	public static boolean savePreset(String name) {
		try {
			File fileHandle = new File(name+".sqmp");
			if(!fileHandle.exists()){
				fileHandle.createNewFile();
			}
			if (fileHandle.exists()){
				FileWriter fileWriter = new FileWriter(fileHandle);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

				String stringToPrint;
				stringToPrint=ConversionTab.instance.SeqTextArea.getText()+"?"+
				ConversionTab.instance.AFuncTextArea.getText();
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
