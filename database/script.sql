-- Creación de la Base de Datos.
CREATE DATABASE papeleria_campus;

-- Creación de esquema general para base de datos.
CREATE SCHEMA catalogos;

-- Creación de la tabla Productos.
CREATE TABLE catalogos.productos
(
    id_producto SERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL,

    -- Restricción de codigo.
    -- codigo <> '' impide el vacío y UPPER(BTRIM(codigo)) obliga a que el código
    -- venga sin espacios laterales y en mayúsculas.
    CONSTRAINT chk_productos_codigo
        CHECK (codigo <> '' AND codigo = UPPER(BTRIM(codigo))),

    -- Restricción de nombre.
    -- Cuenta los caracteres luego de eliminar los espacios.
    CONSTRAINT chk_productos_nombre
        CHECK (CHAR_LENGTH(BTRIM(nombre)) BETWEEN 3 AND 80),

    -- Restricción de categoria a Papelería, Tecnología o Accesorios.
    CONSTRAINT chk_productos_categoria
        CHECK (categoria IN ('Papelería', 'Tecnología', 'Accesorios')),

    -- Restricción de precio. No puede ser menor a 0.
    CONSTRAINT chk_productos_precio
        CHECK (precio > 0),

    -- Restricción de stock. Debe ser mayor o igual a 0.
    CONSTRAINT chk_productos_stock
        CHECK (stock >= 0)
);

-- Inserción de datos de prueba en la tabla productos.
INSERT INTO catalogos.productos (codigo, nombre, categoria, precio, stock) VALUES
                                                                               ('P001', 'Cuaderno universitario', 'Papelería', 65.00, 25),
                                                                               ('P002', 'Cuaderno cuadriculado', 'Papelería', 72.50, 12),
                                                                               ('P003', 'Lapicero azul', 'Papelería', 12.00, 80),
                                                                               ('P004', 'Resma de papel carta', 'Papelería', 185.00, 0),
                                                                               ('T001', 'Memoria USB 32 GB', 'Tecnología', 210.00, 8),
                                                                               ('T002', 'Mouse inalámbrico', 'Tecnología', 320.00, 6),
                                                                               ('T003', 'Teclado USB', 'Tecnología', 450.00, 4),
                                                                               ('T004', 'Cable HDMI', 'Tecnología', 180.00, 0),
                                                                               ('A001', 'Mochila escolar', 'Accesorios', 850.00, 5),
                                                                               ('A002', 'Estuche de lápices', 'Accesorios', 95.00, 18),
                                                                               ('A003', 'Botella reutilizable', 'Accesorios', 240.00, 0),
                                                                               ('A004', 'Regla metálica', 'Accesorios', 45.00, 20);

-- Query para comprobar la inserción de datos.
SELECT *
FROM catalogos.productos;

-- Ver productos con stock = 0.
SELECT *
FROM catalogos.productos
WHERE stock = 0;