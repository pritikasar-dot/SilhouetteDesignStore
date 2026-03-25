/**
 * 
 */
package com.mystore.dataprovider;

import java.util.Map;

import org.testng.annotations.DataProvider;

import com.mystore.utility.NewExcelLibrary;

/**
 * 
 */
public class DataProviders {
 NewExcelLibrary obj = new NewExcelLibrary();

//Class --> LoginPageTest,HomePageTest Test Case--> loginTest, wishListTest, orderHistoryandDetailsTest

 @DataProvider(name = "Credentials")
 public Object[][] getCredentials() {
     String sheetName = "Credentials";
     
     int totalRows = obj.getRowCount(sheetName);
     int totalCols = obj.getColumnCount(sheetName);

     // If only headers present or no rows, return empty
     if (totalRows <= 1) {
         System.out.println("⚠ No data found in sheet: " + sheetName);
         return new Object[0][0];
     }

     // Actual data rows (excluding header row)
     int actRows = totalRows - 1;
     Object[][] data = new Object[actRows][totalCols];

     // Fill data from row 2 onwards
     for (int i = 0; i < actRows; i++) {
         for (int j = 0; j < totalCols; j++) {
             data[i][j] = obj.getCellData(sheetName, j, i + 2); 
         }
     }
     return data;
 }

//Class --> AccountCreationPage  Test Case--> verifyCreateAccountPageTest	
	@DataProvider(name = "email")
	public Object[][] getEmail() {
		// Totals rows count
		int rows = obj.getRowCount("Email");
		// Total Columns
		int column = obj.getColumnCount("Email");
		int actRows = rows - 1;

		Object[][] data = new Object[actRows][column];

		for (int i = 0; i < actRows; i++) {
			for (int j = 0; j < column; j++) {
				data[i][j] = obj.getCellData("Email", j, i + 2);
			}
		}
		return data;
	}


	// Class --> SearchResultPageTest, Test Case--> productAvailabilityTest
	@DataProvider(name = "searchProduct")
	public Object[][] getProductPrice() {
		// Totals rows count
		int rows = obj.getRowCount("SearchProduct");
		// Total Columns
		int column = obj.getColumnCount("SearchProduct");
		int actRows = rows - 1;

		Object[][] data = new Object[actRows][column];

		for (int i = 0; i < actRows; i++) {
			for (int j = 0; j < column; j++) {
				data[i][j] = obj.getCellData("SearchProduct", j, i + 2);
			}
		}
		return data;
	}
	
	 @DataProvider(name = "registrationData")
	    public Object[][] getRegistrationData() {
	        return new Object[][] {
	            // First Name | Last Name | Email                           | Password   | Expected Result | MessageType
	            {"John", "Doe1", "johndoe1" + System.currentTimeMillis() + "@test.com", "Test@1234", "success", "✅ Registration successful"},
	            {"John", "Doe1", "existingemail@test.com", "Test@1234", "error", "❌ Email already exists"},
	            {"", "", "", "", "requiredError", "⚠ Required fields error"}
	        };
	    
	}
}
