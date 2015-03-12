package com.carandcoffee;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.LogUtil.log;
import com.carandcoffee.view.CircleImageView;
import com.carandcoffee.view.selec_dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	@SuppressLint("NewApi")
	public Bitmap targeticon;
	String iconurl;
	public TextView titleView;

	private Button button;
	boolean flaginput = false;
	boolean flagpop = false;
	selec_dialog pop;
	public static String custom_service_nickname;

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		View yourlayout = getLayoutInflater().inflate(layoutResID, null);
		RelativeLayout titlebar = (RelativeLayout) (getLayoutInflater()
				.inflate(R.layout.title_bar, null));
		titleView = (TextView) titlebar.findViewById(R.id.title_textview);
		button = (Button) titlebar.findViewById(R.id.titlebar_rightbutton);
		RelativeLayout layout = (RelativeLayout) titlebar
				.findViewById(R.id.yourlayout);
		layout.addView(yourlayout);
		setContentView(titlebar);
		RelativeLayout youlayout = (RelativeLayout) findViewById(R.id.yourlayout);
		youlayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				// TODO Auto-generated method stub

				if (bottom > oldBottom && oldBottom != 0)
					flaginput = false;
				else if (bottom < oldBottom && oldBottom != 0)
					flaginput = true;

			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from__left, R.anim.out_to_right);
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap Bytes2Bimap(byte[] b) {
		Bitmap temp = null;
		if (b.length != 0){
		temp = BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return temp;
	}

	public Bitmap geticon(String username) {
		Bitmap bitmap = null;
		File dir = getCacheDir();
		File icon = new File(dir, username + ".jpg");
		if (icon.exists()) {
			bitmap = BitmapFactory.decodeFile(icon.getPath());
		}
		return bitmap;
	}

	public boolean saveicon(String username, Bitmap map) {
		File dir = getCacheDir();
		File icon = new File(dir, username + ".jpg");
		if (icon.exists())
			icon.delete();
		try {
			FileOutputStream out = new FileOutputStream(icon);
			out.write(Bitmap2Bytes(map));
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String savefile(Bitmap map) {
		File dir = getCacheDir();
		File iamge = new File(dir, System.currentTimeMillis() + ".jpg");
		try {
			FileOutputStream out = new FileOutputStream(iamge);
			out.write(Bitmap2Bytes(map));
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iamge.getAbsolutePath();

	}

	public void downloadicon(final String username, final CircleImageView icon,final TextView title) {
		final AVUser user = AVUser.getCurrentUser();
		if (user != null && username.equals(user.getUsername())) {
			AVFile head_icon = user.getAVFile("avatar");
			if (head_icon == null) {
				targeticon = ((BitmapDrawable) getResources().getDrawable(
						R.drawable.head_icon)).getBitmap();
				if (icon != null)
					icon.setImageBitmap(targeticon);
			} else {
				head_icon.getDataInBackground(new GetDataCallback() {

					@Override
					public void done(byte[] arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if (arg1 == null&&arg0!=null) {
							targeticon = Bytes2Bimap(arg0);
							saveicon(user.getUsername(), targeticon);
							if (icon != null)
								icon.setImageBitmap(targeticon);
						} else {
							Toast.makeText(getApplicationContext(), "获取头像失败",
									Toast.LENGTH_SHORT).show();
							targeticon = ((BitmapDrawable) getResources()
									.getDrawable(R.drawable.head_icon))
									.getBitmap();
						}
					}
				});
			}
		} else  {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("peerId", username);

			AVCloud.callFunctionInBackground("findUserByPeerId", parameters,
					new FunctionCallback<Object>() {
						public void done(Object object, AVException e) {
							if (e == null) {
								@SuppressWarnings("unchecked")
								HashMap<String, String> map = (HashMap<String, String>) object;
								iconurl = map.get("avatarUrl");
								log.e("avatarUrl", map.toString());
								if(title!=null){
								custom_service_nickname=map.get("nickname");
								title.setText(custom_service_nickname);
								}
                               
								iconurl = iconurl.substring(0,
										iconurl.indexOf("?"));
								log.e("icon", iconurl);
								new downloadiconthread().start();
								
								

								log.e("晕代码代销", map.toString());
							} else {

							}
						}
					});

		}

	}

	class downloadiconthread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			if(iconurl!=null){
			File i = writeFromInput(getCacheDir().getAbsolutePath(),
					"service_icon.jpg", getInputStream(iconurl));
			targeticon = Bytes2Bimap(DensityUtil.getBytes(i.getAbsolutePath()));}
			else
				{
				      BitmapDrawable dra=(BitmapDrawable) getResources().getDrawable(R.id.head_icon);
				      targeticon=dra.getBitmap();
				}
			
		}

		public InputStream getInputStream(String urlStr) {
			InputStream is = null;
			try {
				URL url = new URL(urlStr);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setRequestProperty("Connection", "close");
				urlConn.connect();
				is = urlConn.getInputStream();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return is;
		}

		public File writeFromInput(String path, String fileName,
				InputStream input) {
			File file = null;
			OutputStream output = null;
			try {

				file = new File(path, fileName);
				output = new FileOutputStream(file);
				byte buffer[] = new byte[4 * 1024];
				// while ((input.read(buffer)) != -1) {
				// output.write(buffer);
				// }

				while (true) {
					int temp = input.read(buffer, 0, buffer.length);
					if (temp == -1) {
						break;
					}
					output.write(buffer, 0, temp);
				}

				output.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return file;
		}

	}

	public static int getAmrDuration(String path) {
		File file = new File(path);
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
				0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (int) (duration / 1000);
	}

	public void back(View v) {
		InputMethodManager mage = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (!flaginput) {
			onBackPressed();
		} else if (flaginput = true) {
			mage.hideSoftInputFromWindow(v.getWindowToken(), 0);
			flaginput = false;
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public static class DensityUtil {

		/**
		 * 根据图片字节数组，对图片可能进行二次采样，不致于加载过大图片出现内存溢出
		 * 
		 * @param bytes
		 * @return
		 */
		static int IMAGE_MAX_HIGHT;
		static int IMAGE_MAX_WIDTH;

		public static Bitmap getBitmapByBytes(byte[] bytes, Context context) { // 按一定必烈获取缩略图
			
			IMAGE_MAX_WIDTH =500;
			// 对于图片的二次采样,主要得到图片的宽与高
			double width = 0;
			double height = 0;
			double sampleSize = 1.0; // 默认缩放为1
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true; // 仅仅解码边缘区域
			// 如果指定了inJustDecodeBounds，decodeByteArray将返回为空
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			// 得到宽与高
			height = options.outHeight;
			width = options.outWidth;

			// 图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
			while ( (width / sampleSize > IMAGE_MAX_WIDTH)) {
				sampleSize = sampleSize * 2;
				height = height / sampleSize;
				width = width / sampleSize;
				Log.e("heght", sampleSize + "     " + height + "     " + width);
			}
			if(sampleSize!=1.0)
             sampleSize=sampleSize * 2;
			// 不再只加载图片实际边缘
			options.inJustDecodeBounds = false;
			// 并且制定缩放比例
			options.inSampleSize = (int) sampleSize;
			Bitmap a = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					options);
			log.e("现在的高宽", "" + a.getHeight() + "   " + a.getWidth() + "   "
					+ options.inSampleSize);
			return a;
		}

		/**
		 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
		 */
		public static int dip2px(Context context, float dpValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
		 */
		public static int px2dip(Context context, float pxValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}

		public static String parseSuffix(String url) {
			final Pattern pattern = Pattern.compile("\\S*[?]\\S*");
			Matcher matcher = pattern.matcher(url);

			String[] spUrl = url.toString().split("/");
			int len = spUrl.length;
			String endUrl = spUrl[len - 1];

			if (matcher.find()) {
				String[] spEndUrl = endUrl.split("\\?");
				return spEndUrl[0].split("\\.")[1];
			}
			return endUrl.split("\\.")[1];
		}

		public static byte[] getBytes(String filePath) {
			byte[] buffer = null;
			try {
				File file = new File(filePath);
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
				byte[] b = new byte[1000];
				int n;
				while ((n = fis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
				fis.close();
				bos.close();
				buffer = bos.toByteArray();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return buffer;
		}

	}

	public void setBarTitle(int rsdId) {
		titleView.setText(rsdId);
	}
	
	public void setBarTitle(String str) {
		titleView.setText(str);
		
	}
	public void setBarButton(int rsdId, OnClickListener clickListener) {
		button.setText(rsdId);
		button.setOnClickListener(clickListener);
		button.setVisibility(View.VISIBLE);
	}

}
