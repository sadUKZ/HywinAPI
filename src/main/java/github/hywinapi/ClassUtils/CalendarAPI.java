package github.hywinapi.ClassUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;

public class CalendarAPI {

	private Calendar getCalendar = null;

	public CalendarAPI(Calendar calendar) {
		setCalendar(calendar);
	}

	public CalendarAPI() {
		setCalendar(Calendar.getInstance());
	}

	private void setCalendar(Calendar calendar) {
		this.getCalendar = calendar;
	}

	private Calendar getCalendar() {
		setCalendar(Calendar.getInstance());
		return this.getCalendar;
	}

	public int getField(int field) {
		return getCalendar().get(field);
	}

	public int getDay() {
		return getCalendar().get(5);
	}

	public int getMonth() {
		return getCalendar().get(2) + 1;
	}

	public int getHors() {
		return getCalendar().get(11);
	}

	public int getMinutes() {
		return getCalendar().get(12);
	}

	public int getSeconds() {
		return getCalendar().get(13);
	}

	public int getYear() {
		return getCalendar().get(1);
	}

	public String getFormatedDate() {
		return getDay() + "/" + getMonth() + "/" + getYear();
	}

	public String getFormatedTime() {
		return getHors() + ":" + getMinutes() + ":" + getSeconds();
	}

	public String getDateFormated() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)");
		return format.format(getDate());
	}

	public Date getDate() {
		return new Date();
	}

	public boolean is(String day, int hours, int minutes, int seconds){
		if(day.equalsIgnoreCase(getDayString()) && hours == getHors() && minutes == getMinutes() && seconds == getSeconds()){
			return true;
		}
		return false;
	}

	public boolean is(String day, int hours, int minutes){
		if(day.equalsIgnoreCase(getDayString()) && hours == getHors() && minutes == getMinutes()){
			return true;
		}
		return false;
	}

	public boolean is(int day, int hours, int minutes, int seconds){
		if(day == getField(7) && hours == getHors() && minutes == getMinutes() && seconds == getSeconds()){
			return true;
		}
		return false;
	}

	public boolean is(int day, int hours, int minutes) {
		if (day == getField(7) && hours == getHors() && minutes == getMinutes()) {
			return true;
		}
		return false;
	}

	public boolean isHours(int hours) {
		if (hours == getHors()) {
			return true;
		}
		return false;
	}

	public boolean isMinutes(int minutes) {
		if (minutes == getMinutes()) {
			return true;
		}
		return false;
	}

	public boolean isSeconds(int seconds) {
		if (seconds == getSeconds()) {
			return true;
		}
		return false;
	}

	public boolean isDay(int day) {
		if (day == getField(7)) {
			return true;
		}
		return false;
	}

	public boolean isDay(String day) {
		if (day.equals(getDayString())) {
			return true;
		}
		return false;
	}

	public String getDayString() {
		switch (getField(7)) {
		case 1: {
			return "Domingo";
		}
		case 2: {
			return "Segunda";
		}
		case 3: {
			return "Terca";
		}
		case 4: {
			return "Quarta";
		}
		case 5: {
			return "Quinta";
		}
		case 6: {
			return "Sexta";
		}
		case 7: {
			return "Sabado";
		}
		}
		return "§cERROR";
	}

	public static void Synchrony() {
		final Calendar calendar = Calendar.getInstance();
		if (calendar.get(13) != 0) {
			send("§3[CalendarAPI] §aSincronizando tempo...");
			final int time = 60;
			final Calendar c2 = Calendar.getInstance();
			final int timelife = time - c2.get(13);
			send("§3[CalendarAPI] §aO servidor vai parar por " + timelife + " segundos, ate o horario ficar cravado.");
			Calendar c3;
			do {
				c3 = Calendar.getInstance();
			} while (c3.get(13) != 0);
			Bukkit.getConsoleSender().sendMessage("§3[CalendarAPI] §aTempo sincronizado");
		}
	}

	private static void send(String string) {
		Bukkit.getConsoleSender().sendMessage(string);
	}

}