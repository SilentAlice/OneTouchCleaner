package com.xjtu.onetouchcleaner.app;

import java.util.HashMap;
import java.util.List;

import com.xjtu.onetouchcleaner.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

public class SoftwareAdapter extends BaseAdapter {
/* Attributes *******************************************************************/
	private Context context;
	private List<ResolveInfo> resInfo;
	private ResolveInfo res;
	private LayoutInflater inflater = null;
	private PackageManager mPM;
	
	private HashMap<String, Boolean> isSelected;
	
/* Constructors **************************************************************/	
	public SoftwareAdapter(Context context, List<ResolveInfo> resInfo) {
		this.context = context;
		this.resInfo = resInfo;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPM = context.getPackageManager();
		
		isSelected = ((AppActivity)context).getIsSelected();
		init();
	}

/* Override Functions ***********************************************************/	
	@Override
	public int getCount() {
		return resInfo.size();
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
			convertView = inflater.inflate(R.layout.software_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		res = resInfo.get(position);
		holder.IVAppIcon.setImageDrawable(res.loadIcon(mPM));
		holder.TVAppLabel.setText(res.loadLabel(mPM).toString());
		
		// set checkbox in terms of isSelected
		holder.CBToClean.setChecked(getIsSelected().get(res.activityInfo.applicationInfo.dataDir));

		// CheckBox Click Event
		holder.CBToClean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				res = resInfo.get(position);
				if(((CheckBox)v).isChecked())
					getIsSelected().put(res.activityInfo.applicationInfo.dataDir, true);
				else
					getIsSelected().put(res.activityInfo.applicationInfo.dataDir, false);
			}
		});
		
		return convertView;
	}
	
/* Help Functions ***************************************************************/
	
	/* @Description
	 * Initialization function, used to do something after
	 * the reference has been created 
	 * @Location end of Constructor*/
	private void init() {
		
		// To set each data path with 'false'
		for(ResolveInfo listItem: resInfo) {
			if(!isSelected.containsKey(listItem.activityInfo.applicationInfo.dataDir)) { // A new app
				isSelected.put(listItem.activityInfo.applicationInfo.dataDir, false);
				AppStorage.getDataCleaned().put(listItem.activityInfo.applicationInfo.dataDir, false);
			}
		}
	}

/* Behaviors **********************************************************************/
	public HashMap<String, Boolean> getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(HashMap<String, Boolean> isSelected) {
		this.isSelected = isSelected;
	}
	
}

// Internal Class ViewHolder
class ViewHolder {
	ImageView IVAppIcon;
	TextView TVAppLabel;
	CheckBox CBToClean;
	
	public ViewHolder(View view) {
		this.IVAppIcon = (ImageView) view.findViewById(R.id.app_software_list_img);
		this.TVAppLabel = (TextView) view.findViewById(R.id.app_software_list_name);
		this.CBToClean = (CheckBox) view.findViewById(R.id.app_software_list_cb);
	}
}
