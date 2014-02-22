import StudentOrgManagerContract.*;
import StudentOrgManagerContract.Users;


public class StudentOrgDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";    
    private static final String VARCHAR_TYPE = " VARCHAR(255)";
    private static final String TIMESTAMP_TYPE = " TIMESTAMP()";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    
    private static final String SQL_CREATE_USERS = 
    		"CREATE TABLE " + Users.TABLE_NAME + " (" +
    		Users.COLUMN_NAME_USERNAME + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_PASSWORD + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_EMAIL + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_PHONENUMBER + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_FIRSTNAME + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_LASTNAME + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_MAJOR + VARCHAR_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_GRADUATIONYEAR + INT_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_BIO + TEXT_TYPE + COMMA_SEP +
    		Users.COLUMN_NAME_PICTUREREF + TEXT_TYPE + COMMA_SEP +
    		"PRIMARY KEY (" + Users.COLUMN_NAME_USERNAME + ")"
    		+ " )";
 
    private static final String SQL_CREATE_USERORGS= 
    		"CREATE TABLE " + UserOrgs.TABLE_NAME + " (" +
    		UserOrgs.COLUMN_NAME_USERNAME + VARCHAR_TYPE + COMMA_SEP +
    		UserOrgs.COLUMN_NAME_ORGANIZATION + VARCHAR_TYPE + COMMA_SEP +
    		"FOREIGN KEY (" + UserOrgs.COLUMN_NAME_USERNAME +") REFERENCES " + Users.TABLE_NAME 
    			+ "(" + Users.COLUMN_NAME_USERNAME + ")" + COMMA_SEP +
    		"FOREIGN KEY (" + UserOrgs.COLUMN_NAME_ORGANIZATION + ") REFERENCES " 
    			+ Organizations.TABLE_NAME + "(" + Organizations.COLUMN_NAME_ORGNAME + ")" + COMMA_SEP +
    		"PRIMARY KEY (Username, Organization)"
    		+ " )";
    
    public static final String SQL_CREATE_ORGANIZATIONS=
    	    "CREATE TABLE " + Organizations.TABLE_NAME + " (" +
    	    Organizations.COLUMN_NAME_ORGNAME + VARCHAR_TYPE + COMMA_SEP +
    	    Organizations.COLUMN_NAME_TYPE + VARCHAR_TYPE + COMMA_SEP +
    	    Organizations.COLUMN_NAME_SIZE + INT_TYPE + COMMA_SEP + 
    	    "PRIMARY KEY (" + Organizations.COLUMN_NAME_ORGNAME + ")"
    	    + " )"; 
    
    private static final String SQL_CREATE_NEWSITEMS= 
    		"CREATE TABLE " + NewsItems.TABLE_NAME + " (" +
    		NewsItems.COLUMN_NAME_ORGANIZATION + VARCHAR_TYPE + COMMA_SEP +
    		NewsItems.COLUMN_NAME_NEWSTIMESTAMP + TIMESTAMP_TYPE + COMMA_SEP +
    		NewsItems.COLUMN_NAME_ANNOUNCEMENT + TEXT_TYPE + COMMA_SEP + 
    		NewsItems.COLUMN_NAME_POSTER + VARCHAR_TYPE + COMMA_SEP + 
    		"FOREIGN KEY (" + NewsItems.COLUMN_NAME_ORGANIZATION +") REFERENCES " + Organizations.TABLE_NAME 
    			+ "(" + Organizations.COLUMN_NAME_ORGNAME + ")" + COMMA_SEP +
    		"FOREIGN KEY (" + NewsItems.COLUMN_NAME_POSTER + ") REFERENCES " 
    			+ Users.TABLE_NAME + "(" + Users.COLUMN_NAME_USERNAME + ")" + COMMA_SEP +
    		"PRIMARY KEY (Organization, NewsTimeStamp)"
    		+ " )";
    		
    private static final String SQL_CREATE_EVENTS= 
    		"CREATE TABLE " + Events.TABLE_NAME + " (" +
    		Events.COLUMN_NAME_EVENTID + INT_TYPE + " NOT NULL AUTO_INCREMENT" + COMMA_SEP +
    		Events.COLUMN_NAME_ORGANIZATION + VARCHAR_TYPE + COMMA_SEP +
    		Events.COLUMN_NAME_EVENTDATETIME + DATETIME_TYPE + COMMA_SEP + 
    		Events.COLUMN_NAME_LOCATION + VARCHAR_TYPE + COMMA_SEP +
    		Events.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
    		Events.COLUMN_NAME_TYPE + VARCHAR_TYPE + COMMA_SEP + 
    		"FOREIGN KEY (" + Events.COLUMN_NAME_ORGANIZATION +") REFERENCES " + Organizations.TABLE_NAME 
    			+ "(" + Organizations.COLUMN_NAME_ORGNAME + ")" + COMMA_SEP +
    	    "PRIMARY KEY (" + Events.COLUMN_NAME_EVENTID + ")"
    	    + " )"; 

    private static final String SQL_CREATE_ABSENCES= 
    		"CREATE TABLE " + Absences.TABLE_NAME + " (" +
    		Absences.COLUMN_NAME_USERNAME + VARCHAR_TYPE + COMMA_SEP +
    		Absences.COLUMN_NAME_ORGANIZATION + VARCHAR_TYPE + COMMA_SEP +
    		Absences.COLUMN_NAME_EVENTID + INT_TYPE + " NOT NULL AUTO_INCREMENT" + COMMA_SEP +
    		"FOREIGN KEY (" + Absences.COLUMN_NAME_USERNAME +") REFERENCES " + Users.TABLE_NAME 
    			+ "(" + Users.COLUMN_NAME_USERNAME + ")" + COMMA_SEP +
    		"FOREIGN KEY (" + Absences.COLUMN_NAME_ORGANIZATION +") REFERENCES " + Organizations.TABLE_NAME 
    			+ "(" + Organizations.COLUMN_NAME_ORGNAME + ")" + COMMA_SEP +
    		"FOREIGN KEY (" + Absences.COLUMN_NAME_EVENTID + ") REFERENCES " 
    			+ Events.TABLE_NAME + "(" + Events.COLUMN_NAME_EVENTID + ")" + COMMA_SEP +
    		"PRIMARY KEY (Username, Organization, EventID)"
    		+ " )";
    
    public StudentOrgDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }   
}
