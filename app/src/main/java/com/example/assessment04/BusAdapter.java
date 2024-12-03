package com.example.assessment04;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    public static int REQUEST_FOR_LIST_UPDATE = 564;

    private final List<Bus> busList;
    private final Context context;

    public interface OnDeleteListener {
        void onDelete();
    }

    private OnDeleteListener onDeleteListener = null;

    public BusAdapter(List<Bus> busList, Context context) {
        this.busList = busList;
        this.context = context;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = busList.get(position);
        holder.busName.setText(bus.getBusName());
        holder.busRoute.setText(bus.getRoute().toString());
        holder.driverName.setText(bus.getDriverName());
        holder.busNumber.setText(bus.getBusNo());

        // Edit Bus
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditBusActivity.class);
            intent.putExtra("busId", bus.getBusNo());
            intent.putExtra("pos", position);

            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_FOR_LIST_UPDATE);
            } else {
                context.startActivity(intent);
            }
        });

        // Delete Bus
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Bus")
                    .setMessage("Are you sure you want to delete this bus?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseHelper dbHelper = new DatabaseHelper(context);
                        int rowsDeleted = dbHelper.deleteBus(bus.getBusNo());
                        if (rowsDeleted > 0) {
                            busList.remove(position);
                            onDeleteListener.onDelete();
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Bus deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete bus", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {

        TextView busName, busRoute, driverName, busNumber;
        Button editButton, deleteButton;

        public BusViewHolder(View itemView) {
            super(itemView);
            busName = itemView.findViewById(R.id.bus_name);
            busRoute = itemView.findViewById(R.id.bus_route);
            driverName = itemView.findViewById(R.id.driver_name);
            busNumber = itemView.findViewById(R.id.busno);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}

