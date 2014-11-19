package com.netflix.winston.remediator.activities.swf.greeting;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

/*
 * Represents the steps (i.e. tasks) involve in the greeting workflow (i.e. saying "Hello World").
 * 
 * Each step (i.e. task) is performed by an activity method.
 *  
 * *Note: "activities" are independent of each other and can often be used by different workflows.  
 */


/*
 * 1. Configuraiton info for the activities behavior
 * 2. Direct AWS flow framework to use this interface definition to generate an "activities client class".
 */
@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 10) //specifies how long the activity task can be queue, set to 5 miutes.  Also, max time the activity can take to perform the task (10 secs)
@Activities(version="1.01") //allows us and AWS SWF to keep track different generations of this activity implementation
public interface IGreeterActivity {
	   public String getName();
	   public String getGreeting(String name);
	   public void say(String what);
}