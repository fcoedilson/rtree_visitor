package br.edu.fa7.util;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class Database {

	public static void main(String[] args) {
		Configuration cfg = new AnnotationConfiguration();
		cfg.configure();

		SchemaExport se = new SchemaExport(cfg);
		se.create(true, true);
	}
}
