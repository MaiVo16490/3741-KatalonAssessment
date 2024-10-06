import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import groovy.json.JsonSlurper as JsonSlurper
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty as TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType as ConditionType

// Open Browser and navigate to login page
WebUI.openBrowser('')

WebUI.navigateToUrl('https://thinking-tester-contact-list.herokuapp.com')

// Input the email and password
WebUI.setText(findTestObject('Pages/LoginPage/txtUsername'), GlobalVariable.email)

WebUI.setText(findTestObject('Pages/LoginPage/txtPassword'), GlobalVariable.password)

// Click the submit button to login
WebUI.click(findTestObject('Pages/LoginPage/btnSubmit'))

// Wait for page to load and then wait until URL contains 'contactList'
WebUI.waitForPageLoad(10)

WebUI.delay(5 // Adding a delay to allow time for redirection
    )

// Verify that the URL is correct
currentUrl = WebUI.getUrl()

WebUI.verifyMatch(currentUrl, 'https://thinking-tester-contact-list.herokuapp.com/contactList', false)

// Verify that the title contains "My Contacts"
String pageTitle = WebUI.getWindowTitle()
WebUI.verifyMatch(pageTitle, 'My Contacts', false)

// Verify that the logout button is present
WebUI.verifyElementPresent(findTestObject('Pages/ContactListPage/btnLogout'), 10)

// Close Browser
WebUI.closeBrowser()

