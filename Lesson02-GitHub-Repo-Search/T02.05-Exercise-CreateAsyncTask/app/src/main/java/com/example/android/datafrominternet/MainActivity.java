/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.datafrominternet.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchBoxEditText;

    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;

    private String githubSearchResults = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the URL
     * (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {at link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());
       /* try {
            githubSearchResults = NetworkUtils.getResponseFromHttpUrl(githubSearchUrl);
            mSearchResultsTextView.setText(githubSearchResults);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // TODO (4) [Done] Create a new GithubQueryTask and call its execute method, passing in the url to query
        //new GithubQueryTask().execute(githubSearchUrl);

        // Asynctask has been fully deprecated... need to find a new way to accomplish multi thread operation.
        /* found information on Java.Util.Concurrent.  This package allows us to create ExecutorServices which
           will help us execute more time intensive tasks off of the main thread
        */

        // TODO (5) [Done] Create and instance of the ExecutorService class
        // TODO (6) [Done] in that Executor Service call the URL search routine
        // TODO (7) [Done] Create an instance of the Handler class
        // TODO (8) [Done] Use the Handler to update the search results text view

        Executor searchThread = Executors.newSingleThreadExecutor();
        Handler resultHandler = new Handler(Looper.getMainLooper());

        searchThread.execute(new Runnable(){
            @Override
            public void run(){
                try {
                    githubSearchResults =NetworkUtils.getResponseFromHttpUrl(githubSearchUrl);
                } catch (IOException e){
                    e.printStackTrace();
                }

                resultHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchResultsTextView.setText(githubSearchResults);
                    }
                });

            }
        });


    }

    // TODO (1) [Done] Create a class called GithubQueryTask that extends AsyncTask<URL, Void, String>
    // TODO (2) [Done] Override the doInBackground method to perform the query. Return the results. (Hint: You've already written the code to perform the query)
    // TODO (3) [Done] Override onPostExecute to display the results in the TextView
    /*public class GithubQueryTask extends AsyncTask <URL , Void, String>{
        @Override
        protected String doInBackground(URL... urls){
            URL searchURL = urls[0];
            String githubSearchResults = null;
            try{
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")){
                mSearchResultsTextView.setText(s);
            }
        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
