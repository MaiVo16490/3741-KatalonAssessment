import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import utils.ApiHelper as ApiHelper

// Step 1: send request Add Contact via api
def responseAdd = ApiHelper.sendRequest('Object Repository/ContactsAPI/AddContact')
ApiHelper.verifyStatusCode(responseAdd, 201)

// Step 2: send request Update Contact via api
def response = ApiHelper.sendRequest('Object Repository/ContactsAPI/PUT_UpdateContact')
//def jsonRes = ApiHelper.getResponseBody(response)

// Step 3: Verify status code, check for 503 and log
if (response.getStatusCode() == 503) {
    ApiHelper.logBug("Known bug: Edit Contact endpoint returned 503.")
    assert false : "Test failed due to 503 error in Update Contact API."
} else {
    ApiHelper.verifyStatusCode(response, 200)
}