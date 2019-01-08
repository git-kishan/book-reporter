package com.notebook.cvxt001122.bookbank;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnMenuClick{

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinatorLayout;
    private int counter=0;
    private ShimmerFrameLayout shimmerFrameLayout;
    public static final String parmanent="0";
    public static final String temporary="1";
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private ArrayList<Model> dataList;
    private RecyclerAdapter adapter;
    private LinearLayout shimmerEffect;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> keyList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null)
            startActivity(new Intent(MainActivity.this,AuthenticationActivity.class));
        uid=user.getUid();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference().child(uid);
        initilization();

        adapter=new RecyclerAdapter(MainActivity.this,dataList ,MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        reference.addValueEventListener(eventListener);






    }
    private void initilization(){
        dataList=new ArrayList<>();
        recyclerView=findViewById(R.id.recycler_view);
        layoutManager=new LinearLayoutManager(this);
        appBarLayout=findViewById(R.id.appbar_layout);
        shimmerFrameLayout=findViewById(R.id.shimmer);
        shimmerEffect=findViewById(R.id.shimmer_effect);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("book bank");
        frameLayout=findViewById(R.id.fragment_container);
        fab=findViewById(R.id.fab);
        coordinatorLayout=findViewById(R.id.root_layout);
        fab.setOnClickListener(this);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        keyList.clear();
        handleBroadcasting();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataList.clear();
                keyList.clear();
                reference.addValueEventListener(eventListener);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerEffect.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onClick(View view) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(counter==0) {

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_down);
            Fragment fragment = new EntryFragment();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            counter=1;
            recyclerView.setVisibility(View.GONE);

        }

        else
        {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_down);
            Fragment fragment = new EntryFragment();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if(currentFragment.getClass().equals(fragment.getClass())) return;
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }



        }

    @Override
    public void onBackPressed() {
        counter=0;
        recyclerView.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }

    ValueEventListener eventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            dataList.clear();
            keyList.clear();
            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                keyList.add(snapshot.getKey());
                String returningDate = (String) snapshot.child("returningdate").getValue();
                String issuedDate = (String) snapshot.child("issuedate").getValue();
                String bookName = (String) snapshot.child("bookname").getValue();
                 String interval = (String) snapshot.child("interval").getValue();
                Model model = new Model(bookName, issuedDate, returningDate, interval,false);
                dataList.add(model);

            }
            adapter.notifyDataSetChanged();
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerEffect.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, "error occured", Toast.LENGTH_SHORT).show();

        }
    };
    @Override
    public void menuClicked(final int position) {
        reference.child(keyList.get(position)).removeValue();


    }

    private void handleBroadcasting(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    boolean isBroadcasted = (boolean) snapshot.child("isbroadcasted").getValue();
                    String returningDate= (String) snapshot.child("returningdate").getValue();
                    String bookName= (String) snapshot.child("bookname").getValue();
                    int [] seperateDate=spilitReturningDate(returningDate);
                    if(!isBroadcasted){
                        Intent intent=new Intent(MainActivity.this,OneDayBeforeReceiver.class);
                        intent.putExtra("bookname",bookName );
                        int  broadcastId= (int) System.currentTimeMillis();
                        //pending intent for one day before receiver
                        PendingIntent pendingIntent=PendingIntent.getBroadcast(MainActivity.this,broadcastId ,
                                intent,PendingIntent.FLAG_ONE_SHOT );
                        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR,3);
                        calendar.set(Calendar.MINUTE,5);
                        calendar.set(Calendar.DATE,seperateDate[0]-1);
                        calendar.set(Calendar.MONTH,seperateDate[1]);
                        calendar.set(Calendar.YEAR,seperateDate[2] );
                        calendar.add(Calendar.DATE,-1 );
                       // alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        //pending intent for on day receiver
                        Intent secondIntent=new Intent(MainActivity.this,OneDayBeforeReceiver.class);
                        int  secondBroadcastId= (int) System.currentTimeMillis();
                        PendingIntent secondPendingIntent=PendingIntent.getBroadcast(MainActivity.this,secondBroadcastId ,
                                secondIntent,PendingIntent.FLAG_ONE_SHOT );
                        calendar.add(Calendar.DATE,1 );
                        secondIntent.putExtra("bookname", bookName);
                       // alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis() ,secondPendingIntent );
                        Intent intent1=new Intent(MainActivity.this,HandleNotificationService.class);
                        intent1.putExtra("bookname", "kishan");
                        startService(intent1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private int [] spilitReturningDate(String date){
        String [] day=date.split("/", 3);
        int [] seperateDate=new int[] {Integer.parseInt(day[0]),Integer.parseInt(day[1]),Integer.parseInt(day[1])};
        return seperateDate;
    }
}
