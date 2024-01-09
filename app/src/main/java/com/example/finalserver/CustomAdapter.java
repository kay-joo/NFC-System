package com.example.finalserver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {
    //listview를 위한 커스텀 어댑터(학생 일반 유저만 볼 수 있는 리스트 어댑터)
    //관리자(교수)권한 일 때 모든 학생 출입 리스트를 보기 위해선 따로 어댑터 클래스와 파일을 만들도록 하세요.
    private Context context;
    private List list;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
    }

    class ViewHolder{
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_exists;
    }

    public CustomAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.user_list_item, parent, false);
        }

        viewHolder = new ViewHolder();
        viewHolder.tv_name = (TextView) convertView.findViewById(R.id.textPlaceName);
        viewHolder.tv_time = (TextView) convertView.findViewById(R.id.textTime);
        viewHolder.tv_exists = (TextView) convertView.findViewById(R.id.textExists);

        //final TagList taglist = (TagList) list.get(position);
        TagList tagList = (TagList) list.get(position);
        viewHolder.tv_name.setText(tagList.getPlaceName());
        viewHolder.tv_time.setText(tagList.getCurrent_time());
        viewHolder.tv_exists.setText(tagList.getExists());

        return convertView;
    }
}