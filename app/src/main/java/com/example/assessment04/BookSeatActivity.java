package com.example.assessment04;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment04.data.CurrentUser;
import com.example.assessment04.routedata.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookSeatActivity extends AppCompatActivity {

    private static final String SENDER_EMAIL = "morristeshane@gmail.com";

    private static final String SENDER_PASSWORD = "ozkc yafe imga dejm";

    public static final String KEY_BUS_NO = "bus_no";
    public static final String KEY_BUS_NAME = "bus_name";
    public static final String KEY_BUS_DEPARTURE_TIME = "bus_departure";
    public static final String KEY_ROUTE_CODE = "bus_rote";
    public static final String KEY_USER_EMAIL = "use_email";

    private String busNo;
    private TextView busNoView;
    private TextView busNameView;
    private TextView busDepartureView;
    private TextView busRouteView;
    private Button buttonBook;

    private GridView seatView;
    private SeatAdapter adapter;

    private final int columnCount = 6;
    private final int rowCount = 9;
    private final int cellCount = columnCount * rowCount;

    private final DatabaseHelper dbHelper = new DatabaseHelper(this);

    private Seat selectedSeat = null;

    private final String buttonTextBook = "BOOK YOUR SEAT";
    private final String buttonTextCancel = "CANCEL THE SEAT";
    private final String buttonTextSwap = "SWAP YOUR SEAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_seat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        busNo = getIntent().getStringExtra(KEY_BUS_NO);
        String busName = getIntent().getStringExtra(KEY_BUS_NAME);
        String departure = getIntent().getStringExtra(KEY_BUS_DEPARTURE_TIME);
        Route route = Route.routeFrom(
                getIntent().getIntExtra(KEY_ROUTE_CODE, 0)
        );

        busNoView = findViewById(R.id.busNo);
        busNameView = findViewById(R.id.busName);
        busDepartureView = findViewById(R.id.departure);
        busRouteView = findViewById(R.id.route);
        buttonBook = findViewById(R.id.buttonBook);

        busNoView.setText("Bus No:" + busNo);
        busNameView.setText("Bus Name:" + busName);
        busRouteView.setText(route.toString());
        busDepartureView.setText("Out: " + departure);

        buttonBook.setEnabled(false);

        buttonBook.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Action")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String recipient = CurrentUser.email;
                            String subject = "Booking Confirmation";
                            String Body = "Your bus ticket booking is confirmed.\n Bus: " +busNo+ "\nSeat No: " +selectedSeat.getSeatNumber()+ "\n Thank you";
                            if (buttonBook.getText() == buttonTextBook) {
                                dbHelper.insertOrUpdateBooking(busNo, CurrentUser.email, selectedSeat.getSeatNumber());
                                new Thread(() -> {
                                    MailSender.sendEmail(recipient,subject,Body,SENDER_EMAIL,SENDER_PASSWORD);

                                }).start();
                                Toast.makeText(
                                        BookSeatActivity.this,
                                        "Seat No." + selectedSeat.getSeatNumber() + " has been booked :)",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else if (buttonBook.getText() == buttonTextCancel) {
                                dbHelper.requestCancelBooking(busNo, CurrentUser.email);
                                Toast.makeText(
                                        BookSeatActivity.this,
                                        "Seat No." + selectedSeat.getSeatNumber() + " cancel requested :)",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                boolean success = dbHelper.requestSwapSeats(busNo, CurrentUser.email, selectedSeat.getSeatNumber());

                                if (success) {
                                    Toast.makeText(
                                            BookSeatActivity.this,
                                            "Seat No." + selectedSeat.getSeatNumber() + " swap requested :)",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            BookSeatActivity.this,
                                            "Someone already requested a swap or error occurred!",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Code to execute when "No" is clicked
                            // For example, dismiss the dialog
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false); // Optional: Make the dialog non-cancelable

            // Show the dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        ArrayList<Seat> seats = new ArrayList<>(cellCount);

        List<Integer> bookedSeats = dbHelper.getBookedSeats(busNo);
        List<Integer> myBookedSeats = dbHelper.getPassengerBookedSeats(busNo, CurrentUser.email);
        List<Integer> othersBookedSeats = bookedSeats.stream().filter(item -> !myBookedSeats.contains(item)).collect(Collectors.toList());

        for (int i = 0; i < cellCount; i++) {
            int columnIdx = i % columnCount;
            int rowIdx = i / columnCount;

            if (columnIdx == 0 && rowIdx ==0) {
                seats.add(new Seat(null));
            } else if (columnIdx == 3) {
                seats.add(new Seat(null));
            } else if (columnIdx == 0) {
                seats.add(new Seat(String.valueOf(rowIdx)));
            } else if (rowIdx == 0) {
                int idx = (columnIdx < 3)? (columnIdx - 1): (columnIdx - 2);
                char ch = (char) ('A' + idx);
                seats.add(new Seat(ch + ""));
            } else {
                int c = (columnIdx < 3)? (columnIdx - 1): (columnIdx - 2);
                int r = rowIdx - 1;

                int idx = r * (columnCount - 2) + c;
                Seat seat = new Seat(idx, bookedSeats.contains(idx), myBookedSeats.contains(idx));
                seats.add(seat);
            }
        }

        seatView = findViewById(R.id.seatView);
        adapter = new SeatAdapter(this, seats);

        adapter.setOnItemClickListener(seatNumber -> {
            for (Seat s: seats) {
                if (!s.isSeat()) continue;

                if (s.getSeatNumber() == seatNumber) {
                    if (selectedSeat != null) {
                        selectedSeat.setSelected(false);
                        adapter.notifyDataSetChanged();
                    }

                    boolean unableToSwap = myBookedSeats.isEmpty() && othersBookedSeats.contains(s.getSeatNumber());

                    if (selectedSeat != s && !unableToSwap) {
                        selectedSeat = s;
                        s.setSelected(true);
                        adapter.notifyDataSetChanged();
                    } else {
                        selectedSeat = null;
                        s.setSelected(false);
                        buttonBook.setText(buttonTextBook);
                        buttonBook.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    }

                    if (selectedSeat != null) {
                        buttonBook.setEnabled(true);

                        if (myBookedSeats.contains(seatNumber)) {
                            buttonBook.setText(buttonTextCancel);
                        } else if (othersBookedSeats.contains(seatNumber)) {
                            buttonBook.setText(buttonTextSwap);
                        } else {
                            buttonBook.setText(buttonTextBook);
                        }
                    }
                    return;
                }
            }
        });

        seatView.setAdapter(adapter);
    }

}