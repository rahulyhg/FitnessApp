/* 健康*/
package com.damy.jiankang;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.ResolutionSet;
import com.damy.Utils.WheelPicker.NumericWheelAdapter;
import com.damy.Utils.WheelPicker.OnWheelChangedListener;
import com.damy.Utils.WheelPicker.WheelView;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WaistlineActivity extends BaseActivity {

	public float					cur_waistline = 65;
	
	private final int 				START_INT = 5;
    private final int 				END_INT = 200;
    
    private WheelView 				wheel_int;
    private WheelView 				wheel_float;
	
	private TextView				txt_msg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waistline);
		
		initActivity(R.id.rl_waistline);
		
		initControls();
	}
	
	void initControls()
	{
		ImageButton btn_back = (ImageButton)findViewById(R.id.imgbtn_waistline_back);
		
		btn_back.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		
		Button btn_next = (Button)findViewById(R.id.btn_waistline_next);

		btn_next.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNext();
        	}
        });
		
		Button btn_save = (Button)findViewById(R.id.btn_waistline_save);
		
		btn_save.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		txt_msg = (TextView)findViewById(R.id.lbl_waistline_msg);
		
		if ( Global.registering_flag )
		{
			btn_next.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.INVISIBLE);
		}
		else
		{
			btn_next.setVisibility(View.INVISIBLE);
			btn_save.setVisibility(View.VISIBLE);			
		}
		
		cur_waistline = Global.Cur_UserWaistline;
		
		if ( Global.Cur_UserSex == 0 )
		{
			if ( cur_waistline < 90 )
				txt_msg.setText(getResources().getString(R.string.common_waistline_normalmsg));
			else
				txt_msg.setText(getResources().getString(R.string.common_waistline_unnormalmsg));
		}
		else
		{
			if ( cur_waistline < 80 )
				txt_msg.setText(getResources().getString(R.string.common_waistline_normalmsg));
			else
				txt_msg.setText(getResources().getString(R.string.common_waistline_unnormalmsg));
		}
		
		int fntSize = (int)(getResources().getDimension(R.dimen.wheelview_fnt_size) * ResolutionSet.fYpro + 0.50001);
		
		wheel_int = (WheelView) findViewById(R.id.wheel_waistline_int);
		wheel_int.setDefTextSize(fntSize);
		wheel_int.setAdapter(new NumericWheelAdapter(START_INT, END_INT));
		wheel_int.setLabel(" .");
		wheel_int.setCurrentItem((int)cur_waistline - START_INT);
		
		wheel_float = (WheelView) findViewById(R.id.wheel_waistline_float);
		wheel_float.setDefTextSize(fntSize);
		wheel_float.setAdapter(new NumericWheelAdapter(0, 9));
		wheel_float.setLabel("   " + getResources().getString(R.string.common_unit_centimeter));
		float tmp = (float)cur_waistline - (int)cur_waistline;
		wheel_float.setCurrentItem((int)(tmp * 10));
		
		wheel_int.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	int waistline = newValue + START_INT;
            	
            	if ( Global.Cur_UserSex == 0 )
        		{
        			if ( waistline < 90 )
        				txt_msg.setText(getResources().getString(R.string.common_waistline_normalmsg));
        			else
        				txt_msg.setText(getResources().getString(R.string.common_waistline_unnormalmsg));
        		}
        		else
        		{
        			if ( waistline < 80 )
        				txt_msg.setText(getResources().getString(R.string.common_waistline_normalmsg));
        			else
        				txt_msg.setText(getResources().getString(R.string.common_waistline_unnormalmsg));
        		}
            }
        });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }

	private void onClickBack()
	{
		if ( Global.registering_flag )
		{
			Intent weight_activity = new Intent(this, WeightActivity.class);
			startActivity(weight_activity);	
			finish();
		}
		else
		{
			Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
			startActivity(myinfo_activity);	
			finish();
		}
	}

	private void onClickNext()
	{
		getSelectedWaistline();
		
		Global.Cur_UserWaistline = cur_waistline;
		
		Intent healthinfo_activity = new Intent(this, HealthinfoActivity.class);
		startActivity(healthinfo_activity);	
		finish();
	}
	
	private void onClickSave()
	{
		getSelectedWaistline();
		
		new LoadResponseThread(WaistlineActivity.this).start();
	}
	
	private void getSelectedWaistline()
	{
		cur_waistline = (wheel_int.getCurrentItem() + START_INT) + ((float)wheel_float.getCurrentItem() / (float)10);
	}
	
	private void onSuccessWaistline()
	{
		Intent myinfo_activity = new Intent(this, MyInfoActivity.class);
		startActivity(myinfo_activity);	
		finish();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			Global.Cur_UserWaistline = cur_waistline;
			onSuccessWaistline();
		 }
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_EDITUSERINFO;

			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
			
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {            	
            	
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
    	
		requestObj.put("uid", Global.Cur_UserId);
		requestObj.put("type", "waist_now");
		requestObj.put("data", cur_waistline);
		
		return requestObj;
	}
}
