package model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Person {

	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	private String firstName;
	private String lastName;
	private Date birthday;
	private int numAccounts;
	private BigDecimal netWorth;

	public Person (
			String firstName, String lastName, String birthday, int numAccounts, BigDecimal netWorth)
	throws ParseException {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = sdf.parse(birthday);
		this.numAccounts = numAccounts;
		this.netWorth = netWorth;
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

	public int getNumAccounts() {
		return numAccounts;
	}

	public void setNumAccounts(int numAccounts) {
		this.numAccounts = numAccounts;
	}

	public BigDecimal getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(BigDecimal netWorth) {
		this.netWorth = netWorth;
	}
}
