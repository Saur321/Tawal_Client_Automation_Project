package pages.iTower_Tawal;

import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import io.qameta.allure.Allure;
import pages.AllClientsBasePages.BasePage;

public class TroubleTicketingPage extends BasePage {
	SoftAssert sa = new SoftAssert();
	String GetFistColumnValue = "";
	List<WebElement> AscendingOrder;
	List<WebElement> DescendingOrder;
	public String ticketIdText;
	public String ticketIdValue;
	public String OPCOSiteId;
	public String TicketId1;
	public String TicketId2;

	// handle List functionalities Of TroubleTicket
	private void handleListOfTroubleTicket(String locatorKey, String value) {
		try {
			List<WebElement> setOfTickedType = clickOnListOfWebelement(locatorKey);
			if (locatorKey.endsWith("_ID")) {
				setOfTickedType = wait
						.until(ExpectedConditions.presenceOfAllElementsLocatedBy((By.id(OR.getProperty(locatorKey)))));
			} else if (locatorKey.endsWith("_XPATH")) {
				setOfTickedType = wait.until(
						ExpectedConditions.presenceOfAllElementsLocatedBy((By.xpath(OR.getProperty(locatorKey)))));
			} else if (locatorKey.endsWith("_CSS")) {
				setOfTickedType = wait.until(ExpectedConditions
						.presenceOfAllElementsLocatedBy((By.cssSelector(OR.getProperty(locatorKey)))));
			}
			for (WebElement tickedType : setOfTickedType) {
				String TickedType = tickedType.getText();
				if (TickedType.equalsIgnoreCase(value)) {
					tickedType.click();
					break;
				}
			}
			log.info("Typing in " + locatorKey + " element and entered the value as " + value);
			Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
					io.qameta.allure.model.Status.PASSED);
		} catch (NoSuchElementException e) {
			log.error("Element not found: " + locatorKey);
			Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
					io.qameta.allure.model.Status.FAILED);
			e.printStackTrace();
		}

	}

	// Add new trouble ticket
	public void addNewTroubleTicket(String siteId, String ticketType, String severity, String assignedTo,
			String alarmDescription, String serviceImpact, String TicketValue)
			throws InterruptedException, TimeoutException {

		/* Ticket should be added */
		Allure.step("/* Verify Ticket is added */");
		click("addTicketButton_XPATH");
		handleIframe("iframe_ID");
		enterTextIntoInputBoxUsingActionsClass("siteID_XPATH", siteId);
		clickOnSuggestedValue();

		// select ticket type

		handleClickByJS("selectTicketType_XPATH");
		handleListOfTroubleTicket("multiselect_ddlTicketType_XPATH", ticketType);

		// choose severity
		click("selectSeverity_XPATH");
		handleListOfTroubleTicket("selectFromSeverityDropdown_XPATH", severity);

		// select Assigned To
		Allure.step("Added ticket should get assigned to correct user");
		click("selectAssignedTo_XPATH");
		enterTextIntoInputBox("selectAssigned_XPATH", assignedTo);
		clickUsingDynamicLocator("assignedTo_XPATH", assignedTo);

		// Handle alarm description
		// Alarm desc should come based on Ticket Type, Equipment and severity
		Allure.step("/* Alarm desc should come based on Ticket Type, Equipment and severity */");
		click("selectAlarmDescription_XPATH");
		enterTextIntoInputBox("selectAlarm_XPATH", alarmDescription);
		clickUsingDynamicLocator("clickOnAlarm_XPATH", alarmDescription);

		// Handle service impact
		click("service_Impact_XPATH");
		enterTextIntoInputBox("enter_Value_service_Impact_XPATH", serviceImpact);
		clickUsingDynamicLocator("choose_Value_service_Impact_XPATH", serviceImpact);
		click("service_Impact_Start_XPATH");
		int todayDayOfMonth = LocalDate.now().getDayOfMonth();
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='" + todayDayOfMonth + "']")))
				.click();
		LocalTime currentTime = LocalTime.now();

		// Format as HH:mm
		String currentTimeStr = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));
		enterTextIntoInputBox("enter_Value_starttime_XPATH", currentTimeStr);

		/* File should be added while adding */
		Allure.step("/* Files should be added while adding the ticket- Jpg, png, excel, pdf, word */");
		uploadThexlsxFile("AddTicket");
		performScrolling("addTicket_XPATH");
		click("addTicket_XPATH");
		String ErrorMessage = getText("ErrorMessageOnTicketForm_XPATH");
		if (ErrorMessage.equalsIgnoreCase("Ticket(s) already exists for this time period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Invalid Site Id or Site Id not exists in system.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Please select Ticket Type.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Please select Alarm Description.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Site Id cannot be blank.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Please select Severity.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Please select Assigned To.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Please select Service Impact.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 2 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 4 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 3 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 2, Opco 3 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 2, Opco 4 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 3, Opco 4 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		} else if (ErrorMessage.equalsIgnoreCase("Operator Opco 2, Opco 3, Opco 4 is not OnAir for this period.")) {
			Assert.assertEquals(ErrorMessage, "Trouble ticket added successfully");
		}
		Thread.sleep(1000);
		// Ticket ID Generated
		String[] splitedMessage = ErrorMessage.split(":");
		if (splitedMessage[0].equalsIgnoreCase("Trouble ticket added successfully (Ticket Id ")) {
			String TicketID = splitedMessage[1];

			if (TicketValue.equals("First")) {
				TicketId1 = TicketID.replaceAll("[^a-zA-Z0-9]", "");
			} else if (TicketValue.equals("Second")) {
				TicketId2 = TicketID.replaceAll("[^a-zA-Z0-9]", "");
			}
			// Allure.step("Trouble ticket added successfully and TicketID is : " +
			// TicketId, Status.PASSED);
			switchToDefaultContentFromIframe();
			click("closeFrame_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			click("ShowFilterOnTroubleTicketingPage_XPATH");
			Thread.sleep(1000);
			if (TicketValue.equals("First")) {
				handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId1);
			}

			else if (TicketValue.equals("Second")) {
				handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId2);
			}

			handleInputFieldByJS("siteIDInputField_XPATH", siteId);
			Thread.sleep(1000);
			handleClickByJS("filter_Report_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			List<WebElement> listOfTicketID = clickOnListOfWebelement("ticketIdOnPage_XPATH");
			for (WebElement ticketID : listOfTicketID) {
				String Ticket = ticketID.getText();

				if (Ticket.equalsIgnoreCase(TicketId1)) {
					Assert.assertEquals(Ticket, TicketId1, "Ticket added successfully");
					Allure.step("Ticket added successfully with validation : " + TicketId1);
					break;
				} else if (Ticket.equalsIgnoreCase(TicketId2)) {
					Assert.assertEquals(Ticket, TicketId2, "Ticket added successfully");
					Allure.step("Ticket added successfully with validation : " + TicketId2);
					break;
				}

			}
			click("exportTroublTckt_XPATH");
			Thread.sleep(1000);
			String renameFile = "AddTicketForAlarm " + alarmDescription.replaceAll("[^A-Za-z0-9]", "");
			renameDownloadedFile("TroubleTicketing_", renameFile);
			verifyUsingAssertFileIsExistInLocation(renameFile);
		}

	}

	// handle Trouble Ticket functionalities
	public void landingOnTroubleTicketPage() throws InterruptedException {
		explicitWaitWithClickable("insideOpen_XPATH");
		sa.assertEquals(getText("tickingManagementModule_XPATH"), "Ticketing Management");
		explicitWaitWithClickable("rptMenu_XPATH");
		explicitWaitWithClickable("troubleTickting_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

	}

	/* User should able to add/resolve/close the ticket through Bulk upload */
	// Add ticket using bulk Upload
	public void addTicketbulkUpload() throws InterruptedException, TimeoutException {
		explicitWaitWithClickable("troubleTicketBulkUpload_XPATH");
		handleIframe("iframeTroubleTicketBulkUpdate_ID");
		uploadTheCSVFile("TTBulkAdd");
		explicitWaitWithpresenceOfElementLocated("uploadButton_XPATH");
		explicitWaitWithClickable("uploadButton_XPATH");
		String actualText = getText("availableRecords_XPATH");
		assertTrue(actualText.contains("All record(s) uploaded successfully."));
		driver.switchTo().defaultContent();
		Thread.sleep(2000);
		click("closeFrame_XPATH");

	}

	// Resolved Ticket using bulk upload
	public void resolvedTicketbulkUpload() throws InterruptedException, TimeoutException {
		explicitWaitWithClickable("troubleTicketBulkUpload_XPATH");
		handleIframe("iframeTroubleTicketBulkUpdate_ID");
		uploadTheCSVFile("TTBulkResolve");
		explicitWaitWithpresenceOfElementLocated("uploadButton_XPATH");
		explicitWaitWithClickable("uploadButton_XPATH");
		String actualText = getText("availableRecords_XPATH");
		assertTrue(actualText.contains("All record(s) uploaded successfully."));
		driver.switchTo().defaultContent();
		Thread.sleep(2000);
		click("closeFrame_XPATH");

	}

	// Closed Ticket using bulk upload
	public void closedTicketbulkUpload() throws InterruptedException, TimeoutException {
		explicitWaitWithClickable("troubleTicketBulkUpload_XPATH");
		handleIframe("iframeTroubleTicketBulkUpdate_ID");
		uploadTheCSVFile("TTBulkClose");
		explicitWaitWithpresenceOfElementLocated("uploadButton_XPATH");
		explicitWaitWithClickable("uploadButton_XPATH");
		String actualText = getText("availableRecords_XPATH");
		assertTrue(actualText.contains("All record(s) uploaded successfully."));
		driver.switchTo().defaultContent();
		Thread.sleep(2000);
		click("closeFrame_XPATH");

	}

	// export add ticket file using bulk upload
	public void addTicket_ReportFilterAndExport(String status, String siteID)
			throws InterruptedException, TimeoutException {
		// handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		handleSelectTicketStatusSelected(status);
		handleInputFieldByJS("siteIDInputField_XPATH", siteID);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		ticketIdValue = getText("open_Ticket_ID_XPATH");
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		handleInputFieldByJS("TicKetIDInputField_XPATH", ticketIdValue);
		click("exportTroublTckt_XPATH");
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		// Add file renaming logic here
		renameDownloadedFile("TroubleTicketing_", "AddTicket_BulkUpload");
		verifyUsingAssertFileIsExistInLocation("AddTicket_BulkUpload");

	}

	// export resolved ticket file using bulk upload
	public void resolvedTicket_ReportFilterAndExport() throws InterruptedException, TimeoutException {
		deleteOldFilesFromLocation("ResolvedTicket_BulkUpload");
		refreshPage();
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		ticketIdValue = getText("open_Ticket_ID_XPATH");
		handleInputFieldByJS("TicKetIDInputField_XPATH", ticketIdValue);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("exportTroublTckt_XPATH");
		// Add file renaming logic here
		renameDownloadedFile("TroubleTicketing_", "ResolvedTicket_BulkUpload");
		verifyUsingAssertFileIsExistInLocation("ResolvedTicket_BulkUpload");

	}

	// export closed ticket file using bulk upload
	public void closedTicket_ReportFilterAndExport() throws InterruptedException, TimeoutException {
		deleteOldFilesFromLocation("ClosedTicket_BulkUpload");
		refreshPage();
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		handleClickByJS("ticketStatusID_XPATH");
		handleClickByJS("click_On_All_XPATH");
		Thread.sleep(1000);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		ticketIdValue = getText("open_Ticket_ID_XPATH");
		handleInputFieldByJS("TicKetIDInputField_XPATH", ticketIdValue);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("exportTroublTckt_XPATH");
		// Add file renaming logic here
		renameDownloadedFile("TroubleTicketing_", "ClosedTicket_BulkUpload");
		verifyUsingAssertFileIsExistInLocation("ClosedTicket_BulkUpload");

	}

	/* Sorting (short on columns) should be working fine */
	// get site id value in descending Order
	public void changesiteIdTodescendingOrder() {
		DescendingOrder = clickOnListOfWebelement("siteIdOnPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get ticket id value in descending Order
	public void changeticketIdTodescendingOrder() {
		DescendingOrder = clickOnListOfWebelement("ticketIdOnPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get alarm Status value in descending Order
	public void changeAlarmStatusTodescendingOrder() {
		DescendingOrder = clickOnListOfWebelement("alarmStatusOnPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get ticket Log Date value in descending Order
	public void changeTicketLogDateTodescendingOrder() {
		DescendingOrder = clickOnListOfWebelement("ticketLogDateOnPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

//	verify Descending Order Sorting functionalities On Data Grid UI
	public void verifyDescendingOrder() {
		// Verify descending order for column value
		Allure.step("Descending Order -->");
		int loopcountDecending = 0;
		for (int i = 0; i < DescendingOrder.size() - 1; i++) {
			String currentString = DescendingOrder.get(i).getText().trim();
			String nextString = DescendingOrder.get(i + 1).getText().trim();

			// Compare strings directly
			int comparison = currentString.compareToIgnoreCase(nextString);

			if (comparison > 0) {
				Allure.step("Column value in descending order: " + currentString);
			} else if (comparison == 0) {
				Allure.step("Duplicate column value in descending order: " + currentString);
			} else {
				Allure.step("Column value is not in descending order: " + currentString);
			}

			loopcountDecending++;
			if (loopcountDecending == 1) {
				break;
			}
		}
	}

	// get SiteI d value in Ascending Order
	public void changeSiteIdToAscendingOrder() throws InterruptedException {
		// Change sorting to ascending
		click("siteIdText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("siteIdOnPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get ticket Id value in Ascending Order
	public void changeTicketIdToAscendingOrder() throws InterruptedException {
		// Change sorting to ascending
		click("ticketIdText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("ticketIdOnPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get Alarm Status value in Ascending Order
	public void changeAlarmStatusToAscendingOrder() throws InterruptedException {
		// Change sorting to ascending
		click("alarmStatusText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("alarmStatusOnPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get Ticket Log Date value in Ascending Order
	public void changeTicketLogDateToAscendingOrder() throws InterruptedException {
		// Change sorting to ascending
		click("ticketLogDateText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("ticketLogDateOnPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

//	verify Descending Order Sorting functionalities On Data Grid UI
	public void verifyAscendingOrder() {
		// Verify ascending order for strings
		Allure.step("Ascending Order -->");
		int loopcountAscending = 0;
		for (int i = 0; i < AscendingOrder.size() - 1; i++) {
			String currentString = AscendingOrder.get(i).getText().trim();
			String nextString = AscendingOrder.get(i + 1).getText().trim();

			// Compare strings directly
			int comparison = currentString.compareToIgnoreCase(nextString);

			if (comparison < 0) {
				Allure.step("Column value in ascending order: " + currentString);
			} else if (comparison == 0) {
				Allure.step("Duplicate column value in ascending order: " + currentString);
			} else {
				Allure.step("Column value is not in ascending order: " + currentString);
			}

			loopcountAscending++;
			if (loopcountAscending == 1) {
				break;
			}
		}
	}

	/* Paging (number of TT in a page) should be working fine */
	public void handlePaginationOnDataGridUI()
			throws InterruptedException, FileNotFoundException, IOException, TimeoutException {

		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(5000);
		handleCalenderOnTroubleTicket(2);
		String pageCount = getText("totalPageCount_XPATH");
		Allure.step("Total Pages count : " + pageCount);
		deleteOldFilesFromLocation("TotalNumberOfDataUsingPagination");
		click("exportTroublTckt_XPATH");
		Thread.sleep(1500);
		renameDownloadedFile("TroubleTicketing_", "TotalNumberOfDataUsingPagination");
		verifyUsingAssertFileIsExistInLocation("TotalNumberOfDataUsingPagination");
		Thread.sleep(1500);
		readColumnData("Ticket ID", "TotalNumberOfDataUsingPagination");
	}

	// perform scroll Right operation
	public void scrollRight() throws InterruptedException {
		WebElement lefttoright = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"center\"]/div/div[2]/div[2]/div")));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", lefttoright);
		Thread.sleep(3000);
	}

	/* All filters should work fine in TT report */
	// Check Select Province Filter functionality
	public void handleSelectProvinceFilter() throws InterruptedException {
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		handleClickByJS("selectProvince_XPATH");
		click("centralValueSelectProvince_XPATH");
		clickUsingDynamicLocator("allButton_XPATH", String.valueOf(3));
		clickUsingDynamicLocator("noneButton_XPATH", String.valueOf(3));
		String SelectProvince = getTextFromInputBox("enterkeywordsValueSelectProvince_XPATH", "Eastern Cape-B");
		Allure.step("Verify that value equals 'Eastern'", () -> {
			Assert.assertEquals("Eastern Cape-B", SelectProvince);
		});
		click("EasternValueSelectProvince_XPATH");
		driver.findElement(By.xpath("//input[@placeholder='Enter keywords']")).clear();
		Thread.sleep(1000);
		clickUsingDynamicLocator("noneButton_XPATH", String.valueOf(3));
		List<WebElement> selectProvince = clickOnListOfWebelement("multiSelectRegionActivityReport_XPATH");

		for (int i = 0; i <= selectProvince.size() - 1; i++) {
			if (selectProvince.size() > 1) {
				selectProvince.get(selectProvince.size() - 1).click();
				break;
			}
		}
		click("multiSelectcloseSelectProvince_XPATH");
		Thread.sleep(2000);
	}

	public void validateTroubleTicketFiltersFunctionality(int startIndex, int endIndex) {
		int index = startIndex;
		if (index <= endIndex) {

		}
	}

	int index = 2;

	// verify filters dropdown using indexing
	public void HandleDropdowns(int endIndex) throws InterruptedException {
		Thread.sleep(5000);
		if (index <= endIndex) {
			WebElement EnterKeywordValue = driver
					.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]"));

			String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index);
			dynamicLocatorClick("allInputBox_XPATH", index);

			Thread.sleep(2000);
			int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
			if (count >= 1) {
				List<WebElement> drpDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
				String DropDownValue = drpDownList.get(0).getText();
				dynamicLocatorClick("clickOnFirstCheckBox_XPATH", index);
				dynamicLocatorClick("allButton_XPATH", index);
				dynamicLocatorClick("noneButton_XPATH", index);
				dynamicLocatorSendKeys("enterKeywordValue_XPATH", index, DropDownValue);
				EnterKeywordValue.clear();
				dynamicLocatorClick("noneButton_XPATH", index);
				handleDropDownList(drpDownList, DropDownValue);
			}
			dynamicLocatorClick("multiSelectClose_XPATH", index);
			Thread.sleep(3000);
			index++;
			HandleDropdowns(9);
		}
	}

	// Dynamic method to enter text with dynamic xpath including index
	public void dynamicLocatorSendKeys(String locatorKey, int index, String value) {
		String locatorTemplate = OR.getProperty(locatorKey);
		String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
		driver.findElement(By.xpath(dynamicXpath)).sendKeys(value);
	}

	public int getDropDownListCount(String locatorKey, String InputBoxName) {
		String locatorTemplate = OR.getProperty(locatorKey);
		String dynamicXpath = locatorTemplate.replace("{InputBoxName}", String.valueOf(InputBoxName));
		List<WebElement> allDropDownList = driver.findElements(By.xpath(dynamicXpath));
		return allDropDownList.size();
	}

	public int getDropDownListCount(String locatorKey) {
		String locatorTemplate = OR.getProperty(locatorKey);
		List<WebElement> allDropDownList = driver.findElements(By.xpath(locatorTemplate));
		return allDropDownList.size();
	}

	public List<WebElement> getDropDownList(String locatorKey, String inputBoxName) {
		String locatorTemplate = OR.getProperty(locatorKey);
		String dynamicXpath = locatorTemplate.replace("{InputBoxName}", String.valueOf(inputBoxName));
		List<WebElement> allDropDownList = driver.findElements(By.xpath(dynamicXpath));
		return allDropDownList;
	}

	public List<WebElement> getDropDownList(String locatorKey) {
		String locatorTemplate = OR.getProperty(locatorKey);
		List<WebElement> allDropDownList = driver.findElements(By.xpath(locatorTemplate));
		return allDropDownList;
	}

	public void handleDropDownList(List<WebElement> drpDownList, String dropDownValue) {
		for (WebElement dropDown : drpDownList) {
			String DropDown = dropDown.getText();
			if (DropDown.equalsIgnoreCase(dropDownValue)) {
				dropDown.click();
				break;
			}
		}
	}

	// method to handle RCA Category dropdown filters
	public void handleSelectRCACategory() throws InterruptedException {
		click("rcaCategoryHandle_XPATH");
		List<WebElement> ListOfRCACategory = getDropDownList("listOfRCACategory_XPATH");
		int NumberOfRCACategory = getDropDownListCount("listOfRCACategory_XPATH");
		if (NumberOfRCACategory >= 1) {
			String RCACategory = ListOfRCACategory.get(ListOfRCACategory.size() - 1).getText();
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 10, RCACategory);
			clearTextFromInputBox("enterKeywordValue_XPATH", 10);
			dynamicLocatorClick("rcaDropdownOptionClick_XPATH", RCACategory);
		}
	}

	// method to handle select RCA sub category filters
	public void selectSelectRCASubCategory() throws InterruptedException {
		// showHideFilterScroll("scrollRight_XPATH");
		String InputBoxName = getText("allInputBoxRCASub_XPATH");
		click("allInputBoxRCASub_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 11);
			Thread.sleep(2000);
			dynamicLocatorClick("allButton_XPATH", 10);
			dynamicLocatorClick("noneButton_XPATH", 10);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 11, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 11);
			dynamicLocatorClick("noneButton_XPATH", 10);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 11);
		}
	}

	// method to handle select service impact dropdown filters
	public void SelectServiceImpact() throws InterruptedException {
		String InputBoxName = getText("allInputBoxSelectService_XPATH");
		click("allInputBoxSelectService_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 12);
			dynamicLocatorClick("allButton_XPATH", 11);
			dynamicLocatorClick("noneButton_XPATH", 11);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 12, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 12);
			dynamicLocatorClick("noneButton_XPATH", 11);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 12);
		}
	}

	// method to handle select delay reason dropdown filters
	public void SelectDelayReason() throws InterruptedException {
		click("SelectDelayReason_XPATH");
		List<WebElement> listofSelectDelayReason = getDropDownList("listOfSelectDelayReason_XPATH");
		int countofDelayReason = getDropDownListCount("listOfSelectDelayReason_XPATH");
		if (countofDelayReason >= 1) {
			String delayReason = listofSelectDelayReason.get(1).getText();
			Thread.sleep(2000);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 13, delayReason);
			clickAnElementInList(listofSelectDelayReason, 1);
		}
	}

	// method to handle select ticket Hierarchy dropdowns filter
	public void selectTicketHierarchy() throws InterruptedException {
		click("selectTicketHierarchy_XPATH");
		List<WebElement> listofTicketHierarchy = getDropDownList("listOfSelectTicketHierarchy_XPATH");
		int countOfTicketHierarchy = getDropDownListCount("listOfSelectTicketHierarchy_XPATH");
		if (countOfTicketHierarchy >= 1) {
			handleDropDownList(listofTicketHierarchy, "Parent");
		}
	}

	// select service type dropdown handle
	public void SelectServiceType() throws InterruptedException {
		String InputBoxName = getText("allInputBoxSelectServiceType_XPATH");// allInputBox.getText();
		click("allInputBoxSelectServiceType_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 16);
			dynamicLocatorClick("allButton_XPATH", 12);
			dynamicLocatorClick("noneButton_XPATH", 12);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 16, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 16);
			dynamicLocatorClick("noneButton_XPATH", 12);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 16);
		}
	}

	// select vendor dropdown handle
	public void selectVendor() throws InterruptedException {
		String InputBoxName = getText("selectVendor_XPATH");
		click("selectVendor_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 17);
			dynamicLocatorClick("allButton_XPATH", 13);
			dynamicLocatorClick("noneButton_XPATH", 13);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 17, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 17);
			dynamicLocatorClick("noneButton_XPATH", 13);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 17);
		}
	}

	// verify select Operator SLA
	public void selectOperatorSLA() throws InterruptedException {
		click("selectOperatorSLA_XPATH");
		click("selectOperatorSLAOption_XPATH");
	}

	// verify select SLA
	public void selectSLA() throws InterruptedException {
		click("selectSLA_XPATH");
		click("selectSLAOption_XPATH");
	}

	// verify select Operator
	public void selectOperator() throws InterruptedException {
		String InputBoxName = getText("selectOperator_XPATH");
		click("selectOperator_XPATH");

		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 21);
			dynamicLocatorClick("allButton_XPATH", 17);
			dynamicLocatorClick("noneButton_XPATH", 17);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 21, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 21);
			dynamicLocatorClick("noneButton_XPATH", 17);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 21);
		}
	}

	// verify created Date dropdown value
	public void createdDate(String dropdownvalue) {
		click("createdDate_XPATH");
		handleCreatedDate(dropdownvalue);
	}

	// verify handle Created Date dropdown value
	public void handleCreatedDate(String dropdownValue) {
		List<WebElement> listofCreatedDate = driver.findElements(By.xpath(OR.getProperty("listOfCreatedDate_XPATH")));
		if (listofCreatedDate.size() >= 1) {
			for (WebElement CreatedDate : listofCreatedDate) {
				String TypeOfCreatedDate = CreatedDate.getText();
				if (TypeOfCreatedDate.equalsIgnoreCase(dropdownValue)) {
					CreatedDate.click();
				}
			}
		}
	}

	// verify click On Day functionalities for calender
	public void clickOnDay(int day) throws InterruptedException {

		click("clickOnDay_ID");
		click("previousButton_CLASS");
		Thread.sleep(3000);
		List<WebElement> days = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.cssSelector(OR.getProperty("calendarDatesList_CSS"))));

		try {
			for (WebElement dayElement : days) {
				if (dayElement.getText().equals(String.valueOf(day))) {
					dayElement.click();
					click("timeSlotSelection_XPATH");
					break;
				}
			}
		} catch (StaleElementReferenceException e) {
			throw e;
		}
	}

	// verify select User functionalities
	public void selectUser() throws InterruptedException {
		String InputBoxName = getText("selectUser_XPATH");
		handleClickByJS("selectUser_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 25);
			dynamicLocatorClick("allButton_XPATH", 18);
			dynamicLocatorClick("noneButton_XPATH", 18);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 23, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 23);
			dynamicLocatorClick("noneButton_XPATH", 18);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 23);
		}
	}

	public void handleTicketId() {
		enterTextIntoInputBox("ticketId_XPATH", "1234");
	}

	public void handleSiteID() {
		enterTextIntoInputBox("siteId_XPATH", "1234asd");
	}

	public void HandleSelectRMS() throws InterruptedException {
		String InputBoxName = getText("selectRMS_XPATH");
		click("selectRMS_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 29);
			dynamicLocatorClick("allButton_XPATH", 19);
			dynamicLocatorClick("noneButton_XPATH", 19);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 24, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 24);
			dynamicLocatorClick("noneButton_XPATH", 19);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 24);
		}
	}

	// method to handle select site class code
	public void HandleSelectSiteClassCode() throws InterruptedException {
		String InputBoxName = getText("selectSiteClassCode_XPATH");
		click("selectSiteClassCode_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 31);
			dynamicLocatorClick("allButton_XPATH", 21);
			dynamicLocatorClick("noneButton_XPATH", 21);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 26, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 26);
			dynamicLocatorClick("noneButton_XPATH", 21);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 26);
		}
	}

	// select Ticket Treatment dropdown option
	public void selectTicketTreatment() throws InterruptedException {
		String InputBoxName = getText("selectTicketTreatment_XPATH");
		click("selectTicketTreatment_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 32);
			dynamicLocatorClick("allButton_XPATH", 22);
			dynamicLocatorClick("noneButton_XPATH", 22);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 27, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 27);
			dynamicLocatorClick("noneButton_XPATH", 22);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 27);
		}
	}

	// method for select service level
	public void selectServiceLevel() {
		click("selectServiceLevelOption_XPATH");
		click("selectOptionClose_XPATH");
	}

	public void SelectEquipment() throws InterruptedException {
		String InputBoxName = getText("selectEquipmentsOptions_XPATH");
		click("selectEquipmentsOptions_XPATH");
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		int count = getDropDownListCount("allDropDownList_XPATH", InputBoxName);
		if (count >= 1) {
			String DropDownValue = allDropDownList.get(0).getText();

			dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 34);
			dynamicLocatorClick("allButton_XPATH", 23);
			dynamicLocatorClick("noneButton_XPATH", 23);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 29, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 29);
			dynamicLocatorClick("noneButton_XPATH", 23);
			handleDropDownList(allDropDownList, DropDownValue);
			dynamicLocatorClick("multiSelectClose_XPATH", 29);
		}
	}

	// verify Select Save Filter functionalities
	public void SelectSaveFilter() {
		String InputBoxName = getText("selectTicketTreatment_XPATH");
		driver.findElement(By.xpath("//span[text()='Select Save Filter']")).click();
		List<WebElement> allDropDownList = getDropDownList("allDropDownList_XPATH", InputBoxName);
		String DropDownValue = allDropDownList.get(0).getText();
		dynamicLocatorClick("clickOnFirstCheckBox_XPATH", 32);
		click("selectSaveFilter_XPATH");
	}

	/* Report should be exported with valid data. */
	// Handle input DropDown field for data validation
	public void dataFilterBasedOnInputDropDownField(int index, String DropDownValue, String RenameFileName)
			throws InterruptedException, TimeoutException {
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		Thread.sleep(5000);
		dynamicLocatorClick("allInputBox_XPATH", index);
		Thread.sleep(2000);
		if (index == 7) {
			dynamicLocatorClick("noneButton_XPATH", index);
		}
		driver.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]")).sendKeys(DropDownValue);
		dynamicLocatorClick("allButton_XPATH", index);
		dynamicLocatorClick("multiSelectClose_XPATH", index);
		handleCalenderOnTroubleTicketOnBasisOfMonth(3);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);
	}

	// Handle input DropDown field for data validation for RCA Category
	public void dataFilterBasedOnSelectedRCA(String DropDownValue, String RenameFileName)
			throws InterruptedException, TimeoutException {
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		WebElement slider = driver.findElement(By.xpath("//*[@id='nw_listFilterItems']/li[10]/button"));
		Actions act = new Actions(driver);
		act.clickAndHold(slider).moveByOffset(50, 0).release().build().perform();
		handleClickByJS("rcaCategoryHandle_XPATH");
		driver.findElement(By.xpath("//*[@id='nw_listFilterItems']/li[10]/div/div/div/input")).sendKeys(DropDownValue);
		driver.findElement(By.xpath("//span[text()='" + DropDownValue + "']")).click();
		handleCalenderOnTroubleTicketOnBasisOfMonth(5);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);

	}

	// Handle input field for data validation for Sub RCA Category
	public void dataFilterBasedOnSelectedSubRCACategory(int index, String DropDownValue, String RenameFileName)
			throws InterruptedException, TimeoutException {
		Actions actions = new Actions(driver);
		actions.sendKeys(Keys.ESCAPE).perform();
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		click("rcaSubCategoryHandle_XPATH");
		enterTextIntoInputBox("enterKeywordValueOnIndex_XPATH", "DropDownValue");
		dynamicLocatorClick("allButton_XPATH", index);
		click("multiSelectCloseonIndex_XPATH");
		handleCalenderOnTroubleTicketOnBasisOfMonth(5);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);
	}

	// Handle input field for data validation on Site ID
	public void dataFilterBasedOnSiteID(String SiteID, String RenameFileName)
			throws InterruptedException, TimeoutException {
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		handleInputFieldByJS("siteId_XPATH", SiteID);
		handleCalenderOnTroubleTicketOnBasisOfMonth(5);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);
	}

	// Handle input field for data validation on Ticket ID
	public void dataFilterBasedOnTickedID(String TickedTD, String RenameFileName)
			throws InterruptedException, TimeoutException {
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		handleClickByJS("ticketStatusID_XPATH");
		handleClickByJS("click_On_All_XPATH");
		clickEscape();
		handleInputFieldByJS("ticketId_XPATH", TickedTD);
		handleCalenderOnTroubleTicketOnBasisOfMonth(5);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);

	}

	// Handle input field for data validation on Create Date
	public void dataFilterBasedOnSelectedCreateDate(String dropdownvalue, String RenameFileName)
			throws InterruptedException, TimeoutException {
		click("createdDate_XPATH");
		handleCreatedDate(dropdownvalue);
		handleCalenderOnTroubleTicketOnBasisOfMonth(5);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", RenameFileName);

	}

	/* verify Distance in kms value with User Lat and User Long column */
	// verify handle User Lat And User long
	public void handleUserLatAndUserlong(String RenameFileName) throws InterruptedException, TimeoutException {
		deleteOldFilesFromLocation(RenameFileName);
		click("exportTroublTckt_XPATH");
		Thread.sleep(3000);
		renameDownloadedFile("TroubleTicketing_", RenameFileName);
		Thread.sleep(1000);
		readDataForDistanceMeasure(RenameFileName);
	}

// verify read Data For Distance in kms
	public void readDataForDistanceMeasure(String RenameFileName) {
		String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		File downloaddirectory = new File(userDir);
		String downloadDirectory = downloaddirectory + config.getProperty("downloadFileLocation");
		File directory = new File(downloadDirectory);
		File[] files = directory.listFiles((dir, name) -> name.startsWith(RenameFileName));
		String filePath = files[0].getAbsolutePath();

		String line = "";
		String csvSplitBy = ",";
		boolean skipFirstRow = true;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			// Read and skip the first data row
			br.readLine();
			// Read the header row to identify column indices
			String headerLine = br.readLine();
			if (headerLine != null) {
				String[] headers = headerLine.split(csvSplitBy);
				int userLatIndex = -1;
				int userLongIndex = -1;
				int distanceIndex = -1;
				int ticketIdIndex = -1;

				// Find the indices of the desired columns
				for (int i = 0; i < headers.length; i++) {
					if (headers[i].trim().equalsIgnoreCase("User Lat")) {
						userLatIndex = i;
					} else if (headers[i].trim().equalsIgnoreCase("User Long")) {
						userLongIndex = i;
					} else if (headers[i].trim().equalsIgnoreCase("Distance (Kms.)")) {
						distanceIndex = i;
					} else if (headers[i].trim().equalsIgnoreCase("Ticket ID")) {
						ticketIdIndex = i;
					}
				}

				// Check if all required columns were found
				if (userLatIndex != -1 && userLongIndex != -1 && distanceIndex != -1 && ticketIdIndex != -1) {
					Allure.step("Data from CSV with Ticket ID and Location:");
					while ((line = br.readLine()) != null) {
						if (skipFirstRow) {
							skipFirstRow = false;
							continue;
						}
						// Check if the line is blank
						if (line.trim().isEmpty()) {
							continue;
						}

						// Split the line by the delimiter
						String[] data = line.split(csvSplitBy);

						String ticketId = (data.length > ticketIdIndex)
								? data[ticketIdIndex].trim().replace("\"", "").trim()
								: "";
						String userLatStr = (data.length > userLatIndex)
								? data[userLatIndex].trim().replace("\"", "").trim()
								: "";
						String userLongStr = (data.length > userLongIndex)
								? data[userLongIndex].trim().replace("\"", "").trim()
								: "";
						String distanceStr = (data.length > distanceIndex)
								? data[distanceIndex].trim().replace("\"", "").trim()
								: "";

						// Print the Ticket ID along with User Lat, User Long, and Distance
						if (!ticketId.isEmpty() && !userLatStr.isEmpty() && !userLongStr.isEmpty()
								&& !distanceStr.isEmpty()) {
						}

						Double userLat = null;
						Double userLong = null;
						Double distance = null;
						if (!userLatStr.isEmpty() && !userLongStr.isEmpty() && !distanceStr.isEmpty()) {
							try {
								userLat = Double.parseDouble(userLatStr);
							} catch (NumberFormatException e) {
								// Not a numeric value for User Lat
							}
							try {
								userLong = Double.parseDouble(userLongStr);
							} catch (NumberFormatException e) {
								// Not a numeric value for User Long
							}
							try {
								distance = Double.parseDouble(distanceStr);
							} catch (NumberFormatException e) {
								// Not a numeric value for Distance
							}

							if (userLat != null && userLong != null && distance != null) {
								Allure.step("Ticket ID is " + ticketId + ", User Lat: " + userLat + ", User Long: "
										+ userLong + ", Distance (Kms.): " + distance);
							}
						}

					}
				} else {
					Allure.step(
							"One or more of the required columns ('User Lat', 'User Long', 'Distance (Kms.)', 'Ticket ID') not found in the CSV header.");
				}
			} else {
				Allure.step("CSV file is empty or header row is missing.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Send RIGHT arrow key multiple times
	public void scrollTillDistanceHeaderOfDataGrid() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		for (int i = 0; i < 100; i++) {
			js.executeScript("document.querySelector('.ag-body-viewport').scrollLeft += 40;");
			try {
				Thread.sleep(100); // slight pause to allow smooth scrolling (adjust if needed)
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // handle the exception properly
			}
		}
	}

	/* verify UI records with downloaded CSV file data */
	// create AgGrid Table File
	public void createAgGridTableFile() throws InterruptedException, FileNotFoundException, IOException {
		// Wait for grid to load
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Fetch AG-Grid rows
		List<WebElement> rows = clickOnListOfWebelement("fetchAGGridrows_CSS");
		// Define the output file path
		String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String filePath = userDir + "\\src\\main\\resources\\downloadExcel\\AGGridData.csv";
		try (FileWriter writer = new FileWriter(filePath)) {
			// Iterate through each row in the AG-Grid
			for (WebElement row : rows) {
				List<WebElement> cells = row.findElements(By.cssSelector(".ag-cell"));
				// Iterate through each cell in the row
				for (int i = 0; i < cells.size(); i++) {
					writer.append(cells.get(i).getText());
					// Add a comma as a delimiter, except for the last cell
					if (i < cells.size() - 1) {
						writer.append(",");
					}
				}
				// Add a new line character after each row
				writer.append("\n");
			}
			Allure.step("CSV file saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// download File And Rename for AgGrid Comparison File
	public String downloadFileAndRename() throws TimeoutException {
		deleteOldFilesFromLocation("agGridComparisonFile");
		click("exportTroublTckt_XPATH");
		String renamedFile = renameDownloadedFileAndReturnValue("TroubleTicketing", "agGridComparisonFile");
		return renamedFile;
	}

	// verify All Records From Second File Exist In Main File
	public void verifyAllRecordsFromSecondFileExistInMainFile(String mainFilePath)
			throws IOException, TimeoutException {
		String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String filePath = userDir + "\\src\\main\\resources\\downloadExcel\\AGGridData.csv";
		Allure.step("Below are the details fetch for Ticked Id");
		// Read all records from both files
		List<String[]> mainFileRecords = readCSVRecords(mainFilePath, 2); // Skip first 2 rows in main file
		List<String[]> secondFileRecords = readCSVRecords(filePath, 0); // Don't skip any rows in second file
		int i = -1;
		// Verify each record from second file exists in main file through Ticket Id
		// column
		for (String[] secondFileRecord : secondFileRecords) {
			i++;
			boolean recordExists = false;
			String secondFileTicketId = secondFileRecord[0]; // Assuming Ticket ID is first column

			for (String[] mainFileRecord : mainFileRecords) {
				String mainFileTicketId = mainFileRecord[0];

				// Check if ticket IDs match
				if (mainFileTicketId.equals(secondFileTicketId)) {
					recordExists = true;
					Allure.step(i + ": TicketId value from downloaded file: " + mainFileTicketId + " V/S " + i
							+ ": TicketId value from UI : " + secondFileTicketId);
					break;
				}
			}
		}

		// Verify each record from second file exists in main file through site Id
		// column
		Allure.step("Below are the details fetch for Site ID");
		int j = -1;
		for (String[] secondFileRecord : secondFileRecords) {
			j++;
			boolean recordExists = false;
			String secondFileSiteId = secondFileRecord[1]; // Assuming site Id is second column

			for (String[] mainFileRecord : mainFileRecords) {
				String mainFileSiteId = mainFileRecord[1];

				// Check if site IDs match
				if (mainFileSiteId.equals(secondFileSiteId)) {
					recordExists = true;
					Allure.step(j + ": SiteID value from downloaded file: " + mainFileSiteId + " V/S " + j
							+ ": SiteID value from UI : " + secondFileSiteId);
					break;
				}
			}
		}

		// Verify each record from second file exists in main file through site Name
		// column
		Allure.step("Below are the details fetch for Site Name");
		int y = -1;
		for (String[] secondFileRecord : secondFileRecords) {
			y++;
			boolean recordExists = false;
			String secondFileSiteName = secondFileRecord[2]; // Assuming site Name is third column

			for (String[] mainFileRecord : mainFileRecords) {
				String mainFileSiteName = mainFileRecord[2];

				// Check if site Name match
				if (mainFileSiteName.equals(secondFileSiteName)) {
					recordExists = true;
					Allure.step(y + ": Site Name value from downloaded file: " + mainFileSiteName + " V/S " + y
							+ ": Site Name value from UI : " + secondFileSiteName);
					break;
				}
			}
		}

	}

	// read CSV Records
	public static List<String[]> readCSVRecords(String filePath, int rowsToSkip) throws IOException {
		List<String[]> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			int linesSkipped = 0;

			while ((line = br.readLine()) != null) {
				if (linesSkipped < rowsToSkip) {
					linesSkipped++;
					continue; // Skip configured number of rows
				}
				String[] values = parseCSVLine(line);
				records.add(values);
			}
		}
		return records;
	}

	// parse CSV Line
	private static String[] parseCSVLine(String line) {
		List<String> values = new ArrayList<>();
		boolean inQuotes = false;
		StringBuilder currentValue = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (c == '"') {
				inQuotes = !inQuotes;
			} else if (c == ',' && !inQuotes) {
				values.add(currentValue.toString().trim());
				currentValue = new StringBuilder();
			} else {
				currentValue.append(c);
			}
		}
		values.add(currentValue.toString().trim()); // Add last field
		return values.toArray(new String[0]);
	}

	// add Asset Details For Resolved Ticket
	public void addAssetDetailsForResolvedTicket(String alarmDescription, String siteId, String selectAssetType,
			String selctQrCodeAssetId, String selectActionRequired, String assetId, String qrCode, String model,
			String serialNumber, String capacity, String remarks, String warrantyDate, String manufacturer)
			throws InterruptedException {
		click("filterReport_XPATH");
		Thread.sleep(1000);
		handleClickByJS("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		clickEscape();
		handleClickByJS("click_On_Alarm_Description_XPATH");
		handleInputFieldByJS("select_Alarm_Description_XPATH", alarmDescription);
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[contains(text(),'" + alarmDescription + "')]")))
				.click();
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		click("filterReport_XPATH");
		Thread.sleep(3000);
		click("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleClickByJS("cilck_On_Asset_Details_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("select_Asset_Type_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + selectAssetType + "'])[1]")))
				.click();
		click("select_Qr_Code_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + selctQrCodeAssetId + "'])[1]")))
				.click();
		click("select_Action_Required_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + selectActionRequired + "'])[1]")))
				.click();
		Thread.sleep(1000);
		handleInputFieldByJS("asset_Id_On_Asset_Details_XPATH", assetId);
		handleInputFieldByJS("New_QrCode_On_Asset_Details_XPATH", qrCode);
		handleInputFieldByJS("model_On_Asset_Details_XPATH", model);
		handleInputFieldByJS("serial_Number_On_Asset_Details_XPATH", serialNumber);
		handleInputFieldByJS("capacity_On_Asset_Details_XPATH", capacity);
		handleInputFieldByJS("remarks_On_Asset_Details_XPATH", remarks);
		handleInputFieldByJS("warranty_Date_On_Asset_Details_XPATH", warrantyDate);
		handleInputFieldByJS("manufacturer_On_Asset_Details_XPATH", manufacturer);
		click("click_AddAssetButton_XPATH");
		click("click_SaveAssetButton_XPATH");
		Thread.sleep(2000);
	}

	// check movement can be initiated
	public void checkMovementInitiation(String toLocationSiteId, String reasonCategory, String reasonSubCategory,
			String movementDate, String replaceMoveDate) throws InterruptedException {
		Thread.sleep(3000);
		handleClickByJS("asset_Details_Checkbox_XPATH");
		handleInputFieldByJS("to_Location_XPATH", toLocationSiteId);
		click("select_Reason_Category_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + reasonCategory + "'])[1]"))).click();
		click("select_Reason_Sub_Category_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + reasonSubCategory + "'])[1]")))
				.click();
		click("movement_Date_XPATH");
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//a[normalize-space()='" + movementDate + "'])[1]")))
				.click();
		click("replace_move_date_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//a[normalize-space()='" + replaceMoveDate + "'])[1]"))).click();
		Thread.sleep(1000);
		performScrolling("submit_XPATH");
		click("submit_XPATH");
		// click("submit_And_Replace_XPATH");
		Thread.sleep(1000);
		String actualText = getText("verify_Movement_Initiation_XPATH");
		assertTrue(actualText.contains("Asset Movement has been successfully created"));
	}

	// Land On Trouble Ticket Module Using JS
	public void landingOnTroubleTicketPageUsingJS() throws InterruptedException {
		Thread.sleep(1000);
		handleClickByJS("tickingManagementModule_XPATH");
		Thread.sleep(1000);
		handleClickByJS("troubleTickting_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

	}

	// Asset added in parent ticket should be visible in child ticket
	public void verifyAssetDetailsAndMovementInitiationUsingChildTicket(String alarmDescription, String siteId,
			String toLocationSiteId, String reasonCategory, String reasonSubCategory, String movementDate,
			String replaceMoveDate) throws InterruptedException, TimeoutException {
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		click("ticketStatusID_XPATH");
		click("click_On_None_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//span[normalize-space()='Resolved']/preceding-sibling::input")))
				.click();
		clickEscape();
		Thread.sleep(1000);
		handleClickByJS("click_On_Alarm_Description_XPATH");
		handleInputFieldByJS("select_Alarm_Description_XPATH", alarmDescription);
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[contains(text(),'" + alarmDescription + "')]")))
				.click();
		clickEscape();
		Thread.sleep(1000);
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		click("filterReport_XPATH");
		Thread.sleep(2000);
		click("exportTroublTckt_XPATH");
		renameDownloadedFile("TroubleTicketing_", "ReferenceTicketFile");
		verifyUsingAssertFileIsExistInLocation("ReferenceTicketFile");
		Thread.sleep(1000);
		click("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleClickByJS("cilck_On_Asset_Details_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(1000);
		Assert.assertEquals(getText("verify_Asset_Details_XPATH"), "Asset Detail:");

		// Check movement can be initiated using child ticket
		Thread.sleep(3000);
		handleClickByJS("asset_Details_Checkbox_XPATH");
		handleInputFieldByJS("to_Location_XPATH", toLocationSiteId);
		click("select_Reason_Category_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + reasonCategory + "'])[1]"))).click();
		click("select_Reason_Sub_Category_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//ul//li//label//span[text()='" + reasonSubCategory + "'])[1]")))
				.click();
		click("movement_Date_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//a[contains(@class,'highlight')= '" + movementDate + "'])[2]")))
				.click();
		click("replace_move_date_XPATH");
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//a[contains(@class,'highlight')= '\" + movementDate + \"'])[2]")))
				.click();
		Thread.sleep(1000);
		performScrolling("submit_XPATH");
		click("submit_XPATH");
		Thread.sleep(1000);
		String actualText = getText("verify_Movement_Initiation_XPATH");
		assertTrue(actualText.contains("Asset Movement has been successfully created"));
	}

	/* TT closure group or Creater user should able to close the ticket */
	public void handleClosedTicket(String alarmDescription, String siteId, String rcaCategory, String rcaSubCategory,
			String rcaReason, String TicketValue) throws InterruptedException {

		Thread.sleep(1000);
		handleClickByJS("click_On_Alarm_Description_XPATH");
		enterTextIntoInputBoxUsingActionsClass("alarmDescriptionn_XPATH", alarmDescription);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[text()='" + alarmDescription + "']"))).click();
		clickEscape();
		if (TicketValue.equals("First")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId1);
		}

		else if (TicketValue.equals("Second")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId2);
		}
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		Thread.sleep(1000);
		handleClickByJS("ticketStatusID_XPATH");
		handleClickByJS("click_On_All_XPATH");
		clickEscape();
		Thread.sleep(2000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(9000);
		click("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		String ticketStatus = getText("ticket_Status_XPATH");
		if (!(ticketStatus.equals("Closed"))) {

			Allure.step(
					"/* While closing the Ticket user can update Problem start date, end date, RCA details, Remarks */");
			click("ticket_Status_XPATH");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Closed']"))).click();
			click("problem_End_Date_XPATH");
			int todayDayOfMonth = LocalDate.now().getDayOfMonth();
			wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//a[normalize-space()='" + todayDayOfMonth + "']"))).click();
			String time = getDomProperty("problem_Start_Time_XPATH", "value");
			String originalTimeStr = time.substring(time.lastIndexOf(" ") + 1).trim();
			LocalTime originalTime = LocalTime.parse(originalTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
			LocalTime endTime = originalTime.plusMinutes(1);
			String incrementedTimeStr = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
			handleInputFieldByJS("problem_End_Time_XPATH", incrementedTimeStr);

			/* File should be added while closing the ticket */
			Allure.step("/* Files should be added while closing the ticket- Jpg, png, excel, pdf, word */");
			uploadThepngFile("AddTicket");
			handleInputFieldByJS("remarks_On_Close_Ticket_XPATH", "Closed");
			performScrolling("click_On_Update_Ticket_XPATH");
			click("click_On_Update_Ticket_XPATH");
			Thread.sleep(2000);
			String actualText = getText("verify_Ticket_Closed_XPATH");
			Assert.assertTrue(actualText.contains("Trouble ticket closed successfully"));
			Thread.sleep(1000);
			Allure.step("/* No action can be done on ticket once ticket is closed */");
			handleInputFieldByJS("enterValue_HubSiteId_XPATH", "txtHubSite");
			click("click_On_Update_Ticket_XPATH");
			String ErrorText = getText("ErrorMessageOnTicketForm_XPATH");
			Assert.assertTrue(ErrorText.contains("Remarks cannot be blank"));
		}
	}

	/* After Ticket is closed Reference (Child) ticket can be generated */
	public void generateReferenceTicket(String siteId, String rcaCategory, String rcaSubCategory, String rcaReason)
			throws InterruptedException {
		click("generate_Reference_Ticket_Link_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleIframe("iframeAddReference_CSS");
		getDomProperty("site_ID", "value");
		performScrolling("addTicketBut_XPATH");
		Thread.sleep(1000);
		click("addTicketBut_XPATH");
		Thread.sleep(2000);
		String actualText1 = getText("verify_Child_Ticket_XPATH");
		assertTrue(actualText1.contains("Trouble ticket added successfully"));
		Thread.sleep(1000);
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");
		/*
		 * Thread.sleep(1000); click("ShowFilterOnTroubleTicketingPage_XPATH");
		 * handleClickByJS("ticketStatusID_XPATH");
		 * handleClickByJS("click_On_None_XPATH");
		 * clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Closed");
		 * handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		 * click("filterReport_XPATH"); Thread.sleep(1000);
		 * click("open_Ticket_ID_XPATH"); handleIframe("iframe_ID"); Thread.sleep(1000);
		 * explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		 * Allure.
		 * step("All child ticket should be visible in separate tab under update TT form"
		 * ); handleClickByJS("click_ChildTicketTab_XPATH"); Thread.sleep(1000);
		 * click("download_ListedChildTicket_XPATH"); Allure.
		 * step("In Reference/Corrective ticket added asset should be visible which were added in parent ticket"
		 * ); handleClickByJS("cilck_On_Asset_Details_XPATH");
		 * explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		 * switchToDefaultContentFromIframe(); handleClickByJS("closeFrame_XPATH");
		 * Thread.sleep(1000); //Closed child ticket
		 * click("ShowFilterOnTroubleTicketingPage_XPATH");
		 * handleClickByJS("ticketStatusID_XPATH");
		 * handleClickByJS("click_On_None_XPATH");
		 * clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Assign");
		 * handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		 * click("filterReport_XPATH"); Thread.sleep(1000);
		 * click("open_Ticket_ID_XPATH"); handleIframe("iframe_ID");
		 * click("closedButton_XPATH"); String ticketStatus =
		 * getText("get_Close_Status_XPATH"); if (!(ticketStatus.equals("Closed"))) {
		 * click("ticket_Status_CSS");
		 * wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
		 * "//span[normalize-space()='Closed']"))).click();
		 * click("problem_End_Date_XPATH"); int todayDayOfMonth =
		 * LocalDate.now().getDayOfMonth(); wait.until(ExpectedConditions
		 * .elementToBeClickable(By.xpath("//a[normalize-space()='" + todayDayOfMonth +
		 * "']"))).click(); String time = getDomProperty("problem_Start_Time_XPATH",
		 * "value"); String originalTimeStr = time.substring(time.lastIndexOf(" ") +
		 * 1).trim(); LocalTime originalTime = LocalTime.parse(originalTimeStr,
		 * DateTimeFormatter.ofPattern("HH:mm")); LocalTime endTime =
		 * originalTime.plusMinutes(1); String incrementedTimeStr =
		 * endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
		 * handleInputFieldByJS("problem_End_Time_XPATH", incrementedTimeStr);
		 * handleClickByJS("rca_Category_XPATH"); wait.until(ExpectedConditions
		 * .elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaCategory +
		 * "']"))).click(); click("rca_Sub_Category_XPATH");
		 * wait.until(ExpectedConditions
		 * .elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaSubCategory
		 * + "']"))).click(); Thread.sleep(1000);
		 * handleInputFieldByJS("rca_Reason_XPATH", rcaReason);
		 * handleInputFieldByJS("remarks_On_Close_Ticket_XPATH", "Closed");
		 * performScrolling("click_On_Update_Ticket_XPATH");
		 * click("click_On_Update_Ticket_XPATH"); Thread.sleep(2000); String actualText
		 * = getText("verify_Ticket_Closed_XPATH");
		 * Assert.assertTrue(actualText.contains("Trouble ticket closed successfully"));
		 */
	}

	// handle Resolved Ticket
	public void handleResolvedTicket(String alarmDescription, String siteId, String rcaCategory, String rcaSubCategory,
			String rcaReason, String remarks, String TicketValue,String selectFaultArea,String faultAreaDetails,String resolutionMethod) throws InterruptedException {
		Thread.sleep(1000);
		Allure.step(
				"/* Only Assignee and Creator, TT closure group user can take action on ticket, no ther user should able to resolve the TT */");
		handleClickByJS("click_On_Alarm_Description_XPATH");
		enterTextIntoInputBox("alarmDescriptionn_XPATH", alarmDescription);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[contains(text(),'" + alarmDescription + "')]")))
				.click();
		clickEscape();
		if (TicketValue.equals("First")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId1);
		}

		else if (TicketValue.equals("Second")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId2);
		}
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(7000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		Allure.step("/* Alarm history should get maintened */");
		handleClickByJS("cilck_On_Alarm_History_XPATH");
		// click("export_Alarm_History_XPATH");

		Allure.step("/* TT assignment history should get maintained */");
		handleClickByJS("cilck_On_Assign_Detail_XPATH");
		click("export_Assign_Details_XPATH");

		Thread.sleep(1000);
		handleClickByJS("cilck_On_Update_Ticket_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		Allure.step(
				"If user has not select equipment while creating the ticket but once ticket is created equipment should get populate based on alarm configuration");
		String s = getText("equipment_Details_XPATH");
		Allure.step("Equipment Value : " + s);

		Allure.step("/* Verify Operator detail get filled based on Site operator mapping */");
		click("click_On_Operator_XPATH");
		List<WebElement> opratorList = driver.findElements(By.xpath(
				"//li[@class='operatorList']//ul//li/label/input[@aria-selected='true']/following-sibling::span"));
		for (WebElement value : opratorList) {
			String cityname = value.getText();
			Allure.step("operator details Value : " + cityname);

		}
		clickEscape();
		Thread.sleep(2000);
		handleClickByJS("click_On_Ticket_Status_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='Resolved']"))).click();

		Allure.step(
				"/* IF RCA is mandatory against alarm then User needs to provide the RCA category against ticket while resolving */");
		handleClickByJS("rca_Category_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaCategory + "']")))
				.click();
		click("rca_Sub_Category_XPATH");
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaSubCategory + "']")))
				.click();
		Thread.sleep(1000);
		handleInputFieldByJS("rca_Reason_XPATH", rcaReason);
		Thread.sleep(1000);
		enterTextIntoInputBox("fuel_Level_XPATH", "value");
		enterTextIntoInputBox("DG_Meter_Reading_XPATH", "value");
		enterTextIntoInputBox("grid_Meter_Reading_XPATH", "value");
		click("select_Fault_Area_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", selectFaultArea);
		click("Select_Fault_Area_Detail_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", faultAreaDetails);
		click("select_Resolution_Method_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", resolutionMethod);
		click("action_Taken_XPATH");
		click("choose_action_Taken_XPATH");
		enterTextIntoInputBox("fuel_Level_XPATH", "value");
		enterTextIntoInputBox("grid_Meter_Reading_XPATH", "value");
		performScrolling("spare_Part_XPATH");
		/* Files should be added while updating the ticket */
		Allure.step("/* Files should be added while updating the ticket- Jpg, png, excel, pdf, word */");
		uploadThedocxFile("addTicket");
		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", remarks);
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		Assert.assertTrue(actualText.contains("Trouble ticket resolved successfully"));
		Thread.sleep(1000);

	}

	/* For each update audit log should be maintained */
	public void exportAuditLogFile() throws InterruptedException, TimeoutException {
		Thread.sleep(1000);
		Allure.step("/* For each update audit log should be maintained */");
		performScrolling("export_Added_Spare_Parts_File_XPATH");
		click("Show_Ticket_Attachments_XPATH");
		click("export_Added_Spare_Parts_File_XPATH");
		Thread.sleep(1000);
		renameDownloadedFile("TTRemarks_", "AuditLogExportedFile");
		verifyUsingAssertFileIsExistInLocation("AuditLogExportedFile");
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");
	}

	/// search by site id
	public void searchBySiteId(String siteId) throws InterruptedException, TimeoutException {
		click("addTicketButton_XPATH");
		handleIframe("iframe_ID");
		enterTextIntoInputBox("siteID_ID", siteId);
		driver.findElement(By.id("txtATSiteID")).clear();
		enterTextIntoInputBox("siteID_ID", siteId);
		Allure.step("Search By Site Id.....");
		List<WebElement> siteIdList = driver.findElements(By.xpath("/html/body/ul/li/a"));
		for (WebElement dropdownList : siteIdList) {
			String siteIdListText = dropdownList.getText();
			Allure.step(siteIdListText);
		}
		Thread.sleep(1000);
		driver.findElement(By.id("txtATSiteID")).clear();
		enterTextIntoInputBox("siteID_ID", Keys.chord("ZAC"));
		Thread.sleep(2000);
		List<WebElement> siteIdList2 = driver.findElements(By.xpath("/html/body/ul/li/a"));
		siteIdList2.get(0).click();

	}

	// search by name
	public void searchBySiteName(String siteName) throws InterruptedException, TimeoutException {
		enterTextIntoInputBox("siteID_ID", siteName);
		driver.findElement(By.id("txtATSiteID")).clear();
		enterTextIntoInputBox("siteID_ID", siteName);
		Allure.step("Search By Site Name.....");
		List<WebElement> siteNameList = driver.findElements(By.xpath("/html/body/ul/li/a"));
		for (WebElement dropdownList : siteNameList) {
			String siteNameListTesxt = dropdownList.getText();
			Allure.step(siteNameListTesxt);
		}
		Thread.sleep(2000);
		List<WebElement> siteNameList2 = driver.findElements(By.xpath("/html/body/ul/li/a"));
		siteNameList2.get(0).click();

	}

	// search by Opco site id
	public void searchByOpcoSiteId(String opcoSiteId) throws InterruptedException, TimeoutException {
		click("addTicketButton_XPATH");
		handleIframe("iframe_ID");
		click("opcoID_XPATH");
		enterTextIntoInputBox("opcoID_XPATH", opcoSiteId);
		clearTextFromInputBox("opcoID_XPATH");
		enterTextIntoInputBox("opcoID_XPATH", opcoSiteId);
		clearTextFromInputBox("opcoID_XPATH");
		enterTextIntoInputBox("opcoID_XPATH", Keys.chord("15"));
		Thread.sleep(2000);
		Allure.step("Search By OPCO Site Id.....");
		List<WebElement> OpcoSiteIdList = driver.findElements(By.xpath("/html/body/ul[2]/li/a"));
		for (WebElement dropdownValue : OpcoSiteIdList) {
			String OpcoSiteIdListText = dropdownValue.getText();
			Allure.step(OpcoSiteIdListText);
		}
		Thread.sleep(2000);
		List<WebElement> OpcoSiteIdList2 = driver.findElements(By.xpath("/html/body/ul[2]/li/a"));
		OpcoSiteIdList2.get(0).click();
		Thread.sleep(3000);
		switchToDefaultContentFromIframe();
		click("closeFrame_XPATH");
	}

	// handle Access Management tab to change request
	public void landingOnAccessMangmentPage() throws InterruptedException {
		Thread.sleep(1000);
		handleClickByJS("insideOpen_XPATH");
		Thread.sleep(1000);
		sa.assertEquals(getText("AccessManagementModule_XPATH"), "Access Management");
		Thread.sleep(1000);
		handleClickByJS("AccessManagementModule_XPATH");
		Thread.sleep(1000);
		handleClickByJS("myChangeRequest_XPATH");

	}

	// handle Access Management tab to change request
	public void SearchRequestInChangeRequest(String changeId) throws InterruptedException, TimeoutException {

		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleInputFieldByJS("changeIdFilter_XPATH", changeId);
		handleCalenderOnAccessManagment(1, 1);
		clickOnDayWithOutTimeslot(30);
		click("filter_XPATH");
		ticketIdText = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//*[@id='center']/div/div[2]/div[2]/div/div/div[1]/div[9]")))
				.getText();

		if (ticketIdText != null) {
			click("ChangeId_XPATH");
			handleIframe("iframe1_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			handleIframe("iFrame_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			try {
				if ("Dismiss".equals(getText("dismiss_Frame_XPATH"))) {
					Assert.fail("Text should not be 'Dismiss' but it was found.");
				}
			} catch (Exception e) {

			}
		}
	}

	// Search Request In Change Request close Frame
	public void SearchRequestInChangeRequestcloseFrame() throws InterruptedException {

		switchToDefaultContentFromIframe();
		switchToDefaultContentFromIframe();
		click("closediFrame_XPATH");

	}

	public String requestStatusOnAccessManagement() {
		String status = getText("status_XPATH");
		return status;
	}

	public String siteIDOnAccessManagement() {
		String siteId = getDomProperty("siteIDAcess_XPATH", "value");
		return siteId;
	}

	public String plannedEndDateOnAccessManagement() {
		String plannedEnd = getDomProperty("planDate_XPATH", "value");
		return plannedEnd;
	}

	public String plannedStartDateOnAccessManagement() {
		String plannedStart = getDomProperty("planstartdate_XPATH", "value");
		return plannedStart;
	}

	// handle Access Management tab to change request
	public void SearchRequestInTroubleTicket(String ticketId) throws InterruptedException {
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		landingOnTroubleTicketPageUsingJS();
		sa.assertEquals(getText("troubleTickting_XPATH"), "Trouble Ticketing");
		click("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		clickEscape();
		handleCalenderOnTroubleTicketOnBasisOfMonth(2);
		handleInputFieldByJS("ticketIdFilter_XPATH", ticketId);
		click("filterReport_XPATH");
		Thread.sleep(5000);
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("ticketIdOnPage_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleIframe("iframe1_XPATH");
		sa.assertEquals(getText("updateticketmodule_XPATH"), "Update Ticket");
	}

	public String accessRequestStatusOnTroubleTicket() {
		String statusTrouble = getDomProperty("approve_XPATH", "value");
		return statusTrouble;
	}

	public String siteIDOnTroubleTicket() {
		String siteId = getDomProperty("siteIdUpdate_XPATH", "value");
		return siteId;
	}

	public String actualStartDateOnTroubleTicket() {
		String actualStartDate = getDomProperty("startdate_XPATH", "value");
		return actualStartDate;
	}

	public String actualEndDateOnTroubleTicket() {
		String actualEndDateUpadte = getDomProperty("startdate_XPATH", "value");
		return actualEndDateUpadte;
	}

	public String accessIDValueOnTroubleTicket() {
		String actualEndDateUpadte = getDomProperty("Access_ID_Value_XPATH", "value");
		return actualEndDateUpadte;
	}

	// validate Access Request
	public void validateAccessRequest(String ticketId) throws InterruptedException {

		String requestStatusOnAM = requestStatusOnAccessManagement();
		Allure.step("Value available as text for request Status : " + requestStatusOnAM,
				io.qameta.allure.model.Status.PASSED);
		String siteIDOnAM = siteIDOnAccessManagement();
		Allure.step("Value available as text for site ID : " + siteIDOnAM, io.qameta.allure.model.Status.PASSED);
		String plannedStartDateOnAM = plannedStartDateOnAccessManagement();
		Allure.step("Value available as text planned Start Date : " + plannedStartDateOnAM,
				io.qameta.allure.model.Status.PASSED);
		String plannedEndDateOnAM = plannedEndDateOnAccessManagement();
		Allure.step("Value available for planned End Date : " + plannedEndDateOnAM,
				io.qameta.allure.model.Status.PASSED);

		SearchRequestInChangeRequestcloseFrame();
		SearchRequestInTroubleTicket(ticketId);

		String accessRequestStatusOnTT = accessRequestStatusOnTroubleTicket();
		Allure.step("Value available as text for access Request Status : " + accessRequestStatusOnTT,
				io.qameta.allure.model.Status.PASSED);
		String siteIdOnTT = siteIDOnTroubleTicket();
		Allure.step("Value available for site ID  : " + siteIdOnTT, io.qameta.allure.model.Status.PASSED);
		String actualStartDateOnTT = actualStartDateOnTroubleTicket();
		Allure.step("Value available for actual Start Date : " + actualStartDateOnTT,
				io.qameta.allure.model.Status.PASSED);
		String actualEndDateOnTT = actualEndDateOnTroubleTicket();
		Allure.step("Value available for actual End Date : " + actualEndDateOnTT, io.qameta.allure.model.Status.PASSED);
		String accessIDValueOnTT = accessIDValueOnTroubleTicket();
		Allure.step("Value available for access ID Value : " + accessIDValueOnTT, io.qameta.allure.model.Status.PASSED);

		Allure.step("Verify Request Status matches between Access Management and Trouble Ticket", () -> {
			Assert.assertEquals(requestStatusOnAM, accessRequestStatusOnTT,
					"Request Status On Access Management And Access Request Status On Trouble Ticket are not matched");
		});

		Allure.step("Verify Site ID matches between Access Management and Trouble Ticket", () -> {
			Assert.assertEquals(siteIDOnAM, siteIdOnTT,
					"Site id are not matched in Access Management and Trouble Ticket");
		});

		Allure.step("Verify Planned Start Date matches Actual Start Date in  Access Management and Trouble Ticket",
				() -> {
					Assert.assertEquals(plannedStartDateOnAM, actualStartDateOnTT,
							"Plan start date and Actual start date are not matched On Access Management and Trouble Ticket");
				});

		Allure.step("Verify Planned End Date matches Actual End Date in Access Management and Trouble Ticket", () -> {
			Assert.assertEquals(plannedEndDateOnAM, actualEndDateOnTT,
					"Plan End date and Actual End date are not matched On Access Management and Trouble Ticket");
		});
		Thread.sleep(5000);
		switchToDefaultContentFromIframe();
		switchToDefaultContentFromIframe();
		click("closediFrame_XPATH");
	}

	// RMS Data For Ticket Id Should Reflect On Alarm Description Hover
	public void RMSDataForTicketIdShouldReflectOnAlarmDescHover(String ticketID, String ticketMode)
			throws InterruptedException, TimeoutException {
		Thread.sleep(1000);
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		handleClickByJS("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		clickEscape();
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		handleClickByJS("ticket_Mode_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath("//span[normalize-space()='" + ticketMode + "']/preceding-sibling::input[@type=\"checkbox\"]")))
				.click();
		handleInputFieldByJS("TicKetIDInputField_XPATH", ticketID);
		clickEscape();
	//	handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		Thread.sleep(1000);
		handleCalenderOnTroubleTicket(5);
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		// 1. Wait for element to be visible (not just present) and scroll into view
		WebElement tool = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tooltip-colmn.ng-binding"))); // Using
																													// *
																													// for
																													// partial
																													// attribute
																													// match

		// 2. Scroll with smoother JavaScript options
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});", tool);
		String tooltipText = "";
		for (int i = 0; i < 5; i++) {
			try {
				// Perform hover
				new Actions(driver).moveToElement(tool).pause(Duration.ofMillis(500)) // Short pause to trigger hover
						.perform();

				// Immediately fetch tooltip element once it appears
				WebElement tooltip = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='DivHoverAlarmDesc']")));

				// Grab text instantly without waiting too long
				tooltipText = tooltip.getText().trim();

				if (!tooltipText.isEmpty()) {
					Allure.step("Equipment Hover Tooltip: " + tooltipText);
					break; // success → exit loop
				} // Exit loop if successful
			} catch (StaleElementReferenceException | NoSuchElementException e) {
				if (i == 4) {
					throw e; // Last attempt failed
				}
				Thread.sleep(500); // Small retry pause
			}
		}
	}

	/* Wild search should work in site id filter */
	public void wildSearchBySiteId(String siteId) throws InterruptedException {
		handleClickByJS("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		Allure.step("Search By site id: " + siteId);
		handleCalenderOnTroubleTicketOnBasisOfMonth(1);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		String randomSiteId = siteId.substring(0, 3);
		Allure.step("Search By partial site id: " + randomSiteId);
		handleInputFieldByJS("siteIDInputField_XPATH", randomSiteId);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		String randomSiteId2 = siteId.substring(0, 2);
		Allure.step("Search By partial site id: " + randomSiteId2);
		handleInputFieldByJS("siteIDInputField_XPATH", randomSiteId2);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(1000);
	}

	/* Wild search should work in ticket id filter */
	public void wildSearchByTicketId(String TicketId) throws InterruptedException {
		refreshPage();
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		Thread.sleep(1000);
		click("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId);
		Allure.step("Search By ticket id: " + TicketId);
		handleCalenderOnTroubleTicketOnBasisOfMonth(1);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		String randomTicketId = TicketId.substring(0, 3);
		Allure.step("Search By partial ticket id: " + randomTicketId);
		handleInputFieldByJS("TicKetIDInputField_XPATH", randomTicketId);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		click("ShowFilterOnTroubleTicketingPage_XPATH");
		String randomTicketId2 = TicketId.substring(0, 4);
		Allure.step("Search By partial ticket id: " + randomTicketId2);
		handleInputFieldByJS("TicKetIDInputField_XPATH", randomTicketId2);
		handleClickByJS("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(1000);

	}

	/* Wild search should work in OPCO siteId filter */
	public void wildSearchByOPCOSiteId(String OPCOSiteId) throws InterruptedException {
		refreshPage();
		click("ticketStatusID_XPATH");
		click("click_On_All_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("siteIDInputField_XPATH", OPCOSiteId);
		Allure.step("Search By opco id: " + OPCOSiteId);
		handleCalenderOnTroubleTicketOnBasisOfMonth(3);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		String ramdomopcoId = OPCOSiteId.substring(0, 2);
		Allure.step("Search By partial opco id: " + ramdomopcoId);
		handleInputFieldByJS("siteIDInputField_XPATH", ramdomopcoId);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		String opcoId2 = OPCOSiteId.substring(0, 3);
		Allure.step("Search By partial opco id: " + opcoId2);
		handleInputFieldByJS("siteIDInputField_XPATH", opcoId2);
		click("filterReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(1000);

	}

	public void writeData(String ticketType) throws IOException, CsvException {
		String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String path = userDir + "\\src\\main\\resources\\iTower_Clients_Excel\\excel_Tawal\\TTBulk" + ticketType
				+ ".csv";

		File file = new File(path);
		List<String[]> csvData = new ArrayList<>();

		// Read CSV
		try (CSVReader reader = new CSVReader(new FileReader(file))) {
			csvData = reader.readAll();
		}

		// Ensure row 2 exists (index 1 in zero-based)
		while (csvData.size() <= 1) { // Add empty rows if row 2 doesn't exist
			csvData.add(new String[0]);
		}

		// Ensure column 3 exists (index 2 in zero-based)
		if (csvData.get(1).length <= 2) {
			String[] newRow = Arrays.copyOf(csvData.get(1), 3); // Expand to 3 columns
			csvData.set(1, newRow);
		}

		// Modify data (row 2, column 3)
		csvData.get(1)[2] = ticketIdValue; // Zero-based: row 1 (2nd row), column 2 (3rd column)

		// Write back
		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			writer.writeAll(csvData);
		}
	}

	// Update ETA and ETR fields
	public void updateETAAndETRByAssignUser(String alarmDescription, String siteId, String ETAValue, String ETRValue,
			String assignedTo, String remarks, String TicketValue) throws InterruptedException {
		Thread.sleep(1000);
		handleClickByJS("click_On_Alarm_Description_XPATH");
		enterTextIntoInputBox("alarmDescriptionn_XPATH", alarmDescription);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[contains(text(),'" + alarmDescription + "')]")))
				.click();
		clickEscape();
		if (TicketValue.equals("First")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId1);
		}

		else if (TicketValue.equals("Second")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId2);
		}
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(7000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		// select Assigned To
		Allure.step("/* Assigned user should able to reassign the ticket to another user */");
		click("reassign_User_XPATH");
		enterTextIntoInputBox("selectAssigned_XPATH", assignedTo);
		clickUsingDynamicLocator("assignedTo_XPATH", assignedTo);

		/* Assigned user should able to update ETA/ETR */
		Allure.step("/* Assigned user should able to update ETA/ETR */");
		enterTextIntoInputBox("ETA_Value_XPATH", ETAValue);
		enterTextIntoInputBox("ETR_Value_XPATH", ETRValue);

		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", remarks);
		performScrolling("update_Resolved_Ticket_CSS");
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		Assert.assertTrue(actualText.contains("Trouble ticket updated successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");
	}

	// get dropdown list count with dynamic index
	public int getDropDownListCount(String locatorKey, int index, String InputBoxName) {
		String locatorTemplate = OR.getProperty(locatorKey);
		String dynamicXpath = locatorTemplate.replace("{InputBoxName}", String.valueOf(InputBoxName)).replace("{index}",
				String.valueOf(index));
		List<WebElement> allDropDownList = driver.findElements(By.xpath(dynamicXpath));
		return allDropDownList.size();
	}

	// get Dropdown list with dynamic index
	public List<WebElement> getDropDownList(String locatorKey, int index, String inputBoxName) {
		String locatorTemplate = OR.getProperty(locatorKey);
		String dynamicXpath = locatorTemplate.replace("{InputBoxName}", String.valueOf(inputBoxName)).replace("{index}",
				String.valueOf(index));
		List<WebElement> allDropDownList = driver.findElements(By.xpath(dynamicXpath));
		return allDropDownList;
	}

	// handle Select SPM Region dropdown filter
	public void handleSelect_SPM_Region() throws TimeoutException, InterruptedException {
		Thread.sleep(5000);
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Region");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Central");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(1));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(1));
		enterTextIntoInputBox("getText_Select_Region_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Region_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Central");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelect_Sub_Region() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Sub Region");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Hail");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(2));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(2));
		enterTextIntoInputBox("getText_Select_Sub_Region_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Sub_Region_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Hail");

	}

	// handle Select mini cluster dropdown filter
	public void handleSelectMiniCluster() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Mini Cluster");
		String firstValue = clickUsingDynamicLocator("FIRST_XPATH", "Hail");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(3));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(3));
		enterTextIntoInputBox("getText_Select_Mini_Cluster_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Mini_Cluster_XPATH");
		clickUsingDynamicLocator("FIRST_XPATH", "Hail");

	}// handle Select ticket type dropdown filter

	public void handleSelectTicketType() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Ticket Type");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Incident");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(4));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(4));
		enterTextIntoInputBox("getText_Select_Ticket_Type_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Ticket_Type_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Incident");

	}// handle Select Sub Region dropdown filter

	public void handleSelectEquipment() throws TimeoutException, InterruptedException {
		Thread.sleep(1000);
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Equipment");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "AC");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(5));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(5));
		enterTextIntoInputBox("getText_Select_Equipment_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Equipment_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "AC");

	}// handle Select severity dropdown filter

	public void handleSelectSeverity() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Severity");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Critical");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(6));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(6));
		enterTextIntoInputBox("getText_Select_Severity_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Severity_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Critical");

	}// handle Select ticket status dropdown filter

	public void handleSelectTicketStatusSelected(String status) throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "9 Ticket Status Selected");
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(7));
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", status);
		clickEscape();

	}// handle Select Sub Region dropdown filter

	public void handleSelectGroupOfUsers() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Group Of Users");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "1st Escalation");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(8));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(8));
		enterTextIntoInputBox("getText_Select_GroupOf_Users_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_GroupOf_Users_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "1st Escalation");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectUser() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select User");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH",
				"ABDULREHMANABDUlBARI - 590112735");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(9));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(9));
		enterTextIntoInputBox("getText_Select_User_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_User_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "ABDULREHMANABDUlBARI - 590112735");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectTicketMode() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Ticket Mode");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Auto");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(10));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(10));
		enterTextIntoInputBox("getText_Select_Ticket_Mode_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Ticket_Mode_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Auto");

	}

	// handle Select alarm description dropdown filter
	public void handleSelectPowerVendor() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Power Vendor");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "System Define");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(12));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(12));
		enterTextIntoInputBox("getText_Select_Power_Vendor_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Power_Vendor_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "System Define");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectServiceType() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Service Type");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "VIP Metro");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(16));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(16));
		enterTextIntoInputBox("getText_Select_Operator2_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Operator2_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "VIP Metro");

	}// handle Select Sub Region dropdown filter

	public void handleSelectOperator() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Operator");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "KSA2520");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(13));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(13));
		enterTextIntoInputBox("getText_Select_Operator_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Operator_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "KSA2520");

	}// handle Select RCA category dropdown filter

	public void handleSelect_RCACategory() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select RCA Category");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "AC Power Alarm");

	}// handle Select RCA sub-category dropdown filter

	public void handleSelectRCASubCategory() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select RCA Sub Category");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "AC DB Fault");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(10));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(10));
		enterTextIntoInputBox("getText_Select_RCA_Sub_Category_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_RCA_Sub_Category_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "AC DB Fault");
		closeTheOpenTab();

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectVendor() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Vendor");
		String firstValue = clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "ABC");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(18));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(18));
		enterTextIntoInputBox("getText_Select_Vendor_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Vendor_XPATH");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "ABC");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectOperatorSLA() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Operator SLA");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_Operator_SLA_XPATH", "Ooredoo");

	}

	// handle Select Sub Region dropdown filter
	public void handleSelectReassignCount() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Reassign Count");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "1");

	}

	public void handleSelectTicketClosingMode() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Ticket Closing Mode");
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Auto Close");

	}

	public void handleCreateddate() throws TimeoutException, InterruptedException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Created date");
		Thread.sleep(1000);
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "Created date");

	}

	public void handleFromDateCalenderForFilter(int numberOFMonth) throws InterruptedException, TimeoutException {
		click("dateOnCalender_ID");
		for (int i = 1; i <= numberOFMonth; i++) {
			explicitWaitWithClickable("backMonthOnCalender_XPATH");
			Thread.sleep(1000);
		}
		handleClickByJS("selectHourOnCalender_XPATH");

	}

	public void handleFromDateCalenderForFilterSLA(int numberOFMonth) throws InterruptedException, TimeoutException {
		click("dateOnCalender_ID");
		for (int i = 1; i <= numberOFMonth; i++) {
			explicitWaitWithClickable("backMonthOnCalender_SLA_XPATH");
			Thread.sleep(1000);
		}
		int todayDayOfMonth = LocalDate.now().getDayOfMonth();
		clickUsingDynamicLocator("selectDateOnCalender_XPATH", String.valueOf(todayDayOfMonth));

	}

	public void handleToDateCalenderForFilter(int numberOFMonth) throws InterruptedException, TimeoutException {
		click("toDateOnCalender_ID");
		for (int i = 1; i <= numberOFMonth; i++) {
			explicitWaitWithClickable("backMonthOnToDateCalender_XPATH");
			Thread.sleep(1000);
		}

		closeTheOpenTab();

	}

	// Land On Trouble Ticket Module Using JS
	public void landingOnConfiguration() throws InterruptedException {
		Thread.sleep(1000);
		handleClickByJS("insideOpen_XPATH");
		Thread.sleep(1000);
		sa.assertEquals(getText("configurationModule_XPATH"), "Configuration");
		handleClickByJS("rptMenuConfiguration_XPATH");
		Thread.sleep(1000);
	}

	// Add alarm configuration
	public void addAlarmConfiguration(String alarmName, String alarmType, String raiseTicket, String alarmEquipment,
			String ticketRCA) throws InterruptedException {
		click("click_AddAlarmIcon_XPATH");
		handleIframe("alarmDescription_iFrame_XPATH");
		enterTextIntoInputBox("enterAlarmName_XPATH", alarmName);
		handleClickByJS("click_AlarmTypeField_XPATH");
		handleListOfTroubleTicket("select_AlarmTypeDropdownValue_XPATH", alarmType);
		handleClickByJS("click_RaiseTicketField_XPATH");
		handleListOfTroubleTicket("select_RaiseTicketDropdwonValue_XPATH", raiseTicket);
		handleClickByJS("click_AlarmEquipment_XPATH");
		handleListOfTroubleTicket("select_AlarmEquipmentDropdwonValue_XPATH", alarmEquipment);
		handleClickByJS("click_TicketRCA_XPATH");
		handleListOfTroubleTicket("select_TicketRCADropdwonValue_XPATH", ticketRCA);
		handleClickByJS("click_TicketType_XPATH");
		clickUsingDynamicLocator("select_TicketTypeDropdownValue_XPATH", "Incident");
		handleClickByJS("click_TicketType_XPATH");
		explicitWaitWithClickable("click_EffectiveFrom_XPATH");
		selectTodayDate(0);
		Thread.sleep(2000);
		handleClickByJS("save_AlarmConfig_XPATH");
		sa.assertEquals(getText("errorMessageCapture_XPATH"), "Alarm Description already exist.");

	}

	// Handle save filter functionality
	public void saveFilterFunctionality() throws InterruptedException {
		handleClickByJS("click_SaveFilterButton_XPATH");
		handleIframe("iframeSaveFilter_ID");
		click("click_TicketIDCheckbox_XPATH");
		click("click_SiteIDCheckbox_XPATH");
		// click("click_SiteNameCheckbox_XPATH");
		enterTextIntoInputBox("enterFilterName_XPATH", "New Filter Save");
		click("clickSaveButton_ID");
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		click("close_Frame_XPATH");
		// click("ShowFilterOnTroubleTicketingPage_XPATH");
		scrollRight();
		click("clickSaveFilter_XPATH");
		Thread.sleep(2000);
		clickUsingDynamicLocator("selct_First_Value_From_DropDown_XPATH", "New Filter Save");
		click("filterReport_XPATH");
		Thread.sleep(1000);
		click("exportTroublTckt_XPATH");
	}

	// Handle full screen mode functionality
	public void applyFullScreenMode() throws InterruptedException {
		click("click_FullScreenButton_XPATH");
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(1000);
	}

	// Handle refresh button functionality
	public void applyRefreshButton() throws InterruptedException {
		click("clickToggle_XPATH");
		Thread.sleep(1000);
	}

	// Land On Trouble Ticket Module Using JS
	public void landingOnSLAReportPageUsingJS() throws InterruptedException, TimeoutException {
		Thread.sleep(1000);
		handleClickByJS("insideOpen_XPATH");
		Thread.sleep(1000);
		sa.assertEquals(getText("tickingManagementModule_XPATH"), "Ticketing Management");
		handleClickByJS("rptMenu_XPATH");
		Thread.sleep(1000);
		handleClickByJS("troubleTickting_XPATH");
		handleClickByJS("sla_Module_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

	}

	public void verifySLAReport(String operator, String Vendor) throws TimeoutException, InterruptedException {
		Allure.step("/* Vendor/Operator SLA should be coming in report if defined in system */");
		handleClickByJS("sla_Module_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		handleClickByJS("selectSLA_Operator_XPATH");
		Thread.sleep(1000);
		handleListOfTroubleTicket("selectSLA_Operator_Value_XPATH", operator);
		Thread.sleep(1000);
		// select vendor type
		handleClickByJS("selectSLA_Vendor_XPATH");
		handleListOfTroubleTicket("selectSLA_Vendor_Value_XPATH", Vendor);

	}

	public void verifyDataFieldValidation(String status) throws Exception {

		String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

		File downloadDirectoryTemplate = new File(basePath + "iTower_Clients_Excel\\excel_Tawal\\");
		String templateCsvPath = downloadDirectoryTemplate + "\\" + status + ".csv";

		File downloadDirectory = new File(basePath + "downloadExcel\\");
		String targetCsvPath = downloadDirectory + "\\TroubleTicketingFieldValidation.csv";

		String outputExcelPath = downloadDirectoryTemplate + "\\TT_mandatory_field_validation_report.xlsx";
		Path templatePath = Paths.get(templateCsvPath);
		if (!Files.exists(templatePath))
			throw new FileNotFoundException("Template file not found: " + templateCsvPath);

		// 1) Build mandatory headers from template and save them
		List<String> mandatoryHeaders = buildMandatoryHeadersFromTemplate(templateCsvPath);

		// Save mandatory headers to file next to template
		Path mandatoryFile = templatePath.getParent().resolve("TT_mandatory_headers.txt");
		saveMandatoryHeaders(mandatoryFile, mandatoryHeaders);
		System.out.println(
				"Saved mandatory headers (" + mandatoryHeaders.size() + ") to: " + mandatoryFile.toAbsolutePath());

		// 2) If targetCsvPath provided, validate it
		if (targetCsvPath != null && !targetCsvPath.trim().isEmpty()) {
			Path targetPath = Paths.get(targetCsvPath);
			if (!Files.exists(targetPath))
				throw new FileNotFoundException("Target file not found: " + targetCsvPath);

			// Load mandatory headers (from saved file)
			List<String> loadedMandatory = loadMandatoryHeaders(mandatoryFile);

			// Validate target and create Excel (Status + Site Id columns included)
			validateTargetAndWriteReportWithStatusAndSiteId(targetCsvPath, loadedMandatory, outputExcelPath);
			System.out.println("Validation report written to: " + outputExcelPath);
		} else {
			System.out.println("No targetCsvPath provided — only mandatory headers were created/updated.");
		}
	}

	// -----------------------------
	// 1) Build mandatory headers
	// -----------------------------
	private List<String> buildMandatoryHeadersFromTemplate(String templateCsvPath)
			throws IOException, CsvValidationException {
		char sep = detectSeparator(templateCsvPath);
		// read a few rows (header + at least one data row)
		List<String[]> rows = readFirstNRows(templateCsvPath, sep, 3);
		rows = removeSepLineIfPresent(rows); // <-- important fix

		if (rows.isEmpty())
			return Collections.emptyList();

		String[] headers = rows.get(0);
		String[] sample = rows.size() > 1 ? rows.get(1) : new String[0];

		List<String> mandatory = new ArrayList<>();
		for (int i = 0; i < headers.length; i++) {
			String h = headers[i] == null ? "" : stripBom(headers[i]).trim();
			String s = (i < sample.length && sample[i] != null) ? stripBom(sample[i]).trim() : "";
			if (!h.isEmpty() && !s.isEmpty()) {
				mandatory.add(h);
			}
		}
		return mandatory;
	}

	private void saveMandatoryHeaders(Path file, List<String> headers) throws IOException {
		Files.write(file, headers, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	private List<String> loadMandatoryHeaders(Path file) throws IOException {
		if (!Files.exists(file))
			return Collections.emptyList();
		return Files.readAllLines(file, StandardCharsets.UTF_8);
	}

	// -----------------------------
	// 2) Validate target CSV and write report (with Status and Site Id)
	// -----------------------------
	private void validateTargetAndWriteReportWithStatusAndSiteId(String targetCsvPath, List<String> mandatoryHeaders,
			String outputExcelPath) throws IOException, CsvValidationException {

		if (mandatoryHeaders == null)
			mandatoryHeaders = Collections.emptyList();
		char sep = detectSeparator(targetCsvPath);

		List<String[]> rows = readAllRows(targetCsvPath, sep);
		rows = removeSepLineIfPresent(rows);

		if (rows.size() < 2) {
			throw new IllegalStateException("CSV does not contain valid header row on index 1: " + targetCsvPath);
		}

// ----------------------------------------------
// HEADER IS ON SECOND ROW (index 1)
// ----------------------------------------------
		String[] headerRow = rows.get(1);

		Map<String, Integer> headerIndex = new LinkedHashMap<>();
		for (int i = 0; i < headerRow.length; i++) {
			String h = headerRow[i] == null ? "" : stripBom(headerRow[i]).trim();
			if (!h.isEmpty())
				headerIndex.put(h, i);
		}

// Column index detection
		int statusIndex = headerIndex.containsKey("Ticket Status") ? headerIndex.get("Ticket Status") : -1;
		int siteIdIndex = headerIndex.containsKey("Site Id") ? headerIndex.get("Site Id") : -1;

		List<ValidationRecordWithStatusAndSiteId> records = new ArrayList<>();
		Map<String, Integer> missingCounts = new LinkedHashMap<>();

// DATA STARTS FROM 3rd ROW (index = 2)
		int dataStartIndex = 2;
		int totalDataRows = Math.max(0, rows.size() - dataStartIndex);

		for (int r = dataStartIndex; r < rows.size(); r++) {
			String[] dataRow = rows.get(r);

// Displayed line number (row2→1, row3→2 etc.)
			int displayedLine = r - 1;

			String statusValue = (statusIndex >= 0 && statusIndex < dataRow.length)
					? stripBom(dataRow[statusIndex]).trim()
					: "";

			String siteIdValue = (siteIdIndex >= 0 && siteIdIndex < dataRow.length)
					? stripBom(dataRow[siteIdIndex]).trim()
					: "";

			for (String mandHeader : mandatoryHeaders) {
				if (!headerIndex.containsKey(mandHeader)) {
					records.add(new ValidationRecordWithStatusAndSiteId(displayedLine, mandHeader, "", siteIdValue,
							statusValue, "Header missing in target"));
					missingCounts.put(mandHeader, missingCounts.getOrDefault(mandHeader, 0) + 1);
				} else {
					int idx = headerIndex.get(mandHeader);
					String val = idx < dataRow.length ? stripBom(dataRow[idx]).trim() : "";
					if (val.isEmpty()) {
						records.add(new ValidationRecordWithStatusAndSiteId(displayedLine, mandHeader, val, siteIdValue,
								statusValue, "Value not present"));
						missingCounts.put(mandHeader, missingCounts.getOrDefault(mandHeader, 0) + 1);
					} else {
						records.add(new ValidationRecordWithStatusAndSiteId(displayedLine, mandHeader, val, siteIdValue,
								statusValue, "OK"));
					}
				}
			}
		}

		writeValidationExcelWithStatusAndSiteId(outputExcelPath, records, mandatoryHeaders, missingCounts,
				totalDataRows);
	}

	/**
	 * Writes the validation results to an Excel file with colored cells for
	 * visibility. RowLevelComparison columns: CSV Line, Site Id, Status, Header,
	 * Value, Result
	 */
	private void writeValidationExcelWithStatusAndSiteId(String outputXlsxPath,
			List<ValidationRecordWithStatusAndSiteId> records, List<String> mandatoryHeaders,
			Map<String, Integer> missingCounts, int totalDataRows) throws IOException {

		Workbook wb = new XSSFWorkbook();
		try {
			// ============================
			// Styles with colors
			// ============================
			Font bold = wb.createFont();
			bold.setBold(true);

			// Header style
			CellStyle headerStyle = wb.createCellStyle();
			headerStyle.setFont(bold);

			// OK = light green
			CellStyle styleOK = wb.createCellStyle();
			styleOK.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			styleOK.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Value not present = light red
			CellStyle styleMissingValue = wb.createCellStyle();
			styleMissingValue.setFillForegroundColor(IndexedColors.ROSE.getIndex());
			styleMissingValue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Header missing = light orange
			CellStyle styleMissingHeader = wb.createCellStyle();
			styleMissingHeader.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			styleMissingHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Summary: OK header (0% missing)
			CellStyle styleSummaryOK = wb.createCellStyle();
			styleSummaryOK.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			styleSummaryOK.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Summary: missing >0%
			CellStyle styleSummaryBad = wb.createCellStyle();
			styleSummaryBad.setFillForegroundColor(IndexedColors.ROSE.getIndex());
			styleSummaryBad.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// =========================================================
			// Sheet 1: RowLevelComparison (NEW COLUMN ORDER)
			// =========================================================
			org.apache.poi.ss.usermodel.Sheet s1 = wb.createSheet("RowLevelComparison");

			// COLUMN HEADERS IN NEW ORDER
			String[] cols = { "Row Number", "Site Id", "Ticket Status", "Header Name", "Value",
					"Result(Mandatory Value)" };

			Row hr = s1.createRow(0);
			for (int i = 0; i < cols.length; i++) {
				Cell c = hr.createCell(i);
				c.setCellValue(cols[i]);
				c.setCellStyle(headerStyle);
			}

			// Freeze header row
			s1.createFreezePane(0, 1);

			int r = 1;
			for (ValidationRecordWithStatusAndSiteId rec : records) {
				Row row = s1.createRow(r++);
				row.createCell(0).setCellValue(rec.csvLine); // CSV Line (1-based for data rows)
				row.createCell(1).setCellValue(rec.siteId); // Site Id
				row.createCell(2).setCellValue(rec.status); // Status
				row.createCell(3).setCellValue(rec.header); // Header
				row.createCell(4).setCellValue(rec.value); // Value

				Cell resultCell = row.createCell(5);
				resultCell.setCellValue(rec.result); // Result

				// Coloring rule
				if ("OK".equals(rec.result)) {
					resultCell.setCellStyle(styleOK);
				} else if ("Value not present".equals(rec.result)) {
					resultCell.setCellStyle(styleMissingValue);
				} else if ("Header missing in target".equals(rec.result)) {
					resultCell.setCellStyle(styleMissingHeader);
				}
			}

			for (int i = 0; i < cols.length; i++)
				s1.autoSizeColumn(i);

			// =========================================================
			// Sheet 2: Summary per header
			// =========================================================
			org.apache.poi.ss.usermodel.Sheet s2 = wb.createSheet("SummaryPerHeader");
			String[] sumCols = { "Header", "Mandatory", "Missing Count", "Total Rows", "Missing %" };
			Row sh = s2.createRow(0);
			for (int i = 0; i < sumCols.length; i++) {
				Cell c = sh.createCell(i);
				c.setCellValue(sumCols[i]);
				c.setCellStyle(headerStyle);
			}

			int ir = 1;
			for (String header : mandatoryHeaders) {
				int missing = missingCounts.getOrDefault(header, 0);
				double pct = totalDataRows == 0 ? 0 : (100.0 * missing / totalDataRows);

				Row row = s2.createRow(ir++);
				row.createCell(0).setCellValue(header);
				row.createCell(1).setCellValue("Yes");
				row.createCell(2).setCellValue(missing);
				row.createCell(3).setCellValue(totalDataRows);

				Cell pctCell = row.createCell(4);
				pctCell.setCellValue(String.format("%.2f%%", pct));

				// coloring
				if (pct == 0.0) {
					pctCell.setCellStyle(styleSummaryOK);
				} else {
					pctCell.setCellStyle(styleSummaryBad);
				}
			}

			for (int i = 0; i < sumCols.length; i++)
				s2.autoSizeColumn(i);

			// ensure parent dirs
			File out = new File(outputXlsxPath);
			if (out.getParentFile() != null)
				out.getParentFile().mkdirs();

			try (OutputStream fos = new FileOutputStream(out)) {
				wb.write(fos);
			}
		} finally {
			wb.close();
		}
	}

	// -----------------------------
	// CSV helpers
	// -----------------------------
	private char detectSeparator(String path) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					int tabs = countChar(line, '\t');
					int commas = countChar(line, ',');
					return tabs >= commas ? '\t' : ',';
				}
			}
		}
		return '\t';
	}

	private int countChar(String s, char ch) {
		if (s == null)
			return 0;
		int c = 0;
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == ch)
				c++;
		return c;
	}

	private List<String[]> readFirstNRows(String path, char separator, int n)
			throws IOException, CsvValidationException {
		List<String[]> rows = new ArrayList<>(n);
		try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
				CSVReader csv = new CSVReaderBuilder(reader)
						.withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build()) {
			String[] row;
			int i = 0;
			while (i < n && (row = csv.readNext()) != null) {
				rows.add(row);
				i++;
			}
		}
		return rows;
	}

	private List<String[]> readAllRows(String path, char separator) throws IOException, CsvValidationException {
		List<String[]> rows = new ArrayList<>();
		try (Reader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8);
				CSVReader csv = new CSVReaderBuilder(reader)
						.withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build()) {
			String[] row;
			while ((row = csv.readNext()) != null) {
				rows.add(row);
			}
		}
		return rows;
	}

	/**
	 * Remove a leading "sep=," (or "sep=\t", etc) row if present. Also strips UTF-8
	 * BOM from the first cell when checking.
	 */
	private List<String[]> removeSepLineIfPresent(List<String[]> rows) {
		if (rows == null || rows.isEmpty())
			return rows;
		String[] first = rows.get(0);
		if (first != null && first.length > 0 && first[0] != null) {
			String firstCell = stripBom(first[0]).trim().toLowerCase(Locale.ROOT);
			if (firstCell.startsWith("sep=")) {
				// drop the sep= line
				rows.remove(0);
			}
		}
		return rows;
	}

	/**
	 * Remove BOM if present at start of the string.
	 */
	private String stripBom(String s) {
		if (s == null)
			return null;
		if (s.startsWith("\uFEFF"))
			return s.substring(1);
		return s;
	}

	// -----------------------------
	// small classes
	// -----------------------------
	private static class ValidationRecordWithStatusAndSiteId {
		int csvLine;
		String header;
		String value;
		String siteId;
		String status;
		String result;

		ValidationRecordWithStatusAndSiteId(int csvLine, String header, String value, String siteId, String status,
				String result) {
			this.csvLine = csvLine;
			this.header = header == null ? "" : header;
			this.value = value == null ? "" : value;
			this.siteId = siteId == null ? "" : siteId;
			this.status = status == null ? "" : status;
			this.result = result == null ? "" : result;
		}
	}

	public void verifySiteIdAutoFilledByOPCOSiteId(String opcoId) throws InterruptedException {
		click("addTicketButton_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(1000);
		/* Site ID should get auto filled if user fill Opco Site ID */
		Allure.step("/* Verify Site ID get auto filled if user fill Opco Site ID */");
		enterTextIntoInputBoxUsingActionsClass("OPCO_Site_Id_XPATH", opcoId);
		clickOnSuggestedValue();
		getDomProperty("siteID_XPATH", "value");
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		click("closeFrame_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

	}

	public void verifyTicketStatusFromResolvedToAssignByTTReversalGroup(String alarmDescription, String siteId,
			String TicketValue) throws InterruptedException {
		Thread.sleep(1000);
		handleClickByJS("click_On_Alarm_Description_XPATH");
		enterTextIntoInputBoxUsingActionsClass("alarmDescriptionn_XPATH", alarmDescription);

		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//ul//li//label//span[text()='" + alarmDescription + "']"))).click();
		clickEscape();
		if (TicketValue.equals("First")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId1);
		}

		else if (TicketValue.equals("Second")) {
			handleInputFieldByJS("TicKetIDInputField_XPATH", TicketId2);
		}
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		Thread.sleep(1000);
		handleClickByJS("ticketStatusID_XPATH");
		handleClickByJS("click_On_All_XPATH");
		clickEscape();
		Thread.sleep(2000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(5000);
		click("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		handleClickByJS("clickTicketStatus_XPATH");
		Allure.step("TT reversal Group user can reassign the TT from Resolve to assign");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='Assign']"))).click();
		handleInputFieldByJS("remarks_On_Close_Ticket_XPATH", "Resolved");
		performScrolling("click_On_Update_Ticket_XPATH");
		click("click_On_Update_Ticket_XPATH");
		String actualText = getText("verify_Ticket_Closed_XPATH");
		Assert.assertTrue(actualText.contains("Trouble ticket updated successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");
	}

	public void performLogout() {
		WebElement value = driver.findElement(By.xpath("(//span[@id='ctl00_lblWelcomeMessage2'])[1]"));
		Actions ac = new Actions(driver);
		ac.click(value).perform();
		click("click_Logout_XPATH");

	}

	public static String generatePRNumber() {
		// Generate 3 random digits (000-999)
		Random random = new Random();
		int randomNumber = random.nextInt(1000);

		// Format with leading zeros if needed
		return String.format("RTTS%03d", randomNumber);
	}

	public static String generateEpochTime() {

		long currentEpoch = Instant.now().getEpochSecond();
		String epochString = String.valueOf(currentEpoch);
		return epochString;
	}

	public void verifyStatusReferredToAssign(String siteId, String PRNumber) throws InterruptedException {
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleInputFieldByJS("search_PR_Number_XPATH", PRNumber);
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(10000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		handleClickByJS("click_On_Ticket_Status_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='Assign']"))).click();

		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", "remarks");
		performScrolling("update_Resolved_Ticket_CSS");
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		// monitorTicketUpdate();
		Assert.assertTrue(actualText.contains("Trouble ticket updated successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");

	}

	public void verifyStatusAssignToInprogress(String siteId, String PRNumber) throws InterruptedException {
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleInputFieldByJS("search_PR_Number_XPATH", PRNumber);

		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(10000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		handleClickByJS("click_On_Ticket_Status_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='In Progress']"))).click();
		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", "remarks");
		performScrolling("update_Resolved_Ticket_CSS");
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		Assert.assertTrue(actualText.contains("Trouble ticket updated successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");

	}

	public void verifyRestoreTheRTTSticket(String siteId, String PRNumber, String rcaCategory, String rcaSubCategory,
			String rcaReason,String selectFaultArea,String faultAreaDetails,String resolutionMethod) throws InterruptedException {
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleInputFieldByJS("search_PR_Number_XPATH", PRNumber);
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(10000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		handleClickByJS("click_On_Ticket_Status_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='Restored']"))).click();
		click("status_Reason_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", "Required Permanent Solution");
		
		click("select_Fault_Area_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", selectFaultArea);
		click("Select_Fault_Area_Detail_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", faultAreaDetails);
		click("select_Resolution_Method_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", resolutionMethod);
		handleClickByJS("rca_Category_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaCategory + "']")))
				.click();
		click("rca_Sub_Category_XPATH");
		wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='" + rcaSubCategory + "']")))
				.click();
		Thread.sleep(1000);
		handleInputFieldByJS("rca_Reason_XPATH", rcaReason);
		Thread.sleep(1000);

		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", "remarks");
		performScrolling("update_Resolved_Ticket_CSS");
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		Assert.assertTrue(actualText.contains("Trouble ticket updated successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");

	}

	public boolean monitorTicketUpdate() {
		int maxAttempts = 60; // 30 minutes (60 * 30 seconds)
		int attempt = 0;

		while (attempt < maxAttempts) {
			attempt++;
			System.out.println("Attempt " + attempt + ": Checking ticket status...");

			// Get the response from your ticket system/API
			String response = getText("verify_Resolved_Ticket_Updated_CSS");
			;

			if (response.contains("Trouble ticket updated successfully")) {
				System.out.println("✅ Ticket updated successfully!");
				return true;

			} else if (response.contains("Please wait for sometime to update the ticket")) {
				System.out.println("⏳ Please wait for sometime to update the ticket...");

			} else {
				System.out.println("⚠️ Unexpected response: " + response);
			}

			// Wait for 30 seconds before next check
			if (attempt < maxAttempts) {
				System.out.println("Waiting 30 seconds before next check...");
				try {
					TimeUnit.SECONDS.sleep(30);
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted: " + e.getMessage());
					Thread.currentThread().interrupt();
					return false;
				}
			}
		}

		System.out.println("❌ Maximum attempts reached. Ticket update may have failed.");
		return false;
	}
	
	public void verifyResolvedTheRTTSticket(String siteId, String PRNumber,String statusReason) throws InterruptedException {
		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleInputFieldByJS("search_PR_Number_XPATH", PRNumber);
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(10000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		click("status_Reason_XPATH");
		clickUsingDynamicLocator("chhose_select_Fault_Area_XPATH", statusReason);
		handleClickByJS("click_On_Ticket_Status_XPATH");
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//input[@type='radio']/following-sibling::span[normalize-space()='Resolved']"))).click();
		
		
		enterTextIntoInputBox("grid_Meter_Reading_XPATH", "value");
		click("action_Taken_XPATH");
		click("choose_action_Taken_XPATH");
		enterTextIntoInputBox("fuel_Level_XPATH", "value");
		enterTextIntoInputBox("DG_Meter_Reading_XPATH", "value");
		handleInputFieldByJS("remarks_On_Resolved_Ticket_XPATH", "remarks");
		performScrolling("update_Resolved_Ticket_CSS");
		click("update_Resolved_Ticket_CSS");
		String actualText = getText("verify_Resolved_Ticket_Updated_CSS");
		Assert.assertTrue(actualText.contains("Trouble ticket resolved successfully"));
		Thread.sleep(1000);
		switchToDefaultContentFromIframe();
		handleClickByJS("closeFrame_XPATH");

	}
	
	public void verifyClosedTicket(String siteId, String PRNumber) throws InterruptedException {

		handleInputFieldByJS("siteIDInputField_XPATH", siteId);
		handleInputFieldByJS("search_PR_Number_XPATH", PRNumber);
		Thread.sleep(1000);
		handleClickByJS("filterReport_XPATH");
		Thread.sleep(10000);
		handleClickByJS("open_Ticket_ID_XPATH");
		handleIframe("iframe_ID");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		String ticketStatus = getText("ticket_Status_XPATH");
		Assert.assertEquals(ticketStatus,"Closed");
			

			Allure.step("/* No action can be done on ticket once ticket is closed */");
			handleInputFieldByJS("enterValue_HubSiteId_XPATH", "txtHubSite");
			click("click_On_Update_Ticket_XPATH");
			String ErrorText = getText("ErrorMessageOnTicketForm_XPATH");
			Assert.assertTrue(ErrorText.contains("Remarks cannot be blank"));
		}
	}

