/**
 * 
 */
/**
 * 
 */
module SGA {
	// Modules requis
	requires java.sql;      // accès JDBC
	requires java.desktop;  // Swing (WindowBuilder)

	// Exports des packages de l'application
	exports sga.model;
	exports sga.dao;
	// controller et view seront exportés une fois implémentés
	exports sga.util;
}