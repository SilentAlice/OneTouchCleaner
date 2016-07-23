package com.xjtu.onetouchcleaner;

public class Contacts {

	public String mName;
	public String mPhoneNum;
	
	public Contacts(String name, String phone) {
		mName = name;
		mPhoneNum = phone;
	}
	

	@Override
	public String toString(){
		String result = null;
		if(mName == null || mName.equals("")){
			result = mPhoneNum;
		}else{
			result = mName;
		}
		return result;
	}
	
	
	public String getName(){
		return mName;
	}
	
	public String getPhoneNum(){
		return mPhoneNum;
	}
	
	@Override
	public boolean equals(Object o){
		Contacts contacts = (Contacts)o;
		String otherName = contacts.getName();
		String otherphoneNum = contacts.getPhoneNum();
		if(mName != null && !mName.equals(otherName)){
			return false;
		}else if(mName == null && otherName != null){
			return false;
		}
		if(mPhoneNum != null && !mPhoneNum.equals(otherphoneNum)){
			return false;
		}else if(mPhoneNum == null && otherphoneNum != null){
			return false;
		}
		return true;
	}
}
