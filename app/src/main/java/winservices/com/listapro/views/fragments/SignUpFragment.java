package winservices.com.listapro.views.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.ShopKeeper;
import winservices.com.listapro.utils.SharedPrefManager;
import winservices.com.listapro.utils.UtilsFunctions;
import winservices.com.listapro.viewmodels.ShopKeeperVM;
import winservices.com.listapro.views.activities.LauncherActivity;

public class SignUpFragment extends Fragment {

    public final static String TAG = SignUpFragment.class.getSimpleName();
    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private String codeSent;
    private EditText editPhone, editVerifCode;
    private Button btnContinue, btnSignUp;
    private TextView txtDescription;
    private LinearLayout linlayPhoneCointaner;
    private ShopKeeperVM shopKeeperVM;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), "Phone Authentication failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        shopKeeperVM = ViewModelProviders.of(this).get(ShopKeeperVM.class);

        editPhone = view.findViewById(R.id.editPhone);
        btnContinue = view.findViewById(R.id.btnContinue);
        editVerifCode = view.findViewById(R.id.editVerifCode);
        btnSignUp = view.findViewById(R.id.btnNext);
        txtDescription = view.findViewById(R.id.txtDescription);
        linlayPhoneCointaner = view.findViewById(R.id.linLayPhoneContainer);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsFunctions.checkNetworkConnection(Objects.requireNonNull(getContext()))) {

                    final String phone = editPhone.getText().toString();
                    String completePhone = "+212" + phone;
                    if (UtilsFunctions.isEmulator()) {
                        completePhone = "+16505551111"; //whitelist number on Firebase, 123456 is the code to number
                    }

                    if (isPhoneValid(phone)) {
                        final String finalCompletePhone = completePhone;
                        shopKeeperVM.getShopKeeperByPhone(completePhone).observe(getViewLifecycleOwner(), new Observer<ShopKeeper>() {
                            @Override
                            public void onChanged(final ShopKeeper shopKeeper) {
                                if (shopKeeper == null) {
                                    sendVerifCode(finalCompletePhone);

                                    btnContinue.setVisibility(View.GONE);
                                    linlayPhoneCointaner.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    editVerifCode.setVisibility(View.VISIBLE);

                                    StringBuilder sb = new StringBuilder();
                                    sb.append(getString(R.string.sms_sent_to));
                                    sb.append(finalCompletePhone);
                                    sb.append(getString(R.string.with_code));

                                    txtDescription.setText(sb);
                                } else {
                                    shopKeeper.setIsLoggedIn(ShopKeeper.LOGGED_IN);
                                    shopKeeper.setLastLogged(ShopKeeper.LAST_LOGGED);
                                    shopKeeperVM.logIn(shopKeeper);
                                    LauncherActivity launcherActivity = (LauncherActivity) getActivity();
                                    Objects.requireNonNull(launcherActivity).displayFragment(new WelcomeFragment(), WelcomeFragment.TAG);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsFunctions.checkNetworkConnection(Objects.requireNonNull(getContext()))) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            SharedPrefManager.getInstance(getContext()).storeToken(newToken);
                            Log.d(TAG, "Token: " + newToken);
                        }
                    });
                    verifySignUpCode();
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verifySignUpCode() {
        String codeEntered = editVerifCode.getText().toString();
        if (UtilsFunctions.isEmulator()) {
            codeEntered = "123456"; // 123456 is the code to number
        }
        if (isCodeEnteredValid(codeEntered)) {
            if (codeSent != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);
                signInWithPhoneAuthCredential(credential);
            } else {
                editVerifCode.setError(getString(R.string.not_valid_code));
                editVerifCode.requestFocus();
            }
        }
    }

    private boolean isCodeEnteredValid(String codeEntered) {
        if (codeEntered.isEmpty()) {
            editVerifCode.setError(getString(R.string.code_required));
            editVerifCode.requestFocus();
            return false;
        }
        if (codeEntered.length() < 6) {
            editVerifCode.setError(getString(R.string.not_valid_code));
            editVerifCode.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isPhoneValid(String phone) {

        if (phone.isEmpty()) {
            editPhone.setError(getString(R.string.phone_required));
            editPhone.requestFocus();
            return false;
        }

        if (phone.length() < 9) {
            editPhone.setError(getString(R.string.not_valid_phone));
            editPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void sendVerifCode(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        Log.d(TAG, "request code verification for : " + phone);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();

                                    Log.d(TAG, "signIn success - phone number : " + user.getPhoneNumber());
                                    Log.d(TAG, "signIn success - display name : " + user.getDisplayName());

                                    registerShopKeeper();

                                } else {
                                    Log.d(TAG, "signIn : failure");
                                    editVerifCode.setError(getString(R.string.not_valid_code));
                                    editVerifCode.requestFocus();
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    }
                                }
                            }
                        }
                );
    }

    private void registerShopKeeper() {
        dialog = UtilsFunctions.getDialogBuilder(getLayoutInflater(), getContext(), getString(R.string.signing_up)).create();
        dialog.show();

        String phone = "+212" + editPhone.getText().toString();
        String uuid = UtilsFunctions.getUuid(getContext());
        String fcmToken = SharedPrefManager.getInstance(getContext()).getToken();
        final ShopKeeper shopKeeper = new ShopKeeper(phone, uuid, fcmToken);
        shopKeeperVM.signUp(shopKeeper, getContext());
        shopKeeperVM.getLastLoggedShopKeeper().observe(this, new Observer<ShopKeeper>() {
            @Override
            public void onChanged(ShopKeeper shopKeeper) {
                dialog.dismiss();
                if (shopKeeper == null) return;
                Toast.makeText(getContext(), R.string.welcome_to_listapro, Toast.LENGTH_SHORT).show();
                LauncherActivity launcherActivity = (LauncherActivity) getActivity();
                Objects.requireNonNull(launcherActivity).displayFragment(new AddShopFragment(), AddShopFragment.TAG);
            }
        });
    }

}
