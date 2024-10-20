import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
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
import groovy.json.JsonSlurper
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent // Import the correct class
import groovy.json.JsonOutput

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent // Import the correct class
import groovy.json.JsonOutput



   // Step 1: Generate random first and last names
def randomFirstName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomFirstName'()
def randomLastName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomLastName'()

// Update global variables with the newly generated names
GlobalVariable.newfirstName = randomFirstName
GlobalVariable.newlastName = randomLastName

// Log the generated names for debugging
println("Generated Random First Name: ${GlobalVariable.newfirstName}, Random Last Name: ${GlobalVariable.newlastName}")

// Step 2: Define the payload for editing the user
def editPayload = [
    'firstName': GlobalVariable.newfirstName,
    'lastName' : GlobalVariable.newlastName
]

// Step 3: Create the request object for editing the user
TestObject editUserRequest = findTestObject('Object Repository/UsersAPI/UpdateUser')

// Add Authorization header with the bearer token
editUserRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

// Set the body content using HttpTextBodyContent
editUserRequest.setBodyContent(new HttpTextBodyContent(JsonOutput.toJson(editPayload), "UTF-8", "application/json"))

// Step 4: Send the request to edit the user
def editResponse = WS.sendRequest(editUserRequest)

// Extract the response and status code
def editResponseBody = editResponse.getResponseBodyContent()
def editStatus = editResponse.getStatusCode()
println("Edit User Response Status: $editStatus")

// Parse the response
def editJsonResponse = new JsonSlurper().parseText(editResponseBody)

// Log the edited response for debugging
println("Edit User Response Body: " + editResponseBody)

// Verify if the update was successful
if (editStatus == 200) {
    println "User edited successfully: New First Name: ${editJsonResponse.firstName}, New Last Name: ${editJsonResponse.lastName}"
} else {
    println "Failed to edit user. Status code: $editStatus"
}

// Step 5: Send GET request to verify user data via API
TestObject getUserProfileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')

// Add authorization header with bearer token
getUserProfileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

// Log the authorization header
println("Authorization header set to: Bearer " + GlobalVariable.token)

// Send GET request to retrieve the user profile
def getUserResponse = WS.sendRequest(getUserProfileRequest)

// Verify the status code is 200
WS.verifyResponseStatusCode(getUserResponse, 200)

// Parse the response body and log it for debugging
def getUserResponseBody = getUserResponse.getResponseBodyContent()
def getUserJsonResponse = new JsonSlurper().parseText(getUserResponseBody)
println("Get User API Response: " + getUserResponseBody)

// Step 6: Verify that the API response matches the new first and last names
def firstNameAfterEdit = getUserJsonResponse.firstName
def lastNameAfterEdit = getUserJsonResponse.lastName

println("First Name After Edit: ${firstNameAfterEdit}, Last Name After Edit: ${lastNameAfterEdit}")

WS.verifyElementPropertyValue(getUserResponse, 'firstName', GlobalVariable.newfirstName)
WS.verifyElementPropertyValue(getUserResponse, 'lastName', GlobalVariable.newlastName)

// Optional: Verify email if required
WS.verifyElementPropertyValue(getUserResponse, 'email', GlobalVariable.email)

println("User data via API matches GlobalVariable: First Name, Last Name, and Email are correct.")