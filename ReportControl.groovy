package com.custom.report

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import com.custom.report.ReportUtil
import com.custom.report.SuiteReportUtil
import java.util.TreeSet;
import com.driver.script.DriverScript;
import com.util.keyword.Util;
import com.test.accelerator.TestEngine

public class ReportControl{

	public static void initialize(){
		SuiteReportUtil.initializeReport();
		ReportUtil.initializeReport();
	}

	public static void tearDown(){
		TreeSet listOfPassedTC;
		Iterator<String> itr;
		TreeSet listOfRunnableTCs =new TreeSet(DriverScript.listOfExecutableTC);
		if(ReportUtil.listOfFailedTC!=null && ReportUtil.pass==true){
			listOfPassedTC=new TreeSet();
			println("listOfFailedTC: "+ReportUtil.listOfFailedTC)
			if(DriverScript.countOfTotalExecutedTC>ReportUtil.listOfFailedTC.size()){
				itr = ReportUtil.listOfFailedTC.iterator();
				while (itr.hasNext()) {
					listOfRunnableTCs.remove(itr.next());
				}
				listOfPassedTC=listOfRunnableTCs;
			}else if(DriverScript.countOfTotalExecutedTC==ReportUtil.listOfFailedTC.size()){
				listOfPassedTC=null;
			}
			println("listOfPassedTC: "+listOfPassedTC);
			if(listOfPassedTC!=null){
				itr = listOfPassedTC.iterator();
				while (itr.hasNext()) {
					SuiteReportUtil.markPassed(itr.next())
				}
			}
			itr = ReportUtil.listOfFailedTC.iterator();
			while (itr.hasNext()) {
				SuiteReportUtil.markFailed(itr.next())
			}
		}else if(!ReportUtil.pass){
			itr = listOfRunnableTCs.iterator();
			while (itr.hasNext()) {
				SuiteReportUtil.markFailed(itr.next())
			}
		}else{
			itr = listOfRunnableTCs.iterator();
			while (itr.hasNext()) {
				SuiteReportUtil.markPassed(itr.next())
			}
		}
		ReportUtil.tearDown();
		SuiteReportUtil.tearDown();
		ReportUtil.statusReport(); // updated
		SuiteReportUtil.statusReport();
		SuiteReportUtil.openHTMLReport();
		Util.zipMe()
		if(Util.getPropertyVal("RunOnJenkins").toUpperCase().equals("YES")){
			TestEngine.pause(15);
		}
		Util.triggerEmail();
	}
}
