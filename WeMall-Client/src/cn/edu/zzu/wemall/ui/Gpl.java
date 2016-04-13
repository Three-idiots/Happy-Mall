package cn.edu.zzu.wemall.ui;

import cn.edu.zzu.wemall.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;

public class Gpl extends Activity {
	private ViewGroup back;
	private WebView gpl;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_gpl);
		this.gpl = (WebView) findViewById(R.id.gpl);
		this.gpl.getSettings().setRenderPriority(RenderPriority.HIGH);
		//this.gpl.loadUrl("http://192.168.1.126/clause.html");
		this.gpl.loadUrl("file:///android_asset/clause.html");
		this.back = (ViewGroup) findViewById(R.id.title_left_layout_gpl);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
	}
}
