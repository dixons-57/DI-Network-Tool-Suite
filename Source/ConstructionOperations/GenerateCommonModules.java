package ConstructionOperations;

import java.util.Vector;
import CommonStructures.IntSet;
import SetNotationStructure.SetNotationModule;
import SetNotationStructure.SetTransition;

/* Generates common modules which are used in the construction of a Set Notation module */
public class GenerateCommonModules{

	/* Builds an arbitrary MxN Fork */
	public static ConstructionModule createFork(int M, int N){

		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();

		/* Name the module */ 
		String identifierName = new String(M+"x"+N+" Fork");
		
		/* Add input lines and output lines*/
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();
		for(int i=0;i<M;i++){
			outputPorts.add(new ModuleOutputLine("a"+i,i));
			internalSet.addOutputName("a"+i);
			for(int j=0;j<N;j++){
				inputPorts.add(new ModuleInputLine(("c"+i)+j,(i*N)+j));
				internalSet.addInputName(("c"+i)+j);
			}
		}
		for(int j=0;j<N;j++){
			outputPorts.add(new ModuleOutputLine("b"+j,M+j));
			internalSet.addOutputName("b"+j);
		}

		/* Build the (single) state and set of actions */
		internalSet.addStateName(M+"x"+N+"F");
		for(int i=0;i<M;i++){
			for(int j=0;j<N;j++){
				IntSet currentOutputSet = new IntSet();
				currentOutputSet.add(internalSet.getOutputIndex("a"+i));
				currentOutputSet.add(internalSet.getOutputIndex("b"+j));
				internalSet.addTransition(new SetTransition(0,new IntSet(internalSet.getInputIndex(("c"+i)+j)),
						0,currentOutputSet.deepCopy(),internalSet));
			}
		}
		
		/* Build the module and set the initial values of its special attributes */
		ConstructionModule forkModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		forkModule.addAttribute(M);
		forkModule.addAttribute(0);
		forkModule.addAttribute(0);
		return forkModule;
	}

	/* Builds an arbitrary n-way Fork tree (as a single atomic module) */
	public static ConstructionModule createForkTree(int n){
		
		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();
		
		/* Name the module */ 
		String identifierName = new String(n+"-way Fork");
		
		/* Add input lines and output lines*/
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();
		inputPorts.add(new ModuleInputLine("I1",0));
		internalSet.addInputName("I1");
		for(int i=1;i<=n;i++){
			outputPorts.add(new ModuleOutputLine("O"+i,i-1));
			internalSet.addOutputName(new String("O"+i));
		}
		
		/* Build the (single) state and single action */
		internalSet.addStateName(n+"wF");
		IntSet outputSet = new IntSet();
		for(int i=1;i<=n;i++){
			outputSet.add(i-1);
		}
		internalSet.addTransition(new SetTransition(0,new IntSet(0),0,outputSet.deepCopy(),internalSet));

		/* Build the module and set the initial value of its special attribute */
		ConstructionModule forkModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		forkModule.addAttribute(0);

		return forkModule;
	}

	/* Builds an arbitrary n-way Choice tree (as a single atomic module) */
	public static ConstructionModule createChoiceTree(int n){
		
		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();
		
		/* Name the module */ 
		String identifierName = new String(n+"-way Choice");
		
		/* Add input lines and output lines*/
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();
		inputPorts.add(new ModuleInputLine("I1",0));
		internalSet.addInputName("I1");
		for(int i=1;i<=n;i++){
			outputPorts.add(new ModuleOutputLine("O"+i,i-1));
			internalSet.addOutputName("O"+i);
		}
		
		/* Build the (single) state and set of actions */
		internalSet.addStateName(n+"wC");
		for(int i=1;i<=n;i++){
			internalSet.addTransition(new SetTransition(0,new IntSet(0),0,new IntSet(i-1),internalSet));
		}

		/* Build the module and set the initial value of its special attribute */
		ConstructionModule choiceModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		choiceModule.addAttribute(0);

		return choiceModule;
	}

	/* Builds an arbitrary MxN Join */
	public static ConstructionModule createJoin(int xDimension, int yDimension){

		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();

		/* Name the module */
		String identifierName = new String(xDimension+"x"+yDimension+" Join");
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();

		/* Add input lines and output lines*/
		for(int i=0;i<xDimension;i++){
			inputPorts.add(new ModuleInputLine("a"+i,i));
			internalSet.addInputName("a"+i);
			for(int j=0;j<yDimension;j++){
				outputPorts.add(new ModuleOutputLine(("c"+i)+j,(i*yDimension)+j));
				internalSet.addOutputName(("c"+i)+j);
			}
		}
		for(int j=0;j<yDimension;j++){
			inputPorts.add(new ModuleInputLine("b"+j,xDimension+j));
			internalSet.addInputName("b"+j);
		}

		/* Build the (single) state and set of actions */
		internalSet.addStateName(xDimension+"x"+yDimension+"J");
		for(int i=0;i<xDimension;i++){
			for(int j=0;j<yDimension;j++){
				IntSet setInputSet = new IntSet();
				IntSet setSingletonOutputSet = new IntSet();
				setInputSet.add(i);
				setInputSet.add(xDimension+j);
				setSingletonOutputSet.add(internalSet.getOutputIndex(("c"+i)+j));
				internalSet.addTransition(new SetTransition(0,setInputSet,0,setSingletonOutputSet,internalSet));
			}
		}

		/* Build the module and set the initial values of its special attributes */
		ConstructionModule joinModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		joinModule.addAttribute(xDimension);
		joinModule.addAttribute(0);
		joinModule.addAttribute(0);

		return joinModule;
	}

	/* Builds an arbitrary n-way Join tree (as a single atomic module) */
	public static ConstructionModule createJoinTree(int n){
		
		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();

		/* Name the module */
		String identifierName = new String(n +"-way Join");
		
		/* Add input lines and output lines*/
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();
		outputPorts.add(new ModuleOutputLine("O1",0));
		internalSet.addOutputName("O1");
		for(int i=1;i<=n;i++){
			inputPorts.add(new ModuleInputLine("I"+i,i-1));
			internalSet.addInputName("I"+i);
		}
		
		/* Build the (single) state and single action */
		internalSet.addStateName(n+"wJ");
		IntSet setInputSet = new IntSet();
		for(int i=0;i<n;i++){
			setInputSet.add(i);
		}
		IntSet setOutputSet = new IntSet();
		setOutputSet.add(0);
		internalSet.addTransition(new SetTransition(0,setInputSet,0,setOutputSet,internalSet));
		
		/* Build the module and set the initial values of its special attributes */
		ConstructionModule joinTreeModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		joinTreeModule.addAttribute(0);
		return joinTreeModule;
	}

	/* Builds an arbitrary n-way Merge tree (as a single atomic module) */
	public static ConstructionModule createMergeTree(int n){
		
		/* The internal Set Notation module definition to build */
		SetNotationModule internalSet = new SetNotationModule();
		
		/* Name the module */
		String identifierName = new String(n+"-way Merge");
		
		/* Add input lines and output lines*/
		Vector<ModuleInputLine> inputPorts = new Vector<ModuleInputLine>();
		Vector<ModuleOutputLine> outputPorts = new Vector<ModuleOutputLine>();
		outputPorts.add(new ModuleOutputLine("O1",0));
		internalSet.addOutputName("O1");
		for(int i=1;i<=n;i++){
			inputPorts.add(new ModuleInputLine("I"+i,i-1));
			internalSet.addInputName("I"+i);
		}
		
		/* Build the (single) state and set of actions */
		internalSet.addStateName(n+"wM");
		for(int i=1;i<=n;i++){
			IntSet singletonOutputSet = new IntSet();
			singletonOutputSet.add(0);
			internalSet.addTransition(new SetTransition(0,new IntSet(i-1),0,new IntSet(0),internalSet));
		}
		
		/* Build the module and set the initial values of its special attributes */
		ConstructionModule mergeModule = new ConstructionModule(identifierName,inputPorts,outputPorts,internalSet);
		mergeModule.addAttribute(0);
		return mergeModule;
	}
}