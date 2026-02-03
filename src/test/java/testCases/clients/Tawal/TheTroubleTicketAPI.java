package testCases.clients.Tawal;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import pages.AllClientsBasePages.BasePage;
import pages.iTower_Tawal.BaseDataForAPI;
import pages.iTower_Tawal.TroubleTicketingPage;
import utilities.DataUtilities_Tawal;

public class TheTroubleTicketAPI extends BasePage {
	BasePage base = new BasePage();
	TroubleTicketingPage troubleTicket = new TroubleTicketingPage();

	@BeforeClass
	public void setUp() {
		System.out.println("Skipping browser setup for API tests");
	}

	@BeforeMethod
	public void baseURIForAPI() {
		RestAssured.baseURI = "http://192.168.0.35:7003";
	}

	@Test(priority = 1)
	public void getTokenAPI() {
		Response response = given().body(pages.iTower_Tawal.getJsonBodyForAPI.BodyForGetToken())
				.header("Content-Type", "application/json").when().post("/api/Service/GetToken");

		// Validate response
		assertEquals(response.getStatusCode(), 200, "Unexpected status code");
		assertFalse(response.getBody().asString().isEmpty(), "Empty response body");

		// Process response
		String strResp = response.asPrettyString();
		Allure.step("Token Response: " + strResp);

		JsonPath jp = response.jsonPath();
		String tokenvalue = jp.getString("AccessToken");
		assertNotNull(tokenvalue, "AccessToken not found in response");
		assertFalse(tokenvalue.isEmpty(), "AccessToken is empty");
		BaseDataForAPI.tokenid = tokenvalue;
	}

	@Test(dataProviderClass = DataUtilities_Tawal.class, dataProvider = "dataproTawal", priority = 2, dependsOnMethods = "getTokenAPI")
	public void ticketCreationAPI(String siteId) {
		// Validate token
		assertNotNull(pages.iTower_Tawal.BaseDataForAPI.tokenid, "Token not available - getToken may have failed");
		assertFalse(pages.iTower_Tawal.BaseDataForAPI.tokenid.isEmpty(), "Token is empty");

		Response response = given().auth().oauth2(pages.iTower_Tawal.BaseDataForAPI.tokenid)
				.header("Content-Type", "application/json")
				.body(pages.iTower_Tawal.getJsonBodyForAPI.getBodyForTTAPI(TroubleTicketingPage.generatePRNumber(),
						siteId, TroubleTicketingPage.generateEpochTime(), TroubleTicketingPage.generateEpochTime()))
				.when().post("/api/iMaintain/ttCreation");

		// Validate response
		assertEquals(response.getStatusCode(), 200, "Unexpected status code");
		assertFalse(response.getBody().asString().isEmpty(), "Empty response body");

		// Process response
		Allure.step("/* Need to check get ticket API is giving correct response from Postman */");
		String strResp = response.asPrettyString();
		Allure.step("Ticket Details Response: " + strResp);

		JsonPath jp = response.jsonPath();
		String prNumber = jp.getString("prNo");
		assertNotNull(prNumber, "No pr found in response");
		assertFalse(prNumber.isEmpty(), "pr No is empty");
		pages.iTower_Tawal.BaseDataForAPI.prNumber = prNumber;
	}

	@Test(priority = 3, dependsOnMethods = "getTokenAPI")
	public void TicketUpdateToReject() {
		// Validate token
		assertNotNull(pages.iTower_Tawal.BaseDataForAPI.tokenid, "Token not available - getToken may have failed");
		assertFalse(pages.iTower_Tawal.BaseDataForAPI.tokenid.isEmpty(), "Token is empty");

		Response response = given().auth().oauth2(pages.iTower_Tawal.BaseDataForAPI.tokenid)
				.header("Content-Type", "application/json")
				.body(pages.iTower_Tawal.getJsonBodyForAPI
						.getBodyForTicketUpdateRejectAndApprove(BaseDataForAPI.prNumber, "In Progress", "Rejected"))
				.when().put("/api/iMaintain/ttUpdation");

		// Validate response
		assertEquals(response.getStatusCode(), 200, "Unexpected status code");
		assertFalse(response.getBody().asString().isEmpty(), "Empty response body");

		// Process response
		Allure.step("/* Need to check get ticket API is giving correct response from Postman */");
		String strResp = response.asPrettyString();
		Allure.step("Ticket Details Response: " + strResp);

		JsonPath jp = response.jsonPath();
		String processStatus = jp.getString("processStatus");
		assertEquals(processStatus, "Success");

	}

	@Test(priority = 4, dependsOnMethods = "getTokenAPI")
	public void TicketUpdateToApprove() {
		// Validate token
		assertNotNull(pages.iTower_Tawal.BaseDataForAPI.tokenid, "Token not available - getToken may have failed");
		assertFalse(pages.iTower_Tawal.BaseDataForAPI.tokenid.isEmpty(), "Token is empty");

		Response response = given().auth().oauth2(pages.iTower_Tawal.BaseDataForAPI.tokenid)
				.header("Content-Type", "application/json")
				.body(pages.iTower_Tawal.getJsonBodyForAPI
						.getBodyForTicketUpdateRejectAndApprove(BaseDataForAPI.prNumber, "Restored", "Approved"))
				.when().put("/api/iMaintain/ttUpdation");

		// Validate response
		assertEquals(response.getStatusCode(), 200, "Unexpected status code");
		assertFalse(response.getBody().asString().isEmpty(), "Empty response body");

		// Process response
		Allure.step("/* Need to check get ticket API is giving correct response from Postman */");
		String strResp = response.asPrettyString();
		Allure.step("Ticket Details Response: " + strResp);

		JsonPath jp = response.jsonPath();
		String processStatus = jp.getString("processStatus");
		assertEquals(processStatus, "Success");

	}

	@Test(priority = 5, dependsOnMethods = "getTokenAPI")
	public void TicketUpdateToClose() {
		// Validate token
		assertNotNull(pages.iTower_Tawal.BaseDataForAPI.tokenid, "Token not available - getToken may have failed");
		assertFalse(pages.iTower_Tawal.BaseDataForAPI.tokenid.isEmpty(), "Token is empty");

		Response response = given().auth().oauth2(pages.iTower_Tawal.BaseDataForAPI.tokenid)
				.header("Content-Type", "application/json").body(pages.iTower_Tawal.getJsonBodyForAPI
						.getBodyForTicketUpdateClose(BaseDataForAPI.prNumber, "Closed"))
				.when().put("/api/iMaintain/ttUpdation");

		// Validate response
		assertEquals(response.getStatusCode(), 200, "Unexpected status code");
		assertFalse(response.getBody().asString().isEmpty(), "Empty response body");

		// Process response
		Allure.step("/* Need to check get ticket API is giving correct response from Postman */");
		String strResp = response.asPrettyString();
		Allure.step("Ticket Details Response: " + strResp);

		JsonPath jp = response.jsonPath();
		String processStatus = jp.getString("processStatus");
		assertEquals(processStatus, "Success");

	}

}