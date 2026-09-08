package reservas;

import reservas.vista.generadorPdf;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class pruebaReportePdfIT {

    @Test
    void generarReporteConDatosProduceUnPdfValido() throws Exception {
        String[] encabezados = {"Id", "Descripcion"};
        List<String[]> filas = new ArrayList<>();
        filas.add(new String[]{"CAT-001", "Categoria de prueba"});
        filas.add(new String[]{"CAT-002", "Otra categoria de prueba"});

        Path archivoTemporal = Files.createTempFile("reporte_prueba", ".pdf");
        try {
            generadorPdf.generarReporte(archivoTemporal, "Reporte de prueba", encabezados, filas);

            byte[] contenido = Files.readAllBytes(archivoTemporal);
            String encabezadoPdf = new String(contenido, 0, Math.min(5, contenido.length));
            assertEquals("%PDF-", encabezadoPdf, "generarReporte produce un archivo con encabezado %PDF-");
            assertTrue(contenido.length > 100, "generarReporte produce un archivo con contenido");
        } finally {
            Files.deleteIfExists(archivoTemporal);
        }
    }

    @Test
    void generarReporteConTablaVaciaProduceUnPdfValido() throws Exception {
        String[] encabezados = {"Id", "Descripcion"};
        Path archivoTemporal = Files.createTempFile("reporte_prueba_vacio", ".pdf");
        try {
            generadorPdf.generarReporte(archivoTemporal, "Reporte sin registros", encabezados, new ArrayList<>());

            byte[] contenido = Files.readAllBytes(archivoTemporal);
            assertTrue(contenido.length > 50, "generarReporte con tabla vacia igual produce un PDF valido");
        } finally {
            Files.deleteIfExists(archivoTemporal);
        }
    }
}