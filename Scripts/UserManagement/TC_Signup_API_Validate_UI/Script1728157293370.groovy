import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import groovy.json.JsonSlurper
import internal.GlobalVariable
import com.kms.katalon.core.model.FailureHandling

// Step 1: Sign-Up Process

// Generate a random email
def newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)
def newPass = 'Mai@16490'

// Set the email to GlobalVariable for later use
GlobalVariable.newEmail = newEmail

// Send the sign-up request using the predefined 'SignUpAPI' object from Object Repository
def signUpResponse = WS.sendRequest(findTestObject('Object Repository/SignUpAPI', 
    [('newEmail') : newEmail, ('newPass') : newPass]))

// Extract the response body
def responseBody = signUpResponse.getResponseBodyContent()
def responseStatus = signUpResponse.getStatusCode()
println("Sign Up Response Status: " + responseStatus)
println("Sign Up Response Body (Raw): " + responseBody)

// Parse the JSON response
def jsonResponse = new JsonSlurper().parseText(responseBody)

// Log the full parsed response
println("Full Parsed Response: " + jsonResponse)

// Extract token if present
def token = jsonResponse?.token
if (token != null) {
    println("Token: " + token)
    GlobalVariable.authToken = token // Save token to GlobalVariable for future use
} else {
    println("Sign up failed: No token returned")
    return
}

// Step 2: UI Validation for Login

// Open browser and navigate to the login page
WebUI.openBrowser('')
WebUI.navigateToUrl('https://thinking-tester-contact-list.herokuapp.com')

// Input the email and password in the UI
WebUI.setText(findTestObject('Pages/LoginPage/txtUsername'), GlobalVariable.newEmail)
WebUI.setText(findTestObject('Pages/LoginPage/txtPassword'), newPass)

// Click the login button
WebUI.click(findTestObject('Pages/LoginPage/btnSubmit'))

// Add a delay to wait for possible redirection to the contact list
WebUI.delay(5) // Adjust this delay if needed

// Wait for the contact list page to load and verify the login was successful
WebUI.waitForPageLoad(10)

// Get the current URL
def currentUrl = WebUI.getUrl()

// Log the current URL to ensure it's captured
println("Current URL retrieved: " + currentUrl)  // Print the current URL

// Verify that the URL contains 'contactList' (more flexible verification)
boolean urlContainsContactList = currentUrl.contains('/contactList')

// Log the result of the URL check
if (urlContainsContactList) {
    println("The URL contains '/contactList'.")
} else {
    println("The URL does not contain '/contactList', login might have failed.")
    WebUI.verifyTrue(urlContainsContactList) // This will trigger a failure if the URL is incorrect
}

// Verify the presence of the logout button as another validation step
WebUI.verifyElementPresent(findTestObject('Pages/ContactListPage/btnLogout'), 10)

// Close the browser
WebUI.closeBrowser()

// Log completion message
println("Login successfully validated for email: " + GlobalVariable.newEmail)
println("Token used for sign up: " + token)  // Print token again if needed
