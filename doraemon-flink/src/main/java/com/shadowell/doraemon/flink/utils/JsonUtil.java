package com.shadowell.doraemon.flink.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/13
 */
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> String jsonMapper(T model) {
        if (model == null) {
            return "";
        }
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T modelMapper(String s, Class<T> model) throws IOException {
        if (s.isEmpty()) {
            return null;
        }
        return mapper.readValue(s, model);
    }

    public static <T> T modelMapper(File file, Class<T> model) throws IOException {
        if (file == null) {
            return null;
        }
        return mapper.readValue(file, model);
    }

    public static <T> T modelMapper(InputStream input, Class<T> model) throws IOException {
    	return mapper.readValue(input, model);
	}
}
