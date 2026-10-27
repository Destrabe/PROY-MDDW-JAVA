package com.example.limabasics.controllers;

import com.example.limabasics.entities.CarritoItem;
import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.CarritoItemRepository;
import com.example.limabasics.repositories.ProductoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping({"/cart", "/carrito"})
public class CarritoController {

    private final ProductoRepository productoRepository;
    private final CarritoItemRepository carritoItemRepository;

    public CarritoController(ProductoRepository productoRepository, CarritoItemRepository carritoItemRepository) {
        this.productoRepository = productoRepository;
        this.carritoItemRepository = carritoItemRepository;
    }

    public void sincronizarContadoresSesion(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            session.setAttribute("carritoCantidad", 0);
            session.setAttribute("carritoTotal", 0);
            return;
        }
        List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuarioId);
        int totalItems = items.stream().mapToInt(CarritoItem::getCantidad).sum();
        int totalPrecio = items.stream().mapToInt(CarritoItem::getSubtotal).sum();
        session.setAttribute("carritoCantidad", totalItems);
        session.setAttribute("carritoTotal", totalPrecio);
    }

    private void guardarOActualizarItem(Long usuarioId, Producto producto, String color, String talla) {
        String colorLimpio = (color != null && !color.isEmpty()) ? color : "bg-black";
        String tallaLimpia = (talla != null && !talla.isEmpty()) ? talla : "M";

        Optional<CarritoItem> existente = carritoItemRepository
                .findByUsuarioIdAndProducto_IdAndColorAndTalla(usuarioId, producto.getId(), colorLimpio, tallaLimpia);

        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + 1);
            carritoItemRepository.save(item);
        } else {
            CarritoItem nuevo = new CarritoItem(usuarioId, producto, 1, colorLimpio, tallaLimpia);
            carritoItemRepository.save(nuevo);
        }
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<CarritoItem> carrito = (usuarioId != null)
                ? carritoItemRepository.findByUsuarioId(usuarioId)
                : Collections.emptyList();

        sincronizarContadoresSesion(session);
        model.addAttribute("items", carrito);
        return "cart";
    }

    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam Long productoId,
            @RequestParam(required = false, defaultValue = "bg-black") String color,
            @RequestParam(required = false, defaultValue = "M") String talla,
            HttpSession session,
            HttpServletRequest request) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (usuarioId != null && producto != null) {
            guardarOActualizarItem(usuarioId, producto, color, talla);
            sincronizarContadoresSesion(session);
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/catalog");
    }

    @PostMapping("/agregar-ajax")
    @ResponseBody
    public Map<String, Object> agregarProductoAjax(
            @RequestParam Long productoId,
            @RequestParam(required = false, defaultValue = "bg-black") String color,
            @RequestParam(required = false, defaultValue = "M") String talla,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (usuarioId != null && producto != null) {
            guardarOActualizarItem(usuarioId, producto, color, talla);
            sincronizarContadoresSesion(session);

            response.put("success", true);
            response.put("carritoCantidad", session.getAttribute("carritoCantidad"));
            response.put("carritoTotal", session.getAttribute("carritoTotal"));
        } else {
            response.put("success", false);
        }

        return response;
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(
            @RequestParam Long productoId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String talla,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId != null) {
            List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuarioId);
            for (CarritoItem item : items) {
                if (item.getProducto().getId().equals(productoId) &&
                        (color == null || item.getColor().equals(color)) &&
                        (talla == null || item.getTalla().equals(talla))) {
                    carritoItemRepository.delete(item);
                }
            }
            sincronizarContadoresSesion(session);
        }
        return "redirect:/cart";
    }
}