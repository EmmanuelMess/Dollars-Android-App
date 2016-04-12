package com.kosalgeek.genasync12;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * (The MIT License)
 * Copyright (c) 2015 KosalGeek. (kosalgeek at gmail dot com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the 'Software'), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Modified by EmmanuelMess
 **/
public class PostResponseAsyncTask extends AsyncTask<String, Void, String> {

    private String LOG = "PostResponseAsyncTask";

    private ProgressDialog progressDialog;

    private AsyncResponse asyncResponse;
    private Context context;
    private HashMap<String, String> postData = new HashMap<String, String>();
    private String loadingMessage = "Loading...";
    private boolean showLoadingMessage = false;


    private ExceptionHandler exceptionHandler;
    private EachExceptionsHandler eachExceptionsHandler;

    private Exception exception = new Exception();

    public PostResponseAsyncTask(Context context,
                                 AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
        this.context = context;
    }

    public PostResponseAsyncTask(Context context,
                                 boolean showLoadingMessage,
                                 AsyncResponse asyncResponse
    ) {
        this.asyncResponse = asyncResponse;
        this.context = context;
        this.showLoadingMessage = showLoadingMessage;
    }

    public PostResponseAsyncTask(Context context,
                                 HashMap<String, String> postData,
                                 AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.asyncResponse = asyncResponse;
    }

    public PostResponseAsyncTask(Context context,
                                 HashMap<String, String> postData,
                                 boolean showLoadingMessage,
                                 AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.asyncResponse = asyncResponse;
        this.showLoadingMessage = showLoadingMessage;
    }

    public PostResponseAsyncTask(Context context,
                                 String loadingMessage,
                                 AsyncResponse asyncResponse) {
        this.context = context;
        this.loadingMessage = loadingMessage;
        this.asyncResponse = asyncResponse;
    }

    public PostResponseAsyncTask(Context context,
                                 HashMap<String, String> postData,
                                 String loadingMessage,
                                 AsyncResponse asyncResponse) {
        this.context = context;
        this.postData = postData;
        this.loadingMessage = loadingMessage;
        this.asyncResponse = asyncResponse;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public HashMap<String, String> getPostData() {
        return postData;
    }

    public void setPostData(HashMap<String, String> postData) {
        this.postData = postData;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setEachExceptionsHandler(EachExceptionsHandler eachExceptionsHandler) {
        this.eachExceptionsHandler = eachExceptionsHandler;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public Context getContext() {
        return context;
    }

    public AsyncResponse getAsyncResponse() {
        return asyncResponse;
    }

    @Override
    protected void onPreExecute() {
        if (showLoadingMessage) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(loadingMessage);
            progressDialog.show();
        }

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        for (int i = 0; i <= 0; i++) {
            result = invokePost(urls[i], postData);
        }
        return result;
    }

    private String invokePost(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

                Log.d("PostResponseAsyncTask", responseCode + "");
            }
        } catch (MalformedURLException e) {
            Log.d("PostResponseAsyncTask", "MalformedURLException Error: " + e.toString());
            exception = e;
        } catch (ProtocolException e) {
            Log.d("PostResponseAsyncTask", "ProtocolException Error: " + e.toString());
            exception = e;
        } catch (UnsupportedEncodingException e) {
            Log.d("PostResponseAsyncTask", "UnsupportedEncodingException Error: " + e.toString());
            exception = e;
        } catch (IOException e) {
            Log.d("PostResponseAsyncTask", "IOException Error: " + e.toString());
            exception = e;
        }

        return response;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (showLoadingMessage) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        result = result.trim();

        if (asyncResponse != null) {
            asyncResponse.processFinish(result);
        }

        if (exception != null) {
            if (exceptionHandler != null) {
                exceptionHandler.handleException(exception);
            }
            if (eachExceptionsHandler != null) {
                Log.d(LOG, "" + exception.getClass().getSimpleName());
                if (exception instanceof MalformedURLException) {
                    eachExceptionsHandler.handleMalformedURLException((MalformedURLException) exception);
                } else if (exception instanceof ProtocolException) {
                    eachExceptionsHandler.handleProtocolException((ProtocolException) exception);
                } else if (exception instanceof UnsupportedEncodingException) {
                    eachExceptionsHandler.handleUnsupportedEncodingException((UnsupportedEncodingException) exception);
                } else if (exception instanceof IOException) {
                    eachExceptionsHandler.handleIOException((IOException) exception);
                }
            }
        }

    }
}
