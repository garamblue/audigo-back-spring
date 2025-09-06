package com.audigo.audigo_back.common;

public interface ResponseCode {
    // http status code
    // HTTP Status 200
    String SUCCESS = "SU";

    // HTTP Status 400
    String VALIDATION_FAILED = "VF";
    String DUPLICATE_ID = "DUID";
    String DUPLICATE_EMAIL = "DE";
    String DUPLICATE_NICKNAME = "DN";
    String DUPLICATE_TEL_NUMBER = "DT";
    String DUPLICATE_MOBILE_NUMBER = "DMN";
    String NOT_EXISTED_USER = "NU";
    String NOT_EXISTED_ADMIN = "NEA";
    String NOT_EXISTING_CONTENTS = "NEC";

    // HTTP Status 401
    String SIGN_IN_FAIL = "SF";
    String AUTHORIZATION_FAIL = "AF";

    // HTTP Status 403
    String NO_PERMISSION = "NP";

    // HTTP Status 500
    String DATABASE_ERROR = "DBE";
    String DATABASE_INSERT_ERROR = "DBIE";

}