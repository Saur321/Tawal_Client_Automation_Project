package pages.iTower_Tawal;

public class getJsonBodyForAPI extends BaseDataForAPI{
	
	
	public static String BodyForGetToken()
	{
		String body = "{\r\n"
                + "    \"username\": \"amistya\",\r\n"
                + "    \"password\": \"a\"\r\n"
                + "}";
		
		return body;
	}
	
	public static String getBodyForTTAPI(String prNo, String siteId, String reportedDate, String serviceImpactStart) {
	    String body = String.format("{\r\n"
	        + "  \"prNo\": \"%s\",\r\n"
	        + "  \"siteId\": \"%s\",\r\n"
	        + "  \"Node\": \"\",\r\n"
	        + "  \"network\": \"MOBILE\",\r\n"
	        + "  \"problemType\": \"Data Node\",\r\n"
	        + "  \"abstractValue\": \"Tawal Test Ticket_011\",\r\n"
	        + "  \"description\": null,\r\n"
	        + "  \"reportedDate\": \"%s\",\r\n"
	        + "  \"status\": \"Referred\",\r\n"
	        + "  \"refToGroup\": \"TWL_SREriE&M\",\r\n"
	        + "  \"severity\": \"Low\",\r\n"
	        + "  \"serviceImpacted\": \"Degraded\",\r\n"
	        + "  \"serviceImpactStart\": \"%s\",\r\n"
	        + "  \"quantityEquivCircuit\": \"0\",\r\n"
	        + "  \"servicesAffected\": \"FTTH\",\r\n"
	        + "  \"initialAnalysis\": \"\",\r\n"
	        + "  \"xReferences\": null,\r\n"
	        + "  \"twoGNode\": \"1\",\r\n"
	        + "  \"threeGNode\": \"1\",\r\n"
	        + "  \"Lte\": \"11\",\r\n"
	        + "  \"iPSite\": \"1\",\r\n"
	        + "  \"originatorId\": \"1001\",\r\n"
	        + "  \"originatorGroup\": \"112\",\r\n"
	        + "  \"lastReferredGroup\": \"Service Desk\",\r\n"
	        + "  \"intRef\": null,\r\n"
	        + "  \"RestorationApproval\": null,\r\n"
	        + "  \"workInfo\": null,\r\n"
	        + "  \"workInfoSummary\": null,\r\n"
	        + "  \"flag\": null,\r\n"
	        + "  \"WorklogAttachments\": null,\r\n"
	        + "  \"SolutionDetails\": null,\r\n"
	        + "  \"latitude\": null,\r\n"
	        + "  \"longitude\": null,\r\n"
	        + "  \"siteLocation\": null,\r\n"
	        + "  \"siteName\": \"Udhailiyah-GOSP12\",\r\n"
	        + "  \"ttCreatedDate\": \"2024-09-11T14:32:06+03:00\"\r\n"
	        + "}",
	        prNo,
	        siteId,
	        reportedDate,
	        serviceImpactStart
	    );
	    return body;
	}
	
	public static String getBodyForTicketUpdateRejectAndApprove(String prNo, String status, String restorationApproval) {
	    String body = String.format("{\r\n"
	        + "  \"prNo\": \"%s\",\r\n"
	        + "  \"intRef\": \"123\",\r\n"
	        + "  \"status\": \"%s\",\r\n"
	        + "  \"RestorationApproval\": \"%s\",\r\n"
	        + "  \"refToGroup\": \"\",\r\n"
	        + "  \"lastReferredGroup\": \"\",\r\n"
	        + "  \"workInfo\": \"\",\r\n"
	        + "  \"workInfoSummary\": \"\",\r\n"
	        + "  \"flag\": \"ticket rps decision by rtts\",\r\n"
	        + "  \"WorklogAttachments\": [{\r\n"
	        + "    \"file\": \"\",\r\n"
	        + "    \"name\": \"\",\r\n"
	        + "    \"type\": \"\",\r\n"
	        + "    \"latitude\": \"\",\r\n"
	        + "    \"longitude\": \"\",\r\n"
	        + "    \"time\": \"\"\r\n"
	        + "  }],\r\n"
	        + "  \"SolutionDetails\": \"\",\r\n"
	        + "  \"severity\": \"\",\r\n"
	        + "  \"serviceImpacted\": \"\",\r\n"
	        + "  \"serviceImpactStart\": \"\",\r\n"
	        + "  \"servicesAffected\": \"\"\r\n"
	        + "}",
	        prNo,
	        status,
	        restorationApproval
	    );
	    return body;
	}
	
	public static String getBodyForTicketUpdateClose(String prNo, String status) {
	    String body = String.format("{\r\n"
	        + "  \"prNo\": \"%s\",\r\n"
	        + "  \"intRef\": \"123\",\r\n"
	        + "  \"status\": \"%s\",\r\n"
	        + "  \"RestorationApproval\": \"\",\r\n"
	        + "  \"refToGroup\": \"TWL_SREriE&M\",\r\n"
	        + "  \"lastReferredGroup\": \"TWL_SREriE&M\",\r\n"
	        + "  \"workInfo\": \"1111\",\r\n"
	        + "  \"workInfoSummary\": \"\",\r\n"
	        + "  \"flag\": \"Ticket Close Notification by RTTS\",\r\n"
	        + "  \"WorklogAttachments\": [{\r\n"
	        + "    \"file\": \"\",\r\n"
	        + "    \"name\": \"\",\r\n"
	        + "    \"type\": \"\",\r\n"
	        + "    \"latitude\": \"\",\r\n"
	        + "    \"longitude\": \"\",\r\n"
	        + "    \"time\": \"\"\r\n"
	        + "  }],\r\n"
	        + "  \"SolutionDetails\": \"aa\",\r\n"
	        + "  \"severity\": \"\",\r\n"
	        + "  \"serviceImpacted\": \"\",\r\n"
	        + "  \"serviceImpactStart\": \"\",\r\n"
	        + "  \"servicesAffected\": \"\"\r\n"
	        + "}",
	        prNo,
	        status
	    );
	    return body;
	}

}
