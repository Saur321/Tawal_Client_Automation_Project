
package testCases.clients.Tawal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.opencsv.exceptions.CsvException;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import pages.AllClientsBasePages.BasePage;
import pages.iTower_Tawal.BaseDataForAPI;
import pages.iTower_Tawal.LoginPage;
import pages.iTower_Tawal.TroubleTicketingPage;
import utilities.DataUtilities_Tawal;

public class TheTroubleTicketing extends BasePage {
	BasePage base = new BasePage();
	LoginPage login = new LoginPage();
	SoftAssert sa = new SoftAssert();
	TroubleTicketingPage troubleTicket = new TroubleTicketingPage();
	BaseDataForAPI bsAPI = new BaseDataForAPI();

	@Description("Verify setup functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 1)

	public void aVerifySetup(String clientName) throws InterruptedException,
			java.util.concurrent.TimeoutException, FileNotFoundException, IOException, CsvException {
		base.setUp(clientName);
		

	}
	@Description("Verify Login Page functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 1)

	public void testLogin( String username, String password) throws InterruptedException,
			java.util.concurrent.TimeoutException, FileNotFoundException, IOException, CsvException {
		login.doLogin(username, password);

	}

	@Description("Verify Site ID should get auto filled if user fill Opco Site ID")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 2, dependsOnMethods = {
			"testLogin" })
	public void verifySiteIdAutoFilled(String opcoId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifySiteIdAutoFilledByOPCOSiteId(opcoId);
	}

	@Description("Verify Ticket should be added")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 3, dependsOnMethods = {
			"testLogin" })
	public void addTroubleTicket(String siteId, String ticketType, String severity, String assignedTo,
			String alarmDescription, String serviceImpact, String TicketValue)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.addNewTroubleTicket(siteId, ticketType, severity, assignedTo, alarmDescription, serviceImpact,
				TicketValue);

	}

	@Description("Verify test Login To Assign User")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 4,enabled = false)

	public void testLoginToAssignUser(String username, String password)
			throws InterruptedException, java.util.concurrent.TimeoutException, FileNotFoundException, IOException {
		refreshPage();
		troubleTicket.performLogout();
		login.doLogin(username, password);
	}

	@Description("Verify Assigned user should able to update ETA/ETR")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 5, dependsOnMethods = {
			"testLogin" })
	public void updateETAndETR(String alarmDescription, String siteId, String ETAValue, String ETRValue,
			String assignedTo, String remarks, String TicketValue) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.updateETAAndETRByAssignUser(alarmDescription, siteId, ETAValue, ETRValue, assignedTo, remarks,
				TicketValue);
	}

	@Description("Verify test Login To Reassign User")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 6,enabled = false)
	public void testLoginToReassignUser(String username, String password)
			throws InterruptedException, java.util.concurrent.TimeoutException, FileNotFoundException, IOException {
		refreshPage();
		troubleTicket.performLogout();
		Allure.step("/* Reassigned user should able to take action on Ticket */");
		login.doLogin(username, password);
	}

	@Description(" Verify Assigned user should able to resolve the ticket")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 7, dependsOnMethods = {
			"testLogin" })
	public void verifyTicketIsResolved(String alarmDescription, String siteId, String rcaCategory,
			String rcaSubCategory, String rcaReason, String remarks, String TicketValue,String selectFaultArea,String faultAreaDetails,String resolutionMethod)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handleResolvedTicket(alarmDescription, siteId, rcaCategory, rcaSubCategory, rcaReason, remarks,
				TicketValue,selectFaultArea,faultAreaDetails,resolutionMethod);
		troubleTicket.exportAuditLogFile();

	}

	@Description("Verify test Login To TT closure Or Creater group")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 8,enabled = false)
	public void testLoginToTTclosureOrCreater(String username, String password)
			throws InterruptedException, java.util.concurrent.TimeoutException, FileNotFoundException, IOException {
		refreshPage();
		troubleTicket.performLogout();
		login.doLogin(username, password);

	}

	@Description("Verify TT closure group user should able to close the ticket")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 9, dependsOnMethods = {
			"testLogin" })
	public void verifyTicketIsClosed(String alarmDescription, String siteId, String rcaCategory, String rcaSubCategory,
			String rcaReason, String TicketValue)
			throws InterruptedException, TimeoutException, FileNotFoundException, IOException {
		base.refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handleClosedTicket(alarmDescription, siteId, rcaCategory, rcaSubCategory, rcaReason, TicketValue);
	}

	@Description("Verify TT reversal Group user can reassign the TT from Resolve to assign")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 10, dependsOnMethods = {
			"testLogin" })
	public void verifyResolvedToAssignStatus(String alarmDescription, String siteId, String TicketValue)
			throws InterruptedException, TimeoutException, FileNotFoundException, IOException {
		base.refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyTicketStatusFromResolvedToAssignByTTReversalGroup(alarmDescription, siteId, TicketValue);
	}

	@Description("Verify Shorting (short on columns) should be working fine")
	@Test(priority = 11, dependsOnMethods = { "testLogin" })
	public void sortingFunctionalitiesOnDataGridUI()
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		Thread.sleep(5000);
		explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
		handleCalenderOnTroubleTicket(1);
		sa.assertEquals(getText("ticketIdText_XPATH"), "ticket Id");
		troubleTicket.changeticketIdTodescendingOrder();
		troubleTicket.verifyDescendingOrder();
		troubleTicket.changeTicketIdToAscendingOrder();
		troubleTicket.verifyAscendingOrder();
		click("ticketIdText_XPATH");
		sa.assertEquals(getText("siteIdText_XPATH"), "Site Id");
		troubleTicket.changesiteIdTodescendingOrder();
		troubleTicket.verifyDescendingOrder();
		troubleTicket.changeSiteIdToAscendingOrder();
		troubleTicket.verifyAscendingOrder();
		click("siteIdText_XPATH");
		scrollForAGGrid();

	}

	@Description("Verify Paging (number of TT in a page) should be working fine")
	@Test(priority = 12, dependsOnMethods = { "testLogin" })
	public void paginationFunctionalitiesOnDataGridUI() throws InterruptedException, FileNotFoundException, IOException,
			TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handlePaginationOnDataGridUI();

	}

	@Description("Verify All filters should work fine in TT report")
	@Test(priority = 13, dependsOnMethods = { "testLogin" })
	public void testFilterFunctionalitiesOnTroubleTicket()
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handleSelect_SPM_Region();
		troubleTicket.handleSelect_Sub_Region();
		troubleTicket.handleSelectMiniCluster();
		troubleTicket.handleSelectTicketType();
		troubleTicket.handleSelectEquipment();
		troubleTicket.handleSelectSeverity();
		troubleTicket.handleSelectTicketStatusSelected("Assign");
		troubleTicket.handleSelectGroupOfUsers();
		troubleTicket.handleSelectPowerVendor();
		troubleTicket.handleSelectTicketMode();
		troubleTicket.handleSelectOperator();
		troubleTicket.handleSelectServiceType();
		handleInputFieldByJS("TicKetIDInputField_XPATH", "TT19289582");
		handleInputFieldByJS("siteIDInputField_XPATH", "AYYN_0066");
		handleCalenderOnTroubleTicket(1);

	}

	@Description("Verify Save filter option should be working properly")
	@Test(priority = 14, dependsOnMethods = { "testLogin" })
	public void verifySaveFilter() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.saveFilterFunctionality();
	}

	@Description("Verify Refresh (button) option should work fine")
	@Test(priority = 15, dependsOnMethods = { "testLogin" })
	public void verifyRefreshButton() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.applyRefreshButton();
	}

	@Description("Verify Report should be work in Full screen mode as well")
	@Test(priority = 16, dependsOnMethods = { "testLogin" })
	public void verifyFullScreenMode() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.applyFullScreenMode();

	}

	@Description("Verify RMS Data should be there in alarm desc hover")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 17, dependsOnMethods = {
			"testLogin"})
	public void RMSDataOnAlarmNameColumn(String ticketID, String ticketMode)
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		Thread.sleep(4000);
		troubleTicket.RMSDataForTicketIdShouldReflectOnAlarmDescHover(ticketID, ticketMode);
	}

	@Description("Verify Vendor/Operator SLA should be coming in report if defined in system")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 18, dependsOnMethods = {
			"testLogin" })

	public void verifyDataInSLAReport(String operator, String Vendor)
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifySLAReport(operator, Vendor);
		troubleTicket.handleFromDateCalenderForFilterSLA(2);
		handleClickByJS("filterReport_SLA_XPATH");
		Thread.sleep(2000);

	}

	@Description("Verify Wild search should work in Ticket ID and site id filters")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 19, dependsOnMethods = {
	"testLogin" })
	public void verifyWildSearch(String siteId,String TicketId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.wildSearchBySiteId(siteId);
		troubleTicket.wildSearchByTicketId(TicketId);
	}

	@Description("verify Distance should be come in TT report in case of MobileApp transaction.")
	@Test(priority = 20, dependsOnMethods = { "testLogin" })
	public void distanceValueBasedOnUserLatAndUserLong()
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		Thread.sleep(4000);
		handleCalenderOnTroubleTicket(2);
		troubleTicket.scrollTillDistanceHeaderOfDataGrid();
		troubleTicket.handleUserLatAndUserlong("FileDownloadedForUserLatAndUserLong");
	}

	@Description("Verify User is able to add/resolve/close the ticket through Bulk upload")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 21, dependsOnMethods = {
			"testLogin" })
	public void bulkUploadTroubleTicket(String siteId) throws InterruptedException, TimeoutException,
			java.util.concurrent.TimeoutException, IOException, CsvException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.addTicketbulkUpload();
		troubleTicket.addTicket_ReportFilterAndExport("Assign", siteId);
		troubleTicket.writeData("Resolve");
		troubleTicket.writeData("Close");

		/*
		 * troubleTicket.landingOnTroubleTicketPageUsingJS();
		 * troubleTicket.resolvedTicketbulkUpload();
		 * troubleTicket.landingOnTroubleTicketPageUsingJS();
		 * troubleTicket.closedTicketbulkUpload();
		 * troubleTicket.addTicket_ReportFilterAndExport("Closed",siteId);
		 */

	}

	@Description("Verify TMS User can Assign RTTS ticket from Referred to Assign.")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 22, dependsOnMethods = {
			"testLogin" })
	public void verifyReferredRTTStoAssign(String siteId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyStatusReferredToAssign(siteId, BaseDataForAPI.prNumber);

	}

	@Description("Verify TMS User can Assign RTTS ticket from Assign To Inprogress.")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 23, dependsOnMethods = {
			"testLogin" })
	public void verifyAssignRTTSToInProgress(String siteId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyStatusAssignToInprogress(siteId, BaseDataForAPI.prNumber);

	}

	@Description("Verify TMS can Restore the RTTS ticket with all the mandatory fields.")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 24)
	public void verifyRestoreTheRTTSticket(String siteId, String rcaCategory, String rcaSubCategory, String rcaReason,String selectFaultArea,String faultAreaDetails,String resolutionMethod)
			throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyRestoreTheRTTSticket(siteId, BaseDataForAPI.prNumber, rcaCategory, rcaSubCategory,
				rcaReason,selectFaultArea,faultAreaDetails,resolutionMethod);
	}

	@Description("Verify Once RTTS Ticket is Resolved from TMS then closure will be done from RTTS")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 25)
	public void verifyResolvedTheRTTSticket(String siteId,String statusReason) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyResolvedTheRTTSticket(siteId, BaseDataForAPI.prNumber,statusReason);

	}

	@Description("Verify If RTTS Closed the ticket then same status should be updated at TMS end also.")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 26)
	public void verifyClosedTheRTTSticket(String siteId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifyClosedTicket(siteId, BaseDataForAPI.prNumber);

	}

}