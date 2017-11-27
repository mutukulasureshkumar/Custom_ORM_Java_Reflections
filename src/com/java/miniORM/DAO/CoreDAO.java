package com.java.miniORM.DAO;

import java.util.ArrayList;

import com.java.miniORM.Exceptions.CoreException;
import com.java.miniORM.VO.CoreVO;

/**
 * @author ${Suresh M Kumar}
 *
 * Aug 27, 2017
 */
public interface CoreDAO {
	ArrayList<CoreVO> get(CoreVO coreVO);
	boolean add(CoreVO coreVO) throws CoreException;
	boolean update(CoreVO coreVO) throws CoreException;
	boolean delete(CoreVO coreVO);
}
