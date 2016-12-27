package pl.aptewicz.ftthcustomers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import pl.aptewicz.ftthcustomers.model.FtthCustomer;
import pl.aptewicz.ftthcustomers.util.ListViewUtils;

public class DashboardActivity extends AppCompatActivity {

	private FtthCustomer ftthCustomer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		initToolbar();
		setTitle(getString(R.string.issues_list_title));

		ftthCustomer = (FtthCustomer) getIntent().getSerializableExtra(FtthCustomer.FTTH_CUSTOMER);

		ListView issuesListView = (ListView) findViewById(R.id.issues_list);
		TextView emptyIssuesListTextView = (TextView) findViewById(R.id.empty_issues_list_view);
		emptyIssuesListTextView.setText("PUSTA");
		issuesListView.setEmptyView(emptyIssuesListTextView);
		issuesListView.setAdapter(new ArrayAdapter<>(this, ListViewUtils.getListItemLayoutId(),
				ftthCustomer.getFtthIssues().toArray()));
	}

	private void initToolbar() {
		setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.dashboard_options, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_dashboard_add_issue:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
