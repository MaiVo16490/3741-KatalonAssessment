import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import utils.ApiHelper as ApiHelper

// Step 1: send request Add Contact via api
def responseAdd = ApiHelper.sendRequest('Object Repository/ContactsAPI/AddContact')
ApiHelper.verifyStatusCode(responseAdd, 201)

// Step 2: send request Delete Contact via api
def response = ApiHelper.sendRequest('Object Repository/ContactsAPI/DeleteContact')

// Step 3: verify status code
if (response.getStatusCode() == 503) {
    ApiHelper.logBug("Known bug: Delete Contact endpoint returned 503.")
    assert false : "Test failed due to 503 error in Delete Contact API."
} else {
    ApiHelper.verifyStatusCode(response, 200)
}

// Step 4: verify delete contact successfully
def responseGet = ApiHelper.sendRequest('Object Repository/ContactsAPI/GetContact')
def jsonResGet = ApiHelper.getResponseBody(responseGet)
WS.verifyEqual(jsonResGet.size(), 0)