package com.java.miniORM.VO;

import java.util.ArrayList;

public interface CoreVO {
	public ArrayList<String> fieldsWithDefault=new ArrayList<String>();
	public ArrayList<String> whereToUpdate=new ArrayList<String>();
	public String tableName();
}
