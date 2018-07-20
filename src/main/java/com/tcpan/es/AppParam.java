package com.tcpan.es;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

class AppParam 
{
	private String component;

	public static AppParam parseParam(String[] args) throws Exception {
		AppParam appParam = new AppParam();

		//parse params
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("c", "component", true, "Component for use.");
		CommandLine commandLine = parser.parse(options, args);

		//get params
		if (commandLine.hasOption("c")) {
			appParam.component = commandLine.getOptionValue("c");
		} else {
			throw new Exception("You should provide component name.");
		}

		return appParam; 
	}

	public String getComponent() {
		return component;
	}
}
