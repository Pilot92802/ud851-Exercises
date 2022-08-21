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
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    // TODO (12) [Done] Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageTextView;
    // TODO (24) [Done] Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar pbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_github_search_results_json);

        // TODO (13) [Done] Get a reference to the error TextView using findViewById
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);

        // TODO (25) [Done] Get a reference to the ProgressBar using findViewById
        pbView = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView,
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        // TODO [Done] convert AsynchTask to Executor/Handler w/ java.util.concurrent
        Executor searchThread = Executors.newSingleThreadExecutor();
        Handler resultHandler = new Handler(Looper.getMainLooper());

        searchThread.execute(new Runnable(){
            @Override
            public void run(){
                resultHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pbView.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    githubSearchResults = NetworkUtils.getResponseFromHttpUrl(githubSearchUrl);
                    if(githubSearchResults == null)
                        showErrorMessage(resultHandler);
                    else
                        showJSONData(resultHandler);
                } catch (IOException e){
                    e.printStackTrace();
                }


            }
        });

    }

    // TODO (14) [Done] Create a method called showJsonDataView to show the data and hide the error
    private void showJSONData(Handler handler){
        handler.post(new Runnable() {
            @Override
            public void run() {
                pbView.setVisibility(View.INVISIBLE);
                mSearchResultsTextView.setText(githubSearchResults);
                mErrorMessageTextView.setVisibility(View.INVISIBLE);
                mSearchResultsTextView.setVisibility(View.VISIBLE);
            }
        });

    }
    // TODO (15) [Done] Create a method called showErrorMessage to show the error and hide the data
    private void showErrorMessage(Handler handler){
        handler.post(new Runnable() {
            @Override
            public void run() {
                pbView.setVisibility(View.INVISIBLE);
                mSearchResultsTextView.setText(githubSearchResults);
                mErrorMessageTextView.setVisibility(View.VISIBLE);
                mSearchResultsTextView.setVisibility(View.INVISIBLE);
            }
        });
    }

    // TODO [Done] replace GithubQueryTask with Executor/Handler logic
    // TODO (26) [Done] Override onPreExecute to set the loading indicator to visible

    // TODO (27) [Done] As soon as the loading is complete, hide the loading indicator
    // TODO (17) [Done] Call showJsonDataView if we have valid, non-null results
    // TODO (16) [Done] Call showErrorMessage if the result is null in onPostExecute

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
