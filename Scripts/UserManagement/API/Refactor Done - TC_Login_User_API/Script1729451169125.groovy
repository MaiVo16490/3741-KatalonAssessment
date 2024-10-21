import internal.GlobalVariable
import utils.ApiHelper as ApiHelper
// Step 1: Prepare login payload
def loginPayload = [
    'email'    : GlobalVariable.newEmail, // Using generated email from the listener
    'password' : 'Mai@16490'               // Static password
]

// Step 2: Send login request using ApiHelper and verify the response
def loginRequestBody = """
{
    "email": "${loginPayload.email}",
    "password": "${loginPayload.password}"
}
"""
def loginResponse = ApiHelper.sendRequest('Object Repository/UsersAPI/LogInUser', loginRequestBody)

// Step 3: Verify login status, log bug if failed, and mark test as failed
def loginStatus = loginResponse.getStatusCode()
if (loginStatus != 200) {
    def errorMessage = "Login failed with status: ${loginStatus}" 
    println(errorMessage)
    ApiHelper.logBug("Login failed with status: ${loginStatus} for email: ${loginPayload.email}") // No extra parentheses
    return
} else {
    println("Login Response Status: ${loginStatus}")
}