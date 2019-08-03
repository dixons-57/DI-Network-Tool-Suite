package EnvironmentOperations;

import java.util.Vector;

import SetNotationStructure.SetNotationModule;

/* Corresponds to an uncertainty from the Environment chapter of the thesis. 
 * It contains a set of "limited" configurations */
public class Uncertainty {

	/* The set of configurations which makes up this uncertainty */
	private Vector<Configuration> contents = new Vector<Configuration>();
	
	/* Adds the given configuration to this uncertainty */
	public boolean addConfiguration(Configuration newConfiguration){
		if(!containsConfiguration(newConfiguration)){
			contents.add(newConfiguration);
			return true;
		}
		return false;
	}
	
	/* Checks whether this uncertainty contains the given configuration */
	public boolean containsConfiguration(Configuration checkConfiguration){
		for(int i=0;i<contents.size();i++){
			if(contents.get(i).equals(checkConfiguration)){
				return true;
			}
		}
		return false;
	}
	
	/* Checks if this uncertainty is equal to the given uncertainty. It does
	 * this by comparing the sets of configurations */
	public boolean equals(Uncertainty compare){
		if(compare.contents.size()!=contents.size()){
			return false;
		}
		else{
			for(int i=0;i<contents.size();i++){
				Configuration currentConfiguration = contents.get(i);
				boolean found=false;
				for(int j=0;j<compare.contents.size();j++){
					if(currentConfiguration.equals(compare.contents.get(j))){
						found=true;
						break;
					}
				}
				if(!found){
					return false;
				}
			}
		}
		return true;
	}
	
	/* Returns the number of configurations in this uncertainty */
	public int getNoOfConfiguration(){
		return contents.size();
	}
	
	/* Retrieves the configuration at the specified index */
	public Configuration getConfiguration(int index){
		return contents.get(index);
	}
	
	/* Prints this uncertainty */
	public String printConfigurationSet(SetNotationModule moduleDefinition){
		StringBuffer output = new StringBuffer();
		for(int i=0;i<contents.size();i++){
			output.append(contents.get(i).print(moduleDefinition));
			if(i<contents.size()-1){
				output.append(":");
			}
		}
		return output.toString();
	}
}
