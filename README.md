Add details for this project

## How to structure the spreadsheet

Ensure that the following columns are present in this order:
Date, Name, Gross, Fee,	Net

The remaining columns are of minimal value. 
For checks, simply paste the data onto the end 
of the normal PayPal excel file, matching up the 
data from the check spreadsheet so that it matches 
the appropriate column. also put 0 and the gross 
value for the Fee and Net column.

## Running the OMP Report Generator

Run as a jar:

Build with mvn clean install then run
	
java -jar omp.reports-0.0.1-SNAPSHOT.jar /path/to/omp.properties

Run as a Java Application in Eclipse:
-Set up a Java Application, then provide the 
path to omp.properties as a command line arg, then
run

YOU'RE DONE!