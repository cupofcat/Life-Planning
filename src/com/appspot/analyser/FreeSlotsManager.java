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

	private void chooseSlot(List<BaseCalendarSlot> possibleSlots) {
		IEvent event = status.getEvent();
		BaseCalendarSlot chosenSlot = null;
		for (BaseCalendarSlot slot : possibleSlots) {
			if (slot.getDuration() >= status.getEvent().getDuration() + status.getAdditionalEventTime()) {
				chosenSlot = slot;
				chosenSlot.setDuration(status.getEvent().getDuration() + status.getAdditionalEventTime());
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

	public List<BaseCalendarSlot> getPossibleSlots(Proposal proposal) {
		List<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Pair<Calendar, Calendar> timeSlot = proposal.getPossibleTimeSlot();
		Double minDuration = proposal.getDurationInterval().getFirst();
		Double maxDuration = proposal.getDurationInterval().getSecond();
		double nextDuration, currentMax = 0;
		Utilities.printEvents(freeSlots);
		Calendar c = proposal.getStartDate();
		Calendar c2 = proposal.getEndDate();
		for (BaseCalendarSlot slot : freeSlots) {
			if (Utilities.compareHours(timeSlot.getSecond(), slot.getStartDate()) > 0
					&& Utilities.compareHours(timeSlot.getFirst(), slot.getEndDate()) < 0) {
				Calendar slotStartDate = slot.getStartDate();
				Calendar slotEndDate = slot.getEndDate();
				Calendar start = Utilities.max(timeSlot.getFirst(), slotStartDate);
				start.set(slotStartDate.get(Calendar.YEAR), slotStartDate.get(Calendar.MONTH), slotStartDate.get(Calendar.DAY_OF_MONTH));

				Calendar tmp = new GregorianCalendar();
				tmp.setTimeInMillis(start.getTimeInMillis() + (long) (maxDuration * 60000));
				Calendar endSlot = Utilities.min(timeSlot.getSecond(), slotEndDate);
				Calendar end = Utilities.min(tmp, endSlot);
				end.set(slotEndDate.get(Calendar.YEAR), slotEndDate.get(Calendar.MONTH), slotEndDate.get(Calendar.DAY_OF_MONTH));

				nextDuration = Utilities.getDuration(start, end);
				if (nextDuration > minDuration) {
					ret.add(new BaseCalendarSlot("Best fit", null, start, end));
					if (nextDuration > currentMax)
						currentMax = nextDuration;
				}
			} else if (Utilities.compareHours(timeSlot.getSecond(), slot.getStartDate()) < 0)
				break;
		}
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

	public List<BaseCalendarSlot> getFreeSlots() {
		return freeSlots;
	}

}
