package com.tcpan.es.component;

import org.slf4j.Logger;

public abstract class Component {
	protected String workpath;
	Component(String workpath) {
		this.workpath = workpath;
	}
	public Component init() throws Exception {
		return this;
	}
    public void start(Logger logger) {
    }
}