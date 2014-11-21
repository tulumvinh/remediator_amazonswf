package com.netflix.winston.remediator.worker.swf.greeting;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;
import com.netflix.winston.credentials.aws.IAMCredential;
import com.netflix.winston.remediator.activities.swf.greeting.GreetingActivity;
import com.netflix.winston.remediator.workflows.swf.greeting.GreeterWorkflowDeciderActor;

/*
 * Amazon SWF hello world application, see http://docs.aws.amazon.com/amazonswf/latest/awsflowguide/getting-started-example-helloworldworkflow.html
 * 
 * An "actor" to register activity and workflow with SWF.  For this example, this actor is a stand-alone application.
 * 
 * 
 */
public class GreeterRegistrationActorApplication {
	
	public static void main(String argv[]) {
		
		ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000); //max wait time of 70 secs for data to be transferred over an established connection before closing the socket
		
		//== set credentials to access SWF using on-instance credentials
		IAMCredential onInstanceCredentials = new IAMCredential();
		AWSCredentials awsCredentials = onInstanceCredentials.getCredentials();
		
		//== get handle to the SWF
		AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(awsCredentials, config);
		service.setEndpoint("https://swf.us-west-2.amazonaws.com");
		
		String domain = "winston_remeditator_poc";
		/*
		 * the name of the queue used by SQF.  This queue is used by the activity and workflow workers.
		 */
		String activityTasksToPoll = "HelloWorldWorkflowListFoo"; 
		
		/* == bind the worker to the activity
		 * This activity worker is a process or a thread that performs the activity tasks that are part of the workflow.  Multiple activity workers can process
		 * tasks of the same activity type.
		 * 
		 * 
		 * *Note: you can register the activity task programmatically or via AWS console.
		 * 
		 * This activity worker polls SWF for new tasks using the activity tasks "HelloWorldWorkflowList" that are appropriate for this worker to perform; certain tasks can be performed on ly be certain activity worker.
		 * After receiving a task, this activity worker processes the task to completion and then reports to SWF that the task was completed and the result.  This worker
		 * then polls for a new task.  This activity worker associated with a workflow execution continue in this way, processing tasks until the workflow
		 * execution itsef is complete.
		 * 
		 * The activity worker performs the various tasks the workflow has defined.  It consists of:
		 * - the activities implementation, which includes a set of activity methods that perform particular tasks for the workflow.
		 * 
		 * - SWF mediates the interaction between this activity and workflow workers.  SWF does not initiate communication with these workers; it waits for Http requests
		 * from these workers.  
		 * 
		 * This activity worker will poll SWF for activity tasks.  SWF responds directly to the activity worker when a task is available, sending the information
		 * required to perform the task.  This activity worker then calls the appropriate activity method, and returns results to SWF.  Specifically, once an activity task is
		 * completed, the activity worker notifies SWF.  SWF will then record this information in its execution history and adds a task to the "decision task list"
		 * and inform the workflow worker that the task is complete, allowing it to proceed to the next step.  
		 * 
		 * This approach allows the activity worker to run on any system with an internet connection, including EC2 instances, corporate data centers, client computers, etc..
		 * They don't even have to be running the same operating system.  Because the Http requests originate with this activity worker, there is no need for externally visible
		 * ports; this worker can run behind a firewall.
		 * 
		 */
		ActivityWorker aw = new ActivityWorker(service, domain, activityTasksToPoll);
		try {
			aw.addActivitiesImplementation(new GreetingActivity()); //register activity with SWF
		} catch (Exception e) {
			throw new RuntimeException("Error in binding activity workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		aw.start(); //start polling SWF activity tasks from the queue "HelloWorldWorkflowList"
		
		/* == bind the worker to the workflow
		 * The workflow worker orchestrates the execution of the various activities, manages data flow, and handles failed activities.  it consists of:
		 * 
		 * - the workflow implementation, which includes activity orchestration logic (via invoking methods within an acitivy), handles failed activities, and so on.
		 * SWF mediates the interaction between this workflow and activity worker.  SWF does not initiate communication with these workers; it waits for Http requests
		 * from these workers.
		 * 
		 * 
		 * This workflow worker will poll SWF for decision tasks.  SWF will respond once the task is complete to the workflow worker with the result.  The workflow
		 * worker will handle the result (e.g. if success, make a http request to SWF to execute the next activity.  If error, handle it base on your biz logic).  
		 * 
		 * This approach allows the activity worker to run on any system with an internet connection, including EC2 instances, corporate data centers, client computers, etc..
		 * They don't even have to be running the same operating system.  Because the Http requests originate with this activity worker, there is no need for externally visible
		 * ports; this worker can run behind a firewall.
		 *  
		 */
		final String decisionTasksToPoll = "HelloWorldDecisionTasksQueue";
		WorkflowWorker wfw = new WorkflowWorker(service, domain, activityTasksToPoll);
		try {
			wfw.addWorkflowImplementationType(GreeterWorkflowDeciderActor.class); //register workflow with SWF
		} catch (Exception e) {
			throw new RuntimeException("Error in binding workflow workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		wfw.start(); //start polling SWF for decision tasks within queue for the "helloWorldList"
		
		System.out.println("Completed: 1. binding workflow and its activities to its workders.  2. \"workflow\" and \"activity\" workers are now polling SWF for task list to execute.");
		
	}
	
}
