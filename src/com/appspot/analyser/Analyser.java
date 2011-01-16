
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
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.appspot.datastore.PMF;
import com.appspot.datastore.SphereInfo;
import com.appspot.datastore.SphereName;
import com.appspot.datastore.UserProfile;
import com.appspot.datastore.UserProfileStore;

//Main class returning suggestions based on user's google calendar

@RunWith(Enclosed.class)
public class Analyser {
	//constants used in optimization process
	public static final double CONFIDENCE = 0.1;
	static final double ALTERNATIVE = 0.3;
	static final int TRIES = 20;
	private int maxDepth = 3;
	private int maxSuggestions = 3;
	private Map<SphereName, List<Proposal>> proposals;
	private List<IEvent> events;
	private static final Logger log = Logger.getLogger("EventStore");

	public Analyser() {
		//certain proposals to populate our database
		proposals = new HashMap<SphereName, List<Proposal>>();
		Calendar startDate = new GregorianCalendar(2000, 0, 3, 7, 0, 0);
		Calendar endDate = new GregorianCalendar(2000, 0, 3, 8, 30, 0);
		Pair<Calendar, Calendar> possibleSlot = new Pair<Calendar, Calendar>(startDate, endDate);
		Map<SphereName, Double> sphereInfluences = Utilities.generateSpheres(new double[] { 1.0 });
		Proposal p1 = new Proposal("Gym", "Nie ma upierdalania sja", null, null, 20, 60, false, true, sphereInfluences);
		p1.setPossibleTimeSlot(possibleSlot);
		p1.makePersistent();
		LinkedList<Proposal> health = new LinkedList<Proposal>();
		health.add(p1);
		proposals.put(SphereName.HEALTH, health);

		Calendar startWorkDate = new GregorianCalendar(2000, 0, 3, 17, 0, 0);
		Calendar endWorkDate = new GregorianCalendar(2000, 0, 3, 18, 30, 0);
		Pair<Calendar, Calendar> possibleWorkSlot = new Pair<Calendar, Calendar>(startWorkDate, endWorkDate);
		Map<SphereName, Double> sphereWorkInfluences = Utilities.generateSpheres(new double[] { 0, 1.0 });
		Proposal work = new Proposal("Meeting at work", "Szanuj szefa swego bo mozesz miec gorszego", null, null, 20, 60, false, true,
				sphereWorkInfluences);
		work.setPossibleTimeSlot(possibleWorkSlot);
		work.makePersistent();
		
		LinkedList<Proposal> works = new LinkedList<Proposal>();
		works.add(work);
		proposals.put(SphereName.WORK, works);

		Calendar startFamilyDate = new GregorianCalendar(2000, 0, 3, 15, 0, 0);
		Calendar endFamilyDate = new GregorianCalendar(2000, 0, 3, 16, 0, 0);
		Pair<Calendar, Calendar> possibleFamilySlot = new Pair<Calendar, Calendar>(startFamilyDate, endFamilyDate);
		Map<SphereName, Double> sphereFamilyInfluences = Utilities.generateSpheres(new double[] { 0.0, 0.0, 1.0 });
		Proposal family = new Proposal("Family dinner", "Steaks - all that matters", null, null, 20, 60, false, true, sphereFamilyInfluences);
		family.setPossibleTimeSlot(possibleFamilySlot);
		family.makePersistent();
		LinkedList<Proposal> families = new LinkedList<Proposal>();
		families.add(family);
		proposals.put(SphereName.FAMILY, families);

		Calendar startRecreationDate = new GregorianCalendar(2000, 0, 3, 22, 0, 0);
		Calendar endRecreationDate = new GregorianCalendar(2000, 0, 3, 23, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot = new Pair<Calendar, Calendar>(startRecreationDate, endRecreationDate);
		Map<SphereName, Double> sphereRecreationInfluences = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation = new Proposal("Watching a movie", "Robert Burneika box set!", null, null, 20, 60, false, true, sphereRecreationInfluences);
		recreation.setPossibleTimeSlot(possibleRecreationSlot);
		recreation.makePersistent();
		
		Calendar startRecreationDate2 = new GregorianCalendar(2000, 0, 3, 21, 0, 0);
		Calendar endRecreationDate2 = new GregorianCalendar(2000, 0, 3, 22, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot2 = new Pair<Calendar, Calendar>(startRecreationDate2, endRecreationDate2);
		Map<SphereName, Double> sphereRecreationInfluences2 = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation2 = new Proposal("Beer with a friend", "Take 2KC with me", null, null, 20, 60, false, true, sphereRecreationInfluences2);
		recreation2.setPossibleTimeSlot(possibleRecreationSlot2);
		recreation2.makePersistent();
		
		Calendar startRecreationDate3 = new GregorianCalendar(2000, 0, 3, 21, 0, 0);
		Calendar endRecreationDate3 = new GregorianCalendar(2000, 0, 3, 22, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot3 = new Pair<Calendar, Calendar>(startRecreationDate3, endRecreationDate3);
		Map<SphereName, Double> sphereRecreationInfluences3 = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation3 = new Proposal("Poker session", "Royal flush - here I come", null, null, 20, 60, false, true, sphereRecreationInfluences3);
		recreation3.setPossibleTimeSlot(possibleRecreationSlot3);
		recreation3.makePersistent();
		
		Calendar startRecreationDate4 = new GregorianCalendar(2000, 0, 3, 20, 0, 0);
		Calendar endRecreationDate4 = new GregorianCalendar(2000, 0, 3, 21, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot4 = new Pair<Calendar, Calendar>(startRecreationDate4, endRecreationDate4);
		Map<SphereName, Double> sphereRecreationInfluences4 = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation4 = new Proposal("Go fly fishing", "Hope to catch a halibut :)", null, null, 20, 60, false, true, sphereRecreationInfluences4);
		recreation4.setPossibleTimeSlot(possibleRecreationSlot4);
		recreation4.makePersistent();
		
		Calendar startRecreationDate5 = new GregorianCalendar(2000, 0, 3, 20, 0, 0);
		Calendar endRecreationDate5 = new GregorianCalendar(2000, 0, 3, 20, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot5 = new Pair<Calendar, Calendar>(startRecreationDate5, endRecreationDate5);
		Map<SphereName, Double> sphereRecreationInfluences5 = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation5 = new Proposal("Afternoon nap", "Not taking any calls", null, null, 20, 60, false, true, sphereRecreationInfluences5);
		recreation5.setPossibleTimeSlot(possibleRecreationSlot5);
		recreation5.makePersistent();
		
		Calendar startRecreationDate6 = new GregorianCalendar(2000, 0, 3, 19, 0, 0);
		Calendar endRecreationDate6 = new GregorianCalendar(2000, 0, 3, 20, 30, 0);
		Pair<Calendar, Calendar> possibleRecreationSlot6 = new Pair<Calendar, Calendar>(startRecreationDate6, endRecreationDate6);
		Map<SphereName, Double> sphereRecreationInfluences6 = Utilities.generateSpheres(new double[] { 0.0, 0.0, 0.0, 1.0 });
		Proposal recreation6 = new Proposal("Have a smoke", "Co mowi palma do palmy? Za palmy!", null, null, 20, 60, false, true, sphereRecreationInfluences6);
		recreation6.setPossibleTimeSlot(possibleRecreationSlot6);
		recreation6.makePersistent();
		
		
		LinkedList<Proposal> recreations = new LinkedList<Proposal>();
		recreations.add(recreation);
		recreations.add(recreation2);
		recreations.add(recreation3);
		//recreations.add(recreation4);
		//recreations.add(recreation5);
		//recreations.add(recreation6);
		proposals.put(SphereName.RECREATION, recreations);
	}

	@SuppressWarnings("unchecked")
	public List<List<Suggestion>> getSuggestions(List<? extends IEvent> events, String currentUserId) throws IOException {
		UserProfile profile = UserProfileStore.getUserProfile(currentUserId);
		this.events = (List<IEvent>) events;
		return
		 convertToSuggestions(this.getSuggestions(profile.getSpherePreferences(),
		 profile.isFullyOptimized()));
	}
	
	//Method returning sets of sugestions to the user
	private List<List<CalendarStatus>> getSuggestions(Map<SphereName, Double> spherePreferences, boolean optimizeFull) throws IOException {
		//no events in calendar...
		if (events.size() == 0)
			return null;
		LinkedList<List<CalendarStatus>> result = new LinkedList<List<CalendarStatus>>();
		//Generate initial calendar status
		CalendarStatus start = checkGoals(events, spherePreferences);
		//Already within our goals...
		if (isCloseEnough(start, optimizeFull))
			return result;
		removeStaticEvents(events);
		//generate virtual calendars for each event and proposal in our database
		List<CalendarStatus> statuses = Utilities.merge( generateProposalStatuses(start.getDeficitSpheres(optimizeFull), start, true) , 
				generateEventStatuses(events, start)
				);
		if (statuses.isEmpty())
			return result;
		for (int i = 0; result.size() < maxSuggestions && i < statuses.size(); i++) {
			LinkedList<CalendarStatus> list = new LinkedList<CalendarStatus>();
			CalendarStatus nextMin = statuses.get(i);
			CalendarStatus nextStatus = nextMin;
			//remove event form our list, so that we won't schedule same thing twice
			removeEvent(nextMin);
			int count = 1;
			int k = i + 1;
			List<CalendarStatus> alternatives = new LinkedList<CalendarStatus>();
			alternatives.add(nextStatus);
			/* Check if any of the neighbours can become an alternative for our min event
			 * It needs to have almost same influence on the calendar and belong to same sphere
			 */
			while (k < statuses.size() && count < 3) {
				CalendarStatus next = statuses.get(k);
				if (next.compareTo(start) > 0
						|| (next.getCoefficient() > 0.05 && next.getCoefficient() > nextMin.getCoefficient() * (1 + Analyser.ALTERNATIVE)))
					break;
				SphereName mine;
				SphereName nextSphere;
				//checking of major spheres
				if (nextStatus.containsProposal())
					mine = ((Proposal) nextStatus.getEvent()).getMajorSphere();
				else
					mine = Utilities.calculateMajorSphere(nextStatus.getEvent());
				if(next.containsProposal())
					nextSphere = ((Proposal) next.getEvent()).getMajorSphere();
				else
					nextSphere = Utilities.calculateMajorSphere(next.getEvent());
				if(!mine.equals(nextSphere)){
					k++;
					continue;
				}
				removeEvent(next);
				statuses.remove(next);
				alternatives.add(next);
				//Our next status to be passed down will contain proposal
				if (!nextStatus.containsProposal() && next.containsProposal())
					nextStatus = next;
				count++;
			}
			alternatives.remove(nextStatus);
			nextStatus.addAlternatives(alternatives);
			nextStatus.updateSlots();
			List<CalendarStatus> rest = getSuggestions(nextStatus, optimizeFull, maxDepth);
			if (rest != null) {
				//peroform recalculation to improve our calendar based on what we have just scheduled
				CalendarStatus toChange = rest.remove(0);
				CalendarStatus changed = rest.remove(0);
				int reps = 0;
				do {
					Pair<List<CalendarStatus>, List<CalendarStatus>> res = toChange.recalculate(changed);
					List<CalendarStatus> successes = res.getFirst();
					CalendarStatus best = successes.remove(0);
					best.addAlternatives(successes);
					toChange = best;
					CalendarStatus tmp = toChange;
					toChange = changed;
					changed = tmp;
					reps++;
				} while (changed.hasImproved());
				list.addAll(rest);
				//return events in the correct order
				if (reps % 2 == 1) {
					list.addFirst(toChange);
					list.addFirst(changed);
					restoreEvents(changed);
				} else {
					list.addFirst(changed);
					list.addFirst(toChange);
					restoreEvents(toChange);
				}
			}
			else {
				list.add(nextStatus);
				restoreEvents(nextStatus);
			}
			result.add(list);
		}
		return result;
	}
	
	//Recursive function generating new suggestion considering what we chose before
	private List<CalendarStatus> getSuggestions(CalendarStatus currentStatus, boolean optimizeFull, int depth) throws IOException {
		if (isCloseEnough(currentStatus, optimizeFull) || (events.size() == 0 && !haveAnyProposals()) || depth <= 0)
			return null;
		//generate calendars with virtually scheduled events again, pick minimum and find its alternatives
		List<CalendarStatus> statuses = Utilities.merge(generateProposalStatuses(currentStatus.getDeficitSpheres(optimizeFull), currentStatus, false) ,
				generateEventStatuses(events,currentStatus) );

		if (statuses.isEmpty())
			return null;
		LinkedList<CalendarStatus> list = new LinkedList<CalendarStatus>();
		CalendarStatus nextMin = statuses.get(0);

		CalendarStatus nextStatus = nextMin;
		removeEvent(nextMin);
		int i = 1;
		int count = 1;
		List<CalendarStatus> alternatives = new LinkedList<CalendarStatus>();
		alternatives.add(nextStatus);
		//Find alternatives
		while (i < statuses.size() && count < 3) {
			CalendarStatus next = statuses.get(i);
			if (next.compareTo(currentStatus) > 0
					|| (next.getCoefficient() > 0.05 && next.getCoefficient() > nextMin.getCoefficient() * (1 + Analyser.ALTERNATIVE)))
				break;
			SphereName mine;
			SphereName nextSphere;
			if (nextStatus.containsProposal())
				mine = ((Proposal) nextStatus.getEvent()).getMajorSphere();
			else
				mine = Utilities.calculateMajorSphere(nextStatus.getEvent());
			if(next.containsProposal())
				nextSphere = ((Proposal) next.getEvent()).getMajorSphere();
			else
				nextSphere = Utilities.calculateMajorSphere(next.getEvent());
			if(!mine.equals(nextSphere)){
				i++;
				continue;
			}
			removeEvent(next);
			statuses.remove(next);
			alternatives.add(next);
			if(!nextStatus.containsProposal() && next.containsProposal())
			 nextStatus = next;
			count++;
		}
		alternatives.remove(nextStatus);
		nextStatus.addAlternatives(alternatives);
		nextStatus.updateSlots();
		
		CalendarStatus toChange = currentStatus;
		CalendarStatus changed = nextStatus;
		List<CalendarStatus> removed = new LinkedList<CalendarStatus>();
		int reps = 0;
		CalendarStatus current;
		//Perform recalculation based on what we scheduled BEFORE our current min
		do {
			Pair<List<CalendarStatus>, List<CalendarStatus>> res = toChange.recalculate(changed);
			List<CalendarStatus> successes = res.getFirst();
			CalendarStatus best = successes.remove(0);
			best.addAlternatives(successes);
			toChange = best;
			CalendarStatus tmp = toChange;
			toChange = changed;
			changed = tmp;
			reps++;
		} while (changed.hasImproved());
		if (reps % 2 == 1) {
			current = changed;
			nextStatus = toChange;
		} else {
			current = toChange;
			nextStatus = changed;
		}		
		removed.add(current);
		List<CalendarStatus> rest = getSuggestions(nextStatus, optimizeFull, depth - 1);
		if (rest != null) {
			toChange = rest.remove(0);
			changed = rest.remove(0);
			reps = 0;
			//Another recalculation based on what we scheduled AFTER choosing our current min event
			do {
				Pair<List<CalendarStatus>, List<CalendarStatus>> res = toChange.recalculate(changed);
				List<CalendarStatus> successes = res.getFirst();
				CalendarStatus best = successes.remove(0);
				best.addAlternatives(successes);
				toChange = best;
				CalendarStatus tmp = toChange;
				toChange = changed;
				changed = tmp;
				reps++;
			} while (changed.hasImproved());
			if (reps % 2 == 1) {
				removed.add(changed);
				removed.add(toChange);
				restoreEvents(changed);
			} else {
				removed.add(toChange);
				removed.add(changed);
				restoreEvents(toChange);
			}
			list.addAll(removed);
			list.addAll(rest);
		} else {
			list.addAll(removed);
			list.add(nextStatus);
			restoreEvents(nextStatus);
		}
		return list;
	}

	/* Test if we have reached the confidence interval for spheres */
	private boolean isCloseEnough(CalendarStatus currentStatus, boolean optimizeFull) {
		return currentStatus.getCoefficient() < Math.pow(Analyser.CONFIDENCE, 2) || (!optimizeFull && currentStatus.isWithinConfidenceInterval());
	}
	//Check if we have any proposals left
	private boolean haveAnyProposals() {
		List<Proposal> next;
		for (SphereName sphere : SphereName.values()) {
			next = proposals.get(sphere);
			if (next != null && !next.isEmpty())
				return true;
		}
		return false;
	}

	/* If event is a proposal, remove from proposals, else from events */
	private void removeEvent(CalendarStatus status) {
		if (status.containsProposal())
			proposals.get(((Proposal) status.getEvent()).getMajorSphere()).remove(status.getEvent());
		else
			events.remove(status.getEvent());
	}

	/*
	 * If event or its alternative is a proposal, add to proposals, else to
	 * events
	 */
	private void restoreEvents(CalendarStatus status) {
		if (status.containsProposal())
			proposals.get(((Proposal) status.getEvent()).getMajorSphere()).add((Proposal) status.getEvent());
		else
			events.add(status.getEvent());
		if (status.hasAlternatives()) {
			for (CalendarStatus alternative : status.getAlternatives()) {
				if (alternative.containsProposal())
					proposals.get(((Proposal) alternative.getEvent()).getMajorSphere()).add((Proposal) alternative.getEvent());
				else
					events.add(alternative.getEvent());
			}
		}
	}

	/*
	 * Create calendar statuses for all events, order them in terms of how well
	 * they match to targets for spheres (the coefficient of accuracy)
	 */
	private List<CalendarStatus> generateEventStatuses(List<? extends IEvent> events, CalendarStatus currentStatus) {
		List<CalendarStatus> result = new LinkedList<CalendarStatus>();
		CalendarStatus newStatus;
		for (IEvent event : events) {
			newStatus = new CalendarStatus(event, currentStatus);
			if (newStatus.hasImproved())
				result.add(newStatus);
		}
		Collections.sort(result);
		return result;
	}

	/*
	 * Get proposals for spheres which are not on target Either from memory or
	 * pull from dataStore
	 */
	@SuppressWarnings("unchecked")
	private List<CalendarStatus> generateProposalStatuses(List<SphereName> deficitSpheres, CalendarStatus currentStatus, boolean truncateProposals) {
		List<CalendarStatus> result = new LinkedList<CalendarStatus>();
		PersistenceManager pmf = PMF.get().getPersistenceManager();
		List<Proposal> cache;
		for (SphereName sphere : deficitSpheres) {
			cache = proposals.get(sphere);
			if (cache == null) {
				cache = new LinkedList<Proposal>();

				Collection<Proposal> res = (Collection<Proposal>) pmf.newQuery(
						"select from " + Proposal.class.getName() + " where majorSphere =='" + sphere + "'").execute();
				for (Proposal p : res) {

					cache.add(p);
				}
				permute(cache);
				proposals.put(sphere, cache);
			}
			CalendarStatus next;
			Iterator<Proposal> it = cache.iterator();
			while (it.hasNext()) {
				next = currentStatus.checkProposal(it.next());
				if (next != null && next.hasImproved())
					result.add(next);
				else if (truncateProposals)
					it.remove();
			}
			Collections.sort(result);
		}
		return result;
	}
	//Permute list
	private void permute(List<Proposal> list) {
		List<Proposal> newList = new LinkedList<Proposal>();
		Random rand = new Random();
		while (!list.isEmpty()) {
			newList.add(list.remove(rand.nextInt(list.size())));
		}
		list.addAll(newList);
	}

	/*
	 * Convert from calendar statuses to suggestions which will be passed to
	 * front end
	 */
	private LinkedList<List<Suggestion>> convertToSuggestions(List<List<CalendarStatus>> statuses) {
		LinkedList<List<Suggestion>> listSuggestions = new LinkedList<List<Suggestion>>();
		Iterator<List<CalendarStatus>> it = statuses.iterator();
		while (it.hasNext()) {
			listSuggestions.add(convert(it.next()));
		}
		return listSuggestions;
	}

	/* Convert system's statuses into Suggestions */
	public List<Suggestion> convert(List<CalendarStatus> statuses) {
		List<Suggestion> suggestions = new LinkedList<Suggestion>();
		Iterator<CalendarStatus> iterator = statuses.iterator();
		while (iterator.hasNext()) {
			CalendarStatus status = iterator.next();
			IEvent event = status.getEvent();
			double additionalTime = status.getAdditionalEventTime();
			Suggestion result;
			if (event.getDuration() + additionalTime == 0) {
				result = new DeleteSuggestion(event);
			} else if (status.containsProposal()) {
				Calendar end = Utilities.copyCalendar(event.getStartDate(), (int) (additionalTime + event.getDuration()));
				event.setEndDate(end);
				result = new InsertSuggestion(event);
			} else {
				Calendar end = Utilities.copyCalendar(event.getEndDate(), (int) additionalTime);
				result = new RescheduleSuggestion(event, event.getStartDate(), end);
			}
			if (status.hasAlternatives()) {
				/* Convert any alternative statuses also */
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

	/*
	 * Work out overall sphere coefficients considering current events. Create
	 * SphereInfo's for each sphere Define initial calendar status
	 */
	public CalendarStatus checkGoals(List<? extends IEvent> events, Map<SphereName, Double> choices) throws IOException {
		List<BaseCalendarSlot> freeSlots = getFreeSlots(events);
		Map<SphereName, Double> times = new HashMap<SphereName, Double>();
		initializeTimes(times, choices.keySet());
		Map<SphereName, Double> currentRatios = new HashMap<SphereName, Double>();
		double sum = 0;
		for (IEvent event : events) {
			double durationInMins = event.getDuration();
			Map<SphereName, Double> sphereInfluences = event.getSpheres();
			Set<SphereName> keys = sphereInfluences.keySet();
			for (SphereName key : keys) {
				double time = Math.round(sphereInfluences.get(key) * durationInMins);
				times.put(key, times.get(key) + time);
			}
			sum += durationInMins;
		}
		for (SphereName key : times.keySet()) {
			currentRatios.put(key, times.get(key) / sum);
		}
		Map<SphereName, SphereInfo> sphereResults = generateSphereResults(choices, currentRatios, times);
		return new CalendarStatus(sum, sphereResults, freeSlots);
	}

	/* Find slots of free time in between events */
	private List<BaseCalendarSlot> getFreeSlots(List<? extends IEvent> events) {
		LinkedList<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Collections.sort(events);
		Iterator<? extends IEvent> it = events.iterator();
		IEvent beginning = it.next();
		/* Remove begin event */
		it.remove();
		IEvent curr = beginning;
		while (it.hasNext()) {
			IEvent next = it.next();
			if (curr.getEndDate().compareTo(next.getStartDate()) < 0) {
				BaseCalendarSlot newSlot = new BaseCalendarSlot("Free Slot", null, curr.getEndDate(), next.getStartDate());
				ret.add(newSlot);
				Pair<Double, Double> durationInterval = curr.getDurationInterval();
				/*
				 * If the free slot is less than 1h long, adjust curr's duration
				 * interval to include this shorter time, not 1h
				 */
				durationInterval.setSecond(Math.min(durationInterval.getSecond(), curr.getDuration() + newSlot.getDuration()));
				curr = next;
			} else {
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

	private Map<SphereName, SphereInfo> generateSphereResults(Map<SphereName, Double> choices, Map<SphereName, Double> currentRatios,
			Map<SphereName, Double> times) {
		Map<SphereName, SphereInfo> sphereResults = new HashMap<SphereName, SphereInfo>();
		for (SphereName key : times.keySet()) {
			SphereInfo info = new SphereInfo(currentRatios.get(key), choices.get(key), times.get(key));
			sphereResults.put(key, info);
		}
		return sphereResults;
	}

	// private void printEvents(Collection<? extends ICalendarSlot> events) {
	// for (ICalendarSlot event : events)
	// System.out.println(event.getTitle() + "  " +
	// printDate(event.getStartDate()) + "  " + printDate(event.getEndDate()));
	// }
	//
	// private String printDate(Calendar cal) {
	// return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) +
	// "/" + cal.get(Calendar.YEAR) + "  " + cal.get(Calendar.HOUR_OF_DAY)
	// + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
	// }

	private static void initializeTimes(Map<SphereName, Double> times, Set<SphereName> keys) {
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
			Map<SphereName, SphereInfo> sphereInfos = generateSphereInfos(new double[] { 2.5, 4.5, 0.5, 7.5 });
			CalendarStatus other = new CalendarStatus(10.0, sphereInfos, a.getFreeSlots(events));
			for (IEvent event : events) {
				CalendarStatus c1 = new CalendarStatus(event, other);
				statuses.add(c1);
			}
			List<Suggestion> suggestions = a.convert(statuses);
			assertTrue("Not all conversions successful", statuses.size() == suggestions.size());
			boolean s1 = suggestions.get(0) instanceof RescheduleSuggestion;
			boolean s2 = suggestions.get(1) instanceof DeleteSuggestion;
			boolean s3 = suggestions.get(2) instanceof RescheduleSuggestion;
			assertTrue("Converting statuses into suggestions broken", s1 & s2 & s3);
		}

		@Test
		public void testGetFreeSlotsBaseCase() {
			events = sampleEvents();
			IEvent reschedule1 = events.get(1);
			double oldIntervalReschedule1 = reschedule1.getDurationInterval().getSecond();
			List<BaseCalendarSlot> freeSlots = a.getFreeSlots(events);

			BaseCalendarSlot fs1 = freeSlots.get(0);
			double fs1Duration = fs1.getDuration();
			boolean size1 = fs1Duration == 780;
			boolean startFS1 = fs1.getStartDate().equals(new GregorianCalendar(2000, 3, 3, 0, 0, 0));
			boolean endFS1 = fs1.getEndDate().equals(new GregorianCalendar(2000, 3, 3, 13, 0, 0));

			BaseCalendarSlot fs2 = freeSlots.get(1);
			double fs2Duration = fs2.getDuration();
			boolean size2 = fs2Duration == 45;
			boolean startFS2 = fs2.getStartDate().equals(new GregorianCalendar(2000, 3, 3, 14, 45, 0));
			boolean endFS2 = fs2.getEndDate().equals(new GregorianCalendar(2000, 3, 3, 15, 30, 0));

			double newIntervalFs2 = reschedule1.getDurationInterval().getSecond();
			double reschedule1Duration = reschedule1.getDuration();
			boolean interval2 = newIntervalFs2 == Math.min(oldIntervalReschedule1, reschedule1Duration + fs2Duration);
			assertTrue("Basic case where curr.end < next.start does not work", size1 & startFS1 & endFS1 & size2 & startFS2 & endFS2 & interval2);
		}

		@Test
		public void testGetFreeSlotsSwallows() {
			events = sampleEvents();
			List<BaseCalendarSlot> freeSlots = a.getFreeSlots(events);
			boolean size = freeSlots.size() == 5;
			boolean startFS3 = freeSlots.get(2).getStartDate().equals(new GregorianCalendar(2000, 3, 3, 15, 30, 0));
			boolean endFS3 = freeSlots.get(2).getEndDate().equals(new GregorianCalendar(2000, 3, 3, 16, 0, 0));
			boolean startFS4 = freeSlots.get(3).getStartDate().equals(new GregorianCalendar(2000, 3, 3, 18, 30, 0));
			boolean endFS4 = freeSlots.get(3).getEndDate().equals(new GregorianCalendar(2000, 3, 3, 19, 30, 0));
			assertTrue("Case where one event entirely overlaps another does not work", size & startFS3 & endFS3 & startFS4 & endFS4);
		}

		@Test
		public void testGetFreeSlotsExtendsBeyond() {
			events = sampleEvents();
			IEvent reschedule5 = events.get(6);
			a.getFreeSlots(events);
			Pair<Double, Double> newIntervalFs5 = reschedule5.getDurationInterval();
			double reschedule5Duration = reschedule5.getDuration();
			boolean setIntervalToDuration = newIntervalFs5.getSecond() == reschedule5Duration;
			assertTrue("Case where one event entirely overlaps another does not work", setIntervalToDuration);
		}

		@Test
		public void testRemoveStaticEvents() {
			events = sampleEvents();
			double sizeBefore = events.size();
			a.removeStaticEvents(events);
			double sizeAfter = events.size();
			boolean size = sizeBefore != sizeAfter + 1;
			for (IEvent event : events) {
				assertTrue("An event that cannot be rescheduled is not removed properly", !event.getTitle().equals("reschedule 6"));
			}
			assertTrue("A rescheduable event might have been removed", size);
		}

		@Test
		public void testIsCloseEnough() {
			/*
			 * Not sure what figures I need for si1 and si2! events =
			 * sampleEvents(); Map<SphereName, SphereInfo> si1 =
			 * generateSphereInfos(new double[]{0.1, 0.3, 0.3, 0.4});
			 * Map<SphereName, SphereInfo> si2 = generateSphereInfos(new
			 * double[]{0.3, 0.3, 0.3, 0.1}); CalendarStatus c1 = new
			 * CalendarStatus(events.get(1), new CalendarStatus(100.0, si1));
			 * CalendarStatus c2 = new CalendarStatus(events.get(2), new
			 * CalendarStatus(100.0, si2)); assertTrue("", !a.isCloseEnough(c1,
			 * true)); assertTrue("", a.isCloseEnough(c2, true)); assertTrue("",
			 * !a.isCloseEnough(c1, false)); assertTrue("", a.isCloseEnough(c2,
			 * false));
			 */
		}

		@Test
		public void testGetSortedStatuses() {
			events = sampleEvents();
			events.remove(0);
			events.remove(events.size() - 1);
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
			Map<SphereName, SphereInfo> results = a.generateSphereResults(generateSpheres(new double[] { 0.1, 0.3, 0.3, 0.4 }), currentRatios, times);
			CalendarStatus start = new CalendarStatus(sum, results, a.getFreeSlots(events));
			List<CalendarStatus> sortedStatuses = a.generateEventStatuses(events, start);
			assertTrue("Not all events translated into statuses correctly", sortedStatuses.size() == events.size());
			double min = 0.0;
			for (CalendarStatus status : sortedStatuses) {
				assertTrue("Get sorted statuses orders events incorectly", status.getCoefficient() >= min);
				min = status.getCoefficient();
			}
		}

		@Test
		public void testCheckGoals() {
			events = sampleEvents();
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
			Map<SphereName, Double> choices = generateSpheres(new double[] { 0.1, 0.3, 0.3, 0.4 });
			try {
				CalendarStatus start = a.checkGoals(events, choices);
				assertTrue("Incorrect coefficient in sphere info creation", start.getCoefficient() == 122035.3982300885);
				assertTrue("Incorrect userBusyTime in sphere info creation", start.getUserBusyTime() == sum);
				for (SphereName sn : start.getSphereResults().keySet()) {
					SphereInfo si = start.getSphereResults().get(sn);
					assertTrue("Sphere time incorrect in sphere info creation", si.getSphereTotalTime() == times.get(sn));
					assertTrue("Sphere current ratio incorrect in sphere info creation", si.getCurrentRatio() == currentRatios.get(sn));
				}
			} catch (Exception e) {
				fail("Checking goals failed - exception thrown: " + e.getLocalizedMessage());
			}
		}

		/* Testing utils */
		private HashMap<SphereName, SphereInfo> generateSphereInfos(double[] values) {
			SphereName[] names = SphereName.values();
			HashMap<SphereName, SphereInfo> res = new HashMap<SphereName, SphereInfo>();
			for (int i = 0; i < names.length; i++) {
				SphereInfo si = new SphereInfo(values[i], values[i], 10.0);
				res.put(names[i], si);
			}
			return res;
		}

		private HashMap<SphereName, Double> generateSpheres(double[] values) {
			SphereName[] names = SphereName.values();
			HashMap<SphereName, Double> res = new HashMap<SphereName, Double>();
			for (int i = 0; i < names.length; i++) {
				res.put(names[i], values[i]);
			}
			return res;
		}

		private List<? extends IEvent> sampleEvents() {
			Suggestion begin = new RescheduleSuggestion("begin", null, new GregorianCalendar(2000, 3, 3, 0, 0, 0), new GregorianCalendar(2000, 3, 3,
					0, 0, 0));
			begin.setDeurationInterval(0, 0);
			Suggestion end = new RescheduleSuggestion("end", null, new GregorianCalendar(2000, 3, 3, 23, 59, 59), new GregorianCalendar(2000, 3, 3,
					23, 59, 59));
			end.setDeurationInterval(0, 0);
			/* Should remain a RescheduleSuggestion */
			Suggestion s1 = new RescheduleSuggestion("reschedule 1", null, new GregorianCalendar(2000, 3, 3, 13, 0, 0), new GregorianCalendar(2000,
					3, 3, 14, 45, 0));
			s1.setSpheres(generateSpheres(new double[] { 0.1, 0.3, 0.3, 0.4 }));
			s1.setDeurationInterval(30, 151);
			s1.setReschedule(true);
			/* Should get converted to a DeleteSuggestion */
			Suggestion s2 = new RescheduleSuggestion("delete", null, new GregorianCalendar(2000, 3, 3, 15, 30, 0), new GregorianCalendar(2000, 3, 3,
					15, 30, 0));
			s2.setSpheres(generateSpheres(new double[] { 0.3, 0.3, 0.3, 0.1 }));
			s2.setDeurationInterval(0, 40);
			s2.setReschedule(true);
			/* Should get converted to a RescheduleSuggestion */
			Suggestion s3 = new InsertSuggestion("reschedule 2", null, new GregorianCalendar(2000, 3, 3, 16, 00, 0), new GregorianCalendar(2000, 3,
					3, 18, 30, 0));
			s3.setSpheres(generateSpheres(new double[] { 0.0, 1.0, 0.0, 0.0 }));
			s3.setDeurationInterval(0, 170);
			s3.setReschedule(true);
			/* Special case - s4 and s5 fits inside s3 */
			Suggestion s4 = new InsertSuggestion("reschedule 3", null, new GregorianCalendar(2000, 3, 3, 16, 30, 0), new GregorianCalendar(2000, 3,
					3, 17, 20, 0));
			s4.setSpheres(generateSpheres(new double[] { 0.0, 1.0, 0.0, 0.0 }));
			s4.setDeurationInterval(0, 100);
			s4.setReschedule(true);
			Suggestion s5 = new InsertSuggestion("reschedule 4", null, new GregorianCalendar(2000, 3, 3, 16, 30, 0), new GregorianCalendar(2000, 3,
					3, 18, 20, 0));
			s5.setSpheres(generateSpheres(new double[] { 0.0, 1.0, 0.0, 0.0 }));
			s5.setDeurationInterval(0, 61);
			s5.setReschedule(true);
			/* s6 ends before s7 does */
			Suggestion s6 = new InsertSuggestion("reschedule 5", null, new GregorianCalendar(2000, 3, 3, 19, 30, 0), new GregorianCalendar(2000, 3,
					3, 20, 20, 0));
			s6.setSpheres(generateSpheres(new double[] { 0.0, 1.0, 0.0, 0.0 }));
			s6.setDeurationInterval(0, 60);
			s6.setReschedule(true);
			Suggestion s7 = new InsertSuggestion("reschedule 6", null, new GregorianCalendar(2000, 3, 3, 19, 40, 0), new GregorianCalendar(2000, 3,
					3, 21, 20, 0));
			s7.setSpheres(generateSpheres(new double[] { 0.0, 1.0, 0.0, 0.0 }));
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