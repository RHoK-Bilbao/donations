package org.rhok.microdonaciones.data.models;

public class MDProject {
	private String title;
	private String imgName;
	private String fundation;
	private String description;
	private int actualRecaudation;
	private int objetiveRecaudation;
	private String extraInfoUrl;
	private String donateUrl;
	
	public MDProject(){
	}
	
	public MDProject (String title, String imgName, String fundation,String description, int actualRecaudation,int objetiveRecaudation,  String extraInfoUrl, String donateUrl){
		this.title=title;
		this.imgName=imgName;
		this.fundation=fundation;
		this.description=description;
		this.actualRecaudation=actualRecaudation;
		this.objetiveRecaudation=objetiveRecaudation;
		this.extraInfoUrl=extraInfoUrl;
		this.donateUrl=donateUrl;
	}

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgName() {
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	public String getFundation() {
		return fundation;
	}
	public void setFundation(String fundation) {
		this.fundation = fundation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getActualRecaudation() {
		return actualRecaudation;
	}
	public void setActualRecaudation(int actualRecaudation) {
		this.actualRecaudation = actualRecaudation;
	}
	public int getObjetiveRecaudation() {
		return objetiveRecaudation;
	}
	public void setObjetiveRecaudation(int objetiveRecaudation) {
		this.objetiveRecaudation = objetiveRecaudation;
	}
	public String getExtraInfoUrl() {
		return extraInfoUrl;
	}
	public void setExtraInfoUrl(String extraInfoUrl) {
		this.extraInfoUrl = extraInfoUrl;
	}
	public String getDonateUrl() {
		return donateUrl;
	}
	public void setDonateUrl(String donateUrl) {
		this.donateUrl = donateUrl;
	}

	

	
}
