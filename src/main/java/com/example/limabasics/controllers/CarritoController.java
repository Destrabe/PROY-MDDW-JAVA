package com.example.limabasics.controllers;

import com.example.limabasics.entities.CarritoItem;
import com.example.limabasics.entities.Pedido;
import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.CarritoItemRepository;
import com.example.limabasics.repositories.PedidoRepository;
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
public class CarritoController {

    private final ProductoRepository productoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PedidoRepository pedidoRepository;

    public CarritoController(ProductoRepository productoRepository,
                               CarritoItemRepository carritoItemRepository,
                               PedidoRepository pedidoRepository) {
        this.productoRepository = productoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.pedidoRepository = pedidoRepository;
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

    // Convertimos la clase CSS al nombre real del color para guardarlo en la orden
    private String nombrarColor(String claseColor) {
        if (claseColor == null) return "Clásico";
        switch (claseColor.trim().toLowerCase()) {
            case "bg-white": return "Blanco";
            case "bg-black": return "Negro";
            case "bg-gray-200": return "Gris Claro";
            case "bg-gray-400": return "Gris";
            case "bg-gray-500": return "Gris Oscuro";
            case "bg-red-500": 
            case "bg-[#ff0000]": return "Rojo";
            case "bg-[#8b7355]": return "Marrón";
            case "bg-[#6b705c]": return "Verde Kaki";
            case "bg-[#1b263b]": return "Azul Marino";
            case "bg-[#ddbea9]": return "Beige";
            case "bg-[#fbff00]": return "Amarillo";
            default: return "Clásico";
        }
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

    @GetMapping("/cart")
    public String verCarrito(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<CarritoItem> carrito = (usuarioId != null)
                ? carritoItemRepository.findByUsuarioId(usuarioId)
                : Collections.emptyList();

        sincronizarContadoresSesion(session);
        model.addAttribute("items", carrito);
        return "cart";
    }

    @GetMapping("/checkout")
    public String verCheckout(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/";
        }
        List<CarritoItem> carrito = carritoItemRepository.findByUsuarioId(usuarioId);
        if (carrito.isEmpty()) {
            return "redirect:/cart";
        }
        sincronizarContadoresSesion(session);
        model.addAttribute("items", carrito);
        return "checkout";
    }

    @PostMapping("/checkout/procesar")
    @ResponseBody
    public Map<String, Object> procesarCompra(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId != null) {
            List<CarritoItem> items = carritoItemRepository.findByUsuarioId(usuarioId);
            if (!items.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                double subtotalItems = 0;
                
                // Formato para Figma: ImagenUrl|Nombre|Talla|Color|Cantidad|Precio
                for (CarritoItem item : items) {
                    sb.append(item.getProducto().getImagen()).append("|")
                      .append(item.getProducto().getNombre()).append("|")
                      .append(item.getTalla()).append("|")
                      .append(nombrarColor(item.getColor())).append("|")
                      .append(item.getCantidad()).append("|")
                      .append(String.format(java.util.Locale.US, "%.2f", (double)item.getSubtotal())).append(";");
                    subtotalItems += item.getSubtotal();
                }

                double totalFinal = subtotalItems + 8.0;

                Pedido nuevoPedido = new Pedido(usuarioId, sb.toString(), totalFinal);
                pedidoRepository.save(nuevoPedido);

                carritoItemRepository.deleteAll(items);
                sincronizarContadoresSesion(session);
                
                response.put("success", true);
            } else {
                response.put("success", false);
            }
        } else {
            response.put("success", false);
        }
        return response;
    }

    @GetMapping("/pedidos")
    public String verMisPedidos(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/";
        }
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        model.addAttribute("pedidos", pedidos);
        sincronizarContadoresSesion(session);
        return "orders";
    }

    @PostMapping("/cart/agregar")
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

    @PostMapping("/cart/agregar-ajax")
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

    @PostMapping("/cart/eliminar")
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