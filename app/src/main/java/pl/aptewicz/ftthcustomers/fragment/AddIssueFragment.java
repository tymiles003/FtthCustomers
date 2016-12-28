package pl.aptewicz.ftthcustomers.fragment;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import pl.aptewicz.ftthcustomers.R;
import pl.aptewicz.ftthcustomers.model.FtthCustomer;
import pl.aptewicz.ftthcustomers.model.FtthIssue;
import pl.aptewicz.ftthcustomers.util.ProgressUtils;


public class AddIssueFragment extends Fragment {

    private FtthCustomer ftthCustomer;

    private EditText issueDescription;

    private View addIssueForm;

    private View progressView;

    private OnIssueAddedListener onIssueAddedListener;

    public interface OnIssueAddedListener {
        void onIssueAdded(FtthCustomer ftthCustomer);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onIssueAddedListener = (OnIssueAddedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_issue_fragment, container, false);

        ftthCustomer = (FtthCustomer) getArguments().getSerializable(FtthCustomer.FTTH_CUSTOMER);

        Button addIssueButton = (Button) rootView.findViewById(R.id.add_issue_button);
        addIssueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIssue();
            }
        });

        issueDescription = (EditText) rootView.findViewById(R.id.issue_description);
        addIssueForm = rootView.findViewById(R.id.add_issue_form);
        progressView = rootView.findViewById(R.id.add_issue_progress);

        return rootView;
    }

    public void addIssue() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        ProgressUtils.showProgress(true, getContext(), addIssueForm, progressView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FtthIssue ftthIssue = new FtthIssue();
                ftthIssue.setDescription(issueDescription.getText().toString());
                ftthCustomer.getFtthIssues().add(ftthIssue);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                end();
            }
        }).start();
    }

    public void end() {
        onIssueAddedListener.onIssueAdded(ftthCustomer);
    }
}
