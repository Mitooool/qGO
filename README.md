# qGO Algorithm

The qGO algorithm is designed for the precise quantification of mitochondrial genomes. The usage instructions are as follows:

## Steps for Usage:

1. ### Download the Source Code

   Download the source code to your local machine.

2. ### Import the Source Code

   Import the downloaded source code into your Java Integrated Development Environment (IDE). It is recommended to use IntelliJ IDEA for optimal experience.

3. ### Compile and Run the Program

   Compile and run the code in your IDE, then the following interface will appear：

   ![image](https://github.com/user-attachments/assets/536bafe2-bbb2-4303-8565-ae33d404bc90)



4. ### Specify the FASTA File

   You can specify the FASTA file using the file path text box located at the top of the interface. There are two ways to do this:

   ​	   ① Manual Entry: Click in the text box and type the full path to your .fasta file.

   ​	   ② Browse Button: Click "Browse" to open a file chooser dialog. Navigate to your .fasta file, select it, and click "Open" The selected file path will automatically populate the text box.

5. ### Gene Regions

   The qGO application allows you to add multiple gene regions with custom parameters.

   To add a gene region, you need to input three fields:

   ​	① Region Number: An identifier for the region.
   
   ​	② Starting Index: The beginning index of the region.
   
   ​	③ Ending Index: The final index of the region.

   After entering these details, click the button labeled "Add Gene Region" to add the entered gene region to the table.

   To remove a gene region from the table, follow these steps:

   ​	① Click on the desired row in the Gene Regions Table to highlight it.
   
   ​	② Click the button labeled "Remove Selected" to remove the highlighted gene region from the table.

7. ### Compile and Run the Program

   After selecting the FASTA file and defining the necessary gene regions, you can execute the application by clicking the button labeled "Run" The results will then be displayed in the console.

## Additional Notes:

- Since the algorithm is implemented in Java, ensure that you have the Java Runtime Environment (JRE) installed. It is recommended to use the Java Development Kit (JDK) for compilation and execution.
- Ensure that your input files are correctly formatted to avoid runtime errors.
- If you encounter issues, check the IDE's console for error messages which can help in troubleshooting.
