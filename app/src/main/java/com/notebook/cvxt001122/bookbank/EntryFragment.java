package com.notebook.cvxt001122.bookbank;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class EntryFragment extends Fragment implements View.OnClickListener {


    public EntryFragment() {
    }


    private TextInputLayout bookNameInputLayout,issuedDateInputLayout;
    private TextInputEditText bookNameInputEditText,issuedDateInputEditText;
    private MaterialButton saveButton,cancelButton;
    private ConstraintLayout constraintLayout;
    final  Calendar calendar=Calendar.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private RadioGroup radioGroup;
    private MaterialRadioButton radioButton;
    private String radioButtonReference="temp";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_entry, container, false);
        bookNameInputEditText=view.findViewById(R.id.text_input_edit_text1);
        issuedDateInputEditText=view.findViewById(R.id.text_input_edit_text2);
        bookNameInputLayout=view.findViewById(R.id.text_input_layout_1);
        issuedDateInputLayout=view.findViewById(R.id.text_input_layout_2);
        saveButton=view.findViewById(R.id.material_button_save);
        cancelButton=view.findViewById(R.id.material_button_cancel);
        radioGroup=view.findViewById(R.id.radio_group);
        radioButton=radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        constraintLayout=view.findViewById(R.id.root_layout);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        if(user==null)
            startActivity(new Intent(getContext(),AuthenticationActivity.class));

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        uid=user.getUid();
        databaseReference=database.getReference().child(uid);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        issuedDateInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton=radioGroup.findViewById(i);
                if(radioButton.isChecked()&& radioButton.getId()==R.id.temporary){
                    radioButtonReference="temp";
                    Log.i("TAG","radio button reference temporary" );
                }
                if(radioButton.isChecked()&& radioButton.getId()==R.id.parmanent){
                    radioButtonReference = "parm";
                    Log.i("TAG","radio button reference parmanent" );

                }
            }
        });
    }
    private void updateLabel() {
        DateHandler dateHandler=new DateHandler(calendar);
        issuedDateInputEditText.setText(dateHandler.getFormatedDate());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.material_button_cancel)
            onCancelClicked();
        if(view.getId()==R.id.material_button_save)
            onSavedClicked();
    }
    private void onSavedClicked(){

        boolean valid=checkValidation();
        if(valid){
            String bookname=bookNameInputEditText.getText().toString();
            String issuedate=calendar.get(Calendar.DATE)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);

            if(MainActivity.temporary.equals(radioButtonReference)) {
                Log.i("TAG","changing returning date" );
                calendar.add(Calendar.DATE, 15);
            }

            if(MainActivity.parmanent.equals(radioButtonReference)) {
                Log.i("TAG","changing returning date" );
                calendar.add(Calendar.MONTH, 6);
            }

            String returningdate=calendar.get(Calendar.DATE)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);
            String key=databaseReference.push().getKey();
            if(key==null){
                Snackbar.make(constraintLayout , "failed to add,try later", Snackbar.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return;
            }
            databaseReference.child(key).child("bookname").setValue(bookname);
            databaseReference.child(key).child("issuedate").setValue(issuedate);
            databaseReference.child(key).child("returningdate").setValue(returningdate);
            databaseReference.child(key).child("interval").setValue(radioButtonReference);
            databaseReference.child(key).child("isbroadcasted").setValue(false);

            Snackbar.make(constraintLayout, "data saved",Snackbar.LENGTH_SHORT ).show();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        getActivity().onBackPressed();
                    }catch (NullPointerException e)
                    {
                        Log.i("TAG","exception on pressing back button in fragment:- "+e.getLocalizedMessage() );
                    }

                }
            }, 2000);

        }

    }
    private void onCancelClicked(){
        getActivity().onBackPressed();
    }
    private boolean checkValidation(){
        if(bookNameInputEditText.getText().toString().length()==0){
            Snackbar.make(constraintLayout,"book name is empty" ,Snackbar.LENGTH_SHORT ).show();
            return false;
        }
        else if(bookNameInputEditText.getText().toString().length()>30){
            Snackbar.make(constraintLayout,"book name must be less 30" ,Snackbar.LENGTH_SHORT ).show();
            return false;
        }
        else if(issuedDateInputEditText.getText().toString().length()==0){
            Snackbar.make(constraintLayout,"select issued date" ,Snackbar.LENGTH_SHORT ).show();
            return false;
        }
       return true;
    }

}
