package reservas;

import reservas.modelo.administrador;
import reservas.modelo.categoria;
import reservas.modelo.estadoReserva;
import reservas.modelo.funcionario;
import reservas.modelo.recurso;
import reservas.modelo.reserva;
import reservas.modelo.rolUsuario;
import reservas.modelo.usuario;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class persistenciaXml {

    private static final String ARCHIVO = "datos.xml";

    private persistenciaXml() {
    }

    public static void guardar() {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor = fabrica.newDocumentBuilder();
            Document documento = constructor.newDocument();

            Element raiz = documento.createElement("sistemaReservas");
            documento.appendChild(raiz);

            Element nodoUsuarios = documento.createElement("usuarios");
            raiz.appendChild(nodoUsuarios);
            for (usuario usuario : almacenDatos.usuarios) {
                Element nodoUsuario = documento.createElement("usuario");
                nodoUsuario.setAttribute("id", usuario.getId());
                nodoUsuario.setAttribute("clave", usuario.getClave());
                nodoUsuario.setAttribute("rol", usuario.getRol().name());
                if (usuario instanceof funcionario funcionario) {
                    nodoUsuario.setAttribute("nombre", funcionario.getNombre());
                    nodoUsuario.setAttribute("telefono", funcionario.getTelefono());
                }
                nodoUsuarios.appendChild(nodoUsuario);
            }

            Element nodoCategorias = documento.createElement("categorias");
            raiz.appendChild(nodoCategorias);
            for (categoria categoria : almacenDatos.categorias) {
                Element nodoCategoria = documento.createElement("categoria");
                nodoCategoria.setAttribute("id", categoria.getId());
                nodoCategoria.setAttribute("descripcion", categoria.getDescripcion());
                nodoCategorias.appendChild(nodoCategoria);
            }

            Element nodoRecursos = documento.createElement("recursos");
            raiz.appendChild(nodoRecursos);
            for (recurso recurso : almacenDatos.recursos) {
                Element nodoRecurso = documento.createElement("recurso");
                nodoRecurso.setAttribute("id", recurso.getId());
                nodoRecurso.setAttribute("categoriaId", recurso.getCategoria().getId());
                nodoRecurso.setAttribute("descripcion", recurso.getDescripcion());
                nodoRecursos.appendChild(nodoRecurso);
            }

            Element nodoReservas = documento.createElement("reservas");
            raiz.appendChild(nodoReservas);
            for (reserva reserva : almacenDatos.reservas) {
                Element nodoReserva = documento.createElement("reserva");
                nodoReserva.setAttribute("id", reserva.getId());
                nodoReserva.setAttribute("actividad", reserva.getActividad());
                nodoReserva.setAttribute("fecha", reserva.getFecha().toString());
                nodoReserva.setAttribute("horaInicio", reserva.getHoraInicio().toString());
                nodoReserva.setAttribute("horaFin", reserva.getHoraFin().toString());
                nodoReserva.setAttribute("estado", reserva.getEstado().name());
                nodoReserva.setAttribute("funcionarioId", reserva.getFuncionario().getId());
                for (recurso recursoAsignado : reserva.getRecursosAsignados()) {
                    Element nodoRecursoAsignado = documento.createElement("recursoAsignado");
                    nodoRecursoAsignado.setAttribute("id", recursoAsignado.getId());
                    nodoReserva.appendChild(nodoRecursoAsignado);
                }
                nodoReservas.appendChild(nodoReserva);
            }

            Element nodoSecuencia = documento.createElement("siguienteNumeroReserva");
            nodoSecuencia.setAttribute("valor", String.valueOf(almacenDatos.obtenerSiguienteNumeroReserva()));
            raiz.appendChild(nodoSecuencia);

            Transformer transformador = TransformerFactory.newInstance().newTransformer();
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            transformador.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformador.transform(new DOMSource(documento), new StreamResult(new File(ARCHIVO)));
        } catch (Exception excepcion) {
            System.err.println("No se pudo guardar " + ARCHIVO + ": " + excepcion.getMessage());
        }
    }

    public static boolean cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return false;
        }
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor = fabrica.newDocumentBuilder();
            Document documento = constructor.parse(archivo);
            documento.getDocumentElement().normalize();

            List<usuario> usuariosCargados = new ArrayList<>();
            NodeList nodosUsuario = documento.getElementsByTagName("usuario");
            for (int i = 0; i < nodosUsuario.getLength(); i++) {
                Element nodoUsuario = (Element) nodosUsuario.item(i);
                String id = nodoUsuario.getAttribute("id");
                String clave = nodoUsuario.getAttribute("clave");
                rolUsuario rol = rolUsuario.valueOf(nodoUsuario.getAttribute("rol"));
                if (rol == rolUsuario.ADMINISTRADOR) {
                    usuariosCargados.add(new administrador(id, clave));
                } else {
                    String nombre = nodoUsuario.getAttribute("nombre");
                    String telefono = nodoUsuario.getAttribute("telefono");
                    usuariosCargados.add(new funcionario(id, clave, nombre, telefono));
                }
            }

            List<categoria> categoriasCargadas = new ArrayList<>();
            NodeList nodosCategoria = documento.getElementsByTagName("categoria");
            for (int i = 0; i < nodosCategoria.getLength(); i++) {
                Element nodoCategoria = (Element) nodosCategoria.item(i);
                categoriasCargadas.add(new categoria(nodoCategoria.getAttribute("id"),
                        nodoCategoria.getAttribute("descripcion")));
            }

            List<recurso> recursosCargados = new ArrayList<>();
            NodeList nodosRecurso = documento.getElementsByTagName("recurso");
            for (int i = 0; i < nodosRecurso.getLength(); i++) {
                Element nodoRecurso = (Element) nodosRecurso.item(i);
                categoria categoriaDelRecurso = buscarCategoriaPorId(categoriasCargadas, nodoRecurso.getAttribute("categoriaId"));
                recursosCargados.add(new recurso(nodoRecurso.getAttribute("id"), categoriaDelRecurso,
                        nodoRecurso.getAttribute("descripcion")));
            }

            List<reserva> reservasCargadas = new ArrayList<>();
            NodeList nodosReserva = documento.getElementsByTagName("reserva");
            for (int i = 0; i < nodosReserva.getLength(); i++) {
                Element nodoReserva = (Element) nodosReserva.item(i);
                funcionario funcionarioDeLaReserva =
                        (funcionario) buscarUsuarioPorId(usuariosCargados, nodoReserva.getAttribute("funcionarioId"));
                LocalDate fecha = LocalDate.parse(nodoReserva.getAttribute("fecha"));
                LocalTime horaInicio = LocalTime.parse(nodoReserva.getAttribute("horaInicio"));
                LocalTime horaFin = LocalTime.parse(nodoReserva.getAttribute("horaFin"));
                estadoReserva estado = estadoReserva.valueOf(nodoReserva.getAttribute("estado"));

                List<recurso> recursosAsignados = new ArrayList<>();
                NodeList nodosRecursoAsignado = nodoReserva.getElementsByTagName("recursoAsignado");
                for (int j = 0; j < nodosRecursoAsignado.getLength(); j++) {
                    Element nodoRecursoAsignado = (Element) nodosRecursoAsignado.item(j);
                    recurso recursoAsignado = buscarRecursoPorId(recursosCargados, nodoRecursoAsignado.getAttribute("id"));
                    if (recursoAsignado != null) {
                        recursosAsignados.add(recursoAsignado);
                    }
                }

                reservasCargadas.add(new reserva(nodoReserva.getAttribute("id"), nodoReserva.getAttribute("actividad"),
                        fecha, horaInicio, horaFin, estado, funcionarioDeLaReserva, recursosAsignados));
            }

            int siguienteNumero = 1;
            NodeList nodosSecuencia = documento.getElementsByTagName("siguienteNumeroReserva");
            if (nodosSecuencia.getLength() > 0) {
                Element nodoSecuencia = (Element) nodosSecuencia.item(0);
                siguienteNumero = Integer.parseInt(nodoSecuencia.getAttribute("valor"));
            }

            almacenDatos.usuarios.clear();
            almacenDatos.usuarios.addAll(usuariosCargados);
            almacenDatos.categorias.clear();
            almacenDatos.categorias.addAll(categoriasCargadas);
            almacenDatos.recursos.clear();
            almacenDatos.recursos.addAll(recursosCargados);
            almacenDatos.reservas.clear();
            almacenDatos.reservas.addAll(reservasCargadas);
            almacenDatos.establecerSiguienteNumeroReserva(siguienteNumero);
            return true;
        } catch (Exception excepcion) {
            System.err.println("No se pudo cargar " + ARCHIVO + ", se usan los datos de ejemplo: "
                    + excepcion.getMessage());
            return false;
        }
    }

    private static categoria buscarCategoriaPorId(List<categoria> categorias, String id) {
        for (categoria categoria : categorias) {
            if (categoria.getId().equals(id)) {
                return categoria;
            }
        }
        return null;
    }

    private static usuario buscarUsuarioPorId(List<usuario> usuarios, String id) {
        for (usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    private static recurso buscarRecursoPorId(List<recurso> recursos, String id) {
        for (recurso recurso : recursos) {
            if (recurso.getId().equals(id)) {
                return recurso;
            }
        }
        return null;
    }
}