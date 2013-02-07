package repast.simphony.statecharts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.PriorityType;
import repast.simphony.engine.schedule.ScheduleParameters;

public enum StateChartScheduler {

	INSTANCE;

	private final static long MAX_BEFOFE_CLEAR = 10000;
	protected Map<Double, ResolveActionsMapValue> resolveActions = new HashMap<Double, ResolveActionsMapValue>();

	protected Map<Double, BeginActionsMapValue> beginActions = new HashMap<Double, BeginActionsMapValue>();

	static class ResolveActionsMapValue {
		private StateChartResolveAction scra;
		private ISchedulableAction isa;
		private boolean remove = false;

		protected ResolveActionsMapValue(StateChartResolveAction scra,
				ISchedulableAction isa) {
			this.scra = scra;
			this.isa = isa;
		}

		protected void registerListener(DefaultStateChart<?> sc) {
			scra.registerListener(sc);
		}

		protected void removeListener(DefaultStateChart<?> sc) {
			scra.removeListener(sc);
			if (!scra.hasListeners()) {
				RunEnvironment.getInstance().getCurrentSchedule()
						.removeAction(isa);
				isa = null;
				scra = null;
				remove = true;
			}
		}
		
		protected void nullify(){
			scra.removeAllListeners();
			RunEnvironment.getInstance().getCurrentSchedule()
			.removeAction(isa);
			isa = null;
			scra = null;
			remove = true;
		}

		protected boolean toRemove() {
			return remove;
		}
	}
	
	static class BeginActionsMapValue {
		private StateChartBeginAction scba;
		private ISchedulableAction isa;

		protected BeginActionsMapValue(StateChartBeginAction scba, ISchedulableAction isa) {
			this.scba = scba;
			this.isa = isa;
		}

		protected void registerListener(DefaultStateChart<?> sc) {
			scba.registerListener(sc);
		}
		
		protected void nullify(){
			scba.removeAllListeners();
			RunEnvironment.getInstance().getCurrentSchedule()
			.removeAction(isa);
			isa = null;
			scba = null;
		}
	}

	public void initialize() {
		resolveClearCounter = 0;
		beginClearCounter = 0;

		// remove resolveActions from schedule
		for (ResolveActionsMapValue ramv : resolveActions.values()){
			ramv.nullify();
		}
		resolveActions.clear();
		
		for (BeginActionsMapValue bamv : beginActions.values()){
			bamv.nullify();
		}
		beginActions.clear();
	}

	long resolveClearCounter = 0;
	long beginClearCounter = 0;
	

	// called by StateChartResolveAction after notifying listeners
	// this allows for the rTime.compareTo(time) <= 0 expression
	// since the current time resolve actions have all been run
	protected void clearOldResolveActions() {
		resolveClearCounter++;
		if (resolveClearCounter > MAX_BEFOFE_CLEAR) {
			double time = RunEnvironment.getInstance().getCurrentSchedule()
					.getTickCount();
			List<Double> keysToRemove = new ArrayList<Double>();
			for (Double rTime : resolveActions.keySet()) {
				if (rTime.compareTo(time) <= 0)
					keysToRemove.add(rTime);
			}
			for (Double key : keysToRemove) {
				resolveActions.remove(key);
			}
			resolveClearCounter = 0;
		}
	}
	
	protected void clearOldBeginActions() {
		beginClearCounter++;
		if (resolveClearCounter > MAX_BEFOFE_CLEAR) {
			double time = RunEnvironment.getInstance().getCurrentSchedule()
					.getTickCount();
			List<Double> keysToRemove = new ArrayList<Double>();
			for (Double rTime : beginActions.keySet()) {
				if (rTime.compareTo(time) <= 0)
					keysToRemove.add(rTime);
			}
			for (Double key : keysToRemove) {
				beginActions.remove(key);
			}
			beginClearCounter = 0;
		}
	}


	protected void scheduleResolveTime(double nextTime, DefaultStateChart<?> sc) {
		ResolveActionsMapValue ramv = resolveActions.get(nextTime);
		if (ramv == null) {
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			StateChartResolveAction scra = new StateChartResolveAction();
			ISchedulableAction ia = schedule.schedule(ScheduleParameters
					.createOneTime(nextTime, PriorityType.FIRST_OF_LAST),
					scra);
			ramv = new ResolveActionsMapValue(scra, ia);
			resolveActions.put(nextTime, ramv);

		}
		ramv.registerListener(sc);
	}
	
	public void scheduleBeginTime(double nextTime, DefaultStateChart<?> sc) {
		double currentTickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (currentTickCount >= nextTime){
			nextTime = currentTickCount + nextTime;
		}
		BeginActionsMapValue bamv = beginActions.get(nextTime);
		if (bamv == null) {
			ISchedule schedule = RunEnvironment.getInstance()
					.getCurrentSchedule();
			StateChartBeginAction scba = new StateChartBeginAction();
			ISchedulableAction ia = schedule.schedule(ScheduleParameters
					.createOneTime(nextTime, PriorityType.FIRST),
					scba);
			bamv = new BeginActionsMapValue(scba, ia);
			beginActions.put(nextTime, bamv);
		}
		bamv.registerListener(sc);
	}

	protected void removeResolveTime(double nextTime, DefaultStateChart<?> sc) {
		if (resolveActions.containsKey(nextTime)) {
			ResolveActionsMapValue ramv = resolveActions.get(nextTime);
			ramv.removeListener(sc);
			if (ramv.toRemove())
				resolveActions.remove(nextTime);
		} else {
			throw new IllegalStateException(
					"Excess removeResolveTime call detected for StateChart: "
							+ sc);
		}
	}


}
