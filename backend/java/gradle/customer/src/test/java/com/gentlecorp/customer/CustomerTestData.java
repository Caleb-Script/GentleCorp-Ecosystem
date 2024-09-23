package com.gentlecorp.customer;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;

class CustomerTestData {


  static final String SCHEMA_HOST = "http://localhost:";
  static final String LOGIN_PATH = "/auth/login";
  static final String ALL_PATH = "/all/";
  static final String CONTACT_PATH = "/contact";

  static final String CONTACT_URL_TEMPLATE = SCHEMA_HOST + "%d" + CUSTOMER_PATH + CONTACT_PATH + "%s";

  final static String ADMIN_ID = "/00000000-0000-0000-0000-000000000000";

  static final String ID_CALEB = "/00000000-0000-0000-0000-000000000025";
  static final String ID_ANNA = "/00000000-0000-0000-0000-000000000024";
  static final String ID_HIROSHI = "/00000000-0000-0000-0000-000000000018";
  static final String ID_ERIK = "/00000000-0000-0000-0000-000000000005";
  static final String ID_LEROY = "/00000000-0000-0000-0000-000000000026";

  final static String CONTTACT_R_ID = "/00000000-0000-0000-0000-000000000057";
  static final String NOT_EXISTING_ID = "/20000000-0000-0000-0000-000000000000";
  static final String CALEB_CONTACT_ID_1 = "/20000000-0000-0000-0000-000000000000";

  static final String SUPREME = "SUPREME";
  static final String BASIC = "BASIC";
  static final String ELITE = "ELITE";
  static final String ADMIN = "ADMIN";
  static final String USER = "USER";

  // Testrollen und -zugangsdaten
  static final String ROLE_ADMIN = "admin";
  static final String ROLE_USER = "user";
  static final String ROLE_SUPREME = "gentlecg99";
  static final String ROLE_ELITE = "leroy135";
  static final String ROLE_BASIC = "erik";
  static final String ROLE_PASSWORD = "p";

  // HTTP-Header
  static final String HEADER_IF_NONE_MATCH = "If-None-Match";
  static final String HEADER_IF_MATCH = "If-Match";

  // Authentifizierungsinformationen
  static final String USERNAME = "username";
  static final String PASSWORD = "password";
  static final String ACCESS_TOKEN = "access_token";
  static final String AUTHORIZATION = "Authorization";
  static final String BEARER = "Bearer ";

  // Kundenattribute
  static final String CUSTOMER = "customer";
  static final String LAST_NAME = "lastName";
  static final String FIRST_NAME = "firstName";
  static final String EMAIL = "email";
  static final String PHONE_NUMBER = "phoneNumber";
  static final String TIER_LEVEL = "tierLevel";
  static final String IS_SUBSCRIBED = "isSubscribed";
  static final String BIRTHDATE = "birthdate";
  static final String GENDER = "gender";
  static final String MARITAL_STATUS = "maritalStatus";
  static final String INTERESTS = "interests";
  static final String CONTACT_OPTIONS = "contactOptions";

  //Query Parameter
  static final String CUSTOMER_STATUS = "customerStatus";
  static final String PREFIX = "prefix";

  // Adressattribute
  static final String ADDRESS = "address";
  static final String STREET = "street";
  static final String HOUSE_NUMBER = "houseNumber";
  static final String ZIP_CODE = "zipCode";
  static final String CITY = "city";
  static final String STATE = "state";
  static final String COUNTRY = "country";


  //Kontaktattribute
  static final String RELATIONSHIP = "relationship";
  static final String WITHDRAWAL_LIMIT = "withdrawalLimit";
  static final String IS_EMERGENCY_CONTACT = "isEmergencyContact";


  // Hiroshi's Daten
  static final String USERNAME_HIROSHI = "hiroshi.tanaka";
  static final String LAST_NAME_HIROSHI = "Tanaka";
  static final String FIRST_NAME_HIROSHI = "Hiroshi";
  static final String EMAIL_HIROSHI = "hiroshi.tanaka@example.com";
  static final String PHONE_NUMBER_HIROSHI = "+81-3-1234-5678";
  static final String BIRTH_DATE_HIROSHI = "1988-06-20";
  static final String STREET_HIROSHI = "Shibuya Crossing";
  static final String HOUSE_NUMBER_HIROSHI = "1-2-3";
  static final String ZIP_CODE_HIROSHI = "150-0001";
  static final String CITY_HIROSHI = "Tokyo";
  static final String STATE_HIROSHI = "Kanto";
  static final String COUNTRY_HIROSHI = "Japan";

  // Erik's Daten
  static final String USERNAME_ERIK = "erik";
  static final String LAST_NAME_ERIK = "Schmidt";
  static final String FIRST_NAME_ERIK = "Erik";
  static final String EMAIL_ERIK = "erik.schmidt@example.com";
  static final String PHONE_NUMBER_ERIK = "030-2345678";
  static final String BIRTH_DATE_ERIK = "1982-03-25";
  static final String STREET_ERIK = "Eichenstraße";
  static final String HOUSE_NUMBER_ERIK = "8";
  static final String ZIP_CODE_ERIK = "20255";
  static final String CITY_ERIK = "Hamburg";
  static final String STATE_ERIK = "Hamburg";
  static final String COUNTRY_ERIK = "Deutschland";

  // Leroy's Daten
  static final String USERNAME_LEROY = "leroy135";
  static final String LAST_NAME_LEROY = "Jefferson";
  static final String FIRST_NAME_LEROY = "Leroy";
  static final String EMAIL_LEROY = "leroy135@icloud.com";
  static final String PHONE_NUMBER_LEROY = "015111951223";
  static final String BIRTH_DATE_LEROY = "1999-05-03";
  static final String STREET_LEROY = "Connell Street";
  static final String HOUSE_NUMBER_LEROY = "42";
  static final String ZIP_CODE_LEROY = "D01 C3N0";
  static final String CITY_LEROY = "Dublin";
  static final String STATE_LEROY = "Leinster";
  static final String COUNTRY_LEROY = "Ireland";

  // Caleb's Daten
  static final String USERNAME_CALEB = "gentlecg99";
  static final String LAST_NAME_CALEB = "Gyamfi";
  static final String FIRST_NAME_CALEB = "Caleb";
  static final String EMAIL_CALEB = "caleb_g@outlook.de";
  static final String PHONE_NUMBER_CALEB = "015111951223";
  static final String BIRTH_DATE_CALEB = "1999-05-03";
  static final String STREET_CALEB = "Namurstraße";
  static final String HOUSE_NUMBER_CALEB = "4";
  static final String ZIP_CODE_CALEB = "70374";
  static final String CITY_CALEB = "Stuttgart";
  static final String STATE_CALEB = "Baden Württemberg";
  static final String COUNTRY_CALEB = "Deutschland";

  // E-Tags
  static final String ETAG_VALUE_MINUS_1 = "\"-1\"";
  static final String ETAG_VALUE_0 = "\"0\"";
  static final String ETAG_VALUE_1 = "\"1\"";
  static final String ETAG_VALUE_2 = "\"2\"";
  static final String ETAG_VALUE_3 = "\"3\"";
  static final String INVALID_ETAG_VALUE = "\"3";

  // Problem Detail
  static final String INVALID_KEY = "Invalid key: ";
  static final int BAD_REQUEST_STATUS = 400;
  static final String BAD_REQUEST_TITLE = "Bad Request";
  static final String BAD_REQUEST_TYPE = "/problem/badRequest";

  static final int TOTAL_CUSTOMERS = 27;

  //Query Parameter
  static final String QUERY_SON = "son";
  static final String QUERY_IVA = "iva";
  static final String QUERY_G = "g";
  static final String QUERY_M = "m";
  static final String QUERY_XYZ = "xyz";
  static final String QUERY_IVANOV = "ivanov";
  static final String QUERY_ICLOUD_COM = "icloud.com";
  static final String QUERY_IS_SUBSCRIBED = "true";
  static final String QUERY_IS_NOT_SUBSCRIBED = "false";
  static final String QUERY_BIRTH_DATE_BEFORE = "before;1991-01-01";
  static final String QUERY_BIRTH_DATE_AFTER = "after;1999-01-01";
  static final String QUERY_BIRTH_DATE_BETWEEN = "between;1991-01-01;1998-12-31";
  static final String QUERY_ZIP_CODE_70374 = "70374";
  static final String QUERY_ZIP_CODE_Y1000 = "Y1000";
  static final String QUERY_ZIP_CODE_KA = "KA";
  static final String QUERY_CITY_STUTTGART = "Stuttgart";
  static final String QUERY_CITY_KUMASI = "kumasi";
  static final String QUERY_CITY_TOK = "tok";
  static final String QUERY_STATE_NEW_SOUTH_WALES = "New South Wales";
  static final String QUERY_STATE_BA = "Ba";
  static final String QUERY_COUNTRY_USA = "USA";
  static final String QUERY_COUNTRY_LAND = "land";

  // Invalid Query Parameter
  static final String INVALID = "invalid";
  static final int INVALID_TIER_LEVEL_4 = 4;
  static final String INVALID_BIRTHDATE_FORMAT = "invalid,2000-01-01";

  // Tier  Level
  static final int TIER_LEVEL_3 = 3;
  static final int TIER_LEVEL_2 = 2;
  static final int TIER_LEVEL_1 = 1;

  static final String GENDER_FEMALE = "F";
  static final String GENDER_MALE = "M";
  static final String GENDER_DIVERSE = "D";

  // MArital Status
  static final String MARITAL_STATUS_SINGLE = "S";
  static final String MARITAL_STATUS_MARRIED = "M";
  static final String MARITAL_STATUS_DIVORCED = "D";
  static final String MARITAL_STATUS_WIDOW = "W";

  // Customer Status
  static final String CUSTOMER_STATUS_ACTIVE = "A";
  static final String CUSTOMER_STATUS_INACTIVE = "I";
  static final String CUSTOMER_STATUS_CLOSED = "C";
  static final String CUSTOMER_STATUS_BLOCKED = "B";

  // Interessen
  static final String INTEREST_INVESTMENTS = "I";
  static final String INTEREST_SAVINGS_AND_FINANCES = "SF";
  static final String INTEREST_CREDIT_AND_DEBT = "CD";
  static final String INTEREST_BANK_PRODUCTS_AND_SERVICES = "BPS";
  static final String INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING = "FEC";
  static final String INTEREST_REAL_ESTATE = "RE";
  static final String INTEREST_INSURANCE = "IN";
  static final String INTEREST_SUSTAINABLE_FINANCE = "SUF";
  static final String INTEREST_TECHNOLOGY_AND_INNOVATION = "IT";
  static final String INTEREST_TRAVEL = "T";

  // Kontakt Optionen
  static final String CONTACT_OPTION_PHONE = "P";
  static final String CONTACT_OPTION_EMAIL = "E";
  static final String CONTACT_OPTION_LETTER = "L";
  static final String CONTACT_OPTION_SMS = "S";

  // Neue Konstanten für die spezifischen Werte
  static final String NEW_USER_LAST_NAME = "Gyamfi";
  static final String NEW_USER_FIRST_NAME = "Caleb";
  static final String NEW_USER_PHONE_NUMBER = "015111951223";
  static final String NEW_USER_BIRTH_DATE = "1999-05-03";
  static final String NEW_USER_STREET = "Namurstraße";
  static final String NEW_USER_HOUSE_NUMBER = "4";
  static final String NEW_USER_ZIP_CODE = "70374";
  static final String NEW_USER_CITY = "Stuttgart";
  static final String NEW_USER_STATE = "Baden-Württemberg";
  static final String NEW_USER_COUNTRY = "Germany";
  static final String NEW_USER_PASSWORD = "Caleb123.";
  static final String NEW_USER_SUBSCRIPTION = QUERY_IS_SUBSCRIBED;
  static final String NEW_USER_GENDER = GENDER_MALE;
  static final String NEW_USER_MARITAL_STATUS = MARITAL_STATUS_SINGLE;
  static final String NEW_USER_INTERESTS = INTEREST_INVESTMENTS;
  static final String NEW_USER_CONTACT_OPTIONS = CONTACT_OPTION_PHONE;
  static final String SUPREME_USERNAME = "gentlecg99_supreme";
  static final String ELITE_USERNAME = "gentlecg99_elite";
  static final String BASIC_USERNAME = "gentlecg99_basic";
  static final String SUPREME_EMAIL = "supreme@ok.de";
  static final String ELITE_EMAIL = "elite@ok.de";
  static final String BASIC_EMAIL = "basic@ok.de";

  static final String INVALID_EMAIL = "kwame.owusuexample.com";
  static final String EXISTING_EMAIL = "kwame.owusu@example.com";
  static final String DUPLICATE_USERNAME = "gentlecg99";
  static final String DUPLICATE_INTERESTS = NEW_USER_INTERESTS;
  static final String DUPLICATE_CONTACT_OPTIONS = NEW_USER_CONTACT_OPTIONS;
  static final String INVALID_LAST_NAME = "123Invalid";
  static final String INVALID_FIRST_NAME = "Invalid123";
  static final String INVALID_PHONE_NUMBER = "123";
  static final String INVALID_USERNAME = "a";
  static final int INVALID_TIER_LEVEL = 5;
  static final String INVALID_GENDER = "INVALID";
  static final String INVALID_MARITAL_STATUS = "INVALID";
  static final String FUTURE_BIRTHDATE =  "A Date in the Future";


  static final String NEW_CONTACT_LAST_NAME = "Rolly";
  static final String NEW_CONTACT_FIRST_NAME = "Hola";
  static final String NEW_CONTACT_RELATIONSHIP = "S";
  static final int NEW_CONTACT_WITHDRAWAL_LIMIT = 50;
  static final boolean NEW_CONTACT_IS_EMERGENCY = false;

  static final String EXISTING_CONTACT_LAST_NAME = "Andersson";
  static final String EXISTING_CONTACT_FIRST_NAME = "Eric";
  static final String EXISTING_CONTACT_RELATIONSHIP = "S";
  static final int EXISTING_CONTACT_WITHDRAWAL_LIMIT = 50;
  static final boolean EXISTING_CONTACT_IS_EMERGENCY = false;


  static final String INVALID_CONTACT_FIRST_NAME = "";
  static final String INVALID_CONTACT_LAST_NAME = "";
  static final String INVALID_CONTACT_RELATIONSHIP = "";
  static final int INVALID_CONTACT_WITHDRAWAL_LIMIT = -1;
  static final Boolean INVALID_CONTACT_IS_EMERGENCY = null;





















  static final String UPDATED_LAST_NAME = "Updatedastame";
  static final String UPDATED_FIRST_NAME = "Updatedirstame";
  static final String UPDATED_USERNAME = "Updatedirstadme";
  static final String UPDATED_EMAIL = "updated.email@example.com";
  static final String UPDATED_PHONE_NUMBER = "+49 987 654321";
  static final int UPDATED_TIER_LEVEL = 2;
  static final boolean UPDATED_IS_SUBSCRIBED = false;
  static final String UPDATED_BIRTH_DATE = "1990-01-01";
  static final String UPDATED_GENDER = "F";
  static final String UPDATED_MARITAL_STATUS = "M";
  static final String UPDATED_INTEREST = "IT";
  static final String UPDATED_CONTACT_OPTION = "S";
  static final String UPDATED_STREET = "Updated Street";
  static final String UPDATED_HOUSE_NUMBER = "10B";
  static final String UPDATED_ZIP_CODE = "54321";
  static final String UPDATED_CITY = "Updated City";
  static final String UPDATED_STATE = "Updated State";
  static final String UPDATED_COUNTRY = "Updated Country";



  static final String PASSWORD_PATH = "/password";
  static final String NEW_PASSWORD = "123.Caleb";
  static final String NEW_INVALID_PASSWORD = "p";


}
