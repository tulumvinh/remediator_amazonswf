package com.netflix.winston.remediator.activities.swf.greeting;

public class GreetingActivity  implements IGreeterActivity {

	@Override
	public String getName() {
		return "World";
	}

	@Override
	public String getGreeting(String name) {
		return "Hello " + name;
	}

	@Override
	public void say(String what) {
		System.out.println(what);
	}
}
