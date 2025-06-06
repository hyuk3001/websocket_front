package com.example.webfront.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.webfront.R;
import com.example.webfront.dto.UserLoginRequest;

public class LoginActivity extends AppCompatActivity {


    // UI 컴포넌트
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Animation buttonPressedAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // XML에서 정의된 뷰 연결
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        buttonPressedAnim = AnimationUtils.loadAnimation(this, R.anim.button_pressed_anim);

        loginButton.setOnClickListener(v -> {
            loginButton.startAnimation(buttonPressedAnim);
            String userId = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // UserLoginRequest 객체 생성
            UserLoginRequest loginRequest = UserLoginRequest.builder()
                    .userId(userId)
                    .password(password)
                    .build();

            // API 호출 (loginRequest 전달)
        });
    }
}