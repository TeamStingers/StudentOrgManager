var express = require('express');
var app = express();
var server = require('http').createServer(app);
var mysql = require('mysql');

//EXPRESS CONFIG
app.use(express.bodyParser());

var portNum = 80;

var connection = mysql.createConnection({
	host     : 'localhost',
	user     : 'root',
	password : 'root',
	database : 'StudentOrgManager',
	port 	 : '3306'
});

connection.connect();

server.listen(process.env.PORT || portNum);

/*
The results are returned from a query have one element in the results array for 
each statement in the query. So it is multi dimensional.

Ex.

connection.query('SELECT 1; SELECT 2', function(err, results) {
  if (err) throw err;

  // `results` is an array with one element for every statement in the query:
  console.log(results[0]); // [{1: 1}]
  console.log(results[1]); // [{2: 2}]
});

If the query only contains one statement, reuslts is just an array of json objects.
In this case, the array is just one dimension.
*/

//Demo web service methods

app.get('/blah', function(req, res){
	res.json([{success:"blah"}]);
});

app.get('/login/:Username/:Password', function(req, res){
	var post = {Username: req.param("Username"), Password: req.param("Password") };

	var sql = "SELECT * FROM ?? WHERE ?? = ? AND ?? = ?";
	var inserts = ['Users', 'Username', post.Username, 'Password', post.Password];
	sql = mysql.format(sql, inserts);

	queryConnection(sql, function(result){
		console.log(result);

		if(result.length > 0) result = [{authenticated:true}];
		else result = [{authenticated:false}];
		res.json(result);
	});
});

app.get('/get_user_orgs/:Username', function(req, res){
	connection.query("SELECT * FROM UserOrgs WHERE Username=?", 
		[req.param("Username")], function(err, result){
		
		res.json(result);
	});
});

app.get('/get_all_orgs', function(req, res){
	connection.query("SELECT * FROM Organizations", function(err, result){
		res.json(result);
	});
});

app.get('/get_user_info/:Username', function(req, res){
	connection.query('SELECT * FROM Users WHERE Username=?', [req.params.Username], function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.get('/get_org_info/:Organization', function(req, res){
	connection.query('SELECT * FROM Organizations WHERE OrgName=?', [req.params.Organization], function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.get('/delete_user/:username', function(req, res){
	connection.query('DELETE FROM Users WHERE Username=?', [req.params.username], function(err, result){
		if(err) console.log(err);
		res.send("User: " + req.params.username + " successfully deleted.");
	})
});


////////// Actual WebService


app.post('/login', function(req, res){
	var post = req.body;

	var sql = "SELECT * FROM ?? WHERE ?? = ? AND ?? = ?";
	var inserts = ['Users', 'Username', post.Username, 'Password', post.Password];
	sql = mysql.format(sql, inserts);

	// post {Username: $Username, Password: $Password}
	queryConnection(sql, function(result){
		res.json(result);
	});
});

app.post('/get_all_orgs', function(req, res){
	var post = req.body;

	connection.query("SELECT * FROM Organizations", function(err, result){
		res.json(result);
	});
});

app.post('/get_user_info', function(req, res){
	var post = req.body;

	connection.query('SELECT * FROM Users WHERE Username= ?', [post.Username], function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.post('/get_org_info', function(req, res){
	var post = req.body;

	connection.query('SELECT * FROM Organizations WHERE OrgName=?', [post.Organization], function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.post('/create_user', function(req, res){
	var post = req.body;
	console.log('req rec');
	//post all user fields like they are in relational diagram
	connection.query('INSERT INTO Users SET ?', post, function(err, result) {
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/delete_user', function(req, res){
	var post = req.body;
	//post {Username:$Username}
	connection.query('DELETE FROM Users WHERE ?', post, function(err, result){
		if(err) console.log(err);
		res.send([{success:true}]);
	})
});

app.post('/update_user', function(req, res){
	var post = req.body;

	//post all user fields like they are in relational diagram
	connection.query('UPDATE Users SET ? WHERE Username=' + 
		connection.escape(post.Username), post, function(err, result){
			if(err) console.log(err);
			res.send([{success:true}]);
	});
});

app.post('/add_user_to_org', function(req, res){
	var post = req.body;
	
	console.log(post);

	//need to post Position = Member and MemberType=RegularMember and DuesPaid=Unpaid by default
	var insertQuery = connection.query('INSERT INTO UserOrgs SET ?', post, function(err, result) {
		if(err){
			console.log(err);
			res.send([]);
		}
		else{
			var sizeQuery = connection.query('UPDATE Organizations SET Size = Size + 1 WHERE OrgName= ?', 
			[post.Organization], function(err, result) {
				if(err){
					console.log(err);
					res.send([]);
				}else{
					res.send([{success:true}]);					
				}
			})
		}
	});
});

app.post('/remove_user_from_org', function(req, res){
	var post = req.body;

	var removeSql = 'DELETE FROM UserOrgs WHERE Username=' + connection.escape(post.Username)+
					' AND Organization=' + connection.escape(post.Organization);

	connection.query(removeSql, function(err, results) {
		if(err){
			console.log(err);
			res.send([]);		
		}
		else{
			var sizeQuery = connection.query('UPDATE Organizations SET Size = Size - 1 WHERE OrgName= ?', 
			[post.Organization], function(err, result) {
				if(err){
					console.log(err);
					res.send([]);					
				}
				res.send([{success:true}]);
			});
		} 
	});
});

app.post('/add_absence', function(req, res){
	var post = req.body;

	var addAbsQuery = connection.query('INSERT INTO Absences SET ?', post, function(err, result) {
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/remove_absence', function(req, res){
	var post = req.body;

	var removeAbsSql = 'DELETE FROM Absences WHERE Username=' + connection.escape(post.Username) +
						' AND Organization=' + connection.escape(post.Organization) +
						' AND EventID=' + connection.escape(post.EventID);

	connection.query(removeAbsSql, function(err, results){
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/get_user_position', function(req, res){
	var post = req.body;

	var sql = 	"SELECT Position, MemberType FROM UserOrgs WHERE Username=" + connection.escape(post.Username) +
				" AND Organization=" + connection.escape(post.Organization);

	queryConnection(sql, function(result){
		res.json(result);
	});
});

app.post('/change_user_position', function(req, res){
	var post = req.body;

	var changePosSql = 'UPDATE UserOrgs SET Position=' + connection.escape(post.Position) +
						', MemberType=' + connection.escape(post.MemberType) +
						' WHERE Organization=' +connection.escape(post.Organization) + 
						' AND Username=' + connection.escape(post.Username);

	connection.query(changePosSql, function(err, results){
		if(err) console.log(err);
		res.send([{success:true}]);		
	});
});

app.post('/create_org', function(req, res){
	var post = req.body;

	//Post: {OrgName: $orgname, Type:$type, CreatorUser:$creator, AnnualDues:$dues}
	var createOrgSql = 	'INSERT INTO Organizations SET OrgName=' + connection.escape(post.OrgName) +
						", Type=" + connection.escape(post.Type) +
						", AnnualDues=" + parseFloat(post.AnnualDues);

	connection.query(createOrgSql, post, function(err, result){
		if(err) console.log(err);
			//need to post Username, Organization, Position = Member and MemberType=RegularMember and DuesPaid=Unpaid by default
		
		var insertSql = 'INSERT INTO UserOrgs SET Username=' + connection.escape(post.CreatorUser) +
					", Organization=" + connection.escape(post.OrgName) +
					", Position=" + "'Admin'" +
					", MemberType=" + "'Admin'" +
					", DuesPaid=" + "'Unpaid'";

		connection.query(insertSql, function(err, result){
			console.log(insertSql);
			console.log('in the thing');
			res.send([{success:true}]);
		});
	});
});

app.post('/delete_org', function(req, res){
	var post = req.body;

	//post {OrgName : 'OrgName'}
	connection.query('DELETE FROM UserOrgs WHERE Organization=' +connection.escape(post.OrgName), 
		post, function(err ,result){
		
		if(err){
			console.log(err);
			res.send([]);
		}

		connection.query('DELETE FROM Organizations WHERE ?', post, function(err ,result){
			if(err){
				console.log(err);
				res.send([]);
			}
			res.send([{success:true}]);
		});
	});
});

app.post('/create_news_item', function(req, res){
	var post = req.body;

	var createNewsSql = 'INSERT INTO NewsItems SET Organization=' + connection.escape(post.Organization) +
						', NewsTimeStamp=NOW(), Announcement=' + connection.escape(post.Announcement) +
						', Poster=' + connection.escape(post.Poster);

	connection.query(createNewsSql, function(err, results){
		if(err) console.log(err);
		res.send([{success:true}]);		
	});
});

app.post('/delete_news_item', function(req, res){
	var post = req.body;

	var deleteNewsSql = "DELETE FROM NewsItems WHERE Organization=" + connection.escape(post.Organization) +
						" AND NewsTimeStamp=" + connection.escape(post.NewsTimeStamp);

	//POST {Organization:'OrgName' AND Announcement='AnouncementText'}
	connection.query(deleteNewsSql, function(err, result){
		// console.log(result);
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/create_event', function(req, res){
	var post = req.body;
/*
	var d1 = new Date(post.EventDateTime);	
	var d2 = new Date(post.EventDateTime);
	d1.setHours(d2.getHours()-4);

	console.log(d1);
*/
	var createEventSql = "INSERT INTO Events SET Organization=" + connection.escape(post.Organization) +
						", Location=" + connection.escape(post.Location) +
						", Description=" + connection.escape(post.Description) +
						", Type=" + connection.escape(post.Type) +
						", EventDateTime=" + connection.escape(post.EventDateTime) + 
						", EventName=" + connection.escape(post.EventName);

	connection.query(createEventSql, function(err, results){
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/delete_event', function(req, res){
	var post = req.body;
	//post {EventID : '$EventID'}
	connection.query('DELETE FROM Events WHERE ?', post, function(err, result){
		if(err) console.log(err);
		res.send([{success:true}]);
	});
});

app.post('/get_user_orgs', function(req, res){
	var post = req.body;

	//post {Username: $Username}
	connection.query("SELECT * FROM UserOrgs WHERE ?", post, function(err, result){
		res.json(result);
	});
});

app.post('/get_org_news', function(req, res){
	var post = req.body;

	//post {Organization:$OrgName}
	connection.query("SELECT * FROM NewsItems WHERE ?", post, function(err, result){

		res.json(result);
	});
});

app.post('/get_org_events', function(req, res){
	var post = req.body;

	//post {Organization:$OrgName}
	connection.query("SELECT * FROM Events WHERE ?", post, function(err, result){
		res.send(result);
	});
});

app.post('/get_event_absences', function(req, res){
	var post = req.body;

	var getAbsSql = "SELECT Username, Organization" +
					" FROM Events INNER JOIN Absences ON Absences.EventID=Events.EventID" +
					" WHERE EventID="+ connection.escape(post.EventID);

	connection.query(getAbsSql, function(err, result){
		res.json(result);
	});
});

app.post('/get_user_absences_for_org', function(req, res){
	var post = req.body;

	//post {Username:$username, Organization:$orgname}
	var getUserAbsSql = "SELECT Absences.EventID, Username, Absences.Organization, EventName, EventDateTime" +
				" FROM Events INNER JOIN Absences ON Absences.EventID=Events.EventID" +
				" WHERE Absences.Organization="+ connection.escape(post.Organization) + " AND" +
				" Username=" + connection.escape(post.Username);

	connection.query(getUserAbsSql, function(err, result){
		if(err) console.log(err);
		console.log(result);
		res.json(result);
	});
});

app.post('/get_user_msgs', function(req, res){
	var post = req.body;

	var sql = "SELECT Messages.MessageID, ReadStatus, MsgContent, SendingUser, MsgTimeStamp, MessageType FROM " +
				"Messages INNER JOIN UserMessage ON Messages.MessageID=UserMessage.MessageID " +
				"WHERE ReceivingMember=" + connection.escape(post.Username);

	connection.query(sql, function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.post('/delete_message', function(req, res){
	var post = req.body;

	var sql = "DELETE FROM UserMessage WHERE MessageID=" + connection.escape(post.MessageID);

	connection.query(sql, function(err, result){
		if(err){
			res.send([]);
			console.log(err);
		}
			res.send([{success:true}]);	
	});
});

app.post('/send_message', function(req, res){
	var post = req.body;

	var sendMsgSql = "INSERT INTO Messages SET SendingUser="+connection.escape(post.SendingUser)+
						", MsgTimeStamp=NOW(), MessageType='StandardMessage'" +
						", MsgContent=" + connection.escape(post.MsgContent);

	//post {SendingUser:$senderusername, ReceivingMember:$recverUsername}
	connection.query(sendMsgSql, function(err, result){
		if(err){
			console.log(err);
			res.send([]);
		}

		var userMsgSql = "INSERT INTO UserMessage SET MessageID=" + connection.escape(result.insertId) +
							", ReceivingMember=" + connection.escape(post.ReceivingMember) +
							", ReadStatus='Unread'";

		connection.query(userMsgSql, function(err, result){
			if(err){
				console.log(err);
				res.send([]);
			}

			res.send([{success:true}]);
		});
	});
});

app.post('/read_message', function(req, res){
	var post = req.body;

	//post {MessageID:$MsgId}
	var readMsgSql = "UPDATE UserMessage SET ReadStatus='Read'" + 
						" WHERE MessageID=" + connection.escape(post.MessageID);

	connection.query(readMsgSql, function(err, results){
		if(err) console.log(err);
		res.send([{success:true}]);	
	});
});

app.post('/get_users_for_org', function(req, res){
	var post = req.body;

	var sql = "SELECT Username FROM UserOrgs WHERE Organization=" + connection.escape(post.Organization);

	connection.query(sql, function(err, results){
		if(err) console.log(err);
		res.send(results);	
	});
});

//implement if there is time left, need to insert into UserMessage for each receiving member

// app.post('/send_notification', function(req, res){
// 	var post = req.body;

// 	var sendMsgSql = "INSERT INTO Messages SET SendingUser="+connection.escape(post.SendingUser)+
// 						", MsgTimeStamp=NOW(), MessageType='Notification'";

// 	//post {SendingUser:$senderusername, ReceivingMember:$recverUsername}
// 	connection.query(sendMsgSql, function(err, result){
// 		var userMsgSql = "INSERT INTO UserMessage MessageID=" + result[0].MessageID +
// 							", ReceivingMember=" + connection.escape(post.ReceivingMember) +
// 							", ReadStatus='Unread'";

// 		connection.query(userMsgSql, function(err, result){
// 			if(err) console.log(err);
// 		});
// 	});
// });

app.post('/get_dues_status', function(req, res){
	var post = req.body;

	//post {Organization:$orgname, Username:$un}
	var getDuesStatusSql = "SELECT DuesPaid FROM UserOrgs WHERE Username=" + 
		connection.escape(post.Username) + " AND Organization="+connection.escape(post.Organization);

	connection.query(getDuesStatusSql, function(err, result){
		if(err) console.log(err);
		res.json(result);
	});
});

app.post('/update_org_dues', function(req, res){
	var post = req.body;

	//post {Organization:$orgname, AnnualDues:$anualdue}
	var updateDuesSql = "UPDATE Organizations SET AnnualDues=" + connection.escape(post.AnnualDues) +
		" WHERE OrgName=" + connection.escape(post.Organization);

	connection.query(updateDuesSql, function(err, result){
		if(err) console.log(err);
		res.send([{success:true}]);	
	});
});

app.post('/update_user_dues', function(req, res){
	var post = req.body;

	//post {OrgName:$orgname, Username:$un, Status:} .. where status is paid or unpaid
	var updateDuesSql = "UPDATE UserOrgs SET DuesPaid=" + connection.escape(post.Status) +
		" WHERE Username=" + connection.escape(post.Username) + " AND Organization=" +
		connection.escape(post.Organization);

	connection.query(updateDuesSql, function(err, result){
		if(err) console.log(err);
		res.send([{success:true}]);	
	});
});

//**********************

function queryConnection(sql, cb){
	connection.query(sql, function(err, result){
		if(err) console.log(err);
		cb(result);
	});
}

function toSqlDateTime(dateString){
	return new Date(dateString).toISOString().slice(0, 19).replace('T', ' ');
}


// setInterval(function(){ 

// 	//mailer job

// }, 30*secondsInMinute*millisInSecond);
