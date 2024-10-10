**3741 - Katalon Assessment**

Project Overview

This project is built by Võ Đình Bạch Mai for the assessment of automation skills using Katalon Studio, focusing on both UI and API functionalities for the Contact List application. The automation covers key features such as User Management and Contact Management, ensuring comprehensive validation across both interfaces.

**Goals**

Automate core features like User Management and Contact Management.
Implement a structured approach using combined UI and API validations for better test coverage and efficiency.

**Coding Conventions**

1. Naming Conventions
   
  1.1 Test Cases:
  Prefix test case names with TC_ and include a descriptive name indicating the function being tested.
  Example: TC_AddContact_UI_Validate_API for a test case that adds a contact via UI and validates it via API.

  1.2 Variables and Methods:
  Use camelCase for variable names and method names.
  Example: firstName, submitForm()

  1.3 UI Elements:
  Follow a specific naming convention for each UI element type to maintain consistency and clarity:
  txt: For text fields (e.g., txtFirstName)
  btn: For buttons (e.g., btnSubmit)
  tbl: For tables (e.g., tblContactList)
  ....

2. Comments
  Comment all complex logic and important sections of the code.
  
  Use block comments for methods and inline comments for specific code explanations.

3. Code Structure
  Follow the Single Responsibility Principle for each test case to ensure each case handles one task only.
  
  Reuse code by creating reusable methods in Custom Keywords to reduce duplication.

**Test Execution**

1. Running Tests:
  Test cases can be run individually or grouped into Test Suites for organized test execution.
  
  Test execution can be done through the Katalon UI or via the command line for integration with CI/CD pipelines.

2. Setup and Teardown
   
  Test Listeners for Setup and Teardown
  
  Test Listeners are used to automatically set up the environment before the test suite starts and clean up the environment afterward.
  
  Setup: Prepares necessary test data, initializes API tokens, or configures the application state.
  
  Teardown: Cleans up the test environment, removes test data, and resets the application state.

Last Updated: October 10, 2024
