package DISetAlgebraOperations;

import java.util.Vector;

import DISetAlgebraLTSStructure.LTSState;
import DISetAlgebraLTSStructure.Simulation;
import DISetAlgebraLTSStructure.SimulationPair;
import DISetAlgebraLTSStructure.Transition;
import DISetAlgebraStructure.NamedPortSet;
import GUI.ConsoleWindow;

/* This verifies a (bi)simulation defined by the user in the DI-Set Algebra tab's LTS screen. Unlike the theoretical basis of the relation
 * defined in the thesis, when defining state pairs, the software requires that the left network (also known as Network 1) is always on the left side
 * of the relation, and right network (also known as Network 2) is always on the right side of the relation. Hence (s,s) is the format of all pairs,
 * where s is from the left LTS/network, and s' is from the right LTS/network. This is simpler logistically from a programming
 * point of view, but also is due to the fact that in the case of defining a bisimulation, it makes more sense to define once in a fixed the order of components,
 * rather than having to also define its symmetric counterpart. */
public class SimulationVerification {
	
	/* Checks whether the given simulation is valid. If simulationType is 1 then we are checking bisimilarity. If simulationType is 2
	 * then we are checking whether network 1 simulates network 2, and if simulationType is 3 then we are checking whether network 2 
	 * simulates network 1. It iterates through each state pair in the relation and checks that the conditions are met, by utilising
	 * recursive methods below which check for the existence of paths of tau transitions (known as tau* paths). See the ConsoleWindow.output calls
	 * below for more information as to what operations are performed at each point */
	public static boolean validSimulation(Simulation simulationDefinition, int simulationType){
		if(simulationType==1){
			ConsoleWindow.output("Verifying bisimulation");
		}
		else if(simulationType==2){
			ConsoleWindow.output("Verifying network 1 simulates network 2");
		}
		else if(simulationType==3){
			ConsoleWindow.output("Verifying network 2 simulates network 1");
		}
		for(int i=0;i<simulationDefinition.getNoOfPairs();i++){
			SimulationPair currentPair = simulationDefinition.getPair(i);
			ConsoleWindow.output("Checking that pair "+currentPair.printPair()+" satisfies conditions");
			
			LTSState leftLTSState = currentPair.getLeftState();
			LTSState rightLTSState = currentPair.getRightState();
			
			if(simulationType==1 || simulationType==3){
				
				/* Check all of left side moves are matched by the right */
				for(int j=0;j<leftLTSState.getNoOfOutgoing();j++){
					Transition leftTransition=leftLTSState.getOutgoingTransition(j);
					
					ConsoleWindow.output("Checking that left side transition "+leftTransition.printArrow()+" "+leftLTSState.getOutgoingState(j).getStateNo() + " is matched "+
							"by the right side");
					
					/* If the transition on the left is a tau */
					if(leftTransition.getType()>1){
						
						ConsoleWindow.output("Left side transition is a tau so checking for a tau* path on the right side");
						
						/* See if the tau on the left and a (possibly empty) 
						 * series of taus on the right leads to a state in the relation */
						if(!checkTauPath(leftLTSState.getOutgoingState(j).getStateNo(),rightLTSState,simulationDefinition,new Vector<Integer>(),true)){
							ConsoleWindow.output("Right side can not match with a resulting state in the simulation");
							return false;
						}
					}
					
					/* If the transition on the left is not a tau */
					else{
							ConsoleWindow.output("Left side transition is visible so checking for a tau* path followed by a visible transition, "
									+ "followed by a tau* path on the right side");
						if(!checkTauPathThenVisibleThenTauPath(leftLTSState.getOutgoingState(j).getStateNo(),rightLTSState,
								leftTransition.getType(),leftTransition.getTransitionLabel(),simulationDefinition,new Vector<Integer>(),true)){
							ConsoleWindow.output("Right side can not match with a resulting state in the simulation");
							return false;
						}						
					}
				}
				ConsoleWindow.output("All moves on the left here are matched by the right side");
			}
			if(simulationType==1 || simulationType==2){
				
				/* Check all of right side moves are matched by the left */
				for(int j=0;j<rightLTSState.getNoOfOutgoing();j++){
					Transition rightTransition=rightLTSState.getOutgoingTransition(j);
					
					ConsoleWindow.output("Checking that right side transition "+rightTransition.printArrow()+" "+rightLTSState.getOutgoingState(j).getStateNo() + " is matched "+
							"by the left side");
					
					/* If the transition on the right is a tau */
					if(rightTransition.getType()>1){
						
						ConsoleWindow.output("Right side transition is a tau so checking for a tau* path on the left side");
						
						/* See if the tau on the right and a (possibly empty) 
						 * series of taus on the left leads to a state in the relation */
						if(!checkTauPath(rightLTSState.getOutgoingState(j).getStateNo(),leftLTSState,simulationDefinition,new Vector<Integer>(),false)){
							ConsoleWindow.output("Left side can not match with a resulting state in the simulation");
							return false;
						}
					}
					
					/* If the transition on the right is not a tau */
					else{
							ConsoleWindow.output("Right side transition is visible so checking for a tau* path followed by a visible transition, "
									+ "followed by a tau* path on the left side");
						if(!checkTauPathThenVisibleThenTauPath(rightLTSState.getOutgoingState(j).getStateNo(),leftLTSState,
								rightTransition.getType(),rightTransition.getTransitionLabel(),simulationDefinition,new Vector<Integer>(),false)){

							ConsoleWindow.output("Left side can not match with a resulting state in the simulation");
							return false;
						}
					}
				}
				ConsoleWindow.output("All moves on the right here are matched by the left side");
			}
		}
		return true;
	}

	/* Checks whether there is a *tau path from stateToEvolve to some numbered randomState such that
	 * (fixedState,randomState) is in the (formal) relation if rightMatching=true, or (randomState,fixedState) if rightMatching=false 
	 * Note as described above, it is always represented as (fixedState, randomState) in the GUI */
	private static boolean checkTauPath(int fixedState, LTSState stateToEvolve, Simulation simulationDefinition, 
			Vector<Integer> statesVisited, boolean rightMatchingLeft) {
		
		/* First check if this state is actually in the relation, before bothering to recurse */
		if(rightMatchingLeft){
			if(simulationDefinition.pairPresent(fixedState,stateToEvolve.getStateNo())){
				return true;
			}
		}
		else{
			if(simulationDefinition.pairPresent(stateToEvolve.getStateNo(),fixedState)){
				return true;
			}
		}
		
		/* Record current stateToEvolve as visited */
		statesVisited.addElement(new Integer(stateToEvolve.getStateNo()));
		
		/* For every transition from stateToEvolve */
		for(int i=0;i<stateToEvolve.getNoOfOutgoing();i++){
			Transition currentTransition = stateToEvolve.getOutgoingTransition(i);
			
			/* We only care if it is a tau */
			if(currentTransition.getType()>1){
				
				/* We only care about the target state if we have not 
				 * already visited it (prevents infinite recursion)*/
				if(!LTSGeneration.visited(stateToEvolve.getOutgoingState(i).getStateNo(),statesVisited)){
					
					/* Recursively check to see if there is a path from this target state, otherwise abandon
					 * and try a new transition from stateToEvolve */
					if (checkTauPath(fixedState,stateToEvolve.getOutgoingState(i),simulationDefinition,statesVisited,rightMatchingLeft)){
						return true;
					}
				}
			}
		}
		
		/* If we haven't found a path from here yet then there is not one from this state, so go back up the stack and try a different "route" */
		return false;
	}
	
	/* Utilises the above recursive method, and recursively checks whether there is a tau* path, then a visible transition (which matches visibleLabel), then a tau* path
	 * from stateToEvolve to some numbered randomState such that (fixedState,randomState) is in the (formal) relation if rightMatching=true, 
	 * or (randomState,fixedState) if rightMatching=false. Note as described above, it is always represented as (fixedState, randomState) in the GUI */
	private static boolean checkTauPathThenVisibleThenTauPath(int fixedState, LTSState stateToEvolve, int transitionType,
			NamedPortSet visibleLabel, Simulation simulationDefinition, Vector<Integer> statesVisited, boolean rightMatchingLeft) {
		
		/* Record current stateToEvolve as visited */
		statesVisited.addElement(new Integer(stateToEvolve.getStateNo()));
		
		/* For every transition from stateToEvolve */
		for(int i=0;i<stateToEvolve.getNoOfOutgoing();i++){
			Transition currentTransition = stateToEvolve.getOutgoingTransition(i);
			
			/* Try not doing any more tau movements, but checking for visible transition, then
			 * checking for a tau* path following afterwards */
				
				/* If visible transition type matches then try to match visible label */
				if(currentTransition.getType()==transitionType){
					
					/* If visible label matches in a transition starting from stateToEvolve! */
					if(currentTransition.getTransitionLabel().sameAs(visibleLabel)){				
							
							/* See if there is a (possibly empty) tau* path from the target state (after visible transition)
							 * to a state newState such that (fixedState,newState) is in the relation (or vice versa if rightMatchingLeft=false) */
							if(checkTauPath(fixedState,stateToEvolve.getOutgoingState(i),simulationDefinition,new Vector<Integer>(),rightMatchingLeft)){
								return true;
							}
					}
				}
				
			/* Otherwise search some more possible states to perform the visible action from by following a single tau movement and recursing this method */
			else if(currentTransition.getType()>1){
				
				/* We only care about the target state of this tau transition if we have not already visited it (prevents infinite recursion)*/
				if(!LTSGeneration.visited(stateToEvolve.getOutgoingState(i).getStateNo(),statesVisited)){
					
					/* Return true if there is a path from this target state, otherwise abandon and try a new transition (visible or tau) from stateToEvolve */
					if (checkTauPathThenVisibleThenTauPath(fixedState,stateToEvolve.getOutgoingState(i),transitionType,visibleLabel,
							simulationDefinition,statesVisited,rightMatchingLeft)){
						return true;
					}
				}
			}
		}
		
		/* If we haven't found a path from here yet then there is not one from this state, so go back up the stack and try a different "route" */
		return false;
	}
	
}
