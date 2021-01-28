package com.example.notetaker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    ListActivity listActivity;
    List<Model> modelList;
    Context context;

    public CustomAdapter(ListActivity listActivity, List<Model> modelList) {
        this.listActivity = listActivity;
        this.modelList = modelList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.model_layout,viewGroup,false);

        ViewHolder viewHolder=new ViewHolder(itemView);
        //handle item click
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // called when user click item

                //show data in toast on click
                /*String title=modelList.get(position).getTitle();
                String desc=modelList.get(position).getDescription();
                Toast.makeText(listActivity, title+"\n"+desc, Toast.LENGTH_SHORT).show();*/
                String id=modelList.get(position).getId();
                String title=modelList.get(position).getTitle();
                String description=modelList.get(position).getDescription();

                //intent to start activity
                Intent intent=new Intent(listActivity,DisplayActivity.class);
                intent.putExtra("pId",id);
                intent.putExtra("pTitle",title);
                intent.putExtra("pDescription",description);
                listActivity.startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {
                //called when user long click
                AlertDialog.Builder builder=new AlertDialog.Builder(listActivity);
                String[] options={"Update","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which ==0){
                            String id=modelList.get(position).getId();
                            String title=modelList.get(position).getTitle();
                            String description=modelList.get(position).getDescription();

                            //intent to start activity
                            Intent intent=new Intent(listActivity,MainActivity.class);
                            intent.putExtra("pId",id);
                            intent.putExtra("pTitle",title);
                            intent.putExtra("pDescription",description);

                            listActivity.startActivity(intent);

                        }
                        if(which==1){
                            listActivity.deleteData(position);

                        }

                    }
                }).create().show();

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //bind views/ set data
        viewHolder.mTitle.setText(modelList.get(i).getTitle());
        viewHolder.mDescription.setText(modelList.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
