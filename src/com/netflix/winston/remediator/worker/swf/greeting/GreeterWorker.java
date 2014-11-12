package com.netflix.winston.remediator.worker.swf.greeting;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;
import com.netflix.winston.remediator.activities.swf.greeting.GreetingActivity;

public class GreeterWorker {
	
	public static void main(String argv[]) {
		
		ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000); //max wait time of 70 secs for data to be transferred over an established connection before closing the socket
		
		//== get/set credentials to access SWF
		String swfAccessId = "ASIAJE5KE5RSAAP7DGMQ";
		String swfSecretKey = "55hktLC78ou0XRbhjzsVXOCjBI7LaNMAcWo19Tfl";
		AWSCredentials awsCredentials = new BasicAWSCredentials(swfAccessId, swfSecretKey);
		
		//== get handle to the SWF workflow
		AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(awsCredentials, config);
		service.setEndpoint("https://swf.us-east-1.amazonaws.com");
		
		//String domain = "helloWorldWalkthrough";
		String domain = "skynet_remeditator_demo";
		String taskListToPoll = "HelloWorldList"; //a task lists that SWF uses to manage communication between the woflow and activities workers.
		
		//== bind the worker to the activity
		ActivityWorker aw = new ActivityWorker(service, domain, taskListToPoll);
		try {
			aw.addActivitiesImplementation(new GreetingActivity()); //binding worker to activity
		} catch (Exception e) {
			throw new RuntimeException("Error in binding activity workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		aw.start(); //start polling SWF for the "helloWorldList" task list.
		
		//== bind the worker to the workflow
		WorkflowWorker wfw = new WorkflowWorker(service, domain, taskListToPoll);
		try {
			wfw.addWorkflowImplementationType(GreeterWorkflow.class); //binding workder to workflow
		} catch (Exception e) {
			throw new RuntimeException("Error in binding workflow workder.  Msg: " + e.getLocalizedMessage(), e);
		}
		wfw.start(); //start polling SWF for the "helloWorldList" task list		
		
	}
	
}
