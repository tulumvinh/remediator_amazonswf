package com.netflix.winston.remediator.starters.swf.greeting;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.netflix.winston.credentials.aws.IAMCredential;
import com.netflix.winston.remediator.workflows.swf.greeting.IGreeterWorkflowClientExternal;
import com.netflix.winston.remediator.workflows.swf.greeting.IGreeterWorkflowClientExternalFactory;
import com.netflix.winston.remediator.workflows.swf.greeting.IGreeterWorkflowClientExternalFactoryImpl;

/*
 * Amazon SWF hello world application, see http://docs.aws.amazon.com/amazonswf/latest/awsflowguide/getting-started-example-helloworldworkflow.html.
 * Also, see http://docs.aws.amazon.com/amazonswf/latest/developerguide/swf-dev-workflow-exec-lifecycle.html for a graphical explanation.
 * 
 * An "actor" to start the workflow execution, also referred to as "workflow instance".  For this example, this actor is a stand-alone application.
 * 
 * 
 */
public class GreeterStarterActorApplication {
	/*
	id of the running workflow execution.  Remediator will auto generate this id and persist it in data store each time it runs the workflow.  Better yet, SWF gives you
	a "runId" with each workflow execution (see http://docs.aws.amazon.com/amazonswf/latest/developerguide/swf-dev-about-workflows.html and search for literal "runId").
	*/
	private static final String WORKFLOW_EXECUTION_ID = "workflow_exec_1.01_2"; 

	public static void main(String[] args) {
		ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000);
		
		//Fetch on-instance IAM credential
		IAMCredential onInstanceCredentials = new IAMCredential();
		AWSCredentials awsCredentials = onInstanceCredentials.getCredentials();
		
		//== get handle to the SWF
		AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(awsCredentials, config);
		service.setEndpoint("https://swf.us-west-2.amazonaws.com");
		
		String domain = "winston_remeditator_poc";
		
		IGreeterWorkflowClientExternalFactory factory = new IGreeterWorkflowClientExternalFactoryImpl(service, domain); //handle to the workflow client
		IGreeterWorkflowClientExternal greeter = factory.getClient(WORKFLOW_EXECUTION_ID); //got the workflow client
		greeter.greet(); //begin the workflow
		
		System.out.println("Started workflox, execution id of \"" + WORKFLOW_EXECUTION_ID + "\", waiting for the worker and activity workers to execute the tasks.");
	}
}
