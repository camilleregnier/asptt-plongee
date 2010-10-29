package com.asptt.plongee.resa.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.TechnicalException;
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
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO ADHERENT (`LICENSE`, `NOM`, `PRENOM`, `NIVEAU`, `TELEPHONE`, `MAIL`, `ENCADRANT`, `PILOTE`, `DATE_DEBUT`, `ACTIF`, `PASSWORD`)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
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
			st.setString(10, adh.getNumeroLicense());
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
					sb.append("INSERT INTO REL_ADHERENT_ROLES (`ADHERENT_LICENSE`, `ROLES_idROLES`)");
					sb.append(" VALUES (?, ?)");
					st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public void delete(Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ADHERENT");
			sb.append(" SET DATE_FIN = current_timestamp , ACTIF = 0");
			sb.append(" WHERE LICENSE = ?");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent n'a pu être supprimé");
			}
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent update(Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ADHERENT");
			sb.append(" SET NIVEAU = ?,");
			sb.append(" TELEPHONE = ?,");
			sb.append(" MAIL = ?,");
			sb.append(" ENCADRANT = ?,");
			sb.append(" PILOTE = ?,");
			sb.append(" ACTIF = ?,");
			sb.append(" NOM = ?,");
			sb.append(" PRENOM = ?");
			sb.append(" WHERE license = ?");

			PreparedStatement st = conex.prepareStatement(sb.toString());
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
			st.setString(7, adh.getNom());
			st.setString(8, adh.getPrenom());
			st.setString(9, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"L'adhérent n'a pu être enregistré");
			}
			sb = new StringBuffer();
			
			// gestion des roles 1er temps : on supprime les roles
			sb.append("DELETE FROM REL_ADHERENT_ROLES WHERE ADHERENT_LICENSE = ? ");
			PreparedStatement st1 = conex.prepareStatement(sb.toString());
			st1.setString(1, adh.getNumeroLicense());
			if (st1.executeUpdate() == 0) {
				throw new TechnicalException(
						"Impossible de supprimer les roles de l'adherent");
			}
			// gestion des roles 2ieme temps : on les re-creer
			Iterator it = adh.getRoles().iterator();
			sb = new StringBuffer();
			while (it.hasNext()) {
				sb.append("INSERT INTO REL_ADHERENT_ROLES (`ADHERENT_LICENSE`, `ROLES_idROLES`)");
				sb.append(" VALUES (?, ?)");
				st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent updatePassword(Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ADHERENT");
			sb.append(" SET PASSWORD = ?");
			sb.append(" WHERE license = ?");

			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adh.getPassword());
			st.setString(2, adh.getNumeroLicense());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"Le mot de passe de l'adhérent n'a pu être modifié");
			}
			return adh;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		} finally {
			try {
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> findAll() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT order by NOM");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getAdherentsTous() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where ACTIF = 1 or ACTIF = 0 order by NOM");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsActifs() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where ACTIF = 1 order by NOM");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getAdherentsInactifs() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where ACTIF = 0 order by NOM");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	@Override
	public List<Adherent> getExternes() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where ACTIF = 2 order by NOM");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent findById(String id) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where LICENSE = ? and ACTIF <> 0");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}
	
	public Adherent findByIdAll(String id) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where LICENSE = ? ");
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public Adherent authenticateAdherent(String id, String pwd) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("select * from ADHERENT where LICENSE = ?  and PASSWORD = ? and ACTIF <> 0");
			st.setString(1, id);
			st.setString(2, pwd);
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
				if(null != conex ){
					conex.close();
				}
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
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer(
					"SELECT r.LIBELLE FROM REL_ADHERENT_ROLES rel, ROLES r ");
			sb.append(" where rel.ROLES_idROLES = r.idROLES ");
			sb.append(" and rel.ADHERENT_LICENSE = ?");
			st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
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
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			String generiqueName = "%";
			generiqueName.concat(name);
			generiqueName.concat("%");
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD ");
			sb.append(" from ADHERENT a ");
			sb.append(" where NOM LIKE ? order by NOM");
			st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}

	public List<Adherent> getAdherentsLikeRole(String role)
	throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD ");
			sb.append(" FROM ADHERENT a, REL_ADHERENT_ROLES rel, ROLES r");
			sb.append(" where a.license = rel.adherent_license");
			sb.append(" and rel.roles_idRoles = r.idroles");
			sb.append(" and r.libelle = ? order by NOM");
			st = conex.prepareStatement(sb.toString());
			st.setString(1, role);
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException("Impossible de cloturer la connexion");
			}
		}
	}
	
	/**
	 * Recherche les adherents inscrits à la plongée
	 * filtre sur niveau plongeur si non null
	 * filtre sur encadrement si non null
	 * trie donne l'ordre de tri : 
	 * 	"nom" = nom du plongeur
	 *  "date" ou null = ordre d'inscription à la plongée
	 */
	public List<Adherent> getAdherentsInscrits(Plongee plongee,  String niveauPlongeur, String niveauEncadrement, String trie)
			throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD ");
			sb.append(" from PLONGEE p, INSCRIPTION_PLONGEE i, ADHERENT a ");
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
			if(null == trie || trie.equalsIgnoreCase("date")){
				sb.append(" order by DATE_INSCRIPTION");
			} else if(trie.equalsIgnoreCase("nom")){
				sb.append(" order by NOM");
			}
			st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}
	
	/**
	 * Retourne la liste des adherents en liste d'attente
	 * sur la plongée
	 * trie par le DATE_ATTENTE
	 */
	public List<Adherent> getAdherentsWaiting(Plongee plongee)
			throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer(
					"select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD ");
			sb.append(" from PLONGEE p, LISTE_ATTENTE la, ADHERENT a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE ");
			sb.append(" and DATE_INSCRIPTION is null");
			sb.append(" order by DATE_ATTENTE");
			st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new TechnicalException(
						"Impossible de cloturer la connexion");
			}
		}
	}

	public int getIdRole(String libelle) throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select idRoles from ROLES where libelle=? ");
			st = conex.prepareStatement(sb.toString());
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
				if(null != conex ){
					conex.close();
				}
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
		adherent.setPassword(rs.getString("PASSWORD"));
		
		return adherent;
	}

}
