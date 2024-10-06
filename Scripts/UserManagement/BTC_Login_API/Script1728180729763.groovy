import groovy.json.JsonSlurper
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent

// Define the API request object using the correct path (without "Object Repository/")
def requestObject = findTestObject('Object Repository/Auth')

// Set the correct request body using HttpTextBodyContent
requestObject.setBodyContent(new HttpTextBodyContent('{"email": "bachmai16490@gmail.com", "password": "Mai@16490"}', "UTF-8", "application/json"))

// Send the POST request to login
def response = WS.sendRequest(requestObject)

// Verify status code 200
WS.verifyResponseStatusCode(response, 200)

// Parse the JSON response
def jsonResponse = new JsonSlurper().parseText(response.getResponseBodyContent())
println("Login API Response: " + jsonResponse)

// Extract token and email from response
def token = jsonResponse.token
def userEmail = jsonResponse.user.email
def userId = jsonResponse.user._id

println("Extracted Token: " + token)
println("User Email: " + userEmail)
println("User ID: " + userId)

// Verify email
assert userEmail == "bachmai16490@gmail.com" : "Email does not match"

// Save the token to a global variable
GlobalVariable.authToken = token

println("Login successful! Token and user details are validated.")
