import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.Keys as Keys
import groovy.json.JsonOutput
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent


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
    GlobalVariable.bearerAuthToken = token // Store token for future use
} else {
    println("Sign up failed: No token returned")
    return // Exit if no token is returned
}

// Step 8: Send GET request to verify user data via API
TestObject getUserProfileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')

// Add authorization header with bearer token
getUserProfileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.bearerAuthToken))

// Log the authorization header
println("Authorization header set to: Bearer " + GlobalVariable.bearerAuthToken)

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



