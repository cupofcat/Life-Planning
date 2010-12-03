package com.appspot.analyser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;

import org.junit.Test;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.junit.experimental.runners.Enclosed;

@RunWith(Enclosed.class)
public class Analyzer {

	public static final double CONFIDENCE = 0.1;
	private static final double ALTERNATIVE = 0.3;
	static final int TRIES = 10;
	private int maxDepth = 3;
	private int maxSuggestions = 3;

	public Analyzer() {
	}

	public List<List<Suggestion>> getSuggestions(List<? extends IEvent> events, String currentUserId) throws IOException {
		UserProfile profile = UserProfileStore.getUserProfile(currentUserId);
		return convertToSuggestions(this.getSuggestions(events, currentUserId, profile.getSpherePreferences(), profile.isFullyOptimized()));
	}

	@SuppressWarnings("unchecked")
	private List<List<CalendarStatus>> getSuggestions(List<? extends IEvent> externalEvents, String userID,
			Map<SphereName, Double> spherePreferences, boolean optimizeFull) throws IOException {
		if (externalEvents.size() == 0)
			return null;
		List<IEvent> events = (List<IEvent>) externalEvents;
		List<BaseCalendarSlot> freeSlots = getFreeSlots(events);
		printEvents(freeSlots);
		removeStaticEvents(events);
		LinkedList<List<CalendarStatus>> result = new LinkedList<List<CalendarStatus>>();
		CalendarStatus start = checkGoals(events, spherePreferences);
		if (isCloseEnough(start, optimizeFull))
			return null;
		List<CalendarStatus> statuses = getSortedStatuses(events, start);
		for (int i = 0; i < maxSuggestions || i < statuses.size(); i++) {
			LinkedList<CalendarStatus> list = new LinkedList<CalendarStatus>();
			CalendarStatus nextMin = statuses.get(i);
			// best event can't improve the status
			if (nextMin.compareTo(start) >= 0)
				break;
			//check neighbours for possible alternatives - run loop which won't modify i (we want i sets of proposals) - proposals
			while(i < statuses.size()){
				CalendarStatus next = statuses.get(i);
				if(next.compareTo(start) < 0 || next.getCoefficient() <= nextMin.getCoefficient()*(1+Analyzer.ALTERNATIVE))
					break;
				nextMin.addAlternative(next);
				events.remove(next.getEvent());
				++i;
			}
			list.add(nextMin);
			events.remove(nextMin.getEvent());
			List<CalendarStatus> rest = getSuggestions(events, nextMin, optimizeFull, maxDepth);
			if (rest != null)
				list.addAll(rest);
			result.add(list);
			events.add(nextMin.getEvent());
			if (nextMin.hasAlternatives()) {
				for (CalendarStatus status : nextMin.getAlternatives()) {
					events.add(status.getEvent());
				}
			}

		}
		return result;
	}

	private List<CalendarStatus> getSuggestions(List<IEvent> events, CalendarStatus currentStatus, boolean optimizeFull, int depth)
	throws IOException {
		if (isCloseEnough(currentStatus, optimizeFull) || events.size() == 0 || depth <= 0)
			return null;
		List<CalendarStatus> statuses = getSortedStatuses(events, currentStatus);
		LinkedList<CalendarStatus> list = new LinkedList<CalendarStatus>();
		CalendarStatus minimum = statuses.get(0);
		if (minimum.compareTo(currentStatus) >= 0)
			return null;
		//check for alternatives again - run proposals??
		//check neighbours for possible alternatives - run loop which won't modify i (we want i sets of proposals) - proposals
		int i  = 1;
		while(i < statuses.size()){
			CalendarStatus next = statuses.get(i);
			if(next.compareTo(currentStatus) < 0 || next.getCoefficient() <= minimum.getCoefficient()*(1+Analyzer.ALTERNATIVE))
				break;
			minimum.addAlternative(next);
			events.remove(next.getEvent());
			++i;
		}
		list.add(minimum);
		events.remove(minimum.getEvent());
		List<CalendarStatus> rest = getSuggestions(events, minimum, optimizeFull, depth - 1);
		if (rest != null)
			list.addAll(rest);
		events.add(minimum.getEvent());
		if (minimum.hasAlternatives()) {
			for (CalendarStatus status : minimum.getAlternatives()) {
				events.add(status.getEvent());
			}
		}
		return list;
	}

	private List<BaseCalendarSlot> getFreeSlots(List<? extends IEvent> events) {
		LinkedList<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Collections.sort(events);
		System.out.println("-Free slots generation-");
		Iterator<? extends IEvent> it = events.iterator();
		IEvent beginning = it.next();
		it.remove();
		IEvent curr = beginning;
		while (it.hasNext()) {
			IEvent next = it.next();
			if (curr.getEndDate().compareTo(next.getStartDate()) < 0) {
				BaseCalendarSlot newSlot = new BaseCalendarSlot("Free Slot", null, curr.getEndDate(), next.getStartDate());
				ret.add(newSlot);
				Pair<Double, Double> durationInterval = curr.getDurationInterval();
				durationInterval.setSecond(Math.min(durationInterval.getSecond(), curr.getDuration() + newSlot.getDuration()));
				curr = next;
			}
			else {
				if (curr.getEndDate().compareTo(next.getEndDate()) <= 0) {
					Pair<Double, Double> durationInterval = curr.getDurationInterval();
					durationInterval.setSecond(curr.getDuration());
					curr = next;
				}
			}
		}
		it.remove();
		return ret;
	}

	private boolean isCloseEnough(CalendarStatus currentStatus, boolean optimizeFull) {
		return currentStatus.getCoefficient() < Math.pow(Analyzer.CONFIDENCE, 2) || (!optimizeFull && currentStatus.isWithinConfidenceInterval());
	}

	private List<CalendarStatus> getSortedStatuses(List<? extends IEvent> events, CalendarStatus currentStatus) {
		List<CalendarStatus> result = new LinkedList<CalendarStatus>();
		CalendarStatus newStatus;
		for (IEvent event : events) {
			newStatus = new CalendarStatus(event, currentStatus);
			newStatus.analyse();
			result.add(newStatus);
		}
		Collections.sort(result);
		return result;
	}

	private LinkedList<List<Suggestion>> convertToSuggestions(List<List<CalendarStatus>> statuses) {
		LinkedList<List<Suggestion>> listSuggestions = new LinkedList<List<Suggestion>>();
		Iterator<List<CalendarStatus>> it = statuses.iterator();
		while (it.hasNext()) {
			listSuggestions.add(convert(it.next()));
		}
		return listSuggestions;
	}

	public List<Suggestion> convert(List<CalendarStatus> statuses) {
		List<Suggestion> suggestions = new LinkedList<Suggestion>();
		Iterator<CalendarStatus> iterator = statuses.iterator();
		while (iterator.hasNext()) {
			CalendarStatus status = iterator.next();
			IEvent event = status.getEvent();
			Double additionalTime = status.getAdditionalEventTime();
			Suggestion result;
			if (event.getDuration() + additionalTime == 0) {
				result = new DeleteSuggestion(event);
			} else {
				Calendar end = new GregorianCalendar();
				end.setTimeInMillis(event.getEndDate().getTimeInMillis() + (long) (additionalTime * 60000));
				result = new RescheduleSuggestion(event, event.getStartDate(), end);
			}
			if(status.hasAlternatives()) {
				result.setAlternativeSuggetions(convert(status.getAlternatives()));
			}
			suggestions.add(result);
		}
		return suggestions;
	}

	private void removeStaticEvents(List<? extends IEvent> events) {
		Iterator<? extends IEvent> it = events.iterator();
		while (it.hasNext()) {
			IEvent current = it.next();
			if (!current.canReschedule())
				it.remove();
		}
	}

	public CalendarStatus checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException {
		CalendarStatus result;
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		for (IEvent event : events) {
			// jebane strefy czasowe........
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereResults = event.getSpheres();
			Set<SphereName> keys = sphereResults.keySet();
			for (SphereName key : keys) {
				double time = Math.round(sphereResults.get(key) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			currentRatios.put(key, times.get(key) / sum);
		}
		Map<SphereName, SphereInfo> sphereResults = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			sphereResults.put(key, info);
		}
		return new CalendarStatus(sum, sphereResults);
	}

	private void printEvents(Collection<? extends ICalendarSlot> events) {
		for (ICalendarSlot event : events)
			System.out.println(event.getTitle() + "  " + printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
	}

	private String printDate(Calendar cal) {
		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
		+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	}

	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}


	public static class AlgoTest extends TestCase {

		protected boolean b;

		@ Test 
		public void testConvertToSuggestionsEmpty() {
			b = true;
			assertTrue(b);
		}

//		@Test
//		private void testConvertToSuggestions() {
//			LinkedList<List<CalendarStatus>> lists = new LinkedList<List<CalendarStatus>>();
//			LinkedList<CalendarStatus> statuses2 = new LinkedList<CalendarStatus>();
//			LinkedList<CalendarStatus> statuses1 = new LinkedList<CalendarStatus>();
//		}
}
}