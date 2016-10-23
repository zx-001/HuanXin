package com.huanxindemo.dell.huanxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huanxindemo.dell.huanxin.util.ToastUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_login;
    EditText edt_password;
    EditText edt_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = (Button)findViewById(R.id.btn_login);
        edt_username = (EditText)findViewById(R.id.edt_username);
        edt_password = (EditText)findViewById(R.id.edt_password);


        btn_login.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_login:

                String username = edt_username.getText().toString().trim();
                String password = edt_password.getText().toString().trim();

                //逻辑判断
                if(null == username || username.equals("")){
                    ToastUtil.show(getApplicationContext(),"用户名不能为空");
                    return;
                }

                if(null == password || password.equals("")){
                    ToastUtil.show(getApplicationContext(),"密码名不能为空");
                    return;
                }

                Login(username,password);

                break;
        }
    }

    //登陆
    public void Login(String userName,String password){

        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");

                COnstact();

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }

    //跳转到获取联系人列表
    public void COnstact(){

        Intent intent = new Intent(MainActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

