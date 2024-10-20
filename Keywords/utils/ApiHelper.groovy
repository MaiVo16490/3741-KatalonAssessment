package utils
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

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
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.json.JsonSlurper

import internal.GlobalVariable

public class ApiHelper {
	@Keyword
	static def sendRequest(String path) {
		return WS.sendRequest(findTestObject(path))
	}

	@Keyword
	static def verifyStatusCode(def response, int expectedStatusCode) {
		WS.verifyResponseStatusCode(response, expectedStatusCode, FailureHandling.STOP_ON_FAILURE)
	}

	@Keyword
	static def getResponseBody(def response) {
		return new JsonSlurper().parseText(response.getResponseBodyContent())
	}

	@Keyword
	static def getToken(def jsonResponse) {
		return jsonResponse.token
	}
	@Keyword
	static def setBody(String path, String body) {
		def request = findTestObject(path)
		request.setBodyContent(new HttpTextBodyContent(body))
		return request
	}
	@Keyword
	static def sendRequest(String path, String data) {
		def abc = ApiHelper.setBody(path, data)
		return WS.sendRequest(abc)
	}

	static void logBug(String bugMessage) {
		println "LOG BUG: ${bugMessage}"
		def logFile = new File("bug_log.txt")
		logFile << "${new Date()} - ${bugMessage}\n"
	}
}
