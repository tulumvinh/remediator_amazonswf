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
 * Amazon SWF hello world application, see http://docs.aws.amazon.com/amazonswf/latest/awsflowguide/getting-started-example-helloworldworkflow.html
 * 
 * A means to start the workflow execution.
 * 
 * 
 */
public class GreeterStarter {

	public static void main(String[] args) {
		ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000);
		
		//Fetch on-instance IAM credential
		IAMCredential onInstanceCredentials = new IAMCredential();
		AWSCredentials awsCredentials = onInstanceCredentials.getCredentials();
		
		//== get handle to the SWF
		AmazonSimpleWorkflow service = new AmazonSimpleWorkflowClient(awsCredentials, config);
		service.setEndpoint("https://swf.us-west-2.amazonaws.com");
		
		String domain = "winston_remeditator_poc";
		
		IGreeterWorkflowClientExternalFactory factory = new IGreeterWorkflowClientExternalFactoryImpl(service, domain);
		IGreeterWorkflowClientExternal greeter = factory.getClient("workflow_exec_1.01_1"); //get the proxy for the workflow
		greeter.greet(); //begin the workflow
		
		System.exit(0);
	}
}
