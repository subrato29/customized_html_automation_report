package com.custom.report

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import internal.GlobalVariable

import java.io.*;
import java.text.ParseException;
import java.util.Date;
import com.driver.script.DriverScript;
import com.custom.report.SuiteReportUtil;
import java.awt.List;
import java.util.TreeSet;
import com.test.accelerator.TestEngine;
import com.util.keyword.Util;

public class ReportUtil extends DriverScript{
	public static StringBuilder htmlStringBuilder;
	public static boolean initializeReport=false;
	public static String pathOfReportGenerated;
	public static ArrayList<String> listOfFailedTC = null;
	public static boolean pass=false;

	public static long startTime=0;
	public static int responseTime=0;

	public static String htmlReportPath(){
		String fileName;
		if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
			fileName = "TestCaseReport.html";
		}else{
			fileName = "TestCaseLevel_"+timeStamp+".html";
		}
		return (SuiteReportUtil.customReportDirectory+"/"+fileName);
	}

	public static void writeToFile(String fileContent, String fileName) throws IOException {
		if(SuiteReportUtil.dirOfReportFolderGenerated){
			File file = new File(pathOfReportGenerated);
			OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
			Writer writer=new OutputStreamWriter(outputStream);
			writer.write(fileContent);
			writer.close();
		}
	}

	public static void initializeReport(){
		if (testEnv != null) {
			String tableHeader;
			if (testType.equals("SANITY")) {
				tableHeader = "CBMS Automated Sanity Test Report";
			} else {
				tableHeader = SuiteReportUtil.customReportHeader+"- Test Case Level";
			}
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
				htmlStringBuilder.append("<tr bgcolor=\"#FFA07A\" align=\"center\"><td style=\"width: 7%\"><font face='Cambria'><b>TCID</font></b></td><td><font face='Cambria'><b>TestCaseName</font></b></td><td style=\"width: 50%\"><font face='Cambria'><b>Step Description</b></font></td><td style=\"width: 8%\"><font face='Cambria'><b>ExecutionStatus</b></font></td></tr>");
			}catch(Throwable t){
			}
		}
	}
	public static void markPassed(String comment){
		try {
			if(!initializeReport){
				initializeReport();
			}
			pass=true;
			if(getPropertyVal("ScreenshotPass").toUpperCase().equals("YES") && (!getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")) && beforeTestSuite){
				String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
				if(pathOfScreenshotCaptured!=null){
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				}else{
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				}
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void markPassed(String comment, boolean wantScreenshotCapture){
		try {
			if(!initializeReport){
				initializeReport();
			}
			pass=true;
			if(wantScreenshotCapture && (!getPropertyVal("HeadlessBrowser").toUpperCase().equals("YES")) && (!getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")) && beforeTestSuite){
				String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
				if(pathOfScreenshotCaptured!=null){
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				}else{
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
				}
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='green' face='Cambria'><b>PASS</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void markFailed(String comment){
		try {
			if(continueRun){
				if(!initializeReport){
					initializeReport();
				}
				if(listOfFailedTC == null){
					listOfFailedTC = new ArrayList<>();
				}
				if(logout){
					continueRun=false;
				}
				listOfFailedTC.add(testCaseID);
				println (listOfFailedTC)
				if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES") && beforeTestSuite){
					String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
					if(pathOfScreenshotCaptured!=null){
						htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
					}else{
						htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
					}
				}else{
					if(getPropertyVal("ScreenshotFail").toUpperCase().equals("YES") && beforeTestSuite){
						String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
						if(pathOfScreenshotCaptured!=null){
							htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
						}else{
							htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
						}
					}else{
						htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
					}
				}
				writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
				if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
					TestEngine.quit();
				}else{
					if(getPropertyVal("FailureAndExit").toUpperCase().equals("YES") || !login){
						TestEngine.quit();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void markFailed(String comment,boolean wantScreenshotCapture){
		try {
			if(!initializeReport){
				initializeReport();
			}
			if(listOfFailedTC==null){
				listOfFailedTC=new TreeSet();
			}
			listOfFailedTC.add(testCaseID);
			if(wantScreenshotCapture && (!getPropertyVal("HeadlessBrowser").toUpperCase().equals("YES")) && (!getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")) && beforeTestSuite){
				String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
				if(pathOfScreenshotCaptured!=null){
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
				}else{
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
				}
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='red' face='Cambria'><b>FAIL</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
			if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
				TestEngine.quit();
			}else{
				if(getPropertyVal("FailureAndExit").toUpperCase().equals("YES")){
					TestEngine.quit();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void markInfo(String comment){
		try {
			if(!initializeReport){
				initializeReport();
			}
			htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='blue' face='Cambria'><b>INFO</b></font></a></td></tr>");
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void markWarning(String comment){
		try {
			if(!initializeReport){
				initializeReport();
			}
			if(getPropertyVal("ScreenshotWarning").toUpperCase().equals("YES") && beforeTestSuite){
				String pathOfScreenshotCaptured=SuiteReportUtil.screeshotCapture()
				if(pathOfScreenshotCaptured!=null){
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><a href="+pathOfScreenshotCaptured+"><font color='orange' face='Cambria'><b>WARNING</b></font></a></td></tr>");
				}else{
					htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='orange' face='Cambria'><b>WARNING</b></font></a></td></tr>");
				}
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><a href="+SuiteReportUtil.pathOfReportGenerated+"><font face='Cambria'>"+testCaseID+"</font></td><td><font face='Cambria'>"+testCaseName+"</font></td><td><font face='Cambria'>"+comment+"</font></td><td><font color='orange' face='Cambria'><b>WARNING</b></font></a></td></tr>");
			}
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void tearDown(){
		htmlStringBuilder.append("</table></body></html>");
	}


	public static void SavePDF(){
		SuiteReportUtil.PDFCapture()
	}

	public static void statusReport(){
		try{
			String testEnvironment=testEnv;
			long time=Util.getTotalExecutionTime(startTime,Util.getCurrentMilliSeconds());
			boolean responseSec = false;
			responseTime= (int)(time/60);
			if(responseTime == 0){
				responseTime= (int)(time);
				responseSec=true;
			}

			htmlStringBuilder.append("<h1></h1>");
			htmlStringBuilder.append("<table border=\"1\" bordercolor=\"#000000\">");
			htmlStringBuilder.append("<tr align=\"left\"><font face='Cambria' size=\"4\"><b>Test Case Performance</b></font></tr>");
			htmlStringBuilder.append("<tr bgcolor=\"#FFA07A\" align=\"center\"><td><font face='Cambria'><b>Status</b></font></td><td><font face='Cambria'><b>Details</b></font></td></tr>");
			if(responseSec){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Total Execution Time(sec.)</b></font></td><td><font face='Cambria'><b>"+responseTime+"</b></font></td></tr>");
			}else{
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Total Execution Time(min.)</b></font></td><td><font face='Cambria'><b>"+responseTime+"</b></font></td></tr>");
			}
			if(testEnvironment!=""){
				htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Test Environment</b></font></td><td><font face='Cambria'><b>"+testEnvironment+"</b></font></td></tr>");
			}
			htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>TimeStamp</b></font></td><td><font face='Cambria'><b>"+Util.getCurrentTimestamp()+"</b></font></td></tr>");
			if(!getPropertyVal("ExecutionPlatform").toUpperCase().equals("GRID")){
				if(getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
					htmlStringBuilder.append("<tr align=\"center\"><td><font face='Cambria'><b>Execution Platform</b></font></td><td><font face='Cambria'><b>Jenkins</b></font></td></tr>");
				}
			}
			htmlStringBuilder.append("</table>");
			writeToFile(htmlStringBuilder.toString(),pathOfReportGenerated);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
}
