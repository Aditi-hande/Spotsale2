package com.example.ecommerce.spotsale2;

public final class Constants {
    /*    Address Fetching Intent Service    */
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String DOCUMENT_REFERENCE = PACKAGE_NAME + ".REFERENCE";

    /*    Location Monitoring Service    */
    public static final long LOCATION_INTERVAL = 5000;
    public static final long LOCATION_FASTEST_INTERVAL = 5000;

}
