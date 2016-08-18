package com.fadhil.pshycologydictionary;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fadhil.pshycologydictionary.adapter.ListKataAdapter;
import com.fadhil.pshycologydictionary.app.DictionaryApplication;
import com.fadhil.pshycologydictionary.data.DefaultData;
import com.fadhil.pshycologydictionary.helper.KamusHelper;
import com.fadhil.pshycologydictionary.model.KamusModel;
import com.fadhil.pshycologydictionary.model.KamusObserver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    private ListView lvKata;
    private FloatingActionButton fab;
    private ArrayList<KamusModel> listKata;
    private KamusHelper kamusHelper;
    public ListKataAdapter listKataAdapter;
    private DictionaryApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvKata = (ListView)findViewById(R.id.lvListKata);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        listKata = new ArrayList<KamusModel>();
        application = (DictionaryApplication)getApplication();
        application.getKamusObserver().addObserver(this);

        kamusHelper = new KamusHelper(MainActivity.this);
        try {
            kamusHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listKata = kamusHelper.getAllData();
        if(listKata.size()>0){
            bindData();
        }
        else{
            insertDefaultData();
        }
        lvKata.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteDialog(listKata.get(position).getId());
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormInputUpdateActivity.toFormInputUpdate(MainActivity.this);
            }
        });
        lvKata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMeaningDialog(MainActivity.this, listKata.get(position));
            }
        });
    }

    private  void  insertDefaultData(){
        new StoreDefaultData().execute();
    }


    @Override
    public void update(Observable observable, Object o) {
        if (o.equals(KamusObserver.NEED_TO_REFRESH)){
            bindData();
        }
    }
    private class  StoreDefaultData extends AsyncTask<Void,Void,Void>{
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle(getString(R.string.notify_input_data));
            mProgressDialog.setMessage(getString(R.string.text_please_wait));
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            for (int i = 0; i < DefaultData.defaultData.length; i++) {
                kamusHelper.insert(KamusModel.getKamusModel(DefaultData.defaultData[i][0],
                        DefaultData.defaultData[i][1]));
            }

            listKata = kamusHelper.getAllData();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            listKataAdapter = new ListKataAdapter(MainActivity.this,listKata);
            lvKata.setAdapter(listKataAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        if (kamusHelper!=null){
            kamusHelper.close();
        }
        super.onDestroy();
    }
    public static void showMeaningDialog(final Activity activity, final KamusModel item) {
        final Dialog dialog = new Dialog(activity, R.style.AppCompatAlertDialogStyle);
        dialog.setContentView(R.layout.dialog_arti);
        dialog.setCancelable(true);

        TextView txtArti = (TextView)dialog.findViewById(R.id.txtMeaning);
        TextView txtKata = (TextView)dialog.findViewById(R.id.txtWord);
        Button btnTutup = (Button)dialog.findViewById(R.id.btnTutup);
        Button btnEdit = (Button)dialog.findViewById(R.id.btnEdit);

        txtArti.setText(item.getArti());
        txtKata.setText(item.getKata());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FormInputUpdateActivity.toFormInputUpdate(activity,item);
                dialog.dismiss();
            }
        });

        btnTutup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void deleteDialog(final int id) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        dialog.setTitle("Hapus");
        dialog.setContentView(R.layout.dialog_delete);
        dialog.setCancelable(true);

        Button btnYes = (Button)dialog.findViewById(R.id.btnDeleteYes);
        Button btnCancel = (Button)dialog.findViewById(R.id.btnDeleteCancel);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                kamusHelper.delete(id);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.text_success_delete), Toast.LENGTH_LONG).show();
                application.getKamusObserver().refresh();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void bindData(){
        if (listKata.size()>0) {
            listKata.clear();
        }
        listKata = kamusHelper.getAllData();
        listKataAdapter = new ListKataAdapter(MainActivity.this, listKata);
        lvKata.setAdapter(listKataAdapter);
        listKataAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


}
