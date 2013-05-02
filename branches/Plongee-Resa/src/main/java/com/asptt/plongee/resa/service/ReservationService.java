package com.asptt.plongee.resa.service;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.asptt.plongee.resa.model.Adherent;
import com.asptt.plongee.resa.model.Plongee;

@WebService 
public interface ReservationService 
{

	@WebMethod
	List<Plongee> rechercherPlongeeProchainJour(
			@WebParam(name = "isVesteRouge")
			boolean isVesteRouge);
	
	@WebMethod
	List<Plongee> rechercherPlongeesAdherentInscrit(
			@WebParam(name = "numeroLicence") String numeroLicence, 
			@WebParam(name = "nombreHeure")int nbHours);
	
}
