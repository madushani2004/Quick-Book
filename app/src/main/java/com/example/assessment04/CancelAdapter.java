package com.example.assessment04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment04.data.Cancel;

import java.util.List;

public class CancelAdapter extends RecyclerView.Adapter<CancelAdapter.CancelViewHolder> {
    private Context context;
    private List<Cancel> data;

    public CancelAdapter(Context context, List<Cancel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CancelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_request_item, parent, false);
        return new CancelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelViewHolder holder, int position) {
        holder.passengerEmail.setText(data.get(position).passenger);

        holder.accept.setOnClickListener(view -> {
            DatabaseHelper helper = new DatabaseHelper(context);
            helper.cancelBooking(data.get(position).seatNo);
            Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
            data.remove(position);
            CancelAdapter.this.notifyDataSetChanged();
        });

        holder.cancel.setOnClickListener(view -> {
            DatabaseHelper helper = new DatabaseHelper(context);
            helper.declineCancelBooking(data.get(position).seatNo);
            Toast.makeText(context, "Declined!", Toast.LENGTH_SHORT).show();
            data.remove(position);
            CancelAdapter.this.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CancelViewHolder extends RecyclerView.ViewHolder {

        TextView passengerEmail;
        Button accept, cancel;

        public CancelViewHolder(@NonNull View itemView) {
            super(itemView);
            passengerEmail = itemView.findViewById(R.id.passengerEmail);
            accept = itemView.findViewById(R.id.buttonAccept);
            cancel = itemView.findViewById(R.id.buttonCancel);
        }
    }

}
