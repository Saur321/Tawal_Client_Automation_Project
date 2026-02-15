package testCases.clients.Tawal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.opencsv.exceptions.CsvException;

import io.qameta.allure.Description;
import pages.AllClientsBasePages.BasePage;
import pages.iTower_Tawal.LoginPage;
import pages.iTower_Tawal.SiteActivityPage;
import pages.iTower_Tawal.TroubleTicketingPage;
import utilities.DataUtilities_Tawal;

public class TheSiteActivity extends BasePage {
	BasePage base = new BasePage();
	LoginPage login = new LoginPage();
	SoftAssert sa = new SoftAssert();
	SiteActivityPage siteActivity = new SiteActivityPage();
	TroubleTicketingPage troubleTicket = new TroubleTicketingPage();

	@Description("Verify Login Page functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 1)

	public void testLogin(String clientName, String username, String password) throws InterruptedException {
		base.setUp(clientName);
		login.doLogin(username, password);
	}

	@Description("Verify User is able to schedule the new site manually")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 2, dependsOnMethods = {
			"testLogin" })
	public void scheduleNewActivity(String siteId, String selectActivityType, String selectAssignGroup,
			String selectReviewGroup, String assignUserValue, String reviewUserValue)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		sa.assertEquals(base.getText("activity_Report_XPATH"), "Activity Report");
		siteActivity.addActivityReport(siteId, selectActivityType, selectAssignGroup, selectReviewGroup,
				assignUserValue, reviewUserValue);
	}

	@Description("Verify User should also get option to delete the manual activity till the time activity is schedule and remarks should be mandatory while deleting")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 3, dependsOnMethods = {
			"testLogin" })
	public void deleteSchedulePMActivity(String siteID, String selectActivityType, String assignUserValue,
			String selectActivityStatus, String remarks)
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.deletedScheduleActivity(siteID, selectActivityType, assignUserValue, selectActivityStatus,
				remarks);
	}

	@Description("Verify Assigned user is able to done activity")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 4, dependsOnMethods = {
			"testLogin" })
	public void verifyPMForDone(String SiteID, String folderName, String checklistForDone) throws Exception {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.filterScheduledActivity(SiteID);
		siteActivity.uploadMediaAndCheckListForDoneActivity(folderName, checklistForDone);
		siteActivity.verifyStatusOfActivityAfterDone();

	}

	@Description("Verify Images are visible on D button along with Image Tags")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 5, dependsOnMethods = {
			"testLogin" })
	public void verifyImagesAndTags(String siteID) throws Exception {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.filterActivityReport(siteID);
		siteActivity.verifyImagesAreVisibleAlongWithImageTags();

	}

	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 6)
	public void testLoginToReviewUser(String username, String password)
			throws InterruptedException, java.util.concurrent.TimeoutException, FileNotFoundException, IOException {
		refreshPage();
		troubleTicket.performLogout();
		login.doLogin(username, password);

	}

	@Description("Verify Reviewer is able to directly approve checklist through direct approve option")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 7, dependsOnMethods = {
			"testLoginToReviewUser" })
	public void performDirectApprove(String siteId, String selectProperPhotosAndInformation, String selectQualityPMTask,
			String selectVisualCheck1, String selectVisualCheck2, String selectVisualCheck3)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.openDoneActivity(siteId);
		siteActivity.activityApproveDirectly(selectProperPhotosAndInformation, selectQualityPMTask, selectVisualCheck1,
				selectVisualCheck2, selectVisualCheck3);

	}

	@Description("Verify User is able to reject the activity directly")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 8, dependsOnMethods = {
			"testLoginToReviewUser" })
	public void performDirectReject(String siteId, String selectRejectionReasonCategory)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.openDoneActivity(siteId);
		siteActivity.activityRejectDirectly(siteId, selectRejectionReasonCategory);
	}

	@Description("Verify Reviewer is able to approve the activity")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 9, dependsOnMethods = {
			"testLoginToReviewUser" })
	public void performApproveActivity(String siteId, String verifyChecklistFile)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.openDoneActivity(siteId);
		siteActivity.verifyApproveActivity(verifyChecklistFile);
	}

	@Description("Verify Reviewer is able to reject the activity")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 10, dependsOnMethods = {
			"testLoginToReviewUser" })
	public void performRejectActivity(String siteId, String selectRejectionReasonCategoryChooseFile,
			String remarksForRejection, String verifyChecklistFile)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.openDoneActivity(siteId);
		uploadTheExcelFileFromUploadLocation("upload_Checklist_For_Approved_XPATH", verifyChecklistFile);
		siteActivity.rejectFromChooseFile(siteId, selectRejectionReasonCategoryChooseFile, remarksForRejection);
	}

	@Description("Verify Login Page functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 11)

	public void testLogin2(String clientName, String username, String password) throws InterruptedException {
		refreshPage();
		troubleTicket.performLogout();
		login.doLogin(username, password);
	}

	@Description("Verify Rejected PM can be rescheduled and count is visible")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 12, dependsOnMethods = {
			"testLogin2" })
	public void rescheduleActivity(String siteID, String selectActivityStatus, String selectActivityType,
			String assignUserValue) throws InterruptedException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.rescheduleSiteActivity(siteID, selectActivityStatus, selectActivityType, assignUserValue);
	}

	@Description("User should able to schedule PM through bulk upload option")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawalPM", priority = 13, dependsOnMethods = {
			"testLogin" })
	public void bulkUploadSchedulePMActivity(String addSiteID) throws InterruptedException, TimeoutException,
			IOException, CsvException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.addActivityReporBulkUpload(addSiteID);
		/*
		 * siteActivity.writeData("PM Bulk Upload Update");
		 * siteActivity.writeData("PM Bulk Upload Delete");
		 * siteActivity.updateActivityReporBulkUpload(addSiteID);
		 * siteActivity.deleteActivityReporBulkUpload(addSiteID);
		 */
	}

	@Description("Verify Pagination is working fine")
	@Test(priority = 14, dependsOnMethods = { "testLogin" })
	public void verifyPagination() throws InterruptedException, FileNotFoundException, TimeoutException, IOException,
			java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		click("show_Hide_Filters_XPATH");
		siteActivity.handleCalenderForSiteActivity("Jan", "1");
		siteActivity.paginationOnSiteActivityReport();

	}

	@Description("Verify Activity report is exported successfully into CSV with valid data")
	@Test(priority = 15, dependsOnMethods = { "testLogin" })
	public void verifyActivityReport()
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		click("show_Hide_Filters_XPATH");
		siteActivity.handleCalenderForSiteActivity("Jan", "1");
		explicitWaitWithClickable("click_ButtonFilterActivityReport_XPATH");
		click("activityReport_export_XPATH");
		renameDownloadedFile("ActivityReport_", "VerifyActivityReport_");
		verifyUsingAssertFileIsExistInLocation("VerifyActivityReport_");
	}

	@Description("Verify sorting is working fine in activity report")
	@Test(priority = 16, dependsOnMethods = { "testLogin" })
	public void verifySorting() throws InterruptedException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.sortingOfSiteIdColumn();
		refreshPage();
		siteActivity.sortingOfActivityIdColumn();
	}

	@Description("Verify All filters work fine in Site Activity report")
	@Test(priority = 17, dependsOnMethods = { "testLogin" })
	public void testFilterFunctionalitiesOnSiteActivityReport()
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();
		siteActivity.handleSelectActivity();
		siteActivity.handleSelectSiteOwner();
		siteActivity.handleFilters();
	}

	@Description("Verify All filters work fine in Scheduled Activities")
	@Test(priority = 18, dependsOnMethods = { "testLogin" })
	public void testFilterFunctionalitiesOnScheduledActivities()
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		siteActivity.landingOnSiteActivityPage();

		siteActivity.handleFiltersfor("1000012");

	}
}