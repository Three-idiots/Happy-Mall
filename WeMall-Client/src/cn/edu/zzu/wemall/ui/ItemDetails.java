package cn.edu.zzu.wemall.ui;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.database.SQLProcess;
import cn.edu.zzu.wemall.mylazylist.ImageLoader;
import cn.edu.zzu.wemall.object.GoodsItem;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;

/*
 * 
 * author liudewei
 * 商品详情
 * 
 */
public class ItemDetails extends Activity implements OnClickListener {
	private TextView name;
	private TextView intro;
	private TextView price,addtocart,tiemsummary,dgotocart;
	private EditText itemnum;
	private ImageView image, share;
	private ImageLoader imageLoader;
	private ViewGroup backbar;
	private Button itemcut, itemadd;
	// 以上为组件变量,以下为商品属性变量
	private boolean flag=false;//购物车刷新标志
	private boolean flag2=false;//去购物车标志
	private GoodsItem thisitem;
	private int num=0;
	private int goodid;
	private String goodname, goodimgurl;
	private Double goodprice;
	private SQLProcess wemalldb;
	// ///////////////////////////////////////////////////
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	// ///////////////////////////////////////////////////
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_good_details);
		// ///////////////////////////////////////////
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		// /////////////////////////////////////////////////
		// 获取该商品的详细数据
		this.thisitem = (GoodsItem) bundle.getSerializable("t");
		this.goodid = thisitem.getId();
		this.goodname = thisitem.getName();
		this.goodimgurl = thisitem.getImage();
		this.goodprice =thisitem.getPrice();
		this.imageLoader = new ImageLoader(this.getApplicationContext());
		this.name = (TextView) findViewById(R.id.detaliesname);
		this.price = (TextView) findViewById(R.id.detailsprice);
		this.image = (ImageView) findViewById(R.id.detailimage);
		this.name.setText(thisitem.getName());
		this.price.setText(thisitem.getPrice().toString());
		this.intro = (TextView) findViewById(R.id.intro);
		this.intro.setText(thisitem.getIntro());
		this.itemcut = (Button) findViewById(R.id.itemcut);
		this.itemcut.setOnClickListener(this);
		this.itemadd = (Button) findViewById(R.id.itemadd);
		this.itemadd.setOnClickListener(this);
		this.itemnum = (EditText) findViewById(R.id.itemnum);
		this.tiemsummary=(TextView) findViewById(R.id.tiemsummary);
		this.addtocart = (TextView) findViewById(R.id.addtocart);
		this.dgotocart=(TextView) findViewById(R.id.dgotocart);
		this.dgotocart.setOnClickListener(this);
		this.addtocart.setOnClickListener(this);
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_details);
		this.backbar.setOnClickListener(this);
		this.share = (ImageView) findViewById(R.id.title_right_button_details);
		this.share.setOnClickListener(this);
		this.imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE
				+ "Public/Uploads/"
				+ thisitem.getImage(), this.image);
		// //////////////////////初始化数据库和其他信息///////////////////////////////
		this.wemalldb = new SQLProcess(this);
		if(wemalldb.read_cart_item_num(goodid) !=0){
			this.num=wemalldb.read_cart_item_num(goodid);
		};
		this.itemnum.setText(String.valueOf(num));
		// ///////////////////////////////////////////////////////////////
		// 设置分享内容
		String sharecontent="我正在WeMall移动商城购买"+this.goodname+",物美价廉，大家快来下载使用吧";
		String shareurl="www.itlaborer.com";
		String sharetitle="WeMall移动商城";
		mController.setShareContent(sharecontent);
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN);
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "100424468",
				"c7394704798a158208a74ab60104f0ba");
		qqSsoHandler.addToSocialSDK();
		QQShareContent qqShareContent = new QQShareContent();
		// 设置分享文字
		qqShareContent.setShareContent(sharecontent);
		// 设置分享title
		qqShareContent.setTitle(sharetitle);
		// 设置点击分享内容的跳转链接
		qqShareContent.setTargetUrl(shareurl);
		mController.setShareMedia(qqShareContent);
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();
		QZoneShareContent qzone = new QZoneShareContent();
		// 设置分享文字
		qzone.setShareContent(sharecontent);
		// 设置点击消息的跳转URL
		qzone.setTargetUrl(shareurl);
		// 设置分享内容的标题
		qzone.setTitle(sharetitle);
		mController.setShareMedia(qzone);

		// /////////////////////////////////////////////////////////////
	}

	@Override
	public void onClick(View view) {
		DecimalFormat df=new DecimalFormat("0.#");
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.itemcut:
			//首先读取edittext里的数值
			num=Integer.parseInt(itemnum.getText().toString());
			if (num == 0) {
				
			} else if (num > 0) {
				itemnum.setText(String.valueOf(--num));
				tiemsummary.setText(df.format(num*goodprice));
			}
			break;
		case R.id.itemadd:
			//首先读取edittext里的数值
			itemnum.setText(String.valueOf(++num));
			tiemsummary.setText(df.format(num*goodprice));
			num=Integer.parseInt(itemnum.getText().toString());
			break;
		case R.id.addtocart:
			//首先读取edittext里的数值
			//购物车有变动,通知购物车刷新
			flag=true;
			num=Integer.parseInt(itemnum.getText().toString());
			if (num != 0) {
				Toast.makeText(this, num+"份"+goodname+"已加入购物车", Toast.LENGTH_SHORT)
				.show();
				wemalldb.insert_to_cart(goodid, goodname, goodimgurl, num,
						goodprice);
			}
			else{
				//移除此商品
				Toast.makeText(this,goodname+"已从购物车移除", Toast.LENGTH_SHORT).show();
				wemalldb.delete_cartitem(goodid);
			}
			break;
		case R.id.title_right_button_details:
			mController.openShare(ItemDetails.this, false);
			break;
		case R.id.title_left_layout_details:
			this.config_exit_this_activity();
			break;
		case R.id.dgotocart:
			flag2=true;
			this.config_exit_this_activity();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		this.config_exit_this_activity();
	}

	public void config_exit_this_activity() {
		
		Intent intent = new Intent(ItemDetails.this, MainUIMain.class);
		intent.putExtra("flag",flag);
		intent.putExtra("flag2",flag2);
		setResult(0x222, intent);
		this.finish();
		overridePendingTransition(R.anim.wemall_slide_in_left, R.anim.wemall_slide_out_right);
	}

	// U盟统计
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart("商品详情-" + this.name);

	}

	public void onPause() {
		super.onPause();
		wemalldb.close();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd("商品详情" + this.name);
	}

}
