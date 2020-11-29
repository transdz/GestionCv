package gestion.entities;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Past;

import org.hibernate.annotations.GenerationTime;

import com.sun.istack.NotNull;

@NamedQueries({ @NamedQuery(name = "findAllPersonnes", query = "Select p from Personne p"),
		@NamedQuery(name = "findPersonneByEmail", query = "Select p from Personne p where p.email= :email") })
@Entity
public class Personne implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue()
	Long id;

	@Column
	@NotNull
	String nom;

	@Column
	@NotNull
	String prenom;

	@Column
	String siteweb;

	@Column
	String email;

	@Column
	String password;

	@Column
	@Past
	Date dateNaissance;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<Personne> cooptations = new ArrayList<Personne>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Activite> activites = new ArrayList<Activite>();

	public Personne() {
		// TODO Auto-generated constructor stub
		this.nom = "";
		this.prenom = "";
		this.siteweb = "";
		this.email = "";
		this.password = "";

	}

	public Personne(String nom, String prenom, String siteWeb, String email, String password, Date dateNaissance) {
		// TODO Auto-generated constructor stub
		this.nom = nom;
		this.prenom = prenom;
		this.siteweb = siteWeb;
		this.email = email;
		this.password = password;
		this.dateNaissance = dateNaissance;
		this.cooptations = new ArrayList<Personne>();
	}

	public void addCooptation(Personne personne) {
		this.cooptations.add(personne);
	}

	public void addActivite(Activite activite) {
		activite.setPersonne(this);
		this.activites.add(activite);
	}

	public void removeActivite(Activite activite) {
		this.activites.remove(activite);
		activite.setPersonne(null);
	}

	public List<Activite> getActivities() {
		return this.activites;
	}

	public Long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	public String getNom() {
		// TODO Auto-generated method stub
		return this.nom;
	}

	public boolean verifyPassword(String password2) {
		// TODO Auto-generated method stub
		return this.password.equals(password2);
	}

	public List<Personne> getCoptations() {
		// TODO Auto-generated method stub
		return this.cooptations;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}

	// J'ai rajouté le restant de setters et getters avec génération auto
	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getSiteweb() {
		return siteweb;
	}

	public void setSiteweb(String siteweb) {
		this.siteweb = siteweb;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public List<Personne> getCooptations() {
		return cooptations;
	}

	public void setCooptations(List<Personne> cooptations) {
		this.cooptations = cooptations;
	}

	public List<Activite> getActivites() {
		return activites;
	}

	public void setActivites(List<Activite> activites) {
		this.activites = activites;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
