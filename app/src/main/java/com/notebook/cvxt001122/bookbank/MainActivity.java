package com.notebook.cvxt001122.bookbank;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import android.util.Log;
import android.view.MenuItem;
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
    public static final String parmanent="parm";
    public static final String temporary="temp";
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
        toolbar.setTitle("Book bank");
        toolbar.inflateMenu(R.menu.toolbar_menu);
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.logOut){
                    AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(MainActivity.this,AuthenticationActivity.class));

                        }
                    });
                }
                return false;
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

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    boolean isBroadcasted;
                    String returningDate,bookName;
                    try {
                         isBroadcasted = (boolean) snapshot.child("isbroadcasted").getValue();
                         returningDate = (String) snapshot.child("returningdate").getValue();
                         bookName = (String) snapshot.child("bookname").getValue();
                    }catch (NullPointerException e){
                        Log.i("TAG","null pointer exception in invoking boolean,"+e.getLocalizedMessage() );
                    }finally {
                        isBroadcasted = (boolean) snapshot.child("isbroadcasted").getValue();
                        returningDate = (String) snapshot.child("returningdate").getValue();
                        bookName = (String) snapshot.child("bookname").getValue();
                    }
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
                        calendar.set(Calendar.HOUR,8);
                        calendar.set(Calendar.MINUTE,30);
                        calendar.set(Calendar.DATE,seperateDate[0]);
                        calendar.add(Calendar.DATE,-1 );
                        calendar.set(Calendar.MONTH,seperateDate[1]);
                        calendar.set(Calendar.YEAR,seperateDate[2] );
                        if(calendar.getTimeInMillis()<System.currentTimeMillis()) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        }
                        //pending intent for on day receiver

                        Intent secondIntent=new Intent(MainActivity.this,OnSubmitDayReceiver.class);
                        int  secondBroadcastId= (int) System.currentTimeMillis();
                        PendingIntent secondPendingIntent=PendingIntent.getBroadcast(MainActivity.this,secondBroadcastId ,
                                secondIntent,PendingIntent.FLAG_ONE_SHOT );
                        calendar.add(Calendar.DATE,1 );
                        secondIntent.putExtra("bookname", bookName);
                        if(calendar.getTimeInMillis()>=System.currentTimeMillis()) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), secondPendingIntent);
                        }
                        snapshot.child("isbroadcasted").getRef().setValue(true);


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
        int [] seperateDate=new int[] {Integer.parseInt(day[0]),Integer.parseInt(day[1]),Integer.parseInt(day[2])};
        return seperateDate;
    }
}
