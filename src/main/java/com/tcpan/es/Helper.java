package com.tcpan.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Helper 
{
	public static ArrayList<String> getIndices(String host, int port)
	{
		String url = "http://" + host + ":" + port + "/_cat/indices";

		HttpClient httpClient = new HttpClient();
		//set timeout for connect server
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		//get request
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
        
        try {
        	int responseCode = httpClient.executeMethod(getMethod);
        	if (responseCode == HttpStatus.SC_OK) {
        		ArrayList<String> data = new ArrayList<String>();
        		BufferedReader reader = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), "UTF-8"));
        		String line = null;
        		while ((line = reader.readLine()) != null) {
        			String[] indexPart = line.split(" ");
        			if (indexPart.length >= 3) {
        				data.add(indexPart[2]);
        			}
        		}
        		reader.close();
        		return data;
        	}
        } catch (HttpException ex) {
        } catch (IOException ex) {
        }
        return null;
	}
}
