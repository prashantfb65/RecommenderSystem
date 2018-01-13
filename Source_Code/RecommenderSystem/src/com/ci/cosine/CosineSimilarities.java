package com.ci.cosine;
/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             06-March-2017
Copyright:                University College Dublin
Subject:				  COLLECTIVE INTELLIGENCE (Recommender System)
Description:              The file is dedicated for finding Cosine similarity between the users and Predicting Rating for an Item
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ci.bean.CSVPojo;
import com.ci.bean.KeyValueDouble;
import com.ci.excel.CSVGenerator;
import com.ci.main.CompareSort;


public class CosineSimilarities {

	public static Map<String, List<KeyValueDouble>> userSimMap =  null; //User, User, Similarity
	public int nonCoverageCount = 0;			//Variable to store Non-Covered Item during Prediction Making Process
	public static double comparisonsMade = 0;	//Variable stores no. of comparison made while making prediction for all items							
	public static Map<String, List<KeyValueDouble>> userMapSim =  null;	//User, List-> Movies, Ratings
	DecimalFormat df = new DecimalFormat("0.000"); 	//Formatter for Double data - Precisions

	/**
	 * Method Name: [computeCosineSimilarity]
	 * Computes Cosine Similarity between two Users
	 * @param  delta   Item-Rating lists of Two users
	 * @return         similarity in double
	 * Task 3
	 */
	public double computeCosineSimilarity(List<KeyValueDouble> itemRatingList1, List<KeyValueDouble> itemRatingList2){

		/**
		 * Task 3
		 * cosine similarity ===> cos(A1.A2) = (A1.A2)/(||A1|| ||A2||)
		 * Numerator is dot product
		 * Denominator is the product of the vector sum
		 */

		//Variables Declaration
		double docProduct = 0;         	//Stores dotProduct of Vector 1 and Vector 2
		double vectorOneSquareSum = 0;	//Stores Square sum of Vector1
		double vectorTwoSquareSum = 0;	//Stores Square sum of Vector1
		double vector1 = 0.0;			//Vector 1 
		double vector2 = 0.0;			//Vector 2
		double vectorProduct = 0.0;		//Vector Product of Document Instance one and two
		double cosineSimilarity = 0.0;	//Cosine similarities

		int corratedDocs = 0; //Number of corrated documents for the two users

		/**
		 * The below code calculates dot product for two document and Product of the vector sum
		 * Here the two Item_rating list are iterated over each other to find the similarity
		 */
		for(KeyValueDouble itemRatingPair1: itemRatingList1){
			for(KeyValueDouble itemRatingPair2: itemRatingList2){
				if(itemRatingPair1.getKey().equals(itemRatingPair2.getKey())){ // Complexity Reduced by only including co-rated documents
					corratedDocs++;
					double numerator = itemRatingPair1.getValue()*itemRatingPair2.getValue();
					docProduct  = docProduct + numerator;		// Preparation for the Numerator
					vectorOneSquareSum = vectorOneSquareSum +  itemRatingPair1.getValue()* itemRatingPair1.getValue();
					vectorTwoSquareSum = vectorTwoSquareSum +  itemRatingPair2.getValue()* itemRatingPair2.getValue();
				}
			}
		}
		vector1 = Math.sqrt(vectorOneSquareSum);	// Preparation for the Denominator
		vector2 = Math.sqrt(vectorTwoSquareSum);	// Preparation for the Denominator
		vectorProduct = vector1*vector2;			// Denominator

		/**
		 * The cosine similarity is calculated here
		 */
		cosineSimilarity = (docProduct/vectorProduct)*100;  //Cosine Similarity Multiplied by 100 (Internal Use)
		if(!Double.isFinite(cosineSimilarity)){
			return 0;
		}else{
			/*
			 * Various measure are taken to enhance the similarity parameters
			 */
			if(corratedDocs==1 && cosineSimilarity>80){
				return (cosineSimilarity-10);
			}else if(corratedDocs<10 && cosineSimilarity>95){
				return (cosineSimilarity-2.5);
			}else if(corratedDocs>30 && cosineSimilarity>85){
				return (cosineSimilarity + Math.log(10*corratedDocs) );
			}else return cosineSimilarity;
		}
	}

	/**
	 * End of Method [computeCosineSimilarity]
	 */



	/**
	 * 
	 * @param movieMap
	 * @param userSimilarityMap
	 * @param userMap
	 * @param neighbour
	 * @param methodUsed To Iterate over all the Movies to for form Movie -> List ( User, Rating) pair 
	 * and predicting the Ratings
	 * RMSE , Coverage is Evaluate too - Task 3
	 * @return
	 */
	public boolean sim_item_rating(Map<String, List<KeyValueDouble>> movieMap, Map<String, List<KeyValueDouble>> userSimilarityMap, Map<String, List<KeyValueDouble>> userMap, int neighbour, String methodUsed){
		userSimMap = userSimilarityMap ;
		userMapSim = userMap ;
		double predictedRating;
		double sumRmseTask3 = 0;

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
				predictedRating = pairEvaluation(kv.getKey(),entry.getKey(),neighbour, methodUsed );
				double individualRMSE = 0;
				
				/*
				 * If the rating couldn't be predicted but it as -99
				 * Put Individual RMSE as -99 as it can be calculated too
				 */
				if(!Double.isFinite(predictedRating) || predictedRating==0){
					nonCoverageCount++;
					predictedRating = -99;
					individualRMSE = -99;
				}else{
					individualRMSE = Math.abs(predictedRating-kv.getValue());
					sumRmseTask3 = sumRmseTask3 + Math.abs(predictedRating-kv.getValue())*Math.abs(predictedRating-kv.getValue());
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
		boolean status = csvGenerate.generateCSV(csvPredictionTechnique, "Technique2_Prediction");
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		if(status){
			System.out.println("Recommendation Technique 2 : [Cosine Similarity] \t( L1O : Technique2_Prediction.csv Generated ....) \n");
		}else{
			System.out.println("Recommendation Technique 2 : [Cosine Similarity] \n");
		}

		System.out.print("COVERAGE  = " + df.format(((comparisonsMade-nonCoverageCount)/comparisonsMade)*100d) + " %" +
		"\t OVERALL  RMSE = " + df.format(Math.sqrt(sumRmseTask3/(comparisonsMade - nonCoverageCount))));
		/*
		 * Static Variable Initialized to zero
		 */
		comparisonsMade = 0;
		return true;
	}
	/**
	 * End of Method [sim_item_rating]
	 */
	
	
	
	/**
	 * 
	 * @param user	- User ID
	 * @param movie	- Item (Movie)
	 * @param range - Neighbor
	 * @param methodUsed Method pairEvaluation is for predicting the Rating excluding the user , movie tuple
	 * @return returns the predicted rating
	 */
	public static double pairEvaluation(String user, String movie, int range, String methodUsed){
		
		/*
		 * Variable Initialization for Majority Vote Technique -> Counting no. of 1's 2's..5's
		 */
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		double count4 = 0;
		double count5 = 0;


		List<KeyValueDouble> counts = new ArrayList<>();
		double prediction = -99; //Assigning Initial Prediction to be -99

		comparisonsMade++;
		int internalCount = 0;
		int internalCountExst = 0;
		double predictedRating = 0d;
		
		/*
		 * Extracting similar users to User Passed and 
		 * find the neighbors for that particular item 
		 * to predit's its rating
		 */
		if(userMapSim.containsKey(user)){

			List<KeyValueDouble> kvListSim = userSimMap.get(user);
			List<KeyValueDouble> kvList = userMapSim.get(user);
			for(KeyValueDouble kv: kvListSim){
				/*
				 * For Similarity more than 88% 
				 */
				if(internalCount<=range && kv.getValue()>88){
					internalCount++;
					for(KeyValueDouble kv2: kvList){
						if(kv.getKey().equalsIgnoreCase(kv2.getKey())){
							internalCountExst++;
							predictedRating+= kv2.getValue();

							switch ((int)kv2.getValue()) {
							case 1:  count1++;
							break;
							case 2:  count2++;
							break;
							case 3:  count3++;
							break;
							case 4:  count4++;
							break;
							case 5:  count5++;
							break;
							}
						}
					}
				}else{
					break;
				}
			}
			
			/*
			 * Storing the count of Numbers in the List<KeyValueDouble> Pair
			 */
			counts.add(new KeyValueDouble("1", count1));
			counts.add(new KeyValueDouble("2", count2));
			counts.add(new KeyValueDouble("3", count3));
			counts.add(new KeyValueDouble("4", count4));
			counts.add(new KeyValueDouble("5", count5));
		}

		/*
		 * Predicting on the basis of method asked by the user which can either be Average Rating 
		 * or Majority Voting
		 */
		if(methodUsed!=null && methodUsed.equalsIgnoreCase("Average Rating")){
			prediction =  predictedRating/internalCountExst;
		}else if(methodUsed!=null && methodUsed.equalsIgnoreCase("Majority Voting")){
			Collections.sort(counts, new CompareSort().reversed());
			if(counts.get(0).getValue()==0){
				prediction = 0;  
			}else{
				prediction =  Double.parseDouble(counts.get(0).getKey());
			}
		}
		return prediction;
	}
	/**
	 * End of Method [pairEvaluation]
	 */
	
}

/**
 * End of File
 */
