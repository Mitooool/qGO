# qGO Algorithm

The qGO algorithm is designed for the precise quantification of mitochondrial genomes. The usage instructions are as follows:

1. #### Download the Source Code

   Download the source code to your local machine.

2. #### Java Environment

   This algorithm is written in Java, so ensure that you have the Java runtime environment installed correctly before running it.

3. #### Import the Source Code

   In your installed Java development environment (IDEA is recommended), import the downloaded source code.

4. #### Main Method Location

   The main method is located in the Main.java file under the src folder. Before running the main method, note that you need to specify the path to the FASTA format file containing the gene sequences on line 280 of the code.

5. #### Defining Gene Regions

   Line 306 of the code contains the manual region definitions. The example code provided in the source is as follows:

   ```java
   geneRegions.add(new GeneRegion(1, 0, 4));
   geneRegions.add(new GeneRegion(2, 5, 28));
   geneRegions.add(new GeneRegion(3, 29, 44));
   geneRegions.add(new GeneRegion(4, 45, 46));
   ```



This means that there are a total of 4 regions (4 gene regions). The three parameters in GeneRegion represent: the region number、the starting index of the region, and the ending index of the region. 

Users can adjust these parameters as needed, adding or removing gene regions and modifying their indices.
