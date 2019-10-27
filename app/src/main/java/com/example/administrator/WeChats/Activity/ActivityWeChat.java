package com.example.administrator.WeChats.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.administrator.WeChats.R;
import com.example.administrator.WeChats.ViewIndicator;
import com.example.administrator.WeChats.data.Chats;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ActivityWeChat extends AppCompatActivity implements ViewIndicator.OnIndicateListener,AdapterView.OnItemClickListener
{
    public static String number;
    public static  String displayName;
    public static Fragment[] mFragments;
    ArrayAdapter<String>talk_adapter;
    private List<String> talkList   = new ArrayList<>();

    public static  void actionStart(Context context) {
        Intent intent= new Intent(context , ActivityWeChat.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_wechat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);//返回箭头

//---------------------------------------------------------
        setFragmentIndicator(0);
//        FragmentIndicator wechat_fragment=new FragmentIndicator();
//        wechat_fragment.setFragmentIndicator(0);
//---------------------------------------------------------
        final ListView talkView = findViewById(R.id.talk_view);
        talk_adapter = new ArrayAdapter<>(ActivityWeChat.this,
                android.R.layout.simple_list_item_1, talkList);
        talkView.setAdapter(talk_adapter);

        if (ContextCompat.checkSelfPermission(ActivityWeChat.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(ActivityWeChat.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        else
            readContacts();
        talkView.setOnItemClickListener(this);
    }
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position)+ "";
                ActivityTalk.actionStart(this,name);//传递name参数
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_search:
            {//activity_menu_search
                Toast.makeText(this,"menu_search",Toast.LENGTH_SHORT).show();
            }break;
            case R.id.menu_add:
            {//activity_menu_add
               Toast.makeText(this,"menu_add",Toast.LENGTH_SHORT).show();
            }break;
            default :break;
        }
        return super.onOptionsItemSelected(item);
    }


    private static final String PHONE_BOOK_LABEL = "phonebook_label";//返回联系人名称的首字母
    private void readContacts() {
        Cursor cursor = null;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER, PHONE_BOOK_LABEL};
        String sortOrder = ContactsContract.Contacts.Photo.SORT_KEY_PRIMARY+ " ASC";//DESC
        try{
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,null,null,sortOrder);
            if(cursor != null)
            {
                while(cursor.moveToNext())
                {
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    talkList.add(displayName + "\n" + number);
                }
                talk_adapter.notifyDataSetChanged();
            }
        }catch (Exception e)    {e.printStackTrace();}
        finally {
            if(cursor!=null)    cursor.close();
        }
    }


    @Override
    public void  onRequestPermissionsResult(int requestCode, @NonNull String[] permission,@NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    readContacts();
                else
                    Toast.makeText(ActivityWeChat.this, "you denied the permission", Toast.LENGTH_SHORT).show();
                break;
        default:break;
        }
    }

   public  void setFragmentIndicator(int whichIsDefault) { //初始化fragment,always is 0
       mFragments = new Fragment[4];
       mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_wechat);
       mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_contacts);
       mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_discover);
       mFragments[3] = getSupportFragmentManager().findFragmentById(R.id.fragment_me);

       getSupportFragmentManager().beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).show(mFragments[whichIsDefault]).commit(); //显示默认的Fragment
       ViewIndicator mIndicator = findViewById(R.id.indicator);           //绑定自定义的菜单栏组件
       ViewIndicator.setIndicator(whichIsDefault);
       mIndicator.setOnIndicateListener(this);
   }
           @Override
           public void onIndicate(View v, int which) {
               getSupportFragmentManager().beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]).show(mFragments[which]).commit();
               switch (which) {
                   case 0:ActivityWeChat.actionStart(this);
                       break;
                   case 1: ActivityContacts.actionStart(this);
                       break;
                   case 2: ActivityDiscover.actionStart(this);
                       break;
                   case 3: ActivityMe.actionStart(this);
                       break;
                   default:
                       break;
               }
           }
}