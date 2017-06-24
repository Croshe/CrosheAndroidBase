package com.croshe.android.base.extend.adapter;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CroshePageAdapter extends PagerAdapter {
	private List<? extends View> items=null;
	private List<String> titles=null;
	private int mChildCount = 0;
	
	
	public CroshePageAdapter(List<? extends View> items){
		this.items=items;
	}

	public CroshePageAdapter(List<? extends View> items, List<String> titles){
		this.items=items;
		this.titles=titles;
	}


	@Override
	public void notifyDataSetChanged() {         
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object)   {
		if ( mChildCount > 0) {
			mChildCount --;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}


	@Override
	public void destroyItem(ViewGroup container, int position,
                            Object object) {
		if(position<items.size()){
			container.removeView(items.get(position));
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		if(position<items.size()&&items.get(position)!=null){
			View v=items.get(position);
			container.addView(v);
			return v;
		}
		return null;
	}
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		if(titles!=null){
			return titles.get(position);
		}
		return null;
	}

}
