package com.example.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Game extends AppCompatActivity {

    String[] moves = {"", "", "", "", "", "", "", "", ""};
    int[][] winning_positions = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };
    String current_player;
    int number_of_moves = 0;
    boolean winner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById (R.id.game_toolbar) ;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String player1 = intent.getStringExtra(StartGame.player1);
        String player2 = intent.getStringExtra(StartGame.player2);

        Random random = new Random();
        current_player = random.nextBoolean() ? player1 : player2;
        TextView game_state_view = findViewById(R.id.game_state);
        game_state_view.setText("Game State: " + current_player + "'s turn");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resource,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int itemId = item.getItemId();

        if (itemId != R.id.change_background_color_action) {
            return super.onOptionsItemSelected(item);
        }

        int initialColor = Color.WHITE;

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                showToast(color);
                View mainLayout = findViewById(R.id.main);
                mainLayout.setBackgroundColor(color);
            }

        });
        colorPickerDialog.show();


        return true;
    }

    private void showToast(int color) {
        String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
    }

    public void setXor0(View view) {
        Button clickedButton = (Button) view;
        int button_id = clickedButton.getId();
        String button_name = view.getResources().getResourceEntryName(button_id);
        String button_pressed = button_name.split("_")[1];
        int button_pressed_number = Integer.parseInt(button_pressed);

        Intent intent = getIntent();
        String player1 = intent.getStringExtra(StartGame.player1);
        String player2 = intent.getStringExtra(StartGame.player2);
        TextView game_state_view = findViewById(R.id.game_state);
        String game_state = game_state_view.getText().toString();

        if (!winner) {
            if (clickedButton.getText().toString().isEmpty()) {
                number_of_moves++;
                if (Objects.equals(current_player, player1)) {
                    clickedButton.setText("X");
                    moves[button_pressed_number] = "X";
                    current_player = player2;
                } else if (current_player.equals(player2)) {
                    clickedButton.setText("0");
                    moves[button_pressed_number] = "0";
                    current_player = player1;
                }
                checkGameState();
            } else {
                Toast.makeText(getApplicationContext(),
                        "That spot's already taken!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "The game has ended!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void checkGameState() {
        TextView game_state_view = findViewById(R.id.game_state);

        for (int i = 0; i < 8; i++) {
            winner = false;
            if (Objects.equals(moves[winning_positions[i][0]], moves[winning_positions[i][1]]) && Objects.equals(moves[winning_positions[i][0]], moves[winning_positions[i][2]]) && !Objects.equals(moves[winning_positions[i][0]], "")) {
                winner = true;
                if (Objects.equals(moves[winning_positions[i][0]], "X")) {
                    game_state_view.setText("Player 1 WON!");
                } else if (Objects.equals(moves[winning_positions[i][0]], "0")) {
                    game_state_view.setText("Player 2 WON!");
                }
                break;
            }
        }

        if (!winner) {
            if (number_of_moves == 9) {
                game_state_view.setText("It's a tie!");
                winner = true;
            } else {
                game_state_view.setText("Game State: " + current_player + "'s turn");
            }
        }
    }
}