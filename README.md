__________________________________________________________________________________________

CF Recommendor System Document Classifier Version 1.0 07/03/2017
__________________________________________________________________________________________

General Usage Notes
------------------------------------------------------------------------------------------
- This documents will help you to run Recommender System on Movie Lens Dataset 100k.dat
- The Calloborative Filtering Technique is used to Design this Recommender System
- User Based Calloborative Filtering Techinques : Cosine Similarity, Pearson Correlation
- The Predictions made are based on  neighbourhood size & minimum neighbor overlap


System Requiements:
-------------------------------------------------------------------------------------------
- Java JRE 8 installed
- Java JRE 8 system Requirement for different operating systems can be found 
  from the below link
  https://www.java.com/en/download/help/sysreq.xml
  

Running Application and Importing Source Code on Windows/Linux/OS X Machine
-------------------------------------------------------------------------------------------
1. Unzip the 16201447.zip file. (The Extracted Folder 16201447 will contains 
	3 sub-folders and a README file)

2. The Report folder consists of the Project Report

2. The Source_Code folder consists of Java Code Implementation and can be imported as 
	"Existing Projects into Workspace" in Eclipse or any other IDE
	
	The Source Code Structure is as Follows:
	
	a. Package [com.ci.main] 
		Recommender.java 
			Main File . The execution starts from this file , it alse comprises of [ Task1 , Task 2]
		CompareSort.java
			Implements Comparator Interface to sort Multiple String keys, Double value pairs
			
	b. Package [com.ci.bean]
		CSVPojo.java	
			Pojo File handling CSV Print Structure
		KeyValueDouble.java
			 Pojo File handling String Key and Double Value Pair
			
		
	c. Package [com.ci.excel]
		CSVGenerator.java
			Generic File to Generate CSV [Task 2,  Task 3, Task 4]
			
	d. Package [com.ci.cosine]
		CosineSimilarities.java
			The file is dedicated for finding Cosine similarity between the users and Predicting Rating for an Item [Task 3]
			
	e. Package [com.ci.resnick]
		Resnick.java
			The file is dedicated for finding Pearson's Correlation between the users and Predicting Rating for an Item using Resnick formula	[ Task 4]

3. The Executable folder consists of RecommenderSytem_16201447.jar and 100k.dat file. The CSV's would be generated in this folder.

4. RecommenderSytem_16201447.jar is an executable file and can be run as:

 	For windows Users:
	i. Open the Command Prompt and Navigate to the extracted folder
		eg. cd C:\Users\UserName\Downloads\kNN_16201447\
	ii. Type
		java -jar RecommenderSystem_16201447.jar
	iii. The Program executes and several User Input Prompts will appear to the User
	
	For Linux Users:
	i. Open the Terminal and Navigate to the extracted folder
		eg. cd C:\Users\UserName\Downloads\kNN_16201447\
 	ii. Type
		java -jar kNNClassifier_16201447.jar
	iii. The Program executes and several User Input Prompts will appear to the User
			
	For Mac Users:
	i. Open the OS X command line and Navigate to the extracted folder
	ii. Type
		java -jar kNNClassifier_16201447.jar
	iii. The Program executes and several User Input Prompts will appear to the User

5. The CSV's are generated in Executable Folder
		
CF Recommendor System can be reached at:
-------------------------------------------------------------------------------------------
Voice : 353-89211-3858

Web Site: http://www.ucd.ie/

E-mail: prashant.garg@ucdconnect.ie 

Linkedin: https://www.linkedin.com/in/prashantucd

-------------------------------------------------------------------------------------------