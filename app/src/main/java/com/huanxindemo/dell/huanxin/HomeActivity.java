package com.huanxindemo.dell.huanxin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.huanxindemo.dell.huanxin.adapter.Adapter_Constact;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{

    ListView lv_main;
    Button btn_add_mian;
    Adapter_Constact adapter_constact;
    List<String> usernames;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 1:
                    //从线程传递list到主线程更新UI
                    usernames = (List<String>) msg.obj;
                    adapter_constact.addresst(usernames);

                    Log.d("usernames", usernames.size() + "");
                    break;

                case 2:

                    String name = (String) msg.obj;
                    friend(name);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lv_main = (ListView) findViewById(R.id.lv_main);
        btn_add_mian = (Button) findViewById(R.id.btn_add_mian);
        adapter_constact = new Adapter_Constact(HomeActivity.this);
        lv_main.setAdapter(adapter_constact);


        //设置监听
        lv_main.setOnItemClickListener(this);
        btn_add_mian.setOnClickListener(this);

        FriendThread friendThread = new FriendThread();
        friendThread.start();
        AddFriend();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_add_mian://添加的好友

                AddFriend addFriend = new AddFriend();
                addFriend.start();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_USERNAME, usernames.get(position));
        startActivity(intent);
        //finish();
    }
    //获取联系人列表
    public void ContectList() {

        try {
            usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();

            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.obj = usernames;
            handler.sendMessage(msg);

            Log.d("usernames1", usernames.size() + "");

        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }
    //好友监听的方法
    public void AddFriend() {

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意

                Log.d("username", "好友请求被同意" + username);
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                Log.d("username", "好友请求被拒绝" + username);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.d("username", "收到好友邀请" + username);


                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.obj = username;
                handler.sendMessage(msg);

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                Log.d("username", "被删除时回调此方法" + username);
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.d("username", "增加了联系人时回调此方法" + username);
            }
        });
    }


    //开线程是获取好友列表
    class FriendThread extends Thread {

        @Override
        public void run() {
            super.run();

            ContectList();
        }
    }

    //添加好友
    public void addFriend() {

        //参数为要添加的好友的username和添加理由
        try {
            EMClient.getInstance().contactManager().addContact("zx2", "你好");
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    class AddFriend extends Thread {

        @Override
        public void run() {
            super.run();

            addFriend();
        }
    }

    //同意或者拒绝好友请求
    public void friend(final String username) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog

                argeFriend argeFriend = new argeFriend(username);
                argeFriend.start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                refuseFriend argeFriend = new refuseFriend(username);
                argeFriend.start();
            }
        });


        //参数都设置完成了，创建并显示出来
        builder.show();
    }


    //同意好友请求
    class argeFriend extends Thread{

        String username;

        argeFriend(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            super.run();

            try {
                EMClient.getInstance().contactManager().acceptInvitation(username);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    //jujue好友请求
    class refuseFriend extends Thread{

        String username;

        refuseFriend(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            super.run();

            try {
                EMClient.getInstance().contactManager().declineInvitation(username);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }



}


