package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Game extends AppCompatActivity {

    String[][] board = {
            {"", "", ""},
            {"", "", ""},
            {"", "", ""}
    };

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

        Intent intent = getIntent();
        String player1 = intent.getStringExtra(StartGame.player1);
        String player2 = intent.getStringExtra(StartGame.player2);
        TextView game_state_view = findViewById(R.id.game_state);
        String game_state = game_state_view.getText().toString();

        if (clickedButton.getText().toString().isEmpty()) {
            if (game_state.contains(player1)) {
                clickedButton.setText("X");
                clickedButton.setEnabled(false);
                checkGameState(player1, clickedButton.getId());
                game_state_view.setText("Game State: " + player2 + "'s turn");
            } else if (game_state.contains(player2)) {
                clickedButton.setText("0");
                clickedButton.setEnabled(false);
                checkGameState(player2, clickedButton.getId());
                game_state_view.setText("Game State: " + player1 + "'s turn");
            }
        }
    }

    public void checkGameState(String player_name, int btnId) {
        TextView game_state_view = findViewById(R.id.game_state);
        Log.d("BUTTON ID", "Button ID: " + btnId);



    }
}