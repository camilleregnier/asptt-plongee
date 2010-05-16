package com.asptt.plongee.resa.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Adherent.Encadrement;

public class AdherentJdbcDao extends AbstractJdbcDao implements AdherentDao {

	private PlongeeDao plongeeDao;
	
    public void setPlongeeDao(PlongeeDao plongeeDao) {   // setter appelé par Spring pour injecter le bean "plongeeDao"
        this.plongeeDao = plongeeDao;
    }

	public Adherent create(Adherent adh) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO ADHERENT (`LICENSE`, `NOM`, `PRENOM`, `NIVEAU`, `TELEPHONE`, `MAIL`, `ENCADRANT`, `PILOTE`)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			st.setString(2, adh.getNom());
			st.setString(3, adh.getPrenom());
//			st.setString(4, adh.getNiveau().toString());
			st.setString(4, adh.getNiveau());
			st.setString(5, adh.getTelephone());
			st.setString(6, adh.getMail());
			if(null == adh.getEncadrement()){
				st.setString(7, null);
			} else {
				st.setString(7, adh.getEncadrement());
			}
			if(adh.isPilote()){
				st.setInt(8, 1);
			}else{
				st.setInt(8, 0);
			}
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent n'a pu être enregistré"); 
			}
			return adh;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public void delete(Adherent adh) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM ADHERENT WHERE");
			sb.append(" LICENSE = ?");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent n'a pu être supprimé"); 
			}
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent update(Adherent obj) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Adherent> findAll() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection()
				.prepareStatement("select * from ADHERENT");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		}finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent findById(String id) throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().
					prepareStatement("select * from ADHERENT where LICENSE = ?");
			st.setString(1, id);
			ResultSet rs = st.executeQuery();
			Adherent adherent = null;
			if (rs.next()) {
				adherent = wrapAdherent(rs);
			}
			return adherent;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}


	
	
	public List<String> getStrRoles(Adherent adherent) throws TechnicalException {
		PreparedStatement st;
		try {

			StringBuffer sb = new StringBuffer("SELECT r.LIBELLE FROM rel_adherent_roles rel, roles r ");
			sb.append(" where rel.ROLES_idROLES = r.idROLES ");
			sb.append(" and rel.ADHERENT_LICENSE = ?");
			st = getDataSource().getConnection().
			prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			ResultSet rs = st.executeQuery();
			List<String> result = new ArrayList<String>();
			while(rs.next()){
				result.add(rs.getString("LIBELLE"));
			}
			return result;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsInscrits(Plongee  plongee)
	throws TechnicalException {
		PreparedStatement st;
		try {
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE ");
			sb.append(" from plongee p, inscription_plongee i, adherent a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE ");
			st = getDataSource().getConnection().
			prepareStatement(sb.toString());
			st.setInt(1, plongee.getId());
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			//AdherentServiceImpl daoAdh =AdherentServiceImpl.getInstance();
			while(rs.next()){
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsWaiting(Plongee  plongee)
	throws TechnicalException {
		PreparedStatement st;
		try {
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE ");
			sb.append(" from plongee p, liste_attente la, adherent a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE ");
			sb.append(" and DATE_INSCRIPTION is null");
			st = getDataSource().getConnection().
			prepareStatement(sb.toString());
			st.setInt(1, plongee.getId());
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while(rs.next()){
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally{
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}
	
//	public Adherent findDP(List<Adherent> adherents) throws TechnicalException {
//		for(Adherent a : adherents){
//			if(a.isDp()){
//				return a;
//			}
//		}
//		return null;
//	}
	
//	public Adherent findPilote(List<Adherent> adherents) throws TechnicalException {
//		for(Adherent a : adherents){
//			if(a.isPilote()){
//				return a;
//			}
//		}
//		return null;
//	}
	
	private Adherent wrapAdherent(ResultSet rs) throws SQLException, TechnicalException {
		String licence = rs.getString("LICENSE");
		String nom = rs.getString("NOM");
		String prenom = rs.getString("PRENOM");
		NiveauAutonomie niveau = NiveauAutonomie.valueOf(rs.getString("NIVEAU"));
		String telephone = rs.getString("TELEPHONE");
		String mail = rs.getString("MAIL");
		Encadrement encadrant = null;
		if(null != rs.getString("ENCADRANT")){
			encadrant = Encadrement.valueOf(rs.getString("ENCADRANT"));
		}
		int pilote = rs.getInt("PILOTE");
		Adherent adherent = new Adherent();
		adherent.setNumeroLicense(licence);
		adherent.setNom(nom);
		adherent.setPrenom(prenom);
		adherent.setEnumNiveau(niveau);
		adherent.setTelephone(telephone);
		adherent.setMail(mail);
		adherent.setEnumEncadrement(encadrant);
		adherent.setRoles(getStrRoles(adherent));
		if(pilote == 1){
			adherent.setPilote(true);
		} else {
			adherent.setPilote(false);
		}
		return adherent;
	}

}
