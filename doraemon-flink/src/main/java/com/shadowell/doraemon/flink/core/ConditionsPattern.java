package com.shadowell.doraemon.flink.core;

import com.shadowell.doraemon.flink.entity.OperatorDescriptor;
import com.shadowell.doraemon.flink.entity.Quantifier;
import com.shadowell.doraemon.flink.entity.RuleUnit;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Author: Jie Feng
 * Date: Created in 2019/12/13
 */
public class ConditionsPattern {

    private static final Logger logger = LoggerFactory.getLogger(ConditionsPattern.class);


    /**
     * 处理规则中的条件
     * @param condition
     * @return
     * @throws Exception
     */
    private static boolean conditionProcess(OperatorDescriptor condition) throws Exception{
        double fieldValue = condition.getFieldValue();
        Comparators comparator = condition.getComparator();
        double threshold = condition.getThreshold();
        boolean result = false;
        switch (comparator) {
            case EQ:
                result = fieldValue == threshold;
                break;
            case GT:
                result = fieldValue > threshold;
                break;
            case GE:
                result = fieldValue >= threshold;
                break;
            case LT:
                result = fieldValue < threshold;
                break;
            case LE:
                result = fieldValue <= threshold;
                break;
            case NEQ:
                result = fieldValue != threshold;
                break;
            default:
                throw new Exception("Un support comparator: "  + comparator);
        }
        return result;
    }

    /**
     * 处理quantifier条件
     * @param pattern
     * @param quantifier
     * @return
     */
    private static Pattern quantifierPattern(Pattern pattern, Quantifier quantifier) throws Exception{
        Conditions quantifierType = quantifier.getType();
        int quantifierValue = quantifier.getValue();
        switch (quantifierType) {
            case TIMES:
                return pattern.times(quantifierValue);
            case ONE_OR_MORE:
                return pattern.oneOrMore();
            case TIMES_OR_MORE:
                return pattern.timesOrMore(quantifierValue);
            case GREEDY:
                return pattern.greedy();
            case OPTIONAL:
                return pattern.optional();
			default:
				throw new Exception("Un support quantifier: " + quantifierType);
        }
    }

    /**
     * 处理单个Pattern中的条件
     * @param pattern
     * @param rule
     * @return
     * @throws Exception
     */
    public static Pattern conditionsPattern(Pattern pattern, RuleUnit rule) throws Exception {
        List<OperatorDescriptor> conditions = rule.getConditions();
        Quantifier quantifier = rule.getQuantifier();
        List<String> joiners = rule.getJoiners();

        //需要校验condition和joiners的数量是否匹配， joiners.size() = condition.siz()-1
        if (conditions.size() - 1 != joiners.size()) throw new Exception("Joiner数量与conditions数量不匹配");
        for (int i = 1; i <= conditions.size(); i++) {
            switch (joiners.get(i)) {
                case "&&":
                    andConditionPattern(pattern, conditions.get(i));
                    break;
                case "||":
                    orConditionPattern(pattern, conditions.get(i));
                    break;
                default:
                    throw new Exception("Un support joiner: " + joiners.get(i));
            }
        }
        // 增加quantifier配置
        quantifierPattern(pattern, quantifier);
        return pattern;
    }

    /**
     * 处理And条件
     * @param pattern
     * @param condition
     * @return Pattern
     */
    @SuppressWarnings("unchecked")
    private static <T> Pattern andConditionPattern(Pattern pattern, OperatorDescriptor condition) {
        pattern.where(new IterativeCondition<T>() {
            @Override
            public boolean filter(T event, Context<T> ctx) throws Exception {
                return conditionProcess(condition);
            }
        });
        return pattern;
    }

    /**
     * 处理Or条件
     * @param pattern
     * @param condition
     * @return Pattern
     */
    @SuppressWarnings("unchecked")
    private static <T> Pattern orConditionPattern(Pattern pattern, OperatorDescriptor condition) {
        pattern.or(new IterativeCondition<T>() {
            @Override
            public boolean filter(T event, Context<T> ctx) throws Exception {
                return conditionProcess(condition);
            }
        });
        return pattern;
    }

    private ConditionsPattern() {}
}
