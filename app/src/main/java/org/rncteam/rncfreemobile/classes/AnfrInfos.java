package org.rncteam.rncfreemobile.classes;

import org.json.JSONArray;

/**
 * Created by cedric_f25 on 21/09/2015.
 */
public class AnfrInfos {
    private static final String TAG = "AnfrInfos";

    private String lieu;
    private String add1;
    private String add2;
    private String add3;
    private String cp;
    private String commune;

    private String hauteur;
    private String implantation;
    private String modification;
    private String activation;
    private String typeSupport;

    private String proprietaire;

    private Rnc rnc;

    private JSONArray azimuts;

    public String getLieu() {
        return lieu;
    }
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getAdd1() {
        return add1;
    }
    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return add2;
    }
    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getAdd3() {
        return add3;
    }
    public void setAdd3(String add3) {
        this.add3 = add3;
    }

    public String getCp() {
        return cp;
    }
    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCommune() {
        return commune;
    }
    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getHauteur() {
        return hauteur;
    }
    public void setHauteur(String hauteur) {
        this.hauteur = hauteur;
    }

    public String getImplantation() {
        return implantation;
    }
    public void setImplantation(String implantation) {
        this.implantation = implantation;
    }

    public String getModification() {
        return modification;
    }
    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getActivation() {
        return activation;
    }
    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getTypeSupport() {
        return typeSupport;
    }
    public void setTypeSupport(String typeSupport) {
        this.typeSupport = typeSupport;
    }

    public String getProrietaire() {
        return proprietaire;
    }
    public void setProrietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public JSONArray getAzimuts() {
        return azimuts;
    }
    public void setAzimuts(JSONArray azimuts) {
        this.azimuts = azimuts;
    }

    public Rnc getRnc() {
        return rnc;
    }
    public void setRnc(Rnc rnc) {
        this.rnc = rnc;
    }
}
