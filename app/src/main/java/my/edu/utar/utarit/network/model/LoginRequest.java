package my.edu.utar.utarit.network.model;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){ return email; }
    public String getPassword(){ return password; }
}
