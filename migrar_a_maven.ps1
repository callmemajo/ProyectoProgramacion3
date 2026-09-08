$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force -Path "src/main/java/reservas/api" | Out-Null
New-Item -ItemType Directory -Force -Path "src/main/java/reservas/controller" | Out-Null
New-Item -ItemType Directory -Force -Path "src/main/java/reservas/modelo" | Out-Null
New-Item -ItemType Directory -Force -Path "src/main/java/reservas/vista" | Out-Null
New-Item -ItemType Directory -Force -Path "src/test/java/reservas" | Out-Null

git mv "src/reservas/almacenDatos.java" "src/main/java/reservas/almacenDatos.java"
git mv "src/reservas/api/analizadorJson.java" "src/main/java/reservas/api/analizadorJson.java"
git mv "src/reservas/api/clienteGemini.java" "src/main/java/reservas/api/clienteGemini.java"
git mv "src/reservas/controller/controladorActividades.java" "src/main/java/reservas/controller/controladorActividades.java"
git mv "src/reservas/controller/controladorCalendarizacion.java" "src/main/java/reservas/controller/controladorCalendarizacion.java"
git mv "src/reservas/controller/controladorCambiarClave.java" "src/main/java/reservas/controller/controladorCambiarClave.java"
git mv "src/reservas/controller/controladorCategorias.java" "src/main/java/reservas/controller/controladorCategorias.java"
git mv "src/reservas/controller/controladorEstadisticas.java" "src/main/java/reservas/controller/controladorEstadisticas.java"
git mv "src/reservas/controller/controladorFuncionarios.java" "src/main/java/reservas/controller/controladorFuncionarios.java"
git mv "src/reservas/controller/controladorLogin.java" "src/main/java/reservas/controller/controladorLogin.java"
git mv "src/reservas/controller/controladorRecursos.java" "src/main/java/reservas/controller/controladorRecursos.java"
git mv "src/reservas/controller/controladorReservas.java" "src/main/java/reservas/controller/controladorReservas.java"
git mv "src/reservas/main.java" "src/main/java/reservas/main.java"
git mv "src/reservas/modelo/administrador.java" "src/main/java/reservas/modelo/administrador.java"
git mv "src/reservas/modelo/categoria.java" "src/main/java/reservas/modelo/categoria.java"
git mv "src/reservas/modelo/estadoReserva.java" "src/main/java/reservas/modelo/estadoReserva.java"
git mv "src/reservas/modelo/funcionario.java" "src/main/java/reservas/modelo/funcionario.java"
git mv "src/reservas/modelo/recurso.java" "src/main/java/reservas/modelo/recurso.java"
git mv "src/reservas/modelo/reserva.java" "src/main/java/reservas/modelo/reserva.java"
git mv "src/reservas/modelo/rolUsuario.java" "src/main/java/reservas/modelo/rolUsuario.java"
git mv "src/reservas/modelo/usuario.java" "src/main/java/reservas/modelo/usuario.java"
git mv "src/reservas/persistenciaXml.java" "src/main/java/reservas/persistenciaXml.java"
git mv "src/reservas/pruebaClienteGeminiManual.java" "src/main/java/reservas/pruebaClienteGeminiManual.java"
git mv "src/reservas/vista/actividadesPanel.java" "src/main/java/reservas/vista/actividadesPanel.java"
git mv "src/reservas/vista/avatarCirculo.java" "src/main/java/reservas/vista/avatarCirculo.java"
git mv "src/reservas/vista/botonRedondeado.java" "src/main/java/reservas/vista/botonRedondeado.java"
git mv "src/reservas/vista/calendarizacionRecursosPanel.java" "src/main/java/reservas/vista/calendarizacionRecursosPanel.java"
git mv "src/reservas/vista/cambiarClaveDialog.java" "src/main/java/reservas/vista/cambiarClaveDialog.java"
git mv "src/reservas/vista/categoriasPanel.java" "src/main/java/reservas/vista/categoriasPanel.java"
git mv "src/reservas/vista/estadisticasPanel.java" "src/main/java/reservas/vista/estadisticasPanel.java"
git mv "src/reservas/vista/estilos.java" "src/main/java/reservas/vista/estilos.java"
git mv "src/reservas/vista/funcionariosPanel.java" "src/main/java/reservas/vista/funcionariosPanel.java"
git mv "src/reservas/vista/generadorPdf.java" "src/main/java/reservas/vista/generadorPdf.java"
git mv "src/reservas/vista/graficoBarras.java" "src/main/java/reservas/vista/graficoBarras.java"
git mv "src/reservas/vista/loginFrame.java" "src/main/java/reservas/vista/loginFrame.java"
git mv "src/reservas/vista/panelGradiente.java" "src/main/java/reservas/vista/panelGradiente.java"
git mv "src/reservas/vista/panelTarjeta.java" "src/main/java/reservas/vista/panelTarjeta.java"
git mv "src/reservas/vista/recursosPanel.java" "src/main/java/reservas/vista/recursosPanel.java"
git mv "src/reservas/vista/rendererCeldaMatriz.java" "src/main/java/reservas/vista/rendererCeldaMatriz.java"
git mv "src/reservas/vista/reservasPanel.java" "src/main/java/reservas/vista/reservasPanel.java"
git mv "src/reservas/vista/ventanaPrincipalFrame.java" "src/main/java/reservas/vista/ventanaPrincipalFrame.java"

git rm -f "src/reservas/pruebaLogica.java"

Remove-Item -Force -ErrorAction SilentlyContinue -Recurse "src/reservas"

if (Test-Path "out") {
    git rm -r --cached "out" 2>$null | Out-Null
    Remove-Item -Force -Recurse "out"
}

Write-Host "Listo. La carpeta src/reservas vieja quedo movida a src/main/java/reservas."
Write-Host "Ahora copia estos archivos que te mando por separado a la raiz del proyecto:"
Write-Host "  pom.xml"
Write-Host "  .gitignore (reemplaza el que ya tenias)"
Write-Host "Y estos 7 archivos nuevos a src/test/java/reservas/ (crea esa carpeta si no existe):"
Write-Host "  pruebaLoginYClaveTest.java, pruebaReservasTest.java, pruebaFuncionariosTest.java,"
Write-Host "  pruebaCategoriasTest.java, pruebaRecursosTest.java, pruebaReportePdfIT.java,"
Write-Host "  pruebaPersistenciaXmlIT.java"
Write-Host ""
Write-Host "Despues, en IntelliJ: click derecho sobre pom.xml -> Add as Maven Project"
Write-Host "(o icono de Maven que aparece arriba a la derecha -> Load Maven Project)."
