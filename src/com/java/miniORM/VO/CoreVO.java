package com.java.miniORM.VO;

import java.util.ArrayList;

/**
 * @author ${Suresh M Kumar}
 *
 * Aug 27, 2017
 */
public interface CoreVO {
	public ArrayList<String> fieldsWithDefault=new ArrayList<String>();
	public ArrayList<String> whereToUpdate=new ArrayList<String>();
	public String tableName();
}
