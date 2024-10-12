package customKeywords

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

import internal.GlobalVariable

import customKeywords.RandomNameGenerator


import com.kms.katalon.core.annotation.Keyword

public class RandomNameGenerator {

	@Keyword
	def generateRandomFirstName() {
		def firstNames = [
			"John",
			"Jane",
			"Alex",
			"Chris",
			"Katie",
			"Linda",
			"Steve",
			"Michael",
			"Anna",
			"David"
		]
		return firstNames[(new Random()).nextInt(firstNames.size())]
	}

	@Keyword
	def generateRandomLastName() {
		def lastNames = [
			"Smith",
			"Johnson",
			"Brown",
			"Taylor",
			"Anderson",
			"Lee",
			"Martinez",
			"Garcia",
			"Clark",
			"Lewis"
		]
		return lastNames[(new Random()).nextInt(lastNames.size())]
	}
}