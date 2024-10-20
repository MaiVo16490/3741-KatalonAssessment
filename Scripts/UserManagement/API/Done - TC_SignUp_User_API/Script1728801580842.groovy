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