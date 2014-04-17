var mysql = require('mysql');

var connection = mysql.createConnection({
	host     : 'localhost',
	user     : 'root',
	password : 'password',
	database : 'StudentOrgManager',
	port 	 : '3306'
});

createTables();

//dont pass any params to createTables..doenst work

function createTables(toExecute){

	toExecute = typeof toExecute !== 'undefined' ? toExecute : executeAllStatements;

	var executeAll = false;
	var exit = function(){process.exit(1)};

	function queryConnection(sql, cb){
		connection.query(sql, function(err, result){
			if(err) console.log(err);
			if(executeAll) cb();
		});
	}

	function executeAllStatements(){
		executeAll = true;
		createUsersTable();
	}

	function createUsersTable(){
		var sql = 	"CREATE TABLE Users(" +
					"Username VARCHAR(255) NOT NULL, Password VARCHAR(255) NOT NULL, EMail VARCHAR(255) NOT NULL, " +
					"PhoneNumber VARCHAR(255), FirstName VARCHAR(255), LastName VARCHAR(255), "+
					"Major VARCHAR(255), GraduationYear INT, Bio TEXT, PictureRef TEXT, "+
					"PRIMARY KEY(Username))";
	
		queryConnection(sql, createOrganizationsTable);
	}

	function createOrganizationsTable(){
		var sql =	"CREATE TABLE Organizations(" +
					"OrgName VARCHAR(255), Type VARCHAR(255), Size INT DEFAULT 1, AnnualDues FLOAT, "+
					"PRIMARY KEY(OrgName))";

		queryConnection(sql, createUserOrgsTable);
	}

	function createUserOrgsTable(){
		var sql = 	"CREATE TABLE UserOrgs(" +
					"Username VARCHAR(255), Organization VARCHAR(255), Position VARCHAR(255), " +
					"MemberType VARCHAR(255), DuesPaid VARCHAR(255)," +
					"FOREIGN KEY(Username) REFERENCES Users(Username), "+
					"FOREIGN KEY(Organization) REFERENCES Organizations(OrgName), "+
					"PRIMARY KEY(Username, Organization))";

		queryConnection(sql, createNewsItemsTable);
	}

	function createNewsItemsTable(){
		var sql = 	"CREATE TABLE NewsItems(" +
					"Organization VARCHAR(255), NewsTimeStamp TIMESTAMP DEFAULT NOW(), Announcement TEXT, " +
					"Poster VARCHAR(255), FOREIGN KEY(Organization) REFERENCES Organizations(OrgName), "+
					"FOREIGN KEY(Poster) REFERENCES Users(Username), PRIMARY KEY(Organization, NewsTimeStamp))";
	
		queryConnection(sql, createEventsTable);
	}

	function createEventsTable(){
		var sql =	"CREATE TABLE Events(" +
					"EventID INT NOT NULL AUTO_INCREMENT, EventName VARCHAR(255), Organization VARCHAR(255), " +
					"EventDateTime DATETIME, Location VARCHAR(255), Description TEXT, " +
					"Type VARCHAR(255), FOREIGN KEY(Organization) REFERENCES Organizations(OrgName), " +
					"PRIMARY KEY(EventID))";

		queryConnection(sql, createAbsencesTable);
	}


	function createAbsencesTable(){
		var sql = 	"CREATE TABLE Absences(" +
					"Username VARCHAR(255), Organization VARCHAR(255), EventID INT, " +
					"FOREIGN KEY(Username) REFERENCES Users(Username), " +
					"FOREIGN KEY(Organization) REFERENCES Organizations(OrgName), " +
					"FOREIGN KEY(EventID) REFERENCES Events(EventID), " + 
					"PRIMARY KEY(Username, Organization, EventID))";

		queryConnection(sql, createMessagesTable);
	}


	function createMessagesTable(){
		var sql = 	"CREATE TABLE Messages(" + 
					"MessageID INT NOT NULL AUTO_INCREMENT, MsgContent TEXT, SendingUser VARCHAR(255), " +
					"MsgTimeStamp TIMESTAMP DEFAULT NOW(), MessageType VARCHAR(255), " + 
					"FOREIGN KEY(SendingUser) REFERENCES Users(Username), " +
					"PRIMARY KEY(MessageID))";

		queryConnection(sql, createUserMessageTable);
	}


	function createUserMessageTable(){
		var sql = 	"CREATE TABLE UserMessage(" +
					"MessageID INT, ReceivingMember VARCHAR(255), ReadStatus VARCHAR(255), " +
					"FOREIGN KEY(MessageID) REFERENCES Messages(MessageID), " +
					"FOREIGN KEY(ReceivingMember) REFERENCES Users(Username), " + 
					"PRIMARY KEY(MessageID, ReceivingMember))";

		queryConnection(sql, exit);
	}

	toExecute();
}