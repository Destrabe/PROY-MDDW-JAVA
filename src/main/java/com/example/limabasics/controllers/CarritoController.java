package com.example.limabasics.controllers;

import com.example.limabasics.entities.CarritoItem;
import com.example.limabasics.entities.DetallePedido;
import com.example.limabasics.entities.Pedido;
import com.example.limabasics.entities.Producto;
import com.example.limabasics.entities.Usuario;
import com.example.limabasics.repositories.CarritoItemRepository;
import com.example.limabasics.repositories.PedidoRepository;
import com.example.limabasics.repositories.ProductoRepository;
import com.example.limabasics.repositories.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public CarritoController(ProductoRepository productoRepository,
            CarritoItemRepository carritoItemRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void sincronizarContadoresSesion(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            session.setAttribute("carritoCantidad", 0);
            session.setAttribute("carritoTotal", 0);
            return;
        }
        List<CarritoItem> items = carritoItemRepository.findByUsuario_Id(usuarioId);
        int totalItems = items.stream().mapToInt(CarritoItem::getCantidad).sum();
        int totalPrecio = items.stream().mapToInt(CarritoItem::getSubtotal).sum();
        session.setAttribute("carritoCantidad", totalItems);
        session.setAttribute("carritoTotal", totalPrecio);
    }

    private String nombrarColor(String claseColor) {
        if (claseColor == null)
            return "Clásico";
        switch (claseColor.trim().toLowerCase()) {
            case "bg-white":
                return "Blanco";
            case "bg-black":
                return "Negro";
            case "bg-gray-200":
                return "Gris Claro";
            case "bg-gray-400":
                return "Gris";
            case "bg-gray-500":
                return "Gris Oscuro";
            case "bg-red-500":
            case "bg-[#ff0000]":
                return "Rojo";
            case "bg-[#8b7355]":
                return "Marrón";
            case "bg-[#6b705c]":
                return "Verde Kaki";
            case "bg-[#1b263b]":
                return "Azul Marino";
            case "bg-[#ddbea9]":
                return "Beige";
            case "bg-[#fbff00]":
                return "Amarillo";
            default:
                return "Clásico";
        }
    }

    private void guardarOActualizarItem(Usuario usuario, Producto producto, String color, String talla) {
        String colorLimpio = (color != null && !color.isEmpty()) ? color : "bg-black";
        String tallaLimpia = (talla != null && !talla.isEmpty()) ? talla : "M";

        Optional<CarritoItem> existente = carritoItemRepository
                .findByUsuario_IdAndProducto_IdAndColorAndTalla(usuario.getId(), producto.getId(), colorLimpio,
                        tallaLimpia);

        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + 1);
            carritoItemRepository.save(item);
        } else {
            CarritoItem nuevo = new CarritoItem(usuario, producto, 1, colorLimpio, tallaLimpia);
            carritoItemRepository.save(nuevo);
        }
    }

    @GetMapping("/cart")
    public String verCarrito(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        List<CarritoItem> carrito = (usuarioId != null)
                ? carritoItemRepository.findByUsuario_Id(usuarioId)
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
        List<CarritoItem> carrito = carritoItemRepository.findByUsuario_Id(usuarioId);
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
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            List<CarritoItem> items = carritoItemRepository.findByUsuario_Id(usuarioId);

            if (usuario != null && !items.isEmpty()) {
                double subtotalItems = 0;

                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setUsuario(usuario);
                nuevoPedido.setEstado("Confirmado");

                for (CarritoItem item : items) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setProducto(item.getProducto());
                    detalle.setCantidad(item.getCantidad());
                    detalle.setColor(nombrarColor(item.getColor()));
                    detalle.setTalla(item.getTalla());
                    detalle.setSubtotal((double) item.getSubtotal());

                    nuevoPedido.addDetalle(detalle);
                    subtotalItems += item.getSubtotal();
                }

                double totalFinal = subtotalItems + 8.0;
                nuevoPedido.setTotal(totalFinal);

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
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaCreacionDesc();
        pedidos.removeIf(p -> !p.getUsuario().getId().equals(usuarioId));
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
        Usuario usuario = (usuarioId != null) ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (usuario != null && producto != null) {
            guardarOActualizarItem(usuario, producto, color, talla);
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
        Usuario usuario = (usuarioId != null) ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (usuario != null && producto != null) {
            guardarOActualizarItem(usuario, producto, color, talla);
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
            List<CarritoItem> items = carritoItemRepository.findByUsuario_Id(usuarioId);
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