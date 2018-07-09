package com.tcpan.es;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

class AppParam 
{
	private String esHost;
	private int esPort;
	private int esDeleteDays;

	public static AppParam parseParam(String[] args) throws Exception
	{
		AppParam appParam = new AppParam();

		//parse params
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("h", "host", true, "Elasticsearch service host.");
		options.addOption("p", "port", true, "Elasticsearch service port.");
		options.addOption("d", "days", true, "Delete elasticsearch indeices which created [n] days ago.");
		CommandLine commandLine = parser.parse(options, args);
		
		//get params
		if (commandLine.hasOption("h")) {
			appParam.esHost = commandLine.getOptionValue("h");
		} else {
			throw new Exception("You should provide elasticsearch host.");
		}
		if (commandLine.hasOption("p")) {
			appParam.esPort = Integer.parseInt(commandLine.getOptionValue("p"));
		} else {
			throw new Exception("You should provide elasticsearch port.");
		}
		if (commandLine.hasOption("d")) {
			appParam.esDeleteDays = Integer.parseInt(commandLine.getOptionValue("d"));
		} else {
			throw new Exception("You should provide a number, use for delete elasticsearch indeices which created [n] days ago.");
		}

		return appParam; 
	}

	public String getEsHost()
	{
		return esHost;
	}

	public int getEsPort()
	{
		return esPort;
	}

	public int getEsDeleteDays()
	{
		return esDeleteDays;
	}
}
