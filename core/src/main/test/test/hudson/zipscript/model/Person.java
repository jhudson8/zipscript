/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Person {

	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	private String firstName;
	private String lastName;
	private Date birthday;

	public Person(String firstName, String lastName, String birthday)
			throws ParseException {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = sdf.parse(birthday);
	}

	public int getId() {
		return new String(getFirstName() + getLastName()).hashCode();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(birthday);
		int year = cal.get(Calendar.YEAR);
		cal.setTimeInMillis(System.currentTimeMillis());
		int year2 = cal.get(Calendar.YEAR);
		return year2 - year;
	}
}
