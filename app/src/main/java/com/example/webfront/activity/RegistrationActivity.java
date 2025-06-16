package com.example.webfront.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.webfront.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {




    // 로그 태그
    private static final String TAG = "RegistrationActivity";
    // Firebase Auth 객체
    private FirebaseAuth mAuth;
    // 인증 ID 및 재전송 토큰 저장 변수
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;

//    // 인증 콜백 정의
//    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
//            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                @Override
//                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
//                    // 인증 성공 - 자동으로 로그인 처리
//                    Log.d(TAG, "onVerificationCompleted:" + credential);
//                    signInWithPhoneAuthCredential(credential);
//                }
//
//                @Override
//                public void onVerificationFailed(@NonNull FirebaseException e) {
//                    // 인증 실패 처리
//                    Log.w(TAG, "onVerificationFailed", e);
//
//                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                        // 잘못된 요청
//                        Log.e(TAG, "Invalid request: " + e.getMessage());
//                    } else if (e instanceof FirebaseTooManyRequestsException) {
//                        // 인증 요청 제한 초과
//                        Log.e(TAG, "SMS quota exceeded: " + e.getMessage());
//                    } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
//                        // reCAPTCHA 확인 시 null Activity
//                        Log.e(TAG, "Missing Activity for reCAPTCHA");
//                    }
//
//                    // UI 업데이트 및 사용자 알림
//                }
//
//                @Override
//                public void onCodeSent(@NonNull String verificationId,
//                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                    // 인증 코드가 전송 완료됨
//                    Log.d(TAG, "onCodeSent:" + verificationId);
//
//                    // 인증 코드 및 재전송 토큰 저장
//                    mVerificationId = verificationId;
//                    mResendToken = token;
//
//                    // UI 업데이트 (사용자 입력 대기)
//                }
//            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);


//        FirebaseApp.initializeApp(this);
//        // Firebase Auth 초기화
//        mAuth = FirebaseAuth.getInstance();

        // EditText 연결
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // 버튼 및 입력 필드 연결
        findViewById(R.id.registerButton).setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            // 전화번호 입력 확인
            if (phoneNumber.isEmpty()) {
                phoneNumberEditText.setError("전화번호를 입력해주세요");
                return;
            }

            // 인증 여부 확인
            if (mVerificationId == null) {
                // 인증되지 않은 경우 메시지 출력
                phoneNumberEditText.setError("전화번호 인증이 필요합니다");
            }
//            else {
//                // 인증 완료 상태: 다음 단계로 진행
//                proceedWithRegistration();
//            }
        });

        findViewById(R.id.verifyButton).setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                phoneNumberEditText.setError("전화번호를 입력해주세요");
                return;
            } else if (!phoneNumber.matches("^[+]?[0-9]{10,13}$")) {
                phoneNumberEditText.setError("유효한 전화번호를 입력해주세요");
            }
            // 전화번호 인증 시작
//            startPhoneNumberVerification(phoneNumber);
        });

        // Phone Number TextWatcher 설정
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhoneNumber();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Email TextWatcher 설정
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Password TextWatcher 설정
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

//    // 전화번호 인증 시작
//    private void startPhoneNumberVerification(String phoneNumber) {
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(mAuth)
//                        .setPhoneNumber(phoneNumber)       // 인증할 전화번호
//                        .setTimeout(60L, TimeUnit.SECONDS) // 인증 타임아웃 (60초)
//                        .setActivity(this)                 // 현재 Activity
//                        .setCallbacks(mCallbacks)          // 인증 콜백
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//
//    // 인증된 PhoneAuthCredential로 Firebase 로그인
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        Log.d(TAG, "로그인 성공!");
//                        // 로그인 성공 처리 (Ex. 홈 화면 전환)
//                    } else {
//                        Log.e(TAG, "로그인 실패: " + task.getException());
//                    }
//                });
//    }


    // 이메일 유효성 검사 함수
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
        boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (isValid) {
            emailEditText.setBackgroundResource(R.drawable.background_radius);
        } else {
            emailEditText.setBackgroundResource(R.drawable.background_radius_red);
        }

        return isValid;
    }

    // 비밀번호 유효성 검사 함수
    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,16}$";
        boolean isValid = Pattern.matches(passwordPattern, password);

        if (isValid) {
            passwordEditText.setBackgroundResource(R.drawable.background_radius);
        } else {
            passwordEditText.setBackgroundResource(R.drawable.background_radius_red);
        }

        return isValid;
    }

    // 전화번호 유효성 검사 함수
    private boolean validatePhoneNumber() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String phonePattern = "^[0-9]{10,15}$"; // 허용: 10~15자리 숫자
        boolean isValid = Pattern.matches(phonePattern, phoneNumber);

        if (isValid) {
            phoneNumberEditText.setBackgroundResource(R.drawable.background_radius);
        } else {
            phoneNumberEditText.setBackgroundResource(R.drawable.background_radius_red);
        }

        return isValid;
    }
}