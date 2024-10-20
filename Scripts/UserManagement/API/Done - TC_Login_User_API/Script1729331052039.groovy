import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
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

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType

// Step 1: Perform Login via API using previously created user details
def loginPayload = [
    'email'    : GlobalVariable.newEmail, // Using the generated email from the test listener
    'password' : 'Mai@16490'               // Using the same password from the sign-up
]

// Send the login request using the API
def loginResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/LogInUser', [
    ('email')    : loginPayload.email,
    ('password') : loginPayload.password
]))

// Extract the login response content and status code
def loginResponseBody = loginResponse.getResponseBodyContent()
def loginStatus = loginResponse.getStatusCode()
println("Login Response Status: $loginStatus")

// Parse the login response to extract the token
def loginJsonResponse = new JsonSlurper().parseText(loginResponseBody)
def loginToken = loginJsonResponse?.token

// Check if the login was successful
if (loginToken) {
    println("Login successful, token received: ${loginToken}")
    GlobalVariable.token = loginToken // Store the token for future use
} else {
    println("Login failed: No token returned")
    return // Exit if login fails
}

// Step 2: Send GET request to verify user data via API
TestObject getUserProfileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')

// Add authorization header with bearer token from the login response
getUserProfileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

// Log the authorization header
println("Authorization header set to: Bearer " + GlobalVariable.token)

// Send GET request to retrieve the user profile
def getUserResponse = WS.sendRequest(getUserProfileRequest)

// Verify the status code is 200
WS.verifyResponseStatusCode(getUserResponse, 200)

// Parse the response body and verify the user details
def getUserResponseBody = getUserResponse.getResponseBodyContent()
def getUserJsonResponse = new JsonSlurper().parseText(getUserResponseBody)

// Log the response for debugging
println("Get User API Response: " + getUserResponseBody)

// Step 3: Verify that the API response matches the global variables (firstName, lastName, email)
WS.verifyElementPropertyValue(getUserResponse, 'firstName', GlobalVariable.newfirstName)
WS.verifyElementPropertyValue(getUserResponse, 'lastName', GlobalVariable.newlastName)
WS.verifyElementPropertyValue(getUserResponse, 'email', GlobalVariable.newEmail)

// Log the verification result
println("User data via API matches GlobalVariable: First Name, Last Name, and Email are correct.")