package com.custom.report

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.awt.Desktop;
import com.driver.script.DriverScript;
import com.util.keyword.Util;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot

public class SuiteReportUtil extends DriverScript{
	public static String customReportHeader=Util.getPropertyVal("CustomReportHeader");
	public static String customReportDirectory;
	public static long startTime=0;
	public static int responseTime = 0, executionTimePerTC = 0;
	public static String directoryOfZipReport;
	public static boolean dirOfReportFolderGenerated=false;

	public static String createNewDirectory(String pathDir){
		File file = new File(pathDir);
		if (!file.exists()) {
			if (file.mkdir()) {
				return pathDir;
			} else {
				return null;
			}
		}else{
			return pathDir;
		}
	}

	public static String htmlReportPath(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String strDate = sdf.format(cal.getTime());

		SimpleDateFormat sdf1 = new SimpleDateFormat();
		sdf1.applyPattern("MM/dd/yyyy HH:mm:ss");
		Date date = null;
		try {
			date = sdf1.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String strDateNow=sdf1.format(date);
		timeStamp=strDateNow.toString().replace("/", "_").replace(":", "_").replace(" ", "_");
		createNewDirectory(System.getProperty("user.dir")+"/CustomReport");

		String fileName;
		if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
			fileName = "TestSuiteReport.html";
			customReportDirectory=System.getProperty("user.dir")+"/CustomReport/Reports";
			createNewDirectory(customReportDirectory);
			Util.deleteDirectory(customReportDirectory);
			dirOfReportFolderGenerated=true;
		}else{
			fileName = "TestSuiteLevel_"+timeStamp+".html";
			customReportDirectory=System.getProperty("user.dir")+"/CustomReport/TestResult_"+timeStamp;
			dirOfReportFolderGenerated=true;
		}
		createNewDirectory(customReportDirectory);
		return (customReportDirectory+"/"+fileName);
	}

	public static void writeToFile(String fileContent, String fileName) throws IOException {
		if(dirOfReportFolderGenerated){
			File file = new File(pathOfReportGenerated);
			OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
			Writer writer=new OutputStreamWriter(outputStream);
			writer.write(fileContent);
			writer.close();
		}
	}

	public static void statusReport(){
		try{
			int percentageOfPass=((countOfPassedTC/countOfTotalExecutedTC)*100);
			int percentageOfFail=((countOfFailedTC/countOfTotalExecutedTC)*100);
			String testEnvironment=testEnv.toUpperCase();
			long time=Util.getTotalExecutionTime(startTime,Util.getCurrentMilliSeconds());
			boolean responseSec = false;
			responseTime = (int)(time/60);
			if(responseTime == 0){
				responseTime= (int)(time);
				responseSec=true;
			}

			htmlStringBuilder.append("<h1></h1>");
			htmlStringBuilder.append("<table border=\"1\" bordercolor=\"#000000\">");
			htmlStringBuilder.append("<tr align=\"left\"><font face='Cambria' size=\"4\"><b>Suite Performance</b></font></tr>");
			htmlStringBuilder.append("<tr bgcolor=\"#00FFFF\" align=\"center\"><td><font face='Cambria'><b>Status</b></font></td><td><font face='Cambria'><b>Count(Details)</b></font></td><td><font face='Cambria'><b>Percentage(%)</b></font></td></tr>");
			if(countOfPassedTC==0){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Passed</b></font></td><td><font face='Cambria'><b>"+countOfPassedTC+"</b></font></td><td><font face='Cambria'><b>"+percentageOfPass+"</b></font></td></tr>");
			}else{
				htmlStringBuilder.append("<tr style=\"color:green\" align=\"center\"><td><font face='Cambria'><b>Passed</b></font></td><td><font face='Cambria'><b>"+countOfPassedTC+"</b></font></td><td><font face='Cambria'><b>"+percentageOfPass+"</b></font></td></tr>");
			}
			if(countOfFailedTC==0){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Failed</b></font></td><td><font face='Cambria'><b>"+countOfFailedTC+"</b></font></td><td><font face='Cambria'><b>"+percentageOfFail+"</b></font></td></tr>");
			}else{
				htmlStringBuilder.append("<tr style=\"color:red\" align=\"center\"><td><font face='Cambria'><b>Failed</b></font></td><td><font face='Cambria'><b>"+countOfFailedTC+"</b></font></td><td><font face='Cambria'><b>"+percentageOfFail+"</b></font></td></tr>");
			}
			htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Total Executed TC</b></font></td><td><font face='Cambria'><b>"+countOfTotalExecutedTC+"</b></font></td><td><font face='Cambria'><b>100</b></font></td></tr>");
			if(responseSec){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Total Execution Time(sec.)</b></font></td><td><font face='Cambria'><b>"+responseTime+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Total Execution Time(min.)</b></font></td><td><font face='Cambria'><b>"+responseTime+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
			}
			if(testEnvironment!=""){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Test Environment</b></font></td><td><font face='Cambria'><b>"+testEnvironment+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
			}
			htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>TimeStamp</b></font></td><td><font face='Cambria'><b>"+Util.getCurrentTimestamp()+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
			htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Machine IP</b></font></td><td><font face='Cambria'><b>"+Util.getYourMachineIP()+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");

			String headLessMode;
			if(!getPropertyVal("ExecutionPlatform").toUpperCase().equals("GRID")){
				if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
					htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Execution Platform</b></font></td><td><font face='Cambria'><b>Jenkins</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
				}else{
					if(getPropertyVal("HeadlessBrowser").toUpperCase().equals("YES")){
						headLessMode="True";
					}else{
						headLessMode="False";
					}
					htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>HeadLessMode</b></font></td><td><font face='Cambria'><b>"+headLessMode+"</b></font></td><td><font face='Cambria'><b>NA</b></font></td></tr>");
				}
			}

			htmlStringBuilder.append("</table>");
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		}catch(Throwable t){
			println("=================================================================================================")
			println("===========================No test case of your runnable test suite is in 'Yes' runmode=========================")
			println("=================================================================================================")
		}
	}

	public static void initializeReport(){
		String tableHeader=customReportHeader+"- Test Suite Level";
		pathOfReportGenerated=htmlReportPath();
		startTime=Util.getCurrentMilliSeconds();
		try{
			initializeReport=true;
			htmlStringBuilder=new StringBuilder();
			htmlStringBuilder.append("<html><center><head></center></head>");
			htmlStringBuilder.append("<body>");
			htmlStringBuilder.append("<h1><center><font face='Cambria'><b>"+tableHeader+"</b></font></center></h1>");
			htmlStringBuilder.append("<table style=\"width: 80%\""
					+ "align=\"center\" "
					+ "border=\"1\" "
					+ "style=\"table-layout:fixed\" "
					+ "bordercolor=\"#000000\">");
			if(!DriverScript.testTypeHeader.equals("NA")){
				htmlStringBuilder.append("<tr bgcolor=\"#00FFFF\" align=\"center\"><td style=\"width: 10%\"><font face='Cambria'><b>TCID</font></b></td><td style=\"width: 15%\"><font face='Cambria'><b>TestType</b></font></td><td><font face='Cambria'><b>TestCaseName</b></font></td><td style=\"width: 15%\"><font face='Cambria'><b>ExecutionStatus</b></font></td><td style=\"width: 15%\"><font face='Cambria'><b>ExecutionTime</b></font></td></tr>");
			}else{
				htmlStringBuilder.append("<tr bgcolor=\"#00FFFF\" align=\"center\"><td style=\"width: 10%\"><font face='Cambria'><b>TCID</font></b></td><td><font face='Cambria'><b>TestCaseName</b></font></td><td style=\"width: 15%\"><font face='Cambria'><b>ExecutionStatus</b></font></td><td style=\"width: 15%\"><font face='Cambria'><b>ExecutionTime</b></font></td></tr>");
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	public static void markPassed(String testCaseID){
		try {
			if(!initializeReport){
				initializeReport();
			}
			countOfPassedTC++;
			int rowNum=xlsController.getCellRowNum(TESTDATASHEET, TCID, testCaseID);
			String testCaseName=xlsController.getCellData(TESTDATASHEET, "TestCaseName", rowNum);
			String testType;

			if(!DriverScript.testTypeHeader.equals("NA")){
				testType=DriverScript.getTestType(rowNum);
				//htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testType+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testType+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td><td><font face='Cambria'><b>"+map_tcid_executiontime.get (testCaseID)+"</b></font></a></td></tr>");
			}else{
				//htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td><td><font face='Cambria'><b>"+map_tcid_executiontime.get (testCaseID)+"</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void markFailed(String testCaseID){
		try {
			if(!initializeReport){
				initializeReport();
			}
			countOfFailedTC++;
			int rowNum=xlsController.getCellRowNum(TESTDATASHEET, TCID, testCaseID);
			String testCaseName=xlsController.getCellData(TESTDATASHEET, "TestCaseName", rowNum);
			String testType;

			if(!DriverScript.testTypeHeader.equals("NA")){
				testType=DriverScript.getTestType(rowNum);
				//htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testType+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testType+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td><td><font face='Cambria'><b>"+map_tcid_executiontime.get (testCaseID)+"</b></font></a></td></tr>")
			}else{
				//htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+ReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td><td><font face='Cambria'><b>"+map_tcid_executiontime.get (testCaseID)+"</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void tearDown(){
		htmlStringBuilder.append("</table></body></html>");
	}


	public static String openHTMLReport() throws IOException{
		try{
			if (!Util.getPropertyVal("RunOnJenkins").toUpperCase().equals("YES") && Util.getPropertyVal("WantToOpenCustomReport").toUpperCase().equals("YES")){
				File file = new File(pathOfReportGenerated);
				if(!Desktop.isDesktopSupported()){
					println("Desktop is not supported");
				}
				Desktop desktop = Desktop.getDesktop();
				if(file.exists()) desktop.open(file);
				return pathOfReportGenerated;
			}else{
				return null;
			}
		}catch(Throwable t){
			t.printStackTrace();
			return null;
		}
	}


	public static String getZip(){
		if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
			String[] srcFiles = {pathOfReportGenerated};
			directoryOfZipReport=System.getProperty("user.dir")+"/CustomReport/Reports.zip";
			try {
				byte[] buffer = new byte[1024];
				FileOutputStream fos = new FileOutputStream(directoryOfZipReport);
				ZipOutputStream zos = new ZipOutputStream(fos);
				for (int i=0; i < srcFiles.length; i++) {
					File srcFile = new File(srcFiles[i]);
					FileInputStream fis = new FileInputStream(srcFile);
					zos.putNextEntry(new ZipEntry(srcFile.getName()));
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
					fis.close();
				}
				zos.close();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}



	/*
	 ####################################################################################
	 ##############################
	 # Function Name : screenshotCapture
	 # Description   : This function will take screenshot of the screen
	 # Developed on : 07/18/2019
	 # Author : Subrato Sarkar
	 ####################################################################################
	 ##############################
	 */
	public static String screeshotCapture() throws Exception{
		String pathOfScreenshotCaptured;
		try{
			SuiteReportUtil.createNewDirectory(customReportDirectory+"/Screenshot");
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar now = Calendar.getInstance();
			Robot robot = new Robot();
			String screenshotFileExtension="Screenshot_"+formatter.format(now.getTime()).toString().replace("/", "_").replace(":", "_").replace(" ", "_")+".jpg";
			File source_file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(source_file, new File(customReportDirectory+"/Screenshot/"+screenshotFileExtension));
			pathOfScreenshotCaptured = "Screenshot/"+screenshotFileExtension;
		}catch(Throwable t){
			pathOfScreenshotCaptured=null;
		}
		return pathOfScreenshotCaptured;
	}


	public static String PDFCapture() throws Exception{
		String pathOfPDFCaptured;
		try{
			SuiteReportUtil.createNewDirectory(customReportDirectory+"/PDF");
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Calendar now = Calendar.getInstance();
			Robot robot = new Robot();
			String PDFFileExtension="PDF_"+formatter.format(now.getTime()).toString().replace("/", "_").replace(":", "_").replace(" ", "_")+".pdf";
			File file = new File(System.getProperty("user.dir")+File.separator+"Reports"+File.separator+"PDF"+File.separator+"correspondence.pdf");

			FileUtils.copyFile(file, new File(customReportDirectory+"/PDF/"+PDFFileExtension));
			pathOfPDFCaptured = "PDF/"+PDFFileExtension;
		}catch(Throwable t){
			pathOfPDFCaptured=null;
		}
		return pathOfPDFCaptured;
	}
}
