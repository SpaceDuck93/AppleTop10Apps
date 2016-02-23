package com.spaceduck.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String mFileContents;
    private Button btnParse;
    private ListView listApps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnParse = (Button) findViewById(R.id.btnParse);
        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this, R.layout.list_item, parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });
        listApps = (ListView) findViewById(R.id.xmlListView);
        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Class within main method that allows app to run asynchronous with 3 parameters
    private class DownloadData extends AsyncTask<String, Void, String>{
        //String= link to download, void =we don't want a progress bar, string=file contents

        @Override //override method do in background
        protected String doInBackground(String... params) {
        //method we want to run in background. parameters can vary in quantities
        //without holding up application
            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null){
                Log.d("DownloadData", "Error downloading");
                }
        return mFileContents;
        }



        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            Log.d("DownloadData", "Result was: " + result);


        }

        private String downloadXMLFile(String urlPath) { //Download method with String param
        //temporary buffer to store contents
        StringBuilder tempBuffer = new StringBuilder();
        try{
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int respone = connection.getResponseCode();
            Log.d("DownloadData", "The response code was " + respone);
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            //^^^ getting ready to download data. Opening Stream Channel.
            //Downloading Data using 2 variables
            int charRead; //Read one char at a time
            char[] inputBuffer = new char[500];
            while (true){
                charRead = isr.read(inputBuffer);
                if (charRead <=0){
                    break;
                }
                tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
            }
            return tempBuffer.toString();

        }catch(IOException e){
           Log.d("DownloadData", "IO Exception reading data: " +e.getMessage());

        }catch (SecurityException e){
            Log.d("DownloadData", "Security exception. Needs Permissions? " + e.getMessage());
        }
        return null;

        }
    }

}

