package org.hustunique.lockscreen.function;

import java.util.List;

import org.hustunique.lockscreen.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;

public class SwipeAdapter extends BaseAdapter {

	private Context context;
	private SwipeListView swipeListView;
	private List<String> list;

	public SwipeAdapter(Context context, SwipeListView swipeListView,
			List<String> list) {
		this.context = context;
		this.swipeListView = swipeListView;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null == list) {
			return 0;
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_store_list, null);
			viewHolder.nameView = (TextView) convertView
					.findViewById(R.id.item_store_name);
			viewHolder.numView = (TextView) convertView
					.findViewById(R.id.item_store_number);
			viewHolder.delete = (Button) convertView
					.findViewById(R.id.item_store_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				swipeListView.closeAnimate(position);
				swipeListView.dismiss(position);
			}
		});

		viewHolder.nameView.setText(list.get(position));
		viewHolder.numView.setText(list.size() + "词");

		return convertView;
	}

	class ViewHolder {
		TextView nameView;
		TextView numView;
		Button delete;
	}

}
