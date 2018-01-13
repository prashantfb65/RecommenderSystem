package com.ci.resnick;
/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             06-March-2017
Copyright:                University College Dublin
Subject:				  COLLECTIVE INTELLIGENCE (Recommender System)
Description:              The file is dedicated for finding Pearson's Correlation between the users and 
							Predicting Rating for an Item using Resnick formula
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ci.bean.CSVPojo;
import com.ci.bean.KeyValueDouble;
import com.ci.excel.CSVGenerator;

public class Resnick {

	/**
	 * Pearson’s Correlation Coefficient measures the extent to which two variables are
		linear related; -1 perfect negative correlation, 0 no correlation, 1 perfect
		correlation.
	 */
	
	public static Map<String, List<KeyValueDouble>> userCorrelatedMap =  null; //User, User, Correlation
	public int nonCoverageCount = 0;			//Variable to store Non-Covered Item during Prediction Making Process
	public static double comparisonsMade = 0;	//Variable stores no. of comparison made while making prediction for all items							
	public static Map<String, List<KeyValueDouble>> userMapCor =  null;	//User, List-> Movies, Ratings
	DecimalFormat df = new DecimalFormat("0.000"); 	//Formatter for Double data - Precisions

	
	/**
	 * Method Name: [pearsonCorrelation]
	 * Computes Correlation between two Users
	 * @param  delta   Item-Rating lists of Two users
	 * @return         similarity in double
	 * Task 3
	 */
	public double pearsonCorrelation(List<KeyValueDouble> user1,List<KeyValueDouble> user2, double mean1, double mean2){
		
		double num = 0; 				//Total Numerator
		double vectorOneSquareSum = 0;	//Stores Square sum of Vector1
		double vectorTwoSquareSum = 0;	//Stores Square sum of Vector2
		double vector1 = 0.0;			//Vector 1 
		double vector2 = 0.0;			//Vector 2
		double correlation = 0.0;		//Initialization of Initial Correlation to Zero
		
		/*
		 * Iterating the Item-Ratings of 1 user to other to find Correlation
		 */
		for(KeyValueDouble kv1: user1){
			for(KeyValueDouble kv2: user2){
				if(kv1.getKey().equals(kv2.getKey())){ 
					double numerator = (kv1.getValue()-mean1) * (kv2.getValue()-mean2);
					num  = num + numerator;			// Preparation for the Numerator
					vectorOneSquareSum = vectorOneSquareSum + Math.pow((kv1.getValue() - mean1), 2);
					vectorTwoSquareSum = vectorTwoSquareSum + Math.pow((kv2.getValue() - mean2), 2);
				}
			}
		}
		vector1 = Math.sqrt(vectorOneSquareSum);		// Preparation for the Denominator
		vector2 = Math.sqrt(vectorTwoSquareSum);		// Preparation for the Denominator
		correlation = num/(vector1*vector2);			//Correlation being evaluated 
		
		/*
		 * Here 0 represents that no correlation exists
		 */
		if(!Double.isFinite(correlation)){
			return 0;
		}else{
			return correlation;
		}
	}
	
	/**
	 * End of Method [pearsonCorrelation]
	 */

	
	/**
	 * 
	 * @param movieMap
	 * @param userSimilarityMap
	 * @param userMap
	 * @param neighbour
	 * @param methodUsed To Iterate over all the Movies to for form Movie -> List ( User, Rating) pair 
	 * and predicting the Ratings
	 * RMSE , Coverage is Evaluate too - Task 4
	 * @return
	 */
	public boolean cor_item_rating(Map<String, List<KeyValueDouble>> movieMap, Map<String, List<KeyValueDouble>> userSimilarityMap, Map<String, List<KeyValueDouble>> userMap, int neighbour, List<KeyValueDouble> meanList){
		
		userCorrelatedMap = userSimilarityMap;	//Using userSimilarity from Recommender File and reassigning it to class variable
		userMapCor = userMap ;			//Using userMapCor from Recommender File and reassigning it to class variable
		double predictedRating;			
		double sumRmseTask4 = 0;

		List<CSVPojo> csvPredictionTechnique = new ArrayList<>(); 	//Variable to store data to be trasfered to CSV
		
		/*
		 * Iterating over the movies and constructing Movie , User Tuple
		 */
		for (Map.Entry<String, List<KeyValueDouble>> entry : movieMap.entrySet()){
			List<CSVPojo> csvPredictionTechnique2 = new ArrayList<>(); 
			for(KeyValueDouble kv: entry.getValue()){
				CSVPojo singleEntry = new CSVPojo();	//Each Line for the CSV creating
				/*
				 * Invoking the pairEvalluation method to find prediction for User , Item Pair : Not using its own rating
				 */
				predictedRating = pairEvaluation(kv.getKey(),entry.getKey(), meanList, neighbour);
				double individualRMSE = 0;
				
				/*
				 * If the rating couldn't be predicted but it as -99
				 * Put Individual RMSE as -99 as it can be calculated too
				 */
				if(!Double.isFinite(predictedRating)){
					nonCoverageCount++;
					predictedRating = -99;
					individualRMSE = -99;
				}else{
					individualRMSE = Math.abs(predictedRating-kv.getValue());
					sumRmseTask4 = sumRmseTask4 + Math.abs(predictedRating-kv.getValue())*Math.abs(predictedRating-kv.getValue());
				}
				/*
				 * Setting the attributes of the CSV
				 */
				singleEntry.setUserID(kv.getKey());
				singleEntry.setItem(entry.getKey());
				singleEntry.setActualRating(kv.getValue());
				singleEntry.setPredictedRating(predictedRating);
				singleEntry.setRmse(individualRMSE);
				/*
				 * Adding the entry to the complete CSV Intermediate
				 */
				csvPredictionTechnique2.add(singleEntry);

			}
			/*
			 * Append the CSV intermediate to the complete CSV
			 */
			csvPredictionTechnique.addAll(csvPredictionTechnique2);
		}
		
		CSVGenerator csvGenerate = new CSVGenerator();
		boolean status = csvGenerate.generateCSV(csvPredictionTechnique, "Technique3_Prediction");
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		if(status){
			System.out.println("Recommendation Technique 3 : [Resnick Formula] \t( L1O : Technique3_Prediction.csv Generated ....) \n");
		}else{
			System.out.println("Recommendation Technique 2 : [Resnick Formula] \n");
		}

		System.out.print("COVERAGE  = " + df.format(((comparisonsMade-nonCoverageCount)/comparisonsMade)*100d) + " %" +
		"\t OVERALL  RMSE = " + df.format(Math.sqrt(sumRmseTask4/(comparisonsMade - nonCoverageCount))));
		
		/*
		 * Static Variable Initialized to zero
		 */
		comparisonsMade = 0;
		return true;
	}
	/**
	 * End of Method [cor_item_rating]
	 */
	
	
	/**
	 * 
	 * @param user - User ID
	 * @param movie - Item (Movie)
	 * @param meanList - Neighbor
	 * @param neighbor 
	 * @param methodUsed Method pairEvaluation is for predicting the Rating excluding the user , movie tuple
	 * @return
	 */
	public static double pairEvaluation(String user, String movie , List<KeyValueDouble> meanList , int neighbor){
		
		comparisonsMade++;
		int internalCount = 0;
		double meanUser1 = 0d;	//Mean Rating Given by User 1
		double meanUser2 = 0d;	//Mean Rating Given by User 2
		double denom = 0d;
		double num = 0d;

		/*
		 * Extracting highly correlated users to User Passed and 
		 * find the neighbors for that particular item 
		 * to predit's its rating
		 */
		if(userCorrelatedMap.containsKey(user)){
			List<KeyValueDouble> kvListSim = userCorrelatedMap.get(user);
			for(KeyValueDouble kv: kvListSim){
				internalCount++;
				/*
				 * For Correlation more than 0
				 */
				if(internalCount<=neighbor && kv.getValue()>0){
					List<KeyValueDouble> correraltedUser = userMapCor.get(kv.getKey());
					for(KeyValueDouble userInst : correraltedUser){
						if(movie.equalsIgnoreCase(userInst.getKey())){
							
							/*
							 * Finding the mean for the users
							 */
							for(KeyValueDouble meanInd: meanList){
								if(user.equalsIgnoreCase(meanInd.getKey())){
									meanUser1 = meanInd.getValue();
								}
								if(kv.getKey().equalsIgnoreCase(meanInd.getKey())){
									meanUser2 = meanInd.getValue();
								}
							}
							denom = denom + kv.getValue();
							num = num + (userInst.getValue() - meanUser2)*kv.getValue();
						}
					}
				}else{
					break;
				}
			}
		}
		return meanUser1+(num/denom);
	}
	/**
	 * End of Method [pairEvaluation]
	 */
	
}
/**
 * End of File
 */
