package com.dchavez.camunda.async.events.model;

public class CamundaStartModel {
	
	private String type;
	private String action;
	private String pId;

	public CamundaStartModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public CamundaStartModel(String type, String action, String pId) {
		super();
		this.type = type;
		this.action = action;
		this.pId = pId;	}

}
