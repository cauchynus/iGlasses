package com.google.firebase.samples.apps.mlkit.facedetection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private OnItemClickListener mListener;//un click al recycler

    public Adapter(List<Glasses> horizontalGlassesList, Context context, OnItemClickListener listener){
        this.horizontalGlassesList= horizontalGlassesList;
        this.context = context;
        this.mListener = listener;
    }

    @Override
    public GlassesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View glassesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list_glasses_stuff, parent, false);
        GlassesViewHolder gvh = new GlassesViewHolder(glassesProductView, mListener);//le pasamos la view y el click al holder
        return gvh;
    }
    public interface OnItemClickListener {
        void onItemClick(Glasses gafa);//lo que se le pasa en el onItemClick si o si en el HomeActivity
    }
    @Override
    public void onBindViewHolder(GlassesViewHolder holder, final int position) {
        holder.imageView.setImageResource(horizontalGlassesList.get(position).getProductImage());
        holder.txtview.setText(horizontalGlassesList.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return horizontalGlassesList.size();
    }

    public class GlassesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtview;
        public GlassesViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            imageView=view.findViewById(R.id.idProductImage);
            txtview=view.findViewById(R.id.idProductName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(horizontalGlassesList.get(getAdapterPosition()));//este controla q posicion hemos seleccionado
                }
            });
        }
    }
}