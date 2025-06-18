package com.example.webfront.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.webfront.R;
import com.example.webfront.Retrofit.RetrofitClient;
import com.example.webfront.Retrofit.RetrofitService;
import com.example.webfront.dto.SmsSendRequest;
import com.example.webfront.dto.SmsVerificationRequest;
import com.example.webfront.dto.UserOperationResponse;
import com.example.webfront.dto.UserRegistrationRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {


    private static final String TAG = "RegistrationActivity";

    private EditText userIdEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private EditText nickNameEditText;
    private EditText checkVerifyCodeEditText;

    private Button registerButton;
    private Button sendVerifyCodeButton;
    private Button verifyButton;
    private boolean isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // activity와 연결
        nickNameEditText = findViewById(R.id.nickNameEditText);
        userIdEditText = findViewById(R.id.userIdEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        checkVerifyCodeEditText = findViewById(R.id.checkVerifyCodeEditText);
        registerButton = findViewById(R.id.registerButton);
        sendVerifyCodeButton = findViewById(R.id.sendVerifyCodeButton);
        verifyButton = findViewById(R.id.verifyButton);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // 인증 코드 전송 버튼
        sendVerifyCodeButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (!validatePhoneNumber()) {
                return;
            }
            sendVerifyCode(phoneNumber);
        });

        // 인증 코드 확인 버튼
        verifyButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String code = checkVerifyCodeEditText.getText().toString().trim();
            if (!validatePhoneNumber()) {
                return;
            }
            if (code.isEmpty()) {
                checkVerifyCodeEditText.setError("인증 코드를 입력해주세요");
                return;
            }
            verifyCode(phoneNumber, code);
        });

        // 회원가입 버튼 동작
        registerButton.setOnClickListener(v -> handleRegistration());

        // 전화번호 EditText 포커스 변경 리스너 설정
        phoneNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 포커스를 잃으면 유효성 검사 실행
                validatePhoneNumber();
            }
        });

        // 이메일 EditText 포커스 변경 리스너 설정
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 포커스를 잃으면 유효성 검사 실행
                validateEmail();
            }
        });

        // 비밀번호 EditText 포커스 변경 리스너 설정
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 포커스를 잃으면 유효성 검사 실행
                validatePassword();
            }
        });

    }

    private void handleRegistration() {
        // 유효성 검사
        if (!validateEmail() || !validatePassword() || !validatePhoneNumber() || !validateName()) {
            return;
        }

        // UserRegistrationRequest 객체 생성
        String userId = userIdEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String name = nickNameEditText.getText().toString().trim();

        UserRegistrationRequest userRequest = UserRegistrationRequest.builder()
                .userId(userId)
                .email(email)
                .password(password)
                .phone(phoneNumber)
                .name(name)
                .build();

        // 서버로 데이터 전송
        sendUserRegistrationRequest(userRequest);
    }

    private void sendUserRegistrationRequest(UserRegistrationRequest request) {
        // Retrofit 인스턴스 가져오기
        RetrofitService retrofitService = RetrofitClient.getApiService();

        // 회원가입 API 호출
        retrofitService.signup(request).enqueue(new Callback<UserOperationResponse>() {
            @Override
            public void onResponse(Call<UserOperationResponse> call, Response<UserOperationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserOperationResponse result = response.body();

                    if (result.isResult()) {
                        // 회원가입 성공 처리
                        Toast.makeText(RegistrationActivity.this,  result.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // 회원가입 실패 처리
                        Toast.makeText(RegistrationActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 응답이 실패했거나 body가 null인 경우
                    Toast.makeText(RegistrationActivity.this, "회원가입 요청 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOperationResponse> call, Throwable t) {
                // 네트워크 등 호출 실패 처리
                Log.e(TAG, "회원가입 요청 실패", t);
                Toast.makeText(RegistrationActivity.this, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerifyCode(String phoneNumber) {
        SmsSendRequest smsSendRequest = new SmsSendRequest(phoneNumber);

        RetrofitService service = RetrofitClient.getApiService();
        service.sendSms(smsSendRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity.this, "인증 코드 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "SMS 전송 요청 실패", t);
                Toast.makeText(RegistrationActivity.this, "서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyCode(String phoneNumber, String code) {
        SmsVerificationRequest verificationRequest = new SmsVerificationRequest(phoneNumber, code);

        RetrofitService service = RetrofitClient.getApiService();
        service.verifySms(verificationRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isVerified = true;
                    Toast.makeText(RegistrationActivity.this, "휴대폰 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    isVerified = false;
                    Toast.makeText(RegistrationActivity.this, "인증에 실패했습니다. 코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "SMS 인증 요청 실패", t);
                Toast.makeText(RegistrationActivity.this, "서버와의 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 이메일 유효성 검사 함수
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
        boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (email.isEmpty()) {
            emailEditText.setError("이메일을 입력해주세요");
            return false;
        } else if(!isValid){
            emailEditText.setBackgroundResource(R.drawable.background_radius_red);
            emailEditText.setError("이메일을 확인해주세요.");
        } else{
            emailEditText.setBackgroundResource(R.drawable.background_radius);
            
        }

        return isValid;
    }

    // 비밀번호 유효성 검사 함수
    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,16}$";
        boolean isValid = Pattern.matches(passwordPattern, password);

        if (password.isEmpty()) {
            passwordEditText.setError("비밀번호를 입력해주세요");
            return false;
        } else if (!isValid){
            passwordEditText.setBackgroundResource(R.drawable.background_radius_red);
            passwordEditText.setError("비밀번호는 문자, 숫자, 특수문자를 포함한 8~16자로 입력하세요.");
        } else{
            passwordEditText.setBackgroundResource(R.drawable.background_radius);
            
        }

        return isValid;
    }

    // 전화번호 유효성 검사 함수
    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String phonePattern = "^[0-9]{10,15}$"; // 허용: 10~15자리 숫자
        boolean isValid = Pattern.matches(phonePattern, phoneNumber);

        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("전화번호를 입력해주세요");
            return false;
        } else if(!isValid){
            phoneNumberEditText.setBackgroundResource(R.drawable.background_radius_red);
            phoneNumberEditText.setError("유효한 전화번호를 입력하세요.");
        }
        else {
            phoneNumberEditText.setBackgroundResource(R.drawable.background_radius);
        }

        return isValid;
    }

    // 아이디 입력
    private boolean validateUserId() {
        String userid = userIdEditText.getText().toString().trim();

        if (userid.isEmpty()) {
            userIdEditText.setError("아이디를 입력해주세요");
            return false;
        } else {
            userIdEditText.setBackgroundResource(R.drawable.background_radius);
        }

        return true;
    }

    // 이름 유효성 검사 함수
    private boolean validateName() {
        String name = nickNameEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nickNameEditText.setError("닉네임을 입력하세요.");
        }
        return true;
    }
}