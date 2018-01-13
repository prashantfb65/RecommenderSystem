package com.ci.main;
/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             06-March-2017
Copyright:                University College Dublin
Subject:				  COLLECTIVE INTELLIGENCE (Recommender System)
Description:              Initialization File for the Recommender System - The Program Starts from here
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/


/*
 * Inclusion of Header Files
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.ci.bean.CSVPojo;
import com.ci.bean.KeyValueDouble;
import com.ci.cosine.CosineSimilarities;
import com.ci.excel.CSVGenerator;
import com.ci.resnick.Resnick;

public class Recommender {
	
	
	private List<String> allLines = new ArrayList<>();		//Variable to store all the lines from rating file in a list

	// Making the set of Users, Movies and Rating
	private static Set<String> userId = new HashSet<>();		//Storing Unique Users
	private static Set<String> movies = new HashSet<>();		//Storing Unique Items(Movies)
	private static Set<String> ratings = new HashSet<>();		//Storing Unique Ratings

	public Map<String, List<KeyValueDouble>> movieMap =  new LinkedHashMap<>();		//Data Structure for Storing Movie -> List of User & Ratings
	public Map<String, List<KeyValueDouble>> userMap = new LinkedHashMap<>();		//Data Structure for Storing User -> List of Movie & Ratings

	public static Map<String, List<KeyValueDouble>> userSimilarityMap =  new HashMap<>(); //User, User, Similarity
	public static Map<String, List<KeyValueDouble>> userCorrelationMap =  new HashMap<>(); //User, User, Correlation

	public List<KeyValueDouble> mean = new ArrayList<>();
	public double rmseTask2 = 0;


	private static int count1Star = 0;						// No. of entities in 1* class
	private static int count2Star = 0;						// No. of entities in 2* class
	private static int count3Star = 0;						// No. of entities in 3* class	
	private static int count4Star = 0;						// No. of entities in 4* class
	private static int count5Star = 0;						// No. of entities in 5* class
	private static double coveragePer = 0;			 		//Variable to store overall coverage for Average Prediction

	DecimalFormat df = new DecimalFormat("0.000"); 			//Formatter for the Double Value Precisions

	/**
	 * Main method
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Recommender rv = new Recommender();
		List<String> fetchedLines = null;

		//Call to ReadRatingCSV method to Read the Input File 
		fetchedLines = rv.readRatingCSVFile("100k.dat");
		if(fetchedLines!=null){
			/*
			 * Task 1b
			 * Data structures for storing and manipulating ratings data  
			 * Movie to rating mapping with users and movies
			 */
			rv.dataStructureMapping(fetchedLines);

			/*
			 * Task 1e
			 * Finding Mean, median, standard deviation, max, min ratings per User
			 */
			rv.findMeanMedianSDMinMax(rv.userMap, "User");


			/*
			 * Task 1f
			 * Finding Mean, median, standard deviation, max, min ratings per Movie(Item)
			 */
			rv.findMeanMedianSDMinMax(rv.movieMap, "Movie");
			System.out.println("----------------------------------------------------------------------------------------------------------------");

			/**
			 * Task 2
			 * There are three different recommendation techniques supposed to be coded.
			 * The below code implements the first one,  it also computes coverage & root mean squared error for L1O technique
			 * and coverage on omission of user item tuples
			 * 
			 * Task 
			 * also it retrieves number of 1*, 2*, .. 5* Ratings given by all users 
			 */

			double sumRmseTask2 = 0;
			int notCovered = 0;				// Contains the number of instances when the predictions couldn't be made
			List<KeyValueDouble> intrVal = null;

			List<CSVPojo> csvPredictionTechnique = new ArrayList<>(); 	//Variable to store data to be trasfered to CSV
			long startTime = System.currentTimeMillis(); 	// Start Timer for Evaluation RMSE and Coverage for Mean Prediction Technique 
			for (Map.Entry<String, List<KeyValueDouble>> entry : rv.movieMap.entrySet()){
				List<CSVPojo> csvPredictionTechnique2 = new ArrayList<>();
				/*
				 * Finding Total  number  of  ratings  for  each  of  the  5  ratings  classes
				 * Task 1g
				 */
				for(KeyValueDouble kv: entry.getValue()){
					CSVPojo singleEntry = new CSVPojo();
					switch ((int)kv.getValue()) {
					case 1:  count1Star++;
					break;
					case 2:  count2Star++;
					break;
					case 3:  count3Star++;
					break;
					case 4:  count4Star++;
					break;
					case 5:  count5Star++;
					break;
					default: System.out.println("Error while counting Rating");;
					break;
					}

					/*
					 * Baseline Prediction Technique - mean_item_rating(user_id, item_id) method calling to 
					 * 
					 */
					double compute = rv.mean_item_rating(kv.getKey(), entry.getKey() );
					double individualRMSE = 0;
					//If no rating is predicted the count of notCovered item(movie) is increased after leaving user_id, item_id tuple
					if(!Double.isFinite(compute)){
						notCovered++;
						individualRMSE = -99;
					}else{
						individualRMSE = Math.abs(compute-kv.getValue());
						sumRmseTask2 = sumRmseTask2 + Math.abs(compute-kv.getValue())*Math.abs(compute-kv.getValue());
					}

					/*
					 * Setting the attributes of the CSV
					 */
					singleEntry.setUserID(kv.getKey());
					singleEntry.setItem(entry.getKey());
					singleEntry.setActualRating(kv.getValue());
					singleEntry.setPredictedRating(compute);
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

			System.out.println("Ratings for each of the 5 ratings  |\t" + "1 * = " + count1Star +", 2 * = " + 
					count2Star +", 3 * = "+ count3Star +", 4 * = " +count4Star +", 5 * = " + count5Star );
			System.out.println("----------------------------------------------------------------------------------------------------------------");

			CSVGenerator csvGenerate = new CSVGenerator();
			boolean status = csvGenerate.generateCSV(csvPredictionTechnique, "Technique1_Prediction");

			if(status){
				System.out.println("Recommendation Technique 1 : [Average Rating] \t( L1O : Technique1_Prediction.csv Generated ....) \n");
			}else{
				System.out.println("Recommendation Technique 1 : Average Rating \n");
			}


			double size = rv.allLines.size() ;					//Total Test Dataset Size 
			coveragePer = ((size - notCovered)/size)*100; 		//Coverage evaluated as prediction made upon total test data

			long endTime = System.currentTimeMillis(); // Start Timer for Evaluation RMSE and Coverage for Mean Prediction Technique 
			System.out.println("COVERAGE  = " + coveragePer + " %" +  "\t OVERALL  RMSE = " + rv.df.format(Math.sqrt(sumRmseTask2/(size - notCovered)))
			+ "\t TIME ELAPSED = " + (endTime-startTime)/1000 + " seconds" );
			System.out.println("----------------------------------------------------------------------------------------------------------------");



			/**
			 * Task 3
			 * There are three different recommendation techniques supposed to be coded.
			 * The below code implements the Second one, i.e cosine similarity and computes coverage & root mean squared error for L1O technique
			 * and coverage on omission of user item tuples
			 * 
			 * The Predictions are made on two basis - Majority Voting , Average Rating of k-Nearest Neighbors
			 */
			String message = "Do you want to use Cosine Similarity for making Predictions [Press 'Y' or 'N']= ";
			String proceedStatus = "";
			boolean flag = false;


			console: while(!flag){
				if(!proceedStatus.equalsIgnoreCase("Y") || !proceedStatus.equalsIgnoreCase("N")){
					proceedStatus = rv.readFromConsole(message);
				}
				if(proceedStatus!=null && proceedStatus.equalsIgnoreCase("Y")){
					flag = true;
					String message1 = "Enter the no. of closest neighbors you want use for making Prediction [1-250] = ";
					int range= 0;
					String value = "";
					boolean flag1 = false;
					while(!flag1){
						if(range <=0 || range >250){
							value = rv.readFromConsole(message1);
							try{
								range = Integer.parseInt(value);
							}catch (Exception e) {
								System.out.println("Check the value Type entered...");
							}
						}
						if(range>0 && range<=250){
							flag1 = true;
						}
					}

					String message2 = "Press '1' for Average Rating, '2' for Majority Voting : ";
					String methodUsed = "";
					boolean flag2 = false;
					while(!flag2){
						if(!methodUsed.equalsIgnoreCase("1") || !methodUsed.equalsIgnoreCase("2")){
							methodUsed = rv.readFromConsole(message2);
						}
						if(methodUsed!=null && methodUsed.equalsIgnoreCase("1")){
							methodUsed = "Average Rating";
							flag2 = true;
						}else if(methodUsed!=null && methodUsed.equalsIgnoreCase("2")){
							methodUsed = "Majority Voting";
							flag2 = true;
						}else{
							methodUsed = rv.readFromConsole(message2);
						}
					}
					System.out.println("----------------------------------------------------------------------------------------------------------------");
					System.out.println("\nRecommendation Technique 2 : [Cosine Similarity]" + "\t Method = " + methodUsed + ", k = " + range);


					//----------------------------------------Cosine
					System.out.print("\nPLEASE  WAIT.... CALCULATING  SIMILARITIES ");
					long startTimeCosine = System.currentTimeMillis(); 	// Start Timer for Evaluation RMSE and Coverage for Cosine Sim Prediction Technique
					/*
					 * Creating an object of CosineSimilarities file to calculate the similarities between 
					 * different users based on the item(movie) rating given by them
					 */
					CosineSimilarities cs = new CosineSimilarities();
					List<KeyValueDouble> similarityList = null; 	//Variable for storing the similarity between the users

					/*
					 * Iterating over the loop of the users over users to find the similarity between them
					 */
					for (Map.Entry<String, List<KeyValueDouble>> user1 : rv.userMap.entrySet()){
						similarityList = new ArrayList<>();			
						List<KeyValueDouble> user1movieRatings = user1.getValue();  	//Variable storing the movies-rating list for User1
						double similarity = 0;
						for (Map.Entry<String, List<KeyValueDouble>> user2 : rv.userMap.entrySet()){
							List<KeyValueDouble> user2movieRatings = user2.getValue();	//Variable storing the movies-rating list for User1
							if(!user1.getKey().equalsIgnoreCase(user2.getKey())){

								/*
								 * The method computeCosineSimilarity is invoked, which finds the similarity between two users
								 */
								similarity = cs.computeCosineSimilarity(user1movieRatings, user2movieRatings);

								/*
								 * This instance of similarity between two users is added into a list
								 */
								similarityList.add(new KeyValueDouble(user2.getKey(),similarity ));
							}
						}
						/*
						 * Sorting the list of similarity in the decreased order of similarity
						 */
						Collections.sort(similarityList, new CompareSort().reversed());

						/*
						 * Adding the similarity list User1 with all users in userSimilarityMap
						 */
						userSimilarityMap.put(user1.getKey(),similarityList);
					}
					long endTimeCosine = System.currentTimeMillis();		
					System.out.println(" \tTIME ELAPSED = " + (endTimeCosine - startTimeCosine)/1000 + " seconds");
					System.out.println("\nPLEASE  WAIT.... PREDICTING RATINGS NOW \n ");

					/*
					 * sim_item_rating method is called to predict the rating for the movies with L1O strategy
					 * It will calculate RMSE and Coverage also
					 */
					boolean completed = cs.sim_item_rating(rv.movieMap,userSimilarityMap, rv.userMap,range, methodUsed);
					long endTimePredictions = System.currentTimeMillis(); 		// End Timer for Evaluation RMSE and Coverage for Cosine Sim Prediction Technique
					System.out.println("\t TIME ELAPSED = " + (endTimePredictions-endTimeCosine)/1000 + " seconds"); 

					System.out.println("\nT A S K  C O M P  L E T E D");
					System.out.println("----------------------------------------------------------------------------------------------------------------\n");

					if(completed){
						flag = true;
					}

					//----------------------------------------------------------------------
				}else if(proceedStatus!=null && proceedStatus.equalsIgnoreCase("N")){
					flag = true;
					String message3 = "Do you want to use Resnick Formula for making Predictions [Press 'Y' or 'N']= ";
					String proceedStatusResnik = "";
					boolean flag3 = false;

					while(!flag3){
						if(!proceedStatusResnik.equalsIgnoreCase("Y") || !proceedStatusResnik.equalsIgnoreCase("N")){
							proceedStatusResnik = rv.readFromConsole(message3);
						}
						if(proceedStatusResnik!=null && proceedStatusResnik.equalsIgnoreCase("Y")){
							flag3 = true;
							String message4 = "Enter the no. of closest neighbors you want use for making Prediction [1-250] = ";
							int range= 0;
							String value = "";
							boolean flag5 = false;
							while(!flag5){
								if(range <=0 || range >250){
									value = rv.readFromConsole(message4);
									try{
										range = Integer.parseInt(value);
									}catch (Exception e) {
										System.out.println("Check the value Type entered...");
									}
								}
								if(range>0 && range<=250){
									flag5 = true;
								}
							}
							
							System.out.println("----------------------------------------------------------------------------------------------------------------");
							System.out.println("\nRecommendation Technique 3 : [Resnik Formula]" + "\t Method = Pearson Correlation " + " , k = " + range);
							System.out.print("\nPLEASE  WAIT.... CALCULATING  CORRELATION ");
							long startTimeResnick = System.currentTimeMillis(); 	// Start Timer for Evaluation RMSE and Coverage for Resnick Prediction Technique
							/*
							 * Creating an object of Resnick file to calculate the correlation between 
							 * different users based on the item(movie) rating by Pearson's Correlation Formulae
							 */
							Resnick rn = new Resnick();
							List<KeyValueDouble> correlationList = null; 	//Variable for storing the correlation between the users

							/*
							 * Iterating over the loop of the users over users to find the correlation between them
							 */
							for (Map.Entry<String, List<KeyValueDouble>> user1 : rv.userMap.entrySet()){
								correlationList = new ArrayList<>();			
								List<KeyValueDouble> user1movieRatings = user1.getValue();  	//Variable storing the movies-rating list for User1
								double correlation = 0;
								
								double meanRatingUser1 = 0; 	//Initializing mean value of User 1 to Zero
								double meanRatingUser2 = 0; 	//Initializing mean value of User 2 to Zero
								
								for (Map.Entry<String, List<KeyValueDouble>> user2 : rv.userMap.entrySet()){
									List<KeyValueDouble> user2movieRatings = user2.getValue();	//Variable storing the movies-rating list for User1
									if(!user1.getKey().equalsIgnoreCase(user2.getKey())){
										
										/*
										 * Finding the Mean User Rating for User 1 and User 2
										 */
										for(KeyValueDouble kv: rv.mean){
											if(kv.getKey().equalsIgnoreCase(user1.getKey())){
												meanRatingUser1 = kv.getValue();
											}
											if(kv.getKey().equalsIgnoreCase(user2.getKey())){
												meanRatingUser2 = kv.getValue();
											}
										}
										/*
										 * The method pearsonCorrelation is invoked, which finds the correlation between two users
										 */
										correlation = rn.pearsonCorrelation(user1movieRatings, user2movieRatings, meanRatingUser1,meanRatingUser2);
										
										/*
										 * This instance of correlation between two users is added into a correlation list
										 */
										correlationList.add(new KeyValueDouble(user2.getKey(),correlation));
									}
								}
								/*
								 * Sorting the list of correlation in the decreased order of correlation
								 */
								Collections.sort(correlationList, new CompareSort().reversed());

								/*
								 * Adding the similarity list User1 with all users in userCorrelationMap
								 */
								userCorrelationMap.put(user1.getKey(),correlationList);
							}
							long endTimeResnick = System.currentTimeMillis();		// Time at when all the correlation were found
							System.out.println(" \tTIME ELAPSED = " + (endTimeResnick - startTimeResnick)/1000 + " seconds");
							System.out.println("\nPLEASE  WAIT.... PREDICTING RATINGS NOW \n ");

							/*
							 * sim_item_rating method is called to predict the rating for the movies with L1O strategy
							 * It will calculate RMSE and Coverage also
							 */
							boolean completed = rn.cor_item_rating(rv.movieMap, userCorrelationMap , rv.userMap,range, rv.mean);
							
							long endTimePredictions = System.currentTimeMillis(); 		// End Timer for Evaluation RMSE and Coverage for Resnik Prediction Technique
							System.out.println("\t TIME ELAPSED = " + (endTimePredictions-endTimeResnick)/1000 + " seconds"); 

							System.out.println("\nT A S K  C O M P  L E T E D");
							System.out.println("----------------------------------------------------------------------------------------------------------------\n");

							if(completed){
								flag3 = true;
							}
						}
						if(proceedStatusResnik!=null && proceedStatusResnik.equalsIgnoreCase("N")){
							flag3 = true;
						}
					}
				}else{
					proceedStatus = rv.readFromConsole(message);
				}
			}



		}else{
			System.out.println("Some Problem while Reading the File");
		}
	}

	/**
	 * Method Name: [readRatingCSVFile] - Task 1a
	 * Reads the 100k.dat CSV file and returns the list of lines present in the file
	 * @param fileName of the File to be read
	 * @return list of lines present in the file
	 */
	public List<String> readRatingCSVFile(String fileName) {
		BufferedReader br; 				//BufferedReader variable 
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = null;			//Represents the Line Being Read from Buffered Reader

			/** 
			 * Reading the Complete File and Storing Each Line in a List of String
			 */
			try {
				while ((line = br.readLine()) != null) {
					allLines.add(line);
				}
			} catch (IOException e) {
				System.out.println("Some error while reading the input file");
			}
		} catch (FileNotFoundException e) {
			System.out.println("The CSV File doesn't Exist :: " + e);
			return null;
		}
		return allLines;
	}
	/**
	 * End of Method [readRatingCSVFile]
	 */


	/**
	 * Method Name: [dataStructureMapping] - Task 1b
	 * Data Structure Creation for storing and manipulating the rating data 
	 * @param List of all Lines present in the Rating file
	 * @return 
	 */
	public void dataStructureMapping(List<String> completeFile){

		String userIdLocal = null;  //Variable to reference each line's user
		String movieLocal = null;	//Variable to reference each line's movie
		double ratingLocal;			//Variable to reference each line's rating
		double ratingDensity = 0; 	//Variable to store Rating Density
		/*
		 * Iterating through all the lines present in the Rating file (100k.dat)
		 */
		for(String eachLine: completeFile){
			String[] splitLine = eachLine.split(",");
			/*
			 * Adding the Users, Movies and Ratings to the Set to retrieve unique instances of each
			 * This will help us to retrieve Total number of users, items, ratings 
			 * Task 1c
			 */
			userId.add(splitLine[0]);
			movies.add(splitLine[1]);
			ratings.add(splitLine[2]);

			userIdLocal = splitLine[0];
			movieLocal = splitLine[1];
			ratingLocal = Double.parseDouble(splitLine[2]);

			/*
			 * Here, as the data-structure used involves two HashMaps
			 * One stores userID as key with value as a list of key-value pair -string (movie) , double (rating) and another
			 * stores , movie as as key with value as a list of key-value pair -string (userID) , double (rating) 
			 */
			mapWithUserAsKey(userIdLocal,movieLocal,ratingLocal);  				//Method called to form HashMap of Key- User, Value- List of Movie with Rating
			mapWithMoviesAsKey(movieLocal,userIdLocal,ratingLocal); 			//Method called to form HashMap of Key- Movie, Value- List of User with Rating

			/*
			 * Calculating Rating Density which is Total Ratings available divided by Total Possible Ratings
			 * Task 1d
			 */
			double denom = userId.size()*movies.size();
			ratingDensity = completeFile.size()/(denom);	
		}
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		System.out.println("U S E R S = " + userId.size() + " \tM O V I E S = " + movies.size() + " \tR A T I N G S = " + ratings.size() +"\t| \tRATING DENSITY = " + df.format(ratingDensity*100) + "%");
		System.out.println("----------------------------------------------------------------------------------------------------------------");
	}
	/**
	 * End of Method [dataStructureMapping]
	 */



	/**
	 * Method Name: [mapWithUserAsKey] - Task 1b
	 * Takes in single instance of movie, user and rating and puts it into the map 
	 * @param Single Instance of User, Movie and Rating
	 * @return 
	 */
	public void mapWithUserAsKey(String user, String mov, double rating){
		List<KeyValueDouble> kvPair;
		if(userMap.containsKey(user)){
			kvPair = (List<KeyValueDouble>) userMap.get(user);
		}else{
			kvPair = new ArrayList<KeyValueDouble>();
		}
		kvPair.add(new KeyValueDouble(mov, rating));
		userMap.put(user, kvPair);
	}
	/**
	 * End of Method [mapWithUserAsKey]
	 */


	/**
	 * Method Name: [mapWithMoviesAsKey] - Task 1b
	 * Takes in single instance of movie, user and rating and puts it into the map 
	 * @param Single Instance of User, Movie and Rating
	 * @return 
	 */
	public void mapWithMoviesAsKey(String mov, String user, double rating){
		List<KeyValueDouble> kvPair;
		if(movieMap.containsKey(mov)){
			kvPair = (List<KeyValueDouble>) movieMap.get(mov);
		}else{
			kvPair = new ArrayList<KeyValueDouble>();
		}
		kvPair.add(new KeyValueDouble(user, rating));
		movieMap.put(mov, kvPair);
	}
	/**
	 * End of Method [mapWithMoviesAsKey]
	 */


	/**
	 * Method Name: [findMeanMedianSDMinMax] - Task 1e and 1f
	 * The method finds mean , median , standard deviation, minimum rating and maximum rating for every user and movie(item)
	 * @param User Map/Movie(Item) Map, String type - User/Movie(Item)
	 * @return 
	 */
	public void findMeanMedianSDMinMax(Map<String, List<KeyValueDouble>> map, String type){

		/*
		 * Intermediate variables used while computing Mean , Median, Standard Deviation, 
		 * Minimum Rating and Maximum Rating
		 */
		double sum = 0;
		double sumSD = 0;
		double meanStats = 0;
		double standardDevStats  = 0;
		int medianPostion = 0;
		double medianStats = 0;
		double min = 0;
		double max = 0;

		/*
		 * The below code will compute the statistics and print them to a CSV
		 * Stats_User.csv for Mean, median, standard deviation, max, min ratings per user
		 * Stats_Movie.csv for Mean, median, standard deviation, max, min ratings per item
		 */
		try{
			PrintWriter writeCSV = new PrintWriter("Stats_" +type + ".csv");

			//Header for the CSV file
			writeCSV.println(type +"," + "Mean" +"," + "Median" +"," + "Standard Deviation"  +"," + "Minimum" +"," + "Maximum");

			//Iterating through the User/Item(Movie) map
			for (Map.Entry<String, List<KeyValueDouble>> entry : map.entrySet()){
				int count = entry.getValue().size();
				for(KeyValueDouble kv : entry.getValue()){
					sum = sum + kv.getValue();  		// Adding the sum of ratings for evaluating mean and standard deviation

					/*
					 *  Evaluation for Minimum and Maximum Rating 
					 */
					if(min==0){
						min = kv.getValue();
					}if(max==0){
						max = kv.getValue();
					}if(kv.getValue()<min){
						min = kv.getValue();
					}if(kv.getValue()>max){
						max = kv.getValue();
					}

				}

				meanStats = sum/count;			//Mean Calculated as Sum of all Ratings upon total number of users


				//Adding the mean of rating for every User to be used in Resnik Prediction
				if(type.equalsIgnoreCase("User")){
					mean.add(new KeyValueDouble(entry.getKey(), meanStats));
				}

				/*
				 * Calculating Standard Deviation
				 */
				for(KeyValueDouble kv : entry.getValue()){
					sumSD = sumSD + Math.pow(kv.getValue()-meanStats, 2);
				}
				standardDevStats = Math.sqrt(sumSD/count);

				/*
				 * Calculating Median
				 */
				List<KeyValueDouble> medianVar = entry.getValue(); 
				Collections.sort(medianVar, new CompareSort()); //Sorting the Ratings before evaluating the Median
				if(count%2==0){
					medianPostion = (count-1)/2;
					medianStats = (medianVar.get(medianPostion).getValue() + medianVar.get(medianPostion+1).getValue())/2;
				}else if(count%2!=0){
					medianPostion = (count-1)/2;
					medianStats = medianVar.get(medianPostion).getValue();
				}

				/*
				 * Iterating the Stats to the CSV
				 */
				writeCSV.println(entry.getKey() + ","+ df.format(meanStats) +"," + df.format(medianStats)  
				+ "," + df.format(standardDevStats) + "," + min + " , " + max);

				/*
				 * Resetting the stats for next User/Item stats evaluation
				 */
				sum = 0;
				meanStats = 0;
				sumSD = 0;
				standardDevStats = 0;
				min = 0;
				max = 0;
			}
			writeCSV.close();
			System.out.println( "Stats_"+type+".csv" + " Generated .... " + "Mean, median, standard deviation, max, min ratings per " + type);
		}catch(IOException e){
			System.out.println("Please close the already opened file : " + e);
		}
	}
	/**
	 * End of Method [findMeanMedianSDMinMax]
	 */



	/**
	 * Method Name: [mean_item_rating] - Task 1e and 1f
	 * The method finds mean , median , standard deviation, minimum rating and maximum rating for every user and movie(item)
	 * @param Tuple of user and movie which is to be removed while making the prediction for the item
	 * @return the average rating for this item(movie) calculated across all ratings for the item, except the tuple passed in the method 
	 */
	public double mean_item_rating(String user, String mov){
		List<KeyValueDouble> kvPair;
		double avgRating = 0;
		double rating = 0;
		if(movieMap.containsKey(mov)){
			kvPair = (List<KeyValueDouble>) movieMap.get(mov);
			for(KeyValueDouble kv : kvPair){
				if(!kv.getKey().equalsIgnoreCase(user) ){
					rating+=kv.getValue();
				}
			}
			avgRating = rating/(kvPair.size()-1);
		}                                        
		return  avgRating;
	}
	/**
	 * End of Method [mean_item_rating]
	 */


	/**
	 * Method Name: [readFromConsole] - Task 3 and 4
	 * The method reads user preference from the console 
	 * @param Tuple of user and movie which is to be removed while making the prediction for the item
	 * @return the average rating for this item(movie) calculated across all ratings for the item, except the tuple passed in the method 
	 */
	public String readFromConsole(String message){
		System.out.print(message);
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		String executionStatus = " ";
		try{
			executionStatus = reader.next(); // Scans the next token of the input as an String
			return executionStatus;
		}catch(Exception e){
			System.out.println("Incorrect Option ...");
		}
		return executionStatus;
	}
	/**
	 * End of Method [readFromConsole]
	 */
}
