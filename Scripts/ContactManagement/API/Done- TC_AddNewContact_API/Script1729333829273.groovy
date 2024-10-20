import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import utils.ApiHelper as ApiHelper

String data = 
"""
    {
        "firstName": "${firstName}",
        "lastName": "${lastName}",
        "birthdate": "${birthDate}",
        "email": "${email}",
        "phone": "${phone}",
        "street1": "${street1}",
		"street2": "${street2}",
		"city": "${city}",
		"stateProvince": "${stateProvince}",
		"stateProvince": "${postalCode}",
		"country": "${country}"
    }
"""

// Step 1: send request Add Contact via api
def response = ApiHelper.sendRequest('Object Repository/ContactsAPI/AddContact', data)

// Step 2: verify status code
ApiHelper.verifyStatusCode(response, 201)

// Step 3: verify reponse
def jsonRes = ApiHelper.getResponseBody(response)

// Step 4: send request Get User Profile via api
def responseGet = ApiHelper.sendRequest('Object Repository/ContactsAPI/GetContact')
def jsonResGet = ApiHelper.getResponseBody(responseGet)

// Step 5: verify data
['firstName', 'lastName', 'birthdate', 'email', 'phone', 'street1', 'street2', 'city', 'stateProvince', 'postalCode', 'country', 'owner'].each {
	WS.verifyEqual(jsonResGet[0][it], jsonRes[it])
}