package InputOutputOperations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import GUI.ConstructionTab;
import GUI.ConversionTab;
import GUI.EnvironmentTab;
import GUIListeners.ConstructionTabListener;
import GUIListeners.ConversionTabListener;
import GUIListeners.EnvironmentTabListener;

/* Contains operations for saving/loading Set Notation Module presets in the Conversion, 
 * Construction and Environment Generation tabs.
 * Files use the .snp extension (for set notation preset) */
public class SetNotationModulePresets {

	/* Loads the list of files containing .snp extension into the preset lists
	 * in the three tabs */
	public static void loadFileList(){
		EnvironmentTab.instance.PresetBox.removeAllItems();
		EnvironmentTab.instance.PresetBox.addItem("");
		ConstructionTab.instance.PresetBox.removeAllItems();
		ConstructionTab.instance.PresetBox.addItem("");
		ConversionTab.instance.SetNotationPresetBox.removeAllItems();
		ConversionTab.instance.SetNotationPresetBox.addItem("");

		File directoryHandle = new File("./");
		File[] fileList =directoryHandle.listFiles();
		for (int i=0;i<fileList.length;i++){
			String name = fileList[i].getName();
			if((!(name.length()<4)) && name.substring(name.length()-4).equals(".snp")){
				EnvironmentTab.instance.PresetBox.addItem(name.substring(0,name.length()-4));
				ConstructionTab.instance.PresetBox.addItem(name.substring(0,name.length()-4));
				ConversionTab.instance.SetNotationPresetBox.addItem(name.substring(0,name.length()-4));
			}
		}
	}

	/* Loads the module definition from the given .snp file into the appropriate tab's preset box, 
	 * then utilises existing parsing functionality (see ParseSetNotationModule) to build object
	 * representation */
	public static boolean loadPreset(String name, int screen) {
		try {
			File fileHandle = new File(name+".snp");
			if (fileHandle.exists()){
				FileReader fileReader = new FileReader(fileHandle);
				BufferedReader bufferedReader=new BufferedReader(fileReader);
				String parsedString =bufferedReader.readLine();
				if(screen==0){
					ConversionTabListener.instance.loadSet(parsedString);
				}
				else if(screen==1){
					ConstructionTabListener.instance.loadModule(parsedString);
				}
				else if(screen==2){
					EnvironmentTabListener.instance.loadModule(parsedString);
				}
				
				bufferedReader.close();
				fileReader.close();
			}
		} 
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/* Saves the module definition to a new .snp file */
	public static boolean savePreset(String definition, String name) {
		try {

			File fileHandle = new File(name+".snp");
			if(!fileHandle.exists()){
				fileHandle.createNewFile();
			}
			if (fileHandle.exists()){
				FileWriter fileWriter = new FileWriter(fileHandle);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
	
				definition=definition.replaceAll("\n", "");
				definition=definition.replaceAll("\r", "");

				bufferedWriter.write(definition);

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
