package br.dev.guereguere.dto;

import java.io.Serializable;

public class LoginRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String senha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    //    public String getEmail(String keySecret) {
//        return AdvancedEncryptionStandardUtil.decrypt(email,keySecret);
//    }
//
//    public void setEmail(String email,String keySecret) {
//        this.email = AdvancedEncryptionStandardUtil.encrypt(email,keySecret);
//    }
//
//    public String getSenha(String keySecret) {
//        return AdvancedEncryptionStandardUtil.decrypt(senha,keySecret);
//    }
//
//    public void setSenha(String senha,String keySecret) {
//        this.senha = AdvancedEncryptionStandardUtil.encrypt(senha,keySecret);
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getSenha() {
//        return senha;
//    }
//
//    public void setSenha(String senha) {
//        this.senha = senha;
//    }
}
