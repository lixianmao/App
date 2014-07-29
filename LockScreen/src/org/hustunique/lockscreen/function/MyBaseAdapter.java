package org.hustunique.lockscreen.function;

import java.util.List;

import org.hustunique.lockscreen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter{

	private Context context;
	private List<String> list;
	
	public MyBaseAdapter(Context context, List<String> list) {
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.out.println("getView" + position +" " + convertView);
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.item_store_data, null);
		TextView textView = (TextView) convertView.findViewById(R.id.item_store_data_tv);
		textView.setText(list.get(position));
		
		return convertView;
	}

}
