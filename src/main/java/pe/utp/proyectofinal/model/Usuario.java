package pe.utp.proyectofinal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id // Llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable
    private Long id;

    @Column(nullable = false, unique = true) // No nulo y no se puede repetir
    private String email;

    @Column(nullable = false, unique = true) // Acá también
    private String nombre;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol = "USER";

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}