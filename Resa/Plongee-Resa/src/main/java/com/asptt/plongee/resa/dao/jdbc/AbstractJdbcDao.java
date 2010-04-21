package com.asptt.plongee.resa.dao.jdbc;


import javax.sql.DataSource;


public abstract class AbstractJdbcDao {

	private DataSource dataSource;

	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
