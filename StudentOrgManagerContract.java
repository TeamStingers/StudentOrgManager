
public final class StudentOrgManagerContract {
	public StudentOrgManagerContract(){}
	
	public static abstract class StudentOrgManager implements BaseColumns {
		public static final String TABLE_NAME = "users";
		public static final String COLUMN_NAME_USERNAME = "username";
		public static final String COLUMN_NAME_PASSWORD = "password";
		public static final String COLUMN_NAME_EMAIL = "email";
		public static final String COLUMN_NAME_PHONENUMBER = "phonenumber";
		public static final String COLUMN_NAME_FIRSTNAME = "firstname";
		public static final String COLUMN_NAME_LASTNAME = "lastname";
		public static final String COLUMN_NAME_MAJOR = "major";
		public static final String COLUMN_NAME_GRADUATIONYEAR = "graduationyear";
		public static final String COLUMN_NAME_BIO = "bio";
		public static final String COLUMN_NAME_PICTUREREF = "pictureref";
	}
}
