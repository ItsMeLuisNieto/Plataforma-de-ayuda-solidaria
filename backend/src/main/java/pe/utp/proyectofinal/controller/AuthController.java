package pe.utp.proyectofinal.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import pe.utp.proyectofinal.dto.RegistroRequest;
import pe.utp.proyectofinal.exception.BusinessException;
import pe.utp.proyectofinal.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public String registrarUsuario(@Valid @ModelAttribute RegistroRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/registro?error=" + mapearError(result);
        }

        try {
            usuarioService.registrar(request);
            return "redirect:/login";
        } catch (BusinessException ex) {
            return "redirect:/registro?error=" + ex.getCode();
        } catch (DataIntegrityViolationException ex) {
            return "redirect:/registro?error=nombre_exists";
        }
    }

    private String mapearError(BindingResult result) {
        if (result.hasFieldErrors("email")) {
            return "null_email";
        }
        if (result.hasFieldErrors("nombre")) {
            return "invalid_name";
        }
        return "pass_mismatch";
    }
}
