package com.capstone.autofix;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.helper.SQLiteHandler;
import com.capstone.autofix.helper.SessionManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView p_pic;
    private TextView p_name,p_addr,p_phone,p_user,p_email,p_id;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account,container,false);

        p_name = (TextView) view.findViewById(R.id.prof_name);
        p_addr = (TextView) view.findViewById(R.id.prof_address);
        p_pic = (CircleImageView) view.findViewById(R.id.prof_pic);
        p_email = (TextView) view.findViewById(R.id.prof_email);
        p_phone = (TextView) view.findViewById(R.id.prof_phone);
        p_user = (TextView) view.findViewById(R.id.prof_user);
        p_id = (TextView) view.findViewById(R.id.prof_id);

        db = new SQLiteHandler(getActivity());
        session = new SessionManager(getActivity());
        HashMap<String,String> user = db.getUserDetails();
        String p = user.get("photo");
        String pic;
        if (p.equals("null")){
            pic = AppConfig.MAIN_URL+"uploads/blank.png";
        }else {
            pic = AppConfig.MAIN_URL +"uploads/" + p;
        }
        String name = user.get("fullname");
        String address = user.get("address");
        String contact = user.get("contact");
        String username = user.get("username");
        String email = user.get("email");
        String cid = user.get("cid");

        p_name.setText(name);
        p_addr.setText(address);
        p_phone.setText(contact);
        p_user.setText(username);
        p_id.setText(cid);
        p_email.setText(email);
        Picasso.get().load(pic).resize(300,300).centerCrop().into(p_pic);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
