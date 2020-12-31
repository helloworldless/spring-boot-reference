package com.davidagood.springbootreference;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Headers {

	private Map<String, String> headers = new HashMap<>();

	public void add(String name, String value) {
		this.headers.put(name, value);
	}

}
