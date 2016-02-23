package com.transapps.callprotector.UI.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.transapps.callprotector.Adapters.SearchListAdapter;
import com.transapps.callprotector.Models.ModelNumberQuerry;
import com.transapps.callprotector.Models.Records;
import com.transapps.callprotector.R;
import com.transapps.callprotector.RestRequests.GetRequests;
import com.transapps.callprotector.RestRequests.RetrofitAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSearch extends Fragment {
    public boolean mHasError = false;
    private TextInputLayout textInputLayout;
    private EditText editText;
    private ImageView searchNumber;
    private ImageView recentNumber;
    private RecyclerView mRecyclerView;
    private SearchListAdapter mSearchAdapter;

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
        textInputLayout = (TextInputLayout) v.findViewById(R.id.text_input_layout);
        editText = (EditText) v.findViewById(R.id.edit_text);
        searchNumber = (ImageView) v.findViewById(R.id.searchNumber);
        recentNumber = (ImageView) v.findViewById(R.id.recentNumber);

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
                    hideKeyboard();
                    GetRequests service = RetrofitAdapter.createService(GetRequests.class, "admin", "1234");
                    service.getNumber(editText.getText().toString()).enqueue(new Callback<ModelNumberQuerry>() {
                        @Override
                        public void onResponse(Call<ModelNumberQuerry> call, Response<ModelNumberQuerry> response) {
                            Records record = getRecord(response);
                            if (record != null) {
                                mSearchAdapter.addItem(record);
                            }
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


        mRecyclerView = (RecyclerView) v.findViewById(R.id.resultsList);
        List<Records> data = new ArrayList<>();
        mSearchAdapter = new SearchListAdapter(getContext(), data);
        mRecyclerView.setAdapter(mSearchAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnItemTouchListener(new RecycleItemClickListener(getContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), "onClick " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "onLongClick " + position, Toast.LENGTH_SHORT).show();
            }
        }));

    }


    private Records getRecord(Response<ModelNumberQuerry> response) {
        Records myRecord = new Records();
        if (null == response.body()) {
            return null;
        }

        ModelNumberQuerry a = response.body();
        for (Records b : a.theRecords) {
            myRecord = b;
        }
        return myRecord;
    }


    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

    class RecycleItemClickListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecycleItemClickListener(Context context, RecyclerView view, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;/*super.onSingleTapUp(e);*/
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    /*super.onLongPress(e);*/
                    View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, mRecyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View children = rv.findChildViewUnder(e.getX(), e.getY());
            if (children != null && gestureDetector.onTouchEvent(e) && clickListener != null) {
                clickListener.onClick(children, mRecyclerView.getChildAdapterPosition(children));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    public interface OnError {
        void onError(boolean b);

        boolean shouldShowError();

        void showError();

        void hideError();
    }
}
