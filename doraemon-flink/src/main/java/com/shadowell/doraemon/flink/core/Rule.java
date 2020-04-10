package com.paic.app.core;

import com.paic.app.entity.OperatorDescriptor;
import com.paic.app.entity.Quantifier;
import com.paic.app.entity.RuleUnit;
import com.paic.app.sources.TiDBSource;
import com.paic.common.utils.JsonUtil;
import com.paic.app.config.FileConfig;
import com.paic.app.config.TiDBConfig;
import com.paic.app.entity.RulesEntity;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/18
 */
public class Rule {

	private static final Logger logger = LoggerFactory.getLogger(ConditionsPattern.class);

	/**
	 * 从TiDB加载规则
	 * @return
	 * @throws IOException
	 */
	public static Set<RulesEntity> loadRuleFromTiDB(TiDBConfig tiDBConfig) throws IOException {

		String sql = "select rule_detail from rule_dpp;";
		TiDBSource tiDBSource = new TiDBSource(tiDBConfig);
		Set<RulesEntity> rulesEntities = new HashSet<>();
		try {
			tiDBSource.connect();

			ResultSet resultSet = tiDBSource.execute(sql);
			while (resultSet.next()) {
				String result = resultSet.getString("rule_detail");
				RulesEntity rulesEntity = JsonUtil.modelMapper(result, RulesEntity.class);
				rulesEntities.add(rulesEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rulesEntities;
	}

	/**
	 * 从文件加载初始json规则
	 * @return
	 * @throws IOException
	 */
	public static RulesEntity loadRuleFromFile(FileConfig fileConfig) throws IOException {
		String path = fileConfig.getRuleFilePath() + "/" + fileConfig.getRuleFileName();
		File ruleFile = new File(path);
		if (!ruleFile.exists()) throw new IOException("No such rule file: " + path);
		return JsonUtil.modelMapper(ruleFile, RulesEntity.class);
	}

	public static RulesEntity load(InputStream in) throws Exception {
		return JsonUtil.modelMapper(in, RulesEntity.class);
	}

	public static Set<Pattern> build(Set<RulesEntity> rulesEntities) throws Exception {
		Set<Pattern> patterns = new HashSet<>();
		try {
			rulesEntities.forEach(rule -> {
				Pattern pattern = patternBuilder(rule);
				patterns.add(pattern);
			});
		}catch (Exception e) {
			e.printStackTrace();
		}
		return patterns;
	}

	/**
	 * 根据RuleEntity构建Pattern Sequence
	 * @param rule
	 * @return Pattern
	 * @throws Exception
	 */
	private static <T> Pattern<T, ?> patternBuilder(RulesEntity rule) {
		Pattern<T, ?> pattern = null;
		List<RuleUnit> ruleGroup = rule.getRules();
		Integer interval = rule.getEvent_interval();
		ruleGroup.forEach(ruleUnit -> {
			String ruleName = ruleUnit.getId();
			Operations ruleCombine = ruleUnit.getCombine();
			List<OperatorDescriptor> conditions = ruleUnit.getConditions();
			Quantifier quantifier = ruleUnit.getQuantifier();
			List<String> joiners = ruleUnit.getJoiners();
			conditions.forEach(condition -> {
				Pattern<T,?> start = Pattern.begin(Pattern.<T>begin(ruleName));
				try {
					switch (ruleCombine) {
						case BEGIN:
							ConditionsPattern.conditionsPattern(start, ruleUnit);
							break;
						case NEXT:
							Pattern<T, ?> next = start.next(ruleName);
							ConditionsPattern.conditionsPattern(next, ruleUnit);
							break;
//					case FOLLOWED_BY:
//						Pattern<T, ?> followedBy = start.followedBy(ruleName);
//						break;
//					case FOLLOWED_BY_ANY:
//						Pattern<T, ?> followedByAny = start.followedByAny(ruleName);
//						break;
//					case NOT_NEXT:
//						Pattern<T, ?> notNext = start.notNext(ruleName);
//						break;
//					case NOT_FOLLOWED_BY:
//						Pattern<T, ?> notFollowedBY = start.notFollowedBy(ruleName);
//						break;
//						case WITHIN:
//							Pattern<T, ?> withIn = next.within(Time.seconds(interval));
//							break;
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		return pattern;
	}

	private Rule() {}
}
