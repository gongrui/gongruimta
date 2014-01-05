package com.gongrui.mta.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gongrui.mta.R;
import com.gongrui.mta.common.DateUtil;
import com.gongrui.mta.entity.Temperature;

public class ListViewAdapter extends BaseAdapter {
	public List<Temperature> items;
	private LayoutInflater inflater;

	
	private float x, ux;

	private Context mContext;

	public ListViewAdapter(Context context, List<Temperature> items) {
		this.items = items;
		this.mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.history_list, null);
			viewHolder.tvContent = (TextView) view.findViewById(R.id.tvcontent);
			viewHolder.tvCwsj = (TextView) view.findViewById(R.id.tvcwsj);
			viewHolder.tvCwwd = (TextView) view.findViewById(R.id.tvcwwd);
			viewHolder.tvWZ = (TextView) view.findViewById(R.id.tvwz);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		Temperature item = items.get(position);

//		// 为每一个view项设置触控监听
//		view.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//
//				final ViewHolder holder = (ViewHolder) v.getTag();
//
//				// 当按下时处理
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//					// 设置背景为选中状态
//					v.setBackgroundResource(R.drawable.mm_listitem_pressed);
//					// 获取按下时的x轴坐标
//					x = event.getX();
//					// 判断之前是否出现了删除按钮如果存在就隐藏
//					if (curDel_btn != null) {
//						curDel_btn.setVisibility(View.GONE);
//					}
//
//				} else if (event.getAction() == MotionEvent.ACTION_UP) {// 松开处理
//
//					// 设置背景为未选中正常状态
//					v.setBackgroundResource(R.drawable.mm_listitem_simple);
//					// 获取松开时的x坐标
//					ux = event.getX();
//
//					// 判断当前项中按钮控件不为空时
//					if (holder.btnDel != null) {
//
//						// 按下和松开绝对值差当大于20时显示删除按钮，否则不显示
//
//						if (Math.abs(x - ux) > 20) {
//							holder.btnDel.setVisibility(View.VISIBLE);
//							curDel_btn = holder.btnDel;
//						}
//					}
//				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 当滑动时背景为选中状态
//
//					v.setBackgroundResource(R.drawable.mm_listitem_pressed);
//
//				} else {// 其他模式
//					// 设置背景为未选中正常状态
//					v.setBackgroundResource(R.drawable.mm_listitem_simple);
//				}
//
//				return true;
//			}
//		});

		viewHolder.tvContent.setText(String.format("%s-[%s]", item.getGc(),item.getBd(),item.getCw()));

		viewHolder.tvCwwd.setText(String.format("(%s %s %s)", item.getWd1(), item.getWd2(), item.getWd3()));
		
		viewHolder.tvCwsj.setText(DateUtil.longtime2str(item.getCwsj(), "MM-dd HH:mm"));

		viewHolder.tvWZ.setText(item.getWz());

//		// 为删除按钮添加监听事件，实现点击删除按钮时删除该项
//		viewHolder.btnDel.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				if (curDel_btn != null)
//					curDel_btn.setVisibility(View.GONE);
//				items.remove(position);
//				notifyDataSetChanged();
//			}
//		});

		return view;
	}

	/**
	 * 
	 * 
	 * @param item
	 */
	public void addItem(Temperature item) {
		items.add(item);
	}

	public void addAllItem(List<Temperature> items) {

		for (Temperature t : items) {
			addItem(t);
		}
	}

	final static class ViewHolder {
		TextView tvContent;
		TextView tvWZ;
		TextView tvCwsj;
		TextView tvCwwd;
	}
}
