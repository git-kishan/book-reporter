package com.notebook.cvxt001122.bookbank;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private ArrayList<Model> bookList;
    private Context context;
    private LayoutInflater inflater;
    private OnMenuClick referenceContext;
    public RecyclerAdapter (Context context, ArrayList<Model> bookList, Activity mreferenceContext){
        this.context=context;
        this.bookList=bookList;
        inflater=LayoutInflater.from(context);
        this.referenceContext= (OnMenuClick) mreferenceContext;
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=inflater.inflate(R.layout.card_layout,viewGroup,false );
        return new CardViewHolder(view,context,referenceContext);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int i) {
        if(holder instanceof CardViewHolder){
            ((CardViewHolder) holder).bookName.setText(bookList.get(i).getBookName());
            ((CardViewHolder) holder).issuedDate.setText(bookList.get(i).getIssuedDate());
            ((CardViewHolder) holder).returningDate.setText(bookList.get(i).getReturningDate());
            String value=bookList.get(i).getInterval();
            if(value==null)
                value="null";
            else if(value.equals("temp"))
            {
                value="temporary";
            }else value="parmanent";
            ((CardViewHolder) holder).interval.setText(value);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}
