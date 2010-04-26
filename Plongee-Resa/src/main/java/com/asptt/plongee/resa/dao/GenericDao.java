package com.asptt.plongee.resa.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * L'interface GenericDao d�finit les fonctions �l�mentaires � toute interface DAO ("CRUDS").
 * <p>Ces fonctions sont :
 * <ul>
 * <li>la cr�ation (<i><b>C</b>RUDS - create</i>)</li>
 * <li>la lecture � partir de l'identifiant (<i>C<b>R</b>UDS - read</i>)</li>
 * <li>la mise � jour (<i>CR<b>U</b>DS - update</i>)</li>
 * <li>la suppression (<i>CRU<b>D</b>S - delete</i>)</li>
 * <li>la recherche (de tous) (<i>CRUD<b>S</b> - search</i>)</li>
 * </ul>
 * </p> 

 * @param <T> identit� persistante
 * @param <ID> identifiant (serializable) de l'entit�
 */
@Transactional (readOnly=true, propagation=Propagation.REQUIRED)
public interface GenericDao<T, ID extends Serializable> {

	/**
	 * Rend l'entit� donn�e persistante.
	 * @param obj entit� � persister
	 * @return l'entit� persist�e (permet de r�cup�rer les valeurs auto-g�n�r�es) 
	 * @throws TechnicalException erreur technique
	 */
	public T create(final T obj) throws TechnicalException;

	/**
	 * Met � jour les donn�es persist�es de l'entit� persistante.
	 * @param obj entit� persistante
	 * @return l'entit� mise � jour (permet de r�cup�rer les valeurs auto-g�n�r�es) 
	 * @throws TechnicalException erreur technique
	 */
	public T update(final T obj) throws TechnicalException;

	/**
	 * Supprime l'entit� de la couche physique de persistance.
	 * <p>Remarque : la fonction ne d�clenchera pas d'exception si l'entit� n'est plus pr�sente dans la couche physique de persistance au moment de la suppression.</p>
	 * @param obj entit� � supprimer
	 * @throws TechnicalException erreur technique
	 */
	public void delete(final T obj) throws TechnicalException;

	/**
	 * Retourne l'entit� persistante correspondant � l'identifiant donn�.<br>
	 * Si aucune entit� ne correspond � cet identifiant, la fonction retourne <code>null</code>.
	 * @param id identifiant de l'entit� recherch�e
	 * @return l'entit� persistante correspondant � l'identifiant donn� ou null
	 * @throws TechnicalException erreur technique
	 */
	public T findById(final ID id) throws TechnicalException;

	/**
	 * Retourne l'ensemble des entit�s persistantes.<br>
	 * Si aucune entit� n'est persist�e, la fonction retourne une liste vide.
	 * @return l'ensemble des entit�s persistantes
	 * @throws TechnicalException erreur technique
	 */
	public List<T> findAll() throws TechnicalException;
}
