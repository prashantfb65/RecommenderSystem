package com.ci.excel;
/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             06-March-2017
Copyright:                University College Dublin
Subject:				  COLLECTIVE INTELLIGENCE (Recommender System)
Description:              Generic File to Generate CSV 
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import com.ci.bean.CSVPojo;

public class CSVGenerator {
	DecimalFormat df = new DecimalFormat("0.000");  	//Formatter for Double data - Precisions

	/**
	 * The Method generateCSV populates List of CSVPojo to the CSV
	 * @param csvToPrint : Takes the Map which is to populated into the CSV
	 * @param technique	: Takes the type of technique used to predict the item ratings
	 * @return
	 */
	public boolean generateCSV(List<CSVPojo> csvToPrint, String technique){
		try{
			/*
			 * Initializing the PrintWriter object to invoke writing of a CSV
			 */
			PrintWriter pw = new PrintWriter(technique + ".csv"); 	//Name and Path of the CSV 
			pw.println( "User" + "," + " Item(Movie)"  + " ," + "Actual Rating" +"," +"Predicted Rating" + "," + "RMSE"); 	//Header of the CSV


			/*
			 * Iterating through the List of CSVPojo to write into the CSV
			 */
			for (CSVPojo singleLineToPrint: csvToPrint){
				if(!Double.isFinite(singleLineToPrint.getPredictedRating())||singleLineToPrint.getPredictedRating()==-99){
					pw.println(singleLineToPrint.getUserID() + "," +  singleLineToPrint.getItem() + "," +
							df.format(singleLineToPrint.getActualRating()) + "," + "NA" +"," + "NA");	
				}else{
					pw.println(singleLineToPrint.getUserID() + "," +  singleLineToPrint.getItem() + "," +
							df.format(singleLineToPrint.getActualRating()) + "," + df.format(singleLineToPrint.getPredictedRating()) +"," + 
							df.format(singleLineToPrint.getRmse()));	
				}
			}
			pw.close();	// CLosing the PrintWriter Object
		}catch(IOException e){
			System.out.println("Some error while generating the CSV file");
			return false;
		}
		return true;
	}
	/**
	 * End
	 */
}
/**
 * End of File
 */