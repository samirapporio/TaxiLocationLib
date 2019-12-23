package com.apporioinfolabs.taxilocationlib;



import android.os.Bundle;
import android.widget.TextView;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.db.OfflineLogModel;
import com.mindorks.placeholderview.PlaceHolderView;

import java.util.List;

public class SqllogsStashActivity extends BaseActivity implements HolderSqlLogs.OnClickistener {

    PlaceHolderView placeholder;
    TextView logs ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqllogs_stash);
        placeholder = findViewById(R.id.placeholder);
        logs = findViewById(R.id.logs);

        List<OfflineLogModel> sql_logs =  AtsApplication.getSqlLite().getAllLogsFromTable();

        for(int i =0 ; i < sql_logs.size() ; i++){
            placeholder.addView(new HolderSqlLogs(sql_logs.get(i), this));
        }
    }

    @Override
    public void onClick(int position) {
        logs.setText(""+AtsApplication.getSqlLite().getAllLogsFromTable().get(position).get_log());
    }
}
