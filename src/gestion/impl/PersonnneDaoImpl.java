package gestion.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.openejb.jee.wls.SqlQuery;
import org.hibernate.Session;

import gestion.dao.IPersonneDAO;
import gestion.entities.Activite;
import gestion.entities.NatureActivite;
import gestion.entities.Personne;

@Stateless
public class PersonnneDaoImpl implements IPersonneDAO {
	
	
	@PersistenceContext(unitName="persistence")
	EntityManager em; 
	


	@Override
	public List<Personne> getAllPerson() {
		  Query query = em.createNamedQuery("findAllPersonnes",Personne.class);
		  return query.getResultList();
	}
	@Override
	public List<Personne> getAllPerson(int page, int pageSize) {
		// TODO Auto-generated method stub
		 Query query = em.createNamedQuery("findAllPersonnes",Personne.class);
		 query.setFirstResult(pageSize*page);
		 query.setMaxResults(pageSize);
		 return query.getResultList();
	}
	@Override
	public Personne getPersonById(long id) {
		return em.find(Personne.class,id);
		
	}

	@Override
	public void deletePersonne(Personne p) {
		em.remove(em.contains(p) ? p : em.merge(p));

	}

	@Override
	public void deletePersonneById(long id) {
		Personne p=getPersonById(id);
	
		em.remove(p);
		
	}

	@Override
	public void savePersonne(Personne p) {
		if(p.getIdPerson() == null) {
			System.err.println("On persiste une personne");
			em.persist(p);
		}
		else {
			System.err.println("On merge une personne");
			em.merge(p);
			}
	}
	public List<Personne> applyFilter(String nom,String prenom,String activiteTitre){

		HashMap<String, String> parameters = new HashMap();
		if(!nom.equals("")) parameters.put("n", nom);
		if(!prenom.equals("")) parameters.put("p", prenom);
		if(!activiteTitre.equals("")) parameters.put("a", activiteTitre);
		
		/*Il faut construire la requêtes en fonction des params présents*/
		/*TODO: rajouter le champs activite... Je l'ai enlevé temporairement*/
		/*TODO: rendre insensible à la casse ?? ie. Aucune différence entre minuscule et maj*/
		String queryString= "select p from Personne p where ";
		
		int counter = 0;
		
		for (Map.Entry me : parameters.entrySet()) {
			
	          System.out.println("Key: "+me.getKey() + " & Value: " + me.getValue());
	          //TODO: Utiliser StringBuilder LOL ???
	          if(counter > 0) {
	        	  queryString += " and ";
	          }
	          
	          if(me.getKey().equals("n") && !me.getValue().equals("")) {
	        	  queryString += "p.nom like :nomF";
	          }
	          if(me.getKey().equals("p") && !me.getValue().equals("")) {
	        	  queryString += "p.prenom like :prenomF";
	          }
	          if(me.getKey().equals("a") && !me.getValue().equals("")) {
	        	  queryString += "a.titre like :titreF and a.personne =p";
	          }
	          counter++;
	        }
		System.out.println("QUERY FINALE FILTER: "+queryString);
		Query query = em.createQuery(queryString,Personne.class);
		
		for (Map.Entry me : parameters.entrySet()) {
	          if(me.getKey().equals("n") && !me.getValue().equals("")) {query.setParameter("nomF", "%"+nom+"%");}
	          if(me.getKey().equals("p") && !me.getValue().equals("")) {query.setParameter("prenomF","%"+ prenom+"%");}
	          if(me.getKey().equals("a") && !me.getValue().equals("")) {query.setParameter("titreF","%"+activiteTitre+"%");}
	        }
		try {
			List<Personne> results = query.getResultList();
			return results;

		}catch(NoResultException e) {
			/*WHAT ???*/
			return new ArrayList<Personne>();
		}

		
	}

	@Override
	public Personne getPersonneByEmail(String email) {

		Query query = em.createNamedQuery("findPersonneByEmail", Personne.class);
		query.setParameter("email", email);
		try {
			return (Personne) query.getSingleResult();

		}catch(NoResultException e) {
			return null;
		}
	}



	@Override
	public List<Personne> getFilteredData(Map<String,String> filters, Map<String, String> activiteFilters,int first,int pageSize) {
		// TODO Auto-generated method stub
		org.hibernate.Session session =em.unwrap(Session.class);
		applyFilter(session, filters);
		if(!activiteFilters.isEmpty()) {
			String queryString = "select distinct idperson from personne_activite pa left join activite a on pa.activites_idActivity = a.idActivity where";
			if( activiteFilters.get("activity")!=null) {
				queryString += " a.nature="+NatureActivite.valueOf(activiteFilters.get("activity")).ordinal()+" and ";
			}
			queryString += "a.description LIKE '%"+activiteFilters.getOrDefault("description","")+"%'";
			
			Query tst = em.createNativeQuery(queryString);
			tst.setMaxResults(1000);
			List<Long>  a = tst.getResultList();
			if(a.size()==0) return null;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Personne> cq = cb.createQuery(Personne.class);
			Root<Personne> root = cq.from(Personne.class);
			CriteriaQuery<Personne> vrai = cq.where(root.get("idPerson").in(a));
			//Query vrai = em.createQuery("select p from Personne p where p.idPerson in (1,2,3,4,5,6,7,8,9,10) ");
			//Query vrai = em.createNamedQuery("findAllPersonnesIn");
			TypedQuery<Personne> allQuery = em.createQuery(vrai);
	        return allQuery.getResultList();
		}else {
			Query query = em.createNamedQuery("findAllPersonnes",Personne.class);

			return query.getResultList();

		}
		//	query.setMaxResults(1000);
		
		
	}
	@Override
	public int countAllPersonne(Map<String, String> filters, Map<String, String> activiteFilters) {
		// TODO Auto-generated method stub
		org.hibernate.Session session =em.unwrap(Session.class);
		applyFilter(session, filters);
		// 		session.enableFilter("prenom").setParameter("value", "%po%");
	//	System.out.println(		session.getEnabledFilter("prenom").getFilterDefinition());
		if(!activiteFilters.isEmpty()) {
			String queryString = "select count(distinct idperson) from personne_activite pa left join activite a on pa.activites_idActivity = a.idActivity where";
			if( activiteFilters.get("activity")!=null) {
				queryString += " a.nature="+NatureActivite.valueOf(activiteFilters.get("activity")).ordinal()+" and ";
			}
			queryString += "a.description LIKE '%"+activiteFilters.getOrDefault("description","")+"%'";
			
			System.out.println(queryString);
			Query tst = em.createNativeQuery(queryString);
			List liste = tst.getResultList();
			for(Object o:liste) {
				System.out.println(o.toString());
				return Integer.parseInt(o.toString()) ;
			}
		}
		Query query = em.createNamedQuery("countAllPersonnes");

		List liste = query.getResultList();
		
		for(Object o:liste) {
			System.out.println(o.toString());
			return Integer.parseInt(o.toString()) ;
		}
		return 0;
	}
	
	private void applyFilter(Session session, Map<String, String> filters) {
		for(String column:filters.keySet()) {
			System.out.println(column+"=="+filters.get(column));
			if(column=="activity") {
				session.enableFilter(column).setParameter("value", filters.get(column));
			}else {
				session.enableFilter(column).setParameter("value", "%"+filters.get(column)+"%");

			}
		}
	}

}
