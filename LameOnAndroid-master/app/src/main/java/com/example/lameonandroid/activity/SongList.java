package com.example.lameonandroid.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lameonandroid.R;
import com.example.lameonandroid.adapter.RecycleFileAdapter;
import com.example.lameonandroid.custom_view.RecycleViewDivider;
import com.example.lameonandroid.function.Constants;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.lameonandroid.R.drawable.file;

/**
 *
 *
 * @author pdm
 */
@SuppressLint("ShowToast")
public class SongList extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<SdFile> data;
    private LinearLayout ll_edit;
    private TextView tv_delete, tv_unedit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEdit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEdit() {
        if (adapter != null) {
            adapter.setEdit(true);
            adapter.notifyDataSetChanged();
        }
        ll_edit.setVisibility(View.VISIBLE);
    }

    private void goneEdit(){
        if (adapter != null) {
            adapter.setEdit(false);
            adapter.notifyDataSetChanged();
        }
        ll_edit.setVisibility(View.GONE);
    }
    private SdFile sdFile = null;
    ProgressDialog progressDialog;
    private RecycleFileAdapter adapter;
    private Toolbar toolbar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlist_main);
        initView();
    }

    private void initView() {
        ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_unedit = (TextView) findViewById(R.id.tv_unedit);
        tv_delete.setOnClickListener(this);
        tv_unedit.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.converting));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        data = new ArrayList<SdFile>();

        recyclerView = (RecyclerView) findViewById(R.id.file_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局方式
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置分割线
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL,
                1, Color.BLUE));
//        recyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
//        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);

        //设置每个item高度固定，提高性能
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                data.clear();
//                sortData(Constants.ROOT);
//                adapter.notifyDataSetChanged();
//                recyclerView.refreshComplete();
//            }
//
//            @Override
//            public void onLoadMore() {
//
//            }
//        });
        // 适配器
        // 获取数据
        String path = Constants.INSIDEROOT;
        Log.e("hhhhhhhhhhhhhh",Constants.DEFULTROOT);
        sortData(Constants.DEFULTROOT);
        //如果没有WAV文件，则从资源文件中获取
        if (data.size() == 0){
            handle.post(run);
        }else {
//            for (SdFile file : data){
//                new File(file.getFilePath()).delete();
//            }
        }
        adapter = new RecycleFileAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecycleFileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (data.get(position).getName().toLowerCase().endsWith(".wav")) {
                    showOperateDialog(data.get(position).getFilePath());
                } else {
                    playMusic(data.get(position).getFilePath());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        File file = new File(Constants.DEFULTROOT + "ccc.wav");
        if (file.exists()){
            Log.e("hahahhahhahahah","存在");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                showDelteDialog();
                break;
            case R.id.tv_unedit:
                ll_edit.setVisibility(View.INVISIBLE);
                adapter.setEdit(false);
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setIschecked(false);
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }
    private void sortData(String path){
        initData(path);
        Collections.sort(data);
    }
    /**
     * @param path 当前列表的目录
     * @author
     */
    private void initData(String path) {
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "路径出错", Toast.LENGTH_SHORT).show();
        } else {
            File[] file_list = file.listFiles();// 拿到该目录下所有的文件对象
            for (File f : file_list) {
                sdFile = new SdFile();
                sdFile.setFile(f);// 文件对象
                sdFile.setFilePath(f.getAbsolutePath());// 文件路径
                sdFile.setName(f.getName());// 文件名
                sdFile.setSize(f.length());
                if (f.isDirectory()) {
                    initData(f.getPath());
                } else if (f.getName().toLowerCase().endsWith(".wav")
                        || f.getName().toLowerCase().endsWith(".mp3")) {
                    data.add(sdFile);
                }
            }
        }
    }

    private void playMusic(String path) {
        try {
            File audioFile = new File(path);
            if (audioFile.exists()) {
                Uri uri = Uri.fromFile(audioFile);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "audio/wav");
                intent.setDataAndType(uri, "audio/mp3");
                intent.setDataAndType(uri, "audio/*");
                startActivity(intent);
            } else {
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "No suitable application opens", Toast.LENGTH_SHORT).show();
        } finally {

        }
    }

    private void showOperateDialog(final String wavPath) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.operate))
                .setPositiveButton(getResources().getString(R.string.convert),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //wav转码mp3
                                convert(wavPath);
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.player),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                playMusic(wavPath);

                            }
                        }).show();
    }

    private void convert(final String wavPath) {
        File file = new File(wavPath);
        if (file.exists()) {
            progressDialog.setMax((int) file.length());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            ConvertRunnble runnble = new ConvertRunnble(wavPath);
            Thread convertThread = new Thread(runnble);
            convertThread.start();
        } else {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConvertRunnble implements Runnable {
        private String wavPath;

        public ConvertRunnble(String wavPath) {
            this.wavPath = wavPath;
        }

        @Override
        public void run() {
            long a = System.nanoTime() / 1000000;
            String mp3Path = wavPath.substring(0, wavPath.lastIndexOf(".")) + ".mp3";
            convert(wavPath, mp3Path);
            int b = (int) (System.nanoTime() / 1000000 - a) / 1000;//这里单位为:秒
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = b;
            handle.sendMessage(msg);
        }
    }
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            try {
                //得到资源文件中所有的Wav歌曲
                String[] files = getAssets().list("");
                for (String fileName : files) {
                    //找到所有以.orm.xml结尾的文件
                    if (fileName.endsWith(".wav")) {
                        File file = new File(Constants.DEFULTROOT + "/" + fileName);
                        InputStream in = getAssets().open(fileName);
                        FileOutputStream out = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int length = 0;
                        while ((length = in.read(buf)) > -1) {
                            out.write(buf, 0, length);
                        }
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
                Message msg = new Message();
                msg.what = 2;
                handle.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        progressDialog.setProgress(msg.arg1);
                        Log.e("进度显示…………", msg.arg1 + "");
                        break;
                    case 1:
                        //转码成功
                        progressDialog.dismiss();
                        Toast.makeText(SongList.this, "Time = " + msg.arg1 + "秒", Toast.LENGTH_LONG).show();
                        Log.e("Time = ", msg.arg1 + "秒");
                        //先清除集合
                        data.clear();
                        sortData(Constants.ROOT);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        sortData(Constants.DEFULTROOT);
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };


    private void showDelteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.select))
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //删除所有选中歌曲
                                if (data.size() > 0) {
                                    for (int i = 0; i < data.size(); i++) {
                                        if (data.get(i).ischecked()) {
                                            String path = data.get(i).getFilePath();
                                            if (path != null) {
                                                //这里是彻底删除文件，为了测试，我屏蔽了
                                                new File(data.get(i).getFilePath()).delete();
                                            }
                                            adapter.remove(i);
                                            i = i - 1;
                                        }
                                    }
                                }
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }
                        }).show();
    }

    public native String getLameVersion();

    public native void convert(String wavFile, String mp3File);

    static {
        System.loadLibrary("lame");
    }

    /**
     * 更新进度，供C调用
     *
     * @param progress
     */
    public void updateProgress(int progress) {
        Message msg = new Message();
        msg.arg1 = progress;
        msg.what = 0;
        handle.sendMessage(msg);
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (adapter.isEdit()){
            goneEdit();
        }else {
            super.onBackPressed();
        }
    }

}