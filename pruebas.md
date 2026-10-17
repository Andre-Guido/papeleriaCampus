# Evidencias de pruebas

## Búsqueda y filtros: pruebas B1 a B7

### B1
- **Prueba:** Texto: CUADERNO; sin otros filtros.
- **Resultado:** P001 y P002; contador 2.

![Resultado](evidencias/B1.png)

### B2
- **Prueba:** Texto: T00; sin otros filtros.
- **Resultado:** T001 a T004; contador 4.

![Resultado](evidencias/B2.png)

### B3
- **Prueba:** Categoría: Papelería; texto vacío.
- **Resultado:** P001 a P004; contador 4.

![Resultado](evidencias/B3.png)

### B4
- **Prueba:** Solo con existencias; categoría Todas.
- **Resultado:** 9 productos; excluye P004, T004 y A003.

![Resultado](evidencias/B4.png)

### B5
- **Prueba:** Texto: Texto USB + Tecnología + Solo con existencias.
- **Resultado:** T001 y T003; contador 2.

![Resultado](evidencias/B5.png)

### B6
- **Prueba:** Texto: ZZZ999; sin otros filtros.
- **Resultado:** 0 filas, contador 0 y aviso Sin coincidencias.

![Resultado](evidencias/B6.png)

### B7
- **Prueba:** Pulsar Limpiar filtros después de B5.
- **Resultado:** Texto vacío, Todas, casilla desmarcada y 12 filas.

![Resultado](evidencias/B7.png)


## Validaciones y pruebas de error
### V1
- **Prueba:** Código vacío, solo espacios o 11 caracteres.
- **Resultado:** Rechaza; exige de 1 a 10 caracteres tras trim.

![Resultado](evidencias/V1.png)

### V2
- **Prueba:** Nombre solo con espacios, AB o 81 caracteres.
- **Resultado:** Rechaza; exige de 3 a 80 caracteres tras trim.

![Resultado](evidencias/V2.png)

### V3
- **Prueba:** Categoría sin seleccionar; valor ajeno al catálogo si el
  control permite escribir.
- **Resultado:** Rechaza o impide introducir una categoría no permitida.

![Resultado](evidencias/V3.png)

### V4
- **Prueba:** Precio: abc.
- **Resultado:** Rechaza sin cerrar la aplicación.

![Resultado](evidencias/V4.png)

### V5
- **Prueba:** Precio: 0 y -1.
- **Resultado:** Rechaza ambos valores.

![Resultado](evidencias/V5.1.png)
![Resultado](evidencias/V5.2.png)

### V6
- **Prueba:** Precio: 12.345 y 100000000.00.
- **Resultado:** Rechaza exceso de decimales y de rango; no redondea
  silenciosamente.

![Resultado](evidencias/V6.1.png)
![Resultado](evidencias/V6.2.png)

### V7
- **Prueba:** Stock: abc y 2.5.
- **Resultado:** Rechaza texto y cantidades no enteras.

![Resultado](evidencias/V7.1.png)
![Resultado](evidencias/V7.2.png)

### V8
- **Prueba:** Stock: -1 y 2147483648; luego stock 0.
- **Resultado:** Rechaza los dos primeros y admite 0.

![Resultado](evidencias/V8.1.png)
![Resultado](evidencias/V8.2.png)
![Resultado](evidencias/V8.3.png)

### V9
- **Prueba:** Crear otro producto con código " p001 "; actualizar otro
  producto a P001.
- **Resultado:** Rechaza duplicados tras normalizar. Editar P001
  conservando su propio código sí debe permitirse.

![Resultado](evidencias/V9.1.png)
![Resultado](evidencias/V9.2.1.png)
![Resultado](evidencias/V9.2.2.png)

### V10
- **Prueba:** Actualizar o eliminar sin una fila seleccionada.
- **Resultado:** Bloquea la acción e informa que debe seleccionar un
  producto.

![Resultado](evidencias/V10.1.png)
![Resultado](evidencias/V10.2.png)

## Excepciones y operaciones fallidas: E1 a E4
### E1
**Conexión:** Pruebe un puerto incorrecto en una configuración de prueba. Debe aparecer un aviso comprensible, la
aplicación no debe cerrarse inesperadamente y el fallo no debe presentarse como una consulta exitosa con cero
resultados.

![Resultado](evidencias/E1.1.png)
![Resultado](evidencias/E1.2.png)

### E2
**Restricción SQL:** Provoque una duplicidad o un CHECK fallido mediante una llamada de prueba al DAO, sin validación
de interfaz. Debe gestionarse SQLException y conservarse la integridad. No desactive restricciones ni agregue botones de
prueba a la interfaz final.

![Resultado](evidencias/E2.png)

### E3
**Cero filas afectadas:** Seleccione un producto temporal, elimínelo directamente en la base de prueba e intente
actualizarlo desde la aplicación. No debe anunciar éxito; debe advertir que ya no existe y permitir recargar. Aplique el
mismo control a DELETE.

![Resultado](evidencias/E3.1.png)
![Resultado](evidencias/E3.2.png)
![Resultado](evidencias/E3.3.png)
![Resultado](evidencias/E3.4.png)

### E4
**Diagnóstico:** Registre la operación y el SQLState en consola o archivo. Muestre al usuario un mensaje sin contraseñas
ni trazas técnicas. Una excepción no debe producir un aviso falso de guardado exitoso.

![Resultado](evidencias/E4.png)