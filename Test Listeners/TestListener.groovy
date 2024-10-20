import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.util.KeywordUtil
import utils.ApiHelper as ApiHelper
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

class TestListener {

 @BeforeTestCase
    def sampleBeforeTestCase(TestCaseContext testCaseContext) {
        def testCaseName = testCaseContext.getTestCaseId()

        if (testCaseName.contains('Delete_User')) {
            println "Starting sign up and login for test case: " + testCaseName
            performSignUpAndLogin()

        } else if (testCaseName.contains('Login_User_API')) {
            println "Test case is related to API login, proceeding with login only."
            performLoginOnly()

        } else if (testCaseName.contains('SignUp_User_API')) {
            println "Skipping sign-up and login for SignUp_User_API test case."

        } else if (!testCaseName.contains('Login_User_Web') && !testCaseName.contains('SignUp_User_Web')) {
            println "Starting login for regular test case."
          // Regular flow for other test cases (not related to Login or SignUp through the web)
            def response = ApiHelper.sendRequest('Object Repository/UsersAPI/Auth')
            ApiHelper.verifyStatusCode(response, 200)
            def jsonRes = ApiHelper.getResponseBody(response)
            GlobalVariable.token = jsonRes.token
            println "Login successfully"

        } else {
            println "Skipping login step for: " + testCaseName
        }
    }

      @AfterTestCase
    def sampleAfterTestCase(TestCaseContext testCaseContext) {
        def testCaseName = testCaseContext.getTestCaseId()

        // Check if the test case is related to Login_User_API or SignUp_User_API to clean up user data
        if (testCaseName.contains('Login_User_API') || testCaseName.contains('SignUp_User_API')) {
            println "Cleaning up after test case: " + testCaseName
            deleteUser()
        }
    }



    @BeforeTestSuite
    def sampleBeforeTestSuite(TestSuiteContext testSuiteContext) {
        // Add any setup logic here for the test suite
    }

    @AfterTestSuite
    def sampleAfterTestSuite(TestSuiteContext testSuiteContext) {
        // Add any teardown logic here for the test suite
    }

   // Function for handling sign-up and login via API
    def performSignUpAndLogin() {
    boolean signUpSuccess = false
    int maxRetries = 5 // Set a maximum number of retries to avoid infinite loops
    int retryCount = 0
    String Pass = 'Mai@16490' // Password is static

    // Retry the sign-up process until success or the maximum retry limit is reached
    while (!signUpSuccess && retryCount < maxRetries) {
        def randomFirstName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomFirstName'()
        def randomLastName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomLastName'()
        
        // Generate random email for the new user
        def newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)

        // Store generated email and names in GlobalVariable for later use
        GlobalVariable.newEmail = newEmail
        GlobalVariable.newfirstName = randomFirstName
        GlobalVariable.newlastName = randomLastName

        // Log the details of the signup request before sending it
        println("Sending Sign Up Request with:")
        println("Email: " + GlobalVariable.newEmail)
        println("First Name: " + GlobalVariable.newfirstName)
        println("Last Name: " + GlobalVariable.newlastName)

        // Step 4: Send the sign-up request using the existing test object
        def signUpResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/AddUser', [
            ('newEmail')    : GlobalVariable.newEmail,
            ('newPass')     : Pass,
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

        // Step 7: Check if the email is already in use and retry if needed
        if (responseStatus == 400 && jsonResponse?.message == 'Email address is already in use') {
            println("Email address is already in use. Retrying with a new email...")
            retryCount++
            continue // Retry the sign-up process
        }

        // Step 8: Extract token from response (if present)
        def token = jsonResponse?.token
        if (token) {
            println("Token: $token")
            GlobalVariable.token = token // Store token for future use
            signUpSuccess = true // Mark sign-up as successful
        } else {
            println("Sign up failed: No token returned")
            return // Exit if no token is returned
        }
    }

    // Check if sign-up was unsuccessful after max retries
    if (!signUpSuccess) {
        println("Sign-up process failed after $maxRetries attempts. Exiting.")
        return
    }

    // Step 9: Send GET request to verify user data via API
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

    // Step 10: Verify that the API returned the correct values
    println("First Name from API: " + getUserJsonResponse.firstName)
    println("Last Name from API: " + getUserJsonResponse.lastName)
    println("Email from API: " + getUserJsonResponse.email)

    // Step 11: Verify that the API response matches the global variables (firstName, lastName, email)
    WS.verifyElementPropertyValue(getUserResponse, 'firstName', GlobalVariable.newfirstName)
    WS.verifyElementPropertyValue(getUserResponse, 'lastName', GlobalVariable.newlastName)
    WS.verifyElementPropertyValue(getUserResponse, 'email', GlobalVariable.newEmail)

    // Log the verification result
    println("User data via API matches GlobalVariable: First Name, Last Name, and Email are correct.")
}
	
	
	// Function to delete the user via API after the test case
	def deleteUser() {
		if (GlobalVariable.token) {
			// Send request to delete the user using the token
			def deleteResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/DeleteUser', [
				('Authorization') : 'Bearer ' + GlobalVariable.token
			]))

			// Verify the response is successful (status code 200 or 204)
			def deleteStatus = deleteResponse.getStatusCode()
			if (deleteStatus == 200 || deleteStatus == 204) {
				println("User deleted successfully after test case.")
			} else {
				println("Failed to delete user. Status code: $deleteStatus")
			}
		} else {
			println("No token found, unable to delete user.")
		}
	}
}
