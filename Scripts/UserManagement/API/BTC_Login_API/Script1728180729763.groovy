import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import internal.GlobalVariable

def response = WS.sendRequest(findTestObject('Object Repository/UsersAPI/Auth'))
WS.verifyResponseStatusCode(response, 200)
def jsonResponse = new JsonSlurper().parseText(response.getResponseBodyContent())
def token=jsonResponse.token
def bearer_token="bearer "+token
GlobalVariable.bearerAuthToken = bearer_token