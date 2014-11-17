package com.netflix.winston.credentials.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;

public class IAMCredential {

	private final InstanceProfileCredentialsProvider iamCredProvider;
	
    public IAMCredential()
    {
        this.iamCredProvider = new InstanceProfileCredentialsProvider();
    }	
    
    public String getAccessKeyId()
    {
        return iamCredProvider.getCredentials().getAWSAccessKeyId();
    }

    public String getSecretAccessKey()
    {
        return iamCredProvider.getCredentials().getAWSSecretKey();
    }

    public AWSCredentials getCredentials()
    {
        return iamCredProvider.getCredentials();
    }

	public AWSCredentialsProvider getAwsCredentialProvider() 
	{
		return iamCredProvider;
	}    	
	
}
