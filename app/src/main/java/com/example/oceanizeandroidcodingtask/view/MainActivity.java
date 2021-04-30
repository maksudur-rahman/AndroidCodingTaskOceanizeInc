package com.example.oceanizeandroidcodingtask.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.oceanizeandroidcodingtask.R;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.oceanizeandroidcodingtask.adapter.ItemAdapter;
import com.example.oceanizeandroidcodingtask.adapter.ShellItemAdapter;
import com.example.oceanizeandroidcodingtask.model.Item;
import com.example.oceanizeandroidcodingtask.viewmodel.ItemViewModel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemListener{
    protected RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<Item> itemArrayList;
    private ItemAdapter adapter;
    private ItemViewModel itemViewModel;
    private ListView listView;
    private ShellItemAdapter shellItemAdapter;
    private ArrayList<String> items = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //initialize view
        initializeView();
        // View Model
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        progressBar.setVisibility(View.VISIBLE);
        getSSItemsData();
    }

    private void initializeView() {
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.listView);
        shellItemAdapter = new ShellItemAdapter(this,items);
        listView.setAdapter(shellItemAdapter);
    }
    /**
     * get Item Details from api
     *
     * @param @null
     */
    public void getSSItemsData() {
        itemViewModel.getAllSSList().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> movies) {
                itemArrayList = (ArrayList<Item>) movies;
                showRecyclerViewData();
            }
        });
    }

    public void showRecyclerViewData() {
        progressBar.setVisibility(View.GONE);
        // adapter
        adapter = new ItemAdapter(this,itemArrayList,this);
        // use a Grid layout manager
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void executeRemoteCommand(Item item) {

        try{
            JSch jsch = new JSch();
            Session session = jsch.getSession(item.getUsername(), item.getHost(), item.getPort());
            session.setPassword(item.getPassword());

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.setTimeout(10000);
            session.connect();

            // SSH Channel
            ChannelExec channelssh = (ChannelExec)
                    session.openChannel("exec");

            // Execute command
            channelssh.setCommand(item.getCommand());
            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream in = channelssh.getInputStream();
            InputStream err = channelssh.getExtInputStream();
            channelssh.connect();
            //wait for the "exec" channel to close (it closes once the command finishes).
            //One has to read the output continuously, while waiting for the command to finish. Otherwise,
            // if the command produces enough output to fill in an output buffer, the command will hang,
            // waiting for the buffer to be consumed, what never happens. So you get a deadlock.
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errorBuffer.append(new String(tmp, 0, i));
                }
                if (channelssh.isClosed()) {
                    if ((in.available() > 0) || (err.available() > 0)) continue;
                    System.out.println("exit-status: " + channelssh.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            System.out.println("output: " + outputBuffer.toString());
            System.out.println("error: " + errorBuffer.toString());
            // Sleep needed in order to wait long enough to get result back.
            //Thread.sleep(1_000);
            channelssh.disconnect();
            items.add(">>"+outputBuffer.toString());
            shellItemAdapter.notifyDataSetChanged();
        }
        catch(JSchException | IOException e){
            // show the error in the UI
            items.add(">>"+e.getMessage());
            shellItemAdapter.notifyDataSetChanged();
        }
    }

    //RecyclerView Item click
    @Override
    public void onItemClick(Item item) {
        listView.setVisibility(View.VISIBLE);
        Toast.makeText(this, item.getName()+" Clicked", Toast.LENGTH_SHORT).show();
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                //// do background heavy task here
                try {
                    executeRemoteCommand(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //back button pressed
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}