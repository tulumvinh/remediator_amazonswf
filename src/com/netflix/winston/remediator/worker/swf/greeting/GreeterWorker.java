package com.netflix.winston.remediator.worker.swf.greeting;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;
import com.netflix.winston.credentials.aws.IAMCredential;
import com.netflix.winston.remediator.activities.swf.greeting.GreetingActivity;
import com.netflix.winston.remediator.workflows.swf.greeting.GreeterWorkflow;

/*
 * Amazon SWF hello world application, see http://docs.aws.amazon.com/amazonswf/latest/awsflowguide/getting-started-example-helloworldworkflow.html
 * 
 * This worker's responsibilities include:
 * 
 * - handle communication between SWF and the activities and workflow implementatations by polling the SWF task list
 * - execute the appropriate method for each activity task
 * - managing the data flow
 * 
 */
public class GreeterWorker {
	
	public static void main(String argv[]) {
		
		ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000); //max wait time of 70 secs for data to be transferred over an established connection before closing the socket
		
		//== set credentials to access SWF using on-instance credentials
		IAMCredential onInstanceCredentials = new IAMCredential();
		AWSCredentials awsCredentials = onInstanceCredentials.getCredentials();
		
		//== get handle to the SWF
		AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(awsCredentials, config);
		service.setEndpoint("https://swf.us-west-2.amazonaws.com");
		
		String domain = "winston_remeditator_poc";
		String taskListToPoll = "HelloWorldWorkflowList"; //a task lists that SWF uses to manage communication between the woflow and activities workers.
		
		/* == bind the worker to the activity
		 * This worker will handle communication between SWF and the activities implementation by polling the appropriate SWF tasks lists for tasks,
		 * executing the appropriate method of each task, and managing the data flow.
		 */
		ActivityWorker aw = new ActivityWorker(service, domain, taskListToPoll);
		try {
			aw.addActivitiesImplementation(new GreetingActivity()); //register activty with SWF
		} catch (Exception e) {
			throw new RuntimeException("Error in binding activity workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		aw.start(); //start polling SWF for the "helloWorldList" task list.
		
		/* == bind the worker to the workflow
		 * This worker will handle communication between SWF and the workflow implementation by polling the appropriate SWF tasks lists for tasks,
		 * executing the appropriate method of each task, and managing the data flow. 
		 */
		WorkflowWorker wfw = new WorkflowWorker(service, domain, taskListToPoll);
		try {
			wfw.addWorkflowImplementationType(GreeterWorkflow.class); //register workflow with SWF
		} catch (Exception e) {
			throw new RuntimeException("Error in binding workflow workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		wfw.start(); //start polling SWF for the "helloWorldList" task list
		
		System.out.println("Completed: 1. register of workflow and its activities.  2. started \"workflow\" and \"activity\" workers to poll SWF for task list.");
		System.exit(0);		
		
	}
	
}
