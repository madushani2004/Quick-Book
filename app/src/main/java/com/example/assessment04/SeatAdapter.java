package com.example.assessment04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SeatAdapter extends BaseAdapter {
    private Context context;
    private List<Seat> seats;


    interface OnItemClickListener {
        void onItemClick(int seatNumber);
    }

    private OnItemClickListener listener = null;

    public SeatAdapter(Context context, List<Seat> seats) {
        this.context = context;
        this.seats = seats;
    }

    @Override
    public int getCount() {
        return seats.size();
    }

    @Override
    public Object getItem(int position) {
        return seats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.seat_item, parent, false);
        }

        Seat seat = seats.get(position);
        TextView seatNumber = convertView.findViewById(R.id.tvSeatNumber);

        seatNumber.setText(String.valueOf(seat.getSeatNumber()));
        if (!seat.isSeat()) {
            seatNumber.setText(seat.getCellNumber());
        } else if (seat.isSelected()) {
            seatNumber.setBackgroundColor(
                    context.getResources().getColor(R.color.seat_selected)
            );
        } else if (seat.isUserSeat()) {
            seatNumber.setBackgroundColor(context.getResources().getColor(R.color.seat_yours)); // User's seat
        } else if (seat.isBooked()) {
            seatNumber.setBackgroundColor(
                    context.getResources().getColor(R.color.seat_booked)
            ); // Booked seat
        } else  {
            seatNumber.setBackgroundColor(context.getResources().getColor(R.color.seat_available)); // Available seat
        }

        if (seat.isSeat()) {
            seatNumber.setOnClickListener(view -> listener.onItemClick(seat.getSeatNumber()));
        }

        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

