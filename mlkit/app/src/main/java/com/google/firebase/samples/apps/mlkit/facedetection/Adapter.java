package com.google.firebase.samples.apps.mlkit.facedetection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.facedetection.Objetos.Glasses;
import java.util.List;

/**
 * Created by Sadruddin on 12/24/2017.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.GlassesViewHolder>{
    private List<Glasses> horizontalGlassesList;
    Context context;

    public Adapter(List<Glasses> horizontalGlassesList, Context context){
        this.horizontalGlassesList= horizontalGlassesList;
        this.context = context;
    }

    @Override
    public GlassesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View glassesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list_glasses_stuff, parent, false);
        GlassesViewHolder gvh = new GlassesViewHolder(glassesProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GlassesViewHolder holder, final int position) {
        holder.imageView.setImageResource(horizontalGlassesList.get(position).getProductImage());
        holder.txtview.setText(horizontalGlassesList.get(position).getProductName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = horizontalGlassesList.get(position).getProductName().toString();
                Toast.makeText(context, productName + " seleccionadas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalGlassesList.size();
    }

    public class GlassesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtview;
        public GlassesViewHolder(View view) {
            super(view);
            imageView=view.findViewById(R.id.idProductImage);
            txtview=view.findViewById(R.id.idProductName);
        }
    }
}