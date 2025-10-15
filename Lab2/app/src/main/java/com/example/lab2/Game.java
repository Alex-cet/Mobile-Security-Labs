package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    int number_of_moves = 0;
    boolean winner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String player1 = intent.getStringExtra(StartGame.player1);
        String player2 = intent.getStringExtra(StartGame.player2);

        Random random = new Random();
        String randomPlayer = random.nextBoolean() ? player1 : player2;
        TextView game_state_view = findViewById(R.id.game_state);
        game_state_view.setText("Game State: " + randomPlayer + "'s turn");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                if (game_state.contains(player1)) {
                    clickedButton.setText("X");
                    moves[button_pressed_number] = "X";
                } else if (game_state.contains(player2)) {
                    clickedButton.setText("0");
                    moves[button_pressed_number] = "0";
                }
                checkGameState(player1, player2, button_pressed_number);
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

    public void checkGameState(String player1_name, String player2_name, int button_pressed_number) {
        TextView game_state_view = findViewById(R.id.game_state);
        String game_state = game_state_view.getText().toString();

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
                if (game_state.contains(player1_name)) {
                    game_state_view.setText("Game State: " + player2_name + "'s turn");
                } else if (game_state.contains(player2_name)) {
                    game_state_view.setText("Game State: " + player1_name + "'s turn");
                }
            }
        }

    }
}