package com.asptt.plongee.resa.dao.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.ContactUrgent;
import com.asptt.plongee.resa.model.Message;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Adherent.Encadrement;

public class AdherentJdbcDao extends AbstractJdbcDao implements Serializable, AdherentDao {

	private static final long serialVersionUID = 6165324672085274169L;
	private PlongeeDao plongeeDao;
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
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
			sb.append("INSERT INTO ADHERENT (`LICENSE`, `NOM`, `PRENOM`, `NIVEAU`, `TELEPHONE`, `MAIL`, `ENCADRANT`, `PILOTE`, `DATE_DEBUT`, `ACTIF`, `PASSWORD`, `DATE_CM`, `ANNEE_COTI`)");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			st.setString(2, adh.getNom());
			st.setString(3, adh.getPrenom());
			st.setString(4, adh.getNiveau());
			st.setString(5, adh.getTelephone());
			st.setString(6, adh.getMail());
			if (adh.isEncadrent()) {
				st.setString(7, adh.getEncadrement());
			} else {
				st.setString(7, null);
			}
			if (adh.isPilote()) {
				st.setInt(8, 1);
			} else {
				st.setInt(8, 0);
			}
			st.setInt(9, adh.getActifInt());
			st.setString(10, adh.getNumeroLicense());
			Timestamp tsCm = new Timestamp(adh.getDateCM().getTime());
			st.setTimestamp(11, tsCm);
			st.setInt(12, adh.getAnneeCotisation());
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
			// gestion des Contacts
			if(adh.getContacts().size() > 0){
					createContact(adh.getContacts(), adh);
			}
			return adh;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			sb.append(" PRENOM = ?,");
			sb.append(" DATE_CM = ?,");
			sb.append(" ANNEE_COTI = ?");
			sb.append(" WHERE license = ?");

			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adh.getNiveau());
			st.setString(2, adh.getTelephone());
			st.setString(3, adh.getMail());
			if (adh.isEncadrent()) {
				st.setString(4, adh.getEncadrement());
			} else {
				st.setString(4, null);
			}
			if (adh.isPilote()) {
				st.setInt(5, 1);
			} else {
				st.setInt(5, 0);
			}
			st.setInt(6, adh.getActifInt());
			st.setString(7, adh.getNom());
			st.setString(8, adh.getPrenom());
			Timestamp ts = new Timestamp(adh.getDateCM().getTime());
			st.setTimestamp(9, ts);
			st.setInt(10, adh.getAnneeCotisation());
			st.setString(11, adh.getNumeroLicense());
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
			//Mise à jour des contacts
			updateContactForAdherent(adh);
			
			return adh;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	@Override
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
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
			if (null == adherent){
				logger.info("Pb connexion avec le numero de license : "+id+" existe pas ou mauvais mot de passe :"+pwd+" ");
			} else {
				logger.info("L'adherent "+adherent.getNom()+" / "+adherent.getPrenom()+" vient de se connecter");
			}
			return adherent;
		} catch (SQLException e) {
			logger.fatal("L'adherent "+id+" n'a pas pu se connecter");
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	@Override
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
	public List<Adherent> getAdherentsLikeName(String name)
	throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			String generiqueName = "%";
			generiqueName.concat(name);
			generiqueName.concat("%");
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD, DATE_CM, ANNEE_COTI ");
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
	public List<Adherent> getAdherentsLikeRole(String role)
	throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD, DATE_CM, ANNEE_COTI ");
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
	@Override
	public List<Adherent> getAdherentsInscrits(Plongee plongee,  String niveauPlongeur, String niveauEncadrement, String trie)
			throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD, DATE_CM, ANNEE_COTI ");
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	/**
	 * Retourne la liste des adherents en liste d'attente
	 * sur la plongée
	 * trie par le DATE_ATTENTE
	 */
	@Override
	public List<Adherent> getAdherentsWaiting(Plongee plongee)
			throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer(
					"select LICENSE, NOM, PRENOM, NIVEAU, TELEPHONE, MAIL, ENCADRANT, PILOTE, ACTIF, PASSWORD, DATE_CM, ANNEE_COTI ");
			sb.append(" from PLONGEE p, LISTE_ATTENTE la, ADHERENT a ");
			sb.append(" where idPLONGEES = ?");
			sb.append(" and idPLONGEES = PLONGEES_idPLONGEES ");
			sb.append(" and ADHERENT_LICENSE = LICENSE ");
			sb.append(" and DATE_INSCRIPTION is null");
			sb.append(" and SUPPRIMER = 0");
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
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
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
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
		// Pour les Contacts
		adherent.setContacts(getContacts(adherent));
		
		//Pour les nouveaux champs date du certificat medical et annee de cotisation
		Date dateCM = rs.getDate("DATE_CM");
		adherent.setDateCM(dateCM);
		adherent.setAnneeCotisation(rs.getInt("ANNEE_COTI"));
		return adherent;
	}

	@Override
	public List<Message> getAllMessages() throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select * from MESSAGE");

			st = conex.prepareStatement(sb.toString());
			ResultSet rs = st.executeQuery();

			List<Message> messages = new ArrayList<Message>();
			while (rs.next()) {
				Message message = wrapMessage(rs);
				messages.add(message);
			}
			return messages;
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
	public List<Message> getMessage() throws TechnicalException {
		PreparedStatement st;
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("select * from MESSAGE");
			sb.append(" where date_debut <= CURRENT_TIMESTAMP()");
			sb.append(" and CURRENT_TIMESTAMP() <= date_fin " );
			sb.append(" or date_fin is null " );
			sb.append(" order by date_debut " );

			st = conex.prepareStatement(sb.toString());
			ResultSet rs = st.executeQuery();

			List<Message> messages = new ArrayList<Message>();
			while (rs.next()) {
				Message message = wrapMessage(rs);
				messages.add(message);
			}
			return messages;
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	@Override
	public Message updateMessage(Message message) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE MESSAGE");
			sb.append(" SET LIBELLE = ?,");
			sb.append(" DATE_DEBUT = ?,");
			sb.append(" DATE_FIN = ?");
			sb.append(" WHERE IDMESSAGE = ?");

			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, message.getLibelle());
			Timestamp tsDeb = new Timestamp(message.getDateDebut().getTime());
			st.setTimestamp(2, tsDeb);
			if (null == message.getDateFin()) {
				st.setDate(3, null);
			} else {
				Timestamp tsFin = new Timestamp(message.getDateFin().getTime());
				st.setTimestamp(3, tsFin);
			}
			st.setInt(4, message.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"Le message id n°"+message.getId()+"n'a pu être mis à jour");
			}
			return message;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}

	}
	
	@Override
	public Message createMessage(Message message) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO MESSAGE (`LIBELLE`, `DATE_DEBUT`, `DATE_FIN`)");
			sb.append(" VALUES (?, ?, ?)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, message.getLibelle());
			Timestamp tsDeb = new Timestamp(message.getDateDebut().getTime());
			st.setTimestamp(2, tsDeb);
			if (null == message.getDateFin()) {
				st.setDate(3, null);
			} else {
				Timestamp tsFin = new Timestamp(message.getDateFin().getTime());
				st.setTimestamp(3, tsFin);
			}
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"Le message n'a pu être creé");
			}
			return message;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	@Override
	public void deleteMessage(Message message) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM MESSAGE WHERE IDMESSAGE = ? ");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setInt(1, message.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"Le message n'a pu être supprimé");
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	private Message wrapMessage(ResultSet rs) throws SQLException,	TechnicalException {
		int id = rs.getInt("idMESSAGE");
		String libelle = rs.getString("LIBELLE");
		Date dateDebut = rs.getDate("DATE_DEBUT");
		Date dateFin = rs.getDate("DATE_FIN");
		
		Message message = new Message();
		message.setId(id);
		message.setLibelle(libelle);
		message.setDateDebut(dateDebut);
		message.setDateFin(dateFin);
		
		return message;
	}

	public List<ContactUrgent> getContacts(Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT cu.idCONTACT, cu.TITRE, cu.NOM, cu.PRENOM, cu.TELEPHONE, cu.TELEPHTWO");
			sb.append(" FROM REL_ADHERENT_CONTACT rel , CONTACT_URGENT cu");
			sb.append(" where rel.ADHERENT_LICENSE = ?" );
			sb.append(" and  rel.CONTACT_URGENT_IDCONTACT = cu.IDCONTACT" );
			sb.append(" order by cu.IDCONTACT" );

			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adh.getNumeroLicense());
			ResultSet rs = st.executeQuery();

			List<ContactUrgent> contacts = new ArrayList<ContactUrgent>();
			while (rs.next()) {
				ContactUrgent contact = wrapContact(rs);
				contacts.add(contact);
			}
			return contacts;
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	public ContactUrgent findContact(ContactUrgent contact, Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT c.idCONTACT, c.TITRE, c.NOM, c.PRENOM, c.TELEPHONE, c.TELEPHTWO");
			sb.append(" FROM REL_ADHERENT_CONTACT rel , CONTACT_URGENT c");
			sb.append(" WHERE rel.CONTACT_URGENT_idCONTACT = c.idCONTACT" );
			sb.append(" AND c.NOM = ?" );
			sb.append(" AND c.PRENOM = ?" );
			sb.append(" AND rel.ADHERENT_LICENSE = ?" );

			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, contact.getNom());
			st.setString(2, contact.getPrenom());
			st.setString(3, adh.getNumeroLicense());
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				ContactUrgent result = wrapContact(rs);
				return result;
			}else{
				return null;
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	public void createContact(List<ContactUrgent> contacts, Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = null;
			for(ContactUrgent contact : contacts){
				StringBuffer sb = new StringBuffer();
				sb.append("INSERT INTO CONTACT_URGENT (`TITRE`, `NOM`, `PRENOM`, `TELEPHONE`, `TELEPHTWO`)");
				sb.append(" VALUES (?, ?, ?, ?, ?)");
				st = conex.prepareStatement(sb.toString());
				st.setString(1, contact.getTitre());
				st.setString(2, contact.getNom());
				st.setString(3, contact.getPrenom());
				st.setString(4, contact.getTelephone());
				st.setString(5, contact.getTelephtwo());
				if (st.executeUpdate() == 0) {
					throw new TechnicalException(
							"Le contact :"+contact.getNom()+"n'a pu être creé");
				}else{
					//on recupere l'id du contact que l'on vient de créer 
					StringBuffer sb1 = new StringBuffer();
					sb1.append("SELECT max(idCONTACT) FROM contact_urgent c");
					st = conex.prepareStatement(sb1.toString());
					ResultSet rs = st.executeQuery();
					if (rs.next()) {
						int idContact = rs.getInt("max(idCONTACT)");
						//On crée la relation
						StringBuffer sb2 = new StringBuffer();
						sb2.append("INSERT INTO REL_ADHERENT_CONTACT (`ADHERENT_LICENSE`, `CONTACT_URGENT_IDCONTACT`)");
						sb2.append(" VALUES (?, ?)");
						st = conex.prepareStatement(sb2.toString());
						st.setString(1, adh.getNumeroLicense());
						st.setInt(2, idContact);
						if (st.executeUpdate() == 0) {
							throw new TechnicalException(
									"La relation adherent"+adh.getNom()+" le contact :"+contact.getNom()+", "+ contact.getPrenom()+"n'a pu être creé");
						}
					}else{
						throw new TechnicalException(
								"Imossible de recuperer le dernier contact créer");
					}
					

				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	public void updateContactForAdherent(Adherent adh) throws TechnicalException {
		
		List<ContactUrgent> anciensContacts = getContacts(adh);
		List<ContactUrgent> contactsAMettreAjour = adh.getContacts();

		List<ContactUrgent> newContacts = new ArrayList<ContactUrgent>();
		List<ContactUrgent> delContacts = new ArrayList<ContactUrgent>();
		List<ContactUrgent> majContacts = new ArrayList<ContactUrgent>();

		for(ContactUrgent contact : contactsAMettreAjour){
			ContactUrgent result = findContact(contact, adh);
			if(null == result){
				//Ce contact n'existe pas : on le met dans la liste des contacts à créer
				newContacts.add(contact);
			} else {
				//Contact à mettre à jour
				if(anciensContacts.contains(contact)){
					contact.setId(result.getId());
					majContacts.add(contact);
				}
			}
		}
		// Recherche des contacts à supprimer
		for(ContactUrgent contact : anciensContacts){
			if(! contactsAMettreAjour.contains(contact)){
				delContacts.add(contact);
			}
		}

		createContact(newContacts, adh);
		updateContact(majContacts);
		deleteContact(delContacts, adh);
		
	}

		
	public void updateContact(List<ContactUrgent> contacts) throws TechnicalException {
		Connection conex=null;
		try {
			for(ContactUrgent contact : contacts){
				conex = getDataSource().getConnection();
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE CONTACT_URGENT");
				sb.append(" SET TITRE = ?,");
				sb.append(" NOM = ?,");
				sb.append(" PRENOM = ?,");
				sb.append(" TELEPHONE = ?,");
				sb.append(" TELEPHTWO = ?");
				sb.append(" WHERE IDCONTACT = ?");
	
				PreparedStatement st = conex.prepareStatement(sb.toString());
				st.setString(1, contact.getTitre());
				st.setString(2, contact.getNom());
				st.setString(3, contact.getPrenom());
				st.setString(4, contact.getTelephone());
				st.setString(5, contact.getTelephtwo());
				st.setInt(6, contact.getId());
				if (st.executeUpdate() == 0) {
					throw new TechnicalException(
							"Le Contact id : "+contact.getId()+"n'a pu être mis à jour");
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	public void deleteContact(List<ContactUrgent> contacts, Adherent adh) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = null;
			for(ContactUrgent contact : contacts){
				StringBuffer sb = new StringBuffer();
				sb.append("DELETE FROM REL_ADHERENT_CONTACT");
				sb.append(" WHERE ADHERENT_LICENSE = ?");
				sb.append(" AND CONTACT_URGENT_IDCONTACT = ?");
				st = conex.prepareStatement(sb.toString());
				st.setString(1, adh.getNumeroLicense());
				st.setInt(2, contact.getId());
				if (st.executeUpdate() == 0) {
					throw new TechnicalException(
							"La relation contact :"+contact.getNom()+" adherent "+adh.getNom()+" n'a pu être supprimée");
				}else{
					//On supprime le contact
					StringBuffer sb2 = new StringBuffer();
					sb2.append("DELETE FROM CONTACT_URGENT");
					sb2.append(" WHERE CONTACT_URGENT_IDCONTACT = ?");
					st = conex.prepareStatement(sb2.toString());
					st.setInt(1, contact.getId());
					if (st.executeUpdate() == 0) {
						throw new TechnicalException(
								"La contact :"+contact.getNom()+", "+ contact.getPrenom()+"n'a pu être supprimé");
					}
	
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	private ContactUrgent wrapContact(ResultSet rs) throws SQLException,	TechnicalException {
		int id = rs.getInt("idCONTACT");
		String titre = rs.getString("TITRE");
		String nom = rs.getString("NOM");
		String prenom = rs.getString("PRENOM");
		String telephone = rs.getString("TELEPHONE");
		String mail = rs.getString("TELEPHTWO");
		
		ContactUrgent contact = new ContactUrgent();
		contact.setId(id);
		contact.setTitre(titre);
		contact.setNom(nom);
		contact.setPrenom(prenom);
		contact.setTelephone(telephone);
		contact.setTelephtwo(mail);
		
		return contact;
	}

}
