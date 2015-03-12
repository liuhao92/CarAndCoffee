package com.carandcoffee.custom;

import com.carandcoffee.R;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Myanimation {
	private ImageView leftbuttom;
	private ImageView rightbuttom;
	private ImageView centerbuttom;
	private ImageView bowen;
	private ImageView bg;
	Animation left_button_to_center;
	Animation right_buttom_to_center;
	Animation suoxiao;
	Animation bowen_animation;
	Animation shansuo;
	Animation shansuo1;
	boolean flag = true;
	private Animation center_to_left;
	private Animation center_to_right;
	private Animation huanyuan;
	private Animation	xiaoyuandian;
	ImageView xiaoyuandian_view;
	public Myanimation(ImageView leftbuttom, ImageView rightbuttom,
			ImageView centerbuttom, ImageView bowen, ImageView bg) {
		this.leftbuttom = leftbuttom;
		this.rightbuttom = rightbuttom;
		this.bowen = bowen;
		this.centerbuttom = centerbuttom;
		this.bg = bg;
		
		left_button_to_center = AnimationUtils.loadAnimation(
				leftbuttom.getContext(), R.anim.left_button_to_center);
		right_buttom_to_center = AnimationUtils.loadAnimation(
				leftbuttom.getContext(), R.anim.right_buttom_to_center);
		suoxiao = AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.suoxiao);
		bowen_animation = AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.bowen);
		shansuo = AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.shansuo);
		shansuo1 = AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.shanshuo1);
		center_to_left=AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.center_to_left);
		center_to_right=AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.center_to_right);
		
		huanyuan=AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.huanyuan);
		this.xiaoyuandian=AnimationUtils.loadAnimation(leftbuttom.getContext(),
				R.anim.xiaoyuandian);
		
	}

	public void thefirststep() {
		flag = true;
		left_button_to_center.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				centerbuttom.startAnimation(suoxiao);
				leftbuttom.setVisibility(View.GONE);
				rightbuttom.setVisibility(View.GONE);

			}
		});
		suoxiao.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				bowen.startAnimation(bowen_animation);
				centerbuttom.startAnimation(shansuo);
				bg.startAnimation(shansuo1);
				

			}
		});
		bowen_animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (flag) {
					bowen.startAnimation(bowen_animation);
					centerbuttom.startAnimation(shansuo);
					bg.startAnimation(shansuo1);
				}
			}
		});
		leftbuttom.startAnimation(left_button_to_center);
		rightbuttom.startAnimation(right_buttom_to_center);
	}

	public void thesecondtstep() {
		flag = false;
		center_to_left.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				leftbuttom.setVisibility(View.VISIBLE);
				
			}
		});
		center_to_right.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				rightbuttom.setVisibility(View.VISIBLE);
				
			}
		});
		leftbuttom.startAnimation(center_to_left);
		rightbuttom.startAnimation(center_to_right);
		centerbuttom.startAnimation(huanyuan);
		

	}
}
