package pages.iTower_Tawal;

import java.awt.Color;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
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

public class SiteActivityPage extends BasePage {
	SoftAssert sa = new SoftAssert();
	TroubleTicketingPage troubleTicket = new TroubleTicketingPage();
	String GetFistColumnValue = "";
	List<WebElement> AscendingOrder;
	List<WebElement> DescendingOrder;
	String path;
	public String OPCOSiteId;
	public String availableValue;
	public String getActivityId;
	public String text;

	// Landing on Site Ticket functionalities
	public void landingOnSiteActivityPage() throws InterruptedException {
		Allure.step("/* Check Site Activity page is opening properly */");
		String moduleName = getText("activity_Page_XPATH");
		if (moduleName.equalsIgnoreCase("Activity Report")) {
			// don't perform anything
		} else {
			handleClickByJS("insideOpen_XPATH");
			sa.assertEquals(getText("site_Activity_Module_XPATH"), "Site Activity");
			Allure.step("Check if site activity page is opening properly");
			handleClickByJS("site_Activity_Module_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		}

	}

	public void selectCheckBoxValueFromAddSiteActivityReport(String assignUserValue, String reviewUserValue) {
		click("select_Assign_User_XPATH");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//span[contains(text(),'" + assignUserValue
				+ "')]/preceding-sibling::input[@name='multiselect_ddlAssignUser'])[1]"))).click();
		closeTheOpenTab();
		click("select_Review_User_XPATH");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(),'" + reviewUserValue
				+ "')]/preceding-sibling::input[@name='multiselect_ddlRwAssignUser']"))).click();
		closeTheOpenTab();
	}

	// add new Activity
	public void addActivityReport(String addSiteID, String selectActivityType, String selectAssignGroup,
			String selectReviewGroup, String assignUserValue, String reviewUserValue)
			throws InterruptedException, TimeoutException {

		Allure.step("/* User is schedule the new site manually */");

		handleClickByJS("add_Activity_Button_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_Id_On_Activity_XPATH", addSiteID);
		clickOnSuggestedValue();
		

		
		// New site is scheduled
		click("select_Activity_Type_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Activity_Type_XPATH", selectActivityType);
		click("select_Assign_Group_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Assign_Group_XPATH", selectAssignGroup);
		click("select_Review_Group_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Review_Group_XPATH", selectReviewGroup);
		selectCheckBoxValueFromAddSiteActivityReport(assignUserValue, reviewUserValue);
		explicitWaitWithClickable("schedule_Date_ID");
		selectTodayDate(0);
		explicitWaitWithClickable("review_Plan_Date_ID");
		selectTodayDate(0);
		explicitWaitWithClickable("due_Date_ID");
		selectTodayDate(0);
		click("schedule_Add_Activity_ID");
		// get message after clicking on schedule button
		String ErrorMessage = getText("error_Message_XPATH");
		// Handle error messsage if Ticket(s) already exists or Invalid Site Id
		if (ErrorMessage.equalsIgnoreCase("Activity is already schedule for this date.")) {
			click("close_Form_XPATH");
			Assert.assertEquals(ErrorMessage, "");
		}
		click("close_Form_XPATH");

	}

	public void deleteActivity() throws InterruptedException {
		click("show_Hide_Filters_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", "ZAC200000099");
		click("filter_Report_ID");
		click("delete_Activity_ID");
		acceptTheAlert();
		typesIntoAnAlert("Activity Deleted");
		acceptTheAlert();
		acceptTheAlert();
		refreshPage();
		click("show_Hide_Filters_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", "ZAC200000099");
		click("filter_Report_ID");
		Assert.assertEquals(getText("no_Activity_Data_Found_XPATH"), "Sorry, No Results Found");
	}

	public void addActivityReporBulkUpload(String addSiteID) throws InterruptedException, TimeoutException {
		click("activity_Report_Bulk_Upload_Button_ID");
		handleIframe("iframe_Activity_Rpt_Bulk_Upload_ID");
		uploadTheCSVFile("PM Bulk Upload Add");
		click("activity_Report_Upload_ID");
		try {
			click("error_Records_Export_Grid_ActRpt_XPATH");
			// Add file renaming logic here
			renameDownloadedFile("ActivityBulkUpload", "ErrorAddSiteActivityBulkData");
			verifyUsingAssertFileIsExistInLocation("ErrorAddSiteActivityBulkData");

		} catch (Exception e) {
			// do nothing if no data is going to error
		}
		String NoRecorduploadedErrorMessage = getText("availableRecords_XPATH");
		if (NoRecorduploadedErrorMessage.equalsIgnoreCase("No record uploaded.")) {
			Allure.step("Verify that the error message is 'No record uploaded.'", () -> {
				Assert.assertNotEquals(NoRecorduploadedErrorMessage, "No record uploaded.");
			});
		}
		driver.switchTo().defaultContent();
		handleClickByJS("closeFrame_XPATH");
	/*	Thread.sleep(2000);
		handleClickByJS("filter_Report_ID");
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", addSiteID);
		handleClickByJS("filter_Report_ID");
		Thread.sleep(3000);
		getActivityId = getText("get_Activity_Id_XPATH").trim();
		click("export_Activity_Report_XPATH");
		renameDownloadedFile("ActivityReport_", "AddBulkUploadActivityReport_");
		verifyUsingAssertFileIsExistInLocation("AddBulkUploadActivityReport_");*/
	}

	public void updateActivityReporBulkUpload(String addSiteID) throws InterruptedException, TimeoutException {
		Thread.sleep(3000);
		click("activity_Report_Bulk_Upload_Button_ID");
		handleIframe("iframe_Activity_Rpt_Bulk_Upload_ID");
		uploadTheCSVFile("PM Bulk Upload Update");
		click("activity_Report_Upload_ID");
		try {
			click("error_Records_Export_Grid_ActRpt_XPATH");
			// Add file renaming logic here
			renameDownloadedFile("ActivityBulkUpload", "ErrorUpdateSiteActivityBulkData");
			verifyUsingAssertFileIsExistInLocation("ErrorUpdateSiteActivityBulkData");

		} catch (Exception e) {
			// do nothing if no data is going to error
		}
		String NoRecorduploadedErrorMessage = getText("availableRecords_XPATH");
		if (NoRecorduploadedErrorMessage.equalsIgnoreCase("No record uploaded.")) {
			Allure.step("Verify that the error message is 'No record uploaded.'", () -> {
				Assert.assertNotEquals(NoRecorduploadedErrorMessage, "No record uploaded.");
			});
		}
		driver.switchTo().defaultContent();
		handleClickByJS("closeFrame_XPATH");
		Thread.sleep(2000);
		handleClickByJS("filter_Report_ID");
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", addSiteID);
		handleClickByJS("filter_Report_ID");
		getActivityId = getText("get_Activity_Id_XPATH").trim();
		handleInputFieldByJS("fill_Activity_Id_XPATH", getActivityId);
		click("export_Activity_Report_XPATH");
		renameDownloadedFile("ActivityReport_", "UpdateBulkUploadActivityReport_");
		verifyUsingAssertFileIsExistInLocation("UpdateBulkUploadActivityReport_");
	}

	public void deleteActivityReporBulkUpload(String addSiteID) throws InterruptedException, TimeoutException {
		Thread.sleep(3000);
		click("activity_Report_Bulk_Upload_Button_ID");
		handleIframe("iframe_Activity_Rpt_Bulk_Upload_ID");
		uploadTheCSVFile("PM Bulk Upload Delete");
		click("activity_Report_Upload_ID");
		try {
			click("error_Records_Export_Grid_ActRpt_XPATH");
			// Add file renaming logic here
			renameDownloadedFile("ActivityBulkUpload", "ErrorDeleteSiteActivityBulkData");
			verifyUsingAssertFileIsExistInLocation("ErrorDeleteSiteActivityBulkData");

		} catch (Exception e) {
			// do nothing if no data is going to error
		}
		String NoRecorduploadedErrorMessage = getText("availableRecords_XPATH");
		if (NoRecorduploadedErrorMessage.equalsIgnoreCase("No record uploaded.")) {
			Allure.step("Verify that the error message is 'No record uploaded.'", () -> {
				Assert.assertNotEquals(NoRecorduploadedErrorMessage, "No record uploaded.");
			});
		}
		driver.switchTo().defaultContent();
		handleClickByJS("closeFrame_XPATH");
		Thread.sleep(2000);
		handleClickByJS("filter_Report_ID");
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", addSiteID);
		handleClickByJS("filter_Report_ID");
		Thread.sleep(1000);
		Assert.assertEquals(getText("no_Activity_Data_Found_XPATH"), "Sorry, No Results Found");
	}

	public String downloadFileAndRename() throws TimeoutException {
		deleteOldFilesFromLocation("agGridComparisonFile");
		click("export_Activity_Report_XPATH");
		String renamedFile = renameDownloadedFileBY("ActivityReport_", "activityComparisonFile");
		return renamedFile;
	}

	// Apply filter scheduled Activity
	public void filterScheduledActivity(String siteID) throws InterruptedException, TimeoutException {
		click("scheduled_Activities_XPATH");
		handleInputFieldByJS("site_ID_On_Scheduled_Filter_XPATH", siteID);
		handleClickByJS("scheduled_Activity_Filter_Report_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
	
		// Create the formatter
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

		// Format a date (e.g., current date or a specific date)
		LocalDate currentDate = LocalDate.now();
		String formattedDate = formatter.format(currentDate).toUpperCase();

		Allure.step("/* Assigned user should able to click on S button on scheduled tab */");
		wait.until(ExpectedConditions
		    .elementToBeClickable(By.xpath("//a[contains(@onclick,'" + formattedDate + "') and text()='S']")))
		    .click();
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		String text = null;
		try {
			text = getAlertText();
		} catch (Exception e) {
			// No alert present, text remains null
		}

		// Only check the text if it's not null
		if (text != null && text.contains("You are not authorized")) {
			acceptTheAlert();
			Assert.fail("You are not authorized to done this activity.");
		}
	}

	// Filter Activity Report
	public void filterActivityReport(String siteID) throws InterruptedException, TimeoutException {
		click("show_Hide_Filters_XPATH");
		Thread.sleep(1000);
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", siteID);
		click("filter_Report_ID");
		handleClickByJS("done_Activity_Link_XPATH");

	}

	public void uploadMediaAndCheckListForDoneActivity(String folderName,String checklistForDone) throws Exception {
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Allure.step("/* Images Instructions should get downloaded */");
		handleClickByJS("download_Media_Name_Instruction_XPATH");
		verifyUsingAssertFileIsExistInLocation("MediaUploadInstruction_");

		Allure.step("/* User should able to upload the required image */");
		uploadAllPNGFilesFromFolder(folderName, "media_Choose_Files_XPATH");
		handleClickByJS("upload_Media_XPATH");
		String texts = getAlertText();
		Allure.step(texts);
		Assert.assertTrue(texts.contains("File has been uploaded"));
		acceptTheAlert();
	    Thread.sleep(1000);

		Allure.step("/* User should able to download the checklist */");
		handleClickByJS("download_Checklist_XPATH");
		verifyUsingAssertFileIsExistInLocation("ActivityInspection_");

		Allure.step("/* User should able to upload filled checklist */");
		uploadTheExcelFileFromUploadLocation("checkList_Choose_Files_XPATH", checklistForDone);
		handleClickByJS("upload_Checklist_XPATH");
		Assert.assertTrue(getText("serviceType_C_Done_XPATH").contains("Cooling System PMR done."));
		Thread.sleep(1000);
		click("close_Poppup_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
	}

	// Once checklist is uploaded activity should get done and status should get
	// updated to D
	public void verifyStatusOfActivityAfterDone() {
		Allure.step("/* Once checklist is uploaded activity should get done and status should get updated to D */");
		handleClickByJS("filter_Report_ID");
		Assert.assertEquals(getText("verify_Status_XPATH"), "D");
	}

	public void switchToAssignUser() {
		click("click_For_Logout_XPATH");
		click("sign_Out_XPATH");
		click("click_On_LogOut_XPATH");
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("password")).clear();

	}

	public void switchToOnmModule() throws InterruptedException {
		Thread.sleep(3000);
		click("cilck_On_Error_Block_XPATH");
		click("click_On_Nine_Dot_XPATH");
		click("click_On_ONM_Module_XPATH");
	}

	public void filterScheduleActivity() throws InterruptedException, TimeoutException {
		landingOnSiteActivityPage();
		handleClickByJS("done_Link_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
	}

	public void verifyImagesAreVisibleAlongWithImageTags() throws Exception {
		handleIframe("handle_Frame_Activity_Report_XPATH");
		Allure.step("/* User should able to export checklist in Pdf and in Excel */");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleClickByJS("download_Filled_Checklist_XPATH");
		handleClickByJS("download_Checklist_In_PDF_Format_XPATH");
		Allure.step("/* Images should be visible on D button along with Image Tags */");
		try {

			// Find all image elements in the modal with more specific XPath
			List<WebElement> images = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(
					"//table[@id=\"tblChecklistPhoto\"]//div[@class='tblShowingSite imgGrid']//td//img[@id=\"imgShow\"]")));
			Allure.step("Total images found: " + images.size());
			if (images.isEmpty()) {
				Allure.step("WARNING: No images found");
				takeScreenshot(driver, "no_images_found");
				return;
			}

			// Verify each image with metadata
			for (int i = 0; i < images.size(); i++) {
				try {
					// Refresh reference to avoid stale elements
					WebElement img = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(

							By.xpath(
									"//table[@id=\"tblChecklistPhoto\"]//div[@class='tblShowingSite imgGrid']//td//img[@id=\"imgShow\"]")))
							.get(i);
					verifyImageWithMetadata(driver, img);
				} catch (Exception e) {
					Allure.step("ERROR processing image: " + e.getMessage());
					takeScreenshot(driver, "image_error_" + i);
				}
			}
		} catch (Exception e) {
			Allure.step("ERROR in Checkalltheimagesareshowingfineonweb: " + e.getMessage());
			takeScreenshot(driver, "image_verification_error");
			throw e;
		}

		switchToDefaultContentFromIframe();
		click("closeFrame_XPATH");
	}

	private void verifyImageWithMetadata(WebDriver driver, WebElement img) throws TimeoutException {
		try {
			// Scroll to the image
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", img);

			// Wait for visibility
			wait.until(ExpectedConditions.visibilityOf(img));

			// Check if image is broken
			boolean isImageBroken = (Boolean) ((JavascriptExecutor) driver).executeScript(
					"return arguments[0].complete && " + "typeof arguments[0].naturalWidth != 'undefined' && "
							+ "arguments[0].naturalWidth > 0",
					img);

			if (!isImageBroken) {
				Allure.step("FAIL: Image is broken (width=0 or failed to load)");
				takeScreenshot(driver, "broken_image_" + System.currentTimeMillis());
				sa.fail("Image is broken: " + img.getDomProperty("src"));
			}

			Allure.step("\n=== Verifying Image ===");
			Allure.step("Image Source: " + img.getDomProperty("src"));
			Allure.step("Image loaded successfully");
			Allure.step("Dimensions: " + img.getSize().getWidth() + "x" + img.getSize().getHeight());

			// Find the parent container of the image
			WebElement parentRow = (WebElement) ((JavascriptExecutor) driver)
					.executeScript("return arguments[0].closest('tr')", img);

			if (parentRow == null) {
				Allure.step("WARNING: Could not find parent row for image");
				return;
			}

			// Get all metadata from following siblings
			List<WebElement> metadataRows = parentRow.findElements(By.xpath(
					"./following-sibling::tr[position() <= 3][td[contains(.,'Lat/Long') or contains(.,'Tag') or contains(.,'Time')]]"));

			Map<String, String> metadata = new HashMap<>();
			for (WebElement row : metadataRows) {
				String rowText = row.getText();
				if (rowText.contains("Lat/Long") && !metadata.containsKey("latLong")) {
					metadata.put("latLong", extractValue(rowText, "Lat/Long"));
				}
				if (rowText.contains("Tag") && !metadata.containsKey("tag")) {
					metadata.put("tag", extractValue(rowText, "Tag"));
				}
				if (rowText.contains("Time") && !metadata.containsKey("time")) {
					metadata.put("time", extractValue(rowText, "Time"));
				}
				if (metadata.size() == 3)
					break;
			}

			// Log metadata to Allure report
			Allure.step("\n=== Image Metadata ===");
			Allure.step("Latitude/Longitude: " + metadata.getOrDefault("latLong", "Not found"));
			Allure.step("Tag: " + metadata.getOrDefault("tag", "Not found"));
			Allure.step("Time: " + metadata.getOrDefault("time", "Not found"));

			// Assert that metadata exists (optional)
			if (metadata.isEmpty()) {
				Allure.step("WARNING: No metadata found for image");
				takeScreenshot(driver, "missing_metadata_" + System.currentTimeMillis());
			}

		} catch (Exception e) {
			Allure.step("ERROR during verification: " + e.getMessage());
			takeScreenshot(driver, "image_verification_error_" + System.currentTimeMillis());
			Assert.fail("Image verification error: " + e.getMessage());
		}
		Allure.step("----------------------");
	}

	public String getMetadataValue(WebDriver driver, WebElement contextElement, String fieldName) {
		try {
			// First try to find the metadata in the current row
			try {
				WebElement fieldElement = contextElement
						.findElement(By.xpath(".//td[contains(., '" + fieldName + "')]"));
				String fullText = fieldElement.getText();
				return extractValue(fullText, fieldName);
			} catch (NoSuchElementException e) {
				// If not found in current row, look in following siblings
				WebElement siblingRow = contextElement
						.findElement(By.xpath("./following-sibling::tr[td[contains(., '" + fieldName + "')]][1]"));
				String fullText = siblingRow.getText();
				return extractValue(fullText, fieldName);
			}
		} catch (Exception e) {
			Allure.step("Could not find " + fieldName + " metadata");
			return null;
		}
	}

	private String extractValue(String text, String fieldName) {
		// Improved pattern to handle different formats
		Pattern pattern = Pattern.compile(fieldName + "\\s*:\\s*([^\\n\"]+)");
		java.util.regex.Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group(1).trim() : null;
	}

	public static void scrollToElement(WebDriver driver, WebElement element) {
		try {
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
			Thread.sleep(500);
		} catch (Exception e) {
			Allure.step("Error while scrolling: " + e.getMessage());
		}
	}

	private void takeScreenshot(WebDriver driver, String screenshotName) {
		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src,
					new File("screenshots/" + screenshotName + "_" + System.currentTimeMillis() + ".png"));
		} catch (Exception e) {
			Allure.step("Failed to take screenshot: " + e.getMessage());
		}
	}

	public void distanceDoneOnPMActivity(String siteID, String activityID)
			throws TimeoutException, InterruptedException {
		click("showHide_Filters_XPATH");
		click("click_ActivityStatusFilter_XPATH");
		click("select_DoneActivity_XPATH");
		enterTextIntoInputBox("enter_SiteID_XPATH", siteID);
		enterTextIntoInputBox("enter_ActivityID_XPATH", activityID);
		handleCalenderForSiteActivity();
		click("click_FilterReport_XPATH");
		rightScrolling();
		getTextDistance();
		deleteOldFilesFromLocation("DistanceValueForDoneActivityFile");
		click("activityReport_export_XPATH");
		Thread.sleep(3000);
		renameDownloadedFile("ActivityReport_", "DistanceValueForDoneActivityFile");
		verifyUsingAssertFileIsExistInLocation("DistanceValueForDoneActivityFile");
	}

	public void rightScrolling() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		for (int i = 0; i < 60; i++) {
			js.executeScript("document.querySelector('.ag-body-viewport').scrollLeft += 20;");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void getTextDistance() {

		String value = getText("distanceValue_XPATH");

		if (value.equals("")) {
			Allure.step("The value of distance is coming as blank");
		}
		Allure.step("Distance is: " + value);
	}

	// handle pagination functionalities On Site Activity page
	public void paginationOnSiteActivity(String columnName, String RenameFileName)
			throws InterruptedException, TimeoutException, FileNotFoundException, IOException {
		String MaxPageCount = getText("siteActivity_PageCount_XPATH");
		int pageCount = Integer.parseInt(MaxPageCount);
		int totalDataCountOnSite = 0;
		for (int i = 1; i <= pageCount; i++) {
			String DataOnEveryPage = getText("siteActivity_TotalRecords_ID");
			int dataOnEveryPage = Integer.parseInt(DataOnEveryPage);
			totalDataCountOnSite += dataOnEveryPage;
			click("siteActivity_nextButtonOnPagination_XPATH");
		}
		// Allure.step("Total Pages count : " + pageCount, Status.PASS);
		// Allure.step("Total data in grid UI : " + totalDataCountOnSite, Status.PASS);
		for (int i = 1; i <= pageCount - 1; i++) {
			click("siteActivity_prevButtonOnPagination_XPATH");
			Thread.sleep(2000);
		}
		click("siteActivity_rightButtonOnPagination_XPATH");
		String PageCountAfterClickOndoubleRightButton = getText(
				"siteActivity_PageCountAfterClickOndblRightButton_XPATH");
		int siteActivity_PageCountAfterClickOndblRightButton_XPATH = Integer
				.parseInt(PageCountAfterClickOndoubleRightButton);

		deleteOldFilesFromLocation("ShowAllPMActivityFile");
		// Activity report Paging should be working fine
		click("activityReport_export_XPATH");
		Thread.sleep(3000);
		renameDownloadedFile("ActivityReport_", "ShowAllPMActivityFile");
		verifyUsingAssertFileIsExistInLocation("ShowAllPMActivityFile");
		Thread.sleep(3000);

	}

	public void openDoneActivity(String siteId) throws InterruptedException, TimeoutException {
		click("scheduledActivityTab_XPATH");
		enterTextIntoInputBox("site_ID_On_Scheduled_Filter_XPATH", siteId);
		handleClickByJS("click_SelectActivity_XPATH");
		click("select_SelectActivityDropdownValue_XPATH");
		handleClickByJS("scheduled_Activity_Filter_Report_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		// Create the formatter
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

				// Format a date (e.g., current date or a specific date)
				LocalDate currentDate = LocalDate.now();
				String formattedDate = formatter.format(currentDate).toUpperCase();

				Allure.step("/* Assigned user should able to click on S button on scheduled tab */");
				wait.until(ExpectedConditions
				    .elementToBeClickable(By.xpath("//a[contains(@onclick,'" + formattedDate + "') and text()='D']")))
				    .click();
				explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		String text = null;
		try {
			text = getAlertText();
		} catch (Exception e) {
			// No alert present, text remains null
		}

		// Only check the text if it's not null
		if (text != null && text.contains("You are not authorized")) {
			acceptTheAlert();
			Assert.fail("You are not authorized to done this activity.");
		}
		
	}

	public void activityApproveDirectly(String selectProperPhotosAndInformation, String selectQualityPMTask,
			String selectVisualCheck1, String selectVisualCheck2, String selectVisualCheck3)
			throws InterruptedException {
		handleClickByJS("click_ApprovedChecklist_XPATH");
		click("direct_Approve_XPATH");
		//explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		// Once activity is done Assigned reviewer should able to Verify the activity
		// Reviewer should able to approve the activity
		// User should have rating option while approving the activity either by upload
	/*	handleClickByJS("select_dropdownProperPhotos_XPATH");
		handleListOfWebelementAndEnterValue("click_ProperPhotosAndInformation_XPATH", selectProperPhotosAndInformation);
		handleClickByJS("select_QualityPMTask_XPATH");
		handleListOfWebelementAndEnterValue("click_QualityPMTask_XPATH", selectQualityPMTask);
		handleClickByJS("select_VisualCheck1_XPATH");
		handleListOfWebelementAndEnterValue("click_VisualCheck1_XPATH", selectVisualCheck1);
		handleClickByJS("select_VisualCheck2_XPATH");
		handleListOfWebelementAndEnterValue("click_VisualCheck2_XPATH", selectVisualCheck2);
		handleClickByJS("select_VisualCheck3_XPATH");
		handleListOfWebelementAndEnterValue("click_VisualCheck3_XPATH", selectVisualCheck3);

		// directly approve
		// Reviewer should able to directly approve checklist through direct approve
		// option
		handleClickByJS("click_DirectApproved_XPATH");
		getAlertText();
		dismissTheAlert();*/
		Assert.assertTrue(getText("serviceType_C_Done_XPATH").contains("Acitivity has been reviewed successfully"));
		handleClickByJS("closePage_XPATH");
	}

	// Filter and export data
	public void filterAndExportData(String siteID) throws InterruptedException, TimeoutException {
		handleClickByJS("closePage_XPATH");
		handleClickByJS("activityReportTab_XPATH");
		handleClickByJS("activityReport_ShowHideFilter_XPATH");
		enterTextIntoInputBox("enter_SiteID_ActivityPage_XPATH", siteID);
		handleClickByJS("activity_Filter_Report_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Allure.step(
				"If there is any access request for the PM ticket access request number, status, actual start and end date should be visible on edit form and activity report");
		getText("getActivityId_XPATH");
		handleClickByJS("export_Activity_Report_XPATH");
		renameDownloadedFile("ActivityReport_", "directApproveActivityFile");
		verifyUsingAssertFileIsExistInLocation("directApproveActivityFile");
	}

	public void verifyApproveActivity(String verifyChecklistFile) throws InterruptedException {
		Thread.sleep(3000);
		uploadTheExcelFileFromUploadLocation("upload_Checklist_For_Approved_XPATH",
				verifyChecklistFile);
		handleClickByJS("click_ApprovedChecklist_XPATH");
		Assert.assertTrue(getText("activityApprovedMessage_XPATH").contains("Cooling System PMR verified."));
		handleClickByJS("closePage_XPATH");}
	

	public void activityRejectDirectly(String siteId, String selectRejectionReasonCategory)
			throws InterruptedException, TimeoutException {
		// User should able to reject the activity
		// Once activity is done Assigned reviewer should able to Reject the activity
		handleClickByJS("click_RejectedChecklist_XPATH");
		handleClickByJS("click_Category_XPATH");
		handleListOfWebelementAndEnterValue("click_CategoryDropdownValue_XPATH", selectRejectionReasonCategory);
		// Rejection remarks should be mandatory while rejecting the activity
		enterTextIntoInputBox("enter_Remarks_XPATH", "Reject Checklist");
		handleClickByJS("click_DirectRejected_XPATH");
		getAlertText();
		acceptTheAlert();
		acceptTheAlert();
		handleClickByJS("closePage_XPATH");

	}

	public void rejectFromChooseFile(String siteId, String selectRejectionReasonCategoryChooseFile,
			String remarksForRejection) throws InterruptedException, TimeoutException {
		handleClickByJS("click_RejectedChecklist_XPATH");
		handleClickByJS("click_Category_XPATH");
		
		handleListOfWebelementAndEnterValue("click_CategoryDropdownValue_XPATH",
				selectRejectionReasonCategoryChooseFile);
		handleInputFieldByJS("enter_Remarks_XPATH", remarksForRejection);
		handleClickByJS("click_SubmitButton_XPATH");
		acceptTheAlert();
		Assert.assertTrue(getText("activityApprovedMessage_XPATH").contains("Cooling System PMR Rejected."));
		handleClickByJS("closePage_XPATH");
	}

	// Rejected PM can be rescheduled and can be done and review
	public void rescheduleSiteActivity(String siteID, String selectActivityStatus, String selectActivityType,
			String assignUserValue) throws InterruptedException {
		click("showHide_Filters_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteID);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", selectActivityStatus);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		handleClickByJS("click_ButtonFilterActivityReport_XPATH");
		handleClickByJS("click_EditIcon_XPATH");
		Thread.sleep(1000);
		// User should able to reschedule the activity
		handleClickByJS("click_RescheduleButton_XPATH");
		Thread.sleep(1000);
		Assert.assertEquals(getText("updateActivity_Message_XPATH"), "Record has been Updated Successfully.");
		handleClickByJS("click_CloseButton_XPATH");
		refreshPage();
		explicitWaitWithClickable("activity_Report_XPATH");
		click("showHide_Filters_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteID);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", "Rescheduled");
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		handleClickByJS("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		Assert.assertEquals(getText("get_Reschedule_Status_XPATH"), "RS");
		getText("count_Reschedule_Status_XPATH");

	}

	public void exportCheckilistInPDFAndExcelFormatForDoneStatus(String siteIDDone, String ActivityIDDone,
			String selectActivityStatusDone, String selectActivityType, String assignUserValue)
			throws InterruptedException {
		handleActivityReportCalender();
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteIDDone);
		enterTextIntoInputBox("enter_ActivityID_XPATH", ActivityIDDone);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", selectActivityStatusDone);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		click("click_DoneStatus_XPATH");
		Thread.sleep(2000);

	}

	public void handleActivityReportCalender() throws InterruptedException {

		explicitWaitWithClickable("click_Calender_XPATH");
		for (int i = 1; i <= 1; i++) {
			explicitWaitWithClickable("backMonthOnCalenderActivityReport_XPATH");
			Thread.sleep(1000);
			handleClickByJS("calendarDate_XPATH");
		}
	}

	public void handleIFrameOFSiteActivityReport() throws InterruptedException, TimeoutException {
		driver.switchTo().frame(0);
		handleIframe("iFrameDownloadActivityReportSite_Xpath");
		explicitWaitWithClickable("click_DownloadFilledChecklist_XPATH");
		Thread.sleep(2000);
		explicitWaitWithClickable("click_DownloadChecklistInPDFFormat_XPATH");
		Thread.sleep(2000);
		driver.switchTo().defaultContent();
		handleClickByJS("close_iFrame_XPATH");
		Thread.sleep(2000);
		refreshPage();
	}

	public void exportCheckilistInPDFAndExcelFormatForRejectStatus(String siteIDReject, String ActivityIDReject,
			String selectActivityStatusReject, String selectActivityType, String assignUserValue)
			throws InterruptedException {
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteIDReject);
		enterTextIntoInputBox("enter_ActivityID_XPATH", ActivityIDReject);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", selectActivityStatusReject);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		click("click_RejectStatus_XPATH");
		Thread.sleep(2000);
	}

	public void exportCheckilistInPDFAndExcelFormatForCloseStatus(String siteIDClose, String ActivityIDClose,
			String selectActivityStatusClose, String selectActivityType, String assignUserValue)
			throws InterruptedException {
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteIDClose);
		enterTextIntoInputBox("enter_ActivityID_XPATH", ActivityIDClose);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", selectActivityStatusClose);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", "Test 2504");
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		click("click_CloseStatus_XPATH");
		Thread.sleep(2000);
	}

	public void exportCheckilistInPDFAndExcelFormatForRescheduleStatus(String siteIDReschedule,
			String ActivityIDReschedule, String selectActivityStatusReschedule, String selectActivityType,
			String assignUserValue) throws InterruptedException {
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteIDReschedule);
		enterTextIntoInputBox("enter_ActivityID_XPATH", ActivityIDReschedule);
		click("click_AcivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH",
				selectActivityStatusReschedule);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		click("click_RescheduleStatus_XPATH");
		Thread.sleep(2000);
	}

	// Verify shorting should be working fine in activity report

	// get site id value in descending Order
	public void changesiteIdTodescendingOrderActivityReport() {
		DescendingOrder = clickOnListOfWebelement("siteIdOnActivityReportPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get activity id value in descending Order
	public void changeActivityIdTodescendingOrderActivityReport() {
		DescendingOrder = clickOnListOfWebelement("activityIdOnActivityReportPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get technician value in descending Order
	public void changeTechnicianTodescendingOrderActivityReport() {
		DescendingOrder = clickOnListOfWebelement("technicianOnActivityReportPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get status value in descending Order
	public void changeStatusTodescendingOrderActivityReport() {
		DescendingOrder = clickOnListOfWebelement("statusOnActivityReportPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	// get status value in descending Order
	public void changeScheduleDateTodescendingOrderActivityReport() {
		DescendingOrder = clickOnListOfWebelement("scheduleDateOnActivityReportPage_XPATH");
		WebElement descendingValue = DescendingOrder.get(0);
		GetFistColumnValue = descendingValue.getText();
	}

	public void verifyDescendingOrderActivityReport() {
		// Verify descending order for column value
		Allure.step("Descending Order -->");
		int loopcountDecending = 0;
		for (int i = 0; i < DescendingOrder.size() - 1; i++) {
			String currentString = DescendingOrder.get(i).getText().trim();
			String nextString = DescendingOrder.get(i + 1).getText().trim();

			// Compare strings directly
			int comparison = currentString.compareToIgnoreCase(nextString);

			loopcountDecending++;
			if (loopcountDecending == 1) {
				break;
			}
		}
	}

	// get site Id value in Ascending Order
	public void changeSiteIdToAscendingOrderActivityReport() throws InterruptedException {
		// Change sorting to ascending
		click("siteIdText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("siteIdOnActivityReportPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get activity Id value in Ascending Order
	public void changeActivityIdToAscendingOrderActivityReport() throws InterruptedException {
		// Change sorting to ascending
		click("activityIdText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("activityIdOnActivityReportPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get technician value in Ascending Order
	public void changeTechnicianToAscendingOrderActivityReport() throws InterruptedException {
		// Change sorting to ascending
		click("technicianText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("technicianOnActivityReportPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get status value in Ascending Order
	public void changeStatusToAscendingOrderActivityReport() throws InterruptedException {
		// Change sorting to ascending
		click("statusText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("statusOnActivityReportPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	// get schedule Date value in Ascending Order
	public void changeScheduleDatToAscendingOrderActivityReport() throws InterruptedException {
		// Change sorting to ascending
		click("scheduleDateText_XPATH");
		// Verify sorting changed to ascending
		AscendingOrder = clickOnListOfWebelement("scheduleDateOnActivityReportPage_XPATH");
		WebElement value = AscendingOrder.get(0);
		String GetFistAscendingOrdervalue = value.getText();
		Allure.step("column value Sorted in Ascending order", () -> {
			sa.assertNotEquals(GetFistColumnValue, GetFistAscendingOrdervalue,
					"column value Sorted in Ascending order");
		});
	}

	public void verifyAscendingOrderOfActivityReport() {
		// Verify ascending order for strings
		Allure.step("Ascending Order -->");
		int loopcountAscending = 0;
		for (int i = 0; i < AscendingOrder.size() - 1; i++) {
			String currentString = AscendingOrder.get(i).getText().trim();
			String nextString = AscendingOrder.get(i + 1).getText().trim();

			// Compare strings directly
			int comparison = currentString.compareToIgnoreCase(nextString);
			loopcountAscending++;
			if (loopcountAscending == 1) {
				break;
			}

		}
	}

	/*
	 * User should able to edit the PM and update the assignee of PM by clicking on
	 * edit button
	 */
	public void performEditActivity(String siteID, String selectActivityType, String assignUserValue,
			String selectAssignGroup, String selectReviewGroup, String updatedAssignUserValue, String reviewUserValue)
			throws InterruptedException, TimeoutException {
		Allure.step("/* User should able to edit the PM and update the assignee of PM by clicking on edit button */");
		explicitWaitWithClickable("show_Hide_Filters_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteID);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		updateDropdownValues(selectAssignGroup, selectReviewGroup, updatedAssignUserValue, reviewUserValue);
	}

	public void updateDropdownValues(String selectAssignGroup, String selectReviewGroup, String updatedAssignUserValue,
			String reviewUserValue) throws TimeoutException, InterruptedException {
		handleClickByJS("click_EditButton_XPATH");
		handleClickByJS("edit_Assign_Group_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Assign_Group_XPATH", selectAssignGroup);
		click("edit_Review_Group_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Review_Group_XPATH", selectReviewGroup);
		handleClickByJS("select_Assign_User_XPATH");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(),'"
				+ updatedAssignUserValue + "')]/preceding-sibling::input[@name='multiselect_ddlAssignUser']"))).click();
		closeTheOpenTab();
		handleClickByJS("select_Review_User_XPATH");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(),'" + reviewUserValue
				+ "')]/preceding-sibling::input[@name='multiselect_ddlRwAssignUser']"))).click();
		closeTheOpenTab();
		handleClickByJS("click_UpadeButton_XPATH");
		Assert.assertEquals(getText("updateActivity_Message_XPATH"), "Record has been Updated Successfully.");
		handleClickByJS("click_CloseButton_XPATH");
		explicitWaitWithClickable("show_Hide_Filters_XPATH");
		clearTextFromInputBox("technician_Name_XPATH", 1);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", updatedAssignUserValue);
		click("filter_Report_ID");
		Thread.sleep(2000);
		click("export_Activity_Report_XPATH");
		renameDownloadedFile("ActivityReport_", "updatedActivityFile");
		verifyUsingAssertFileIsExistInLocation("updatedActivityFile");
	}

	// Delete scheduled PM Activity
	public void deletedScheduleActivity(String siteID, String selectActivityType, String assignUserValue,
			String selectActivityStatus, String remarks) throws InterruptedException {
		click("show_Hide_Filters_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_ID_On_Actity_Filter_XPATH", siteID);
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		enterTextIntoInputBoxUsingActionsClass("technician_Name_XPATH", assignUserValue);
		click("click_ActivityStatusFilter_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_ActivityStatusFilter_XPATH", selectActivityStatus);
		handleClickByJS("click_ButtonFilterActivityReport_XPATH");
		Thread.sleep(2000);
		click("delete_Activity_ID");
		acceptTheAlert();
		acceptTheAlert();
	}

	public void writeData(String ticketType) throws IOException, CsvException {
		String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
		String path = userDir + "\\src\\main\\resources\\iTower_Clients_Excel\\excel_Tawal\\" + ticketType
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

		// Ensure column 1 exists in row 2 (index 0 in zero-based)
		if (csvData.get(1).length <= 0) { // If row 2 has no columns, expand to 1 column
			String[] newRow = Arrays.copyOf(csvData.get(1), 1); // Expand to 1 column
			csvData.set(1, newRow);
		}

		// Modify data (row 2, column 1)
		csvData.get(1)[0] = getActivityId; // Zero-based: row 1 (2nd row), column 0 (1st column)

		// Write back
		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			writer.writeAll(csvData);
		}
	}

	public void handleCalenderForSiteActivity(String monthYearValue, String date) {
		click("click_Calender_XPATH");
	
			List<WebElement> monthYear = driver.findElements(By.className("ui-datepicker-title"));
		for(WebElement e  :monthYear) {
			String f = e.getText();
			if (e.equals(f)) {
				break;
			} else {
				driver.findElement(By.xpath("//span[text()='Prev']")).click();
			}
		}
		
		
		driver.findElement(By.xpath("//a[text()=" + date + "]")).click();
	}

	public void paginationOnSiteActivityReport()
			throws InterruptedException, TimeoutException, FileNotFoundException, IOException {
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		String MaxPageCount = getText("siteActivity_PageCount_XPATH");
		int pageCount = Integer.parseInt(MaxPageCount);
		

		for (int i = 1; i <= pageCount - 1; i++) {
			click("siteActivity_prevButtonOnPagination_XPATH");
			Thread.sleep(2000);
		}
		click("siteActivity_rightButtonOnPagination_XPATH");
		getText(
				"siteActivity_PageCountAfterClickOndblRightButton_XPATH");
		

		// Activity report Paging should be working fine
		click("activityReport_export_XPATH");
		renameDownloadedFile("ActivityReport_", "downloadedPaginationActivityReportFile");
		verifyUsingAssertFileIsExistInLocation("downloadedPaginationActivityReportFile");
		Thread.sleep(3000);
		readColumnData("Site Id", "downloadedPaginationActivityReportFile");

	}

	// sorting of Site Id column
	public void sortingOfSiteIdColumn() throws InterruptedException {
		click("show_Hide_Filters_XPATH");
		handleCalenderForSiteActivity("Jan", "1");
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		
		// Capture the first Site ID before sorting

		String beforeSortValue = driver.findElement(By.xpath("//span[@ng-mouseout='HoverOutSiteId();']")).getText();
		Allure.step("Before Sorting First value in Site Id column: " + beforeSortValue);

		// Click on the 'Site ID' column header to sort
		for (int i = 0; i < 2; i++) {
			click("click_SiteIdColumn_XPATH");
			Thread.sleep(1000);
		}

		// Capture the first Site ID after sorting
		String afterSortValue = driver.findElement(By.xpath("//span[@ng-mouseout='HoverOutSiteId();']")).getText();
		Allure.step("After Sorting Last value in Site Id column: " + afterSortValue);

		Allure.step("Verify first Site Id changes after the second click", () -> {
			Assert.assertNotEquals(beforeSortValue, afterSortValue,
					"First Site Id should change after clicking the header twice");
		});
	}

	// sorting of Site Name column
	public void sortingOfSiteNameColumn() throws InterruptedException {
		click("show_Hide_Filters_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleCalenderForSiteActivity("June 2025", "2");
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");

		// Capture the first Site ID before sorting
		click("click_SiteNameColumn_XPATH");
		String beforeSortValue = driver
				.findElement(By.xpath("//div[@class='ag-cell ag-cell-no-focus cell-col-3 ag-cell-value grid-center']"))
				.getText();
		Allure.step("Before Sorting First value in Site Name column: " + beforeSortValue);

		// Click on the 'Site ID' column header to sort
		click("click_SiteNameColumn_XPATH");
		Thread.sleep(1000);

		// Capture the first Site ID after sorting
		String afterSortValue = driver
				.findElement(By.xpath("//div[@class='ag-cell ag-cell-no-focus cell-col-3 ag-cell-value grid-center']"))
				.getText();
		Allure.step("After Sorting Last value in Site Name column: " + afterSortValue);

		Allure.step("Verify first Site Name changes after the second click", () -> {
			Assert.assertNotEquals(beforeSortValue, afterSortValue,
					"First Site Name should change after clicking the header twice");
		});
	}

	// sorting of Activity Id column
	public void sortingOfActivityIdColumn() throws InterruptedException {
		click("show_Hide_Filters_XPATH");
		handleCalenderForSiteActivity("Jan", "1");
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");

		// Capture the first Site ID before sorting
		click("click_ActivityIdColumn_XPATH");
		String beforeSortValue = driver.findElement(By.xpath("//span[@ng-mouseout='HoverOutActivityId();']")).getText();
		Allure.step("Before Sorting First value in Site Activity Id: " + beforeSortValue);

		// Click on the 'Site ID' column header to sort
		click("click_ActivityIdColumn_XPATH");
		Thread.sleep(1000);

		// Capture the first Site ID after sorting
		String afterSortValue = driver.findElement(By.xpath("//span[@ng-mouseout='HoverOutActivityId();']")).getText();
		Allure.step("After Sorting Last value in Activity Id column: " + afterSortValue);

		Allure.step("Verify first Activity ID changes after the second click", () -> {
			Assert.assertNotEquals(beforeSortValue, afterSortValue,
					"First Activity ID should change after clicking the header twice");
		});
	}

	int index = 2;

	// verify filters dropdown SPMM Region
	public void HandleDropdownSPMMRegion() throws InterruptedException {
		WebElement EnterKeywordValue = driver
				.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index);
		dynamicLocatorClick("allInputBox_XPATH", index);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", index, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
					InputBoxName);
			String DropDownValue = drpDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBoxSiteActivityDrp_XPATH", index);
			dynamicLocatorClick("allButton_XPATH", index);
			dynamicLocatorClick("noneButton_XPATH", index);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index, DropDownValue);
			EnterKeywordValue.clear();
			dynamicLocatorClick("noneButton_XPATH", index);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", index);
	}

	// verify filters dropdown SPMM Area
	public void HandleDropdownSPMMArea(int startIndex, int endIndex) throws InterruptedException {
		if (startIndex <= endIndex) {
			WebElement EnterKeywordValue = driver
					.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + startIndex + "]"));

			String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", startIndex);
			dynamicLocatorClick("allInputBox_XPATH", startIndex);

			Thread.sleep(2000);
			int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 1, InputBoxName);
			if (count >= 1) {
				List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
						InputBoxName);
				String DropDownValue = drpDownList.get(0).getText();
				dynamicLocatorClick("clickOnFirstCheckBoxSA_SPMM_Area_XPATH", startIndex);
				dynamicLocatorClick("allButton_XPATH", startIndex);
				dynamicLocatorClick("noneButton_XPATH", startIndex);
				dynamicLocatorSendKeys("enterKeywordValue_XPATH", startIndex, DropDownValue);
				EnterKeywordValue.clear();
				dynamicLocatorClick("noneButton_XPATH", startIndex);
				troubleTicket.handleDropDownList(drpDownList, DropDownValue);
			}
			dynamicLocatorClick("multiSelectClose_XPATH", startIndex);
			Thread.sleep(3000);
			startIndex++;
			HandleDropdownSPMMArea(startIndex, endIndex);
		}
	}

	// verify filters dropdown Select Vender
	public void HandleDropdownSelectActivity(int index, int minusIndex) throws InterruptedException {
		WebElement EnterKeywordValue = driver
				.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index);
		dynamicLocatorClick("allInputBox_XPATH", index);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 1, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
					InputBoxName);
			String DropDownValue = drpDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBoxSA_SPMM_Area_XPATH", index);
			dynamicLocatorClick("allButton_XPATH", index - minusIndex);
			dynamicLocatorClick("noneButton_XPATH", index - minusIndex);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index, DropDownValue);
			EnterKeywordValue.clear();
			dynamicLocatorClick("noneButton_XPATH", index - minusIndex);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", index);
	}

	// verify filters dropdown Select Vender
	public void HandleDropdownSelectActivity(int index, int minusIndex, int minusIndexForOtherItems)
			throws InterruptedException {
		WebElement EnterKeywordValue = driver
				.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + (index - 4) + "]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index);
		dynamicLocatorClick("allInputBox_XPATH", index);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 1, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
					InputBoxName);
			String DropDownValue = drpDownList.get(0).getText();
			dynamicLocatorClick("clickOnFirstCheckBoxSA_SPMM_Area_XPATH", index - minusIndexForOtherItems);
			dynamicLocatorClick("allButton_XPATH", index - minusIndex);
			dynamicLocatorClick("noneButton_XPATH", index - minusIndex);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index - minusIndexForOtherItems, DropDownValue);
			// EnterKeywordValue.click();
			EnterKeywordValue.clear();
			dynamicLocatorClick("noneButton_XPATH", index - minusIndex);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", index - minusIndexForOtherItems);
	}

	// verify filters dropdown Select Vender
	public void HandleDropdownSelectActivityCompleted(int index) throws InterruptedException {
		WebElement EnterKeywordValue = driver
				.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index);
		dynamicLocatorClick("allInputBox_XPATH", index);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 1, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
					InputBoxName);
			String DropDownValue = drpDownList.get(1).getText();
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index, DropDownValue);
			EnterKeywordValue.clear();
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
	}

	// verify filters dropdown Select Vender
	public void HandleDropdownWithoutCheckBoxes(int index, int addedIndex) throws InterruptedException {
		WebElement EnterKeywordValue = driver
				.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[" + index + "]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index + addedIndex);
		dynamicLocatorClick("allInputBox_XPATH", index + addedIndex);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 1, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 1,
					InputBoxName);
			String DropDownValue = drpDownList.get(1).getText();
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index, DropDownValue);
			EnterKeywordValue.clear();
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
	}

	public void setTechnicianName() {
		enterTextIntoInputBox("technicianName_ID", "Test Technician");
	}

	public void setCircleManager() {
		enterTextIntoInputBox("circleManager_ID", "Test Circle Manager");
	}

	public void setSiteId() {
		enterTextIntoInputBox("siteIdd_ID", "Test Site Id");
	}

	public void setActivityId() {
		enterTextIntoInputBox("activityId_ID", "Test Activity Id");
	}

	public void handleCalenderForSiteActivityFilter(String monthYearValue, String date) {
		// click("click_Calender_XPATH");
		while (true) {
			String monthYear = driver.findElement(By.className("ui-datepicker-title")).getText();

			if (monthYear.equals(monthYearValue)) {
				break;
			} else {
				driver.findElement(By.xpath("//span[text()='Prev']")).click();
			}
		}
		driver.findElement(By.xpath("//a[text()=" + date + "]")).click();

	}

	public void clickFirstCalendarInFilters() {
		driver.findElement(By.id("txtPMFdate")).click();
	}

	public void clickSecondCalendarInFilters() {
		driver.findElement(By.id("txtPMTdate")).click();
	}

	public void clickScheduledActivitiesTab() {
		driver.findElement(By.xpath("//span[normalize-space() = 'Scheduled Activities'][@class='ajax__tab_tab']"))
				.click();
	
	}

	// Check Select Province Filter functionality in Scheduled Activities
	public void handleSelectProvinceFilterScheduledActivities(int provinceIndex) throws InterruptedException {
		handleClickByJS("selectProvinceOfScheduledActivities_XPATH");
		click("centralValueSelectProvinceScheduledActivities_XPATH");
		click("allValueSelectProvinceScheduledActivities_XPATH");
		click("noneValueSelectProvinceScheduledActivities_XPATH");
		String SelectProvince = getTextFromInputBox("enterkeywordsValueSelectProvinceScheduledActivities_XPATH",
				"Eastern");
		Allure.step("Verify that value equals 'Eastern'", () -> {
			Assert.assertEquals("Eastern", SelectProvince);
		});
		click("EasternValueSelectProvinceScheduledActivities_XPATH");
		clearTextFromInputBox("enterkeywordsValueSelectProvinceScheduledActivities_XPATH", 21);
		Thread.sleep(1000);
		click("noneValueSelectProvinceScheduledActivities_XPATH");
		List<WebElement> selectProvince = clickOnListOfWebelement("SelectProvinceScheduledActivitiesDrp_XPATH");

		for (int i = 0; i <= selectProvince.size() - 1; i++) {
			if (selectProvince.size() > 1) {
				selectProvince.get(selectProvince.size() - provinceIndex).click();
				break;
			}
		}
		click("multiSelectcloseSelectProvinceScheduledActivities_XPATH");
		Thread.sleep(2000);
	}

	public void HandleDropdownSPMMRegionScheduledActivities() throws InterruptedException {
		Thread.sleep(3000);
		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", 53);
		dynamicLocatorClick("allInputBox_XPATH", 53);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("SelectSPMMRegionDrp_XPATH", InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = clickOnListOfWebelement("SelectSPMMRegionDrp_XPATH");
			String DropDownValue = drpDownList.get(0).getText();
			click("clickOnFirstCheckBoxSPMM_ScheduledActivityDrp_XPATH");
			dynamicLocatorClick("allButton_XPATH", 15);
			dynamicLocatorClick("noneButton_XPATH", 15);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 22, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 22);
			dynamicLocatorClick("noneButton_XPATH", 15);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", 24);
	}

	// verify filters dropdown SPMM Area for Scheduled Activities
	public void HandleDropdownSPMMAreaScheduledActivities() throws InterruptedException {

		WebElement EnterKeywordValue = driver.findElement(By.xpath("(//input[@placeholder='Enter keywords'])[23]"));

		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", 54);
		dynamicLocatorClick("allInputBox_XPATH", 54);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("SelectSPMMAreaDrp_XPATH", InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = clickOnListOfWebelement("SelectSPMMAreaDrp_XPATH");
			String DropDownValue = drpDownList.get(2).getText();
			dynamicLocatorClick("clickOnFirstCheckBoxSA_SPMM_Area_XPATH", 25);
			dynamicLocatorClick("allButton_XPATH", 16);
			dynamicLocatorClick("noneButton_XPATH", 16);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 23, DropDownValue);
			EnterKeywordValue.clear();
			dynamicLocatorClick("noneButton_XPATH", 16);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", 25);
		Thread.sleep(3000);
	}

// verify filters dropdown Select Activity Scheduled Activity
	public void HandleDropdownSelectActivityScheduledActivity(int index1, int index2, int index3)
			throws InterruptedException {
		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", index2);
		dynamicLocatorClick("allInputBox_XPATH", index2);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", index3, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", index3,
					InputBoxName);
			String DropDownValue = drpDownList.get(1).getText();
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", index1, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", index1);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
	}

	public void HandleDropdownSelectSiteOwnerScheduledActivities() throws InterruptedException {
		Thread.sleep(3000);
		String InputBoxName = dynamicLocatorGetText("allInputBox_XPATH", 56);

		dynamicLocatorClick("allInputBox_XPATH", 56);

		Thread.sleep(2000);
		int count = troubleTicket.getDropDownListCount("allDropDownListFilterSA_XPATH", 2, InputBoxName);
		if (count >= 1) {
			List<WebElement> drpDownList = troubleTicket.getDropDownList("allDropDownListFilterSA_XPATH", 2,
					InputBoxName);
			String DropDownValue = drpDownList.get(1).getText();
			click("clickOnFirstCheckBoxSelectSiteOwnerDrp_XPATH");
			dynamicLocatorClick("allButton_XPATH", 17);
			dynamicLocatorClick("noneButton_XPATH", 17);
			dynamicLocatorSendKeys("enterKeywordValue_XPATH", 25, DropDownValue);
			clearTextFromInputBox("enterKeywordValue_XPATH", 25);
			dynamicLocatorClick("noneButton_XPATH", 17);
			troubleTicket.handleDropDownList(drpDownList, DropDownValue);
		}
		dynamicLocatorClick("multiSelectClose_XPATH", 27);
	}

	public void setSiteIdValueInFiltersOfScheduledActivities() {
		enterTextIntoInputBox("setSiteIDValue_XPATH", "1234asd");

	}

	public void handleScheduledActivityCalendar(int date) throws InterruptedException {
		click("scheduled_Activities_XPATH");
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
		explicitWaitWithClickable("Before_Calender_On_Scheduled_Activity_XPATH");
		explicitWaitWithClickable("back_Month_On_Scheduled_Activity_XPATH");
		handleCalendarRefScheduledActivities("calendarDaysListScheduledActivities_XPATH", date);
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		Thread.sleep(2000);
	}

	// handle Select SPM Region dropdown filter
	public void handleSelectActivity() throws TimeoutException {
		click("show_Hide_Filters_XPATH");
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Activity");
		String firstValue = clickUsingDynamicLocator("select_First_Value_Apollo_From_DropDown_XPATH",
				"Due Diligence - Civil");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(4));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(4));
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", firstValue);
		clearTextFromInputBox("enter_Value_Into_Select_Activity_Filter_XPATH");
		clickUsingDynamicLocator("select_First_Value_Apollo_From_DropDown_XPATH", "Due Diligence - Civil");

	}

	// handle Select SPM Region dropdown filter
	public void handleFilters() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_Apollo_XPATH", "Select Activity Completed");
		clickUsingDynamicLocator("select_First_Value_XPATH", "Reset Genset");
		clickUsingDynamicLocator("click_On_Select_Filter_Apollo_XPATH", "Select Power Vendor");
		clickUsingDynamicLocator("select_First_Value_XPATH", "System Define");
		clickUsingDynamicLocator("click_On_Select_Filter_Apollo_XPATH", "Select Generator Status");
		clickUsingDynamicLocator("select_First_Value_XPATH", "Pulled from the site");
		clickUsingDynamicLocator("click_On_Select_Filter_Apollo_XPATH", "Select PM Source");
		clickUsingDynamicLocator("select_First_Value_XPATH", "Fuel Filling");
		clickUsingDynamicLocator("click_On_Select_Filter_Apollo_XPATH", "Schedule Date");
		clickUsingDynamicLocator("select_First_Value_XPATH", "Schedule Date");
		enterTextIntoInputBox("txtTechName_XPATH", "Select Activity Mode");
		enterTextIntoInputBox("txtOMManager_XPATH", "Manual");
		enterTextIntoInputBox("txtPrevMainRptSiteId_XPATH", "Select iAsset Sync Status");
		enterTextIntoInputBox("txtTranId_XPATH", "Success");
	}

	public void handleFiltersSechedule() throws TimeoutException {
		enterTextIntoInputBox("siteID_Apollo_XPATH", "ai12200");
		click("date_XPATH");
		click("create_Dtare_XPATH");
	}

	// handle Select vendor filter
	public void handleSelectvendor() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Vendor");
		String firstValue = clickUsingDynamicLocator("select_First_Value_Apollo_Vendor_From_DropDown_XPATH", "Bidvest");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(5));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(5));
		enterTextIntoInputBox("getText_Select_Vendor_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Vendor_XPATH");
		clickUsingDynamicLocator("select_First_Value_Apollo_Vendor_From_DropDown_XPATH", "Bidvest");

	}

	// handle Select Activity Status filter
	public void handleSelectActivityStatus(String ticketStatus) throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Activity Status");
		String firstValue = clickUsingDynamicLocator("select_First_Value_Apollo_Activity_Status_From_DropDown_XPATH",
				ticketStatus);
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(6));
		clickUsingDynamicLocator("click_On_None_CheckBox_XPATH", String.valueOf(6));
		enterTextIntoInputBox("getText_Select_Activity_Status_XPATH", firstValue);
		clearTextFromInputBox("getText_Select_Activity_Status_XPATH");
		clickUsingDynamicLocator("select_First_Value_Apollo_Activity_Status_From_DropDown_XPATH", ticketStatus);

	}

	// handle Select Activity Status filter
	public void handleSelectSiteOwner() throws TimeoutException {
		clickUsingDynamicLocator("click_On_Select_Filter_XPATH", "Select Site Owner");
		clickUsingDynamicLocator("click_On_All_CheckBox_XPATH", String.valueOf(7));
		clickEscape();

	}

	// add new Activity
	public void addPMActivityReport(String addSiteID, String selectActivityType, String selectAssignGroup)
			throws InterruptedException, TimeoutException {

		Allure.step("/* User is schedule the new site manually */");
		handleClickByJS("add_Activity_Button_XPATH");
		enterTextIntoInputBoxUsingActionsClass("site_Id_On_Activity_XPATH", addSiteID);
		clickOnSuggestedValue();
		getDomProperty("site_Name_Activity_XPATH", "value");
		getDomProperty("OPCO_site_Id_XPATH", "value");

		/* Site should be search based on Site Id/Site Name/Anchor ID */
		availableValue = driver.findElement(By.xpath("//input[@id='opcositeId']")).getDomProperty("value");
		if ((!availableValue.isEmpty())) {
			Allure.step("/* Site should be search based on Site Id/Site Name/Anchor ID */");
			clickOnWebelement("OPCO_site_Id_XPATH").clear();
			clickOnWebelement("site_Id_On_Activity_XPATH").clear();

			/* Site ID should get auto filled if user fill Opco Site ID */
			Allure.step("/* Verify Site ID get auto filled if user fill Opco Site ID */");
			enterTextIntoInputBoxUsingActionsClass("OPCO_site_Id_XPATH", availableValue);
			clickOnSuggestedValue2();
			getDomProperty("site_Name_Activity_XPATH", "value");
			getDomProperty("site_Id_On_Activity_XPATH", "value");
		}

		// New site is scheduled
		click("select_Activity_Type_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Activity_Type_XPATH", selectActivityType);
		click("select_Assign_Group_XPATH");
		handleListOfWebelementAndEnterValue("select_Value_From_Assign_Group_XPATH", selectAssignGroup);
		explicitWaitWithClickable("schedule_Date_ID");
		selectTodayDate(0);
		explicitWaitWithClickable("review_Plan_Date_ID");
		selectTodayDate(0);
		click("schedule_Add_Activity_ID");
		// get message after clicking on schedule button
		String ErrorMessage = getText("error_Message_XPATH");
		// Handle error messsage if Ticket(s) already exists or Invalid Site Id
		if (ErrorMessage.equalsIgnoreCase("Activity is already schedule for this date.")) {
			click("close_Form_XPATH");
			Assert.assertEquals(ErrorMessage, "");
		}
		click("close_Form_XPATH");
		refreshPage();
		// Scheduled activities are visible under scheduled tab
		click("show_Hide_Filters_XPATH");
		click("select_Activity_Filter_XPATH");
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);

		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", addSiteID);
		click("filter_Report_ID");
		click("export_Activity_Report_XPATH");
		Thread.sleep(2000);
		renameDownloadedFile("ActivityReport_", "AddActivityReport_");
		verifyUsingAssertFileIsExistInLocation("AddActivityReport_");

	}

	public void deleteActivityOfPMScheduled(String addSiteID, String selectActivityType, String assignUserValue,
			String remarks) throws InterruptedException {
		enterTextIntoInputBox("enter_Value_Into_Select_Activity_Filter_XPATH", selectActivityType);

		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//input[@name=\"multiselect_ddlTypePM\"]/following-sibling::span[contains(text(),'"
						+ selectActivityType + "')]")))
				.click();
		Thread.sleep(1000);
		Allure.step(
				"If activity is assigned to user level then user should get assigned for that PM ticket irrespective of site user mapping");
		handleInputFieldByJS("technician_Name_XPATH", assignUserValue);
		handleInputFieldByJS("site_ID_On_Actity_Filter_XPATH", addSiteID);
		click("filter_Report_ID");
		Thread.sleep(2000);
		click("delete_Activity_ID");
		acceptTheAlert();
		typesIntoAnAlert(remarks);
		acceptTheAlert();
		acceptTheAlert();
	}

	public void verifyDataFieldValidation(String status) throws Exception {

		String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

		File downloadDirectoryTemplate = new File(basePath + "iTower_Clients_Excel\\excel_MAST\\");
		String templateCsvPath = downloadDirectoryTemplate + "\\" + status + ".csv";

		File downloadDirectory = new File(basePath + "downloadExcel\\");
		String targetCsvPath = downloadDirectory + "\\SiteActivityFieldValidation.csv";

		String outputExcelPath = downloadDirectoryTemplate + "\\SiteActivity_mandatory_field_validation_report.xlsx";
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

		// We expect at least a header row (index 0). Data (if any) starts at index 1.
		if (rows.size() < 1) {
			throw new IllegalStateException("CSV does not contain a valid header row at index 0: " + targetCsvPath);
		}

		// ----------------------------------------------
		// HEADER IS ON FIRST ROW (index 0)
		// ----------------------------------------------
		String[] headerRow = rows.get(0);

		Map<String, Integer> headerIndex = new LinkedHashMap<>();
		for (int i = 0; i < headerRow.length; i++) {
			String h = headerRow[i] == null ? "" : stripBom(headerRow[i]).trim();
			if (!h.isEmpty())
				headerIndex.put(h, i);
		}

		// Column index detection
		int statusIndex = headerIndex.containsKey("Status") ? headerIndex.get("Status") : -1;
		int siteIdIndex = headerIndex.containsKey("Site Id") ? headerIndex.get("Site Id") : -1;

		List<ValidationRecordWithStatusAndSiteId> records = new ArrayList<>();
		Map<String, Integer> missingCounts = new LinkedHashMap<>();

		// DATA STARTS FROM 2nd ROW (index = 1)
		int dataStartIndex = 1;
		int totalDataRows = Math.max(0, rows.size() - dataStartIndex);

		for (int r = dataStartIndex; r < rows.size(); r++) {
			String[] dataRow = rows.get(r);

			// Displayed line number (1-based for data rows): with header at index 0, a data
			// row at index 1 should be shown as line 1
			int displayedLine = r - dataStartIndex + 1;

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
			String[] cols = { "Row Number", "Site Id", "Status", "Header Name", "Value", "Result(Mandatory Value)" };

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

	public void verifyCSVDataWithPageData() throws InterruptedException, TimeoutException, IOException, CsvException {

		renameDownloadedFile("ActivityReport", "csvFileDataSiteActivity_MAST", "csv");

		String rowsCssSelector = "div.ag-body-viewport div.ag-body-container > div"; // adjust as needed

		// CSS selector for cells inside each row
		String cellSelector = "div"; // adjust (e.g. ".ag-cell", ".ag-cell-value")
		String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";

		File downloadDirectoryTemplate = new File(basePath + "iTower_Clients_Excel\\excel_MAST\\");
		String outputXlsxPath = downloadDirectoryTemplate + "\\comparison_result_SiteActivity_MAST.xlsx";

		File downloadDirectory = new File(basePath + "downloadExcel\\");
		String csvPath = downloadDirectory + "\\csvFileDataSiteActivity_MAST.csv";

		// String csvPath =
		// "C:\\Users\\saurabh.ingulkar\\Desktop\\ama\\iTower2.0_MultiClientAutomationProject\\src\\main\\resources\\downloadExcel\\ScheduledX.csv";
		// String outputXlsxPath =
		// "C:\\Users\\saurabh.ingulkar\\Desktop\\comparison_result.xlsx";

		boolean caseInsensitive = true;
		int maxPageRowsToCollect = 0; // 0 => collect up to CSV rows
		long hoverWaitMillis = 800;
		boolean ignoreHeaderLikeCells = true;

		try {
			// Step 1: Read CSV header + data rows
			String[] headers;
			List<String[]> csvDataRows = new ArrayList<>();
			try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
				List<String[]> all = reader.readAll();
				if (all.size() == 0) {
					System.err.println("CSV is empty: " + csvPath);
					return;
				}
				headers = all.get(0);
				if (all.size() > 1)
					csvDataRows.addAll(all.subList(1, all.size()));
				else {
					System.err.println("CSV has no data rows");
					return;
				}
			}

			int colCount = headers.length;
			List<List<String>> normalizedCsvRows = new ArrayList<>();
			for (String[] rowArr : csvDataRows) {
				String[] row = rowArr;
				if (row.length < colCount) {
					String[] tmp = new String[colCount];
					System.arraycopy(row, 0, tmp, 0, row.length);
					for (int i = row.length; i < colCount; i++)
						tmp[i] = "";
					row = tmp;
				}
				List<String> r = new ArrayList<>();
				for (int i = 0; i < colCount; i++)
					r.add(row[i] == null ? "" : row[i].trim());
				normalizedCsvRows.add(r);
			}

			List<String> headerList = new ArrayList<>();
			for (String h : headers)
				headerList.add(h == null ? "" : h.trim());

			// find CSV Site Id column if present
			int siteIdCsvIndex = -1;
			// find CSV Status column if present
			int statusCsvIndex = -1;
			for (int i = 0; i < headerList.size(); i++) {
				String hh = headerList.get(i);
				if (hh == null)
					continue;
				String norm = hh.trim().toLowerCase();
				if (siteIdCsvIndex < 0 && (norm.equals("site id") || norm.equals("siteid") || norm.equals("site_id")
						|| norm.equals("site"))) {
					siteIdCsvIndex = i;
				}
				if (statusCsvIndex < 0 && (norm.equals("status") || norm.equals("status") || norm.equals("status")
						|| norm.equals("status"))) {
					statusCsvIndex = i;
				}
				if (siteIdCsvIndex >= 0 && statusCsvIndex >= 0)
					break;
			}

			int csvRowsCount = normalizedCsvRows.size();
			System.out.println("CSV headers count: " + headerList.size() + ", CSV data rows: " + csvRowsCount);

			// Step 2: collect page rows with tooltip capture
			List<WebElement> rowEls = driver.findElements(By.cssSelector(rowsCssSelector));
			System.out.println("Found page rows in DOM: " + rowEls.size());

			int rowsToCollect = Math.min(csvRowsCount, rowEls.size());
			if (maxPageRowsToCollect > 0)
				rowsToCollect = Math.min(rowsToCollect, maxPageRowsToCollect);

			Actions actions = new Actions(driver);
			JavascriptExecutor js = (JavascriptExecutor) driver;

			List<List<CellWithTooltipMap>> pageRows = new ArrayList<>();
			LinkedHashSet<String> allTooltipKeys = new LinkedHashSet<>();

			for (int r = 0; r < rowsToCollect; r++) {
				WebElement rowEl = rowEls.get(r);
				List<WebElement> cellEls = rowEl.findElements(By.cssSelector(cellSelector));
				List<CellWithTooltipMap> cells = new ArrayList<>();

				for (WebElement cellEl : cellEls) {
					String visible = "";
					try {
						visible = cellEl.getText();
						if (visible == null)
							visible = "";
						visible = visible.trim();
					} catch (Exception ex) {
						visible = "";
					}

					String tooltipText = "";
					List<String> hoverLines = new ArrayList<>();
					String firstTooltip = null;
					List<String> remainingTooltips = new ArrayList<>();

					try {
						String t = cellEl.getAttribute("title");
						if (t != null && !t.trim().isEmpty())
							tooltipText = t.trim();
						else {
							String a = cellEl.getAttribute("aria-label");
							if (a != null && !a.trim().isEmpty())
								tooltipText = a.trim();
						}
					} catch (Exception ignored) {
					}

					if (tooltipText.isEmpty()) {
						try {
							js.executeScript("arguments[0].scrollIntoView({block:'center'});", cellEl);
							actions.moveToElement(cellEl).perform();
							Thread.sleep(hoverWaitMillis);

							List<WebElement> hoverNodes = driver.findElements(By.xpath(
									"//div[contains(@id,'DivHover')]/div/ul/li | //div[contains(@id,'DivHover')]/table/tbody/tr"));

							if (hoverNodes != null && !hoverNodes.isEmpty()) {
								for (WebElement hn : hoverNodes) {
									try {
										if (!hn.isDisplayed())
											continue;
										String txt = hn.getText();
										if (txt == null)
											txt = "";
										txt = txt.trim();
										if (!txt.isEmpty())
											hoverLines.add(txt);
									} catch (StaleElementReferenceException ignored) {
									}
								}
							}

							if (!hoverLines.isEmpty()) {
								firstTooltip = hoverLines.get(0);
								if (hoverLines.size() > 1)
									remainingTooltips.addAll(hoverLines.subList(1, hoverLines.size()));
								tooltipText = String.join("\n", hoverLines);
							}

							if (tooltipText == null || tooltipText.isEmpty()) {
								Object res = js.executeScript(
										"var el = document.querySelector('[role=\"tooltip\"], .tooltip-column, .ant-tooltip, .rc-tooltip, .tooltip');"
												+ "if(el) return (el.innerText||el.textContent).trim(); else return null;");
								if (res != null)
									tooltipText = res.toString().trim();
							}

							if (tooltipText == null || tooltipText.isEmpty()) {
								Object fallback = js
										.executeScript("var nodes = Array.from(document.querySelectorAll('div,span'));"
												+ "for(var i=0;i<nodes.length;i++){"
												+ "  var n = nodes[i]; var s = window.getComputedStyle(n);"
												+ "  if(s && (s.position==='absolute' || s.position==='fixed') && (n.offsetWidth>0 || n.offsetHeight>0)){"
												+ "    var txt = (n.innerText||n.textContent||'').trim();"
												+ "    if(txt.length>0 && txt.length<2000) return txt;"
												+ "  } } return null;");
								if (fallback != null)
									tooltipText = fallback.toString().trim();
							}

						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						} catch (Exception e) {
							/* ignore hover capture errors */ }
					}

					Map<String, String> tooltipMap = parseTooltipToMapRobust(tooltipText);

					if (hoverLines != null && !hoverLines.isEmpty()) {
						int idx = 1;
						for (String line : hoverLines) {
							String syntheticKey = "HoverLine" + idx++;
							if (!tooltipMap.containsValue(line))
								tooltipMap.put(syntheticKey, line);
						}
					}

					for (String k : tooltipMap.keySet())
						allTooltipKeys.add(normalizeKey(k));
					cells.add(new CellWithTooltipMap(visible, tooltipText, tooltipMap));
				}

				pageRows.add(cells);
			}

			System.out.println("Collected page rows with tooltips: " + pageRows.size());

			// Step 3: Compare and write Excel
			int rowsToCompare = Math.min(pageRows.size(), normalizedCsvRows.size());
			System.out.println("Comparing rows: " + rowsToCompare);

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Comparison");

			// Styles
			CellStyle headerStyle = workbook.createCellStyle();
			XSSFFont headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);

			// Add header background color (Light Blue)
			headerStyle.setFillForegroundColor(new XSSFColor(new Color(189, 215, 238), null));
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Add thin border to all header cells
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle matchStyle = workbook.createCellStyle();
			matchStyle.setFillForegroundColor(new XSSFColor(new Color(198, 239, 206), null));
			matchStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle notFoundStyle = workbook.createCellStyle();
			notFoundStyle.setFillForegroundColor(new XSSFColor(new Color(255, 199, 206), null));
			notFoundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle mismatchStyle = workbook.createCellStyle();
			mismatchStyle.setFillForegroundColor(new XSSFColor(new Color(255, 235, 156), null));
			mismatchStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			XSSFCellStyle grayStyle = workbook.createCellStyle();
			grayStyle.setFillForegroundColor(new XSSFColor(new Color(217, 217, 217), null));
			grayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row hdr = sheet.createRow(0);
			// Insert Status as third column (after Site Id)
			String[] outCols = new String[] { "RowIndex", "Site Id", "Status", "Header", "CSV Value", "Page Value",
					"TooltipKeyUsed", "Tooltip/Hidden Value", "Result" };
			for (int i = 0; i < outCols.length; i++) {
				Cell c = hdr.createCell(i);
				c.setCellValue(outCols[i]);
				c.setCellStyle(headerStyle);
			}

			int outRowIdx = 1;

			for (int r = 0; r < rowsToCompare; r++) {
				List<String> csvRow = normalizedCsvRows.get(r);
				List<CellWithTooltipMap> pageRow = pageRows.get(r);

				Map<String, List<Integer>> pageNormVisibleToIdx = new LinkedHashMap<>();
				for (int i = 0; i < pageRow.size(); i++) {
					String pv = pageRow.get(i).visible == null ? "" : pageRow.get(i).visible;
					boolean skip = false;
					if (ignoreHeaderLikeCells) {
						for (String h : headerList) {
							if (h == null)
								continue;
							if (caseInsensitive ? pv.equalsIgnoreCase(h) : pv.equals(h)) {
								Map<String, String> tmap = pageRow.get(i).tooltipMap;
								boolean tooltipHasValue = false;
								if (tmap != null && !tmap.isEmpty()) {
									for (String vv : tmap.values()) {
										if (vv != null && !vv.trim().isEmpty()) {
											tooltipHasValue = true;
											break;
										}
									}
								}
								if (!tooltipHasValue)
									skip = true;
								break;
							}
						}
					}
					if (skip)
						continue;
					String pn = normalize(pv, caseInsensitive);
					pageNormVisibleToIdx.computeIfAbsent(pn, k -> new ArrayList<>()).add(i);
				}

				boolean positional = (pageRow.size() == colCount);

				// Compute Site Id for this row (priority: CSV site column if exists, else
				// tooltip search)
				String siteIdValue = "";
				if (siteIdCsvIndex >= 0 && siteIdCsvIndex < csvRow.size()) {
					siteIdValue = csvRow.get(siteIdCsvIndex);
				}
				if ((siteIdValue == null || siteIdValue.trim().isEmpty())) {
					TooltipMatch siteTm = findBestTooltipForHeaderAcrossRow(extractTooltipMapsFromRow(pageRow),
							"Site Id", headerList, caseInsensitive);
					if (siteTm != null && siteTm.value != null) {
						siteIdValue = siteTm.value;
					}
				}
				if (siteIdValue == null)
					siteIdValue = "";

				// Compute Status for this row (priority: CSV status column if exists, else
				// tooltip search)
				String statusValue = "";
				if (statusCsvIndex >= 0 && statusCsvIndex < csvRow.size()) {
					statusValue = csvRow.get(statusCsvIndex);
				}
				if ((statusValue == null || statusValue.trim().isEmpty())) {
					// try "Status" in tooltips
					TooltipMatch statusTm = findBestTooltipForHeaderAcrossRow(extractTooltipMapsFromRow(pageRow),
							"Status", headerList, caseInsensitive);
					if (statusTm != null && statusTm.value != null) {
						statusValue = statusTm.value;
					}
				}
				if (statusValue == null)
					statusValue = "";

				if (positional) {
					for (int c = 0; c < colCount; c++) {
						String header = headerList.get(c);
						String csvVal = csvRow.get(c);
						String pageVal = c < pageRow.size() ? pageRow.get(c).visible : "";
						String tooltipUsedKey = "";
						String tooltipUsedVal = "";

						boolean equal = compareValues(csvVal, pageVal, caseInsensitive);
						String result = equal ? "MATCH" : "MISMATCH";

						if (!equal) {
							if (c < pageRow.size()) {
								Map<String, String> tmap = pageRow.get(c).tooltipMap;
								String val = findTooltipValueUsingHeader(tmap, header, caseInsensitive);
								String key = findTooltipKeyUsingHeader(tmap, header, caseInsensitive);
								if (val == null) {
									TooltipMatch tm = findBestTooltipForHeaderAcrossRow(
											extractTooltipMapsFromRow(pageRow), header, headerList, caseInsensitive);
									if (tm != null) {
										key = tm.key;
										val = tm.value;
									}
								}
								if (val != null) {
									tooltipUsedKey = key == null ? "" : key;
									tooltipUsedVal = val;
									if (compareValues(csvVal, tooltipUsedVal, caseInsensitive)) {
										result = "MATCH (via tooltip:" + tooltipUsedKey + ")";
									}
								}
							} else {
								TooltipMatch tm = findBestTooltipForHeaderAcrossRow(extractTooltipMapsFromRow(pageRow),
										header, headerList, caseInsensitive);
								if (tm != null && tm.value != null) {
									tooltipUsedKey = tm.key;
									tooltipUsedVal = tm.value;
									if (compareValues(csvVal, tooltipUsedVal, caseInsensitive)) {
										result = "MATCH (via tooltip:" + tooltipUsedKey + ")";
									}
								}
							}
						}

						Row or = sheet.createRow(outRowIdx++);
						or.createCell(0).setCellValue(r);
						or.createCell(1).setCellValue(siteIdValue); // second column
						or.createCell(2).setCellValue(statusValue); // Status is third column
						or.createCell(3).setCellValue(header);
						or.createCell(4).setCellValue(csvVal);
						or.createCell(5).setCellValue(pageVal);
						or.createCell(6).setCellValue(tooltipUsedKey);
						or.createCell(7).setCellValue(tooltipUsedVal);
						Cell res = or.createCell(8);
						res.setCellValue(result);
						if (result.startsWith("MATCH"))
							res.setCellStyle(matchStyle);
						else if ("MISMATCH".equals(result))
							res.setCellStyle(mismatchStyle);
						else
							res.setCellStyle(grayStyle);
					}
				} else {
					for (int c = 0; c < colCount; c++) {
						String header = headerList.get(c);
						String csvVal = csvRow.get(c);
						String csvNorm = normalize(csvVal, caseInsensitive);

						String pageValFound = "";
						String tooltipKeyUsed = "";
						String tooltipValUsed = "";
						String result;

						List<Integer> foundIdx = pageNormVisibleToIdx.getOrDefault(csvNorm, Collections.emptyList());
						if (!foundIdx.isEmpty()) {
							LinkedHashSet<String> dedup = new LinkedHashSet<>();
							for (Integer fi : foundIdx)
								dedup.add(pageRow.get(fi).visible == null ? "" : pageRow.get(fi).visible);
							pageValFound = String.join(" | ", dedup);
							result = "MATCH";
						} else {
							TooltipMatch tm = findBestTooltipForHeaderAcrossRow(extractTooltipMapsFromRow(pageRow),
									header, headerList, caseInsensitive);
							if (tm != null && tm.value != null) {
								tooltipKeyUsed = tm.key;
								tooltipValUsed = tm.value;
								pageValFound = tm.nearbyVisible;
								if (compareValues(csvVal, tooltipValUsed, caseInsensitive))
									result = "MATCH (via tooltip:" + tooltipKeyUsed + ")";
								else
									result = "NOT FOUND";
							} else {
								result = "NOT FOUND";
							}
						}

						Row or = sheet.createRow(outRowIdx++);
						or.createCell(0).setCellValue(r);
						or.createCell(1).setCellValue(siteIdValue); // second column
						or.createCell(2).setCellValue(statusValue); // Status is third column
						or.createCell(3).setCellValue(header);
						or.createCell(4).setCellValue(csvVal);
						or.createCell(5).setCellValue(pageValFound);
						or.createCell(6).setCellValue(tooltipKeyUsed);
						or.createCell(7).setCellValue(tooltipValUsed);
						Cell res = or.createCell(8);
						res.setCellValue(result);
						if (result.startsWith("MATCH"))
							res.setCellStyle(matchStyle);
						else if ("NOT FOUND".equals(result))
							res.setCellStyle(notFoundStyle);
						else
							res.setCellStyle(mismatchStyle);
					}
				}
			}

			for (int i = 0; i < outCols.length; i++)
				sheet.autoSizeColumn(i);
			try (FileOutputStream fos = new FileOutputStream(outputXlsxPath)) {
				workbook.write(fos);
			}
			workbook.close();
			System.out.println("Excel written to: " + outputXlsxPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------- helpers (same as before) -----------------

	private static List<Map<String, String>> extractTooltipMapsFromRow(List<CellWithTooltipMap> pageRow) {
		List<Map<String, String>> maps = new ArrayList<>();
		for (CellWithTooltipMap c : pageRow)
			maps.add(c.tooltipMap == null ? Collections.emptyMap() : c.tooltipMap);
		return maps;
	}

	private static Map<String, String> parseTooltipToMapRobust(String tooltipText) {
		Map<String, String> map = new LinkedHashMap<>();
		if (tooltipText == null)
			return map;
		String normalized = tooltipText.replace("\r", "\n").replaceAll("\\u00A0", " ").trim();
		if (normalized.isEmpty())
			return map;

		String[] lines = normalized.split("\\n+");
		String currentSection = null;
		int hoverLineIdx = 1;
		for (String raw : lines) {
			String line = raw.trim();
			if (line.isEmpty())
				continue;
			if (isSectionHeading(line)) {
				String heading = line.replaceAll("[:\\-]+$", "").replaceAll("\\s{2,}", " ").trim();
				String h = heading;
				if (heading.toLowerCase().contains("site"))
					h = extractShortPrefix(heading, "site");
				else if (heading.toLowerCase().contains("technician"))
					h = extractShortPrefix(heading, "technician");
				else {
					String[] toks = heading.split("\\s+");
					if (toks.length > 0 && toks[0].length() > 0)
						h = toks[0];
				}
				currentSection = h.trim();
				continue;
			}
			int colon = line.indexOf(':');
			if (colon > 0) {
				String key = line.substring(0, colon).trim();
				String val = line.substring(colon + 1).trim();
				if (val.isEmpty()) {
					currentSection = key;
					continue;
				}
				String storeKey = (currentSection == null || currentSection.isEmpty()) ? key
						: (currentSection + " " + key);
				storeKey = storeKey.replaceAll("\\s{2,}", " ").trim();
				uniquePut(map, storeKey, val);
			} else {
				String[] parts = line.split("\\s{2,}");
				if (parts.length >= 2) {
					String key = parts[0].trim();
					String val = parts[1].trim();
					String storeKey = (currentSection == null || currentSection.isEmpty()) ? key
							: (currentSection + " " + key);
					uniquePut(map, storeKey, val);
				} else {
					String storeKey = (currentSection == null || currentSection.isEmpty())
							? ("HoverLine" + hoverLineIdx)
							: (currentSection + " HoverLine" + hoverLineIdx);
					uniquePut(map, storeKey, line);
					hoverLineIdx++;
				}
			}
		}
		return map;
	}

	private static boolean isSectionHeading(String line) {
		if (line == null)
			return false;
		String l = line.toLowerCase();
		if (l.contains("detail") || l.contains("details") || l.contains("lat-long") || l.contains("lat long"))
			return true;
		if (line.matches(".*:\\s*$"))
			return true;
		if (l.equals("site") || l.equals("technician") || l.equals("site details") || l.equals("technician details"))
			return true;
		return false;
	}

	private static void uniquePut(Map<String, String> map, String key, String value) {
		if (key == null)
			key = "Key";
		String k = key;
		int suffix = 2;
		while (map.containsKey(k)) {
			k = key + " (" + suffix + ")";
			suffix++;
		}
		map.put(k, value == null ? "" : value);
	}

	private static String extractShortPrefix(String heading, String token) {
		String[] toks = heading.split("\\s+");
		for (String t : toks) {
			if (t.toLowerCase().contains(token)) {
				if (t.equalsIgnoreCase("site") || t.equalsIgnoreCase("technician"))
					return capitalize(t.toLowerCase());
				return capitalize(token);
			}
		}
		return capitalize(toks[0]);
	}

	private static String capitalize(String s) {
		if (s == null || s.isEmpty())
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private static String findTooltipValueUsingHeader(Map<String, String> tooltipMap, String header,
			boolean caseInsensitive) {
		if (tooltipMap == null || tooltipMap.isEmpty())
			return null;
		String headerNorm = normalize(header, caseInsensitive);

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (!kn.isEmpty() && kn.equals(headerNorm)) {
				String v = tooltipMap.get(k);
				return (v == null || v.isEmpty()) ? null : v;
			}
		}

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (kn.isEmpty())
				continue;
			if (kn.startsWith(headerNorm) || headerNorm.startsWith(kn) || kn.endsWith(headerNorm)
					|| headerNorm.endsWith(kn)) {
				String v = tooltipMap.get(k);
				if (!isBlank(v))
					return v;
			}
		}

		String[] tokens = headerNorm.split("\\s+");
		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			for (String tok : tokens) {
				if (tok.length() < 2)
					continue;
				if (kn.contains(tok)) {
					String v = tooltipMap.get(k);
					return (v == null || v.isEmpty()) ? null : v;
				}
			}
		}
		return null;
	}

	private static String findTooltipKeyUsingHeader(Map<String, String> tooltipMap, String header,
			boolean caseInsensitive) {
		if (tooltipMap == null || tooltipMap.isEmpty())
			return "";
		String headerNorm = normalize(header, caseInsensitive);

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (!kn.isEmpty() && kn.equals(headerNorm))
				return k;
		}

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (kn.isEmpty())
				continue;
			if (kn.startsWith(headerNorm) || headerNorm.startsWith(kn) || kn.endsWith(headerNorm)
					|| headerNorm.endsWith(kn)) {
				return k;
			}
		}

		String[] tokens = headerNorm.split("\\s+");
		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			for (String tok : tokens) {
				if (tok.length() < 2)
					continue;
				if (kn.contains(tok))
					return k;
			}
		}
		return "";
	}

	private static String findTooltipKeyForHeader(Map<String, String> tooltipMap, String header,
			boolean caseInsensitive) {
		if (tooltipMap == null || tooltipMap.isEmpty())
			return "";
		String headerNorm = normalize(header, caseInsensitive);

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (!kn.isEmpty() && kn.equals(headerNorm))
				return k;
		}

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			if (kn.isEmpty())
				continue;
			if (kn.startsWith(headerNorm) || headerNorm.startsWith(kn) || kn.endsWith(headerNorm)
					|| headerNorm.endsWith(kn)) {
				return k;
			}
		}

		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			for (String tok : headerNorm.split("\\s+")) {
				if (tok.length() < 2)
					continue;
				if (kn.contains(tok))
					return k;
			}
		}

		String bestKey = null;
		int bestScore = 0;
		for (String k : tooltipMap.keySet()) {
			if (k == null)
				continue;
			String kn = normalize(k, caseInsensitive);
			int score = 0;
			for (String tok : headerNorm.split(" ")) {
				if (tok.length() < 2)
					continue;
				if (kn.contains(tok))
					score += 2;
			}
			if (headerNorm.contains("site") && kn.contains("site"))
				score += 3;
			if (headerNorm.contains("technician") && kn.contains("technician"))
				score += 3;
			if (headerNorm.contains("lat") && kn.contains("lat"))
				score += 4;
			if (headerNorm.contains("long") && kn.contains("long"))
				score += 4;
			if (kn.equals(headerNorm))
				score += 5;
			if (score > bestScore) {
				bestScore = score;
				bestKey = k;
			}
		}
		return bestKey == null ? "" : bestKey;
	}

	private static TooltipMatch findBestTooltipForHeaderAcrossRow(List<Map<String, String>> tooltipMaps, String header,
			List<String> allHeaders, boolean caseInsensitive) {
		if (tooltipMaps == null || tooltipMaps.isEmpty())
			return null;
		String headerNorm = normalize(header, caseInsensitive);

		// 1 exact across
		for (int mapIndex = 0; mapIndex < tooltipMaps.size(); mapIndex++) {
			Map<String, String> m = tooltipMaps.get(mapIndex);
			for (String k : m.keySet()) {
				if (k == null)
					continue;
				String kn = normalize(k, caseInsensitive);
				if (!kn.isEmpty() && kn.equals(headerNorm)) {
					String v = m.get(k);
					if (v != null && !v.isEmpty())
						return new TooltipMatch(k, v, mapIndex, "");
				}
			}
		}

		// 2 starts/ends across
		for (int mapIndex = 0; mapIndex < tooltipMaps.size(); mapIndex++) {
			Map<String, String> m = tooltipMaps.get(mapIndex);
			for (String k : m.keySet()) {
				if (k == null)
					continue;
				String kn = normalize(k, caseInsensitive);
				if (kn.isEmpty())
					continue;
				if (kn.startsWith(headerNorm) || headerNorm.startsWith(kn) || kn.endsWith(headerNorm)
						|| headerNorm.endsWith(kn)) {
					String v = m.get(k);
					if (v != null && !v.isEmpty())
						return new TooltipMatch(k, v, mapIndex, "");
				}
			}
		}

		// 3 token-based
		String[] tokens = headerNorm.split("\\s+");
		List<String> meaningful = new ArrayList<>();
		for (String t : tokens) {
			if (t == null)
				continue;
			String tt = t.trim();
			if (tt.length() >= 3 && !isGenericToken(tt))
				meaningful.add(tt);
		}
		if (meaningful.isEmpty()) {
			for (String t : tokens)
				if (t != null && t.trim().length() >= 4)
					meaningful.add(t.trim());
		}
		if (!meaningful.isEmpty()) {
			for (int mapIndex = 0; mapIndex < tooltipMaps.size(); mapIndex++) {
				Map<String, String> m = tooltipMaps.get(mapIndex);
				for (String k : m.keySet()) {
					if (k == null)
						continue;
					String kn = normalize(k, caseInsensitive);
					for (String tok : meaningful) {
						if (kn.contains(tok)) {
							String v = m.get(k);
							if (v != null && !v.isEmpty())
								return new TooltipMatch(k, v, mapIndex, "");
						}
					}
				}
			}
		}

		// 4 aggregated search
		for (int mapIndex = 0; mapIndex < tooltipMaps.size(); mapIndex++) {
			Map<String, String> m = tooltipMaps.get(mapIndex);
			StringBuilder combined = new StringBuilder();
			for (Map.Entry<String, String> e : m.entrySet())
				combined.append(e.getKey() == null ? "" : e.getKey()).append(": ")
						.append(e.getValue() == null ? "" : e.getValue()).append("\n");
			String combinedStr = normalize(combined.toString(), caseInsensitive);
			int foundCount = 0;
			for (String tok : meaningful) {
				if (combinedStr.contains(tok))
					foundCount++;
			}
			if (foundCount > 0) {
				String bestKey = findTooltipKeyForHeader(m, header, caseInsensitive);
				if (bestKey != null && !bestKey.isEmpty()) {
					String val = m.get(bestKey);
					if (val != null && !val.isEmpty())
						return new TooltipMatch(bestKey, val, mapIndex, "");
				}
			}
		}

		// 5 heuristic scoring
		String bestKey = null, bestVal = null;
		int bestIdx = -1;
		int bestScore = 0;
		for (int mapIndex = 0; mapIndex < tooltipMaps.size(); mapIndex++) {
			Map<String, String> m = tooltipMaps.get(mapIndex);
			for (String k : m.keySet()) {
				if (k == null)
					continue;
				int score = 0;
				String kn = normalize(k, caseInsensitive);
				for (String tok : headerNorm.split(" ")) {
					if (tok.length() < 2)
						continue;
					if (kn.contains(tok))
						score += 2;
				}
				if (headerNorm.contains("site") && kn.contains("site"))
					score += 1;
				if (headerNorm.contains("technician") && kn.contains("technician"))
					score += 2;
				if (headerNorm.contains("lat") && kn.contains("lat"))
					score += 3;
				if (headerNorm.contains("long") && kn.contains("long"))
					score += 3;
				if (kn.equals(headerNorm))
					score += 8;
				if (kn.startsWith(headerNorm) || headerNorm.startsWith(kn))
					score += 6;
				if (kn.endsWith(headerNorm) || headerNorm.endsWith(kn))
					score += 4;
				if (score > bestScore) {
					bestScore = score;
					bestKey = k;
					bestVal = m.get(k);
					bestIdx = mapIndex;
				}
			}
		}
		if (bestScore >= 6 && bestVal != null && !bestVal.isEmpty())
			return new TooltipMatch(bestKey, bestVal, bestIdx, "");
		return null;
	}

	private static boolean isGenericToken(String tok) {
		if (tok == null)
			return true;
		String t = tok.trim().toLowerCase();
		Set<String> blacklist = new HashSet<>(Arrays.asList("site", "name", "type", "value", "date", "mode", "owner",
				"vendor", "status", "id", "no", "yes"));
		if (t.length() <= 3)
			return true;
		if (blacklist.contains(t))
			return true;
		return false;
	}

	private static boolean compareValues(String a, String b, boolean caseInsensitive) {
		if (a == null)
			a = "";
		if (b == null)
			b = "";
		if (caseInsensitive)
			return a.trim().equalsIgnoreCase(b.trim());
		return a.trim().equals(b.trim());
	}

	private static String normalize(String s, boolean caseInsensitive) {
		if (s == null)
			return "";
		String t = s.trim().replaceAll("\\s+", " ");
		return caseInsensitive ? t.toLowerCase() : t;
	}

	private static String normalizeKey(String k) {
		if (k == null)
			return "";
		String s = k.replace("\u00A0", " ").trim().replaceAll("[:\\-–—]+$", "").replaceAll("\\s+", " ").trim();
		return s.toLowerCase();
	}

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static class CellWithTooltipMap {
		String visible;
		String tooltipText;
		Map<String, String> tooltipMap;

		CellWithTooltipMap(String v, String t, Map<String, String> map) {
			visible = v == null ? "" : v;
			tooltipText = t == null ? "" : t;
			tooltipMap = map == null ? new LinkedHashMap<>() : map;
		}
	}

	private static class TooltipMatch {
		String key;
		String value;
		int mapIndex;
		String nearbyVisible;

		TooltipMatch(String k, String v, int idx, String nearby) {
			key = k;
			value = v;
			mapIndex = idx;
			nearbyVisible = nearby;
		}
	}
public void handleFiltersfor(String siteID) throws InterruptedException {
	click("scheduled_Activities_XPATH");
	handleInputFieldByJS("site_ID_On_Scheduled_Filter_XPATH", siteID);
	
}
}
