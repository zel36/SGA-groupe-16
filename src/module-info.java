/**
 * Module SGA — Système de Gestion d'Accès
 */
module SGA {
    // Modules requis
    requires java.sql;      // accès JDBC
    requires java.desktop;  // Swing

    // Exports des packages de l'application
    exports sga.model;
    exports sga.dao;
    exports sga.controller;
    exports sga.view;
    exports sga.util;
}
