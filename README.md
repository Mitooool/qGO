# qGO Algorithm

The qGO algorithm is designed for the precise quantification of mitochondrial genomes. The usage instructions are as follows:

## Steps for Usage:

1. #### Download the Source Code

   Download the source code to your local machine.

2. #### Import the Source Code

   Import the downloaded source code into your Java Integrated Development Environment (IDE). It is recommended to use IntelliJ IDEA for optimal experience.

3. #### Main Method Location

   The main method can be found in the Main.java file located in the src folder. Before executing this method, you need to perform some configurations.

4. #### Specify the FASTA File

   In Main.java, navigate to line 280 and specify the path to your FASTA file that contains the gene sequences. Ensure the file is correctly formatted.

   Below is an example of the format:

   >\>1

   >nad1,I,-,M,-,nad2,W,-,-N,-,-,cox1,-,-,cox2,K,atp8,atp6,cox3,G,nad3,R,nad4L,nad4,H,S1,L1,nad5

   >\>2

   >nad1,I,-E,M,-,nad2,W,-A,-N,-C,-Y,cox1,-S2,D,cox2,K,atp8,atp6,cox3,G,nad3,R,nad4L,nad4,H,S1,L1,nad5

   >\>3

   >nad1,I,-,M,-E,nad2,W,-,-N,-,-,cox1,-,-,cox2,K,atp8,atp6,cox3,G,nad3,R,nad4L,nad4,H,S1,L1,nad5

5. #### Defining Gene Regions

   On line 306, you will find the code for manually defining gene regions. The provided example is as follows:

   ```java
   geneRegions.add(new GeneRegion(1, 0, 4));
   geneRegions.add(new GeneRegion(2, 5, 28));
   geneRegions.add(new GeneRegion(3, 29, 44));
   geneRegions.add(new GeneRegion(4, 45, 46));
   ```

   

   In this example, there are a total of 4 regions defined. Each GeneRegion consists of three parameters:

   	Region Number: Identifier for the region.
   	Starting Index: The beginning index of the region.
   	Ending Index: The final index of the region.

   You can customize these parameters according to your specific needs. Feel free to add or remove regions and modify their indices as necessary.

6. #### Compile and Run the Program

   Once you have set the necessary parameters, compile the code in your IDE and run the Main method. The console will output the rearranged quantization matrix.

## Additional Notes:

- Since the algorithm is implemented in Java, ensure that you have the Java Runtime Environment (JRE) installed. It is recommended to use the Java Development Kit (JDK) for compilation and execution.
- Ensure that your input files are correctly formatted to avoid runtime errors.
- If you encounter issues, check the IDE's console for error messages which can help in troubleshooting.
