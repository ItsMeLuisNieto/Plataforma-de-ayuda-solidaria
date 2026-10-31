package pe.utp.proyectofinal.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuarios")
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String nombre;

    @JsonIgnore
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol = "USUARIO";

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Campana> campanas = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Testimonio> testimonios = new ArrayList<>();

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
    public List<Campana> getCampanas() { return campanas; }
    public void setCampanas(List<Campana> campanas) { this.campanas = campanas; }
    public List<Testimonio> getTestimonios() { return testimonios; }
    public void setTestimonios(List<Testimonio> testimonios) { this.testimonios = testimonios; }
}
