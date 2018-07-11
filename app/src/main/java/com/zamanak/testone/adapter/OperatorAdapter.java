package com.zamanak.testone.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zamanak.testone.R;
import com.zamanak.testone.objects.OperatorsObj;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OperatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<OperatorsObj> mOperatorList;
    private OnItemClickListener mListener;
    private View view;

    public interface OnItemClickListener {
        void onClick(OperatorsObj obj);
    }

    public OperatorAdapter(ArrayList<OperatorsObj> operatorList,OnItemClickListener listener) {
        this.mOperatorList = operatorList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operator, parent, false);
        return new myViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OperatorsObj obj = mOperatorList.get(position);
        ((myViewHolder) holder).tvTopics.setText(obj.getmText());

        ((myViewHolder)holder).bind(obj,mListener);
    }

    @Override
    public int getItemCount() {
        return mOperatorList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_topics)
        AppCompatTextView tvTopics;

        public myViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(final OperatorsObj obj, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(obj);
                }
            });
        }
    }
}
