package com.transapps.callprotector;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.transapps.callprotector.IncomingCalls.BackgroundCallsService;
import com.transapps.callprotector.Requests.GetRequests;
import com.transapps.callprotector.Requests.RetrofitAdapter;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSearchActivity extends AppCompatActivity {
    private static final int MIN_TEXT_LENGTH = 4;
    private static final String EMPTY_STRING = "";

    public boolean mHasError;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private ImageView searchNumber;
    private ImageView recentNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        initUi();

        Intent intent = new Intent(this, BackgroundCallsService.class);
        startService(intent);
    }

    private void initUi() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_main_search);


        textInputLayout = (TextInputLayout) findViewById(R.id.text_input_layout);
        editText = (EditText) findViewById(R.id.edit_text);
        searchNumber = (ImageView) findViewById(R.id.searchNumber);
        recentNumber = (ImageView) findViewById(R.id.recentNumber);

        recentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastDialed = CallLog.Calls.getLastOutgoingCall(getApplicationContext());
                editText.setText(lastDialed);
            }
        });
        searchNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasError && shouldShowError()) {
                    GetRequests service = RetrofitAdapter.createService(GetRequests.class, "admin", "1234");
                    service.getNumber(editText.getText().toString()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                }
            }
        });

        textInputLayout.setHint(getString(R.string.hint));
        editText.setOnEditorActionListener(ActionListener.newInstance(this, new OnError() {
            @Override
            public void onError(boolean b) {
                mHasError = b;
            }
        }));
    }


    private boolean shouldShowError() {
        int textLength = editText.getText().length();
        return textLength > 0 && textLength < MIN_TEXT_LENGTH;
    }

    private void showError() {
        textInputLayout.setError(getString(R.string.error));
    }

    private void hideError() {
        textInputLayout.setError(EMPTY_STRING);
    }

    public interface OnError {
        public void onError(boolean b);
    }

    private static final class ActionListener implements TextView.OnEditorActionListener {
        private static OnError mCallback;
        private final WeakReference<MainSearchActivity> MainSearchActivityWeakReference;

        public static ActionListener newInstance(MainSearchActivity MainSearchActivity, MainSearchActivity.OnError callback) {
            mCallback = callback;
            WeakReference<MainSearchActivity> MainSearchActivityWeakReference = new WeakReference<>(MainSearchActivity);
            return new ActionListener(MainSearchActivityWeakReference);
        }

        private ActionListener(WeakReference<MainSearchActivity> MainSearchActivityWeakReference) {
            this.MainSearchActivityWeakReference = MainSearchActivityWeakReference;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            MainSearchActivity MainSearchActivity = MainSearchActivityWeakReference.get();
            if (MainSearchActivity != null) {
                if (actionId == EditorInfo.IME_ACTION_GO && MainSearchActivity.shouldShowError()) {
                    MainSearchActivity.showError();
                    mCallback.onError(true);
                } else {
                    MainSearchActivity.hideError();
                    mCallback.onError(false);
                }
            }
            return true;
        }
    }

}
