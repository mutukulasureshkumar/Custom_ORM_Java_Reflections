package com.java.miniORM.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.java.miniORM.DBUtils.DBConnection;
import com.java.miniORM.Exceptions.CoreException;
import com.java.miniORM.VO.CoreVO;


/**
 * @author ${Suresh M Kumar}
 *
 * Aug 27, 2017
 */
public class CoreDAOImpl implements CoreDAO {
	
	private static final String UPDATE="UPDATE";
	private static final String WHEREFORUPDATE="WHEREUPDATE";
	private static final String WHEREFORSELECT="WHEREFORSELECT";
	private static final String ADD="ADD";
	private static Connection connection=null;
	
	@Override
	public ArrayList<CoreVO> get(CoreVO coreVO) {
		StringBuffer query=new StringBuffer();
		Statement statement=null;
		ResultSet resultSet=null;
		ArrayList<CoreVO> coreVOList= new ArrayList<CoreVO>();
		try{
			query.append("select "+getQueryFields(getFields(coreVO))+" from "+coreVO.tableName());
			String whereQuery=buildQuery(coreVO, WHEREFORSELECT);
			if(whereQuery!=null)
				query.append(" where "+whereQuery);
			System.out.println(query.toString().trim());
			connection=DBConnection.getConnection();
			statement=connection.createStatement();
			resultSet=statement.executeQuery(query.toString().trim());
			coreVOList=getObjFromRS(coreVO, resultSet);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return coreVOList;
	}
	
	public boolean add(CoreVO coreVO) throws CoreException {
		StringBuffer query = null;
		PreparedStatement preparedStatement=null;
		boolean result=false;
		try{
			query = new StringBuffer();
			String addValues=buildQuery(coreVO, ADD);
			if(addValues==null){
				throw new CoreException("No Values to Add");
			}
			query.append("insert into "+coreVO.tableName()+"("+getQueryFields(getFields(coreVO))+") values("+addValues+")");
			System.out.println(query.toString().trim());
			connection=DBConnection.getConnection();
			preparedStatement=connection.prepareStatement(query.toString().trim());
			if(preparedStatement.executeUpdate()>0)
				result=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(preparedStatement!=null)
					preparedStatement.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean update(CoreVO coreVO) throws CoreException {
		StringBuffer query = null;
		PreparedStatement preparedStatement=null;
		boolean result=false;
		try{
			query = new StringBuffer();
			String setterValues=buildQuery(coreVO, UPDATE);
			if(setterValues==null){
				throw new CoreException("No Values to Set for Update");
			}
			String whereValues=buildQuery(coreVO, WHEREFORUPDATE);
			if(whereValues==null){
				throw new CoreException("No Where Values to Update");
			}
			query.append("update "+coreVO.tableName()+" set "+setterValues+" where "+whereValues);
			System.out.println(query.toString().trim());
			connection=DBConnection.getConnection();
			preparedStatement=connection.prepareStatement(query.toString().trim());
			if(preparedStatement.executeUpdate()>0)
				result=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(preparedStatement!=null)
					preparedStatement.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public boolean delete(CoreVO coreVO) {
		StringBuffer query = null;
		PreparedStatement preparedStatement=null;
		boolean result=false;
		try{
			query = new StringBuffer();
			query.append("delete from "+coreVO.tableName()+" where "+buildQuery(coreVO, WHEREFORUPDATE));
			System.out.println(query.toString().trim());
			connection=DBConnection.getConnection();
			preparedStatement=connection.prepareStatement(query.toString().trim());
			if(preparedStatement.executeUpdate()>0)
				result=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(preparedStatement!=null)
					preparedStatement.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String getQueryParams(ArrayList<String> list){
		String result = "";
		for(int i=0; i<list.size(); i++){
			result += "?,";
		}
		return result.substring(0, (result.length()-1)).trim();
	}
	
	public String getQueryFields(ArrayList<String> list){
		String result = "";
		for(int i=0; i<list.size(); i++){
			result += list.get(i)+",";
		}
		return result.substring(0, (result.length()-1)).trim();
	}
		
	public Object getGetterValue(CoreVO coreVO, String fieldName) throws Exception{
		Object value= null;
		for (Method method : coreVO.getClass().getDeclaredMethods()) {
	        String name = method.getName();
	        if (name.equalsIgnoreCase("get"+fieldName)) {
	            value = "";
	            try {
	                value = method.invoke(coreVO);
	                break;
	            }
	            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	                e.printStackTrace();
	            }
	        }
	    }
		return value;
	}
	
	public void setSetterValue(CoreVO coreVO, String fieldName, Object fieldValue) throws Exception{
		for (Method method : coreVO.getClass().getDeclaredMethods()) {
	        String name = method.getName();
	        if (name.equalsIgnoreCase("set"+fieldName)) {
	            try {
	                method.invoke(fieldName, fieldValue);
	                break;
	            }
	            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	
	public ArrayList<String> getFields(CoreVO coreVO){
		ArrayList<String> fields = new ArrayList<String>();
		for(Field field: coreVO.getClass().getDeclaredFields()){
			fields.add(field.getName());
		}
		return fields;
	}
	
	@SuppressWarnings("static-access")
	public String buildQuery(CoreVO coreVO, String operation){
		String result=null;
		StringBuffer query = new StringBuffer();
		ArrayList<String> list=getFields(coreVO);
		if(!list.isEmpty()){
			for(int i=0; i<list.size(); i++){
				try {
					Object obj = getGetterValue(coreVO, list.get(i));
					if(operation.equals(ADD)){
						if(obj==null)
							query.append(obj+",");
						else{
							String fullType=obj.getClass().getName();
							String datatype=(String) fullType.subSequence((fullType.lastIndexOf(".")+1), fullType.length());
							if(datatype.equalsIgnoreCase("Integer") || datatype.equalsIgnoreCase("float") || datatype.equalsIgnoreCase("double")){
								query.append(obj+",");
							}else{
								query.append("'"+obj+"',");
							}
						}
					}
					if(obj != null && !operation.equals(ADD)){
						String fullType=obj.getClass().getName();
						String datatype=(String) fullType.subSequence((fullType.lastIndexOf(".")+1), fullType.length());
						if(datatype.equalsIgnoreCase("String") || datatype.equalsIgnoreCase("date") || datatype.equalsIgnoreCase("char")){
							if(operation.equals(WHEREFORSELECT))
								query.append(list.get(i)+"='"+obj+"' and ");
							else if(operation.equals(WHEREFORUPDATE) && coreVO.whereToUpdate.contains(list.get(i)))
								query.append(list.get(i)+"='"+obj+"' and ");
							else if(operation.equals(UPDATE))
								query.append(list.get(i)+"='"+obj+"',");
						}else if(datatype.equalsIgnoreCase("Integer") || datatype.equalsIgnoreCase("float") || datatype.equalsIgnoreCase("double")){
							int intVal; double doubleVal; float floatVal; long longVal;
								if(datatype.equalsIgnoreCase("Integer")){
									intVal=(int) obj;
									if(intVal >0 || coreVO.fieldsWithDefault.contains(list.get(i)) || coreVO.whereToUpdate.contains(list.get(i)))
										if(operation.equals(UPDATE))
											query.append(list.get(i)+"="+obj+",");
										else
											query.append(list.get(i)+"="+obj+" and ");
								}else if(datatype.equalsIgnoreCase("Double")){
									doubleVal=(double) obj;
									if(doubleVal >0 || coreVO.fieldsWithDefault.contains(list.get(i)) || coreVO.whereToUpdate.contains(list.get(i)))
										if(operation.equals(UPDATE))
											query.append(list.get(i)+"="+obj+",");
										else
											query.append(list.get(i)+"="+obj+" and ");
								}else if(datatype.equalsIgnoreCase("Float")){
									floatVal=(float) obj;
									if(floatVal >0 || coreVO.fieldsWithDefault.contains(list.get(i)) || coreVO.whereToUpdate.contains(list.get(i)))
										if(operation.equals(UPDATE))
											query.append(list.get(i)+"="+obj+",");
										else
											query.append(list.get(i)+"="+obj+" and ");
								}else if(datatype.equalsIgnoreCase("Long")){
									longVal=(long) obj;
									if(longVal >0 || coreVO.fieldsWithDefault.contains(list.get(i)) || coreVO.whereToUpdate.contains(list.get(i)))
										if(operation.equals(UPDATE))
											query.append(list.get(i)+"="+obj+",");
										else
											query.append(list.get(i)+"="+obj+" and ");
								}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(query.length()>0 && (operation.equals(WHEREFORSELECT) || operation.equals(WHEREFORUPDATE)))
				result=query.toString().substring(0,(query.length()-4)).trim();
			else if(query.length()>0 && (operation.equals(UPDATE) || operation.equals(ADD)))
				result=query.toString().substring(0,(query.length()-1)).trim();
		}
		return result;
	}
	
	public static boolean isNumber(Object object){
		boolean isNumber=true;
		String fullType=object.getClass().getName();
		String datatype=(String) fullType.subSequence((fullType.lastIndexOf(".")+1), fullType.length());
		if(datatype.equalsIgnoreCase("String") || datatype.equalsIgnoreCase("date") || datatype.equalsIgnoreCase("char")){
			isNumber=false;
		}
		return isNumber;
	}
	
	private ArrayList<CoreVO> getObjFromRS(CoreVO coreVO, ResultSet resultSet){
		ArrayList<CoreVO> resultList=new ArrayList<CoreVO>();
		if(resultSet!=null){
			try{
				while(resultSet.next()){
					for (Field field : coreVO.getClass().getDeclaredFields()){
						for (Method method : coreVO.getClass().getMethods()){
			                if ((method.getName().startsWith("set")) && (method.getName().length() == (field.getName().length() + 3))){
			                	if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
			                    {
			                        try
			                        {
			                            method.setAccessible(true);
			                            
			                            switch(field.getType().getSimpleName().toLowerCase()){
				                            case "integer":
				                            	method.invoke(coreVO,resultSet.getInt(field.getName().toLowerCase()));
				                            	break;
				                            case "long":
				                            	method.invoke(coreVO,resultSet.getLong(field.getName().toLowerCase()));
				                            	break;
				                            case "string":
				                            	method.invoke(coreVO,resultSet.getString(field.getName().toLowerCase()));
				                            	break;
				                            case "boolean":
				                            	method.invoke(coreVO,resultSet.getBoolean(field.getName().toLowerCase()));
				                            	break;
				                            case "timestamp":
				                            	method.invoke(coreVO,resultSet.getTimestamp(field.getName().toLowerCase()));
				                            	break;
				                            case "date":
				                            	method.invoke(coreVO,resultSet.getDate(field.getName().toLowerCase()));
				                            	break;
				                            case "double":
				                            	method.invoke(coreVO,resultSet.getDouble(field.getName().toLowerCase()));
				                            	break;
				                            case "float":
				                            	method.invoke(coreVO,resultSet.getFloat(field.getName().toLowerCase()));
				                            	break;
				                            case "time":
				                            	method.invoke(coreVO,resultSet.getTime(field.getName().toLowerCase()));
				                            	break;
				                            default:
				                            	method.invoke(coreVO,resultSet.getObject(field.getName().toLowerCase()));
			                            }
			                        }
			                        catch (Exception e)
			                        {
			                            e.printStackTrace();
			                        }
			                    }
			                }
			            }
					}
					resultList.add(coreVO);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return resultList;
	}
}
