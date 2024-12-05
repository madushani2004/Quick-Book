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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment04.routedata.TurnManager;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    public static int REQUEST_FOR_LIST_UPDATE = 564;

    private final List<Bus> busList;
    private List<TurnManager.Turn> turnList;
    private final Context context;

    public interface OnDeleteListener {
        void onDelete();
    }

    private OnDeleteListener onDeleteListener = null;

    public BusAdapter(List<Bus> busList, List<TurnManager.Turn> turnList, Context context) {
        this.busList = busList;
        this.turnList = turnList;
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

        holder.turnTime.setVisibility(View.INVISIBLE);
        holder.busName.setText(bus.getBusName());
        holder.busRoute.setText(bus.getRoute().toString());
        holder.driverName.setText(bus.getDriverName());// Display turn time
        holder.busNumber.setText(bus.getBusNo());

        if (!turnList.isEmpty()) {
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, BookSeatActivity.class);
                intent.putExtra(BookSeatActivity.KEY_BUS_NO, bus.getBusNo());
                intent.putExtra(BookSeatActivity.KEY_BUS_NAME, bus.getBusName());
                intent.putExtra(BookSeatActivity.KEY_BUS_DEPARTURE_TIME, turnList.get(position).getDepartureTime().toString());
                intent.putExtra(BookSeatActivity.KEY_ROUTE_CODE, turnList.get(position).getRoute().getRouteCode());
                context.startActivity(intent);
            });

            holder.turnTime.setVisibility(View.VISIBLE);
            TurnManager.Turn turn = turnList.get(position);
            holder.turnTime.setText(String.format("Turn Time: %s", turn.getDepartureTime().toString()));
        }

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

        TextView busName, busRoute, driverName, busNumber, turnTime;
        Button editButton, deleteButton;
        CardView cardView;

        public BusViewHolder(View itemView) {
            super(itemView);
//            cardView = itemView.findViewById(R.id.itemCardView);
            busName = itemView.findViewById(R.id.bus_name);
            busRoute = itemView.findViewById(R.id.bus_route);
            driverName = itemView.findViewById(R.id.driver_name);
            busNumber = itemView.findViewById(R.id.busno);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            turnTime = itemView.findViewById(R.id.turn_time); // TextView for displaying turn time;
        }
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}

