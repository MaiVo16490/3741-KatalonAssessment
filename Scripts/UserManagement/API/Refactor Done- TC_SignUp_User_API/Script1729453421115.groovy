import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import groovy.json.JsonSlurper
import internal.GlobalVariable
import utils.ApiHelper

boolean signUpSuccess = false
int maxRetries = 5 // Maximum retries for sign-up
int retryCount = 0
String Pass = 'Mai@16490' // Static password

// Retry sign-up process until success or maximum retry limit is reached
while (!signUpSuccess && retryCount < maxRetries) {
    
    // Generate random user details
    def randomFirstName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomFirstName'()
    def randomLastName = CustomKeywords.'customKeywords.RandomNameGenerator.generateRandomLastName'()
    def newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)
    
    // Store details in GlobalVariable for later use
    GlobalVariable.newEmail = newEmail
    GlobalVariable.newfirstName = randomFirstName
    GlobalVariable.newlastName = randomLastName

    // Log the details of the signup request
    println("Attempting Sign Up with Email: ${GlobalVariable.newEmail}, First Name: ${GlobalVariable.newfirstName}, Last Name: ${GlobalVariable.newlastName}")

    // Step 1: Send sign-up request
    def signUpResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/AddUser', [
        ('newEmail')    : GlobalVariable.newEmail,
        ('newPass')     : Pass,
        ('newfirstName'): GlobalVariable.newfirstName,
        ('newlastName') : GlobalVariable.newlastName
    ]))

    // Step 2: Extract response status and body
    def responseStatus = signUpResponse.getStatusCode()
    def jsonResponse = new JsonSlurper().parseText(signUpResponse.getResponseBodyContent())

    // Handle status 400 (Email in use), retry with a new email
    if (responseStatus == 400 && jsonResponse?.message == 'Email address is already in use') {
        println("Email already in use, retrying... (${retryCount + 1}/${maxRetries})")
        retryCount++
        continue // Retry with new details
    }

    // Log a bug if the status is not 200 or 400
 if (responseStatus != 201) {
    def errorMessage = "Sign-up failed with status: ${responseStatus} for email: ${GlobalVariable.newEmail}"
    println(errorMessage)
    ApiHelper.logBug(errorMessage) // Pass the error message to logBug method
    return // Exit test case on non-recoverable error
}

    // Step 3: Extract token and verify profile in one step
    def token = jsonResponse?.token
    if (token) {
        GlobalVariable.token = token // Store token for future use
        println("Sign-up successful, token received: ${token}")
        CustomKeywords.'utils.UserProfileVerification.verifyUserProfile'() 
        signUpSuccess = true 
        break // Exit loop once sign-up is successful
    } else {
        println("Sign up failed: No token returned")
        return
    }
}

// Log failure if max retries are reached without success
if (!signUpSuccess) {
    println("Sign-up process failed after ${maxRetries} attempts.")
    ApiHelper.logBug("Sign-up process failed after ${maxRetries} attempts for email: ${GlobalVariable.newEmail}")
}

