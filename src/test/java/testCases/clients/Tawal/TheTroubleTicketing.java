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
import pages.iTower_Tawal.TroubleTicketingPage;
import utilities.DataUtilities_Tawal;

public class TheTroubleTicketing extends BasePage {
	BasePage base = new BasePage();
	LoginPage login = new LoginPage();
	SoftAssert sa = new SoftAssert();
	TroubleTicketingPage troubleTicket = new TroubleTicketingPage();

	@Description("Verify Login Page functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 1)

	public void testLogin(String clientName, String username, String password) throws InterruptedException,
			java.util.concurrent.TimeoutException, FileNotFoundException, IOException, CsvException {
		base.setUp(clientName);
		login.doLogin(username, password);
	}

	

	@Description("Verify Add New Trouble Ticket functionalities")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 2, dependsOnMethods = {
			"testLogin" })
	public void addTroubleTicket(String siteId, String ticketType, String severity, String assignedTo,
			String alarmDescription,String serviceImpact)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
	refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		sa.assertEquals(base.getText("troubleTickting_XPATH"), "Trouble Ticketing");
		troubleTicket.addNewTroubleTicket(siteId, ticketType, severity, assignedTo, alarmDescription,serviceImpact);

	}

	

	@Description("Verify Assigned user should able to update ETA/ETR")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 3, dependsOnMethods = {
			"testLogin" })
	public void updateETAndETR(String alarmDescription, String siteId, String ETAValue, String ETRValue,
			String assignedTo, String remarks) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.updateETAAndETRByAssignUser(alarmDescription, siteId, ETAValue, ETRValue, assignedTo, remarks);
	}

	

	@Description(" Verify ticket is resolved by Assign User and Check Assign User is able to add spare part")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 4, dependsOnMethods = {
			"testLogin" })
	public void addSparePartWhileRT(String alarmDescription, String siteId, String rcaCategory, String rcaSubCategory,
			String rcaReason, String remarks, String selectSparePartsCategory, String selectSpareParts,
			String selectQuantities)
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handleResolvedTicket(alarmDescription, siteId, rcaCategory, rcaSubCategory, rcaReason);
		troubleTicket.addSparePartsInsideResolvedTicket(remarks, selectSparePartsCategory, selectSpareParts,
				selectQuantities);
		troubleTicket.exportAuditLogFile();

	}

	


	@Description("Verify ticket closure permissions for both Creator and TT Closure Group users, along with validation of generated child tickets")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 5, dependsOnMethods = {
			"testLogin" })
	public void addChildTicketWhileClosingT(String alarmDescription, String siteId, String rcaCategory,
			String rcaSubCategory, String rcaReason)
			throws InterruptedException, TimeoutException, FileNotFoundException, IOException {
		base.refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handleClosedTicket(alarmDescription, siteId, rcaCategory, rcaSubCategory, rcaReason);
	}

	

	
	@Description("Verify Sorting Functionalities On Data Grid UI")
	@Test(priority = 6, dependsOnMethods = { "testLogin" })
	public void sortingFunctionalitiesOnDataGridUI()
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
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

	@Description("Verify Pagination Functionalities On Data Grid UI")
	@Test(priority = 7, dependsOnMethods = { "testLogin" })
	public void paginationFunctionalitiesOnDataGridUI() throws InterruptedException, FileNotFoundException, IOException,
			TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.handlePaginationOnDataGridUI();

	}

	

	

	

	@Description("Verify All filters work fine in TT report")
	@Test(priority = 8, dependsOnMethods = { "testLogin" })
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
		troubleTicket.handleSelectTicketStatusSelected();
		troubleTicket.handleSelectGroupOfUsers();
     	troubleTicket.handleSelectPowerVendor();
		troubleTicket.handleSelectTicketMode();
		troubleTicket.handleSelectOperator();
		troubleTicket.handleSelectServiceType();
		handleInputFieldByJS("TicKetIDInputField_XPATH", "TT19289582");
		handleInputFieldByJS("siteIDInputField_XPATH", "AYYN_0066");
		handleCalenderOnTroubleTicket(1);

	}

	

	@Description("Save filter option should be working properly")
	@Test(priority = 9, dependsOnMethods = { "testLogin" })
	public void verifySaveFilter() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.saveFilterFunctionality();
	}

	@Description("Refresh (button) option should work fine")
	@Test(priority = 10, dependsOnMethods = { "testLogin" })
	public void verifyRefreshButton() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.applyRefreshButton();
	}

	@Description("Report should be work in Full screen mode as well")
	@Test(priority = 11, dependsOnMethods = { "testLogin" })
	public void verifyFullScreenMode() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.applyFullScreenMode();

	}

	@Description("RMS Data should be there in alarm desc hover")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 12, dependsOnMethods = {
			"testLogin" })
	public void RMSDataOnAlarmNameColumn(String ticketID, String ticketMode)
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.RMSDataForTicketIdShouldReflectOnAlarmDescHover(ticketID, ticketMode);
	}

	@Description("Verify Vendor/Operator SLA should be coming in report if defined in system")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 13, dependsOnMethods = {
			"testLogin" })

	public void verifyDataInSLAReport(String operator, String Vendor)
			throws InterruptedException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifySLAReport(operator, Vendor);
		troubleTicket.handleFromDateCalenderForFilterSLA(2);
		handleClickByJS("filterReport_SLA_XPATH");

	}
	@Description("Report should be work in Full screen mode as well")
	@Test(priority = 14, dependsOnMethods = { "testLogin" })
	public void verifyWildSearch() throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.wildSearchBySiteId("1000006");
		troubleTicket.wildSearchByTicketId("TT00709125");
	}
	@Description("Report should be work in Full screen mode as well")
	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 15, dependsOnMethods = {
	"testLogin" })
	public void verifySiteIdAutoFilled(String opcoId) throws InterruptedException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.verifySiteIdAutoFilledByOPCOSiteId(opcoId);
	}
	@Description("verify UI records with downloaded CSV file data")
	@Test(priority = 16, dependsOnMethods = { "testLogin" })
	public void verifyUIRecordsWithDownloadedCSVFileData() throws InterruptedException, FileNotFoundException,
			IOException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		Thread.sleep(1000);
		handleCalenderOnTroubleTicket(2);
		troubleTicket.createAgGridTableFile();
		String renamedFile = troubleTicket.downloadFileAndRename();
		troubleTicket.verifyAllRecordsFromSecondFileExistInMainFile(renamedFile);

	}
	@Description("verify Distance in kms value with User Lat and User Long column")
	@Test(priority = 17, dependsOnMethods = { "testLogin" })
	public void distanceValueBasedOnUserLatAndUserLong()
			throws InterruptedException, TimeoutException, java.util.concurrent.TimeoutException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
	//	handleClickByJS("ShowFilterOnTroubleTicketingPage_XPATH");
		Thread.sleep(1000);
		handleCalenderOnTroubleTicket(11);
		troubleTicket.scrollTillDistanceHeaderOfDataGrid();
		troubleTicket.handleUserLatAndUserlong("FileDownloadedForUserLatAndUserLong");
	}
	@Description("Verify User is able to add/resolve/close the ticket through Bulk upload")
	@Test(priority = 18, dependsOnMethods = { "testLogin" })
	public void bulkUploadTroubleTicket() throws InterruptedException, TimeoutException,
			java.util.concurrent.TimeoutException, IOException, CsvException {
		refreshPage();
		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.addTicketbulkUpload();
		troubleTicket.addTicket_ReportFilterAndExport();

		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.writeData("Resolved");
		troubleTicket.resolvedTicketbulkUpload();
		troubleTicket.resolvedTicket_ReportFilterAndExport();

		troubleTicket.landingOnTroubleTicketPageUsingJS();
		troubleTicket.writeData("Closed");
		troubleTicket.closedTicketbulkUpload();
		troubleTicket.closedTicket_ReportFilterAndExport();
	}

}