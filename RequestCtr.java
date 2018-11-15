package com.hsc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


public class RequestCtr
{
  private static String recordID;
  private static String fileID;
  private int expectedTotal;
  private int extractFileCtr;
  private int originalTotal;
  private int originalFileCtr;
  private ArrayList<File> inputFiles = new ArrayList();
  
  public RequestCtr() {}
  
  public static void main(String[] args)
  {
    recordID = args[0];
    fileID = args[1];
    RequestCtr extract = new RequestCtr();
    try {
      extract.process();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void process() throws IOException {
    originalTotal = 0;
    originalFileCtr = 0;
    
    expectedTotal = 0;
    extractFileCtr = 0;
    

    getFileList();
    BufferedWriter writer = createOutputFileWriter();
    for (File file : inputFiles) {
      extractRecordFromFile(file, writer);
    }
    
    writer.write("=========================================================================");
    writer.newLine();
    
    String outputline = "Original requests from " + originalFileCtr + " input files are " + originalTotal;
    writer.write(outputline);
    writer.newLine();
    
    outputline = "Expected total requests from " + extractFileCtr + " extract files are " + expectedTotal;
    writer.write(outputline);
    writer.newLine();
    writer.close();
  }
  
  private void getFileList() {
    File currentFolder = new File(".");
    File[] listOfFiles = currentFolder.listFiles();
    
    for (File currentFile : listOfFiles) {
      if (currentFile.getName().indexOf(fileID) >= 0) {
        inputFiles.add(currentFile);
      }
    }
  }
  
  private void extractRecordFromFile(File file, BufferedWriter writer)
  {
    BufferedReader reader = null;
    int requestCtr = 0;
    
    try
    {
      reader = new BufferedReader(new FileReader(file));
      
      String line;
      
      while ((line = reader.readLine()) != null) {
        String line;
        if (line.startsWith(recordID))
        {

          requestCtr++;
          System.out.println(line);
        }
        else if (line.startsWith("99")) {
          String outputline = "Filename: " + file.getName() + "     #Requests: " + requestCtr;
          writer.write(outputline);
          writer.newLine();
          if (isExtractFile(file.getName())) {
            extractFileCtr += 1;
            expectedTotal += requestCtr;
          }
          
          if (!isOriginalInputFile(file.getName())) break;
          originalFileCtr += 1;
          originalTotal += requestCtr;
          

          break;
        }
      }
      

      reader.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private boolean isExtractFile(String fileName)
  {
    if (fileName.startsWith("extract_")) {
      return true;
    }
    return false;
  }
  
  private boolean isOriginalInputFile(String fileName)
  {
    if (fileName.startsWith("badDataExtract_")) {
      return false;
    }
    if (fileName.startsWith("extract_")) {
      return false;
    }
    if (fileName.startsWith("miss10_")) {
      return false;
    }
    if (fileName.startsWith("miss20_")) {
      return false;
    }
    if (fileName.startsWith("miss79_")) {
      return false;
    }
    
    return true;
  }
  
  private BufferedWriter createOutputFileWriter() {
    String outFileName = "RequestCtr.txt";
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(outFileName));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return writer;
  }
}
