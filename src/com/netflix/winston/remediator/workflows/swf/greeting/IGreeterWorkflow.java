package com.netflix.winston.remediator.workflows.swf.greeting;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

/*
 * Represents the "coordinator" of the "greeting" workflow.  Coordinator includes:
 * - Execute the activity tasks (i.e. methods) in sequence in the correct order.
 * - Execute each activity task with the correct data. 
 * 
 * This "coordinator" will execute the activity task in a linear workflow topology.  Specifically:
 * 
 * Call greeting workflow (start) --> getName -- name --> getGreeting -- greeting --> say  --> print greeting (finish workflow)
 */


/*
 * 1. Configuraiton info for the workflow
 * 2. Direct AWS flow framework to use this interface definition to generate an "workflow client class".
 */
@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 3600) //max time the workflow can run (1 hour)
public interface IGreeterWorkflow {

	@Execute(version = "1.01") //identifies the "entry / starting" point of workflow to Amazon SWF engine.
	public void greet();
}