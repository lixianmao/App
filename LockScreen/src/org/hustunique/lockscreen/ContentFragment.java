package org.hustunique.lockscreen;

import org.hustunique.lockscreen.tools.LockContent;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContentFragment extends Fragment {

	private LockContent lockContent;
	private TextView bodyView, markView, meanView;
	private Paint paint;
	
	public ContentFragment(LockContent lockContent) { // 构造方法
		this.lockContent = lockContent;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_content, null);
		bodyView = (TextView) view.findViewById(R.id.content_body);
		markView = (TextView) view.findViewById(R.id.content_mark);
		meanView = (TextView) view.findViewById(R.id.content_mean);

		fitSize(lockContent.getBody());
		bodyView.setText(lockContent.getBody());
		markView.setText("[" + lockContent.getMark() + "]");
		meanView.setText(nextLine(lockContent.getMean()));
		
		return view;
	}

	private void fitSize(String text) {
		paint = new Paint();
		paint.setTextSize(100);
		
		float textSize = 100;
		int availableWidth = 300;
		while(paint.measureText(text)>availableWidth) {
			textSize -=2;
			paint.setTextSize(textSize);
		}
		bodyView.setTextSize(textSize);
		
	}
	
	/**
	 * 碰到关键字，自动换行
	 * 
	 * @param s
	 * @return
	 */
	private String nextLine(String s) {
		if (s.substring(1).contains("a.")) {
			s = s.replace("a.", "\na.");
		}
		if (s.substring(1).contains("v")) {
			s = s.replace("v", "\nv");
		}
		if (s.substring(1).contains("n")) {
			s = s.replace("n", "\nn");
		}
		if (s.substring(1).contains("ad.")) {
			s = s.replace("ad.", "\nad.");
		}
		return s;
	}
	
}
