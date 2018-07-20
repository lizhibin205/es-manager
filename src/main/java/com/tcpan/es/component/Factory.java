package com.tcpan.es.component;

import java.lang.reflect.Constructor;

import com.tcpan.es.App;

public class Factory {
    public static Component getComponent(String componentName) {
    	try {
    		Class<?> componentClass = Class.forName("com.tcpan.es.component." + componentName);
    		Constructor<?> constructor = componentClass.getDeclaredConstructor(String.class);

    		return ((Component) constructor.newInstance(App.workpath)).init();
    	} catch (Exception ex) {
    		return null;
    	}
    }
}
