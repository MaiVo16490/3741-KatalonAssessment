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

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.context.TestCaseContext
import utils.ApiHelper as ApiHelper

// Step 1: send request Login via api
def responsePost = ApiHelper.sendRequest('Object Repository/UsersAPI/Auth')
def jsonResPost = ApiHelper.getResponseBody(responsePost)
GlobalVariable.token = jsonResPost.token

// Step 2: verify status code
ApiHelper.verifyStatusCode(responsePost, 200)

// Step 3: send request Get User Profile via api
def responseGet = ApiHelper.sendRequest('Object Repository/UsersAPI/GetUserProfile')
def jsonResGet = ApiHelper.getResponseBody(responseGet)


// Step 4: verify data
WS.verifyEqual(jsonResPost.user.firstName, jsonResGet.firstName) 
WS.verifyEqual(jsonResPost.user.lastName, jsonResGet.lastName)
WS.verifyEqual(jsonResPost.user.email, jsonResGet.email)
WS.verifyEqual(jsonResPost.user._id, jsonResGet._id)