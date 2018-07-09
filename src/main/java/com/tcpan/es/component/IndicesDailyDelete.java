package com.tcpan.es.component;

import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;

import com.tcpan.es.Helper;

public class IndicesDailyDelete 
{
	private Logger logger = null;

	public IndicesDailyDelete(Logger logger)
	{
		this.logger = logger;
	}

	public void start(String esHost, int esPort, int esDeleteDays)
	{
		//get all index
		ArrayList<String> indeicesList = Helper.getIndices(esHost, esPort);
		if (indeicesList == null || indeicesList.size() == 0) {
			logger.error("Could't get indeices list.");
			return;
		}
		logger.info("Indeices length: " + indeicesList.size() + ".");

		ArrayList<String> needDeleteIndeices = findNeedDeleteIndeices(indeicesList, esDeleteDays);
		logger.info("Need delete indeices length: " + needDeleteIndeices.size() + ".");

		//es client
		RestHighLevelClient esClient = new RestHighLevelClient(
		    RestClient.builder(new HttpHost(esHost, esPort, "http"))
		);
		//do delete
		try {
			for (String indexName : needDeleteIndeices) {
				GetIndexRequest request = new GetIndexRequest();
				request.indices(indexName);
				if (esClient.indices().exists(request)) {
					logger.info("Index[" + indexName + "] found.");
	
					DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
					deleteRequest.timeout(TimeValue.timeValueSeconds(30));
					DeleteIndexResponse deleteIndexResponse = esClient.indices().delete(deleteRequest);
					if (deleteIndexResponse.isAcknowledged()) {
						logger.info("Delete index[" + indexName + "] success.");
					} else {
						logger.info("Delete index[" + indexName + "] fail.");
					}
				} else {
					logger.error("Index[" + indexName + "] not found.");
				}
			}
			esClient.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}
	}

	private ArrayList<String> findNeedDeleteIndeices(ArrayList<String> indeicesList, int esDeleteDays)
	{
		Date nowDate = new Date();
		ArrayList<String> needDeleteIndeices = new ArrayList<String>();
		String pattern = "\\d{4}\\.\\d{2}\\.\\d{2}$";
		Pattern regxPattern = Pattern.compile(pattern);
		for (String index : indeicesList) {
			//正则匹配如下类型的index
			//logstash-2018.06.15
			Matcher matcheList = regxPattern.matcher(index);
			if (matcheList.find()) {
				//判断日期
				try {
					Date date = (new SimpleDateFormat ("yyyy.MM.dd")).parse(matcheList.group(0));
					long diffDays = (nowDate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24);
					if (diffDays > esDeleteDays) {
						needDeleteIndeices.add(index);
					}
				} catch (ParseException ex) {
					continue;
				}
			}
		}
		
		return needDeleteIndeices;
	}
}
