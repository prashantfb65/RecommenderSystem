package com.ci.bean;
/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             06-March-2017
Copyright:                University College Dublin
Subject:				  COLLECTIVE INTELLIGENCE (Recommender System)
Description:              Pojo File handling CSV Print Structure
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

public class CSVPojo {

	private String userID;				//Will hold the value for User ID
	private String item;				//Will hold the value for Item ID
	private double actualRating;		//Will hold the value of Actual Rating
	private double predictedRating;		//Will hold the value of Predicted Rating
	private double rmse;				//Will hold the value of Overall RMSE

	/**
	 * Getter Setter 
	 *
	 */	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public double getActualRating() {
		return actualRating;
	}
	public void setActualRating(double actualRating) {
		this.actualRating = actualRating;
	}
	public double getPredictedRating() {
		return predictedRating;
	}
	public void setPredictedRating(double predictedRating) {
		this.predictedRating = predictedRating;
	}
	public double getRmse() {
		return rmse;
	}
	public void setRmse(double rmse) {
		this.rmse = rmse;
	}
	/**
	 * End
	 */

}
/**
 * End of File
 */