package test;

public class Customer {
	private int id;
	private String first_name;
	private String last_name;
	private String cc_id;
	private String address;
	private String email;
	private String password;

	public Customer(int id, String first_name, String last_name, String cc_id, String address, String email,
			String password) {
		this.setId(id);
		this.setFirst_name(first_name);
		this.setLast_name(last_name);
		this.setCc_id(cc_id);
		this.setAddress(address);
		this.setEmail(email);
		this.setPassword(password);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getCc_id() {
		return cc_id;
	}

	public void setCc_id(String cc_id) {
		this.cc_id = cc_id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}