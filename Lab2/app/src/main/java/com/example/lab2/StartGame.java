package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class StartGame extends AppCompatActivity {
    public static final String player1 = "p1";
    public static final String player2 = "p2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void startGame(View view) {
        EditText p1 = findViewById(R.id.player1_name);
        EditText p2 = findViewById(R.id.player2_name);
        String player1_name = p1.getText().toString();
        String player2_name = p2.getText().toString();

        if ((!player1_name.isEmpty() && !player2_name.isEmpty()) || !(player1_name.equals(player2_name))) {
            Intent intent = new Intent(this, Game.class);
            intent.putExtra(player1, player1_name);
            intent.putExtra(player2, player2_name);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Please provide a different name for each player!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}