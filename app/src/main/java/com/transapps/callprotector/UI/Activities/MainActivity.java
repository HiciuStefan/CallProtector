package com.transapps.callprotector.UI.Activities;

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

import com.transapps.callprotector.Models.ModelNumberQuerry;
import com.transapps.callprotector.R;
import com.transapps.callprotector.RestRequests.GetRequests;
import com.transapps.callprotector.RestRequests.RetrofitAdapter;
import com.transapps.callprotector.Services.IncomingCalls.BackgroundCallsService;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int MIN_TEXT_LENGTH = 4;
    private static final String EMPTY_ERROR_MESSAGE = "";

    public boolean mHasError;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private ImageView searchNumber;
    private ImageView recentNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
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
                    service.getNumber(editText.getText().toString()).enqueue(new Callback<ModelNumberQuerry>() {
                        @Override
                        public void onResponse(Call<ModelNumberQuerry> call, Response<ModelNumberQuerry> response) {

                        }

                        @Override
                        public void onFailure(Call<ModelNumberQuerry> call, Throwable t) {

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
        return textLength > MIN_TEXT_LENGTH;
    }

    private void showError() {
        textInputLayout.setError(getString(R.string.error));
    }

    private void hideError() {
        textInputLayout.setError(EMPTY_ERROR_MESSAGE);
    }

    public interface OnError {
        public void onError(boolean b);
    }

    private static final class ActionListener implements TextView.OnEditorActionListener {
        private static OnError mCallback;
        private final WeakReference<MainActivity> MainSearchActivityWeakReference;

        public static ActionListener newInstance(MainActivity MainActivity, MainActivity.OnError callback) {
            mCallback = callback;
            WeakReference<MainActivity> MainSearchActivityWeakReference = new WeakReference<>(MainActivity);
            return new ActionListener(MainSearchActivityWeakReference);
        }

        private ActionListener(WeakReference<MainActivity> MainSearchActivityWeakReference) {
            this.MainSearchActivityWeakReference = MainSearchActivityWeakReference;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            MainActivity MainActivity = MainSearchActivityWeakReference.get();
            if (MainActivity != null) {
                if (actionId == EditorInfo.IME_ACTION_GO && MainActivity.shouldShowError()) {
                    MainActivity.showError();
                    mCallback.onError(true);
                } else {
                    MainActivity.hideError();
                    mCallback.onError(false);
                }
            }
            return true;
        }
    }

}
