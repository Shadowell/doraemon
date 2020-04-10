package com.paic.app.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class ActionsPattern {

	private static final Logger logger = LoggerFactory.getLogger(ActionsPattern.class);

	public static <T> List<T> selectPattern(Map<String, List<T>> patterns, String patternName) {
		return patterns.get(patternName);
	}

	private ActionsPattern() {}
}
