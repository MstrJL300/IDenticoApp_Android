package co.com.identico.appcarnet3.Services;
import co.com.identico.appcarnet3.helpers.TodoHelper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Miguel Forero on 27/01/2018.
 */

public class IdenticoRestClient {
    public static final String BASE_URL = TodoHelper.BASE_URL;
    private static final String BASE_API = "api/";
    //private static final String BASE_URL = "http://181.48.173.68/api/";
    private static final String CODE_AUTHORIZATION = "Basic bWlndWVsLmZvcmVyb0BzeXN0ZW1ha2Vycy5jb206bWlndWVsLmZvcmVyb0BzeXN0ZXNzbXdhYQ==";
    private static AsyncHttpClient client = new AsyncHttpClient();

    //Use to get the sql information.
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", CODE_AUTHORIZATION);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", CODE_AUTHORIZATION);
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", CODE_AUTHORIZATION);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + BASE_API + relativeUrl;
    }
}