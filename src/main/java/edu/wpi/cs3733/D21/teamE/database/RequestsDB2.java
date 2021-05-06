package edu.wpi.cs3733.D21.teamE.database;

import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.ExternalPatientObj;
import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.FloralObj;
import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.LanguageInterpreterObj;
import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.LaundryObj;
import edu.wpi.cs3733.D21.teamE.views.serviceRequestObjects.ReligiousRequestObj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequestsDB2 {

	static Connection connection = makeConnection.makeConnection().connection;

	//ALL REQUEST STUFF::::

	/**
	 * Uses executes the SQL statements required to create the requests table.
	 * This table has the attributes:
	 * - requestID: this is used to identify a request. Every request must have one.
	 * - creatorID: this is the username of the user who created the request.
	 * - creationTime: this is a time stamp that is added to the request at the moment it is made.
	 * - requestType: this is the type of request that the user is making. The valid options are: "floral", "medDelivery", "sanitation", "security", "extTransport", 'languageRequest' 'laundryRequest' 'maintenanceRequest' 'foodDelivery' 'internalPatientRequest'
	 * - requestStatus: this is the state in which the request is being processed. The valid options are: "complete", "canceled", "inProgress".
	 * - assigneeID: this is the person who is assigned to the request
	 */
	public static void createRequestsTable() {

		String query = "Create Table requests(" +
				"requestID     int Primary Key, " +
				"creatorID     int References useraccount On Delete Cascade," +
				"creationTime  timestamp, " +
				"requestType   varchar(31), " +
				"requestStatus varchar(10), " +
				"assigneeID    int References useraccount On Delete Cascade," +
				"Constraint requestTypeLimit Check (requestType In ('floral', 'medDelivery', 'sanitation', 'security', 'extTransport', 'internalPatientRequest', 'languageRequest', 'laundryRequest', 'maintenanceRequest', 'foodDelivery', 'religiousRequest')), " +
				"Constraint requestStatusLimit Check (requestStatus In ('complete', 'canceled', 'inProgress')))";

		try (PreparedStatement prepState = connection.prepareStatement(query)) {

			prepState.execute();

		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("error creating requests table");
		}
	}

	public static void addRequest(int userID, int assigneeID, String requestType) {
		String insertRequest = "Insert Into requests " +
				"Values ((Select Count(*) " +
				"         From requests) + 1, ?, Current Timestamp, ?, 'inProgress', ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertRequest)) {
			prepState.setInt(1, userID);
			prepState.setString(2, requestType);
			prepState.setInt(3, assigneeID);
			prepState.execute();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error inserting into requests inside function addRequest()");
		}

	}







	//FLORAL REQUEST STUFF::::

	/**
	 * Uses executes the SQL statements required to create a floralRequests table. This is a type of request and share the same requestID.
	 * This table has the attributes:
	 * - requestID: this is used to identify a request. Every request must have one.
	 * - roomID: this is the nodeID/room the user is sending the request to.
	 * - recipientName: this is the name of the individual they want the flowers to be addressed to
	 * - flowerType: this is the type of flowers that the user wants to request. The valid options are: 'Roses', 'Tulips', 'Carnations', 'Assortment'
	 * - flowerAmount: this the number/quantity of flowers that the user is requesting. The valid options are: 1, 6, 12
	 * - vaseType: this is the type of vase the user wants the flowers to be delivered in. The valid options are: 'Round', 'Square', 'Tall', 'None'
	 * - message: this is a specific detailed message that the user can have delivered with the flowers or an instruction message
	 * *                for whoever is fufilling the request
	 */
	public static void createFloralRequestsTable() {

		String query = "Create Table floralRequests( " +
				"requestID     int Primary Key References requests On Delete Cascade, " +
				"roomID        varchar(31) References node On Delete Cascade, " +
				"recipientName varchar(31), " +
				"flowerType    varchar(31), " +
				"flowerAmount  int, " +
				"vaseType      varchar(31), " +
				"arrangement varchar(31), " +
				"stuffedAnimal varchar(31), " +
				"chocolate varchar(31), " +
				"message       varchar(5000), " +
				"Constraint flowerTypeLimit Check (flowerType In ('Roses', 'Tulips', 'Carnations', 'Assortment')), " +
				"Constraint flowerAmountLimit Check (flowerAmount In (1, 6, 12)), " +
				"Constraint vaseTypeLimit Check (vaseType In ('Round', 'Square', 'Tall', 'None')))";

		try (PreparedStatement prepState = connection.prepareStatement(query)) {

			prepState.execute();

		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("error creating floralRequests table");
		}
	}

	/**
	 * This adds a floral request to the database that the user is making
	 * @param request this is all of the information needed, in a floral request object.
	 */
	public static void addFloralRequest(FloralObj request) {
		addRequest(request.getUserID(), request.getAssigneeID(), "floral");

		String insertFloralRequest = "Insert Into floralrequests Values ((Select Count(*) From requests), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertFloralRequest)) {
			prepState.setString(1, request.getNodeID());
			prepState.setString(2, request.getRecipient());
			prepState.setString(3, request.getFlower());
			prepState.setInt(4, request.getCount());
			prepState.setString(5, request.getVase());
			prepState.setString(6, request.getArrangement());
			prepState.setString(7, request.getStuffedAnimal());
			prepState.setString(8, request.getChocolate());
			prepState.setString(9, request.getMessage());

			prepState.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error inserting into floralRequests inside function addFloralRequest()");
		}
	}

	/**
	 * This edits a floral request form that is already in the database
	 * @param request this the information that the user wants to change stored in a floral request object. (If int = 0 --> do not change, If String = null --> do not change)
	 * @return
	 */
	public static int editFloralRequest(FloralObj request) {

		boolean added = false;
		String query = "Update floralRequests Set ";

		if (request.getRecipient() != null) {
			query = query + "recipientName = '" + request.getRecipient() + "'";

			added = true;
		}
		if (request.getNodeID() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " roomID = '" + request.getNodeID() + "'";
			added = true;
		}
		if (request.getFlower() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " flowerType = '" + request.getFlower() + "'";
			added = true;
		}

		//TODO: changed this line from "request.getCount() != null" because it threw an error. Need make sure that I changed it ok
		if (request.getCount() > 0) {
			if (added) {
				query = query + ", ";
			}
			query = query + " flowerAmount = " + request.getCount();
			added = true;
		}
		if (request.getVase() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " vaseType = '" + request.getVase() + "'";
			added = true;
		}
		if (request.getArrangement() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " arrangement = '" + request.getArrangement() + "'";
			added = true;
		}
		if (request.getStuffedAnimal() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " stuffedAnimal = '" + request.getStuffedAnimal() + "'";
			added = true;
		}
		if (request.getChocolate() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " chocolate = '" + request.getChocolate() + "'";
			added = true;
		}
		if (request.getMessage() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " message = '" + request.getMessage() + "'";
			added = true;
		}

		query = query + " where requestID = " + request.getRequestID();
		try (PreparedStatement prepState = connection.prepareStatement(query)) {
			prepState.executeUpdate();
			prepState.close();
			return 1;
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error in updating floral request");
			return 0;
		}
	}





	//LANGUAGE REQUEST STUFF::::

	/**
	 * adds a language request to the languageRequest table
	 * @param request this is all of the information needed, in a language request object.
	 */
	public static void addLanguageRequest(LanguageInterpreterObj request) {
//		addRequest(userID, assigneeID, "languageRequest");
		addRequest(request.getUserID(), request.getAssigneeID(), "languageRequest");

		String insertLanguageReq = "Insert Into languageRequest Values ((Select Count(*) From requests), ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertLanguageReq)) {
			prepState.setString(1, request.getNodeID());
			prepState.setString(2, request.getLanguage());
			prepState.setString(3, request.getDescription());

			prepState.execute();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error inserting into languageRequest inside function addLanguageRequest()");
		}

	}

	/**
	 * This edits a language request form that is already in the database
	 * @param request this the information that the user wants to change stored in a language request object. (If int = 0 --> do not change, If String = null --> do not change)
	 * @return
	 */
	public static int editLanguageRequest(LanguageInterpreterObj request) {
		boolean added = false;
		String query = "Update languageRequest Set ";

		if (request.getNodeID() != null) {
			query = query + " roomID = '" + request.getNodeID() + "'";
			added = true;
		}
		if (request.getLanguage() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + "languageType = '" + request.getLanguage() + "'";
			added = true;
		}
		if (request.getDescription() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + "description = '" + request.getDescription() + "'";
		}

		query = query + " where requestID = " + request.getRequestID();
		try (PreparedStatement prepState = connection.prepareStatement(query)) {
			prepState.executeUpdate();
			prepState.close();
			return 1;
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error in updating languageRequest");
			return 0;
		}

	}






	// LANGUAGE REQUESTS STUFF::::
	/**
	 * adds a language request to the religiousRequest table
	 * @param request this is all of the information needed, in a religious request object.
	 */
	public static void addReligiousRequest(ReligiousRequestObj request) {
		addRequest(request.getUserID(), request.getAssigneeID(), "religiousRequest");

		String insertMaintenanceReq = "Insert Into religiousRequest Values ((Select Count(*) From requests), ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertMaintenanceReq)) {
			prepState.setString(1, request.getNodeID());
			prepState.setString(2, request.getReligion());
			prepState.setString(3, request.getDescription());

			prepState.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error inserting into religiousRequest inside function addReligiousRequest()");
		}
	}


	/**
	 * This edits a religious request form that is already in the database
	 * @param request this the information that the user wants to change stored in a religious request object. (If int = 0 --> do not change, If String = null --> do not change)
	 * @return
	 */
	public static int editReligiousRequest(ReligiousRequestObj request) {
		boolean added = false;
		String query = "Update religiousRequest Set ";

		if (request.getNodeID() != null) {
			query = query + "roomID = '" + request.getNodeID() + "'";
			added = true;
		}
		if (request.getReligion() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + "religionType = '" + request.getReligion() + "'";
			added = true;
		}
		if (request.getDescription() != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + "description = '" + request.getDescription() + "'";
		}

		query = query + " where requestID = " + request.getRequestID();
		try (PreparedStatement prepState = connection.prepareStatement(query)) {
			prepState.executeUpdate();
			prepState.close();
			return 1;
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error in updating religiousRequest");
			return 0;
		}
	}










	//EXTERNAL PATIENT REQUEST STUFF:

	/**
	 * Uses executes the SQL statements required to create a extTransport table. This is a type of request and share the same requestID.
	 * This table has the attributes:
	 * - requestID: this is used to identify a request. Every request must have one.
	 * - roomID: this is the nodeID/room the user is sending the request to.
	 * - requestType: this the mode of transportation that the request is being made for. The valid options are: 'Ambulance', 'Helicopter', 'Plane'
	 * - severity: this is how sever the patient is who the user/first responders are transporting.
	 * - patientID: this is the ID of the patient that is being transported.
	 * - ETA: this is the estimated time the patient will arrive.
	 * - description: this is a detailed description of request that generally includes what happened to the patient and their current situation.
	 */
	public static void createExtTransportTable() {

		String query = "Create Table extTransport( " +
				"    requestID int Primary Key References requests On Delete Cascade, " +
				"    roomID varchar(31) Not Null References node On Delete Cascade, " +
				"    requestType varchar(100) Not Null, " +
				"    severity varchar(30) Not Null, " +
				"    patientID varchar(31) Not Null, " +
				"    ETA varchar(100), " +
				"    bloodPressure varchar(31), " +
				"    temperature varchar(31), " +
				"    oxygenLevel varchar(31), " +
				"    description varchar(5000)," +
				"    Constraint requestTypeLimitExtTrans Check (requestType In ('Ambulance', 'Helicopter', 'Plane'))" +
				")";

		try (PreparedStatement prepState = connection.prepareStatement(query)) {

			prepState.execute();

		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("error creating extTransport table");
		}
	}

	/**
	 * This function needs to add a external patient form to the table for external patient forms
	 * //@param form this is the form that we will create and send to the database
	 */
	public static void addExternalPatientRequest(ExternalPatientObj externalPatientObj) {
		// int userID, int assigneeID, String roomID, String requestType, String severity, String patientID, String ETA, String bloodPressure, String temperature, String oxygenLevel, String description

		int userID = externalPatientObj.getUserID();
		int assigneeID = externalPatientObj.getAssigneeID();
		String roomID = externalPatientObj.getNodeID();
		String requestType = externalPatientObj.getRequestType();
		String severity = externalPatientObj.getSeverity();
		String patientID = externalPatientObj.getPatientID();
		String ETA = externalPatientObj.getEta();
		String bloodPressure = externalPatientObj.getBloodPressure();
		String temperature = externalPatientObj.getTemperature();
		String oxygenLevel = externalPatientObj.getOxygenLevel();
		String description = externalPatientObj.getDetails();

		addRequest(userID, assigneeID, "extTransport");

		String insertExtTransport = "Insert Into exttransport " +
				"Values ((Select Count(*) " +
				"         From requests), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertExtTransport)) {
			prepState.setString(1, roomID);
			prepState.setString(2, requestType);
			prepState.setString(3, severity);
			prepState.setString(4, patientID);
			prepState.setString(5, ETA);
			prepState.setString(6, bloodPressure);
			prepState.setString(7, temperature);
			prepState.setString(8, oxygenLevel);
			prepState.setString(9, description);

			prepState.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error inserting into extTransport inside function addExternalPatientRequest()");
		}

	}


	/**
	 * This edits a External Transport Services form that is already in the database
	 * takes in an External Patient Object
	 * @return 1 if the update was successful, 0 if it failed
	 */
	public static int editExternalPatientRequest(ExternalPatientObj externalPatientObj) {

		//int requestID, String roomID, String requestType, String severity, String patientID, String description, String ETA, String bloodPressure, String temperature, String oxygenLevel
		String roomID = externalPatientObj.getNodeID();
		String requestType = externalPatientObj.getRequestType();
		String severity = externalPatientObj.getSeverity();
		String patientID = externalPatientObj.getPatientID();
		String ETA = externalPatientObj.getEta();
		String bloodPressure = externalPatientObj.getBloodPressure();
		String temperature = externalPatientObj.getTemperature();
		String oxygenLevel = externalPatientObj.getOxygenLevel();
		String description = externalPatientObj.getDetails();

		boolean added = false;
		String query = "Update extTransport Set ";

		if (roomID != null) {
			query = query + " roomID = '" + roomID + "'";

			added = true;
		}
		if (requestType != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " requestType = '" + requestType + "'";
			added = true;
		}
		if (severity != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " severity = '" + severity + "'";
			added = true;
		}
		if (patientID != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " patientID = '" + patientID + "'";
			added = true;
		}
		if (description != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " description = '" + description + "'";
			added = true;
		}
		if (ETA != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " ETA = '" + ETA + "'";
			added = true;
		}
		if (bloodPressure != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " bloodPressure = '" + bloodPressure + "'";
			added = true;
		}
		if (temperature != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " temperature = '" + temperature + "'";
			added = true;
		}
		if (oxygenLevel != null) {
			if (added) {
				query = query + ", ";
			}
			query = query + " oxygenLevel = '" + oxygenLevel + "'";
			added = true;
		}

		query = query + " where requestID = " + externalPatientObj.getRequestID();

		try (PreparedStatement prepState = connection.prepareStatement(query)) {
			prepState.executeUpdate();
			prepState.close();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error in updating external transport request");
			return 0;
		}
	}


	//LAUNDRY REQUEST STUFF::::

	/**
	 * adds a laundry request to the laundryRequest table
	 * @param request this is all of the information needed, in a religious request object.
	 */
	public static void addLaundryRequest(LaundryObj request) {
//		addRequest(userID, assigneeID, "laundryRequest");
		addRequest(request.getUserID(), request.getAssigneeID(), "laundryRequest");

		String insertLaundryReq = "Insert Into laundryRequest Values ((Select Count(*) From requests), ?, ?, ?, ?)";

		try (PreparedStatement prepState = connection.prepareStatement(insertLaundryReq)) {
//			prepState.setString(1, roomID);
//			prepState.setString(2, washLoadAmount);
//			prepState.setString(3, dryLoadAmount);
//			prepState.setString(4, description);
			prepState.setString(1, request.getNodeID());
			prepState.setString(2, request.getWashLoadAmount());
			prepState.setString(3, request.getDryLoadAmount());
			prepState.setString(4, request.getDescription());

			prepState.execute();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.err.println("Error inserting into laundryRequest inside function addLanguageRequest()");
		}
	}




}
