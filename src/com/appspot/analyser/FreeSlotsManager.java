package com.appspot.analyser;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FreeSlotsManager {

	private List<BaseCalendarSlot> freeSlots;
	private CalendarStatus status;

	public FreeSlotsManager(List<BaseCalendarSlot> freeSlots, CalendarStatus status) {
		super();
		setFreeSlots(freeSlots);
		this.status = status;
	}

	public FreeSlotsManager(List<BaseCalendarSlot> freeSlots, List<BaseCalendarSlot> possibleSlots, CalendarStatus status) {
		this(freeSlots, status);
		chooseSlot(possibleSlots);
	}
	
	public void setFreeSlots(List<BaseCalendarSlot> slots){
		freeSlots = slots;
	}

	public CalendarStatus checkProposal(Proposal proposal) {
		List<BaseCalendarSlot> possibleSlots = getPossibleSlots(proposal);
		if (possibleSlots != null) {
			return new CalendarStatus(proposal, status, Utilities.copyFreeSlots(freeSlots), possibleSlots);
		}
		return null;
	}

	public List<BaseCalendarSlot> getPossibleSlots(Proposal proposal) {
		List<BaseCalendarSlot> ret = new LinkedList<BaseCalendarSlot>();
		Pair<Calendar, Calendar> possibleTimeSlot = proposal.getPossibleTimeSlot();
		Double minDuration = proposal.getDurationInterval().getFirst();
		Double maxDuration = proposal.getDurationInterval().getSecond();
		double nextDuration, currentMax = 0;
		Calendar slotStartDate, slotEndDate;
		for (BaseCalendarSlot freeSlot : freeSlots) {
			BaseCalendarSlot hourSlot = generateStartingSlot(freeSlot.getStartDate(), possibleTimeSlot);
			Calendar possibleStartDate = hourSlot.getStartDate();
			Calendar possibleEndDate = hourSlot.getEndDate();
			slotStartDate = freeSlot.getStartDate();
			slotEndDate = freeSlot.getEndDate();
			/* proposal fits inside the free slot */

			// Change comparisons here and check final output
			while (possibleStartDate.compareTo(slotEndDate) < 0) {
				if (possibleEndDate.compareTo(slotStartDate) > 0 && possibleStartDate.compareTo(slotEndDate) < 0) {

					Calendar start = Utilities.max(possibleStartDate, slotStartDate);

					Calendar tmp = new GregorianCalendar(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), start
							.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), 0);
					tmp.add(Calendar.MINUTE, (int) ((double) maxDuration));

					Calendar endSlot = Utilities.min(possibleEndDate, slotEndDate);

					Calendar end = Utilities.min(tmp, endSlot);

					BaseCalendarSlot candidate = new BaseCalendarSlot("Best fit", null, start, end);
					nextDuration = candidate.getDuration();
					if (nextDuration > minDuration) {
						ret.add(candidate);
						if (nextDuration > currentMax)
							currentMax = nextDuration;
					}
				}
				possibleStartDate.add(Calendar.DAY_OF_MONTH, 1);
				possibleEndDate.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		if (!ret.isEmpty()) {
			if (currentMax > 0) {
				proposal.getDurationInterval().setSecond(currentMax);
			}
			Collections.sort(ret);
			return ret;
		}
		return null;
	}
	
	public void updateEventMaxDuration() {
		IEvent event = status.getEvent();
		Calendar endDate = event.getEndDate();
		if (!status.containsProposal()) {
			if (status.getAdditionalEventTime() > 0)
				endDate.add(Calendar.MINUTE, (int) status.getAdditionalEventTime());
			else
				return;
		}
		for(BaseCalendarSlot freeSlot : freeSlots){
			if(freeSlot.getStartDate().equals(endDate)){
				Pair<Double,Double> possibleDuration = event.getDurationInterval();
				possibleDuration.setSecond(Math.min(possibleDuration.getSecond(), 
						event.getDuration() + freeSlot.getDuration()));
				break;
			}
		}
	}

	public void updateCurrentSlots(CalendarStatus stat) {
		IEvent takenSlot = stat.getEvent();
		Calendar takenStartDate = takenSlot.getStartDate();
		Calendar takenEndDate = takenSlot.getEndDate();
		if (!stat.containsProposal()) {
			if (stat.getAdditionalEventTime() > 0)
				takenEndDate.add(Calendar.MINUTE, (int) stat.getAdditionalEventTime());
			else
				return;
		}
		List<BaseCalendarSlot> toBeUpdated = new LinkedList<BaseCalendarSlot>();
		for (BaseCalendarSlot freeSlot : freeSlots) {
			Calendar slotStartDate = freeSlot.getStartDate();
			Calendar slotEndDate = freeSlot.getEndDate();
			if (takenEndDate.compareTo(slotStartDate) > 0 && takenStartDate.compareTo(slotEndDate) < 0)
				toBeUpdated.add(freeSlot);
		}
		for (BaseCalendarSlot slot : toBeUpdated) {
			Calendar slotStartDate = slot.getStartDate();
			Calendar slotEndDate = slot.getEndDate();
			Calendar start = Utilities.max(takenStartDate, slotStartDate);
			Calendar end = Utilities.min(takenEndDate, slotEndDate);
			BaseCalendarSlot chosen = new BaseCalendarSlot("Free Slot", null, start, end);
			freeSlots.remove(slot);
			this.splitSlot(slot, chosen);
		}
	}
	
	public void sortFreeSlots(){
		Collections.sort(freeSlots);
	}

	private void printSlot(BaseCalendarSlot hourSlot) {
		List<BaseCalendarSlot> hourslots = new LinkedList<BaseCalendarSlot>();
		hourslots.add(hourSlot);
	}

	private BaseCalendarSlot generateStartingSlot(Calendar slotStart, Pair<Calendar, Calendar> possibleTimeSlot) {
		Calendar possibleStart = possibleTimeSlot.getFirst();
		Calendar possibleEnd = possibleTimeSlot.getSecond();
		Calendar start = new GregorianCalendar(slotStart.get(Calendar.YEAR), slotStart.get(Calendar.MONTH), slotStart.get(Calendar.DAY_OF_MONTH),
				possibleStart.get(Calendar.HOUR_OF_DAY), possibleStart.get(Calendar.MINUTE), 0);
		Calendar end = new GregorianCalendar(slotStart.get(Calendar.YEAR), slotStart.get(Calendar.MONTH), slotStart.get(Calendar.DAY_OF_MONTH),
				possibleEnd.get(Calendar.HOUR_OF_DAY), possibleEnd.get(Calendar.MINUTE), 0);
		if (possibleStart.get(Calendar.HOUR_OF_DAY) > possibleEnd.get(Calendar.HOUR_OF_DAY)) {
			start.add(Calendar.DAY_OF_MONTH, -1);
		}
		return new BaseCalendarSlot("Best fit", null, start, end);
	}

	/* If possible, pick a free slot for this event */
	private void chooseSlot(List<BaseCalendarSlot> possibleSlots) {
		/* possibleSlots = when an event can be scheduled */
		IEvent event = status.getEvent();
		BaseCalendarSlot chosenSlot = null;
		double eventDuration = status.getEvent().getDuration() + status.getAdditionalEventTime();
		int start = 0, end = 0;
		for (BaseCalendarSlot slot : possibleSlots) {
			if (chosenSlot == null) {
				if (slot.getDuration() >= eventDuration) {
					end = start + 1;
					chosenSlot = slot;
				} else
					start++;

			} else if (slot.compareTo(chosenSlot) == 0)
				end++;
			else
				break;
		}
		Random rand = new Random();
		chosenSlot = possibleSlots.get(rand.nextInt(end - start) + start);
		chosenSlot.setDuration(eventDuration);
		int index = 0;
		BaseCalendarSlot currentSlot = freeSlots.get(index);
		while (!(currentSlot.getStartDate().compareTo(chosenSlot.getStartDate()) <= 0 && currentSlot.getEndDate().compareTo(chosenSlot.getEndDate()) >= 0)) {
			index++;
			currentSlot = freeSlots.get(index);
		}
		BaseCalendarSlot removedSlot = freeSlots.remove(index);
		splitSlot(removedSlot, chosenSlot);
		sortFreeSlots();
		event.setStartDate(chosenSlot.getStartDate());
		event.setEndDate(chosenSlot.getEndDate());
	}

	private void splitSlot(BaseCalendarSlot removedSlot, BaseCalendarSlot chosenSlot) {
		if (removedSlot.getStartDate().before(chosenSlot.getStartDate())) {
			freeSlots.add(new BaseCalendarSlot("Free Slot", null, removedSlot.getStartDate(), chosenSlot.getStartDate()));
		}
		if (removedSlot.getEndDate().after(chosenSlot.getEndDate())) {
			freeSlots.add(new BaseCalendarSlot("Free Slot", null, chosenSlot.getEndDate(), removedSlot.getEndDate()));
		}
	}

	public List<BaseCalendarSlot> getFreeSlots() {
		return freeSlots;
	}
}