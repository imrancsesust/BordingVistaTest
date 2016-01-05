package com.bordingvista.test.bordingvistatest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bordingvista.test.bordingvistatest.model.Department;
import com.bordingvista.test.bordingvistatest.utility.Constants;
import com.bordingvista.test.bordingvistatest.utility.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private String TAG = "BORDINGVISTATAG";
    private final String apiUrl = "http://vikingmobile.bording.dk/agetor/vikingproxy/ArticleInq?ClientId=1&Store=4";
    private ArrayList<Department> departmentList;
    private ListView listView;
    private Context context;
    private CallServiceAsync serviceAsync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        listView = (ListView) findViewById(R.id.deptListView);
        departmentList  = new ArrayList();
        serviceAsync = new CallServiceAsync();
        serviceAsync.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceAsync.cancel(true);
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

    private class CallServiceAsync extends AsyncTask<String, Integer, String>{
        private ProgressDialog progressDialog;

        private CallServiceAsync() {
            this.progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading Department Data");

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            ServiceHandler serviceHandler = new ServiceHandler();
            String response = serviceHandler.getAPIData(apiUrl);
            return response;
        }

        @Override
        protected void onPostExecute(String output) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (output == null || output.equals(Constants.SERVER_RESPONSE)) {
                Log.d(TAG, "From server down");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "Service API is unreachable", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                parseData(output);
                DeptArrayAdapter deptArrayAdapter = new DeptArrayAdapter(getApplicationContext(), departmentList);
                listView.setAdapter(deptArrayAdapter);
            }
        }
        }

    void parseData(String output){
        try {
            JSONObject rootObject = new JSONObject(output);
            JSONObject articleObject = rootObject.getJSONObject("ArticleInq");
            JSONObject departmentsObject = articleObject.getJSONObject("Departments");
            JSONArray departments = departmentsObject.getJSONArray("Dept");
            Log.d(TAG,"Array Data:"+departments.get(0).toString());

            for (int i = 0; i < departments.length(); i++){
                JSONObject department = departments.getJSONObject(i);
                String deptNo = department.getString("DeptNo");
                String deptText = department.getString("DeptText");
                String count = department.getString("Count");

                Department deptObj = new Department();
                deptObj.setDeptNo(deptNo);
                deptObj.setDeptText(deptText);
                deptObj.setCount(count);

                departmentList.add(deptObj);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public class DeptArrayAdapter extends ArrayAdapter<Department>{

        private class ViewHolder {
            private TextView textViewDeptNo;
            private TextView textViewDeptText;
            private TextView textViewDeptCounter;
        }

        public DeptArrayAdapter(Context context, ArrayList<Department> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView == null) {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.dept_list_row, parent, false);

            }
            if(convertView != null) {
                TextView textViewDeptNo = (TextView) convertView.findViewById(R.id.deptNoTV);
                TextView textViewDeptText = (TextView) convertView.findViewById(R.id.deptTextTV);
                TextView textViewDeptCounter = (TextView) convertView.findViewById(R.id.deptCounterTV);
                Department department = getItem(position);
                Log.d(TAG, "Department obj" + department);
                if (department != null) {
                    textViewDeptNo.setText("Dept No: " +department.getDeptNo());
                    textViewDeptText.setText("Dept Text: " +department.getDeptText());
                    textViewDeptCounter.setText("Dept Counter: " +department.getCount());
                }
            }
            return convertView;
        }
    }
}
