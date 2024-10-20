import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonSlurper
import internal.GlobalVariable




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