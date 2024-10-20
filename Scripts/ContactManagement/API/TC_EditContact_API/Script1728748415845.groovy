import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import utils.ApiHelper as ApiHelper

// Step 1: send request Add Contact via api
def responseAdd = ApiHelper.sendRequest('Object Repository/ContactsAPI/AddContact')
ApiHelper.verifyStatusCode(responseAdd, 201)

// Step 2: send request Update Contact via api
def response = ApiHelper.sendRequest('Object Repository/ContactsAPI/PUT_UpdateContact')
//def jsonRes = ApiHelper.getResponseBody(response)

// Step 3: verify status code
ApiHelper.verifyStatusCode(response, 200)