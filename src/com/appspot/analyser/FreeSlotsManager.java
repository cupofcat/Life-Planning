package com.appspot.analyser;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class FreeSlotsManager {

	private List<BaseCalendarSlot> freeSlots;
	private CalendarStatus status;

	public FreeSlotsManager(List<BaseCalendarSlot> freeSlots, CalendarStatus status) {
		super();
		this.freeSlots = freeSlots;
		this.status = status;
	}

	public FreeSlotsManager(List<BaseCalendarSlot> freeSlots, List<BaseCalendarSlot> possibleSlots, CalendarStatus status) {
		this(freeSlots, status);
		chooseSlot(possibleSlots);
	}

	public CalendarStatus checkProposal(Proposal proposal) {
		List<BaseCalendarSlot> possibleSlots = getPossibleSlots(proposal);
		if (possibleSlots != null) {
			List<BaseCalendarSlot> freeSlotsCopy = new LinkedList<BaseCalendarSlot>();
			freeSlotsCopy.addAll(freeSlots);
			return new CalendarStatus(proposal, status, freeSlotsCopy, possibleSlots);
		}
		return null;
	}

	/*
	 * Find suitable free slots for proposal Adjust proposal's max duration
	 * considering slots found
	 */

	/*
	 * end za pocz i pocz przed koncem free slota - condition 
	 * 1. generate	 * starting skoczek - take possible time and generate it using start date
	 * from free slot taking into account 2 cases : usual and across the day(hours 23 - 2:00) 
	 * 2. compare hours usually to check - modify comparisons and generate length using Calendar.add 
	 *3. add 24 hours to start/end date of skoczek and compare again until start date > freeSlot.endDate
	 */
	public List<BaseCalendarSlot> getPossibleSlots(Proposal proposal) {
		List<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Pair<Calendar, Calendar> possibleTimeSlot = proposal.getPossibleTimeSlot();
		Double minDuration = proposal.getDurationInterval().getFirst();
		Double maxDuration = proposal.getDurationInterval().getSecond();
		double nextDuration, currentMax = 0;
		for (BaseCalendarSlot freeSlot : freeSlots) {
			BaseCalendarSlot hourSlot = generateStartingSlot(freeSlot.getStartDate(), possibleTimeSlot);
			printSlot(hourSlot);
			while (hourSlot.getStartDate().compareTo(freeSlot.getEndDate()) < 0) {
				/* proposal fits inside the free slot */
				
				//Change comparisons here and check final output
				
				if (Utilities.compareHours(possibleTimeSlot.getSecond(), freeSlot.getStartDate()) > 0
						&& Utilities.compareHours(possibleTimeSlot.getFirst(), freeSlot.getEndDate()) < 0) {
					Calendar slotStartDate = freeSlot.getStartDate();
					Calendar slotEndDate = freeSlot.getEndDate();
					/* earliest possible start */
					Calendar start = Utilities.max(possibleTimeSlot.getFirst(), slotStartDate);
					start.set(slotStartDate.get(Calendar.YEAR), slotStartDate.get(Calendar.MONTH), slotStartDate.get(Calendar.DAY_OF_MONTH));
					Calendar tmp = new GregorianCalendar();
					tmp.setTimeInMillis(start.getTimeInMillis() + (long) (maxDuration * 60000));
					Calendar endSlot = Utilities.min(possibleTimeSlot.getSecond(), slotEndDate);
					/* latest possible end */
					Calendar end = Utilities.min(tmp, endSlot);
					end.set(slotEndDate.get(Calendar.YEAR), slotEndDate.get(Calendar.MONTH), slotEndDate.get(Calendar.DAY_OF_MONTH));
					nextDuration = Utilities.getDuration(start, end);
					if (nextDuration > minDuration) {
						ret.add(new BaseCalendarSlot("Best fit", null, start, end));
						if (nextDuration > currentMax)
							currentMax = nextDuration;
					}
				}
				hourSlot.getStartDate().add(Calendar.DAY_OF_MONTH, 1);
				hourSlot.getEndDate().add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		Utilities.printEvents(ret);
		if (!ret.isEmpty()) {
			if (currentMax > 0) {
				proposal.getDurationInterval().setSecond(currentMax);
			}
			Collections.sort(ret);
			Utilities.printEvents(ret);
			return ret;
		}
		return null;
	}

	private void printSlot(BaseCalendarSlot hourSlot) {
		List<BaseCalendarSlot> hourslots = new LinkedList<BaseCalendarSlot>();
		hourslots.add(hourSlot);
		Utilities.printEvents(hourslots);
	}

	private BaseCalendarSlot generateStartingSlot(Calendar slotStart, Pair<Calendar, Calendar> possibleTimeSlot) {
		Calendar possibleStart = possibleTimeSlot.getFirst();
		Calendar possibleEnd = possibleTimeSlot.getSecond();
		Calendar start = new GregorianCalendar(slotStart.get(Calendar.YEAR), slotStart.get(Calendar.MONTH), slotStart.get(Calendar.DAY_OF_MONTH),
				possibleStart.get(Calendar.HOUR_OF_DAY), possibleStart.get(Calendar.MINUTE), 0);
		Calendar end = new GregorianCalendar(slotStart.get(Calendar.YEAR), slotStart.get(Calendar.MONTH), slotStart.get(Calendar.DAY_OF_MONTH),
				possibleEnd.get(Calendar.HOUR_OF_DAY), possibleEnd.get(Calendar.MINUTE), 0);
		if (possibleStart.get(Calendar.HOUR_OF_DAY) > possibleEnd.get(Calendar.HOUR_OF_DAY)) {
			end.add(Calendar.DAY_OF_MONTH, 1);
		}
		return new BaseCalendarSlot("Best fit", null, start, end);
	}

	/* If possible, pick a free slot for this event */
	private void chooseSlot(List<BaseCalendarSlot> possibleSlots) {
		/* possibleSlots = when an event can be scheduled */
		IEvent event = status.getEvent();
		BaseCalendarSlot chosenSlot = null;
		for (BaseCalendarSlot slot : possibleSlots) {
			double eventDuration = status.getEvent().getDuration() + status.getAdditionalEventTime();
			if (slot.getDuration() >= eventDuration) {
				chosenSlot = slot;
				chosenSlot.setDuration(eventDuration);
				break;
			}
		}
		int index = 0;
		BaseCalendarSlot currentSlot = freeSlots.get(index);
		while (!(currentSlot.getStartDate().compareTo(chosenSlot.getStartDate()) <= 0 && currentSlot.getEndDate().compareTo(chosenSlot.getEndDate()) >= 0)) {
			index++;
			currentSlot = freeSlots.get(index);
		}
		BaseCalendarSlot removedSlot = freeSlots.remove(index);
		splitSlot(removedSlot, chosenSlot);
		Collections.sort(freeSlots);
		Utilities.printEvents(freeSlots);
		event.setStartDate(chosenSlot.getStartDate());
		event.setEndDate(chosenSlot.getEndDate());
	}

	private void splitSlot(BaseCalendarSlot removedSlot, BaseCalendarSlot chosenSlot) {
		if (removedSlot.getStartDate().before(chosenSlot.getStartDate())) {
			freeSlots.add(new BaseCalendarSlot(removedSlot.getStartDate(), chosenSlot.getStartDate()));
		}
		if (removedSlot.getEndDate().after(chosenSlot.getEndDate())) {
			freeSlots.add(new BaseCalendarSlot(chosenSlot.getEndDate(), removedSlot.getEndDate()));
		}
	}

	public List<BaseCalendarSlot> getFreeSlots() {
		return freeSlots;
	}
}