package com.bordingvista.test.bordingvistatest.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by imran.zahid on 12/23/2015.
 */
public class ServiceHandler {
    private String response = "";

    public ServiceHandler() {
    }

    public String getAPIData(String url) {
        return this.makeServiceCall(url);
    }

    private String makeServiceCall(String url) {


        try {
            URL urlApi = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlApi.openConnection();

            if (connection.getResponseCode() == 200) {
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                response = stringBuilder.toString();
                connection.disconnect();
            } else {
               response =  Constants.SERVER_RESPONSE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Constants.SERVER_RESPONSE;
        }

        return response;
    }

}
