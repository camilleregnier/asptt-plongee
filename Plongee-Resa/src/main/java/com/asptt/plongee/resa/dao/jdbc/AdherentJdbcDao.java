package com.asptt.plongee.resa.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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

	public void setPlongeeDao(PlongeeDao plongeeDao) { // setter appelé par
														// Spring pour injecter
														// le bean "plongeeDao"
		this.plongeeDao = plongeeDao;
	}

	public Adherent create(Adherent adh) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO ADHERENT (`LICENSE`, `NOM`, `PRENOM`, `NIVEAU`, `TELEPHONE`, `MAIL`, `ENCADRANT`, `PILOTE`, `DATE_DEBUT`, `ACTIF`)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?)");
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			st.setString(2, adh.getNom());
			st.setString(3, adh.getPrenom());
			st.setString(4, adh.getNiveau());
			st.setString(5, adh.getTelephone());
			st.setString(6, adh.getMail());
			if (null == adh.getEncadrement()) {
				st.setString(7, null);
			} else {
				st.setString(7, adh.getEncadrement());
			}
			if (adh.isPilote()) {
				st.setInt(8, 1);
			} else {
				st.setInt(8, 0);
			}
			st.setInt(9, adh.getActifInt());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"L'adhérent n'a pu être enregistré");
			}
			sb = new StringBuffer();
			// gestion des roles
			if(adh.getActifInt() == 1){
				// On gere les role uniquement pour les actifs
				Iterator it = adh.getRoles().iterator();
				while (it.hasNext()) {
					sb
							.append("INSERT INTO rel_adherent_roles (`ADHERENT_LICENSE`, `ROLES_idROLES`)");
					sb.append(" VALUES (?, ?)");
					st = getDataSource().getConnection().prepareStatement(
							sb.toString());
					st.setString(1, adh.getNumeroLicense());
					int id = getIdRole((String) it.next());
					st.setInt(2, id);
					if (st.executeUpdate() == 0) {
						throw new TechnicalException(
								"Le role n'a pu être enregistré");
					}
					sb = new StringBuffer();
				}
			}
			return adh;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public void delete(Adherent adh) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ADHERENT");
			sb.append(" SET DATE_FIN = current_timestamp , ACTIF = 0");
			sb.append(" WHERE LICENSE = ?");
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent n'a pu être supprimé");
			}
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent update(Adherent adh) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ADHERENT");
			sb.append(" SET NIVEAU = ?,");
			sb.append(" TELEPHONE = ?,");
			sb.append(" MAIL = ?,");
			sb.append(" ENCADRANT = ?,");
			sb.append(" PILOTE = ?,");
			sb.append(" ACTIF = ?");
			sb.append(" WHERE license = ?");

			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setString(1, adh.getNiveau());
			st.setString(2, adh.getTelephone());
			st.setString(3, adh.getMail());
			if (null == adh.getEncadrement()) {
				st.setString(4, null);
			} else {
				st.setString(4, adh.getEncadrement());
			}
			if (adh.isPilote()) {
				st.setInt(5, 1);
			} else {
				st.setInt(5, 0);
			}
			st.setInt(6, adh.getActifInt());
			st.setString(7, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"L'adhérent n'a pu être enregistré");
			}
			sb = new StringBuffer();
			
			// gestion des roles 1er temps : on supprime les roles
			sb.append("DELETE FROM rel_adherent_roles WHERE ADHERENT_LICENSE = ? ");
			PreparedStatement st1 = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st1.setString(1, adh.getNumeroLicense());
			if (st1.executeUpdate() == 0) {
				throw new TechnicalException(
						"Impossible de supprimer les roles de l'adherent");
			}
			// gestion des roles 2ieme temps : on les re-creer
			Iterator it = adh.getRoles().iterator();
			sb = new StringBuffer();
			while (it.hasNext()) {
				sb
						.append("INSERT INTO rel_adherent_roles (`ADHERENT_LICENSE`, `ROLES_idROLES`)");
				sb.append(" VALUES (?, ?)");
				st = getDataSource().getConnection().prepareStatement(
						sb.toString());
				st.setString(1, adh.getNumeroLicense());
				int id = getIdRole((String) it.next());
				st.setInt(2, id);
				if (st.executeUpdate() == 0) {
					throw new TechnicalException(
							"Le role n'a pu être enregistré");
				}
				sb = null;
				sb = new StringBuffer();
			}
			return adh;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
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
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getAdherentsTous() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement("select * from ADHERENT where ACTIF = 1 or ACTIF = 0");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsActifs() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement("select * from ADHERENT where ACTIF = 1");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getAdherentsInactifs() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement("select * from ADHERENT where ACTIF = 0");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getExternes() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement("select * from ADHERENT where ACTIF = 2");
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent findById(String id) throws TechnicalException {
		try {
			PreparedStatement st = getDataSource()
					.getConnection()
					.prepareStatement(
							"select * from ADHERENT where LICENSE = ? and ACTIF <> 0");
			st.setString(1, id);
			ResultSet rs = st.executeQuery();
			Adherent adherent = null;
			if (rs.next()) {
				adherent = wrapAdherent(rs);
			}
			return adherent;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<String> getStrRoles(Adherent adherent)
			throws TechnicalException {
		PreparedStatement st;
		try {

			StringBuffer sb = new StringBuffer(
					"SELECT r.LIBELLE FROM rel_adherent_roles rel, roles r ");
			sb.append(" where rel.ROLES_idROLES = r.idROLES ");
			sb.append(" and rel.ADHERENT_LICENSE = ?");
			st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			ResultSet rs = st.executeQuery();
			List<String> result = new ArrayList<String>();
			while (rs.next()) {
				result.add(rs.getString("LIBELLE"));
			}
			return result;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsLikeName(String name)
	throws TechnicalException {
		PreparedStatement st;
		try {
			String generiqueName = "%";
			generiqueName.concat(name);
			generiqueName.concat("%");
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF ");
			sb.append(" from adherent a ");
			sb.append(" where NOM LIKE ?");
			st = getDataSource().getConnection().prepareStatement(sb.toString());
			st.setString(1, generiqueName);
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsInscrits(Plongee plongee,  String niveauPlongeur, String niveauEncadrement)
			throws TechnicalException {
		PreparedStatement st;
		try {
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF ");
			sb.append(" from plongee p, inscription_plongee i, adherent a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE");
			sb.append(" and DATE_ANNUL_PLONGEE is null");
			if(null != niveauPlongeur){
				sb.append(" and a.NIVEAU = ?");
			}
			if(null != niveauEncadrement){
				if(niveauEncadrement.equalsIgnoreCase("TOUS")){
					sb.append(" and a.ENCADRANT is not null");
				} else{
					sb.append(" and a.ENCADRANT = ?");
				}
			}
			st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setInt(1, plongee.getId());
			if(null != niveauPlongeur){
				st.setString(2, niveauPlongeur);
				if(null != niveauEncadrement && !niveauEncadrement.equalsIgnoreCase("TOUS")){
					st.setString(3, niveauEncadrement);
				}
			} else {
				if(null != niveauEncadrement && !niveauEncadrement.equalsIgnoreCase("TOUS")){
					st.setString(2, niveauEncadrement);
				}
			}
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsWaiting(Plongee plongee)
			throws TechnicalException {
		PreparedStatement st;
		try {
			StringBuffer sb = new StringBuffer(
					"select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF ");
			sb.append(" from plongee p, liste_attente la, adherent a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE ");
			sb.append(" and DATE_INSCRIPTION is null");
			st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setInt(1, plongee.getId());
			ResultSet rs = st.executeQuery();
			List<Adherent> adherents = new ArrayList<Adherent>();
			while (rs.next()) {
				Adherent adherent = wrapAdherent(rs);
				adherents.add(adherent);
			}
			return adherents;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public int getIdRole(String libelle) throws TechnicalException {
		PreparedStatement st;
		try {
			StringBuffer sb = new StringBuffer(
					"select idRoles from roles where libelle=? ");
			st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setString(1, libelle);
			ResultSet rs = st.executeQuery();
			int id = 0;
			while (rs.next()) {
				id = rs.getInt("IdRoles");
			}
			return id;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				getDataSource().getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	private Adherent wrapAdherent(ResultSet rs) throws SQLException,
			TechnicalException {
		String licence = rs.getString("LICENSE");
		String nom = rs.getString("NOM");
		String prenom = rs.getString("PRENOM");
		NiveauAutonomie niveau = NiveauAutonomie
				.valueOf(rs.getString("NIVEAU"));
		String telephone = rs.getString("TELEPHONE");
		String mail = rs.getString("MAIL");
		Encadrement encadrant = null;
		if (null != rs.getString("ENCADRANT")) {
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
		adherent.setActifInt(rs.getInt("ACTIF"));
		if (pilote == 1) {
			adherent.setPilote(true);
		} else {
			adherent.setPilote(false);
		}
		
		return adherent;
	}

}
