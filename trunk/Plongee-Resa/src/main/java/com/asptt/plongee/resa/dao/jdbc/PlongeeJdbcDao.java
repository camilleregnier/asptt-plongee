package com.asptt.plongee.resa.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.asptt.plongee.resa.dao.AdherentDao;
import com.asptt.plongee.resa.dao.PlongeeDao;
import com.asptt.plongee.resa.dao.TechnicalException;
import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.NiveauAutonomie;
import com.asptt.plongee.resa.model.Plongee;
import com.asptt.plongee.resa.model.Plongee.Type;
import com.asptt.plongee.resa.service.impl.AdherentServiceImpl;
import com.asptt.plongee.resa.service.impl.PlongeeServiceImpl;

public class PlongeeJdbcDao extends AbstractJdbcDao implements PlongeeDao {
	
	private AdherentDao adherentDao;  // (l'interface AdherentDao et non une implémentation)
	
	
	public Plongee create(Plongee obj) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(Plongee obj) throws TechnicalException {
		// TODO Auto-generated method stub

	}

	public List<Plongee> findAll() throws TechnicalException {
		try {
			PreparedStatement st = getDataSource().getConnection().
			prepareStatement("SELECT * FROM PLONGEE p  WHERE OUVERTURE_FORCEE=1");
			ResultSet rs = st.executeQuery();
			List<Plongee> plongees = new ArrayList<Plongee>();
			while (rs.next()) {
				Plongee plongee = wrapPlongee(rs);
				plongees.add(plongee);
			}
			return plongees;
		} catch (SQLException e) {
			throw new TechnicalException(e);
		}
	}

	public List<Plongee> findAllOuvertes() throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
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
		}
	}

	public Plongee update(Plongee obj) throws TechnicalException {
		// TODO Auto-generated method stub
		return null;
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
		plongee.setNiveauMinimum(niveauMini);
		plongee.setNbMaxPlaces(nbMaxPlongeur);
		if(ouvertForcee == 1){
			plongee.setOuvertureForcee(true);
		} else {
			plongee.setOuvertureForcee(false);
		}
		List<Adherent> participants = adherentDao.getAdherentsInscrits(plongee);
		plongee.setParticipants(participants);
		Adherent dp = adherentDao.findDP(participants);
		if(null != dp){
			plongee.setDp(dp);
		}
		Adherent pilote = adherentDao.findPilote(participants);
		if(null != pilote){
			plongee.setPilote(pilote);
		}
		List<Adherent> attente = adherentDao.getAdherentsWaiting(plongee);
		plongee.setParticipantsEnAttente(attente);
		return plongee;
	}


    public void setAdherentDao(AdherentDao adherentDao) {   // setter appelé par Spring pour injecter le bean "adherentDao"
        this.adherentDao = adherentDao;
    }
}
