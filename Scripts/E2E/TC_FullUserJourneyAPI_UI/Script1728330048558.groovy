import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.openqa.selenium.Keys as Keys
import java.nio.file.WatchService

//"1.User Sign up via API
//2.Login from the UI
//3.Adding contacts from the UI
//4.Update contact from the UI and validate update in API response
//5. Delete single contact from API, then check UI
//6.Adding mutiple contacts from the API, then check UI
//7.Delete all visible contacts and check the API response
//8.User logout"
// Step 1: Generate random first and last name using custom keywords
def randomFirstName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomFirstName'()
def randomLastName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomLastName'()

// Step 2: Generate random email and password for the new user
def newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)
def Pass = 'Mai@16490'

// Store generated email and names in GlobalVariable for later use
GlobalVariable.newEmail = newEmail
GlobalVariable.newfirstName = randomFirstName
GlobalVariable.newlastName = randomLastName

// Step 3: Log the details of the signup request before sending it
println("Sending Sign Up Request with:")
println("Email: " + GlobalVariable.newEmail)
println("First Name: " + GlobalVariable.newfirstName)
println("Last Name: " + GlobalVariable.newlastName)

// Step 4: Send the sign-up request using the existing test object
def signUpResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/AddUser', [
    ('newEmail') : GlobalVariable.newEmail,
    ('newPass')  : Pass,
    ('newfirstName'): GlobalVariable.newfirstName,
    ('newlastName') : GlobalVariable.newlastName
]))

// Step 5: Extract the response content and status code
def responseBody = signUpResponse.getResponseBodyContent()
def responseStatus = signUpResponse.getStatusCode()
println("Sign Up Response Status: $responseStatus")

// Step 6: Parse the JSON response
def jsonResponse = new JsonSlurper().parseText(responseBody)
println("Full Parsed Response: $jsonResponse")

// Step 7: Extract token from response (if present)
def token = jsonResponse?.token
if (token) {
    println("Token: $token")
    GlobalVariable.token = token // Store token for future use
} else {
    println("Sign up failed: No token returned")
    return // Exit if no token is returned
}

// Step 8: Send GET request to verify user data via API
TestObject getUserProfileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')

// Add authorization header with bearer token
getUserProfileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

// Log the authorization header
println("Authorization header set to: Bearer " + GlobalVariable.token)

// Send GET request
def getUserResponse = WS.sendRequest(getUserProfileRequest)

// Verify the status code is 200
WS.verifyResponseStatusCode(getUserResponse, 200)

// Parse the response
def getUserResponseBody = getUserResponse.getResponseBodyContent()
def getUserJsonResponse = new JsonSlurper().parseText(getUserResponseBody)

// Log the response for debugging
println("Get User API Response: " + getUserResponseBody)

// Step 9: Verify that the API returned the correct values
println("First Name from API: " + getUserJsonResponse.firstName)
println("Last Name from API: " + getUserJsonResponse.lastName)
println("Email from API: " + getUserJsonResponse.email)

// Step 10: Verify that the API response matches the global variables (firstName, lastName, email)
WS.verifyElementPropertyValue(getUserResponse, 'firstName', GlobalVariable.newfirstName)
WS.verifyElementPropertyValue(getUserResponse, 'lastName', GlobalVariable.newlastName)
WS.verifyElementPropertyValue(getUserResponse, 'email', GlobalVariable.newEmail)

// Log the verification result
println("User data via API matches GlobalVariable: First Name, Last Name, and Email are correct.")


// Step 7: UI Validation for Login

// Open browser and navigate to the login page
WebUI.openBrowser('')
WebUI.navigateToUrl('https://thinking-tester-contact-list.herokuapp.com')

// Input email and password in the login form
WebUI.setText(findTestObject('Pages/LoginPage/txtEmail'), GlobalVariable.newEmail)
WebUI.setText(findTestObject('Pages/LoginPage/txtPassword'), Pass)

// Click the login button
WebUI.click(findTestObject('Pages/LoginPage/btnSubmit'))

// Wait for possible redirection and page load
WebUI.delay(10) // Increase the delay to ensure page fully loads
WebUI.waitForPageLoad(10)

// Verify successful login by checking the URL
def currentUrl = WebUI.getUrl()
println("Current URL: " + currentUrl)
boolean urlContainsContactList = currentUrl.contains('/contactList')

if (urlContainsContactList) {
    println("Login successful: The URL contains '/contactList'.")
} else {
    println("Login failed: The URL does not contain '/contactList'.")
    WebUI.verifyTrue(urlContainsContactList) // Fail if URL is incorrect
}

// Verify presence of logout button to confirm successful login
WebUI.verifyElementPresent(findTestObject('Pages/ContactListPage/btnLogout'), 10)

// Close the browser
WebUI.closeBrowser()

// Log the successful login
println("Login successfully validated for email: ${GlobalVariable.newEmail}")