package com.asptt.plongee.resa.dao.jdbc;


import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.asptt.plongee.resa.exception.TechnicalException;


public abstract class AbstractJdbcDao {
	
	Logger log = Logger.getLogger(getClass().getName());

	private DataSource dataSource;
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void closeConnexion(Connection connexion) {
		try {
			if(null != connexion ){
				connexion.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(
					"Impossible de cloturer la connexion");
		}
		connexion = null;
	}
}
