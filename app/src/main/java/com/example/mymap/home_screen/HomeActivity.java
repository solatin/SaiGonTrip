package com.example.mymap.home_screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mymap.R;
import com.example.mymap.database.MyLocation;
import com.example.mymap.trip_screen.TripActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static DatabaseReference databaseReference;
    private static final String TAG = "HomeActivity";
    private ListView _listView;
    MyLocationAdapter _myLocationAdapter;
    private Button button;
    ArrayList<MyLocation> locationsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toast.makeText(getApplicationContext(),"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
        setTitle("Choose Your Destinations");
        locationsArrayList = new ArrayList<>();
        initData();
        initListView();
        initButton();
    }

    private void initListView() {
        _myLocationAdapter = new MyLocationAdapter(this, R.layout.home_activity_listview_item, locationsArrayList);
        _listView = findViewById(R.id.listView);
        _listView.setAdapter(_myLocationAdapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MyLocationInfoScreen.class);
                intent.putExtra("location_idx", position);
                startActivity(intent);
            }

        });
    }


    private void initData() {
        databaseReference= FirebaseDatabase.getInstance().getReference("myLocation");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot di:dataSnapshot.getChildren()){
                    locationsArrayList.add(di.getValue(MyLocation.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void initButton() {
        this.button = (Button)findViewById(R.id.button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                let_go();
            }
        });
    }

    private void let_go() {
        SparseBooleanArray sp = _myLocationAdapter.get_checkStates();
        ArrayList<Integer> picked_locations_idx = new ArrayList<>();
        for(int i=0; i<sp.size(); i++){
            if(sp.valueAt(i)){
                picked_locations_idx.add(sp.keyAt(i));
            }
        }

        Log.d(TAG, "num picked "+picked_locations_idx.size());
        for(int i=0; i<picked_locations_idx.size(); i++)
            Log.d(TAG, "picked "+ picked_locations_idx.get(i));
        Intent intent = new Intent(this, TripActivity.class);
        intent.putIntegerArrayListExtra("picked_locations_idx",picked_locations_idx);
        intent.putExtra("roundIntent", 1);
        startActivity(intent);
    }

}