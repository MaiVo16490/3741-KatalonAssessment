import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static org.mockito.Mockito.RETURNS_DEFAULTS

import java.nio.file.WatchService

import javax.media.rtp.rtcp.SenderReport
import javax.security.auth.login.LoginContext
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.setting.WebServiceSettingStore
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import gherkin.ast.Step
import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.context.TestCaseContext
import utils.ApiHelper as ApiHelper

// Step 1: send request Add Contact via api
def response = ApiHelper.sendRequest('Object Repository/ContactsAPI/AddContact')

// Step 2: verify status code
ApiHelper.verifyStatusCode(response, 201)

// Step 3: verify reponse
def jsonRes = ApiHelper.getResponseBody(response)

// Step 4: send request Get User Profile via api
def responseGet = ApiHelper.sendRequest('Object Repository/ContactsAPI/GetContact')
def jsonResGet = ApiHelper.getResponseBody(responseGet)

// Step 5: verify data
WS.verifyEqual(jsonRes, jsonResGet)