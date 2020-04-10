package com.paic.app.core;

import com.paic.app.entity.YamlParameters;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class YamlBuilder {

	private static YamlParameters prop = new YamlParameters();
	private static boolean isBuild = false;

	@SuppressWarnings("unchecked")
	public static YamlParameters build() throws IOException {
		Yaml yaml = new Yaml();
		InputStream in = YamlBuilder.class.getClassLoader().getResourceAsStream("engine.yaml");
		if (!isBuild) {
			prop = yaml.loadAs(in, YamlParameters.class);
			isBuild = true;
			return prop;
		}
		return prop;
	}

	public static YamlParameters build(String file) throws IOException {
		Yaml yaml = new Yaml();
		InputStream in = new FileInputStream(new File(file));
		if (!isBuild) {
			prop = yaml.loadAs(in, YamlParameters.class);
			isBuild = true;
		}
		return prop;
	}

	public static YamlParameters build(InputStream input) throws IOException {
		Yaml yaml = new Yaml();
		if (!isBuild) {
			prop = yaml.loadAs(input, YamlParameters.class);
			isBuild = true;
		}
		return prop;
	}

	private YamlBuilder() {}

}
