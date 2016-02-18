package com.transapps.callprotector.UI.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.transapps.callprotector.Models.ModelNumberQuerry;
import com.transapps.callprotector.R;
import com.transapps.callprotector.RestRequests.GetRequests;
import com.transapps.callprotector.RestRequests.RetrofitAdapter;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearch extends Fragment {
    public boolean mHasError;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private ImageView searchNumber;
    private ImageView recentNumber;

    private static final int MIN_TEXT_LENGTH = 4;
    private static final String EMPTY_ERROR_MESSAGE = "";
    OnError mCallback;

    public FragmentSearch() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_search, container, false);
        initUi(v);
        return v;
    }

    private void initUi(View v) {
        textInputLayout = (TextInputLayout) getActivity().findViewById(R.id.text_input_layout);
        editText = (EditText) getActivity().findViewById(R.id.edit_text);
        searchNumber = (ImageView) getActivity().findViewById(R.id.searchNumber);
        recentNumber = (ImageView) getActivity().findViewById(R.id.recentNumber);

        recentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastDialed = CallLog.Calls.getLastOutgoingCall(getActivity());
                editText.setText(lastDialed);
            }
        });
        searchNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHasError && mCallback.shouldShowError()) {
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
        mCallback = new FragmentSearch.OnError() {
            @Override
            public void onError(boolean b) {
                mHasError = b;
            }

            @Override
            public boolean shouldShowError() {
                int textLength = editText.getText().length();
                return textLength > MIN_TEXT_LENGTH;
            }

            @Override
            public void showError() {
                textInputLayout.setError(getString(R.string.error));
            }

            @Override
            public void hideError() {
                textInputLayout.setError(EMPTY_ERROR_MESSAGE);
            }
        };
        editText.setOnEditorActionListener(ActionListener.newInstance(getActivity(), mCallback));
    }


    public interface OnError {
        void onError(boolean b);
        boolean shouldShowError();
        void showError();
        void hideError();
    }

    private static final class ActionListener implements TextView.OnEditorActionListener {
        private static OnError mCallback;
        private final WeakReference<FragmentActivity> hostingActivityReference;

        public static ActionListener newInstance(FragmentActivity activity, FragmentSearch.OnError callback) {
            mCallback = callback;
            WeakReference<FragmentActivity> MainSearchActivityWeakReference = new WeakReference<>(activity);
            return new ActionListener(MainSearchActivityWeakReference);
        }

        private ActionListener(WeakReference<FragmentActivity> MainSearchActivityWeakReference) {
            this.hostingActivityReference = MainSearchActivityWeakReference;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Activity MainActivity = hostingActivityReference.get();
            if (MainActivity != null) {
                if (actionId == EditorInfo.IME_ACTION_GO && mCallback.shouldShowError()) {
                    mCallback.showError();
                    mCallback.onError(true);
                } else {
                    mCallback.hideError();
                    mCallback.onError(false);
                }
            }
            return true;
        }
    }
}
