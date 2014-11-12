package com.netflix.winston.reemediator.worker.swf.greeting;

import com.amazonaws.ClientConfiguration;

public class GreeterWorker {

	ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70*1000); //max wait time of 70 secs for data to be transferred over an established connection before closing the socket
}
