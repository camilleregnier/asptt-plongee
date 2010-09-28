package com.asptt.plongee.resa.dao.jdbc;

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
import java.util.Locale;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.exception.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.ResaConstants;
import com.asptt.plongee.resa.model.Plongee.Type;
import com.asptt.plongee.resa.service.impl.AdherentServiceImpl;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class PlongeeJdbcDao extends AbstractJdbcDao implements PlongeeDao {
	
	private AdherentDao adherentDao;  // (l'interface AdherentDao et non une implémentation)
	
	public void setAdherentDao(AdherentDao adherentDao) {   // setter appelé par Spring pour injecter le bean "adherentDao"
		this.adherentDao = adherentDao;
	}
	
	
	public Plongee create(Plongee obj) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO PLONGEE (`DATE`, `DEMIE_JOURNEE`, `OUVERTURE_FORCEE`, `NIVEAU_MINI`, `NB_MAX_PLG`)");
			sb.append(" VALUES (?,?,?,?,?)");
			PreparedStatement st = getDataSource().getConnection().prepareStatement(sb.toString());
			//maj de l'heure de la plongée en fonction du type
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(obj.getDate());
			if( obj.getType().equalsIgnoreCase(Plongee.Type.MATIN.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 8);
				gc.set(GregorianCalendar.MINUTE, 0);
				gc.set(GregorianCalendar.SECOND, 0);
				obj.setDate(gc.getTime());
			} else if( obj.getType().equalsIgnoreCase(Plongee.Type.APRES_MIDI.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 13);
				gc.set(GregorianCalendar.MINUTE, 0);
				gc.set(GregorianCalendar.SECOND, 0);
				obj.setDate(gc.getTime());
			} else if( obj.getType().equalsIgnoreCase(Plongee.Type.SOIR.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 18);
				gc.set(GregorianCalendar.MINUTE, 0);
				gc.set(GregorianCalendar.SECOND, 0);
				obj.setDate(gc.getTime());
			} else if( obj.getType().equalsIgnoreCase(Plongee.Type.NUIT.toString()) ){
				gc.set(GregorianCalendar.HOUR_OF_DAY, 21);
				gc.set(GregorianCalendar.MINUTE, 0);
				gc.set(GregorianCalendar.SECOND, 0);
				obj.setDate(gc.getTime());
			}
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
			//on cree la plongée
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"La plongée n'a pu être enregistrée");
			}
			return obj;
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

	public void delete(Plongee obj) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE PLONGEE");
			sb.append(" SET OUVERTURE_FORCEE = 0");
			sb.append(" WHERE IDPLONGEES = ?");
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
			st.setInt(1, obj.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException(
						"La plongée "+obj.getId()+" n'a pu être annulée");
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

	public Plongee update(Plongee obj) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE PLONGEE");
			sb.append(" SET OUVERTURE_FORCEE = ?,");
			sb.append(" NIVEAU_MINI = ?,");
			sb.append(" NB_MAX_PLG = ?");
			sb.append(" WHERE IDPLONGEES = ?");
			PreparedStatement st = getDataSource().getConnection()
					.prepareStatement(sb.toString());
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
	
	public List<Plongee> findAll() throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM PLONGEE p");
			sb.append(" WHERE OUVERTURE_FORCEE=1");
			sb.append(" and date > CURRENT_TIMESTAMP()");
			sb.append(" and date < DATE_ADD(CURRENT_DATE(), INTERVAL " + ResaConstants.MAX_JOUR_VISIBLE + " DAY)");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE");
			PreparedStatement st = getDataSource().getConnection().prepareStatement(sb.toString());
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
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
	
	public List<Plongee> getPlongeesForFewDay( int nbjour) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer("SELECT * FROM PLONGEE p  WHERE OUVERTURE_FORCEE=1");
			sb.append(" and date < DATE_ADD(CURRENT_DATE(), INTERVAL " + nbjour + " DAY)");
			sb.append(" and date > CURRENT_TIMESTAMP()");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE");
			
			PreparedStatement st = getDataSource().getConnection().prepareStatement(sb.toString());
			
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				//if(plongee.isOuverte()) {
					plongees.add(plongee);
				//}
			}
			return plongees;
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

	public Plongee findById(Integer id) throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().
			prepareStatement("SELECT * FROM PLONGEE p  WHERE idPLONGEES = ?");
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			Plongee plongee = null;
			if (rs.next()) {
				plongee = wrapPlongee(rs);
			}
			return plongee;
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

	public List<Plongee> getPlongeesWhereAdherentIsInscrit(Adherent adherent, int nbHours)
	throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT pl.`idPLONGEES`, pl.`DATE`, pl.`DEMIE_JOURNEE`, pl.`OUVERTURE_FORCEE`, pl.`NIVEAU_MINI`, pl.`NB_MAX_PLG`");
			sb.append(" FROM plongee pl, inscription_plongee rel , adherent ad");
			sb.append(" WHERE license = ?");
			sb.append(" AND license = adherent_license");
			sb.append(" AND plongees_idPlongees = idPlongees");
			sb.append(" AND date > DATE_ADD(now(), INTERVAL ? HOUR)");
			sb.append(" AND date_annul_plongee is null");
			sb.append(" and OUVERTURE_FORCEE = 1");
			sb.append(" ORDER BY DATE");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, nbHours);
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
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

	@Override
	public List<Plongee> getPlongeesWhithSameDate(Date date, String type)
			throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT * FROM PLONGEE p");
			sb.append(" WHERE date(DATE) = ? AND DEMIE_JOURNEE = ?");
			sb.append(" and OUVERTURE_FORCEE = 1");
			PreparedStatement st = getDataSource().getConnection().prepareStatement(sb.toString());
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


	public List<Plongee> getListeAttenteForAdherent(Adherent adherent)
	throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT pl.`idPLONGEES`, pl.`DATE`, pl.`DEMIE_JOURNEE`, pl.`OUVERTURE_FORCEE`, pl.`NIVEAU_MINI`, pl.`NB_MAX_PLG`");
			sb.append(" FROM plongee pl, liste_attente rel , adherent ad");
			sb.append(" WHERE license = ?");
			sb.append(" AND license = adherent_license");
			sb.append(" AND plongees_idPlongees = idPlongees");
			sb.append(" AND date < current_date()");
			sb.append(" and OUVERTURE_FORCEE = 1");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
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

	public void inscrireAdherentPlongee(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO INSCRIPTION_PLONGEE (`ADHERENT_LICENSE`, PLONGEES_idPLONGEES, `DATE_INSCRIPTION`)");
			sb.append(" VALUES (?, ?, current_timestamp)");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être inscrit à la plongée:"+plongee.getId()+"."); 
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

	public void supprimeAdherentPlongee(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("update inscription_plongee");
			sb.append(" set date_annul_plongee = current_timestamp");
			sb.append(" where adherent_license = ?  ");
			sb.append(" and plongees_idplongees = ? ");
			sb.append(" and date_annul_plongee is null ");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être de-inscrit à la plongée:"+plongee.getId()+"."); 
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

	public void inscrireAdherentAttente(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		int rang = 0;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select MAX(RANG) from LISTE_ATTENTE where plongees_idplongees = ?");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setInt(1, plongee.getId());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if(0 == rs.getInt(1)){
					rang =1;
				}else{
					rang = rs.getInt(1) + 1;
				}
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
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO LISTE_ATTENTE (ADHERENT_LICENSE, PLONGEES_idPLONGEES, RANG, DATE_ATTENTE)");
			sb.append(" VALUES (?, ?, "+rang+",current_timestamp)");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être inscrit en liste d'attente de la plongée:"+plongee.getId()+"."); 
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

	/**
	 * En fait on ne supprime pas l'adherent
	 * mais on init le champ Date_inscription à la date du jour
	 */
	public void supprimeAdherentAttente(Plongee plongee,
			Adherent adherent) throws TechnicalException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("update liste_attente");
			sb.append(" SET DATE_INSCRIPTION = CURRENT_TIMESTAMP ");
			sb.append(" where adherent_license = ?  ");
			sb.append(" and plongees_idplongees = ? ");
			PreparedStatement st = getDataSource().getConnection().
				prepareStatement(sb.toString());
			st.setString(1, adherent.getNumeroLicense());
			st.setInt(2, plongee.getId());
			if (st.executeUpdate() == 0) {
				throw new TechnicalException("L'adhérent"+adherent.getNumeroLicense()+
						" n'a pu être supprimé de la liste d'attente de la plongée:"+plongee.getId()+"."); 
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

	public void moveAdherentAttenteToInscrit(Plongee plongee, Adherent adherent)
			throws TechnicalException {
			inscrireAdherentPlongee(plongee, adherent);
			supprimeAdherentAttente(plongee, adherent);
	}


	private Plongee wrapPlongee(ResultSet rs)throws SQLException, TechnicalException {
		int id = rs.getInt("idPLONGEES");
		Date date = rs.getDate("DATE");
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
		plongee.setDate(date);
		plongee.setType(demie_journee);
		plongee.setEnumNiveauMinimum(niveauMini);
		plongee.setNbMaxPlaces(nbMaxPlongeur);
		if(ouvertForcee == 1){
			plongee.setOuvertureForcee(true);
		} else {
			plongee.setOuvertureForcee(false);
		}
		List<Adherent> participants = adherentDao.getAdherentsInscrits(plongee,null,null,null);
		plongee.setParticipants(participants);
		for(Adherent a : participants){
			if(a.isDp()){
				plongee.setDp(a);
			}
		}
		for(Adherent a : participants){
			if(a.isPilote()){
				plongee.setPilote(a);
			}
		}
		List<Adherent> attente = adherentDao.getAdherentsWaiting(plongee);
		plongee.setParticipantsEnAttente(attente);
		return plongee;
	}

}
