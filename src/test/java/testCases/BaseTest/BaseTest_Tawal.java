package testCases.BaseTest;

import org.openqa.selenium.WebDriver;

import utilities.ExcelReader;

public class BaseTest_Tawal {
	public WebDriver driver;
	public static ExcelReader excel = new ExcelReader("./src/main/resources/iTower_Clients_Excel/excel_Tawal/TestDataSheetForTroubleTicket.xlsx", 0);
	
	
}
