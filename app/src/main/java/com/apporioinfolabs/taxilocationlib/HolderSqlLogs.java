package com.apporioinfolabs.taxilocationlib;

import android.widget.TextView;

import com.apporioinfolabs.synchroniser.db.OfflineLogModel;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Position;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.holder_sql_stats_item)
public class HolderSqlLogs {

    private OfflineLogModel offlineLogModel ;
    private OnClickistener onClickistener ;
    @View(R.id.text) TextView text;

    @Position
    int mPosition ;


    public HolderSqlLogs(OfflineLogModel offlineLogModel, OnClickistener onClickistener) {
        this.offlineLogModel = offlineLogModel ;
        this.onClickistener = onClickistener;
    }


    @Resolve
    void setDataAccordingly(){
        text.setText(""+offlineLogModel.get_id()+"  -->  "+offlineLogModel.get_log());
    }


    @Click(R.id.text)
    void onRootClick(){
        onClickistener.onClick(mPosition);
    }


    public interface OnClickistener{
        void onClick(int position);
    }
}
