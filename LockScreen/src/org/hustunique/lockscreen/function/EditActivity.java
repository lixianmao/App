package org.hustunique.lockscreen.function;

import org.hustunique.lockscreen.R;
import org.hustunique.lockscreen.database.ContentHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity implements OnClickListener {

	private Button cancelButton, finishButton;
	private EditText bodyText, markText, meanText;
	private String path;
	private String action = "";
	private SQLiteDatabase database;
	private int _id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		initView();
		initData();
	}

	private void initView() {
		bodyText = (EditText) findViewById(R.id.et_body);
		markText = (EditText) findViewById(R.id.et_mark);
		meanText = (EditText) findViewById(R.id.et_mean);
		cancelButton = (Button) findViewById(R.id.edit_cancel);
		finishButton = (Button) findViewById(R.id.edit_finish);

	}

	private void initData() {
		Intent intent = getIntent();

		path = getIntent().getStringExtra("storePath");
		database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);

		action = intent.getAction();
		if (action.equals("update")) {
			_id = intent.getIntExtra("position", 1) + 1;

			Cursor cursor = database.rawQuery(
					"select * from content where ID = ?",
					new String[] { String.valueOf(_id) }); // 查询id时失败？

			if (cursor.moveToFirst())
				Log.e("ss", "move to first ok");

			String body = cursor.getString(1);
			String mark = cursor.getString(3);
			String mean = cursor.getString(2);
			cursor.close();
			
			bodyText.setText(body);
			markText.setText(mark);
			meanText.setText(mean);
		}

		finishButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_cancel:
			database.close();
			finish();
			break;
		case R.id.edit_finish:
			String body = bodyText.getText().toString();
			String mark = markText.getText().toString();
			String mean = meanText.getText().toString();
			if (TextUtils.isEmpty(body)) {
				Toast.makeText(getApplicationContext(), "empty body",
						Toast.LENGTH_SHORT).show();
			} else {
				ContentValues values = new ContentValues();
				values.put(ContentHelper.CONTENT_BODY, body);
				values.put(ContentHelper.CONTENT_MARK, mark);
				values.put(ContentHelper.CONTENT_MEAN, mean);

				Intent data = new Intent();
				data.putExtra("result body", body);

				if (action.equals("update")) {
					database.update(ContentHelper.CONTENT_TABLE, values,
							"ID=?", new String[] { String.valueOf(_id) });
					data.putExtra("result position", _id - 1);
					setResult(13, data);
				} else if (action.equals("insert")) {
					database.insert(ContentHelper.CONTENT_TABLE, null, values);
					setResult(14, data);
				}

				database.close();
				finish();
			}

			break;
		default:
			break;
		}
	}
}
