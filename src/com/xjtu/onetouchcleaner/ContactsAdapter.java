package com.xjtu.onetouchcleaner;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {
/* Attributes *******************************************************************/
	private LayoutInflater inflater = null;
	private List<String> list;
/* Constructors **************************************************************/	
	public ContactsAdapter(Context context, List<String> list) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
	}

/* Override Functions ***********************************************************/	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null || convertView.getTag() == null) {
			convertView = inflater.inflate(R.layout.contacts_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvName.setText(list.get(position));
		return convertView;
	}
}
	
// Internal Class ViewHolder
class ViewHolder {
	TextView tvName;
	public ViewHolder(View view) {
		this.tvName = (TextView) view.findViewById(R.id.contacts_name_tv);
	}
}
