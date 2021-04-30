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

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oceanizeandroidcodingtask.adapter.ItemAdapter;
import com.example.oceanizeandroidcodingtask.model.Item;
import com.example.oceanizeandroidcodingtask.viewmodel.ItemViewModel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemListener{
    protected RecyclerView recyclerView;
    private ArrayList<Item> itemArrayList;
    private ItemAdapter adapter;
    private ItemViewModel itemViewModel;
    private TextView ssOutputTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ssOutputTV = findViewById(R.id.sshOutputTV);
        // View Model
        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        getSSItemsData();
    }

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
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ItemAdapter(this,itemArrayList,this);
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
            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            channelssh.setOutputStream(baos);*/

            // Execute command
            channelssh.setCommand(item.getCommand());
            StringBuilder outputBuffer = new StringBuilder();
            StringBuilder errorBuffer = new StringBuilder();

            InputStream in = channelssh.getInputStream();
            InputStream err = channelssh.getExtInputStream();
            channelssh.connect();
            //wait for the "exec" channel to close (it closes once the command finishes).
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

            //Log.v("Output: ", baos.toString());
            ssOutputTV.setText(outputBuffer.toString());
        }
        catch(JSchException | IOException e){
            // show the error in the UI
            /*Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "Check WIFI or Server! Error : "+e.getMessage(),
                    Snackbar.LENGTH_LONG)
                    .setDuration(20000).setAction("Action", null).show();*/
            ssOutputTV.setText(e.getMessage()+"");
        }
    }

    @Override
    public void onItemClick(Item item) {
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
}