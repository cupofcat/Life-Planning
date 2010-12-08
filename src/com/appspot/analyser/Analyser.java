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

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;

@RunWith(Enclosed.class)
public class Analyser {

	public static final double CONFIDENCE = 0.1;
	private static final double ALTERNATIVE = 0.3;
	static final int TRIES = 10;
	private int maxDepth = 3;
	private int maxSuggestions = 3;
	private Map<SphereName, List<? extends IEvent>> proposals;

	public Analyser() {
		proposals = new HashMap<SphereName, List<? extends IEvent>>();
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
		List<BaseCalendarSlot>[] freeSlots = getFreeSlots(events);
		removeStaticEvents(events);
		LinkedList<List<CalendarStatus>> result = new LinkedList<List<CalendarStatus>>();
		CalendarStatus start = checkGoals(events, spherePreferences);
		if (isCloseEnough(start, optimizeFull))
			return null;
		List<CalendarStatus> statuses = getSortedStatuses(events, start);
		//proposals
		/* 	1. Dodac freesloty do calendarStatus i zmiany na poziomie nowych linkedlistow
		 * 	2. wyciagnac proposale po majorSphere
		 * 	3  idac po proposalach, szukac freslotow z bucketow in startTime + duration
		 * 	4. build calendarStatuses around picked proposals
		 * 	5. (statuses) union (result from 4) 
		 * 	6. if nextMin is proposal, store currentfreeslots somewhere and update currentStatus na nowe freesloty
		 */


		for (int i = 0; i < maxSuggestions || i < statuses.size(); i++) {
			LinkedList<CalendarStatus> list = new LinkedList<CalendarStatus>();			
			CalendarStatus nextMin = statuses.get(i);
			// best event can't improve the status
			if (nextMin.compareTo(start) >= 0)
				break;
			//check neighbours for possible alternatives - run loop which won't modify i (we want i sets of proposals) - proposals
			while(i < statuses.size()){
				CalendarStatus next = statuses.get(i);
				if(next.compareTo(start) < 0 || next.getCoefficient() <= nextMin.getCoefficient()*(1+Analyser.ALTERNATIVE))
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
			if(next.compareTo(currentStatus) < 0 || next.getCoefficient() <= minimum.getCoefficient()*(1+Analyser.ALTERNATIVE))
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

	/* Find slots of free time in between events */
	private List<BaseCalendarSlot>[] getFreeSlots(List<? extends IEvent> events) {
		LinkedList<BaseCalendarSlot>[] ret = (LinkedList<BaseCalendarSlot>[]) new LinkedList[24];
		Collections.sort(events);
		Iterator<? extends IEvent> it = events.iterator();
		IEvent beginning = it.next();
		it.remove();
		IEvent curr = beginning;
		while (it.hasNext()) {
			IEvent next = it.next();
			if (curr.getEndDate().compareTo(next.getStartDate()) < 0) {
				BaseCalendarSlot newSlot = new BaseCalendarSlot("Free Slot", null, curr.getEndDate(), next.getStartDate());
				int index = newSlot.getStartDate().get(Calendar.HOUR_OF_DAY);
				LinkedList<BaseCalendarSlot> updatedList = ret[index];
				if (updatedList == null)
					updatedList = new LinkedList<BaseCalendarSlot>();
				updatedList.add(newSlot);
				ret[index] = updatedList;
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

	/* Test if we have reached the confidence interval for spheres */
	private boolean isCloseEnough(CalendarStatus currentStatus, boolean optimizeFull) {
		return currentStatus.getCoefficient() < Math.pow(Analyser.CONFIDENCE, 2) 
		|| (!optimizeFull && currentStatus.isWithinConfidenceInterval());
	}

	/* Create calendar statuses for all events, order them in terms of 
	 * how well they match to targets for spheres (the coefficient of accuracy) */
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

	/* Convert from calendar statuses to suggestions which will be passed to front end */
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

	/* Remove user events which cannot be rescheduled */
	private void removeStaticEvents(List<? extends IEvent> events) {
		Iterator<? extends IEvent> it = events.iterator();
		while (it.hasNext()) {
			IEvent current = it.next();
			if (!current.canReschedule())
				it.remove();
		}
	}

	/* Work out overall sphere levels considering current events. 
	 *  */
	public CalendarStatus checkGoals(Collection<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException {
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		for (IEvent event : events) {
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
		Map<SphereName, SphereInfo> sphereResults = generateSphereResults(choices, currentRatios, times);
		return new CalendarStatus(sum, sphereResults);
	}

	private Map<SphereName, SphereInfo> generateSphereResults(Map<SphereName, Double> choices,
			Map<SphereName, Double> currentRatios, Map<SphereName, Double> times) {
		Map<SphereName, SphereInfo> sphereResults = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			sphereResults.put(key, info);
		}
		return sphereResults;
	}

	//	private void printEvents(Collection<? extends ICalendarSlot> events) {
	//		for (ICalendarSlot event : events)
	//			System.out.println(event.getTitle() + "  " + printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
	//	}
	//
	//	private String printDate(Calendar cal) {
	//		return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
	//		+ ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	//	}

	private void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
		for (SphereName key : keys)
			times.put(key, 0.0);
	}

	public static class AlgoTest extends TestCase {

		private Analyser a = new Analyser();
		private List<? extends IEvent> events;

		@Test 
		public void testConvert() {
			events = sampleEvents();
			List<CalendarStatus> statuses = new LinkedList<CalendarStatus>();
			Map<SphereName, SphereInfo> sphereInfos = generateSphereInfos(new double[]{2.5, 4.5, 0.5, 7.5});
			CalendarStatus other = new CalendarStatus(10.0, sphereInfos);
			for (IEvent event : events) {
				CalendarStatus c1 = new CalendarStatus(event, other);
				statuses.add(c1);
			}
			List<Suggestion> suggestions = a.convert(statuses);
			assertTrue("Not all conversions successful", statuses.size() == suggestions.size());
			boolean s1 = suggestions.get(1) instanceof RescheduleSuggestion;
			boolean s2 = suggestions.get(2) instanceof DeleteSuggestion;
			boolean s3 = suggestions.get(3) instanceof RescheduleSuggestion;
			assertTrue("Converting statuses into suggestions broken", s1&s2&s3);
		}

		@Test 
		public void testGetFreeSlotsBaseCase() {
			events = sampleEvents();
			IEvent reschedule1 = events.get(1);
			double oldIntervalFs14 = reschedule1.getDurationInterval().getSecond();
			List<BaseCalendarSlot>[] freeSlots = a.getFreeSlots(events);

			List<BaseCalendarSlot> fs14 = freeSlots[14];
			double fs14Duration = fs14.get(0).getDuration();
			double newIntervalFs14 = reschedule1.getDurationInterval().getSecond();
			double reschedule1Duration = reschedule1.getDuration();
			boolean size = fs14.size()==1;
			boolean start = fs14.get(0).getStartDate().equals(new GregorianCalendar(2000, 3, 3, 14, 45, 0));
			boolean end = fs14.get(0).getEndDate().equals(new GregorianCalendar(2000, 3, 3, 15, 30, 0));
			boolean interval = newIntervalFs14 == Math.min(oldIntervalFs14, reschedule1Duration + fs14Duration);
			assertTrue("Basic case where curr.end < next.start does not work", size&start&end&interval);
		}

		@Test 
		public void testGetFreeSlotsSwallows() {
			events = sampleEvents();
			List<BaseCalendarSlot>[] freeSlots = a.getFreeSlots(events);
			List<BaseCalendarSlot> fs17 = freeSlots[17];
			List<BaseCalendarSlot> fs18 = freeSlots[18];
			boolean isNull = fs17==null;
			boolean size18 = fs18.size()==1;
			assertTrue("Case where one event entirely overlaps another does not work", isNull&size18);
		}

		@Test 
		public void testGetFreeSlotsExtendsBeyond() {
			events = sampleEvents();
			IEvent reschedule5 = events.get(6);
			a.getFreeSlots(events);
			double newIntervalFs20 = reschedule5.getDurationInterval().getSecond();
			double reschedule5Duration = reschedule5.getDuration();
			boolean setIntervalToDuration = newIntervalFs20==reschedule5Duration;
			assertTrue("Case where one event entirely overlaps another does not work", 
					setIntervalToDuration);
		}

		@Test
		public void testRemoveStaticEvents() {
			events = sampleEvents();
			double sizeBefore = events.size();
			a.removeStaticEvents(events);
			double sizeAfter = events.size();
			boolean size = sizeBefore != sizeAfter + 1;
			for (IEvent event: events) {
				assertTrue("An event that cannot be rescheduled is not removed properly", !event.getTitle().equals("reschedule 6"));
			}
			assertTrue("A rescheduable event might have been removed", size);
		}

		@Test
		public void testIsCloseEnough() {
			/*		Not sure what figures I need for si1 and si2!
			events = sampleEvents();
			Map<SphereName, SphereInfo> si1 = generateSphereInfos(new double[]{0.1, 0.3, 0.3, 0.4});
			Map<SphereName, SphereInfo> si2 = generateSphereInfos(new double[]{0.3, 0.3, 0.3, 0.1});
			CalendarStatus c1 = new CalendarStatus(events.get(1), 
					new CalendarStatus(100.0, si1));
			CalendarStatus c2 = new CalendarStatus(events.get(2), 
					new CalendarStatus(100.0, si2));
			assertTrue("", !a.isCloseEnough(c1, true));
			assertTrue("", a.isCloseEnough(c2, true));
			assertTrue("", !a.isCloseEnough(c1, false));
			assertTrue("", a.isCloseEnough(c2, false));
			 */
		}

		@Test
		public void testGetSortedStatuses() {
			events = sampleEvents();
			events.remove(0);
			events.remove(events.size()-1);
			Map<SphereName, Double> times = new HashMap<SphereName, Double>();
			times.put(SphereName.HEALTH, 11.0);
			times.put(SphereName.WORK, 492.0);
			times.put(SphereName.FAMILY, 32.0);
			times.put(SphereName.RECREATION, 42.0);
			Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
			currentRatios.put(SphereName.HEALTH, 0.019469026548672566);
			currentRatios.put(SphereName.WORK, 0.8707964601769912);
			currentRatios.put(SphereName.FAMILY, 0.05663716814159292);
			currentRatios.put(SphereName.RECREATION, 0.0743362831858407);
			double sum = 565.0;
			Map<SphereName, SphereInfo> results = 
				a.generateSphereResults(generateSpheres(new double[]{0.1, 0.3, 0.3, 0.4}),
						currentRatios, times);
			CalendarStatus start = new CalendarStatus(sum, results);
			List<CalendarStatus> sortedStatuses = a.getSortedStatuses(events, start);
			assertTrue("Not all events translated into statuses correctly", 
					sortedStatuses.size() == events.size());
			double min = 0.0;
			for (CalendarStatus status : sortedStatuses) {
				assertTrue("Get sorted statuses orders events incorectly", status.getCoefficient() >= min);
				min = status.getCoefficient();
			}
		}

		@Test
		public void testCheckGoals() {
			events = sampleEvents();
			events.remove(0);
			events.remove(events.size()-1);
			Map<SphereName, Double> times = new HashMap<SphereName, Double>();
			times.put(SphereName.HEALTH, 11.0);
			times.put(SphereName.WORK, 492.0);
			times.put(SphereName.FAMILY, 32.0);
			times.put(SphereName.RECREATION, 42.0);
			Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
			currentRatios.put(SphereName.HEALTH, 0.019469026548672566);
			currentRatios.put(SphereName.WORK, 0.8707964601769912);
			currentRatios.put(SphereName.FAMILY, 0.05663716814159292);
			currentRatios.put(SphereName.RECREATION, 0.0743362831858407);
			double sum = 565.0;
			Map<SphereName, Double> choices = generateSpheres(new double[]{0.1, 0.3, 0.3, 0.4});
			try {
				CalendarStatus start = a.checkGoals(events, choices);
				assertTrue("Incorrect coefficient in sphere info creation",
						start.getCoefficient() == 122035.3982300885);
				assertTrue("Incorrect userBusyTime in sphere info creation", 
						start.getUserBusyTime() == sum);
				for(SphereName sn : start.getSphereResults().keySet()) {
					SphereInfo si = start.getSphereResults().get(sn);
					assertTrue("Sphere time incorrect in sphere info creation", 
							si.getSphereTotalTime() == times.get(sn));
					assertTrue("Sphere current ratio incorrect in sphere info creation", 
							si.getCurrentRatio() == currentRatios.get(sn));
				}
			}
			catch (Exception e) {
				fail("Checking goals failed - exception thrown: " + e.getLocalizedMessage());
			}
		}

		/* Utils */
		private HashMap<SphereName, SphereInfo> generateSphereInfos(double[] values){
			SphereName[] names = SphereName.values();
			HashMap<SphereName, SphereInfo> res = new HashMap<SphereName, SphereInfo>();
			for(int i = 0; i < names.length; i++) {
				SphereInfo si = new SphereInfo(values[i], values[i], 10.0);
				res.put(names[i], si);
			}
			return res;
		}

		private HashMap<SphereName, Double> generateSpheres(double[] values){
			SphereName[] names = SphereName.values();
			HashMap<SphereName, Double> res = new HashMap<SphereName, Double>();
			for(int i = 0; i < names.length; i++){
				res.put(names[i], values[i]);
			}
			return res;
		}

		private List<? extends IEvent> sampleEvents() {
			Suggestion begin = new RescheduleSuggestion("begin", null, 
					new GregorianCalendar(2000, 3, 3, 0, 0, 0),new GregorianCalendar(2000, 3, 3, 0, 0, 0) );
			begin.setDeurationInterval(0, 0);
			Suggestion end = new RescheduleSuggestion("end", null, 
					new GregorianCalendar(2000, 3, 3, 23, 59, 59),new GregorianCalendar(2000, 3, 3, 23, 59, 59) );
			end.setDeurationInterval(0, 0);
			/* Should remain a RescheduleSuggestion */
			Suggestion s1 = new RescheduleSuggestion("reschedule 1", null, 
					new GregorianCalendar(2000, 3, 3, 13, 0, 0),new GregorianCalendar(2000, 3, 3, 14, 45, 0) );
			s1.setSpheres(generateSpheres(new double[]{0.1, 0.3, 0.3, 0.4}));
			s1.setDeurationInterval(30, 151);
			s1.setReschedule(true);
			/* Should get converted to a DeleteSuggestion */
			Suggestion s2 = new RescheduleSuggestion("delete", null, 
					new GregorianCalendar(2000, 3, 3, 15, 30, 0),new GregorianCalendar(2000, 3, 3, 15, 30, 0) );
			s2.setSpheres(generateSpheres(new double[]{0.3, 0.3, 0.3, 0.1}));
			s2.setDeurationInterval(0, 40);
			s2.setReschedule(true);
			/* Should get converted to a RescheduleSuggestion */
			Suggestion s3 = new InsertSuggestion("reschedule 2", null, 
					new GregorianCalendar(2000, 3, 3, 16, 00, 0),new GregorianCalendar(2000, 3, 3, 18, 30, 0) );
			s3.setSpheres(generateSpheres(new double[]{0.0, 1.0, 0.0, 0.0}));
			s3.setDeurationInterval(0, 170);
			s3.setReschedule(true);
			/* Special case - s4 and s5 fits inside s3 */
			Suggestion s4 = new InsertSuggestion("reschedule 3", null, 
					new GregorianCalendar(2000, 3, 3, 16, 30, 0),new GregorianCalendar(2000, 3, 3, 17, 20, 0) );
			s4.setSpheres(generateSpheres(new double[]{0.0, 1.0, 0.0, 0.0}));
			s4.setDeurationInterval(0, 100);
			s4.setReschedule(true);
			Suggestion s5 = new InsertSuggestion("reschedule 4", null, 
					new GregorianCalendar(2000, 3, 3, 16, 30, 0),new GregorianCalendar(2000, 3, 3, 18, 20, 0) );
			s5.setSpheres(generateSpheres(new double[]{0.0, 1.0, 0.0, 0.0}));
			s5.setDeurationInterval(0, 61);
			s5.setReschedule(true);
			Suggestion s6 = new InsertSuggestion("reschedule 5", null, 
					new GregorianCalendar(2000, 3, 3, 19, 30, 0),new GregorianCalendar(2000, 3, 3, 20, 20, 0) );
			s6.setSpheres(generateSpheres(new double[]{0.0, 1.0, 0.0, 0.0}));
			s6.setDeurationInterval(0, 60);
			s6.setReschedule(true);
			Suggestion s7 = new InsertSuggestion("reschedule 6", null, 
					new GregorianCalendar(2000, 3, 3, 19, 40, 0),new GregorianCalendar(2000, 3, 3, 21, 20, 0) );
			s7.setSpheres(generateSpheres(new double[]{0.0, 1.0, 0.0, 0.0}));
			s7.setDeurationInterval(0, 180);
			s7.setReschedule(false);
			List<Suggestion> list = new LinkedList<Suggestion>();
			list.add(begin);
			list.add(s1);
			list.add(s2);
			list.add(s3);
			list.add(s4);
			list.add(s5);
			list.add(s6);
			list.add(s7);
			list.add(end);
			Collections.sort(list);
			return list;
		}
	}
}