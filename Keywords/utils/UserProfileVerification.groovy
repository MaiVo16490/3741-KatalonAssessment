package utils

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject // Import findTestObject

class UserProfileVerification {

    @Keyword
    def verifyUserProfile() {
        // Add authorization header with bearer token
        def profileRequest = findTestObject('Object Repository/UsersAPI/GetUserProfile')
        profileRequest.getHttpHeaderProperties().add(new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer ' + GlobalVariable.token))

        // Send the profile request and validate status code
        def profileResponse = WS.sendRequest(profileRequest)
        WS.verifyResponseStatusCode(profileResponse, 200)

        // Verify that the profile data matches the sign-up details
        WS.verifyElementPropertyValue(profileResponse, 'firstName', GlobalVariable.newfirstName)
        WS.verifyElementPropertyValue(profileResponse, 'lastName', GlobalVariable.newlastName)
        WS.verifyElementPropertyValue(profileResponse, 'email', GlobalVariable.newEmail)

        println("User profile successfully verified.")
    }
}
