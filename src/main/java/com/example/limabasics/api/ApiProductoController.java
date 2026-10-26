package com.example.limabasics.api;

import com.example.limabasics.dto.ProductoDTO;
import com.example.limabasics.entities.Producto;
import com.example.limabasics.repositories.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ApiProductoController {

    private final ProductoRepository productoRepository;

    public ApiProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO productoDto) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoDto.nombre());
        nuevoProducto.setCategoria(productoDto.categoria());
        nuevoProducto.setPrecio(productoDto.precio());
        nuevoProducto.setImagen(productoDto.imagen());

        Producto guardado = productoRepository.save(nuevoProducto);
        return ResponseEntity.ok(guardado);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoDetalles) {
        Optional<Producto> productoOpt = productoRepository.findById(id);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setNombre(productoDetalles.getNombre());
            producto.setCategoria(productoDetalles.getCategoria());
            producto.setPrecio(productoDetalles.getPrecio());
            producto.setImagen(productoDetalles.getImagen());

            Producto productoActualizado = productoRepository.save(producto);
            return ResponseEntity.ok(productoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build(); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}