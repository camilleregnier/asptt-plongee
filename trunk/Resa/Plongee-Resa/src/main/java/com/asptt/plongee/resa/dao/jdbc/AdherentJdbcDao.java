package com.asptt.plongee.resa.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;

public class AdherentJdbcDao extends AbstractJdbcDao implements AdherentDao {

	public Adherent create(Adherent obj) throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().prepareStatement("insert into ...");
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent n'a pu être enregistré"); 
			}
			return obj;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		}
	}

	public void delete(Adherent obj) throws TechnicalException {
		// TODO Auto-generated method stub

	}

	public List<Adherent> findAll() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().prepareStatement("select * from ADHERENTS");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		}
	}

	public Adherent findById(String id) throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().prepareStatement("select * from ADHERENT where LICENSE = ?");
			st.setString(1, id);
			ResultSet rs = st.executeQuery();
			Adherent adherent = null;
			if (rs.next()) {
				adherent = wrapAdherent(rs);
			}
			return adherent;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		}
	}

	public Adherent update(Adherent obj) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	private Adherent wrapAdherent(ResultSet rs) throws SQLException {
		String licence = rs.getString("LICENSE");
		String nom = rs.getString("NOM");
		String prenom = rs.getString("PRENOM");
		String niveau = rs.getString("NIVEAU");
		String telephone = rs.getString("TELEPHONE");
		String mail = rs.getString("MAIL");
		String encadrant = rs.getString("ENCADRANT");
		String droit = rs.getString("DROITS");
		int pilote = rs.getInt("PILOTE");
		Adherent adherent = new Adherent();
//		adherent.setId(licence);
//		adherent.setNom(nom);
		adherent.setPrenom(prenom);
//		adherent.setNiveau(niveau);
//		adherent.setTelephone(telephone);
//		adherent.setMail(mail);
//		adherent.setEncadrant(encadrant);
//		adherent.setDroit(droit);
//		if(pilote == 1){
//			adherent.setPilote(true);
//		} else {
//			adherent.setPilote(false);
//		}
		return adherent;
	}
}
