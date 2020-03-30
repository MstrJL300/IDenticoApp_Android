package co.com.identico.appcarnet3.Models;

import java.io.Serializable;

/**
 * Created by Miguel Forero on 26/01/2018.
 */

public class Carnet implements Serializable{

    private int ID;
    private String CarnetId;
    private String IdenticoTypeLicense;
    private String IdenticoEnviado;
    private String IdenticoDescargado;
    private String IdenticoConfirmado;
    private String IdenticoEstado;
    private String IdenticoCodigo;
    private String IdenticoFechaInicial;
    private String IdenticoFechaVencimiento;
    private String IdenticoNombreCliente;
    private String IdenticoNombreCarnet;
    private String IdenticoDocumento;
    private String IdenticoEmail;
    private String IdenticoTabla;
    private String Data;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCarnetId() {
        return CarnetId;
    }

    public void setCarnetId(String carnetId) {
        CarnetId = carnetId;
    }

    public String getIdenticoTypeLicense() {
        return IdenticoTypeLicense;
    }

    public void setIdenticoTypeLicense(String identicoTypeLicense) {
        IdenticoTypeLicense = identicoTypeLicense;
    }

    public String getIdenticoEnviado() {
        return IdenticoEnviado;
    }

    public void setIdenticoEnviado(String identicoEnviado) {
        IdenticoEnviado = identicoEnviado;
    }

    public String getIdenticoDescargado() {
        return IdenticoDescargado;
    }

    public void setIdenticoDescargado(String identicoDescargado) {
        IdenticoDescargado = identicoDescargado;
    }

    public String getIdenticoConfirmado() {
        return IdenticoConfirmado;
    }

    public void setIdenticoConfirmado(String identicoConfirmado) {
        IdenticoConfirmado = identicoConfirmado;
    }

    public String getIdenticoEstado() {
        return IdenticoEstado;
    }

    public void setIdenticoEstado(String identicoEstado) {
        IdenticoEstado = identicoEstado;
    }

    public String getIdenticoCodigo() {
        return IdenticoCodigo;
    }

    public void setIdenticoCodigo(String identicoCodigo) {
        IdenticoCodigo = identicoCodigo;
    }

    public String getIdenticoFechaInicial() {
        return IdenticoFechaInicial;
    }

    public void setIdenticoFechaInicial(String identicoFechaInicial) {
        IdenticoFechaInicial = identicoFechaInicial;
    }

    public String getIdenticoFechaVencimiento() {
        return IdenticoFechaVencimiento;
    }

    public void setIdenticoFechaVencimiento(String identicoFechaVencimiento) {
        IdenticoFechaVencimiento = identicoFechaVencimiento;
    }

    public String getIdenticoNombreCliente() {
        return IdenticoNombreCliente;
    }

    public void setIdenticoNombreCliente(String identicoNombreCliente) {
        IdenticoNombreCliente = identicoNombreCliente;
    }

    public String getIdenticoNombreCarnet() {
        return IdenticoNombreCarnet;
    }

    public void setIdenticoNombreCarnet(String identicoNombreCarnet) {
        IdenticoNombreCarnet = identicoNombreCarnet;
    }

    public String getIdenticoDocumento() {
        return IdenticoDocumento;
    }

    public void setIdenticoDocumento(String identicoDocumento) {
        IdenticoDocumento = identicoDocumento;
    }

    public String getIdenticoEmail() {
        return IdenticoEmail;
    }

    public void setIdenticoEmail(String identicoEmail) {
        IdenticoEmail = identicoEmail;
    }

    public String getIdenticoTabla() {
        return IdenticoTabla;
    }

    public void setIdenticoTabla(String identicoTabla) {
        IdenticoTabla = identicoTabla;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public Carnet(int ID, String carnetId, String identicoTypeLicense, String identicoEnviado, String identicoDescargado, String identicoConfirmado, String identicoEstado, String identicoCodigo, String identicoFechaInicial, String identicoFechaVencimiento, String identicoNombreCliente, String identicoNombreCarnet, String identicoDocumento, String identicoEmail, String identicoTabla, String data) {
        this.ID = ID;
        CarnetId = carnetId;
        IdenticoTypeLicense = identicoTypeLicense;
        IdenticoEnviado = identicoEnviado;
        IdenticoDescargado = identicoDescargado;
        IdenticoConfirmado = identicoConfirmado;
        IdenticoEstado = identicoEstado;
        IdenticoCodigo = identicoCodigo;
        IdenticoFechaInicial = identicoFechaInicial;
        IdenticoFechaVencimiento = identicoFechaVencimiento;
        IdenticoNombreCliente = identicoNombreCliente;
        IdenticoNombreCarnet = identicoNombreCarnet;
        IdenticoDocumento = identicoDocumento;
        IdenticoEmail = identicoEmail;
        IdenticoTabla = identicoTabla;
        Data = data;
    }

    public Carnet() {

    }

}
