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
            println "Test case is related to API login, proceeding with sign-up."
            performSignUp()

        } else if (!testCaseName.contains('Login_User_Web') && !testCaseName.contains('SignUp_User_Web')) {
            println "Starting login for regular test case."
            loginUser() // Regular login flow for other test cases
        } else {
            println "Skipping login step for: " + testCaseName
        }
    }

    @AfterTestCase
    def sampleAfterTestCase(TestCaseContext testCaseContext) {
        def testCaseName = testCaseContext.getTestCaseId()

        if (testCaseName.contains('Login_User_API') || testCaseName.contains('SignUp_User_API')) {
           
			println "Cleaning up after test case: " + testCaseName
			deleteUser()
        }
    }

    def performSignUp() {
        def signUpSuccess = false
        def retryCount = 0
        def maxRetries = 5
        def Pass = 'Mai@16490'

        while (!signUpSuccess && retryCount < maxRetries) {
            generateRandomUserDetails() // Helper method to generate user data

            // Send the sign-up request using ApiHelper
            def requestBody = """
            {
                "email": "${GlobalVariable.newEmail}",
                "password": "$Pass",
                "firstName": "${GlobalVariable.newfirstName}",
                "lastName": "${GlobalVariable.newlastName}"
            }
            """
            def signUpResponse = ApiHelper.sendRequest('Object Repository/UsersAPI/AddUser', requestBody)
            ApiHelper.verifyStatusCode(signUpResponse, 201) // Assuming 200 for success

            def signUpResponseBody = ApiHelper.getResponseBody(signUpResponse)
            if (signUpResponseBody.message == 'Email address is already in use') {
                println("Email already in use. Retrying...")
                retryCount++
            } else {
                GlobalVariable.token = ApiHelper.getToken(signUpResponseBody)
                signUpSuccess = true
                verifyUserProfile() // Verify the user after sign-up
            }
        }

        if (!signUpSuccess) {
            println("Sign-up process failed after $maxRetries attempts.")
        }
    }

    def performSignUpAndLogin() {
        performSignUp() // Reuse performSignUp method
        if (GlobalVariable.token) {
            performLogin(GlobalVariable.newEmail, 'Mai@16490') // Call login after successful sign-up
        }
    }

    def performLogin(String email, String password) {
        // Prepare login request body
        def loginRequestBody = """
        {
            "email": "$email",
            "password": "$password"
        }
        """
        // Send the login request using ApiHelper
        def loginResponse = ApiHelper.sendRequest('Object Repository/UsersAPI/LogInUser', loginRequestBody)
        ApiHelper.verifyStatusCode(loginResponse, 200)

        def loginResponseBody = ApiHelper.getResponseBody(loginResponse)
        GlobalVariable.token = ApiHelper.getToken(loginResponseBody)

        println("Login successful, token received: ${GlobalVariable.token}")
        verifyUserProfile() // Verify the user profile after login
    }

    def loginUser() {
        def response = ApiHelper.sendRequest('Object Repository/UsersAPI/Auth')
        ApiHelper.verifyStatusCode(response, 200)
        def jsonResponse = ApiHelper.getResponseBody(response)
        GlobalVariable.token = jsonResponse.token
        println "Login successfully"
    }

    def generateRandomUserDetails() {
        GlobalVariable.newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)
        GlobalVariable.newfirstName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomFirstName'()
        GlobalVariable.newlastName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomLastName'()
    }


    def deleteUser() {
        if (GlobalVariable.token) {
            // Send delete request using ApiHelper
            def deleteResponse = ApiHelper.sendRequest('Object Repository/UsersAPI/DeleteUser', """
            {
                "Authorization": "Bearer ${GlobalVariable.token}"
            }
            """)
            ApiHelper.verifyStatusCode(deleteResponse, 200)
            println("User deleted successfully.")
        } else {
            println("No token found, unable to delete user.")
        }
    }
	
	// Step to Verify Profile Status and Data (Reusable)
	def verifyUserProfileAfterAuth() {
		// Send GET request to verify user profile after login or sign-up
		def getUserProfileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')
		getUserProfileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

		// Send request and validate status code
		def getUserResponse = ApiHelper.sendRequest('Object Repository/UsersAPI/GetUserProfile')

		if (getUserResponse.getStatusCode() != 200) {
			def errorMessage = "Get User Profile failed with status: ${getUserResponse.getStatusCode()}"
			println(errorMessage)
			ApiHelper.logBug(errorMessage)
			return // Exit if profile verification fails
		}

		// Extract and verify user data
		def getUserResponseBody = ApiHelper.getResponseBody(getUserResponse)
		println("Get User API Response: ${getUserResponseBody}")

		// Verify user data matches global variables
		WS.verifyElementPropertyValue(getUserResponse, 'firstName', GlobalVariable.newfirstName)
		WS.verifyElementPropertyValue(getUserResponse, 'lastName', GlobalVariable.newlastName)
		WS.verifyElementPropertyValue(getUserResponse, 'email', GlobalVariable.newEmail)

		println("User data via API matches GlobalVariable: First Name, Last Name, and Email are correct.")
	}
}
