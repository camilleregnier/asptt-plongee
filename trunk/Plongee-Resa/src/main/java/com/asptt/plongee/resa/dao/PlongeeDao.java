package com.asptt.plongee.resa.dao;


import java.util.List;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

public interface PlongeeDao extends GenericDao<Plongee, Integer>{

	public List<Plongee> findAllOuvertes() throws TechnicalException;
	
}
