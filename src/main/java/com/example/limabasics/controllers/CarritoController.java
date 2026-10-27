package com.example.limabasics.controllers;

import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.ProductoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/cart", "/carrito"})
public class CarritoController {

    private final ProductoRepository productoRepository;

    public CarritoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public static class ItemCarrito {
        private Producto producto;
        private int cantidad;
        private String color;
        private String talla;

        public ItemCarrito(Producto producto, int cantidad, String color, String talla) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.color = (color != null && !color.isEmpty()) ? color : "bg-black";
            this.talla = (talla != null && !talla.isEmpty()) ? talla : "M";
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getTalla() { return talla; }
        public void setTalla(String talla) { this.talla = talla; }
        public int getSubtotal() { return producto.getPrecio() * cantidad; }
    }

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }

    private void actualizarContadores(HttpSession session, List<ItemCarrito> carrito) {
        int totalItems = carrito.stream().mapToInt(ItemCarrito::getCantidad).sum();
        int totalPrecio = carrito.stream().mapToInt(ItemCarrito::getSubtotal).sum();
        session.setAttribute("carritoCantidad", totalItems);
        session.setAttribute("carritoTotal", totalPrecio);
    }

    private void procesarAgregar(Long productoId, String color, String talla, HttpSession session) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            List<ItemCarrito> carrito = obtenerCarrito(session);
            boolean encontrado = false;

            for (ItemCarrito item : carrito) {
                if (item.getProducto().getId().equals(productoId)
                        && item.getColor().equals(color)
                        && item.getTalla().equals(talla)) {
                    item.setCantidad(item.getCantidad() + 1);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                carrito.add(new ItemCarrito(producto, 1, color, talla));
            }
            actualizarContadores(session, carrito);
        }
    }

    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        List<ItemCarrito> carrito = obtenerCarrito(session);
        actualizarContadores(session, carrito);
        model.addAttribute("items", carrito);
        return "cart";
    }

    // Nuevo método AJAX para agregar sin recargar la página
    @PostMapping("/agregar-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarProductoAjax(
            @RequestParam Long productoId,
            @RequestParam(required = false, defaultValue = "bg-black") String color,
            @RequestParam(required = false, defaultValue = "M") String talla,
            HttpSession session) {

        procesarAgregar(productoId, color, talla, session);

        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("carritoCantidad", session.getAttribute("carritoCantidad"));
        response.put("carritoTotal", session.getAttribute("carritoTotal"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam Long productoId,
            @RequestParam(required = false, defaultValue = "bg-black") String color,
            @RequestParam(required = false, defaultValue = "M") String talla,
            HttpSession session,
            HttpServletRequest request) {

        procesarAgregar(productoId, color, talla, session);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/catalog");
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(
            @RequestParam Long productoId,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String talla,
            HttpSession session) {

        List<ItemCarrito> carrito = obtenerCarrito(session);
        carrito.removeIf(item ->
                item.getProducto().getId().equals(productoId) &&
                (color == null || item.getColor().equals(color)) &&
                (talla == null || item.getTalla().equals(talla))
        );
        actualizarContadores(session, carrito);
        return "redirect:/cart";
    }
}