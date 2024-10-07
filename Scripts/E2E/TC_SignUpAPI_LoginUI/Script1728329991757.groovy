import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import groovy.json.JsonSlurper
import internal.GlobalVariable
import com.kms.katalon.core.model.FailureHandling

// Step 1: Sign-Up Process

// Generate random email and password for the new user
def newEmail = CustomKeywords.'customKeywords.RandomEmailGenerator.generateRandomEmail'(300)
def newPass = 'Mai@16490'

// Send sign-up request using the 'SignUpAPI' test object
def signUpResponse = WS.sendRequest(findTestObject('Object Repository/UsersAPI/AddUser', [
    ('newEmail') : newEmail, 
    ('newPass')  : newPass
]))

// Extract the response content and status code
def responseBody = signUpResponse.getResponseBodyContent()
def responseStatus = signUpResponse.getStatusCode()
println("Sign Up Response Status: $responseStatus")

// Parse the JSON response
def jsonResponse = new JsonSlurper().parseText(responseBody)
println("Full Parsed Response: $jsonResponse")


// Extract token from response (if present)
def token = jsonResponse?.token
if (token) {
    println("Token: $token")
    GlobalVariable.bearerAuthToken = token // Store token for future use
} else {
    println("Sign up failed: No token returned")
    return // Exit if no token is returned
}

// Store generated email in GlobalVariable for later use
GlobalVariable.newEmail = newEmail

// Step 2: UI Validation for Login

// Open browser and navigate to the login page
WebUI.openBrowser('')
WebUI.navigateToUrl('https://thinking-tester-contact-list.herokuapp.com')

// Input email and password in the login form
WebUI.setText(findTestObject('Pages/LoginPage/txtEmail'), GlobalVariable.newEmail)
WebUI.setText(findTestObject('Pages/LoginPage/txtPassword'), newPass)

// Click the login button
WebUI.click(findTestObject('Pages/LoginPage/btnSubmit'))

// Wait for possible redirection and page load
WebUI.delay(5) // Adjust as necessary
WebUI.waitForPageLoad(10)

// Verify successful login by checking the URL
def currentUrl = WebUI.getUrl()
println("Current URL: $currentUrl")
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
