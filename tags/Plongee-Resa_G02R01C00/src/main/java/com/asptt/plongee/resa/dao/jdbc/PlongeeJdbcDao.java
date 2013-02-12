package com.asptt.plongee.resa.dao.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.InscriptionFilleul;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Plongee.Type;
import com.asptt.plongee.resa.util.Parameters;

public class PlongeeJdbcDao extends AbstractJdbcDao implements Serializable, PlongeeDao {
	
	private final Logger log = Logger.getLogger(getClass().getName());
	
	private static final long serialVersionUID = 5459542271710949540L;
	private AdherentDao adherentDao;  // (l'interface AdherentDao et non une implémentation)
	
	public void setAdherentDao(AdherentDao adherentDao) {   // setter appelé par Spring pour injecter le bean "adherentDao"
		this.adherentDao = adherentDao;
	}
	
	
	public Plongee create(Plongee obj) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO PLONGEE (`DATE`, `DEMIE_JOURNEE`, `OUVERTURE_FORCEE`, `NIVEAU_MINI`, `NB_MAX_PLG`,`DATE_VISIBLE`)");
			sb.append(" VALUES (?,?,?,?,?,?)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			Timestamp ts = new Timestamp(obj.getDate().getTime());
			st.setTimestamp(1, ts);
			st.setString(2, obj.getType());
			if (obj.getOuvertureForcee()) {
				st.setInt(3, 1);
			} else {
				st.setInt(3, 0);
			}
			if (null == obj.getNiveauMinimum()) {
				st.setString(4, NiveauAutonomie.BATM.toString());
			} else {
				st.setString(4, obj.getNiveauMinimum().toString());
			}
			st.setInt(5, obj.getNbMaxPlaces());
			
			Timestamp tsVisi = new Timestamp(obj.getDateVisible().getTime());
			st.setTimestamp(6, tsVisi);
			
			//on cree la plongée
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"La plongée n'a pu être enregistrée");
			}
			return obj;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	public void delete(Plongee obj) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE PLONGEE");
			sb.append(" SET OUVERTURE_FORCEE = 0");
			sb.append(" WHERE IDPLONGEES = ?");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setInt(1, obj.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"La plongée "+obj.getId()+" n'a pu être annulée");
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}

	public Plongee update(Plongee obj) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE PLONGEE");
			sb.append(" SET OUVERTURE_FORCEE = ?,");
			sb.append(" NIVEAU_MINI = ?,");
			sb.append(" NB_MAX_PLG = ?");
			sb.append(" WHERE IDPLONGEES = ?");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			if (obj.getOuvertureForcee()) {
				st.setInt(1, 1);
			} else {
				st.setInt(1, 0);
			}
			if (null == obj.getNiveauMinimum()) {
				st.setString(2, null);
			} else {
				st.setString(2, obj.getNiveauMinimum().toString());
			}
			st.setInt(3, obj.getNbMaxPlaces());
			st.setInt(4, obj.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"La plongée "+obj.getId()+" n'a pu être modifiée");
			}
			return obj;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally {
			closeConnexion(conex);
		}
	}
	
	public List<Plongee> findAll() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM PLONGEE p");
			sb.append(" WHERE OUVERTURE_FORCEE=1");
			sb.append(" and date > CURRENT_TIMESTAMP()");
			sb.append(" and now() >= DATE_ADD(date_visible, INTERVAL ? DAY)");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			
			st.setString(1, Parameters.getString("visible.max"));
			
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}
	
	/**
	 * Retourne les plongées à partir du lendemain
	 */
	@Override
	public List<Plongee> getPlongeesForEncadrant( int visibleApres, int nbjour) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("SELECT * FROM PLONGEE p");
			sb.append(" WHERE OUVERTURE_FORCEE=1");
			sb.append(" and date > CURRENT_DATE()");
			sb.append(" and now() >= DATE_ADD(date_visible, INTERVAL ? DAY)");
			sb.append(" and now() < DATE_ADD(date, INTERVAL ? HOUR)");
			sb.append(" ORDER BY DATE");
			
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setInt(1, nbjour);
			st.setInt(2, visibleApres);
			
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	/**
	 * Retourne les plongées pour l'adherent
	 */
	@Override
	public List<Plongee> getPlongeesForAdherent() throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer("SELECT * FROM PLONGEE p");
			sb.append(" WHERE OUVERTURE_FORCEE=1");
			sb.append(" and date > CURRENT_TIMESTAMP()");
			sb.append(" and now() >= DATE_VISIBLE");
			sb.append(" ORDER BY DATE");
			
			PreparedStatement st = conex.prepareStatement(sb.toString());
			
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	public Plongee findById(Integer id) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			PreparedStatement st = conex.prepareStatement("SELECT * FROM PLONGEE p  WHERE idPLONGEES = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			Plongee plongee = null;
			if (rs.next()) {
				plongee = wrapPlongee(rs);
			}
			return plongee;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent, int nbHours)
	throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT pl.`idPLONGEES`, pl.`DATE`, pl.`DEMIE_JOURNEE`, pl.`OUVERTURE_FORCEE`, pl.`NIVEAU_MINI`, pl.`NB_MAX_PLG`, pl.`DATE_VISIBLE`");
			sb.append(" FROM PLONGEE pl, INSCRIPTION_PLONGEE rel , ADHERENT ad");
			sb.append(" WHERE license = ?");
			sb.append(" AND license = adherent_license");
			sb.append(" AND plongees_idPlongees = idPlongees");
			sb.append(" AND date > DATE_ADD(now(), INTERVAL ? HOUR)");
			sb.append(" AND date_annul_plongee is null");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, nbHours);
			System.out.println(st.toString());
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public List<InscriptionFilleul> getPlongeesWhereFilleulIsInscrit(Adherent adherent, int nbHours)
	throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT ad.`LICENSE`, pl.`idPLONGEES`");
			sb.append(" FROM PLONGEE pl, INSCRIPTION_PLONGEE rel , ADHERENT ad, REL_PARRAIN_FILLEUL pf");
			sb.append(" WHERE idparrain = ?");
			sb.append(" AND ( idFilleul = adherent_license AND idPlongee = plongees_idPlongees AND date_annul_plongee is null)");
			sb.append(" AND idPlongee = idPlongees");
			sb.append(" AND idFilleul = license");
			sb.append(" AND pl.date > DATE_ADD(now(), INTERVAL ? HOUR)");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE;");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, nbHours);
			ResultSet rs = st.executeQuery();
			List<InscriptionFilleul> plongeesDesFilleuls = new ArrayList<InscriptionFilleul>();
			while (rs.next()) {
				Adherent filleul = adherentDao.findById(rs.getString("LICENSE"));
				Plongee plongee = findById(new Integer(rs.getInt("idPLONGEES")));
				InscriptionFilleul unFilleul = new InscriptionFilleul(filleul,plongee);
				plongeesDesFilleuls.add(unFilleul);
			}
			return plongeesDesFilleuls;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public List<Plongee> getPlongeesWhithSameDate(Date date, String type)
			throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM PLONGEE p");
			sb.append(" WHERE date(DATE) = ? AND DEMIE_JOURNEE = ?");
			sb.append(" and OUVERTURE_FORCEE = 1");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String laDateRecherchee = sdf.format(date);			
			st.setString(1, laDateRecherchee);
			st.setString(2, type);
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}


	@Override
	public List<Plongee> getListeAttenteForAdherent(Adherent adherent)
	throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT pl.`idPLONGEES`, pl.`DATE`, pl.`DEMIE_JOURNEE`, pl.`OUVERTURE_FORCEE`, pl.`NIVEAU_MINI`, pl.`NB_MAX_PLG`");
			sb.append(" FROM PLONGEE pl, LISTE_ATTENTE rel , ADHERENT ad");
			sb.append(" WHERE license = ?");
			sb.append(" AND license = adherent_license");
			sb.append(" AND plongees_idPlongees = idPlongees");
			sb.append(" AND date < current_date()");
			sb.append(" and OUVERTURE_FORCEE = 1");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public void inscrireAdherentPlongee(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO INSCRIPTION_PLONGEE (`ADHERENT_LICENSE`, PLONGEES_idPLONGEES, `DATE_INSCRIPTION`)");
			sb.append(" VALUES (?, ?, current_timestamp)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être inscrit à la plongée:"+plongee.getId()+"."); 
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public void inscrireAdherentPlongee(Plongee plongee,
			Adherent plongeur, Adherent parrain) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO INSCRIPTION_PLONGEE (`ADHERENT_LICENSE`, PLONGEES_idPLONGEES, `DATE_INSCRIPTION`)");
			sb.append(" VALUES (?, ?, current_timestamp)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, plongeur.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+plongeur.getNumeroLicense()+
						" n'a pu être inscrit à la plongée:"+plongee.getId()+"."); 
			}
			StringBuffer sb1 = new StringBuffer();
			sb1.append("INSERT INTO REL_PARRAIN_FILLEUL (`IDPARRAIN`, `IDFILLEUL`, `IDPLONGEE`)");
			sb1.append(" VALUES (?, ?, ?)");
			st = conex.prepareStatement(sb1.toString());
			st.setString(1, parrain.getNumeroLicense());
			st.setString(2, plongeur.getNumeroLicense());
			st.setInt(3, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+plongeur.getNumeroLicense()+
						" n'a pu être inscrit à la plongée:"+plongee.getId()+"."); 
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public void supprimeAdherentPlongee(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("update INSCRIPTION_PLONGEE");
			sb.append(" set date_annul_plongee = current_timestamp");
			sb.append(" where adherent_license = ?  ");
			sb.append(" and plongees_idplongees = ? ");
			sb.append(" and date_annul_plongee is null ");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
					" n'a pu être désinscrit à la plongée:"+plongee.getId()+".",e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public void inscrireAdherentAttente(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO LISTE_ATTENTE (ADHERENT_LICENSE, PLONGEES_idPLONGEES, DATE_ATTENTE, SUPPRIMER)");
			sb.append(" VALUES (?, ?, current_timestamp,?)");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			st.setInt(3, 0);
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être inscrit en liste d'attente de la plongée:"+plongee.getId()+"."); 
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException(e);
		} finally{
			closeConnexion(conex);
		}
	}

	/**
	 * En fait on ne supprime pas l'adherent
	 * mais on init le champ Date_inscription à la date du jour
	 */
	@Override
	public void sortirAdherentAttente(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("update LISTE_ATTENTE");
			sb.append(" SET DATE_INSCRIPTION = CURRENT_TIMESTAMP ");
			sb.append(" where adherent_license = ?  ");
			sb.append(" and plongees_idplongees = ? ");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
					" n'a pu être sorti de la liste d'attente de la plongée:"+plongee.getId()+".",e);
		} finally{
			closeConnexion(conex);
		}
	}

	@Override
	public void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent)
			throws TechnicalException {
			inscrireAdherentPlongee(plongee, adherent);
			sortirAdherentAttente(plongee, adherent);
	}


	private Plongee wrapPlongee(ResultSet rs)throws SQLException, TechnicalException {
		int id = rs.getInt("idPLONGEES");
		Date date = rs.getTimestamp("DATE");
		Date dateVisible = rs.getTimestamp("DATE_VISIBLE");
		Type demie_journee = Type.valueOf(rs.getString("DEMIE_JOURNEE"));
		String nMin = rs.getString("NIVEAU_MINI");
		NiveauAutonomie niveauMini = NiveauAutonomie.P0;	
		if(null != nMin){
			niveauMini = NiveauAutonomie.valueOf(nMin);	
		}
		int ouvertForcee = rs.getInt("OUVERTURE_FORCEE");
		int nbMaxPlongeur = rs.getInt("NB_MAX_PLG");
		Plongee plongee = new Plongee();
		plongee.setId(id);
		plongee.setType(demie_journee);
		//Mise à jour de la date
		//maj de l'heure de la plongée en fonction du type
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		plongee.setDate(date);
		plongee.setDateVisible(dateVisible);
		plongee.setEnumNiveauMinimum(niveauMini);
		plongee.setNbMaxPlaces(nbMaxPlongeur);
		if(ouvertForcee == 1){
			plongee.setOuvertureForcee(true);
		} else {
			plongee.setOuvertureForcee(false);
		}
		List<Adherent> participants = adherentDao.getAdherentsInscrits(plongee,null,null,null);
		plongee.setParticipants(participants);
		plongee.setDp();
		for(Adherent a : participants){
			if(a.isPilote()){
				plongee.setPilote(a);
			}
		}
		List<Adherent> attente = adherentDao.getAdherentsWaiting(plongee);
		plongee.setParticipantsEnAttente(attente);
		return plongee;
	}


	@Override
	public void supprimerDeLaListeAttente(Plongee plongee, Adherent adherent, int indic)  throws TechnicalException {
		Connection conex=null;
		try {
			conex = getDataSource().getConnection();
			StringBuffer sb = new StringBuffer();
			sb.append("update LISTE_ATTENTE");
			sb.append(" SET SUPPRIMER = ? ");
			sb.append(" where adherent_license = ?  ");
			sb.append(" and plongees_idplongees = ? ");
			PreparedStatement st = conex.prepareStatement(sb.toString());
			st.setInt(1, indic);
			st.setString(2, adherent.getNumeroLicense());
			st.setInt(3, plongee.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
					" n'a pu être supprimé de la liste d'attente de la plongée:"+plongee.getId()+", indicateur = "+indic+".",e);
		} finally{
			closeConnexion(conex);
		}	
	}

}
