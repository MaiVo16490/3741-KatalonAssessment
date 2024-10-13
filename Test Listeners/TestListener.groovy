import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static org.mockito.Mockito.RETURNS_DEFAULTS

import java.nio.file.WatchService

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.util.KeywordUtil

import groovy.json.JsonSlurper

import utils.ApiHelper as ApiHelper


class TestListener {
	@BeforeTestCase
	def sampleBeforeTestCase(TestCaseContext testCaseContext) {		
		def testCaseName = testCaseContext.getTestCaseId()
		if (!testCaseName.contains('Login') && !testCaseName.contains('SignUp')) {
			def response = ApiHelper.sendRequest('Object Repository/UsersAPI/Auth')
			ApiHelper.verifyStatusCode(response, 200)
			def jsonRes = ApiHelper.getResponseBody(response)
			GlobalVariable.bearerAuthToken = jsonRes.token
			println "login successfully"
		} else {
			println "Skip login step"
		}
	}

	@AfterTestCase
	def sampleAfterTestCase(TestCaseContext testCaseContext) {

	}

	@BeforeTestSuite
	def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {

	}

	@AfterTestSuite
	def sampleAfterTestSuite(TestSuiteContext testSuiteContext) {

	}
}