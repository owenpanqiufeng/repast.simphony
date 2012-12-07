package repast.simphony.statecharts;

import repast.simphony.parameter.Parameters;

public interface ConditionTriggerCondition<T> {
	public boolean condition(T agent, Transition<T> transition, Parameters params) throws Exception;
}
