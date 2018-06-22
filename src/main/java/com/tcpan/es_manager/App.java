package com.tcpan.es_manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.*;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	try {
    		//parse params
    		CommandLineParser parser = new BasicParser();
    		Options options = new Options( );
    		options.addOption("h", "host", true, "ES service host.");
    		options.addOption("p", "port", true, "ES service port.");
    		options.addOption("d", "days", true, "Delete ES indeices which created [n] days ago.");
    		CommandLine commandLine = parser.parse(options, args);
    		
    		//get params
    		String esHost = "";
    		int esPort = 0;
    		int esDeleteDays = 0;
    		if (commandLine.hasOption("h")) {
    			esHost = commandLine.getOptionValue("h");
    		} else {
    			System.out.println("[Error] You should provide es host.");
    			return;
    		}
    		if (commandLine.hasOption("p")) {
    			esPort = Integer.parseInt(commandLine.getOptionValue("p"));
    		} else {
    			System.out.println("[Error] You should provide es port.");
    			return;
    		}
    		if (commandLine.hasOption("d")) {
    			esDeleteDays = Integer.parseInt(commandLine.getOptionValue("d"));
    		} else {
    			System.out.println("[Error] You should provide es port.");
    			return;
    		}
    		
    		//get all index
    		ArrayList<String> indeicesList = Helper.getIndices(esHost, esPort);
    		if (indeicesList == null || indeicesList.size() == 0) {
    			System.out.println("[Error] Could't get indeices");
    			return;
    		}

    		//find index need delete
    		Date nowDate = new Date();
    		ArrayList<String> needDeleteIndeices = new ArrayList<String>();
    		String pattern = "\\d{4}\\.\\d{2}\\.\\d{2}$";
    		Pattern regxPattern = Pattern.compile(pattern);
    		for (String index : indeicesList) {
    			//正则匹配如下类型的index
    			//logstash-2018.06.15 
    			//System.out.println(index);
    			Matcher matcheList = regxPattern.matcher(index);
    			if (matcheList.find()) {
    				//判断日期
    				Date date = (new SimpleDateFormat ("yyyy.MM.dd")).parse(matcheList.group(0));
    				long diffDays = (nowDate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24);
    				if (diffDays > esDeleteDays) {
    					needDeleteIndeices.add(index);
    				}
    			}
    		}

    		//es client
    		RestHighLevelClient esClient = new RestHighLevelClient(
    		    RestClient.builder(new HttpHost(esHost, esPort, "http"))
    		);
    		//do delete
    		for (String indexName : needDeleteIndeices) {
    			GetIndexRequest request = new GetIndexRequest();
    			request.indices(indexName);
    			if (esClient.indices().exists(request)) {
    				System.out.println("[Message] Index[" + indexName + "] found.");

    				DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
    				deleteRequest.timeout(TimeValue.timeValueSeconds(30));
    				DeleteIndexResponse deleteIndexResponse = esClient.indices().delete(deleteRequest);
    				if (deleteIndexResponse.isAcknowledged()) {
    					System.out.println("[Message] Delete index[" + indexName + "] success.");
    				} else {
    					System.out.println("[Message] Delete index[" + indexName + "] fail.");
    				}
    			} else {
    				System.out.println("[Message] Index[" + indexName + "] not found.");
    			}
    		}
    		
    		esClient.close();
    	} catch (Exception ex) {
    		System.out.println(ex.getMessage());
    		ex.printStackTrace();
    	}
    	//end
    	System.out.println("[Message] done!");
    }
}
